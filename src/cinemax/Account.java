package cinemax;

import java.time.LocalDate;

/**
 * Rappresenta un utente registrato, di qualunque ruolo.
 * <p>
 * Il ruolo e' rappresentato con una costante {@link String} invece che con
 * tre sottoclassi: i tre ruoli non hanno campi diversi tra loro, cambiano
 * solo le operazioni permesse, e quelle sono gestite dalle classi Gestore*.
 * <p>
 * La data di nascita e' facoltativa: usa direttamente {@link LocalDate}
 * della libreria standard e puo' essere <code>null</code> se non fornita.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class Account {
    /** Costante che identifica il ruolo di cliente registrato. */
    public static final String SPETTATORE = "SPETTATORE";
    /** Costante che identifica il ruolo di proiezionista. */
    public static final String OPERATORE = "OPERATORE";
    /** Costante che identifica il ruolo di bigliettaio. */
    public static final String CASSIERE = "CASSIERE";
    /** Il nome dell'account. */
    private String nome;
    /** Il cognome dell'account. */
    private String cognome;
    /** Lo username univoco, usato per il login. */
    private String username;
    /** La password dell'account, salvata in formato hash SHA-256. */
    private String passwordCifrata;
    /** La data di nascita dell'account, facoltativa (puo' essere null). */
    private LocalDate dataNascita;
    /** Il luogo di domicilio dell'account. */
    private String domicilio;
    /** Il ruolo dell'account (uno tra {@link #SPETTATORE}, {@link #OPERATORE}, {@link #CASSIERE}). */
    private String ruolo;

    /**
     * Costruttore che inizializza tutti i campi dell'account.
     *
     * @param nome            il nome dell'account
     * @param cognome         il cognome dell'account
     * @param username        lo username univoco per il login
     * @param passwordCifrata la password gia' cifrata con SHA-256
     * @param dataNascita     la data di nascita (puo' essere null)
     * @param domicilio       il luogo di domicilio
     * @param ruolo           il ruolo nel sistema
     */
    public Account(String nome, String cognome, String username, String passwordCifrata,
            LocalDate dataNascita, String domicilio, String ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordCifrata = passwordCifrata;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce il nome dell'account.
     *
     * @return il nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dell'account.
     *
     * @param nome il nuovo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dell'account.
     *
     * @return il cognome
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome dell'account.
     *
     * @param cognome il nuovo cognome
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce lo username dell'account.
     *
     * @return lo username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'account.
     *
     * @param username il nuovo username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password cifrata dell'account.
     *
     * @return la password in formato hash SHA-256
     */
    public String getPasswordCifrata() {
        return passwordCifrata;
    }

    /**
     * Imposta la password cifrata dell'account.
     *
     * @param passwordCifrata la nuova password (deve essere gia' cifrata)
     */
    public void setPasswordCifrata(String passwordCifrata) {
        this.passwordCifrata = passwordCifrata;
    }

    /**
     * Restituisce la data di nascita dell'account.
     *
     * @return la data di nascita, puo' essere null
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * Imposta la data di nascita dell'account.
     *
     * @param dataNascita la nuova data di nascita
     */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * Restituisce il luogo di domicilio dell'account.
     *
     * @return il domicilio
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * Imposta il luogo di domicilio dell'account.
     *
     * @param domicilio il nuovo domicilio
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * Restituisce il ruolo dell'account.
     *
     * @return il ruolo ({@link #SPETTATORE}, {@link #OPERATORE} o {@link #CASSIERE})
     */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Imposta il ruolo dell'account.
     *
     * @param ruolo il nuovo ruolo
     */
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    /**
     * Restituisce una rappresentazione testuale sintetica dell'account.
     *
     * @return stringa con nome, cognome, username e ruolo
     */
    @Override
    public String toString() {
        return nome + " " + cognome + " (" + username + ", " + ruolo + ")";
    }
}
