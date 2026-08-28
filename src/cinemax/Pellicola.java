package cinemax;

/**
 * Rappresenta i dati anagrafici di un film.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class Pellicola {
    /** Il titolo del film. */
    private String titolo;
    /** Il genere del film. */
    private String genere;
    /** Il regista del film. */
    private String regista;
    /** L'anno di uscita del film. */
    private int anno;
    /** La durata del film in minuti. */
    private int durataMinuti;
    /** L'eta' minima consigliata per la visione. */
    private int etaMinima;

    /**
     * Costruttore che inizializza tutti i campi anagrafici del film.
     *
     * @param titolo       il titolo del film
     * @param genere       il genere del film
     * @param regista      il regista del film
     * @param anno         l'anno di uscita
     * @param durataMinuti la durata in minuti
     * @param etaMinima    l'eta' minima consigliata
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
     *
     * @return il titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo del film.
     *
     * @param titolo il nuovo titolo
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce il genere del film.
     *
     * @return il genere
     */
    public String getGenere() {
        return genere;
    }

    /**
     * Imposta il genere del film.
     *
     * @param genere il nuovo genere
     */
    public void setGenere(String genere) {
        this.genere = genere;
    }

    /**
     * Restituisce il regista del film.
     *
     * @return il regista
     */
    public String getRegista() {
        return regista;
    }

    /**
     * Imposta il regista del film.
     *
     * @param regista il nuovo regista
     */
    public void setRegista(String regista) {
        this.regista = regista;
    }

    /**
     * Restituisce l'anno di uscita del film.
     *
     * @return l'anno
     */
    public int getAnno() {
        return anno;
    }

    /**
     * Imposta l'anno di uscita del film.
     *
     * @param anno il nuovo anno
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * Restituisce la durata del film in minuti.
     *
     * @return la durata in minuti
     */
    public int getDurataMinuti() {
        return durataMinuti;
    }

    /**
     * Imposta la durata del film in minuti.
     *
     * @param durataMinuti la nuova durata in minuti
     */
    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    /**
     * Restituisce l'eta' minima consigliata per la visione.
     *
     * @return l'eta' minima
     */
    public int getEtaMinima() {
        return etaMinima;
    }

    /**
     * Imposta l'eta' minima consigliata per la visione.
     *
     * @param etaMinima la nuova eta' minima
     */
    public void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce una rappresentazione testuale sintetica del film.
     *
     * @return stringa con titolo, genere, anno, regista, durata ed eta' minima
     */
    @Override
    public String toString() {
        return titolo + " (" + genere + ", " + anno + ") - regia di " + regista
                + " - " + durataMinuti + " min - vietato ai minori di " + etaMinima;
    }
}
