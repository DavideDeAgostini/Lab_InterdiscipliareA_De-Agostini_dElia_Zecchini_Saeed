package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta una singola proiezione: un film, in una certa data/ora, a un
 * certo prezzo. Usa direttamente {@link LocalDateTime} della libreria
 * standard invece di una classe scritta apposta.
 * <p>
 * Non esiste un id numerico: una proiezione e' identificata univocamente
 * dalla coppia (titolo del film, data/ora), che funge da chiave composta.
 * Questo funziona perche' modificaProiezione() ed eliminaProiezione() sono
 * permesse solo quando non esistono prenotazioni collegate: quindi, quando
 * una prenotazione fa riferimento a una proiezione, quella proiezione non
 * puo' piu' cambiare titolo o data/ora, e il riferimento resta sempre valido.
 * <p>
 * Il calcolo dei posti liberi non e' responsabilita' di questa classe
 * (richiederebbe di conoscere le prenotazioni): se ne occupa il
 * GestoreSpettacoli.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class Spettacolo {
    /**
     * Formato usato in lettura: i secondi sono opzionali (il file del docente li
     * include, il nostro no).
     */
    public static final DateTimeFormatter FORMATO_LETTURA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");
    /** Formato usato in scrittura e per la visualizzazione a schermo. */
    public static final DateTimeFormatter FORMATO_SCRITTURA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Pellicola film;
    private LocalDateTime dataOra;
    private double prezzoBiglietto;

    public Spettacolo(Pellicola film, LocalDateTime dataOra, double prezzoBiglietto) {
        this.film = film;
        this.dataOra = dataOra;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    public Pellicola getFilm() {
        return film;
    }

    public void setFilm(Pellicola film) {
        this.film = film;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce l'istante di fine proiezione, sommando la durata del film
     * all'orario di inizio.
     */
    public LocalDateTime getDataOraFine() {
        return dataOra.plusMinutes(film.getDurataMinuti());
    }

    /**
     * Due proiezioni sono considerate la stessa proiezione se hanno lo
     * stesso titolo (senza distinzione di maiuscole/minuscole) e la stessa
     * data/ora: e' la chiave composta usata al posto di un id numerico.
     */
    public boolean corrispondeA(String titolo, LocalDateTime dataOra) {
        if (titolo == null || dataOra == null) {
            return false;
        }
        return film.getTitolo().equalsIgnoreCase(titolo) && this.dataOra.equals(dataOra);
    }

    @Override
    public boolean equals(Object altro) {
        if (!(altro instanceof Spettacolo)) {
            return false;
        }
        Spettacolo altraProiezione = (Spettacolo) altro;
        return corrispondeA(altraProiezione.getFilm().getTitolo(), altraProiezione.getDataOra());
    }

    @Override
    public int hashCode() {
        return film.getTitolo().toLowerCase().hashCode() * 31 + dataOra.hashCode();
    }

    @Override
    public String toString() {
        return film.getTitolo() + " - " + dataOra.format(FORMATO_SCRITTURA) + " - " + prezzoBiglietto + " EUR";
    }
}