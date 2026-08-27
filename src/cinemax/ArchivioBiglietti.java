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
 * Il file data/prenotazioni.csv parte con la sola riga di intestazione
 * (nessuna prenotazione ancora effettuata): viene popolato dall'applicazione
 * durante l'uso. Formato di ogni riga (dopo l'intestazione): codice,
 * username_cliente,titolo_film,data_ora_spettacolo,numero_posti,
 * costo_unitario. La proiezione e' referenziata con titolo+data/ora (chiave
 * composta) invece che con un id numerico.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class ArchivioBiglietti {
    private String percorsoFile;
    private Biglietto[] elenco;
    private int quantita;

    public ArchivioBiglietti(String percorsoFile) {
        this.percorsoFile = percorsoFile;
        this.elenco = new Biglietto[10];
        this.quantita = 0;
        caricaDaFile();
    }

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
     * Riscrive interamente il file a partire dall'elenco in memoria, con la riga di
     * intestazione in testa.
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

    public Biglietto[] elencoTutti() {
        Biglietto[] copia = new Biglietto[quantita];
        for (int i = 0; i < quantita; i++) {
            copia[i] = elenco[i];
        }
        return copia;
    }

    public Biglietto trovaPerCodice(String codice) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].getCodice().equalsIgnoreCase(codice)) {
                return elenco[i];
            }
        }
        return null;
    }

    public void aggiungi(Biglietto biglietto) {
        aggiungiInMemoria(biglietto);
        salvaSuFile();
    }

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
