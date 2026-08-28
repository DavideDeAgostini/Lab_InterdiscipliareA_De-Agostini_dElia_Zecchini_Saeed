package cinemax;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funzioni statiche per cifrare e verificare le password (algoritmo SHA-256).
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class Cifratura {
    /**
     * Costruttore privato: la classe espone solo metodi statici e non deve
     * essere istanziata.
     */
    private Cifratura() {
    }

    /**
     * Restituisce l'impronta SHA-256 (in esadecimale) del testo fornito.
     * <p>
     * La codifica dei byte e' fissata esplicitamente a
     * {@link StandardCharsets#UTF_8}, per garantire che lo stesso testo
     * produca sempre lo stesso hash indipendentemente dalla piattaforma di
     * esecuzione.
     *
     * @param testoInChiaro il testo da cifrare
     * @return l'impronta SHA-256 in esadecimale, oppure null in caso di errore
     */
    public static String cifra(String testoInChiaro) {
        try {
            MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");
            byte[] risultato = algoritmo.digest(testoInChiaro.getBytes(StandardCharsets.UTF_8));
            StringBuilder esadecimale = new StringBuilder();
            for (int i = 0; i < risultato.length; i++) {
                esadecimale.append(String.format("%02x", risultato[i]));
            }
            return esadecimale.toString();
        } catch (NoSuchAlgorithmException erroreCifratura) {
            System.out.println("Errore durante la cifratura della password: " +
                    erroreCifratura.getMessage());
            return null;
        }
    }

    /**
     * Verifica se un testo in chiaro corrisponde a un valore gia' cifrato.
     * <p>
     * Ricalcola l'hash del testo in chiaro con {@link #cifra(String)} e lo
     * confronta con il valore cifrato salvato: la password cifrata non viene
     * mai decifrata (operazione impossibile per un hash).
     *
     * @param testoInChiaro il testo da verificare
     * @param cifrato       il valore gia' cifrato con cui confrontare
     * @return true se il testo in chiaro corrisponde al valore cifrato
     */
    public static boolean corrisponde(String testoInChiaro, String cifrato) {
        String calcolato = cifra(testoInChiaro);
        return calcolato != null && cifrato != null && calcolato.equals(cifrato);
    }
}
