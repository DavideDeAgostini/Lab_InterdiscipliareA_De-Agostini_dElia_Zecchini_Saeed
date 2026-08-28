package cinemax;

/**
 * Funzioni statiche per leggere e scrivere righe in formato CSV, gestendo
 * correttamente i campi racchiusi tra virgolette (che possono contenere
 * virgole al loro interno, come nei titoli dei film).
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class CsvUtile {
    /**
     * Costruttore privato: la classe espone solo metodi statici e non deve
     * essere istanziata.
     */
    private CsvUtile() {
    }

    /**
     * Divide una riga CSV in campi, rispettando le virgolette.
     * <p>
     * Un campo racchiuso tra virgolette puo' contenere virgole al proprio
     * interno senza che vengano interpretate come separatori.
     *
     * @param riga la riga CSV da dividere
     * @return un array con i campi estratti dalla riga, nell'ordine in cui compaiono
     */
    public static String[] dividiRiga(String riga) {
        String[] campi = new String[10];
        int numeroCampi = 0;
        StringBuilder campoCorrente = new StringBuilder();
        boolean dentroVirgolette = false;
        for (int i = 0; i < riga.length(); i++) {
            char carattere = riga.charAt(i);
            if (carattere == '"') {
                dentroVirgolette = !dentroVirgolette;
            } else if (carattere == ',' && !dentroVirgolette) {
                campi = aggiungiCampo(campi, numeroCampi, campoCorrente.toString());
                numeroCampi++;
                campoCorrente = new StringBuilder();
            } else {
                campoCorrente.append(carattere);
            }
        }
        campi = aggiungiCampo(campi, numeroCampi, campoCorrente.toString());
        numeroCampi++;
        String[] risultato = new String[numeroCampi];
        for (int i = 0; i < numeroCampi; i++) {
            risultato[i] = campi[i];
        }
        return risultato;
    }

    /**
     * Inserisce un valore nell'array dei campi in costruzione, raddoppiando
     * l'array se l'indice richiesto supera la capacita' attuale.
     * <p>
     * Metodo di supporto usato esclusivamente da {@link #dividiRiga(String)}.
     *
     * @param campi  l'array dei campi in costruzione
     * @param indice l'indice in cui inserire il valore
     * @param valore il valore del campo da inserire
     * @return l'array dei campi, eventualmente ridimensionato
     */
    private static String[] aggiungiCampo(String[] campi, int indice, String valore) {
        if (indice == campi.length) {
            String[] nuovoArray = new String[campi.length * 2];
            for (int i = 0; i < campi.length; i++) {
                nuovoArray[i] = campi[i];
            }
            campi = nuovoArray;
        }
        campi[indice] = valore;
        return campi;
    }

    /**
     * Compone una riga CSV, racchiudendo tra virgolette i campi che
     * contengono virgole o virgolette.
     * <p>
     * Le virgolette eventualmente presenti in un campo vengono raddoppiate,
     * secondo la convenzione CSV standard.
     *
     * @param campi i valori dei campi da comporre in un'unica riga, nell'ordine desiderato
     * @return la riga CSV composta
     */
    public static String componiRiga(String... campi) {
        StringBuilder riga = new StringBuilder();
        for (int i = 0; i < campi.length; i++) {
            String valore;
            if (campi[i] == null) {
                valore = "";
            } else {
                valore = campi[i];
            }
            boolean necessitaVirgolette = valore.contains(",") || valore.contains("\"");
            if (necessitaVirgolette) {
                valore = valore.replace("\"", "\"\"");
                riga.append('"').append(valore).append('"');
            } else {
                riga.append(valore);
            }
            if (i < campi.length - 1) {
                riga.append(',');
            }
        }
        return riga.toString();
    }
}
