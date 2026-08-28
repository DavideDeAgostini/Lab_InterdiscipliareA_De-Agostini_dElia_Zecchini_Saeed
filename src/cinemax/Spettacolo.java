package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta una singola proiezione: un film, in una certa data/ora, a un
 * certo prezzo.
 * <p>
 * Usa direttamente {@link LocalDateTime} della libreria standard invece di
 * una classe scritta apposta.
 * <p>
 * Non esiste un id numerico: una proiezione e' identificata univocamente
 * dalla coppia (titolo del film, data/ora), che funge da chiave composta
 * (vedi {@link #corrispondeA(String, LocalDateTime)}). Questo funziona
 * perche' {@link GestoreSpettacoli#modificaProiezione} ed
 * {@link GestoreSpettacoli#eliminaProiezione} sono permesse solo quando non
 * esistono prenotazioni collegate: quindi, quando una prenotazione fa
 * riferimento a una proiezione, quella proiezione non puo' piu' cambiare
 * titolo o data/ora, e il riferimento resta sempre valido.
 * <p>
 * Il calcolo dei posti liberi non e' responsabilita' di questa classe
 * (richiederebbe di conoscere le prenotazioni): se ne occupa
 * {@link GestoreSpettacoli}.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class Spettacolo {
    /**
     * Formato usato in lettura: i secondi sono opzionali (il file del
     * docente li include, il nostro no).
     */
    public static final DateTimeFormatter FORMATO_LETTURA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");
    /** Formato usato in scrittura e per la visualizzazione a schermo. */
    public static final DateTimeFormatter FORMATO_SCRITTURA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /** Il film proiettato. */
    private Pellicola film;
    /** La data e l'ora di inizio della proiezione. */
    private LocalDateTime dataOra;
    /** Il prezzo del biglietto per questa proiezione, in euro. */
    private double prezzoBiglietto;

    /**
     * Costruttore che inizializza tutti i campi della proiezione.
     *
     * @param film            il film da proiettare
     * @param dataOra         la data e l'ora di inizio
     * @param prezzoBiglietto il prezzo del biglietto in euro
     */
    public Spettacolo(Pellicola film, LocalDateTime dataOra, double prezzoBiglietto) {
        this.film = film;
        this.dataOra = dataOra;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce il film proiettato.
     *
     * @return il film
     */
    public Pellicola getFilm() {
        return film;
    }

    /**
     * Imposta il film proiettato.
     *
     * @param film il nuovo film
     */
    public void setFilm(Pellicola film) {
        this.film = film;
    }

    /**
     * Restituisce la data e l'ora della proiezione.
     *
     * @return la data/ora
     */
    public LocalDateTime getDataOra() {
        return dataOra;
    }

    /**
     * Imposta la data e l'ora della proiezione.
     *
     * @param dataOra la nuova data/ora
     */
    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    /**
     * Restituisce il prezzo del biglietto della proiezione.
     *
     * @return il prezzo in euro
     */
    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    /**
     * Imposta il prezzo del biglietto della proiezione.
     *
     * @param prezzoBiglietto il nuovo prezzo in euro
     */
    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce l'istante di fine proiezione, sommando la durata del film
     * all'orario di inizio.
     *
     * @return la data/ora di fine proiezione
     */
    public LocalDateTime getDataOraFine() {
        return dataOra.plusMinutes(film.getDurataMinuti());
    }

    /**
     * Verifica se questa proiezione corrisponde alla chiave composta
     * fornita (titolo del film + data/ora).
     * <p>
     * Due proiezioni sono considerate la stessa proiezione se hanno lo
     * stesso titolo (senza distinzione di maiuscole/minuscole) e la stessa
     * data/ora: e' la chiave composta usata al posto di un id numerico.
     *
     * @param titolo  il titolo da confrontare (confronto case-insensitive)
     * @param dataOra la data/ora da confrontare
     * @return true se titolo e data/ora corrispondono a questa proiezione
     */
    public boolean corrispondeA(String titolo, LocalDateTime dataOra) {
        if (titolo == null || dataOra == null) {
            return false;
        }
        return film.getTitolo().equalsIgnoreCase(titolo) && this.dataOra.equals(dataOra);
    }

    /**
     * Verifica se due proiezioni sono uguali, sulla base della stessa
     * chiave composta usata da {@link #corrispondeA(String, LocalDateTime)}.
     *
     * @param altro l'oggetto con cui confrontare
     * @return true se l'oggetto e' uno Spettacolo con lo stesso titolo e la stessa data/ora
     */
    @Override
    public boolean equals(Object altro) {
        if (!(altro instanceof Spettacolo)) {
            return false;
        }
        Spettacolo altraProiezione = (Spettacolo) altro;
        return corrispondeA(altraProiezione.getFilm().getTitolo(), altraProiezione.getDataOra());
    }

    /**
     * Restituisce il codice hash della proiezione, calcolato su titolo del
     * film e data/ora, coerentemente con {@link #equals(Object)}.
     *
     * @return il codice hash
     */
    @Override
    public int hashCode() {
        return film.getTitolo().toLowerCase().hashCode() * 31 + dataOra.hashCode();
    }

    /**
     * Restituisce una rappresentazione testuale sintetica della proiezione.
     *
     * @return stringa con titolo del film, data/ora e prezzo del biglietto
     */
    @Override
    public String toString() {
        return film.getTitolo() + " - " + dataOra.format(FORMATO_SCRITTURA) + " - " + prezzoBiglietto + " EUR";
    }
}
