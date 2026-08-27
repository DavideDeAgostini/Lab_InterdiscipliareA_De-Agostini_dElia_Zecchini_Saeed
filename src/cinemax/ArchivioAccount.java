package cinemax;

import java.time.LocalDate;

/**
 * Rappresenta un utente registrato nel sistema CineMax, indipendentemente dal suo ruolo.
 * <p>
 * Scelta progettuale: il ruolo viene gestito tramite costanti String anziche' con
 * l'ereditarieta' (sottoclassi) poiche' i tipi di utente condividono gli stessi identici
 * attributi e differiscono unicamente per i permessi operativi (delegati alle classi Gestore).
 * <p>
 * La data di nascita e' facoltativa e puo' assumere valore {@code null}.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class Account {

    // COSTANTI DI CLASSE

    /** Ruolo base per gli utenti che consultano e acquistano biglietti. */
    public static final String SPETTATORE = "SPETTATORE";

    /** Ruolo amministrativo per la gestione del palinsesto e della sala. */
    public static final String OPERATORE = "OPERATORE";

    /** Ruolo per il personale addetto alla cassa e alla vendita diretta. */
    public static final String CASSIERE = "CASSIERE";

    // ATTRIBUTI DI ISTANZA (Incapsulamento: tutti privati)
    
    private String nome;
    private String cognome;
    private String username;
    private String passwordCifrata; // Memorizza solo l'hash della password per sicurezza
    private LocalDate dataNascita;  // Campo facoltativo: puo' essere null
    private String domicilio;
    private String ruolo;

    // COSTRUTTORE

    /**
     * Costruttore completo per inizializzare tutti i campi di un nuovo Account.
     *
     *  il nome dell'utente
     *  il cognome dell'utente
     *  il nome utente univoco per l'accesso
     *  la password gia' convertita in hash
     *  la data di nascita (puo' essere {@code null} se omessa)
     *  l'indirizzo di domicilio dell'utente
     *  la costante del ruolo assegnato (SPETTATORE, OPERATORE, CASSIERE)
     */
    public Account(String nome, String cognome, String username, String passwordCifrata,
                   LocalDate dataNascita, String domicilio, String ruolo) {
        // Uso del riferimento 'this' per distinguere i parametri dagli attributi di istanza
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordCifrata = passwordCifrata;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    // METODI GETTER E SETTER (Interfaccia pubblica per l'accesso controllato)

    /**
     * Restituisce il nome dell'utente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Aggiorna il nome dell'utente.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Aggiorna il cognome dell'utente.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce l'username dell'account.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Aggiorna l'username dell'account.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce l'impronta cifrata (hash) della password.
     */
    public String getPasswordCifrata() {
        return passwordCifrata;
    }

    /**
     * Imposta il nuovo hash della password cifrata.
     */
    public void setPasswordCifrata(String passwordCifrata) {
        this.passwordCifrata = passwordCifrata;
    }

    /**
     * Restituisce la data di nascita dell'utente.
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * Aggiorna la data di nascita dell'utente.
     */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * Restituisce il domicilio dell'utente.
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * Aggiorna il domicilio dell'utente.
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * Restituisce la tipologia/ruolo associato all'account.
     */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Aggiorna il ruolo dell'account.
     */
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    // METODI SOVRASCRITTI DA Object

    /**
     * Fornisce una rappresentazione sintetica in formato testuale dell'account,
     * utile per log, debug e visualizzazione a schermo.
     *
     * Ritorna una stringa nel formato "Nome Cognome (username, RUOLO)"
     */
    @Override
    public String toString() {
        return nome + " " + cognome + " (" + username + ", " + ruolo + ")";
    }
}
