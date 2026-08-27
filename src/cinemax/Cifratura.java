package cinemax;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funzioni statiche per cifrare e verificare le password (algoritmo SHA-256).
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class Cifratura {

    /**
     * Costruttore privato per impedire l'istanziazione diretta della classe di utilita'.
     */
    private Cifratura() {
    }

    /**
     * Calcola e restituisce l'impronta hash SHA-256 (in formato esadecimale)
     * a partire da una stringa di testo in chiaro. Ritorna null in caso di errore.
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
     * Verifica se una password in chiaro corrisponde a un'impronta hash memorizzata.
     */
    public static boolean corrisponde(String testoInChiaro, String cifrato) {
        String calcolato = cifra(testoInChiaro);
        return calcolato != null && cifrato != null && calcolato.equals(cifrato);
    }
}