import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvFileReader {

    private final String filePath;
    private final char separator;

    public CsvFileReader(String filePath) {
        this(filePath, ',');
    }

    public CsvFileReader(String filePath, char separator) {
        this.filePath = filePath;
        this.separator = separator;
    }

    public List<String[]> readAll() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(parseLine(line, separator));
            }
            return rows;
        }
    }

    public List<Map<String, String>> readAllAsMap() throws IOException {
        List<String[]> rows = readAll();
        if (rows.isEmpty()) {
            return new ArrayList<>();
        }

        String[] header = rows.get(0);
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            list.add(asMap(header, rows.get(i)));
        }
        return list;
    }

    private Map<String, String> asMap(String[] header, String[] row) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < header.length; i++) {
            map.put(header[i], i < row.length ? row[i] : "");
        }
        return map;
    }

    public static String[] parseLine(String line, char separator) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (current == separator && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }

        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "/Users/luigi/Documents/GitHub/Lab_InterdiscipliareA_De-Agostini_dElia_Zecchini_Saeed/data/proiezioni.csv";
        CsvFileReader csvReader = new CsvFileReader(path);

        try {
            List<String[]> rows = csvReader.readAll();
            System.out.println("Rows read: " + rows.size());
            for (String[] row : rows) {
                System.out.println(Arrays.toString(row));
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file CSV: " + e.getMessage());
        }
    }
}