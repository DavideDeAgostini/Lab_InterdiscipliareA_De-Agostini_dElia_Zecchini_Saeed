import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Proiezione{



    public String CercaProiezione(String nome, int index) {
        String fileCsv = "/Users/luigi/Downloads/proiezioni.csv";
        String termineCercato = nome;
        int colonnaDaCercare = index-1; // Indice della colonna (0 = prima colonna)

        try (BufferedReader br = new BufferedReader(new FileReader(fileCsv))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Sostituisci il separatore se usi il punto e virgola ';'
                String[] valori = linea.split(",");
                
                if (valori.length > colonnaDaCercare && valori[colonnaDaCercare].trim().equals(termineCercato)) {
                    System.out.println("Trovato: " + linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}