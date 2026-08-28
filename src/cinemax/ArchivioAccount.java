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
import java.time.LocalDate;

/**
 * Gestisce la lettura, la scrittura e l'accesso in memoria agli account.
 * <p>
 * Il file <code>data/utenti.csv</code> viene consegnato gia' popolato (con le
 * password gia' cifrate), con una riga di intestazione in testa e poi una
 * riga per account, nel formato
 * <code>nome,cognome,username,password_cifrata,data_nascita,domicilio,ruolo</code>.
 * Deve gia' contenere 2 account {@link Account#OPERATORE} e 5 account
 * {@link Account#CASSIERE}, come richiesto dalle specifiche.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class ArchivioAccount {
    /** Percorso del file CSV su cui l'archivio legge e scrive. */
    private String percorsoFile;
    /** Array in memoria degli account caricati, ridimensionato dinamicamente. */
    private Account[] elenco;
    /** Numero di account effettivamente occupati nell'array {@link #elenco}. */
    private int quantita;

    /**
     * Costruttore che imposta il percorso del file e carica subito gli account
     * esistenti tramite {@link #caricaDaFile()}.
     *
     * @param percorsoFile il percorso del file CSV da usare per la persistenza
     */
    public ArchivioAccount(String percorsoFile) {
        this.percorsoFile = percorsoFile;
        this.elenco = new Account[10];
        this.quantita = 0;
        caricaDaFile();
    }

    /**
     * Legge il file CSV e ricostruisce l'elenco degli account in memoria.
     * <p>
     * La riga di intestazione viene riconosciuta tentando di interpretarla
     * come dato: se il parsing fallisce (perche' contiene testo invece di una
     * data valida) viene scartata senza avviso; le righe successive malformate
     * vengono invece segnalate a schermo e ignorate.
     */
    public void caricaDaFile() {
        elenco = new Account[10];
        quantita = 0;
        File file = new File(percorsoFile);
        if (!file.exists()) {
            System.out.println("File utenti non trovato: " + percorsoFile);
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
                    String testoDataNascita = campi[4].trim();
                    LocalDate dataNascita;
                    if (testoDataNascita.isEmpty()) {
                        dataNascita = null;
                    } else {
                        dataNascita = LocalDate.parse(testoDataNascita);
                    }
                    Account account = new Account(campi[0], campi[1], campi[2], campi[3], dataNascita,
                            campi[5], campi[6]);
                    aggiungiInMemoria(account);
                } catch (Exception rigaNonValida) {
                    if (primaRiga) {
                        // riga di intestazione (la data_nascita letterale non e' una data valida): si
                        // ignora senza avviso
                        primaRiga = false;
                        continue;
                    }
                    System.out.println("Riga utenti ignorata (formato non valido): " + riga);
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
            scrittore.write("nome,cognome,username,password_cifrata,data_nascita,domicilio,ruolo");
            scrittore.newLine();
            for (int i = 0; i < quantita; i++) {
                Account account = elenco[i];
                String testoDataNascita;
                if (account.getDataNascita() == null) {
                    testoDataNascita = "";
                } else {
                    testoDataNascita = account.getDataNascita().toString();
                }
                String riga = CsvUtile.componiRiga(account.getNome(), account.getCognome(),
                        account.getUsername(),
                        account.getPasswordCifrata(), testoDataNascita, account.getDomicilio(),
                        account.getRuolo());
                scrittore.write(riga);
                scrittore.newLine();
            }
        } catch (IOException erroreScrittura) {
            System.out.println("Errore durante il salvataggio di " + percorsoFile + ": " +
                    erroreScrittura.getMessage());
        }
    }

    /**
     * Aggiunge un account all'array {@link #elenco} in memoria, raddoppiando
     * la capacita' dell'array se necessario.
     * <p>
     * Non salva su file: e' un'operazione di solo supporto usata da
     * {@link #caricaDaFile()} e da {@link #aggiungi(Account)}.
     *
     * @param account l'account da aggiungere in memoria
     */
    private void aggiungiInMemoria(Account account) {
        if (quantita == elenco.length) {
            Account[] nuovoArray = new Account[elenco.length * 2];
            for (int i = 0; i < elenco.length; i++) {
                nuovoArray[i] = elenco[i];
            }
            elenco = nuovoArray;
        }
        elenco[quantita] = account;
        quantita++;
    }

    /**
     * Restituisce una copia di tutti gli account in memoria.
     * <p>
     * Viene restituita una copia per evitare che codice esterno modifichi
     * l'array interno dell'archivio.
     *
     * @return un array con tutti gli account caricati
     */
    public Account[] elencoTutti() {
        Account[] copia = new Account[quantita];
        for (int i = 0; i < quantita; i++) {
            copia[i] = elenco[i];
        }
        return copia;
    }

    /**
     * Cerca un account per username, senza distinzione tra maiuscole e
     * minuscole.
     *
     * @param username lo username da cercare
     * @return l'account trovato, oppure null se nessun account corrisponde
     */
    public Account trovaPerUsername(String username) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].getUsername().equalsIgnoreCase(username)) {
                return elenco[i];
            }
        }
        return null;
    }

    /**
     * Aggiunge un nuovo account sia in memoria sia su file.
     * <p>
     * Equivale a chiamare {@link #aggiungiInMemoria(Account)} seguito da
     * {@link #salvaSuFile()}.
     *
     * @param account l'account da aggiungere
     */
    public void aggiungi(Account account) {
        aggiungiInMemoria(account);
        salvaSuFile();
    }
}
