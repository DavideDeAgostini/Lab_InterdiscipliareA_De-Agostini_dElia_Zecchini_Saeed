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

    /** Contatore progressivo statico per la generazione sequenziale dei codici. */
    private static int contatore = 1;

    /**
     * Costruttore privato per impedire l'istanziazione della classe di utilita'.
     */
    private GeneratoreCodice() {
    }

    /**
     * Genera un codice univoco nel formato "PRN-000001", garantendo
     * che non sia gia' presente tra le prenotazioni registrate.
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