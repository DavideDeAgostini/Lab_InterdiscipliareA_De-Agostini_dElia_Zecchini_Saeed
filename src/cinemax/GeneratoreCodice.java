package cinemax;

/**
 * Genera codici univoci per le nuove prenotazioni.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class GeneratoreCodice {
    /**
     * Contatore statico usato per comporre il prossimo codice candidato.
     * <p>
     * Non viene persistito su file: riparte da 1 ad ogni riavvio
     * dell'applicazione, ma l'unicita' resta comunque garantita dal
     * controllo esplicito effettuato in {@link #generaCodicePrenotazione(Biglietto[])}.
     */
    private static int contatore = 1;

    /**
     * Costruttore privato: la classe espone solo metodi statici e non deve
     * essere istanziata.
     */
    private GeneratoreCodice() {
    }

    /**
     * Genera un codice nel formato <code>"PRN-000001"</code>, verificando che
     * non sia gia' usato tra le prenotazioni esistenti.
     *
     * @param prenotazioniEsistenti le prenotazioni gia' presenti, contro cui verificare l'unicita'
     * @return un codice di prenotazione univoco rispetto a quelli esistenti
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
