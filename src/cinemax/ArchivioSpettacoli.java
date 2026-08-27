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
 * Ogni riga (a parte l'intestazione) e' nel formato data_ora_proiezione,
 * titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto.
 * La prima riga viene semplicemente tentata come dato: se il tentativo
 * fallisce (perche' contiene testo invece di numeri/date, cioe' e' proprio
 * l'intestazione) viene scartata in silenzio, cosi' il caricamento funziona
 * sia sul file consegnato (con intestazione) sia su un eventuale file senza.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class ArchivioSpettacoli {
    private String percorsoFile;
    private Spettacolo[] elenco;
    private int quantita;

    public ArchivioSpettacoli(String percorsoFile) {
        this.percorsoFile = percorsoFile;
        this.elenco = new Spettacolo[10];
        this.quantita = 0;
        caricaDaFile();
    }

    /**
     * Legge il file CSV e ricostruisce l'elenco in memoria. Le righe malformate
     * vengono segnalate e
     * ignorate.
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
     * Riscrive interamente il file a partire dall'elenco in memoria, con la riga di
     * intestazione in testa.
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

    public Spettacolo[] elencoTutti() {
        Spettacolo[] copia = new Spettacolo[quantita];
        for (int i = 0; i < quantita; i++) {
            copia[i] = elenco[i];
        }
        return copia;
    }

    /** Cerca una proiezione tramite la sua chiave composta (titolo + data/ora). */
    public Spettacolo trovaPerChiave(String titolo, LocalDateTime dataOra) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].corrispondeA(titolo, dataOra)) {
                return elenco[i];
            }
        }
        return null;
    }

    public void aggiungi(Spettacolo spettacolo) {
        aggiungiInMemoria(spettacolo);
        salvaSuFile();
    }

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