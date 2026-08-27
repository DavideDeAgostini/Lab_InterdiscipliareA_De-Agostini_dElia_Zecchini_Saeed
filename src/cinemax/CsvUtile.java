package cinemax;

/**
 * Funzioni statiche per leggere e scrivere righe in formato CSV, gestendo
 * correttamente i campi racchiusi tra virgolette (che possono contenere
 * virgole al loro interno, come nei titoli dei film).
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class CsvUtile {
    private CsvUtile() {
    }

    /** Divide una riga CSV in campi, rispettando le virgolette. */
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
     * Compone una riga CSV, racchiudendo tra virgolette i campi che contengono
     * virgole o virgolette.
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
