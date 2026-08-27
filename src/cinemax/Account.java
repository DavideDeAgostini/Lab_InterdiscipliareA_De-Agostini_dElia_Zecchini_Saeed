package cinemax;

import java.time.LocalDate;

/**
 * Rappresenta un utente registrato, di qualunque ruolo. Il ruolo e'
 * rappresentato con una costante String invece che con tre sottoclassi:
 * i tre ruoli non hanno campi diversi tra loro, cambiano solo le operazioni
 * permesse, e quelle sono gestite dalle classi Gestore*.
 * <p>
 * La data di nascita e' facoltativa: usa direttamente {@link LocalDate}
 * della libreria standard e puo' essere null se non fornita.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class Account {
    //Costanti per la definizione dei ruoli utente ammessi nel sistema
    //Costante per il ruolo di tipo SPETTATORE
    public static final String SPETTATORE = "SPETTATORE";
    //Costante per il ruolo di tipo OPERATORE
    public static final String OPERATORE = "OPERATORE";
    //Costante per il ruolo di tipo CASSIERE
    public static final String CASSIERE = "CASSIERE";
    //Attributi dell'account
    //Nome utente
    private String nome;
    //Cognome utente
    private String cognome;
    //Username univoco per effettuare il login
    private String username;
    //Password cifrata (Hash) per effettuare l'autenticazione in maniera sicura
    private String passwordCifrata;
    //Data di nascita dell'utente (QUesta è facoltativa e può essere null) 
    private LocalDate dataNascita; // puo' essere null: e' facoltativa
    //Indirizzo di domicilio dell'utente
    private String domicilio;
    //Ruolo assegnato all'utente (Spettatore, operatore o cassiere)
    private String ruolo;

    /**Costruttore completo per istanziare un nuovo Account.
     *
     *  nome             il nome dell'utente
     *  cognome          il cognome dell'utente
     *  username         il nome utente per l'autenticazione
     *  passwordCifrata  la password dell'utente gia' cifrata (hash)
     *  dataNascita      la data di nascita (può essere {null})
     *  domicilio        l'indirizzo o luogo di domicilio
     *  ruolo            la tipologia di utente (es. SPETTATORE, OPERATORE, CASSIERE)*//
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

    //Metodi getter e setter

    //Restituisce il nome dell'utente
    public String getNome() {
        return nome;
    }

    //Imposta il nome dell'utente da assegnare
    public void setNome(String nome) {
        this.nome = nome;
    }

    //Restituisce il cognome dell'utente
    public String getCognome() {
        return cognome;
    }

    //Imposta il cognome dell'utente
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    //Restituisce lo username dell'utente
    public String getUsername() {
        return username;
    }

    //Imposta lo username dell'utente
    public void setUsername(String username) {
        this.username = username;
    }

    //Restituisce la password cifrata dell'utente
    public String getPasswordCifrata() {
        return passwordCifrata;
    }

    //Imposta la password dell'utente
    public void setPasswordCifrata(String passwordCifrata) {
        this.passwordCifrata = passwordCifrata;
    }

    //Restituisce la data di nascita dell'utente 
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    //imposta la data di nascita dell'utente
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    //Restituisce il domicilio dell'utente
    public String getDomicilio() {
        return domicilio;
    }

    //Imposta il domicilio dell'utente
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    //Restituisce il ruolo dell'utente
    public String getRuolo() {
        return ruolo;
    }

    //Imposta il ruolo dell'utente
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    //Restituisce una rappresentazione testuale sintetica dell'account "Nome Cognome (Username, RUOLO)"
    @Override
    public String toString() {
        return nome + " " + cognome + " (" + username + ", " + ruolo + ")";
    }
}
