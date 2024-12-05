public class DataStoreUsageExample {
    // DB instance is declared in DBLocator and passed by reference.
    private static DataStore dataStore = DBLocator.getDB();

    public static void main(String[] args) {

        // if you make changes to the object, it will automatically update in the database if set methods are used
        Doctor doctor = dataStore.getDoctor("D001");
        System.out.println("Old list of events for doctor D001: ");
        printEvents();
        System.out.println("Old list of appointments for doctor D001: ");
        printAppointments();
        // An example will be creating a new appointment.
        Appointment appointment = new Appointment("P1002", doctor.getId(), doctor.getName(), "8/11/2024", "10:00", "Consultation");
        // WARNING This will add the appointment to the appointment and schedule csv files.
        // If you run this, make sure to delete the new entries.
        System.out.println("Adding a new appointment...");
        dataStore.addAppointment(appointment);
        System.out.println("\nNew list of events for doctor D001: ");
        printEvents();
        System.out.println("New list of appointments for doctor D001: ");
        printAppointments();
        printMedicationInventory();
        printAdministratorList();

    }
        
    public static void printEvents() {
        System.out.println("Retrieving Doctor from database");
        Doctor doctor = dataStore.getDoctor("D001");
        for (Event event : doctor.getSchedule().getEvents()) {
            System.out.println(event.getEventName() + ", " + event.getEventDescription() + ", " + event.getEventDate() + ", " + event.getEventTime());
        }
        System.out.println();
    }

    public static void printAppointments() {
        System.out.println("Retrieving appointments of doctor D001: ");
        Doctor doctor = dataStore.getDoctor("D001");
        for (Appointment appointment : dataStore.getAppointmentDoctorId("D001")) {
            System.out.println("Dr. " + appointment.getDoctorName() + " appointment with " + appointment.getPatientId());
        }
        System.out.println();
    }

    public static void printMedicationInventory() {
        System.out.println("Medication inventory: ");
        MedicationInventory medicationInventory = dataStore.getMedicationInventory();
        for (Medication medication : medicationInventory.getMedications()) {
            
            System.out.println(medication.getMedicationID() + ". " + medication.getMedicationName() + ", Stock: " + medication.getStockLevel());
        }
    }
    
    public static void printAdministratorList() {
        System.out.println("Administrator list: ");
        //List<Administrator> adminList = dataStore.getAdministrators();
        for (Administrator administrator : dataStore.getAdministrators()) {
            System.out.println(administrator.getId() + ". " + administrator.getName());
        }
    }

}
