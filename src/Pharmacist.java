import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Pharmacist extends User {
    private String ID;
    private String Gender;
    private int Age;
    private MedicationInventory inventory;
    private CSVHandler medicationinventorycsv;
    private CSVHandler stafflistcsv;
    private CSVHandler appointmentlistcsv;
    private List<Pharmacist> pharmacists;

    public Pharmacist(String ID, String name, String gender, String age, 
                      MedicationInventory inventory, 
                      CSVHandler medicationinventorycsv, 
                      CSVHandler stafflistcsv,
                      CSVHandler appointmentlistcsv,
                      List<Pharmacist> pharmacists) {
        super(ID, name, "Pharmacist");
        this.ID = ID;
        this.Gender = gender;
        this.Age = Integer.parseInt(age);
        this.inventory = inventory;
        this.medicationinventorycsv = medicationinventorycsv;
        this.stafflistcsv = stafflistcsv;
        this.appointmentlistcsv = appointmentlistcsv;
    }

    protected String getGender() {return Gender;}
    protected int getAge() {return Age;}

    /**
     * Displays the menu for the pharmacist and handles user input.
     * This method contains a do-while loop that continues to display the menu
     * and prompts the user for input until the user chooses to logout (option 5).
     * The menu options are as follows:
     * 1. View Appointment Outcome Record
     * 2. Update Prescription Status
     * 3. View Medication Inventory
     * 4. Submit Replenishment Request
     * 5. Logout
     * @param scanner A scanner object to read user input from the console.
     */
    @Override
    public void displayMenu(Scanner scanner) {
    
        //System.out.println("\nWelcome " + name + " (Staff ID: " + ID + ")!");
        int choice = -1; // Initialize with an invalid choice to enter the loop
        do {
            try {
                System.out.println("\n--- Pharmacist Menu ---");
                System.out.println("1) View Appointment Outcome Record");
                System.out.println("2) Update Prescription Status");
                System.out.println("3) View Medication Inventory");
                System.out.println("4) Submit Replenishment Request");
                System.out.println("5) Logout");
                System.out.print("What do you want to do? ");
    
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 5.");
                    scanner.nextLine();
                    continue;
                }
    
                switch (choice) {
                    case 1 -> viewAppointmentOutcomeRecords();
                    case 2 -> updatePrescriptionStatus();
                    case 3 -> viewMedicationInventory();
                    case 4 -> submitReplenishmentRequest();
                    case 5 -> System.out.println("Logging out...");
                    default -> System.out.println("Please input a valid option.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        } while (choice != 5);
    }
    
    /**
     * Displays all appointment outcome records from the appointment CSV file.
     * Each record includes appointment details such as the doctor, patient, status,
     * consultation notes, and prescriptions.
     */
    private void viewAppointmentOutcomeRecords() {
        System.out.println("\n--- Appointment Outcome Records ---");
        List<String[]> appointmentData = appointmentlistcsv.readCSV();
        appointmentData = appointmentData.subList(1, appointmentData.size());

        if (appointmentData == null || appointmentData.isEmpty()) {
            System.out.println("No appointment records available.");
            return;
        }

        for (String[] row : appointmentData) {
            System.out.println("Appointment ID: " + row[0]);
            System.out.println("Doctor ID: " + row[1]);
            System.out.println("Doctor Name: " + row[2]);
            System.out.println("Patient ID: " + row[3]);
            System.out.println("Date: " + row[4]);
            System.out.println("Start Time: " + row[5]);
            System.out.println("End Time: " + row[6]);
            System.out.println("Service Type: " + row[7]);
            System.out.println("Status: " + row[8]);

            if (row.length > 9 && !row[9].isEmpty()) {
                String consultationNotes = row[9].replaceAll("^\"|\"$", "");
                String[] notesArray = consultationNotes.split("\\|");

                System.out.println("Consultation Notes:");
                for (String note : notesArray) {
                    System.out.println(" - " + note.trim());
                }
            } 
            else {
                System.out.println("No consultation notes available.");
            }

            if (row.length > 10 && !row[10].isEmpty()) {
                String prescriptions = row[10].replaceAll("^\"|\"$", "");
                String[] prescriptionArray = prescriptions.split("\\|");

                System.out.println("Prescription:");
                for (String prescription : prescriptionArray) {
                    System.out.println(" - " + prescription.trim());
                }
            } else {
                System.out.println("No prescription available.");
            }

            System.out.println("--------------------------------------------------");
        }
    }

    /**
     * Updates the prescription status for selected appointments.
     * Reads appointment data from the CSV, allows the pharmacist to dispense
     * medications, and updates the appointment status and prescription details.
     */
    private void updatePrescriptionStatus() {
        System.out.println("\n--- Update Prescription Status ---");

        // Read appointment data from CSV and retain header row for later writing
        List<String[]> appointmentData = appointmentlistcsv.readCSV();
        String[] headerRow = appointmentData.get(0); // Save the header
        appointmentData = appointmentData.subList(1, appointmentData.size());

        List<String[]> pendingAppointments = new ArrayList<>();
        // Filter appointments with pending prescriptions that have a prescription listed
        for (String[] row : appointmentData) {
            if (row.length > 10 && row[8].equalsIgnoreCase("Pending Medication") && !row[10].isEmpty()) {
                pendingAppointments.add(row);
            }
        }

        if (pendingAppointments.isEmpty()) {
            System.out.println("No pending prescriptions available to update.");
            return;
        }


        System.out.println("Pending Prescriptions:");
        for (int i = 0; i < pendingAppointments.size(); i++) {
            String[] row = pendingAppointments.get(i);
            System.out.println((i + 1) + ") Appointment ID: " + row[0] + ", Patient ID: " + row[3] + ", Doctor ID: " + row[1] + ", Prescriptions: " + row[10]);
        }

        Scanner scanner = new Scanner(System.in);
        int appointmentIndex = 0;
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.print("Select an appointment to update (enter number): ");
                appointmentIndex = scanner.nextInt() -1;
                scanner.nextLine();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        if (appointmentIndex < 0 || appointmentIndex >= pendingAppointments.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        // Retrieve selected appointment
        String[] selectedAppointment = pendingAppointments.get(appointmentIndex);
        System.out.println("Selected Appointment ID: " + selectedAppointment[0]);

        // Lists to store medications and quantities
        List<Medication> medicationsToDispense = new ArrayList<>();
        List<Integer> quantitiesToDispense = new ArrayList<>();

        inventory.viewAllMedication();
        while (true) {
            int medID = -1;
            int quantity = -1;
        
            while (true) {
                try {
                    System.out.print("Enter the medication ID to dispense (or 0 to finish): ");
                    medID = scanner.nextInt();
                    scanner.nextLine();
        
                    if (medID >= 0) {
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter a positive number or 0 to finish.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine();
                }
            }
        
            if (medID == 0) break; // Exit the loop if the user enters 0
        
            while (true) {
                try {
                    System.out.print("Enter the quantity to dispense for Medication ID " + medID + ": ");
                    quantity = scanner.nextInt();
                    scanner.nextLine();
        
                    if (quantity > 0) {
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter a positive number.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine();
                }
            }
        
            // Find medication by ID and add it to the list
            Medication med = findMedicationByID(medID);
            if (med != null) {
                medicationsToDispense.add(med);
                quantitiesToDispense.add(quantity);
            } else {
                System.out.println("Medication ID not found.");
            }
        }

        boolean confirmed = false;
        while (!confirmed) {
            System.out.println("\n--- Summary of Medications to Dispense ---");
            for (int i = 0; i < medicationsToDispense.size(); i++) {
                Medication med = medicationsToDispense.get(i);
                int quantity = quantitiesToDispense.get(i);
                System.out.println((i + 1) + ") Medication ID: " + med.getMedicationID() + ", Name: " + med.getMedicationName() + ", Quantity: " + quantity);
            }
        
            String response = "";
            while (true) {
                System.out.print("Do you confirm this prescription? (Y/N): ");
                response = scanner.nextLine().trim();
            
                if (response.equalsIgnoreCase("Y") || response.equalsIgnoreCase("N")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'Y' for Yes or 'N' for No.");
                }
            }
        
            if (response.equalsIgnoreCase("Y")) {
                confirmed = true;
            } else {
                int itemToEdit = -1;
        
                while (true) {
                    try {
                        System.out.print("Enter the item number you want to change (or 0 to add a new medication): ");
                        itemToEdit = scanner.nextInt() - 1;
                        scanner.nextLine();
        
                        if (itemToEdit >= -1 && itemToEdit < medicationsToDispense.size()) {
                            break;
                        } else {
                            System.out.println("Invalid item number. Please enter a valid number.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                        scanner.nextLine();
                    }
                }
        
                if (itemToEdit == -1) {
                    int medID = -1, quantity = -1;
        
                    while (true) {
                        try {
                            System.out.print("Enter new medication ID: ");
                            medID = scanner.nextInt();
                            scanner.nextLine();
        
                            if (medID > 0) {
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter a positive medication ID.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.nextLine();
                        }
                    }
        
                    while (true) {
                        try {
                            System.out.print("Enter quantity for Medication ID " + medID + ": ");
                            quantity = scanner.nextInt();
                            scanner.nextLine();
        
                            if (quantity > 0) {
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter a positive number for quantity.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.nextLine();
                        }
                    }
        
                    Medication newMed = findMedicationByID(medID);
                    if (newMed != null) {
                        medicationsToDispense.add(newMed);
                        quantitiesToDispense.add(quantity);
                    } else {
                        System.out.println("Medication ID not found.");
                    }
                } else {
                    int medID = -1, quantity = -1;
        
                    while (true) {
                        try {
                            System.out.print("Enter new medication ID to replace: ");
                            medID = scanner.nextInt();
                            scanner.nextLine();
        
                            if (medID > 0) {
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter a positive medication ID.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.nextLine();
                        }
                    }
        
                    while (true) {
                        try {
                            System.out.print("Enter new quantity for Medication ID " + medID + ": ");
                            quantity = scanner.nextInt();
                            scanner.nextLine();
        
                            if (quantity > 0) {
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter a positive number for quantity.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.nextLine();
                        }
                    }
        
                    Medication newMed = findMedicationByID(medID);
                    if (newMed != null) {
                        medicationsToDispense.set(itemToEdit, newMed);
                        quantitiesToDispense.set(itemToEdit, quantity);
                    } else {
                        System.out.println("Medication ID not found.");
                    }
                }
            }
        }        

            // Dispense medications
            StringBuilder dispensedMedications = new StringBuilder();

            for (int i = 0; i < medicationsToDispense.size(); i++) {
                Medication med = medicationsToDispense.get(i);
                int quantity = quantitiesToDispense.get(i);

                if (med.getStockLevel() >= quantity) {
                    med.setStockLevel(med.getStockLevel() - quantity);
                    System.out.println("Dispensed " + quantity + " of " + med.getMedicationName() + ". Remaining stock: " + med.getStockLevel());

                    dispensedMedications.append(med.getMedicationName());

                    if (i < medicationsToDispense.size() - 1) {
                        dispensedMedications.append("|");
                    }
                } else {
                    System.out.println("Not enough stock for " + med.getMedicationName() + ". Dispense failed.");
                }
            }

            // Update the selected appointment's "Prescriptions" and "Status" fields
            if (dispensedMedications.length() > 0) {
                selectedAppointment[8] = "Dispensed";
                selectedAppointment[10] = dispensedMedications.toString();

                System.out.println("Prescription updated for Appointment ID " + selectedAppointment[0] + ": " + selectedAppointment[10]);

                // Update the appointmentData list
                boolean updated = false;
                for (int i = 0; i < appointmentData.size(); i++) {
                    if (appointmentData.get(i)[0].equals(selectedAppointment[0])) {
                        appointmentData.set(i, selectedAppointment);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    System.out.println("Error: Could not find matching appointment ID to update.");
                    return;
                }

                // Write back the updated appointment data with header
                List<String[]> dataWithHeader = new ArrayList<>();
                dataWithHeader.add(headerRow); // Add header row
                dataWithHeader.addAll(appointmentData); // Add updated rows
                updateAppointmentStatusInCSV(dataWithHeader);

                System.out.println("Prescription status updated to 'Dispensed' and medications logged for Appointment ID: " + selectedAppointment[0]);

                updateInventoryInCSV();
            } else {
                System.out.println("No medications were dispensed. CSV not updated.");
            }

    }

    /**
     * Updates the inventory CSV file with the latest medication stock levels.
     */
    private void updateInventoryInCSV() {
        List<String[]> medicationData = medicationinventorycsv.readCSV();
        for (Medication med : inventory.getMedications()) {
            for (String[] row : medicationData) {
                if (row[0].equals(String.valueOf(med.getMedicationID()))) {
                    row[2] = String.valueOf(med.getStockLevel());
                }
            }
        }
        medicationinventorycsv.writeCSV(medicationData);
        DBLocator.getDB().readMedicationCashe();
    }

    /**
     * Finds a medication in the inventory by its ID.
     *
     * @param medID The ID of the medication to find.
     * @return The Medication object if found, or null if not found.
     */
    private Medication findMedicationByID(int medID) {
        for (Medication med : inventory.getMedications()) {
            if (med.getMedicationID() == medID) {
                return med;
            }
        }
        return null;
    }

    /**
     * Writes updated appointment data to the CSV file.
     *
     * @param updatedData A list of updated appointment records.
     */
    private void updateAppointmentStatusInCSV(List<String[]> updatedData) {
        appointmentlistcsv.writeCSV(updatedData);
        DBLocator.getDB().readAppointmentCashe();
    }

    /**
     * Displays the current medication inventory.
     * Lists all medications available along with their details.
     */
    private void viewMedicationInventory() {
        System.out.println("\n--- Medication Inventory ---");
        inventory.viewAllMedication();
    }

    /**
     * Allows the pharmacist to submit or cancel a replenishment request for a medication.
     * The replenishment status is updated in the medication inventory CSV.
     */
    private void submitReplenishmentRequest() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- Submit Replenishment Request ---");
        inventory.viewAllMedication();

        int medID = -1;
        while (true) {
            try {
                System.out.print("Enter the ID of the medication to request replenishment (or 0 to exit): ");
                medID = scanner.nextInt();
                scanner.nextLine();

                if (medID == 0) {
                    System.out.println("Exiting replenishment request...");
                    return;
                }

                if (medID >= 1 && medID <= inventory.getNumberOfMedications()) {
                    break;
                } else {
                    System.out.println("Error: Invalid Medication ID. Please enter a valid ID.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        Medication med = inventory.getMedications()[medID - 1];
        if (med.getWarningQuantity() == -1) {
            System.out.println("Error: Medication not found.");
            return;
        }

        if (med.getReplenishmentRequest()) {
            System.out.println("Replenishment request is already pending for this medication.");
            String response = getValidYesNoResponse(scanner, "Would you like to cancel it? (Y/N): ");

            if (response.equalsIgnoreCase("Y")) {
                med.setReplenishmentRequest(false);
                updateReplenishmentRequestInCSV(medID, false);
                System.out.println("Replenishment request canceled for Medication ID: " + medID);
            } else {
                System.out.println("Replenishment request remains pending.");
            }
        } else {
            System.out.println("No current replenishment request for this medication.");
            String response = getValidYesNoResponse(scanner, "Would you like to submit a replenishment request? (Y/N): ");

            if (response.equalsIgnoreCase("Y")) {
                med.setReplenishmentRequest(true);
                updateReplenishmentRequestInCSV(medID, true);
                System.out.println("Replenishment request submitted successfully for Medication ID: " + medID);
            } else {
                System.out.println("No replenishment request submitted.");
            }
        }
    }

    /**
     * Prompts the user with a yes/no question and returns the response.
     *
     * @param scanner A Scanner object to read user input.
     * @param prompt  The question to display to the user.
     * @return The user's response, either "Y" or "N".
     */
    private String getValidYesNoResponse(Scanner scanner, String prompt) {
        String response = "";
        while (true) {
            System.out.print(prompt);
            response = scanner.nextLine().trim();

            if (response.equalsIgnoreCase("Y") || response.equalsIgnoreCase("N")) {
                return response;
            } else {
                System.out.println("Invalid input. Please enter 'Y' for Yes or 'N' for No.");
            }
        }
    }

    /**
     * Updates the replenishment request status for a specific medication in the inventory CSV file.
     * 
     * The method reads the medication inventory from the CSV, identifies the medication by its ID,
     * and updates the "ReplenishmentRequest" column with the provided status (TRUE or FALSE).
     * The updated inventory data is then written back to the CSV.
     *
     * @param medID        The ID of the medication to update.
     * @param requestStatus The new replenishment request status (true for request submitted, false for canceled).
     */
    private void updateReplenishmentRequestInCSV(int medID, boolean requestStatus) {
        List<String[]> medicationData = medicationinventorycsv.readCSV();

        // Find the relevant row based on medID and update the ReplenishmentRequest column
        for (String[] row : medicationData) {
            if (row[0].equals(String.valueOf(medID))) {
                row[5] = requestStatus ? "TRUE" : "FALSE"; // Update the ReplenishmentRequest column
                break;
            }
        }
        medicationinventorycsv.writeCSV(medicationData);
        DBLocator.getDB().writeMedication();
    }


}