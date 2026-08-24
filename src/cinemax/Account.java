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
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class Account {
    public static final String SPETTATORE = "SPETTATORE";
    public static final String OPERATORE = "OPERATORE";
    public static final String CASSIERE = "CASSIERE";
    private String nome;
    private String cognome;
    private String username;
    private String passwordCifrata;
    private LocalDate dataNascita; // puo' essere null: e' facoltativa
    private String domicilio;
    private String ruolo;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordCifrata() {
        return passwordCifrata;
    }

    public void setPasswordCifrata(String passwordCifrata) {
        this.passwordCifrata = passwordCifrata;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + username + ", " + ruolo + ")";
    }
}