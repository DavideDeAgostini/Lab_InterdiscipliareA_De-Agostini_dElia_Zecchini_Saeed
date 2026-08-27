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
    private String codice;
    private String usernameCliente;
    private String titoloFilm;
    private LocalDateTime dataOraSpettacolo;
    private int numeroPosti;
    private double costoUnitario;

    public Biglietto(String codice, String usernameCliente, String titoloFilm, LocalDateTime dataOraSpettacolo,
            int numeroPosti, double costoUnitario) {
        this.codice = codice;
        this.usernameCliente = usernameCliente;
        this.titoloFilm = titoloFilm;
        this.dataOraSpettacolo = dataOraSpettacolo;
        this.numeroPosti = numeroPosti;
        this.costoUnitario = costoUnitario;
    }

    public String getCodice() {
        return codice;
    }

    public String getUsernameCliente() {
        return usernameCliente;
    }

    public String getTitoloFilm() {
        return titoloFilm;
    }

    public void setTitoloFilm(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    public LocalDateTime getDataOraSpettacolo() {
        return dataOraSpettacolo;
    }

    public void setDataOraSpettacolo(LocalDateTime dataOraSpettacolo) {
        this.dataOraSpettacolo = dataOraSpettacolo;
    }

    public int getNumeroPosti() {
        return numeroPosti;
    }

    public void setNumeroPosti(int numeroPosti) {
        this.numeroPosti = numeroPosti;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public double costoTotale() {
        return numeroPosti * costoUnitario;
    }

    @Override
    public String toString() {
        return codice + " - cliente " + usernameCliente + " - " + titoloFilm + " ("
                + dataOraSpettacolo.format(Spettacolo.FORMATO_SCRITTURA) + ") - " + numeroPosti
                + " posti - totale " + costoTotale() + " EUR";
    }
}
