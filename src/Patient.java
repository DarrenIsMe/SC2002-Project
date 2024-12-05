import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Patient extends User{
    private DataStore dataStore;
    private final String dateOfBirth;
    private final String gender;
    private String phoneNumber;
    private String email;
    private final String bloodType;
    private MedicalRecord medicalRecord;
    private List<Appointment> appointments;
    private final AppointmentManagement appointmentManager = new AppointmentManagement("./src/Appointment_List.csv");

    public Patient(String id, String name, String dateOfBirth, String gender, String bloodType, String email, String phoneNumber){
        super(id, name, "Patient");
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
        this.email = email;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    /**
     * Displays the patient menu and handles the user's input.
     * Options include viewing medical records, updating personal information, managing appointments, viewing scheduled appointments, viewing past appointment outcome records, and logging out.
     * @param sc the scanner to read user input from
     */
    @Override
    public void displayMenu(Scanner sc) {
        int choice = 0;

        do{
            System.out.println("Patient Menu:");
            System.out.println("1. View Medical Record");
            System.out.println("2. Update Personal Information");
            System.out.println("3. Appointment Management");
            System.out.println("4. View Scheduled Appointments");
            System.out.println("5. View Past Appointment Outcome Records");
            System.out.println("6. Logout");
            System.out.print("Please select an option: ");

            try{
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                sc.nextLine();
            }

            switch (choice) {
                case 1 -> viewMedicalRecord();
                case 2 -> updatePersonalInformation(sc);
                case 3 -> appointmentManagement(sc);
                case 4 -> viewScheduledAppointments(sc);
                case 5 -> viewAppointmentOutcomeRecords(sc);
                case 6 -> {
                    super.logout();  
                    return;
                }
                
                default -> System.out.println("Invalid option, please try again.");
            }
        }while(choice != 6);
    }

    public String getDOB(){return dateOfBirth;}
    public String getGender(){return gender;}
    public String getPhoneNumber(){return phoneNumber;}
    public String getEmail(){return email;}
    public String getBloodType(){return bloodType;}

    private DataStore getDataStore() {
        if (dataStore == null) {
            dataStore = DBLocator.getDB();
        }
        return dataStore;
    }

    /**
     * Updates the patient's personal information. The user is prompted to enter a new email and phone number.
     * The new phone number is validated to ensure it is a valid Singapore phone number.
     * Once the information is updated, the changes are written to the database.
     * @param sc A Scanner object to read input from the user
     */
    private void updatePersonalInformation(Scanner sc){
        System.out.print("Enter new email address: ");
        this.email = sc.nextLine();
        while (true){
            System.out.print("Enter new Phone Number: ");
            phoneNumber = sc.nextLine();
            if(phoneNumber.matches("[89]\\d{7}")){
                break;
            }else{
                System.out.println("Invalid phone number. Please try again.");
            }
        }

        System.out.println("Personal information updated.");

        getDataStore().writePatients();
    }

    /**
     * Prints the medical record of the patient, including the patient's ID, name, date of birth, gender, contact information and blood type.
     * Also prints the medical record itself, which is retrieved from the database.
     */
    public void viewMedicalRecord() {
        Patient patient = getDataStore().getPatient(this.getId());
        System.out.println("=== Medical Record ===");
        System.out.println("Patient ID: " + this.getId());
        System.out.println("Name: " + this.name);
        System.out.println("Date of Birth: " + this.dateOfBirth); 
        System.out.println("Gender: " + this.gender);              
        System.out.println("\nContact Information:");
        System.out.println("Email: " + this.email);
        System.out.println("Phone Number: " + this.phoneNumber);
        System.out.println("Blood Type: " + this.bloodType);    
    
        patient.getMedicalRecord().printMedicalRecord();
    }
    

    private void appointmentManagement(Scanner sc){
        appointmentManager.accessManager(this.id, sc);
    }

    /**
     * Allows the patient to view their scheduled appointments. The patient can view appointments that are accepted, pending, or rejected.
     * The patient can select which type of appointment they want to view, and the appointments will be displayed. The patient can then select
     * an appointment to view the details of it. The patient can also choose to go back to the previous menu.
     * @param sc the scanner to read user input from
     */
    private void viewScheduledAppointments(Scanner sc) {
        List<Appointment> patientAppointments = getDataStore().getAppointmentPatientId(this.id);
        if (patientAppointments==null || patientAppointments.isEmpty()) {
            System.out.println("You have no scheduled appointments.");
            return;
        }

        boolean exit = false;
        int choice = 0;

        while (!exit) {
            System.out.println("1. Accepted Appointment");
            System.out.println("2. Pending Appointment (Pending request from Doctor)");
            System.out.println("3. Rejected Appointment");
            System.out.println("4. Back");
            System.out.print("Please select an option: ");

            try{
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                sc.nextLine();
            }

            switch (choice) {
                case 1 -> getScheduledAppointment(1, patientAppointments,sc);
                case 2 -> getScheduledAppointment(2, patientAppointments,sc);
                case 3 -> getScheduledAppointment(3, patientAppointments,sc);
                case 4 -> exit = true;
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    /**
     * Given a choice of 1, 2, or 3, display the respective type of appointments to the patient.
     * 1. Accepted Appointments (Confirmed status)
     * 2. Pending Appointments (Pending status)
     * 3. Rejected Appointments (Cancelled status)
     * After displaying the appointments, prompt the user to press Enter to go back to the menu.
     * @param choice the choice of which type of appointments to display
     * @param appointments the list of appointments belonging to the patient
     * @param sc the scanner to read user input from
     */
    private void getScheduledAppointment(int choice, List<Appointment> appointments, Scanner sc){
        switch (choice) {
            case 1 -> {
                System.out.println("Accepted Appointments:");
                for (Appointment appointment : appointments) {
                    if (appointment.getStatus().equals("Confirmed")) {
                        System.out.println("Appointment ID: " + appointment.getAppointmentId());
                        System.out.println("Doctor: " + appointment.getDoctorName());
                        System.out.println("Date: " + appointment.getDate());
                        System.out.println("Time Slot: " + appointment.getTimeSlot());
                        System.out.println("Status: " + appointment.getStatus());
                        System.out.println("-------------------------");
                    }
                }
                System.out.println("Press Enter to go back to menu...");
                sc.nextLine();
            }
            case 2 -> {
                System.out.println("Pending Appointments:");
                for (Appointment appointment : appointments) {
                    if (appointment.getStatus().equals("Pending")) {
                        System.out.println("Appointment ID: " + appointment.getAppointmentId());
                        System.out.println("Doctor: " + appointment.getDoctorName());
                        System.out.println("Date: " + appointment.getDate());
                        System.out.println("Time Slot: " + appointment.getTimeSlot());
                        System.out.println("Status: " + appointment.getStatus());
                        System.out.println("-------------------------");
                    }
                }
                System.out.println("Press Enter to go back to menu...");
                sc.nextLine();
            }
            case 3 -> {
                System.out.println("Rejected Appointments:");
                for (Appointment appointment : appointments) {
                    if (appointment.getStatus().equals("Cancelled")) {
                        System.out.println("Appointment ID: " + appointment.getAppointmentId());
                        System.out.println("Doctor: " + appointment.getDoctorName());
                        System.out.println("Date: " + appointment.getDate());
                        System.out.println("Time Slot: " + appointment.getTimeSlot());
                        System.out.println("Status: " + appointment.getStatus());
                        System.out.println("-------------------------");
                    }
                }
                System.out.println("Press Enter to go back to menu...");
                sc.nextLine();
            }
            default -> System.out.println("Invalid option, please try again.");
        }
    }

/**
 * Allows the patient to view past appointment outcome records. The user can choose to:
 * 1. Get a list of all completed/dispensed appointments.
 * 2. Search completed appointment outcomes by Appointment ID.
 * 3. View all completed appointment outcomes.
 * 4. Exit to the previous menu.
 * Prompts the user to enter their choice and handles the input accordingly.
 * @param sc the scanner to read user input from
 */
    private void viewAppointmentOutcomeRecords(Scanner sc){
        List<Appointment> patientAppointments = getDataStore().getAppointmentPatientId(this.id);
        if (patientAppointments==null || patientAppointments.isEmpty()) {
            System.out.println("No past appointment records available.");
            return;
        }

        int choice = 0;
        boolean exit = false;

        while (!exit) {
            System.out.println("1. Get List of all Completed/Dispensed Appointments");
            System.out.println("2. Search Completed Appointment Outcomes by AppointmentID");
            System.out.println("3. View all Completed Apppointment Outcomes");
            System.out.println("4. Back");
            System.out.print("Please select an option: ");

            try{
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer."); 
                sc.nextLine();
            }

            switch (choice) {
                case 1 -> getListofCompletedAppointments(patientAppointments);
                case 2 -> {
                    while (true) { 
                        System.out.println("Please enter your Appointment ID (Appointment you want to check):  ");
                        String identifier = sc.nextLine();

                        if (identifier.matches("A\\d+")){
                            getAppointmentOutcomeRecordbyId(identifier, patientAppointments);
                            break;
                        } else {
                            System.out.println("Invalid Appointment ID. It must start with 'A' followed by digits (e.g., A2). Please try again.");
                        }
                    }
                }
                case 3 -> viewAllAppointmentOutcomeRecords(patientAppointments);
                case 4 -> exit = true;
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    /**
     * This method prints a list of all Completed/Dispensed Appointments.
     * It goes through the list of appointments and checks if the status is "Completed" or "Dispensed", if so it prints the appointment ID, date and time.
     * @param appointments The list of appointments to check
     */
    private void getListofCompletedAppointments(List<Appointment> appointments){
        for (Appointment appointment : appointments){
            if(appointment.getStatus().equals("Completed") || appointment.getStatus().equals("Dispensed")){
                System.out.println("List of Completed Appointments' ID: ");
                System.out.println(appointment.getAppointmentId() + ". " + appointment.getDate() + " " + appointment.getTimeSlot());
            }
        }
        System.out.println("");
    }

    /**
     * This method takes an appointment ID and a list of appointments, and prints the appointment outcome record if the appointment ID exists in the list and the status is "Completed" or "Dispensed". If the appointment ID does not exist, it prints a message "No appointment found with the given Appointment ID.".
     * @param identifier The appointment ID to look for
     * @param appointments The list of appointments to search in
     */
    private void getAppointmentOutcomeRecordbyId(String identifier, List<Appointment> appointments) {
        Appointment targetAppointment = null;
        
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(identifier) && (appointment.getStatus().equals("Completed") || appointment.getStatus().equals("Dispensed"))) {
                targetAppointment = appointment;
                break;
            }
        }
        
        if (targetAppointment == null) {
            System.out.println("No appointment found with the given Appointment ID.");
            return;
        }
        
        String formattedOutcome = AppointmentOutcomeFormatter(targetAppointment);
        System.out.println(formattedOutcome);
    }
    
/**
 * Prints all appointment outcome records for appointments with status "Completed" or "Dispensed".
 * If no such appointments exist, prints a message indicating no completed or dispensed appointments were found.
 * 
 * @param appointments the list of appointments to search through
 */
    private void viewAllAppointmentOutcomeRecords(List<Appointment> appointments) {
        StringBuilder allRecords = new StringBuilder();
        boolean hasMatchingAppointments = false;

        if (appointments.isEmpty()) {
            System.out.println("No appointment found");
        }
        
        for (Appointment appointment : appointments) {
            if (appointment.getStatus().equals("Completed") || appointment.getStatus().equals("Dispensed")){
                hasMatchingAppointments = true;
                allRecords.append(AppointmentOutcomeFormatter(appointment));
                allRecords.append("-------------------------\n"); 
            }
        }
        

        if (hasMatchingAppointments) {
            System.out.println(allRecords.toString()); // Print all records
        } else {
            System.out.println("No completed or dispensed appointments found.");
        }
    }
    
    /**
     * Formats the appointment outcome records into a string
     * containing the date of appointment, time of appointment, type of service provided,
     * prescribed medications and consultation notes. If no medications or consultation notes
     * are present, appropriate messages are included in the output.
     * @param appointment the appointment from which the outcome records are to be formatted
     * @return the formatted string
     */
    private String AppointmentOutcomeFormatter(Appointment appointment){
        StringBuilder record = new StringBuilder();
        record.append("\n").append("Appointment Outcome(s):\n");
        record.append("Date of Appointment: ").append(appointment.getDate()).append("\n");
        record.append("Time of Appointment: ").append(appointment.getTimeSlot()).append("\n");
        record.append("Type of Service Provided: ").append(appointment.getServiceType()).append("\n");
        
        if (appointment.getPrescriptions().isEmpty()) {
            record.append("Prescribed Medications: None\n");
        } else {
            record.append("Prescribed Medications:\n");
            for (String medication : appointment.getPrescriptions()) {
                record.append("  - Medication Name: ").append(medication).append("\n");
            }
        }
        
        if (appointment.getConsultationNotes().isEmpty()) {
            record.append("Consultation Notes: None\n");
        } else {
            record.append("Consultation Notes:\n");
            for (String note : appointment.getConsultationNotes()) {
                record.append("  - ").append(note).append("\n");
            }
        }
        
        return record.toString();
    }
}