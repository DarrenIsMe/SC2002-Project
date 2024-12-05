import java.util.*;
import java.util.regex.Pattern;


public class App {
    //private static DataStore dataStore = DBLocator.getDB();
    public static int AppointmentDuration = 15;
    private static User loggedInUser = null;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        //Initialize MedicationInventory here
        CSVHandler medicationInventoryCSV = new CSVHandler("./src/MedicationInventory.csv");
        MedicationInventory inventory = new MedicationInventory() ;                                                //change max number of medications here
        
        List<String[]> medicationList = medicationInventoryCSV.readCSV();
        for(int i = 1; i < medicationList.size(); i++) { //start from 1 because 0 will be the header in the csv file
            inventory.addMedication(i, medicationList.get(i)[1], Integer.parseInt(medicationList.get(i)[2]), Float.parseFloat(medicationList.get(i)[3]), Integer.parseInt(medicationList.get(i)[4]),  Boolean.parseBoolean(medicationList.get(i)[5]), Boolean.parseBoolean(medicationList.get(i)[6]));
        }




        CSVHandler patientListCSV = new CSVHandler("./src/Patient_List.csv");
        List<String[]> patientList = patientListCSV.readCSV();

        CSVHandler appointmentListCSV = new CSVHandler("./src/Appointment_List.csv");



        //initialize all the staffs here         initialize all the staffs here              initialize all the staffs here          initialize all the staffs here          initialize all the staffs here     initialize all the staffs here 
        CSVHandler staffListCSV = new CSVHandler("./src/Staff_List.csv");
        List<String[]> staffList = staffListCSV.readCSV();
        List<Doctor> doctors = new ArrayList<>();
        List<Pharmacist> pharmacists = new ArrayList<>();
        List<Administrator> administrators = new ArrayList<>();
        /* 
        for(int i = 1; i < staffList.size(); i++) { //start from 1 because 0 will be the header in the csv file
            if(staffList.get(i)[2].equals("Doctor")) {
                //Please add constructor for doctors here

            }
            else if(staffList.get(i)[2].equals("Pharmacist")) {
                //Please add constructor for pharmacists here
                Pharmacist pharmacist = new Pharmacist(
                    staffList.get(i)[0], // ID
                    staffList.get(i)[1], // Name
                    staffList.get(i)[3], // Gender
                    staffList.get(i)[4], // Age
                    inventory, 
                    medicationInventoryCSV, 
                    staffListCSV, 
                    appointmentListCSV, 
                    pharmacists
                );

                pharmacists.add(pharmacist);
                pharmacist.pharmacistMenu();
            }
            else if(staffList.get(i)[2].equals("Administrator")) {
                Administrator newAdmin = new Administrator(staffList.get(i)[0], staffList.get(i)[1], staffList.get(i)[3], Integer.parseInt(staffList.get(i)[4]), inventory, medicationInventoryCSV, staffListCSV, staffList, doctors, pharmacists, appointmentListCSV);
                administrators.add(newAdmin);
                newAdmin.displayAdminMenu(); //for testing purpose only. Remove when not testing!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        }
        */

        //ADD LOGIN FUNCTION HERE PLEASE !!!!!!!!!!!
        while (loggedInUser == null) {
            System.out.println("\n=== Welcome to HMS ===");
            System.out.println("1. Register a New Patient Account");
            System.out.println("2. Login to an Existing Account");
            System.out.print("Enter your choice: ");
            int choice = 0;
            try {choice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine(); continue;}

            switch (choice) {
                case 1:
                    registerNewPatient(sc, patientList, patientListCSV);
                    break;
                case 2:
                    loginExistingAccount(sc, patientList, staffList, inventory, doctors, pharmacists, administrators, appointmentListCSV, staffListCSV, medicationInventoryCSV, patientListCSV);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void registerNewPatient(Scanner sc, List<String[]> patientList, CSVHandler patientListCSV) throws Exception {
        sc.nextLine();
        String dob, gender, bloodType, phoneNumber;
        String[] validBloodTypes = {"A", "B", "AB", "O", "A-", "B-", "AB-", "O-", "A+", "B+", "AB+", "O+"};

        System.out.println("\n=== Patient Registration ===");
        System.out.print("Enter Full Name: ");
        String name = sc.nextLine();

        // Validating date of birth format
        do {
            System.out.print("Enter Date of Birth (DD/MM/YYYY): ");
            dob = sc.nextLine(); 
            if (!isDateValid(dob)) { System.out.println("Invalid format. Please enter the date in DD/MM/YYYY format."); }
        } while (!isDateValid(dob));
        
        // Validating gender input & standardizing
        do {
            System.out.print("Enter Gender: ");
            gender = sc.nextLine().trim().toLowerCase();

            // Check if gender is valid
            if (gender.equals("f") || gender.equals("female")) {
                gender = "Female"; // Standardize gender
            } else if (gender.equals("m") || gender.equals("male")) {
                gender = "Male"; // Standardize gender
            } else {
                System.out.println("Invalid input. Please enter a valid gender");
                continue; // Go to the next iteration to ask for input again
            }
        } while (!(gender.equals("Female") || gender.equals("Male")));
        
        // Validating blood type
        do {
            System.out.print("Enter Blood Type: ");
            bloodType = sc.nextLine().trim().toUpperCase();
            if (!Arrays.asList(validBloodTypes).contains(bloodType)) { System.out.println("Invalid blood type. Please enter a valid blood type.");}
        } while (!Arrays.asList(validBloodTypes).contains(bloodType));
        
        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        // Validating phone number
        do {
            System.out.print("Enter Phone Number: ");
            phoneNumber = sc.nextLine();
            if (!phoneNumber.matches("\\d{8}")) { System.out.println("Invalid phone number. Please input a valid phone number");}
        } while(!phoneNumber.matches("\\d{8}"));
        

        // Check if the user already exists
        for (String[] row : patientList) {
            if (row[1].equalsIgnoreCase(name) && row[2].equals(dob) && row[3].equalsIgnoreCase(gender) && row[6].equals(phoneNumber)) {
                System.out.println("Patient account already exists. Registration rejected.");
                return;
            }
        }

        // User account does not exist in system 
        // Generate new Patient ID
        String newPatientId = generateNewPatientId(patientList);

        // Add new patient data
        String[] newPatient = {
            newPatientId,
            name,
            dob,
            gender,
            bloodType,
            email,
            phoneNumber,
            "password" // Default password
        };
        patientList.add(newPatient);

        // Update the CSV
        patientListCSV.writeCSV(patientList);
        DBLocator.getDB().readPatientCashe();

        System.out.println("Registration successful! Your Patient ID is: " + newPatientId);
        System.out.println("Please login using your Patient ID and the default password 'password'.");
    }

    private static String generateNewPatientId(List<String[]> patientList) {
        int lastId = 0; // Default starting point if no patients exist
        for (String[] row : patientList) {
            if (row[0].startsWith("P1")) {
                try {
                    int id = Integer.parseInt(row[0].substring(2)); // Extract numeric part after "P1"
                    lastId = Math.max(lastId, id); // Track the highest ID found
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid ID: " + row[0]); // Handle unexpected formatting
                }
            }
        }
        return String.format("P1%03d", lastId + 1); // Generate the new ID with leading zeros
    }
    

    private static void loginExistingAccount(Scanner sc, List<String[]> patientList, List<String[]> staffList, 
                                            MedicationInventory inventory, 
                                            List<Doctor> doctors, List<Pharmacist> pharmacists, List<Administrator> administrators,
                                            CSVHandler appointmentListCSV, CSVHandler staffListCSV, CSVHandler medicationInventoryCSV, CSVHandler patientListCSV) throws Exception {
        System.out.println("\n=== Login ===");
        System.out.println("Please select your user type:");
        System.out.println("1. Patient\n2. Doctor\n3. Pharmacist\n4. Administrator");
        System.out.print("Enter your choice: ");
        int userTypeChoice = 0;
        try {userTypeChoice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();} 
        String userType = getUserType(userTypeChoice);

        if (userType != null) {
            handleLogin(userType, sc, patientList, staffList, inventory, doctors, pharmacists, administrators, appointmentListCSV, staffListCSV, medicationInventoryCSV, patientListCSV);
            while (loggedInUser != null) {
                loggedInUser.displayMenu(sc);
            }
        } else {
            System.out.println("Invalid choice. Please select again.");
        }
    }

    private static void handleLogin(String userType, Scanner sc, List<String[]> patientList, List<String[]> staffList, 
                                    MedicationInventory inventory, 
                                    List<Doctor> doctors, List<Pharmacist> pharmacists, List<Administrator> administrators,
                                    CSVHandler appointmentListCSV, CSVHandler staffListCSV, CSVHandler medicationInventoryCSV, CSVHandler patientListCSV) throws Exception {
        sc.nextLine();
        DataStore dataStore = DBLocator.getDB();
        String id = "";
        String currentid;
        do { 
            System.out.print("Enter User ID: ");
            id = sc.nextLine(); 
            if (!isValidUserID(id, userType)) {
                System.out.println("Invalid User ID. Please try again.");
            }
        } while (!isValidUserID(id, userType));
        System.out.print("Enter Password: ");
        String password = sc.nextLine(); 

        if ("patient".equals(userType)) {
            for (String[] patient : patientList) {
                if (patient[0].equals(id) && patient[7].equals(password)) {
                    
                    System.out.println("Login successful! Welcome, " + patient[1] + ".");
                    Patient newPatient = new Patient(id, patient[1], patient[2], patient[3], patient[4], patient[5], patient[6]);
                    loggedInUser = newPatient;

                    // Check for first-time login
                    if (loggedInUser != null && patient[7].equals("password")) {
                        System.out.println("It's your first time logging in. Please change your password.");
                        password = changePasswordPrompt(loggedInUser, sc);
                        newPatient.setPassword(password);
                    }
                    
                    String[] newPatientRecord = new String[] {
                        newPatient.getId(),
                        newPatient.getName(),
                        newPatient.getDOB(),   
                        newPatient.getGender(),
                        newPatient.getBloodType(),
                        newPatient.getEmail(),
                        newPatient.getPhoneNumber(),
                        password,
                    };
                    // Replace the current patient record in patientList
                    for (int i = 0; i < patientList.size(); i++) {
                        if (patientList.get(i)[0].equals(id)) {
                            patientList.set(i, newPatientRecord); // Replace the record
                            break;
                        }
                    }
                    
                    // Write the updated list to CSV
                    patientListCSV.writeCSV(patientList);
                    DBLocator.getDB().readPatientCashe();

                    newPatient.displayMenu(sc);
                    loggedInUser = null; // logs out
                    return;
                } 
            } System.out.println("Incorrect ID or password entered.");
        } else if (userType.equalsIgnoreCase("doctor") || userType.equalsIgnoreCase("pharmacist") || userType.equalsIgnoreCase("admin")){
            for (String[] staff : staffList) {
                if (staff[0].equals(id) && staff[5].equals(password)) {
                    currentid = id;
                    System.out.println("Login successful! Welcome, " + staff[1] + ".");
                    switch (userType) {
                        case "doctor": 
                            Doctor newDoctor = dataStore.getDoctor(id);
                            loggedInUser = newDoctor;
                            doctors.add(newDoctor);
                            newDoctor.displayMenu(sc);
                            loggedInUser = null; // logs out
                            break;

                        case "pharmacist":
                            Pharmacist newPharmacist = new Pharmacist(id, staff[1], staff[3], staff[4], inventory, medicationInventoryCSV, staffListCSV, appointmentListCSV, pharmacists);
                            loggedInUser = newPharmacist;
                            pharmacists.add(newPharmacist);
                            newPharmacist.displayMenu(sc);
                            loggedInUser = null; // logs out
                            break;

                        case "admin":
                            Administrator newAdmin = new Administrator(id, staff[1], staff[3], Integer.parseInt(staff[4]), inventory, medicationInventoryCSV, staffListCSV, staffList, doctors, pharmacists, appointmentListCSV, currentid);
                            loggedInUser = newAdmin;
                            administrators.add(newAdmin);
                            newAdmin.displayMenu(sc);
                            loggedInUser = null; // logs out
                            break;

                        default: System.out.println("Invalid user type");
                    };
                    return;
                }
            }
        } else System.out.println("Invalid credentials. Please try again.");
    }
    

    private static String getUserType(int choice) {
        switch (choice) {
            case 1: return "patient";
            case 2: return "doctor";
            case 3: return "pharmacist";
            case 4: return "admin";
            default: return null;
        }
    }

    // Validation for User ID
    private static boolean isValidUserID(String id, String userType) {
        switch (userType) {
            case "patient": return Pattern.matches("P1\\d{3}", id);
            case "doctor": return Pattern.matches("D\\d{3}", id);
            case "pharmacist": return Pattern.matches("P\\d{3}", id); 
            case "admin": return Pattern.matches("A\\d{3}", id); 
            default: return false;
        }
    }


    // Prompt the user to change their password
    private static String changePasswordPrompt(User user, Scanner sc) {
        while (true) {
            System.out.print("Enter a new password: ");
            String newPassword = sc.nextLine();
            if (user.changePassword(newPassword)) { // Ensure `changePassword` validates password internally
                return newPassword;
            }
        }
    }

    private static boolean isDateValid(String dob) {
        // Validate if the date is in the format DD/MM/YYYY
        if (dob.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] parts = dob.split("/");  // Split the date by "/"
            
            // Parse day, month, and year
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            
            // Validate the month
            if (month < 1 || month > 12) {
                return false;
            }
            
            // Get the maximum number of days in the month
            int maxDay = getMaxDaysInMonth(month, year);
            
            // Validate the day
            if (day < 1 || day > maxDay) {
                return false;
            }
            
            // If everything is valid, break the loop
            return true;
        } else return false;
    }

    // Helper function to get the max days in a given month for a given year
    private static int getMaxDaysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                // Check for leap year
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29;  // Leap year
                } else {
                    return 28;  // Non-leap year
                }
            default:
                return 0;  // Invalid month (should not happen due to earlier check)
        }
    }
}

