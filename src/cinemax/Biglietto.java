package cinemax;

import java.time.LocalDateTime;

/**
 * Rappresenta una prenotazione effettuata da un cliente per una proiezione.
 * <p>
 * La proiezione non e' referenziata con un id numerico, ma con la sua
 * chiave composta (titolo del film + data/ora), esattamente come viene
 * identificata in {@link Spettacolo}.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class Biglietto {
    /** Il codice univoco della prenotazione, generato da {@link GeneratoreCodice}. */
    private String codice;
    /** Lo username del cliente che ha effettuato la prenotazione. */
    private String usernameCliente;
    /** Il titolo del film della proiezione prenotata. */
    private String titoloFilm;
    /** La data e l'ora della proiezione prenotata. */
    private LocalDateTime dataOraSpettacolo;
    /** Il numero di posti prenotati. */
    private int numeroPosti;
    /** Il costo unitario del biglietto, in euro. */
    private double costoUnitario;

    /**
     * Costruttore che inizializza tutti i campi della prenotazione.
     *
     * @param codice            il codice univoco della prenotazione
     * @param usernameCliente   lo username del cliente
     * @param titoloFilm        il titolo del film prenotato
     * @param dataOraSpettacolo la data/ora della proiezione prenotata
     * @param numeroPosti       il numero di posti prenotati
     * @param costoUnitario     il costo unitario del biglietto in euro
     */
    public Biglietto(String codice, String usernameCliente, String titoloFilm, LocalDateTime dataOraSpettacolo,
            int numeroPosti, double costoUnitario) {
        this.codice = codice;
        this.usernameCliente = usernameCliente;
        this.titoloFilm = titoloFilm;
        this.dataOraSpettacolo = dataOraSpettacolo;
        this.numeroPosti = numeroPosti;
        this.costoUnitario = costoUnitario;
    }

    /**
     * Restituisce il codice univoco della prenotazione.
     *
     * @return il codice
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Restituisce lo username del cliente che ha effettuato la prenotazione.
     *
     * @return lo username del cliente
     */
    public String getUsernameCliente() {
        return usernameCliente;
    }

    /**
     * Restituisce il titolo del film della proiezione prenotata.
     *
     * @return il titolo del film
     */
    public String getTitoloFilm() {
        return titoloFilm;
    }

    /**
     * Imposta il titolo del film della proiezione prenotata.
     *
     * @param titoloFilm il nuovo titolo del film
     */
    public void setTitoloFilm(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    /**
     * Restituisce la data e l'ora della proiezione prenotata.
     *
     * @return la data/ora della proiezione
     */
    public LocalDateTime getDataOraSpettacolo() {
        return dataOraSpettacolo;
    }

    /**
     * Imposta la data e l'ora della proiezione prenotata.
     *
     * @param dataOraSpettacolo la nuova data/ora della proiezione
     */
    public void setDataOraSpettacolo(LocalDateTime dataOraSpettacolo) {
        this.dataOraSpettacolo = dataOraSpettacolo;
    }

    /**
     * Restituisce il numero di posti prenotati.
     *
     * @return il numero di posti
     */
    public int getNumeroPosti() {
        return numeroPosti;
    }

    /**
     * Imposta il numero di posti prenotati.
     *
     * @param numeroPosti il nuovo numero di posti
     */
    public void setNumeroPosti(int numeroPosti) {
        this.numeroPosti = numeroPosti;
    }

    /**
     * Restituisce il costo unitario del biglietto.
     *
     * @return il costo unitario in euro
     */
    public double getCostoUnitario() {
        return costoUnitario;
    }

    /**
     * Imposta il costo unitario del biglietto.
     *
     * @param costoUnitario il nuovo costo unitario in euro
     */
    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    /**
     * Calcola il costo totale della prenotazione.
     *
     * @return il prodotto tra numero di posti e costo unitario, in euro
     */
    public double costoTotale() {
        return numeroPosti * costoUnitario;
    }

    /**
     * Restituisce una rappresentazione testuale sintetica della prenotazione.
     *
     * @return stringa con codice, cliente, film, data/ora, posti e totale
     */
    @Override
    public String toString() {
        return codice + " - cliente " + usernameCliente + " - " + titoloFilm + " ("
                + dataOraSpettacolo.format(Spettacolo.FORMATO_SCRITTURA) + ") - " + numeroPosti
                + " posti - totale " + costoTotale() + " EUR";
    }
}
