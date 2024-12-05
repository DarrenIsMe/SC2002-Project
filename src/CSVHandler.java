import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {
    private String filePath;

    public CSVHandler(String filePath) {
        this.filePath = filePath;
    }


    public void printCSVData(List<String[]> data) {              //THIS FUNCTION PRINTS THE ENTIRE CSV FILE DATA
        for (String[] row : data) {
            System.out.println(String.join(" | ", row));
        }
    }
    

    // Method to read CSV file                                  //THIS FUNCTION WILL SIMPLY RETURN ALL THE CSV DATA
    public List<String[]> readCSV() {
        List<String[]> data = new ArrayList<>();
        try {
            // Read entire file content
            String content = readFileContent(filePath);
            // Split content by newlines to get lines
            String[] lines = content.split("\\r?\\n");

            for (String line : lines) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Split each line by commas to get fields
                String[] fields = line.split(",");
                data.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Helper method to read the entire file content                //DONT TOUCH THIS. DON'T USE IT EITHER
    private String readFileContent(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    // Method to write data to CSV file
    public void writeCSV(List<String[]> data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String[] fields : data) {
                String line = String.join(",", fields);
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Example usage



    /* 
    public static void main(String[] args) {
        String filePath = "data.csv";
        CSVHandler csvHandler = new CSVHandler(filePath);

        // Writing data to CSV
        List<String[]> dataToWrite = new ArrayList<>();
        dataToWrite.add(new String[]{"Name", "Age", "City"});
        dataToWrite.add(new String[]{"John Doe", "30", "New York"});
        dataToWrite.add(new String[]{"Jane Smith", "25", "Los Angeles"});
        csvHandler.writeCSV(dataToWrite);

        // Reading data from CSV
        List<String[]> dataRead = csvHandler.readCSV();
        for (String[] row : dataRead) {
            System.out.println(String.join(" | ", row));
        }
            
    }
    */
}
