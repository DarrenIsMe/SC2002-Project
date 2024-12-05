import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AppointmentManagement {
    private static DataStore dataStore = DBLocator.getDB();

    public AppointmentManagement(String filePath) {
        
    }

    // Main access point for different user roles
    public void accessManager(String userId, Scanner sc) {
        if (userId.startsWith("P")) {
            handlePatientActions(userId, sc);
        } else if (userId.startsWith("D")) {
            handleDoctorActions(userId);
        } else {
            System.out.println("Invalid user ID.");
        }
    }

    // Actions for Patients
    private void handlePatientActions(String patientId, Scanner sc) {
        System.out.println("\nPatient Portal: ");
        System.out.println("1. Schedule Appointment");
        System.out.println("2. Reschedule Appointment");
        System.out.println("3. Cancel Appointment");
        System.out.println("Enter your choice: ");
        int choice = 0;
        try {choice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}

        switch (choice) {
            case 1:
                scheduleAppointment(patientId, sc);
                break;
            case 2:
                rescheduleAppointment(patientId, sc);
                break;
            case 3:
                cancelAppointment(patientId, sc);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Schedules an appointment for a patient with a doctor.
     * Allows the patient to select a doctor and an available time slot
     * and then creates an appointment with the selected details.
     * The appointment is then added to the database with a status of "Pending"
     * and the patient is asked to provide a description of their symptoms.
     * The description is then added to the consultation notes of the appointment.
     * @param patientId the patient's ID
     * @param sc a Scanner object to read user input
     */
    private void scheduleAppointment(String patientId, Scanner sc) {
        dataStore = DBLocator.getDB();
        System.out.println("Available Doctors:");
        // Store doctors in a list for selection
        List<String> availableDoctors = dataStore.getDoctorsString();
        int index = 1;
        for (String doctorId : availableDoctors) {
            Doctor doctor = dataStore.getDoctor(doctorId);
            System.out.println(index++ + ". " + doctor.getName());
        }

        System.out.print("Select a doctor (Enter the number): ");
        int doctorChoice = 0;
        try {doctorChoice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}

        // Validate the user's choice
        if (doctorChoice < 1 || doctorChoice > availableDoctors.size()) {
            System.out.println("Invalid choice. Please try again.");
            //sc.close();
            return;
        }

        Doctor doctor = dataStore.getDoctor(availableDoctors.get(doctorChoice-1));

        System.out.println("Available time slots for Dr. " + doctor.getName() + ":");
        List<Date> availableSlots = doctor.getSchedule().getAvailableTimeslots(DateTimeHelper.getCurrentDateTime(), 15, 100);
        index = 1;
        for (Date date : availableSlots) {
            System.out.println(index++ + ". " + DateTimeHelper.dateTimeToString(date));
        }

        System.out.print("Select date and time slot (Enter the number): ");
        int dateTimeChoice = 0;
        try {dateTimeChoice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}

        // Validate the user's choice
        if (dateTimeChoice < 1 || dateTimeChoice > availableSlots.size()) {
            System.out.println("Invalid choice. Please try again.");
            //sc.close();
            return; // Exit the method if the choice is invalid
        }

        Date selectedSlot = availableSlots.get(dateTimeChoice - 1);
        String date = DateTimeHelper.dateTimeToDateString(selectedSlot);
        String timeSlot = DateTimeHelper.dateTimeToTimeString(selectedSlot);

        // Create new appointment with doctorName included
        Appointment newAppointment = new Appointment(patientId, doctor.getId(), doctor.getName(), date, timeSlot, "Consultation");
        newAppointment.setStatus("Pending");

        // Prompt for symptoms description
        System.out.println("Please describe your symptoms for the appointment: ");
        sc.nextLine(); 
        String symptomsDescription = sc.nextLine();

        // Add patient's symptoms description to consultation notes
        String symptomsNote = "Patient's description of symptoms (" + date + "): " + symptomsDescription;
        newAppointment.addConsultationNotes(symptomsNote);

        dataStore.addAppointment(newAppointment);

        System.out.println("Appointment request sent to doctor. Status: Pending.");
    }

    /**
     * Allows the patient to cancel one of their pending or confirmed appointments.
     * 
     * @param patientId The ID of the patient who is canceling the appointment
     * @param sc        The Scanner object to read input from the user
     */
    private void cancelAppointment(String patientId, Scanner sc) {
        dataStore = DBLocator.getDB();
        List<Appointment> patientAppointments = dataStore.getAppointmentPatientId(patientId);
        List<Appointment> appointmentList = new ArrayList<>();

        // Gather all pending or confirmed appointments for the patient
        for (Appointment appointment : patientAppointments) {
            if (appointment.getStatus().equalsIgnoreCase("Pending") || appointment.getStatus().equalsIgnoreCase("Confirmed")) {
                appointmentList.add(appointment);
            }
        }

        // Check if the patient has any appointments to cancel
        if (appointmentList.isEmpty()) {
            System.out.println("You have no pending or confirmed appointments to cancel.");
            return;
        }

        // Display the appointments in the specified format
        System.out.println("Your Appointments:");
        int index = 1;
        for (Appointment appointment : appointmentList) {
            System.out.println(index++ + ". " + appointment.getDoctorName() + ", " + appointment.getDate() + ", " + appointment.getTimeSlot());
        }

        // Allow the user to choose an appointment to cancel
        System.out.print("Select an appointment to cancel (Enter the number): ");
        int choice = 0;
        try {choice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}

        // Validate the user's choice
        if (choice < 1 || choice > appointmentList.size()) {
            System.out.println("Invalid choice. Please try again.");
            //sc.close();
            return;
        }

        // Cancel the selected appointment
        Appointment selectedAppointment = appointmentList.get(choice - 1);
        System.out.println("Appointment with Dr. " + selectedAppointment.getDoctorName() + " on " + selectedAppointment.getDate() + " at " + selectedAppointment.getTimeSlot() + " has been canceled.");
        dataStore.cancelAppointment(selectedAppointment);
        //sc.close();
    }

    /**
     * Reschedules an appointment for a patient with a doctor.
     * Allows the patient to cancel one of their pending or confirmed appointments
     * and then schedule a new appointment with a doctor of their choice.
     * The patient is asked to select a doctor and an available time slot
     * and then creates an appointment with the selected details.
     * The appointment is then added to the database with a status of "Pending"
     * and the patient is asked to provide a description of their symptoms.
     * The description is then added to the consultation notes of the appointment.
     * @param patientId the patient's ID
     * @param sc the Scanner object to read user input
     */
	private void rescheduleAppointment(String patientId, Scanner sc) {
        //Scanner scanner = new Scanner(System.in);
		cancelAppointment(patientId, sc);
		scheduleAppointment(patientId, sc);
        //scanner.close();
    }

    // Actions for Doctors
    private void handleDoctorActions(String doctorId) {
        dataStore = DBLocator.getDB();
        Doctor doctor = dataStore.getDoctor(doctorId);
        Scanner sc = doctor.getScanner();
        int choice;
        do{
            System.out.println("\nDoctor Portal: ");
            System.out.println("1. Manage Your Schedule");
            System.out.println("2. Review New Appointment Requests");
            System.out.println("3. Record Appointment");
            System.out.println("4. View Outcome of Completed appointment");
            System.out.println("0. Exit");
            System.out.println("Enter your choice: ");
            choice = -1;
            try {choice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}

            switch (choice) {
                case 1:
                    doctor.manageSchedule();
                    break;
                case 2:
                    reviewAppointments(doctorId, sc);
                    break;
                case 3:
                    recordCompletedAppointment(doctorId, sc);
                    break;
                case 4:
                    viewAppointmentOutcome(doctorId, sc);
                    break;
                case 0:
                    System.out.println("Exiting Appointment Management System...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice!=0);
        //scanner.close();
    }

	
    /**
     * Allows a doctor to review their pending appointments and accept or reject them.
     * The doctor is presented with a list of their pending appointments, and can
     * then select which ones to accept or reject.
     * If the doctor wants to accept an appointment, they are asked to confirm the
     * acceptance.
     * If the doctor wants to reject an appointment, they are asked to confirm the
     * rejection.
     * The doctor can also choose to update the service type of the appointment
     * if they accept it.
     * @param doctorId the doctor's ID
     * @param scanner the Scanner object to read user input
     */
	private void reviewAppointments(String doctorId, Scanner scanner) {
        dataStore = DBLocator.getDB();
        List<Appointment> appointments = dataStore.getAppointmentDoctorId(doctorId).stream().filter(a -> a.getStatus().equalsIgnoreCase("Pending")).toList();

        if (appointments.isEmpty()) {
            scanner.nextLine();
            System.out.println("You do not have any pending appointment requests.\n");
            System.out.println("\nPress \"ENTER\" to continue...");
            scanner.nextLine();
            return;
        }
        
        int index = 1;
        for (Appointment appointment : appointments) {
            System.out.println((index++) + ":");
			System.out.println("   Patient ID: " + appointment.getPatientId());
			System.out.println("   Doctor: " + appointment.getDoctorName());
			System.out.println("   Date: " + appointment.getDate());
			System.out.println("   Time: " + appointment.getTimeSlot());
            System.out.println("   Notes: ");
            for (String note : appointment.getConsultationNotes()) {
                System.out.println("      " + note);
            }
			System.out.println("--------------------------");
        }
        scanner.nextLine();
		System.out.print("Do you want to accept or reject any appointments? (yes/no): ");
		String response = scanner.nextLine();
	
		if (response.equalsIgnoreCase("yes")) {
			System.out.print("Enter the indices of the appointments to accept/reject (separated by spaces, press Enter to finish): ");
			String input = scanner.nextLine();
			
			// Split the input and process each index
			String[] indices = input.split("\\s+");
			for (String indexStr : indices) {
				try {
					index = Integer.parseInt(indexStr) - 1; // Convert to zero-based index
					if (index >= 0 && index < appointments.size()) {
						Appointment appointment = appointments.get(index);

                        // Ask doctor if they want to update serviceType
                        System.out.print("Do you want to update the serviceType for this appointment? (yes/no): ");
                        String updateResponse = scanner.nextLine();
                        if (updateResponse.equalsIgnoreCase("yes")) {
                            System.out.print("Enter the new service type: ");
                            String newServiceType = scanner.nextLine();
                            appointment.setServiceType(newServiceType);
                        }
                        
						// Ask the user if they want to accept or reject this appointment
						System.out.print("Do you want to accept (a) or reject (r) appointment for Patient ID: " + appointment.getPatientId() + "? ");
						String decision = scanner.nextLine();
						if (decision.equalsIgnoreCase("a")) {
							// ACode to accept the appointment
							appointment.setStatus("Confirmed"); // Update status
							System.out.println("Appointment accepted for Patient ID: " + appointment.getPatientId());
						} else if (decision.equalsIgnoreCase("r")) {
							// Reject the appointment
							dataStore.cancelAppointment(appointment);// Update status
							System.out.println("Appointment rejected for Patient ID: " + appointment.getPatientId());
						} else {
							System.out.println("Invalid input, skipping this appointment.");
						}
					} else {
						System.out.println("Invalid index: " + (index + 1));
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid input: " + indexStr + ". Please enter valid indices.");
				}
			}
            dataStore.writeAppointment();
            dataStore.writeSchedule();
			System.out.println("All selected appointments have been reviewed.");
		}
		else { System.out.println("No pending appointments were accepted or rejected."); }
		//scanner.close(); 
    }
	
    /**
     * Records a completed appointment.
     * Asks the doctor for the Appointment ID they want to mark as completed.
     * Then asks for the consultation notes and any prescriptions.
     * After adding the consultation notes and prescriptions, changes the status of the appointment to "Completed".
     * If the doctor did not prescribe any medications, the appointment status is changed to "Pending Medication".
     * @param doctorId the doctor's ID
     * @param scanner the Scanner object to read user input
     */
    private void recordCompletedAppointment(String doctorId, Scanner scanner) {
        //Scanner scanner = new Scanner(System.in);
        String appointmentId;
        dataStore = DBLocator.getDB();
        List<Appointment> acceptedAppointments = dataStore.getAppointmentDoctorId(doctorId).stream().filter(a -> a.getStatus().equalsIgnoreCase("Confirmed")).toList();
        if (acceptedAppointments.isEmpty()) {
            System.out.println("No appointments found.\n");
            System.out.println("\nPress \"ENTER\" to continue...");
            scanner.nextLine();
            return;
        }

        for (Appointment appointment : acceptedAppointments) {
            System.out.println("   Appointment ID: " + appointment.getAppointmentId());
			System.out.println("   Patient ID: " + appointment.getPatientId());
			System.out.println("   Doctor: " + appointment.getDoctorName());
			System.out.println("   Date: " + appointment.getDate());
			System.out.println("   Time: " + appointment.getTimeSlot());
			System.out.println("--------------------------");
        }

        scanner.nextLine();
        List<String> appointmentIds = new ArrayList<>();
        for (Appointment appointment : acceptedAppointments) {
            appointmentIds.add(appointment.getAppointmentId());
        }

        do {
            System.out.print("Enter completed Appointment ID: ");
            appointmentId = scanner.nextLine();

            // Check if input matches the required format and is a valid appointment ID
            if (!appointmentId.matches("A\\d+")) {
                System.out.println("Invalid input format. Please enter a valid Appointment ID.");
            } else if (!appointmentIds.contains(appointmentId)) {
                System.out.println("Invalid Appointment ID. Please enter a valid Appointment ID from the list.");
            }
        } while (!appointmentId.matches("A\\d+") || !appointmentIds.contains(appointmentId)); 
        
		for (Appointment appointment : acceptedAppointments) {
			if (appointment != null && appointment.getAppointmentId().equalsIgnoreCase(appointmentId)) {
				if (appointment.getDoctorId().equals(doctorId)) {
                    appointment.clearConsultationNotes();
                    appointment.clearPrescription();

					// Add consultation notes
                    System.out.println("Add consultation notes");
                    System.out.println("Enter \"Clear\" to clear consultation notes");
                    System.out.println("Enter \"Done\" to conclude consultation notes:");
                    while (true){
                        String notes = scanner.nextLine();
                        if (notes.equalsIgnoreCase("Clear")) {
                            appointment.clearConsultationNotes();
                            System.out.println("Consultation notes cleared");
                        }
                        else if (notes.equalsIgnoreCase("Done")) {
                            break;
                        }
                        else if (!notes.isBlank()) {
                            appointment.addConsultationNotes(notes);
                        }
                    }
		
					// Add prescription(s)
					System.out.println("Add Prescription(s)");
                    System.out.println("Enter \"List\" to view the list of medications");
                    System.out.println("Enter \"Clear\" to clear Prescriptions");
                    System.out.println("Enter \"Done\" to conclude Prescriptions:");
                    while (true) {
                        String prescription = scanner.nextLine(); // Read prescriptions as a string
                        if (prescription.equalsIgnoreCase("List")) {
                            System.out.println("\nList of medications: ");
                            for (Medication medication : dataStore.getMedicationInventory().getMedications()) {
                                if (medication.getMedicationName().isBlank()) {
                                    break;
                                }
                                System.out.println(medication.getMedicationName());
                            }
                            System.out.println("\nPress \"ENTER\" to continue...");
                            scanner.nextLine();
                            System.out.println("Add Prescription(s)");
                            System.out.println("Enter \"List\" to view the list of medications");
                            System.out.println("Enter \"Clear\" to clear Prescriptions");
                            System.out.println("Enter \"Done\" to conclude Prescriptions:");
                        }
                        else if (prescription.equalsIgnoreCase("Clear")) {
                            appointment.clearPrescription();
                            System.out.println("Prescriptions cleared");
                        }
                        else if (prescription.equalsIgnoreCase("Done")) {
                            break;
                        }
                        else if (!prescription.isBlank()) {
                            boolean found = false;
                            MedicationInventory medicationInventory = dataStore.getMedicationInventory();
                            for (Medication medication : medicationInventory.getMedications()) {
                                if (medication.getMedicationName().equalsIgnoreCase(prescription)) {
                                    found = true;
                                    appointment.addPrescription(medication);
                                    break;
                                }
                            }
                            if (!found) {
                                System.out.println("No such medication found!");
                            }
                        }
                    }
                    if (appointment.getPrescriptions().isEmpty()) {
                        appointment.setStatus("Completed");
                    }
                    else {
                        appointment.setStatus("Pending Medication");
                    }
                    Schedule doctorSchedule = dataStore.getDoctor(appointment.getDoctorId()).getSchedule();
                    for (Event appointmentEvent : doctorSchedule.getAppointments()) {
                        if (appointmentEvent.getEventDate().equals(appointment.getDate()) && appointmentEvent.getEventTime().equals(appointment.getTimeSlot())) {
                            doctorSchedule.getEvents().remove(appointmentEvent);
                            break;
                        }
                    }
					// Save appointments to CSV
					dataStore.writeAppointment();
                    dataStore.writeSchedule();
		
					System.out.println("Appointment marked as completed.");
                    System.out.println("\nPress \"ENTER\" to continue...");
                    scanner.nextLine();
                    return;
				}
			}
		}
    }

/**
 * Allows a doctor to view the outcome of their completed, dispensed, or 
 * pending medication appointments. The doctor is presented with a list 
 * of such appointments and can select an appointment to view detailed 
 * information, including consultation notes and prescribed medicine.
 * 
 * @param doctorId the doctor's ID
 * @param scanner the Scanner object to read user input
 */
    private void viewAppointmentOutcome(String doctorId, Scanner scanner) {
        String appointmentId;
        dataStore = DBLocator.getDB();
        List<Appointment> appointments = dataStore.getAppointmentDoctorId(doctorId).stream().filter(a -> 
        a.getStatus().equalsIgnoreCase("Pending medication") || 
        a.getStatus().equalsIgnoreCase("Completed") || 
        a.getStatus().equalsIgnoreCase("Dispensed")).toList();
        if (appointments.isEmpty()) {
            System.out.println("No completed appointments found!\n");
            System.out.println("\nPress \"ENTER\" to continue...");
            scanner.nextLine();
            return;
        }

        for (Appointment appointment : appointments) {
            System.out.println("   Appointment ID: " + appointment.getAppointmentId());
			System.out.println("   Patient ID: " + appointment.getPatientId());
			System.out.println("   Doctor: " + appointment.getDoctorName());
			System.out.println("   Date: " + appointment.getDate());
			System.out.println("   Time: " + appointment.getTimeSlot());
			System.out.println("--------------------------");
        }

        scanner.nextLine();
        List<String> appointmentIds = new ArrayList<>();
        for (Appointment appointment : appointments) {
            appointmentIds.add(appointment.getAppointmentId());
        }

        do {
            System.out.print("Enter completed Appointment ID: ");
            appointmentId = scanner.nextLine();

            // Check if input matches the required format and is a valid appointment ID
            if (!appointmentId.matches("A\\d+")) {
                System.out.println("Invalid input format. Please enter a valid Appointment ID.");
            } else if (!appointmentIds.contains(appointmentId)) {
                System.out.println("Invalid Appointment ID. Please enter a valid Appointment ID from the list.");
            }
        } while (!appointmentId.matches("A\\d+") || !appointmentIds.contains(appointmentId)); 

		for (Appointment appointment : appointments) {
			if (appointment != null && appointment.getAppointmentId().equalsIgnoreCase(appointmentId)) {
				if (appointment.getDoctorId().equals(doctorId)) {
                    System.out.println("\nInformation on appointment " + appointment.getAppointmentId());
                    System.out.println("Consultation notes: ");
                    for (String note : appointment.getConsultationNotes()) {
                        System.out.println(note);
                    }
                    System.out.println("\nPrescribed medicine: ");
                    for (String medication : appointment.getPrescriptions()) {
                        System.out.println(medication);
                    }
                    System.out.println("\nMedication prescription status: " + appointment.getStatus());
                    System.out.println("\nPress \"ENTER\" to continue...");
                    scanner.nextLine();
                    return;
                }
            }
        }
        System.out.println("\nInvalid Appointment ID\n");
    }
    
}
