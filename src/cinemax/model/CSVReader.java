import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

    public static void main(String[] args) {

        String path = "/Users/luigi/Downloads/proiezioni.csv";
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            while((line = br.readLine()) != null){
            String[] values = line.split(",");
            System.out.println("Data e Ora Proiezione: " + values[0] + ", Titolo: " + values[1] + ", Genere: " + values[2] + ", Regista: " + values[3] + ", Anno: " + values[4] + ", Duarata (min): " + values[5] + ", Età Minima: " + values[6] + ", Prezzo Biglietto." + values[7]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        




    }
}