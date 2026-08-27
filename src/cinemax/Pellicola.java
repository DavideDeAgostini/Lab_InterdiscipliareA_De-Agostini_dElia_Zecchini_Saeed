package cinemax;

/**
 * Rappresenta i dati anagrafici di un film.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class Pellicola {

    /** Titolo dell'opera cinematografica. */
    private String titolo;

    /** Genere cinematografico (es. Azione, Commedia, Drammatico). */
    private String genere;

    /** Nome e cognome del regista. */
    private String regista;

    /** Anno di uscita o produzione del film. */
    private int anno;

    /** Durata complessiva della proiezione espressa in minuti. */
    private int durataMinuti;

    /** Eta' minima richiesta per la visione (0 se per tutti). */
    private int etaMinima;

    /**
     * Costruttore completo per inizializzare tutte le informazioni anagrafiche del film.
     */
    public Pellicola(String titolo, String genere, String regista, int anno, int durataMinuti, int etaMinima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce il titolo del film.
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Aggiorna il titolo del film.
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce il genere del film.
     */
    public String getGenere() {
        return genere;
    }

    /**
     * Aggiorna il genere del film.
     */
    public void setGenere(String genere) {
        this.genere = genere;
    }

    /**
     * Restituisce il regista del film.
     */
    public String getRegista() {
        return regista;
    }

    /**
     * Aggiorna il nome del regista.
     */
    public void setRegista(String regista) {
        this.regista = regista;
    }

    /**
     * Restituisce l'anno di uscita del film.
     */
    public int getAnno() {
        return anno;
    }

    /**
     * Aggiorna l'anno di produzione del film.
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * Restituisce la durata del film in minuti.
     */
    public int getDurataMinuti() {
        return durataMinuti;
    }

    /**
     * Aggiorna la durata del film in minuti.
     */
    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    /**
     * Restituisce l'eta' minima richiesta per la visione.
     */
    public int getEtaMinima() {
        return etaMinima;
    }

    /**
     * Aggiorna la soglia di eta' minima per la visione.
     */
    public void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce la scheda testuale riepilogativa della pellicola.
     */
    @Override
    public String toString() {
        return titolo + " (" + genere + ", " + anno + ") - regia di " + regista
                + " - " + durataMinuti + " min - vietato ai minori di " + etaMinima;
    }
}