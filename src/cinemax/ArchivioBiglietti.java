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
 * Gestisce la lettura, la scrittura e l'accesso in memoria alle prenotazioni.
 * <p>
 * Il file <code>data/prenotazioni.csv</code> parte con la sola riga di
 * intestazione (nessuna prenotazione ancora effettuata): viene popolato
 * dall'applicazione durante l'uso. Formato di ogni riga (dopo l'intestazione):
 * <code>codice,username_cliente,titolo_film,data_ora_spettacolo,numero_posti,costo_unitario</code>.
 * La proiezione e' referenziata con titolo+data/ora (chiave composta, vedi
 * {@link Spettacolo#corrispondeA(String, LocalDateTime)}) invece che con un
 * id numerico.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class ArchivioBiglietti {
    /** Percorso del file CSV su cui l'archivio legge e scrive. */
    private String percorsoFile;
    /** Array in memoria dei biglietti caricati, ridimensionato dinamicamente. */
    private Biglietto[] elenco;
    /** Numero di biglietti effettivamente occupati nell'array {@link #elenco}. */
    private int quantita;

    /**
     * Costruttore che imposta il percorso del file e carica subito le
     * prenotazioni esistenti tramite {@link #caricaDaFile()}.
     *
     * @param percorsoFile il percorso del file CSV da usare per la persistenza
     */
    public ArchivioBiglietti(String percorsoFile) {
        this.percorsoFile = percorsoFile;
        this.elenco = new Biglietto[10];
        this.quantita = 0;
        caricaDaFile();
    }

    /**
     * Legge il file CSV e ricostruisce l'elenco delle prenotazioni in memoria.
     * <p>
     * La riga di intestazione viene riconosciuta tentando di interpretarla
     * come dato: se il parsing fallisce viene scartata senza avviso; le righe
     * successive malformate vengono invece segnalate a schermo e ignorate.
     */
    public void caricaDaFile() {
        elenco = new Biglietto[10];
        quantita = 0;
        File file = new File(percorsoFile);
        if (!file.exists()) {
            System.out.println("File prenotazioni non trovato, verra' creato al primo salvataggio: " +
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
                    String[] campi = CsvUtile.dividiRiga(riga);
                    LocalDateTime dataOraSpettacolo = LocalDateTime.parse(campi[3].trim(),
                            Spettacolo.FORMATO_LETTURA);
                    Biglietto biglietto = new Biglietto(campi[0], campi[1], campi[2], dataOraSpettacolo,
                            Integer.parseInt(campi[4].trim()), Double.parseDouble(campi[5].trim()));
                    aggiungiInMemoria(biglietto);
                } catch (Exception rigaNonValida) {
                    if (primaRiga) {
                        // probabile riga di intestazione: si ignora senza avviso
                        primaRiga = false;
                        continue;
                    }
                    System.out.println("Riga prenotazioni ignorata (formato non valido): " + riga);
                }
                primaRiga = false;
            }
        } catch (IOException erroreLettura) {
            System.out.println("Errore durante la lettura di " + percorsoFile + ": " +
                    erroreLettura.getMessage());
        }
    }

    /**
     * Riscrive interamente il file a partire dall'elenco in memoria, con la
     * riga di intestazione in testa.
     */
    public void salvaSuFile() {
        try (BufferedWriter scrittore = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(percorsoFile), StandardCharsets.UTF_8))) {

            scrittore.write("codice,username_cliente,titolo_film,data_ora_spettacolo,numero_posti,costo_unitario");
            scrittore.newLine();
            for (int i = 0; i < quantita; i++) {
                Biglietto biglietto = elenco[i];
                String riga = CsvUtile.componiRiga(biglietto.getCodice(), biglietto.getUsernameCliente(),
                        biglietto.getTitoloFilm(),
                        biglietto.getDataOraSpettacolo().format(Spettacolo.FORMATO_SCRITTURA),
                        String.valueOf(biglietto.getNumeroPosti()),
                        String.valueOf(biglietto.getCostoUnitario()));
                scrittore.write(riga);
                scrittore.newLine();
            }
        } catch (IOException erroreScrittura) {
            System.out.println("Errore durante il salvataggio di " + percorsoFile + ": " +
                    erroreScrittura.getMessage());
        }
    }

    /**
     * Aggiunge un biglietto all'array {@link #elenco} in memoria, raddoppiando
     * la capacita' dell'array se necessario.
     * <p>
     * Non salva su file: e' un'operazione di solo supporto usata da
     * {@link #caricaDaFile()} e da {@link #aggiungi(Biglietto)}.
     *
     * @param biglietto il biglietto da aggiungere in memoria
     */
    private void aggiungiInMemoria(Biglietto biglietto) {
        if (quantita == elenco.length) {
            Biglietto[] nuovoArray = new Biglietto[elenco.length * 2];
            for (int i = 0; i < elenco.length; i++) {
                nuovoArray[i] = elenco[i];
            }
            elenco = nuovoArray;
        }
        elenco[quantita] = biglietto;
        quantita++;
    }

    /**
     * Restituisce una copia di tutte le prenotazioni in memoria.
     * <p>
     * Viene restituita una copia per evitare che codice esterno modifichi
     * l'array interno dell'archivio.
     *
     * @return un array con tutte le prenotazioni caricate
     */
    public Biglietto[] elencoTutti() {
        Biglietto[] copia = new Biglietto[quantita];
        for (int i = 0; i < quantita; i++) {
            copia[i] = elenco[i];
        }
        return copia;
    }

    /**
     * Cerca una prenotazione per codice, senza distinzione tra maiuscole e
     * minuscole.
     *
     * @param codice il codice della prenotazione da cercare
     * @return la prenotazione trovata, oppure null se nessuna corrisponde
     */
    public Biglietto trovaPerCodice(String codice) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].getCodice().equalsIgnoreCase(codice)) {
                return elenco[i];
            }
        }
        return null;
    }

    /**
     * Aggiunge una nuova prenotazione sia in memoria sia su file.
     * <p>
     * Equivale a chiamare {@link #aggiungiInMemoria(Biglietto)} seguito da
     * {@link #salvaSuFile()}.
     *
     * @param biglietto la prenotazione da aggiungere
     */
    public void aggiungi(Biglietto biglietto) {
        aggiungiInMemoria(biglietto);
        salvaSuFile();
    }

    /**
     * Rimuove una prenotazione dall'elenco in memoria e salva su file.
     * <p>
     * Trasla di una posizione tutti gli elementi successivi a quello
     * rimosso, in modo che l'invariante "gli elementi occupati sono le prime
     * {@link #quantita} posizioni" resti sempre valida.
     *
     * @param codice il codice della prenotazione da rimuovere
     * @return true se la prenotazione e' stata trovata e rimossa, false altrimenti
     */
    public boolean rimuovi(String codice) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].getCodice().equalsIgnoreCase(codice)) {
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
