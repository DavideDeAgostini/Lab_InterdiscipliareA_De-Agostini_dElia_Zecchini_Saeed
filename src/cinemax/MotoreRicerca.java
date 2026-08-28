package cinemax;

import java.time.LocalDateTime;

/**
 * Contiene la logica di ricerca sulle proiezioni e sulle prenotazioni.
 * <p>
 * Tenuta separata dai Gestore* perche' i criteri (parziali, intervalli,
 * combinazioni) sono complessi e riusati in piu' punti dell'applicazione.
 *
 * @author Davide De Agostini - Matricola 766294 - CO
 * @author Luigi d'Elia - Matricola 765969 - CO
 * @author Ahsan Saeed - Matricola 767241 - CO
 * @author Martina Zecchini - Matricola 765842 - CO
 */
public class MotoreRicerca {
    /** L'archivio delle proiezioni su cui operano le ricerche di {@link #cercaProiezione}. */
    private ArchivioSpettacoli archivioSpettacoli;
    /** L'archivio delle prenotazioni su cui operano le ricerche di {@link #cercaPrenotazione}. */
    private ArchivioBiglietti archivioBiglietti;
    /** L'archivio degli account, usato per risolvere nome e cognome del cliente nella ricerca prenotazioni. */
    private ArchivioAccount archivioAccount;

    /**
     * Costruttore che collega il motore di ricerca agli archivi necessari.
     *
     * @param archivioSpettacoli l'archivio delle proiezioni
     * @param archivioBiglietti  l'archivio delle prenotazioni
     * @param archivioAccount    l'archivio degli account
     */
    public MotoreRicerca(ArchivioSpettacoli archivioSpettacoli, ArchivioBiglietti archivioBiglietti,
            ArchivioAccount archivioAccount) {
        this.archivioSpettacoli = archivioSpettacoli;
        this.archivioBiglietti = archivioBiglietti;
        this.archivioAccount = archivioAccount;
    }

    /**
     * Cerca le proiezioni che soddisfano i criteri forniti, combinati in AND.
     * <p>
     * Ogni parametro puo' essere null (o stringa vuota per il titolo) per non
     * essere applicato.
     *
     * @param titoloParziale titolo (anche parziale) da cercare, o null per non filtrare
     * @param genere         genere esatto da cercare, o null per non filtrare
     * @param dataInizio     inizio dell'intervallo di data/ora, o null per non filtrare
     * @param dataFine       fine dell'intervallo di data/ora, o null per non filtrare
     * @param prezzoMinimo   prezzo minimo del biglietto, o null per non filtrare
     * @param prezzoMassimo  prezzo massimo del biglietto, o null per non filtrare
     * @return un array con le proiezioni che soddisfano tutti i criteri specificati
     */
    public Spettacolo[] cercaProiezione(String titoloParziale, String genere, LocalDateTime dataInizio,
            LocalDateTime dataFine, Double prezzoMinimo, Double prezzoMassimo) {
        Spettacolo[] tutti = archivioSpettacoli.elencoTutti();
        Spettacolo[] risultatoTemp = new Spettacolo[tutti.length];
        int trovati = 0;
        for (int i = 0; i < tutti.length; i++) {
            Spettacolo spettacolo = tutti[i];
            boolean corrisponde = true;
            if (titoloParziale != null && !titoloParziale.isEmpty()) {
                String titoloMinuscolo = spettacolo.getFilm().getTitolo().toLowerCase();
                if (!titoloMinuscolo.contains(titoloParziale.toLowerCase())) {
                    corrisponde = false;
                }
            }
            if (corrisponde && genere != null && !genere.isEmpty()) {
                if (!spettacolo.getFilm().getGenere().equalsIgnoreCase(genere)) {
                    corrisponde = false;
                }
            }
            if (corrisponde && dataInizio != null) {
                if (spettacolo.getDataOra().isBefore(dataInizio)) {
                    corrisponde = false;
                }
            }
            if (corrisponde && dataFine != null) {
                if (spettacolo.getDataOra().isAfter(dataFine)) {
                    corrisponde = false;
                }
            }
            if (corrisponde && prezzoMinimo != null) {
                if (spettacolo.getPrezzoBiglietto() < prezzoMinimo) {
                    corrisponde = false;
                }
            }
            if (corrisponde && prezzoMassimo != null) {
                if (spettacolo.getPrezzoBiglietto() > prezzoMassimo) {
                    corrisponde = false;
                }
            }
            if (corrisponde) {
                risultatoTemp[trovati] = spettacolo;
                trovati++;
            }
        }
        Spettacolo[] risultato = new Spettacolo[trovati];
        for (int i = 0; i < trovati; i++) {
            risultato[i] = risultatoTemp[i];
        }
        return risultato;
    }

    /**
     * Cerca le prenotazioni che soddisfano i criteri forniti, combinati in AND.
     * <p>
     * Ogni parametro puo' essere null (o stringa vuota per i campi testuali)
     * per non essere applicato. Il nome/cognome del cliente viene risolto
     * tramite {@link ArchivioAccount#trovaPerUsername(String)}.
     *
     * @param codice                codice esatto della prenotazione, o null per non filtrare
     * @param nomeCognomeParziale   nome e/o cognome (anche parziale) del cliente, o null per non filtrare
     * @param titoloParziale        titolo (anche parziale) del film, o null per non filtrare
     * @param dataInizio            inizio dell'intervallo di data/ora, o null per non filtrare
     * @param dataFine              fine dell'intervallo di data/ora, o null per non filtrare
     * @return un array con le prenotazioni che soddisfano tutti i criteri specificati
     */
    public Biglietto[] cercaPrenotazione(String codice, String nomeCognomeParziale, String titoloParziale,
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        Biglietto[] risultatoTemp = new Biglietto[tutti.length];
        int trovati = 0;
        for (int i = 0; i < tutti.length; i++) {
            Biglietto biglietto = tutti[i];
            boolean corrisponde = true;
            if (codice != null && !codice.isEmpty()) {
                if (!biglietto.getCodice().equalsIgnoreCase(codice)) {
                    corrisponde = false;
                }
            }
            if (corrisponde && nomeCognomeParziale != null && !nomeCognomeParziale.isEmpty()) {
                Account cliente = archivioAccount.trovaPerUsername(biglietto.getUsernameCliente());
                String nomeCompleto;
                if (cliente == null) {
                    nomeCompleto = "";
                } else {
                    nomeCompleto = (cliente.getNome() + " " + cliente.getCognome()).toLowerCase();
                }
                if (!nomeCompleto.contains(nomeCognomeParziale.toLowerCase())) {
                    corrisponde = false;
                }
            }
            if (corrisponde && titoloParziale != null && !titoloParziale.isEmpty()) {
                if (!biglietto.getTitoloFilm().toLowerCase().contains(titoloParziale.toLowerCase())) {
                    corrisponde = false;
                }
            }
            if (corrisponde && dataInizio != null) {
                if (biglietto.getDataOraSpettacolo().isBefore(dataInizio)) {
                    corrisponde = false;
                }
            }
            if (corrisponde && dataFine != null) {
                if (biglietto.getDataOraSpettacolo().isAfter(dataFine)) {
                    corrisponde = false;
                }
            }
            if (corrisponde) {
                risultatoTemp[trovati] = biglietto;
                trovati++;
            }
        }
        Biglietto[] risultato = new Biglietto[trovati];
        for (int i = 0; i < trovati; i++) {
            risultato[i] = risultatoTemp[i];
        }
        return risultato;
    }
}
