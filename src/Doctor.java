import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Doctor extends User {
    private String gender;
    private int age;
    private Schedule schedule;
    private List<String> assignedPatients; // List of patient IDs
    private static Scanner sc;
    private static int pageSize = 5;
    private static DataStore dataStore = DBLocator.getDB();

    public Doctor(String id, String name, String gender, int age, Schedule schedule, List<String> assignedPatients) {
        super(id, name, "Doctor");
        this.gender = gender;
        this.age = age;
        this.schedule = schedule;
        this.assignedPatients = assignedPatients;
    }

    public String getGender() {return gender;}
    public int getAge() {return age;}
    public Schedule getSchedule() {return schedule;}
    public List<String> getAssignedPatients() {return assignedPatients;}

    /**
     * Display the menu for the doctor to access their features.
     * 
     * @param sc The scanner to read the user's input.
     */
    @Override
    public void displayMenu(Scanner sc) {
        this.sc = sc;
        int choice;
        do{
            String message = "\nWelcome Dr. " + this.name + "\n"
                        + "--- Doctor Menu ---\n"
                        + "1) Manage Patient Medical Records\n"
                        + "2) Process Appointments\n"
                        + "0) Logout\n"
                        + "What do you want to do?\n";
            choice = getIntInput(message);
            switch(choice){
                case 1:
                    managePatientMedicalRecords();
                    break;

                case 2:
                    AppointmentManagement appointmentManagement = new AppointmentManagement("./src/Appointment_List.csv");
                    appointmentManagement.accessManager(this.id, sc);
                    break;

                case 0:
                    System.out.println("Logging out...");
                    promptEnterKey();
                    break;

                default:
                    System.out.println("Please input a valid option.");
                    promptEnterKey();
                    break;
            }
        } while(choice != 0);
    }
    // Patient Medical Records Manager methods

    /**
     * Manages the medical records of patients assigned to the doctor.
     * Displays a menu for viewing or updating patient medical records.
     * If no patients are assigned, notifies the doctor and exits.
     * Continuously prompts the doctor for an action until they choose to exit.
     */
    public void managePatientMedicalRecords() {
        if (assignedPatients.isEmpty()){
            System.out.println("You have not been assigned any patients.");
            promptEnterKey();
            return;
        }
        
        int choice;
        do{
            String message = "\n--- Medical Record Management System ---\n"
                        + "1) View Patient Medical Records\n"
                        + "2) Update Patient Medical Records\n"
                        + "0) Exit\n"
                        + "What do you want to do?\n";
            choice = getIntInput(message);
            String patientId;
            switch(choice){
                case 1:
                    patientId = selectPatient();
                    if (patientId!=null)
                        printPatientMedicalRecords(patientId);
                        promptEnterKey();
                    break;

                case 2:
                    patientId = selectPatient();
                    if (patientId!=null)
                        updatePatientMedicalRecords(patientId);
                        promptEnterKey();
                    break;

                case 0:
                    System.out.println("Exiting Patient Medical Records Management System...");
                    promptEnterKey();
                    break;

                default:
                    System.out.println("Please input a valid option.");
                    promptEnterKey();
                    break;
            }
        } while(choice != 0);
    }

    /**
     * Prints the medical record of a patient, including their personal data and 
     * medical record information.
     * @param patientId the ID of the patient whose medical record is to be printed.
     */
    public void printPatientMedicalRecords(String patientId) {
        dataStore = DBLocator.getDB();
        Patient patient = dataStore.getPatient(patientId);
        String patientData = "\nPatient ID: " + patient.getId()
                        + "\nPatient Name: " + patient.getName()
                        + "\nDate of Birth: " + patient.getDOB()
                        + "\nGender: " + patient.getGender()
                        + "\nPhone number: " + patient.getPhoneNumber()
                        + "\nEmail address: " + patient.getEmail()
                        + "\nBlood Type: " + patient.getBloodType();
        System.out.println(patientData);
        patient.getMedicalRecord().printMedicalRecord();
    }

    /**
     * Updates the medical records of a patient by potentially adding a new diagnosis.
     * 
     * @param patientId the ID of the patient whose medical record is to be updated.
     *                  The method retrieves the patient's record and prompts the user
     *                  to add a new diagnosis. If a diagnosis is added, the updated 
     *                  medical record is saved.
     */
    public void updatePatientMedicalRecords(String patientId) {
        dataStore = DBLocator.getDB();
        Patient patient = dataStore.getPatient(patientId);
        int choice;
        printPatientMedicalRecords(patientId);
        String message = "\nAdd a new diagnosis for this patient?\n"
                        +"1) Yes\n"
                        +"2) No\n";
        while (true) {
            choice = getIntInput(message);
            if (choice==1) {
                patient.getMedicalRecord().addDiagnosis();
                dataStore.writeMedicalRecord();
                return;
            }
            else if (choice==2) {
                return;
            }
            else {
                System.out.println("Invalid input!");
                promptEnterKey();
            }
        }

    }

    // Schedule Manager methods

    /**
     * Manages the doctor's schedule by providing a menu with options to view,
     * add, or remove events. The user can choose to view the entire schedule,
     * personal schedule, or upcoming medical appointments. The method loops
     * until the user decides to exit.
     */
    public void manageSchedule() {
        int choice;
        do{
            String message = "\n--- Schedule Management System ---\n"
                        + "1) View Entire Schedule\n"
                        + "2) View Personal Schedule\n"
                        + "3) View Upcoming Medical Appointments\n"
                        + "4) Add An Event To Your Schedule\n"
                        + "5) Remove An Event From Your Schedule\n"
                        + "0) Exit\n"
                        + "What do you want to do?\n";
            choice = getIntInput(message);
            Event event;
            switch(choice){
                case 1:
                    event = selectEvent("all");
                    if (event == null) {
                        promptEnterKey();
                        break;
                    }
                    if (event.getEventType().equalsIgnoreCase("Appointment")) {
                        event.printEvent();
                        promptEnterKey();
                        break;
                    }
                    showEventDetails(event);
                    break;

                case 2:
                    event = selectEvent("Personal");
                    if (event == null){
                        promptEnterKey();
                        break;
                    }
                    showEventDetails(event);
                    break;
                case 3:
                    event = selectEvent("Appointment");
                    if (event == null) {
                        promptEnterKey();
                        break;
                    }
                    event.printEvent();
                    promptEnterKey();
                    break;
                case 4:
                    addEvent();
                    break;
                case 5:
                    removeEvent(selectEvent("Personal"));
                    break;
                case 0:
                    System.out.println("Exiting Schedule Management System...");
                    promptEnterKey();
                    break;
                default:
                    System.out.println("Please enter a valid input!");
                    promptEnterKey();
                    break;
            }
        } while (choice!=0);
    }

    /**
     * Displays the details of the given event and provides options 
     * to modify or remove the event.
     *
     * @param event The event whose details are to be displayed.
     */
    public void showEventDetails(Event event) {
        event.printEvent();
        int choice;
        do{
            String message = "\n1. Modify Event\n"
                    + "2. Remove Event\n"
                    + "3. Back\n"
                    + "What do you want to do? ";
            choice = getIntInput(message);
            switch(choice){
                case 1:
                    modifyEvent(event);
                    choice=3;
                    break;
                case 2:
                    removeEvent(event);
                    choice=3;
                    break;
                default:
                    System.out.println("Please enter a valid input!");
                    break;
            }
        } while (choice!=3);
    }

    /**
     * Prompts the user to input details for a new event, validates the input, 
     * and adds the event to the schedule if confirmed by the user.
     * 
     * The user is asked to provide the event's name, description, date, time, 
     * and duration. The event is then displayed, and the user is given the option 
     * to confirm or cancel the addition of the event to their schedule.
     */
    public void addEvent() {
        dataStore = DBLocator.getDB();
        String name, description, date, time;
        int duration;
        String message = "Enter the details of your event\n"
                    + "Name: ";
        do {
            name = getStringInput(message);
            if (name.isBlank()) {
                System.out.println("Invalid entry!");
                promptEnterKey();
            }
        } while (name.isBlank());
        do {
            description = getStringInput("Description: ");
            if (description.isBlank()) {
                System.out.println("Invalid entry!");
                promptEnterKey();
            }
        } while (description.isBlank());
        date = getDateInput("Date (DD/MM/YYYY): ");
        time = getTimeInput("Time (HH:mm): ");
        do {
            duration = getIntInput("Duration (Minutes): ");
            if (duration<=0) {
                System.out.println("Invalid duration!");
                promptEnterKey();
            }
        } while (duration<=0);
        Event newEvent = new Event(name, description, "Personal", date, time, duration);
        newEvent.setEventId("-");
        newEvent.printEvent();
        message = "Add event to schedule?\n"
                + "1. Yes\n"
                + "2. No\n";
        while (true) {
            int confirm = getIntInput(message);
            if (confirm==1) {
                dataStore.addEvent(id, newEvent);
                dataStore.writeSchedule();
                break;
            }
            else if (confirm==2) {
                break;
            }
            else{
                System.out.println("Invalid input!");
            }
        }
    }
    
    /**
     * Removes an event from the doctor's schedule
     * @param event the event to be removed
     */
    public void removeEvent(Event event) {
        dataStore = DBLocator.getDB();
        if (event == null) {
            promptEnterKey();
            return;
        }
        event.printEvent();
        String message = "Remove event from schedule?\n"
                + "1. Yes\n"
                + "2. No\n";
        while (true) {
            int confirm = getIntInput(message);
            if (confirm==1) {
                dataStore.removeEvent(this, event);
                dataStore.writeSchedule();
                break;
            }
            else if (confirm==2) {
                break;
            }
            else{
                System.out.println("Invalid input!");
            }
        }
    }

    /**
     * Modifies the details of an existing event by prompting the user for new input.
     * If the user confirms the changes, the event is updated and the schedule is saved.
     * 
     * @param event The event to be modified. If null, the method exits without changes.
     */
    public void modifyEvent(Event event) {
        dataStore = DBLocator.getDB();
        if (event == null) {
            return;
        }
        String name, description, date, time;
        int duration;
        String message = "Enter the new details of your event\n"
                    + "Name: ";
        name = getStringInput(message);
        description = getStringInput("Description: ");
        date = getDateInput("Date (DD/MM/YYYY): ");
        time = getTimeInput("Time (HH:mm): ");
        duration = getIntInput("Duration (Minutes): ");
        Event newEvent = new Event(name, description, "Personal", date, time, duration);
        newEvent.printEvent();
        message = "Save changes to Event?\n"
                + "1. Yes\n"
                + "2. No\n";
        while (true) {
            int confirm = getIntInput(message);
            if (confirm==1) {
                event.setEventName(name);
                event.setEventDescription(description);
                event.setEventStartDate(date);
                event.setEventStartTime(time);
                event.setEventDuration(duration);
                dataStore.writeSchedule();
                break;
            }
            else if (confirm==2) {
                break;
            }
            else{
                System.out.println("Invalid input!");
            }
        }
    }

    // Appointment Manager methods




    // Utility methods
    public int getIntInput(String message) {
        int choice;
        System.out.print(message);
        try {
            choice = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            sc.nextLine();
            choice=-1;
        }
        return choice;
    }

    public String getStringInput(String message) {
        String string;
        System.out.print(message);
        string = sc.nextLine();
        return string;
    }

    public String getDateInput(String message) {
        Calendar cal = DateTimeHelper.getInstance();
        String date;
        boolean valid = false;
        do {
            System.out.print(message);
            date = sc.nextLine();
            List<Integer> dateFields = DateTimeHelper.dateStringToFields(date);
            if (dateFields==null) {
                continue;
            }
            cal.set(Calendar.DATE, dateFields.get(0));
            cal.set(Calendar.MONTH, dateFields.get(1));
            cal.set(Calendar.YEAR, dateFields.get(2));
            try {
                cal.getTime();
                valid = true;
            }
            catch (IllegalArgumentException e) {
                System.out.println("Invalid Date!");
            }
        } while (!valid);
        return date;
    }

    public String getTimeInput(String message) {
        Calendar cal = DateTimeHelper.getInstance();
        String time;
        boolean valid = false;
        do {
            System.out.print(message);
            time = sc.nextLine();
            List<Integer> timeFields = DateTimeHelper.timeStringToFields(time);
            if (timeFields==null) {
                continue;
            }
            cal.set(Calendar.HOUR_OF_DAY, timeFields.get(0));
            cal.set(Calendar.MINUTE, timeFields.get(1));
            try {
                cal.getTime();
                valid = true;
            }
            catch (IllegalArgumentException e) {
                System.out.println("Invalid Time!");
            }
        } while (!valid);
        return time;
    }

    public String selectPatient() {
        if (assignedPatients.isEmpty()) {
            System.out.println("You have no patients assigned to you.");
            return null;
        }
        int choice;
        int page=0;
        System.out.println("Select a patient: ");
        assignedPatients.sort(new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        do {
            int i;
            System.out.println();
            for (i=0; i<pageSize; i++) {
                if (i + (page*pageSize)<assignedPatients.size()){
                    System.out.println(i+1 + ") " + assignedPatients.get(i + (page*pageSize)));
                }
                else {
                    break;
                }
            }
            System.out.println();
            if (page<(assignedPatients.size()-1)/5) {
                System.out.println("6) Next Page");

            }
            if (page>0) {
                System.out.println("7) Previous Page");
            }
            System.out.println("0) Cancel");
            choice = getIntInput("");
            if (choice==6 && page<(assignedPatients.size()-1)/5) {
                page++;
            }
            else if (choice==7 && page!=0 ) {
                page--;
            }
            else if (choice>0 && choice<i+1) {
                return assignedPatients.get(choice + (page*pageSize) - 1);
            }
            else if (choice==0) {
                System.out.println("Cancelling operation... ");
                return null;
            }
            else {
                System.out.println("Please enter a valid option.");
            }
        } while (choice!=0);

        return null;
    }

    public Event selectEvent(String type) {
        List<Event> events;
        if (type.equalsIgnoreCase("Personal")) {
            events = schedule.getPersonalEvents();
            if (events==null || events.isEmpty()){
                System.out.println("No upcoming Personal events found!");
                return null;
            }
        }
        else if (type.equalsIgnoreCase("Appointment")) {
            events = schedule.getAppointments();
            if (events==null || events.isEmpty()){
                System.out.println("No upcoming Appointments found!");
                return null;
            }
        }
        else {
            events = schedule.getEvents();
            if (events==null || events.isEmpty()){
                System.out.println("No upcoming events found!");
                return null;
            }
        }
        
        int choice;
        int page=0;
        System.out.println("Select an Event: ");
        do {
            int i;
            System.out.println();
            for (i=0; i<pageSize; i++) {
                if (i + (page*pageSize)<events.size()){
                    int eventIndex = i+(page*pageSize);
                    System.out.println(i+1 + ") " + DateTimeHelper.dateTimeToString(events.get(eventIndex).getEventStartDate()) + ": " + events.get(eventIndex).getEventName());
                }
                else{
                    break;
                }
            }
            System.out.println();
            if (page<(events.size()-1)/5) {
                System.out.println("6) Next Page");
            }
            if (page>0) {
                System.out.println("7) Previous Page");
            }
            System.out.println("0) Cancel");
            choice = getIntInput("");
            if (choice==6 && page<(events.size()-1)/5) {
                page++;
            }
            else if (choice==7 && page!=0) {
                page--;
            }
            else if (choice>0 && choice<i+1) {
                return events.get(choice + (page*pageSize) - 1);
            }
            else if (choice == 0) {
                System.out.println("Cancelling operation... ");
                return null;
            }
            else {
                System.out.println("Please enter a valid option.");
            }
        } while (choice!=0);

        return null;
    }

    private void promptEnterKey() {
        System.out.println("\nPress \"ENTER\" to continue...");
        sc.nextLine();
    }

    public Scanner getScanner() {
        return sc;
    }







    public static void main(String[] args) {
        DataStore dataStore = DBLocator.getDB();
        Doctor doctor = dataStore.getDoctor("D001");
        Scanner sc = new Scanner(System.in);
        doctor.displayMenu(sc);

    }

}