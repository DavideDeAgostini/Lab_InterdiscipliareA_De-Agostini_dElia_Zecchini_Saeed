package cinemax;

import java.time.LocalDate;

/**
 * Gestisce login, registrazione e stato di sessione dell'utente corrente.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class GestoreAccessi {
    /** L'archivio degli account su cui operano login e registrazione. */
    private ArchivioAccount archivioAccount;
    /** L'account attualmente autenticato, oppure null se nessuno ha effettuato il login. */
    private Account utenteCorrente;

    /**
     * Costruttore che collega il gestore all'archivio degli account.
     *
     * @param archivioAccount l'archivio da cui leggere e su cui salvare gli account
     */
    public GestoreAccessi(ArchivioAccount archivioAccount) {
        this.archivioAccount = archivioAccount;
        this.utenteCorrente = null;
    }

    /**
     * Verifica username e password; se corretti imposta l'utente corrente e lo
     * restituisce.
     * <p>
     * La verifica della password avviene tramite {@link Cifratura#corrisponde(String, String)},
     * senza mai decifrare la password salvata.
     *
     * @param username lo username da verificare
     * @param password la password in chiaro inserita dall'utente
     * @return l'account autenticato, oppure null se username o password non sono corretti
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
     * Registra un nuovo cliente, se lo username non e' gia' in uso.
     * <p>
     * La password viene cifrata con {@link Cifratura#cifra(String)} prima di
     * essere salvata; il nuovo account viene creato con ruolo
     * {@link Account#SPETTATORE}.
     *
     * @param nome        il nome del nuovo cliente
     * @param cognome     il cognome del nuovo cliente
     * @param username    lo username scelto (deve essere univoco)
     * @param password    la password in chiaro scelta dal cliente
     * @param dataNascita la data di nascita (puo' essere null)
     * @param domicilio   il luogo di domicilio
     * @return true se la registrazione e' andata a buon fine, false altrimenti
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
     * Termina la sessione dell'utente corrente, riportando
     * {@link #utenteCorrente} a null.
     */
    public void logout() {
        utenteCorrente = null;
    }

    /**
     * Restituisce l'utente attualmente autenticato.
     *
     * @return l'account corrente, oppure null se nessuno ha effettuato il login
     */
    public Account getUtenteCorrente() {
        return utenteCorrente;
    }
}
