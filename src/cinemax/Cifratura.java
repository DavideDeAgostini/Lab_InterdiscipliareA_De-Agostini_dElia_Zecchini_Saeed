package cinemax;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funzioni statiche per cifrare e verificare le password (algoritmo SHA-256).
 *
 * @author Davide De Agostini
 * @author Luigi d'Elia
 * @author Ahsan Saeed
 * @author Martina Zecchini
 */
public class Cifratura {
    private Cifratura() {
    }

    /**
     * Restituisce l'impronta SHA-256 (in esadecimale) del testo fornito, oppure
     * null in caso di errore.
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

    /** Verifica se un testo in chiaro corrisponde a un valore gia' cifrato. */
    public static boolean corrisponde(String testoInChiaro, String cifrato) {
        String calcolato = cifra(testoInChiaro);
        return calcolato != null && cifrato != null && calcolato.equals(cifrato);
    }
}
