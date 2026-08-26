package cinemax;

import java.time.LocalDate;

/**
 * Gestisce login, registrazione e stato di sessione dell'utente corrente.
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class GestoreAccessi {
    private ArchivioAccount archivioAccount;
    private Account utenteCorrente;

    public GestoreAccessi(ArchivioAccount archivioAccount) {
        this.archivioAccount = archivioAccount;
        this.utenteCorrente = null;
    }

    /**
     * Verifica username e password; se corretti imposta l'utente corrente e lo
     * restituisce.
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

    /** Registra un nuovo cliente, se lo username non e' gia' in uso. */
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

    public void logout() {
        utenteCorrente = null;
    }

    public Account getUtenteCorrente() {
        return utenteCorrente;
    }
}
