package cinemax;

import java.time.LocalDate;

/**
 * Gestisce login, registrazione e stato di sessione dell'utente corrente.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class GestoreAccessi {

    /** Riferimento all'archivio per la gestione degli account persistenti. */
    private ArchivioAccount archivioAccount;

    /** Mantiene il riferimento all'account attualmente autenticato (null se nessun utente e' loggato). */
    private Account utenteCorrente;

    /**
     * Costruttore: inizializza il gestore associando l'archivio degli account
     * e impostando la sessione iniziale a null.
     */
    public GestoreAccessi(ArchivioAccount archivioAccount) {
        this.archivioAccount = archivioAccount;
        this.utenteCorrente = null;
    }

    /**
     * Esegue l'autenticazione verificando username e hash della password.
     * In caso di successo memorizza l'account come utente corrente.
     */
    public Account login(String username, String password) {
        Account account = archivioAccount.trovaPerUsername(username);
        if (account == null) {
            System.out.println("Username non trovato.");
            return null;
        }
        if (!Cifratura.corrisponde(password, account.getPasswordCifrata())) {
            System.out.println("Password errata.");
            return null;
        }
        utenteCorrente = account;
        return account;
    }

    /**
     * Registra un nuovo account con ruolo SPETTATORE, cifrando la password
     * e verificando che lo username scelto sia univoco e valido.
     */
    public boolean registraCliente(String nome, String cognome, String username, String password,
            LocalDate dataNascita, String domicilio) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username non valido.");
            return false;
        }
        if (archivioAccount.trovaPerUsername(username) != null) {
            System.out.println("Username gia' in uso.");
            return false;
        }
        String passwordCifrata = Cifratura.cifra(password);
        if (passwordCifrata == null) {
            System.out.println("Errore durante la registrazione.");
            return false;
        }
        Account nuovo = new Account(nome, cognome, username, passwordCifrata, dataNascita, domicilio,
                Account.SPETTATORE);
        archivioAccount.aggiungi(nuovo);
        return true;
    }

    /**
     * Termina la sessione dell'utente corrente reimpostando il riferimento a null.
     */
    public void logout() {
        utenteCorrente = null;
    }

    /**
     * Restituisce l'account attualmente loggato nella sessione di lavoro.
     */
    public Account getUtenteCorrente() {
        return utenteCorrente;
    }
}