import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SoonLiTesting { //THIS CLASS IS FOR MY PERSONAL TESTING ONLY, NO ONE SHOULD USE IT BUT ME , GRACIAS, MERCI BEAUCOUP     An idiot admires complexity, a genius admires simplicity -Terry Davis
    
    //private static DataStore dataStore = DBLocator.getDB();
    

    public static void main(String[] args) {
        
        


        
        


        System.out.println("Working Directory = " + System.getProperty("user.dir"));   //This line is used to check where the working directory is, so you can properly link your csv file based on the working directory
        CSVHandler medicationIventoryCSV = new CSVHandler("./src/MedicationInventory.csv");
        MedicationInventory inventory = new MedicationInventory() ;                                                //change max number of medications here
        
        List<String[]> medicationList = medicationIventoryCSV.readCSV();
        for(int i = 1; i < medicationList.size(); i++) { //start from 1 because 0 will be the header in the csv file
            inventory.addMedication(i, medicationList.get(i)[1], Integer.parseInt(medicationList.get(i)[2]), Float.parseFloat(medicationList.get(i)[3]), Integer.parseInt(medicationList.get(i)[4]),  Boolean.parseBoolean(medicationList.get(i)[5]), Boolean.parseBoolean(medicationList.get(i)[6]));        }

        CSVHandler staffListCSV = new CSVHandler("./src/Staff_List.csv");
        CSVHandler appointmentListCSV = new CSVHandler("./src/Appointment_List.csv");
        List<String[]> staffList = staffListCSV.readCSV();
        List<Doctor> doctors = new ArrayList<>();
        List<Pharmacist> pharmacists = new ArrayList<>();
        List<Administrator> administrators = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        for(int i = 1; i < staffList.size(); i++) { //start from 1 because 0 will be the header in the csv file
            if(staffList.get(i)[2].equals("Doctor")) {
                //Please add constructor for doctors here
            }
            else if(staffList.get(i)[2].equals("Pharmacist")) {
                //Please add constructor for pharmacists here
            }
            else if(staffList.get(i)[2].equals("Administrator")) {
                Administrator newAdmin = new Administrator(staffList.get(i)[0], staffList.get(i)[1], staffList.get(i)[3], Integer.parseInt(staffList.get(i)[4]), inventory, medicationIventoryCSV, staffListCSV, staffList, doctors, pharmacists, appointmentListCSV, "");
                administrators.add(newAdmin);
                newAdmin.displayMenu(sc); //for testing purpose only. Remove when not testing!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        }
    


    }
}
