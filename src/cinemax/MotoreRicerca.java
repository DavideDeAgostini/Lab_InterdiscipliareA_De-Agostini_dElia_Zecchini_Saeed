package cinemax;

import java.time.LocalDateTime;

/**
 * Contiene la logica di ricerca sulle proiezioni e sulle prenotazioni.
 * Tenuta separata dai Gestori perche' i criteri (parziali, intervalli,
 * combinazioni) sono complessi e riusati in piu' punti dell'applicazione.
 *
 * @author Davide De Agostini 766294 (CO)
 * @author Luigi d'Elia 765969 (CO)
 * @author Ahsan Saeed 767241 (CO)
 * @author Martina Zecchini 765842 (CO)
 */
public class MotoreRicerca {
    
    /** Archivio per accedere in sola lettura ai dati delle proiezioni in programmazione. */
    private ArchivioSpettacoli archivioSpettacoli;
    
    /** Archivio per accedere in sola lettura allo storico e alle prenotazioni attive. */
    private ArchivioBiglietti archivioBiglietti;
    
    /** Archivio per accedere in sola lettura ai dati degli utenti (es. per ricerca per nome). */
    private ArchivioAccount archivioAccount;

    /**
     * Costruttore che inietta le dipendenze degli archivi necessari per 
     * effettuare le ricerche incrociate sui dati in memoria.
     */
    public MotoreRicerca(ArchivioSpettacoli archivioSpettacoli, ArchivioBiglietti archivioBiglietti,
            ArchivioAccount archivioAccount) {
        this.archivioSpettacoli = archivioSpettacoli;
        this.archivioBiglietti = archivioBiglietti;
        this.archivioAccount = archivioAccount;
    }

    /**
     * Cerca le proiezioni che soddisfano i criteri forniti, combinati in AND.
     * Ogni parametro puo' essere null (o stringa vuota) per non essere applicato.
     * La ricerca per titolo e' parziale (sottostringa) e ignora le maiuscole/minuscole.
     */
    public Spettacolo[] cercaProiezione(String titoloParziale, String genere, LocalDateTime dataInizio,
            LocalDateTime dataFine, Double prezzoMinimo, Double prezzoMassimo) {
        Spettacolo[] tutti = archivioSpettacoli.elencoTutti();
        Spettacolo[] risultatoTemp = new Spettacolo[tutti.length];
        int trovati = 0;
        
        for (int i = 0; i < tutti.length; i++) {
            Spettacolo spettacolo = tutti[i];
            boolean corrisponde = true;
            
            // Filtro per titolo (ricerca parziale case-insensitive)
            if (titoloParziale != null && !titoloParziale.isEmpty()) {
                String titoloMinuscolo = spettacolo.getFilm().getTitolo().toLowerCase();
                if (!titoloMinuscolo.contains(titoloParziale.toLowerCase())) {
                    corrisponde = false;
                }
            }
            
            // Filtro per genere esatto (case-insensitive)
            if (corrisponde && genere != null && !genere.isEmpty()) {
                if (!spettacolo.getFilm().getGenere().equalsIgnoreCase(genere)) {
                    corrisponde = false;
                }
            }
            
            // Filtri temporali: da una certa data/ora in poi
            if (corrisponde && dataInizio != null) {
                if (spettacolo.getDataOra().isBefore(dataInizio)) {
                    corrisponde = false;
                }
            }
            
            // Filtri temporali: fino a una certa data/ora
            if (corrisponde && dataFine != null) {
                if (spettacolo.getDataOra().isAfter(dataFine)) {
                    corrisponde = false;
                }
            }
            
            // Filtri di prezzo minimo e massimo
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
            
            // Se lo spettacolo ha superato tutti i filtri attivi, viene aggiunto
            if (corrisponde) {
                risultatoTemp[trovati] = spettacolo;
                trovati++;
            }
        }
        
        // Travaso in un array della dimensione esatta (senza celle null residue)
        Spettacolo[] risultato = new Spettacolo[trovati];
        for (int i = 0; i < trovati; i++) {
            risultato[i] = risultatoTemp[i];
        }
        return risultato;
    }

    /**
     * Cerca le prenotazioni che soddisfano i criteri forniti, combinati in AND.
     * Ogni parametro puo' essere null (o stringa vuota) per non essere applicato.
     * La ricerca per nome/cognome interroga dinamicamente l'archivio account.
     */
    public Biglietto[] cercaPrenotazione(String codice, String nomeCognomeParziale, String titoloParziale,
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        Biglietto[] tutti = archivioBiglietti.elencoTutti();
        Biglietto[] risultatoTemp = new Biglietto[tutti.length];
        int trovati = 0;
        
        for (int i = 0; i < tutti.length; i++) {
            Biglietto biglietto = tutti[i];
            boolean corrisponde = true;
            
            // Filtro per codice esatto del biglietto
            if (codice != null && !codice.isEmpty()) {
                if (!biglietto.getCodice().equalsIgnoreCase(codice)) {
                    corrisponde = false;
                }
            }
            
            // Filtro combinato su nome e cognome dell'utente associato
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
            
            // Filtro parziale per titolo del film
            if (corrisponde && titoloParziale != null && !titoloParziale.isEmpty()) {
                if (!biglietto.getTitoloFilm().toLowerCase().contains(titoloParziale.toLowerCase())) {
                    corrisponde = false;
                }
            }
            
            // Filtri sul periodo in cui si tiene lo spettacolo
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
            
            // Se la prenotazione ha superato i filtri, la conserviamo
            if (corrisponde) {
                risultatoTemp[trovati] = biglietto;
                trovati++;
            }
        }
        
        // Travaso in un array tagliato a misura
        Biglietto[] risultato = new Biglietto[trovati];
        for (int i = 0; i < trovati; i++) {
            risultato[i] = risultatoTemp[i];
        }
        return risultato;
    }
}