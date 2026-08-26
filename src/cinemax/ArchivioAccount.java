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
 * Il file data/utenti.csv viene consegnato gia' popolato (con le password
 * gia' cifrate), con una riga di intestazione in testa e poi una riga per
 * account, nel formato nome,cognome,username,password_cifrata,data_nascita,
 * domicilio,ruolo. Deve gia' contenere 2 account OPERATORE e 5 account
 * CASSIERE, come richiesto dalle specifiche.
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class ArchivioAccount {
    private String percorsoFile;
    private Account[] elenco;
    private int quantita;

    public ArchivioAccount(String percorsoFile) {
        this.percorsoFile = percorsoFile;
        this.elenco = new Account[10];
        this.quantita = 0;
        caricaDaFile();
    }

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
    ignora senza avviso
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
     * Riscrive interamente il file a partire dall'elenco in memoria, con la riga di
     * intestazione in testa.
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

    public Account[] elencoTutti() {
        Account[] copia = new Account[quantita];
        for (int i = 0; i < quantita; i++) {
            copia[i] = elenco[i];
        }
        return copia;
    }

    public Account trovaPerUsername(String username) {
        for (int i = 0; i < quantita; i++) {
            if (elenco[i].getUsername().equalsIgnoreCase(username)) {
                return elenco[i];
            }
        }
        return null;
    }

    public void aggiungi(Account account) {
        aggiungiInMemoria(account);
        salvaSuFile();
    }
}
