package cinemax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Gestisce la lettura, la scrittura e l'accesso in memoria alle proiezioni,
 * mantenute in un array che viene ridimensionato quando necessario.
 * <p>
 * Ogni riga (a parte l'intestazione) e' nel formato
 * <code>data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto</code>.
 * La prima riga viene semplicemente tentata come dato: se il tentativo
 * fallisce (perche' contiene testo invece di numeri/date, cioe' e' proprio
 * l'intestazione) viene scartata in silenzio, cosi' il caricamento funziona
 * sia sul file consegnato (con intestazione) sia su un eventuale file senza.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class ArchivioSpettacoli {
    /** Percorso del file CSV su cui l'archivio legge e scrive. */
    private String percorsoFile;
    /** Array in memoria delle proiezioni caricate, ridimensionato dinamicamente. */
    private Spettacolo[] elenco;
    /** Numero di proiezioni effettivamente occupate nell'array {@link #elenco}. */
    private int quantita;

    /**
     * Costruttore che imposta il percorso del file e carica subito le
     * proiezioni esistenti tramite {@link #caricaDaFile()}.
     *
     * @param percorsoFile il percorso del file CSV da usare per la persistenza
     */
    public ArchivioSpettacoli(String percorsoFile) {
        this.percorsoFile = percorsoFile;
        this.elenco = new Spettacolo[10];
        this.quantita = 0;
        caricaDaFile();
    }

    /**
     * Legge il file CSV e ricostruisce l'elenco delle proiezioni in memoria.
     * Le righe malformate vengono segnalate e ignorate.
     */
    public void caricaDaFile() {
        elenco = new Spettacolo[10];
        quantita = 0;
        File file = new File(percorsoFile);
        if (!file.exists()) {
            System.out.println("File proiezioni non trovato, verra' creato al primo salvataggio: " +
                    percorsoFile);
            return;
        }
        try (BufferedReader lettore = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String riga;
            boolean primaRiga = true;
            while ((riga = lettore.readLine()) != null) {
                if (riga.trim().isEmpty()) {
                    continue;
                }
                try {
                    Spettacolo spettacolo = leggiRiga(riga);
                    aggiungiInMemoria(spettacolo);
                } catch (Exception rigaNonValida) {
                    if (primaRiga) {
                        // probabile riga di intestazione (es. file fornito dal docente): si ignora
                        // senza avviso

                        primaRiga = false;
                        continue;
                    }
                    System.out.println("Riga proiezioni ignorata (formato non valido): " + riga);
                }
                primaRiga = false;
            }
        } catch (IOException erroreLettura) {
            System.out.println("Errore durante la lettura di " + percorsoFile + ": " +
                    erroreLettura.getMessage());
        }
    }

    /**
     * Interpreta una singola riga CSV come una proiezione.
     * <p>
     * Delega la divisione dei campi a {@link CsvUtile#dividiRiga(String)} e
     * costruisce un {@link Spettacolo} completo di {@link Pellicola}
     * associata.
     *
     * @param riga la riga CSV da interpretare
     * @return la proiezione corrispondente alla riga
     */
    private Spettacolo leggiRiga(String riga) {
        String[] campi = CsvUtile.dividiRiga(riga);
        LocalDateTime dataOra = LocalDateTime.parse(campi[0].trim(), Spettacolo.FORMATO_LETTURA);
        Pellicola film = new Pellicola(campi[1], campi[2], campi[3],
                Integer.parseInt(campi[4].trim()), Integer.parseInt(campi[5].trim()),
                Integer.parseInt(campi[6].trim()));
        double prezzo = Double.parseDouble(campi[7].trim());
        return new Spettacolo(film, dataOra, prezzo);
    }

    /**
     * Riscrive interamente il file a partire dall'elenco in memoria, con la
     * riga di intestazione in testa.
     */
    public void salvaSuFile() {
        try (BufferedWriter scrittore = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(percorsoFile), StandardCharsets.UTF_8))) {

            scrittore.write(
                    "data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto");
            scrittore.newLine();
            for (int i = 0; i < quantita; i++) {
                Spettacolo spettacolo = elenco[i];
                Pellicola film = spettacolo.getFilm();
                String riga = CsvUtile.componiRiga(
                        spettacolo.getDataOra().format(Spettacolo.FORMATO_SCRITTURA),
                        film.getTitolo(), film.getGenere(), film.getRegista(),
                        String.valueOf(film.getAnno()), String.valueOf(film.getDurataMinuti()),
                        String.valueOf(film.getEtaMinima()),
                        String.valueOf(spettacolo.getPrezzoBiglietto()));
                scrittore.write(riga);
                scrittore.newLine();
            }
        } catch (IOException erroreScrittura) {
            System.out
                    .println("Errore durante il salvataggio di " + percorsoFile + ": " + erroreScrittura.getMessage());
        }
    }

    /**
     * Aggiunge una proiezione all'array {@link #elenco} in memoria,
     * raddoppiando la capacita' dell'array se necessario.
     * <p>
     * Non salva su file: e' un'operazione di solo supporto usata da
     * {@link #caricaDaFile()} e da {@link #aggiungi(Spettacolo)}.
     *
     * @param spettacolo la proiezione da aggiungere in memoria
     */
    private void aggiungiInMemoria(Spettacolo spettacolo) {
        if (quantita == elenco.length) {
            Spettacolo[] nuovoArray = new Spettacolo[elenco.length * 2];
            for (int i = 0; i < elenco.length; i++) {
                nuovoArray[i] = elenco[i];
            }
            elenco = nuovoArray;
        }
        elenco[quantita] = spettacolo;
        quantita++;
    }

    /**
     * Restituisce una copia di tutte le proiezioni in memoria.
     * <p>
     * Viene restituita una copia per evitare che codice esterno modifichi
     * l'array interno dell'archivio.
     *
     * @return un array con tutte le proiezioni caricate
     */
    public Spettacolo[] elencoTutti() {
        Spettacolo[] copia = new Spettacolo[quantita];
        for (int i = 0; i < quantita; i++) {
            copia[i] = elenco[i];
        }
        return copia;
    }

    /**
     * Cerca una proiezione tramite la sua chiave composta (titolo + data/ora).
     *
     * @param titolo  il titolo del film da cercare (confronto case-insensitive)
     * @param dataOra la data/ora della proiezione da cercare
     * @return la proiezione trovata, oppure null se nessuna corrisponde
     */
    public Spettacolo trovaPerChiave(String titolo, LocalDateTime dataOra) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].corrispondeA(titolo, dataOra)) {
                return elenco[i];
            }
        }
        return null;
    }

    /**
     * Aggiunge una nuova proiezione sia in memoria sia su file.
     * <p>
     * Equivale a chiamare {@link #aggiungiInMemoria(Spettacolo)} seguito da
     * {@link #salvaSuFile()}.
     *
     * @param spettacolo la proiezione da aggiungere
     */
    public void aggiungi(Spettacolo spettacolo) {
        aggiungiInMemoria(spettacolo);
        salvaSuFile();
    }

    /**
     * Rimuove una proiezione dall'elenco in memoria e salva su file.
     * <p>
     * Trasla di una posizione tutti gli elementi successivi a quello
     * rimosso, in modo che l'invariante "gli elementi occupati sono le prime
     * {@link #quantita} posizioni" resti sempre valida.
     *
     * @param titolo  il titolo del film della proiezione da rimuovere
     * @param dataOra la data/ora della proiezione da rimuovere
     * @return true se la proiezione e' stata trovata e rimossa, false altrimenti
     */
    public boolean rimuovi(String titolo, LocalDateTime dataOra) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].corrispondeA(titolo, dataOra)) {
                for (int j = i; j < quantita - 1; j++) {
                    elenco[j] = elenco[j + 1];
                }
                elenco[quantita - 1] = null;
                quantita--;
                salvaSuFile();
                return true;
            }
        }
        return false;
    }
}
