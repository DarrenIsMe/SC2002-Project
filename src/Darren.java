import java.util.*;


public class Darren {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        
        CSVHandler medicationIventoryCSV = new CSVHandler("main/HospitalManagementSystem/src/MedicationInventory.csv"); 
        //Initialize MedicationInventory here
        MedicationInventory inventory = new MedicationInventory() ;                                                //change max number of medications here
        
        List<String[]> medicationList = medicationIventoryCSV.readCSV();
        for(int i = 1; i < medicationList.size(); i++) { //start from 1 because 0 will be the header in the csv file
            inventory.addMedication(i, medicationList.get(i)[1], Integer.parseInt(medicationList.get(i)[2]), Float.parseFloat(medicationList.get(i)[3]), Integer.parseInt(medicationList.get(i)[4]),  Boolean.parseBoolean(medicationList.get(i)[5]), Boolean.parseBoolean(medicationList.get(i)[6]));
        }




        CSVHandler patientListCSV = new CSVHandler("main/HospitalManagementSystem/src/Patient_List.csv");
        List<String[]> patientList = patientListCSV.readCSV();



        //initialize all the staffs here         initialize all the staffs here              initialize all the staffs here          initialize all the staffs here          initialize all the staffs here     initialize all the staffs here 
        CSVHandler staffListCSV = new CSVHandler("main/HospitalManagementSystem/src/Staff_List.csv");
        List<String[]> staffList = staffListCSV.readCSV();
        List<Doctor> doctors = new ArrayList<>();
        List<Pharmacist> pharmacists = new ArrayList<>();
        List<Administrator> administrators = new ArrayList<>();
        CSVHandler appointmentlistcsv = new CSVHandler("main/HospitalManagementSystem/src/Appointment_List.csv");
        for(int i = 1; i < staffList.size(); i++) { //start from 1 because 0 will be the header in the csv file
            if(staffList.get(i)[2].equals("Doctor")) {
                //Please add constructor for doctors here
            }
            else if(staffList.get(i)[2].equals("Pharmacist")) {
                //Please add constructor for pharmacists here
            }
            else if(staffList.get(i)[2].equals("Administrator")) {
                Administrator newAdmin = new Administrator(staffList.get(i)[0], staffList.get(i)[1], staffList.get(i)[3], Integer.parseInt(staffList.get(i)[4]), inventory, medicationIventoryCSV, staffListCSV, staffList, doctors, pharmacists, appointmentlistcsv, "");
                administrators.add(newAdmin);
                //newAdmin.displayAdminMenu(); //for testing purpose only. Remove when not testing!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        }
    }
}
