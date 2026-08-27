package cinemax;

/**
 * Genera codici univoci per le nuove prenotazioni.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class GeneratoreCodice {
    private static int contatore = 1;

    private GeneratoreCodice() {
    }

    /**
     * Genera un codice nel formato "PRN-000001", verificando che non sia gia' usato
     * tra le prenotazioni
     * esistenti.
     */
    public static String generaCodicePrenotazione(Biglietto[] prenotazioniEsistenti) {
        String candidato;
        boolean giaUsato;
        do {
            candidato = String.format("PRN-%06d", contatore);
            contatore++;
            giaUsato = false;
            for (int i = 0; i < prenotazioniEsistenti.length; i++) {
                if (prenotazioniEsistenti[i].getCodice().equalsIgnoreCase(candidato)) {
                    giaUsato = true;
                    break;
                }
            }
        } while (giaUsato);
        return candidato;
    }
}
