package cinemax;

import java.time.LocalDateTime;

/**
 * Rappresenta una prenotazione effettuata da un cliente per una proiezione.
 * La proiezione non e' referenziata con un id numerico, ma con la sua
 * chiave composta (titolo del film + data/ora), esattamente come viene
 * identificata in Spettacolo.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class Biglietto {

    /** Codice identificativo univoco della prenotazione/biglietto. */
    private String codice;

    /** Username del cliente che ha effettuato l'acquisto. */
    private String usernameCliente;

    /** Titolo del film associato allo spettacolo prenotato. */
    private String titoloFilm;

    /** Data e ora di inizio della proiezione. */
    private LocalDateTime dataOraSpettacolo;

    /** Quantita' di posti riservati con questo biglietto. */
    private int numeroPosti;

    /** Prezzo del singolo posto a sedere. */
    private double costoUnitario;

    /**
     * Costruttore completo per inizializzare tutti i campi del biglietto.
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
     * Restituisce il codice univoco del biglietto.
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Restituisce lo username del cliente proprietario del biglietto.
     */
    public String getUsernameCliente() {
        return usernameCliente;
    }

    /**
     * Restituisce il titolo del film prenotato.
     */
    public String getTitoloFilm() {
        return titoloFilm;
    }

    /**
     * Aggiorna il titolo del film associato al biglietto.
     */
    public void setTitoloFilm(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    /**
     * Restituisce la data e l'ora della proiezione.
     */
    public LocalDateTime getDataOraSpettacolo() {
        return dataOraSpettacolo;
    }

    /**
     * Aggiorna la data e l'ora della proiezione.
     */
    public void setDataOraSpettacolo(LocalDateTime dataOraSpettacolo) {
        this.dataOraSpettacolo = dataOraSpettacolo;
    }

    /**
     * Restituisce il numero di posti prenotati.
     */
    public int getNumeroPosti() {
        return numeroPosti;
    }

    /**
     * Aggiorna il numero di posti prenotati.
     */
    public void setNumeroPosti(int numeroPosti) {
        this.numeroPosti = numeroPosti;
    }

    /**
     * Restituisce il costo del singolo posto.
     */
    public double getCostoUnitario() {
        return costoUnitario;
    }

    /**
     * Aggiorna il costo del singolo posto.
     */
    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    /**
     * Calcola l'importo totale della prenotazione (numero posti * costo unitario).
     */
    public double costoTotale() {
        return numeroPosti * costoUnitario;
    }

    /**
     * Restituisce la rappresentazione testuale del biglietto con riepilogo dei dettagli.
     */
    @Override
    public String toString() {
        return codice + " - cliente " + usernameCliente + " - " + titoloFilm + " ("
                + dataOraSpettacolo.format(Spettacolo.FORMATO_SCRITTURA) + ") - " + numeroPosti
                + " posti - totale " + costoTotale() + " EUR";
    }
}
