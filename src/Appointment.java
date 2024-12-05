import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Appointment {
    private static int appointmentCounter = 1; // for generating unique IDs

    protected String appointmentId;
    protected String patientId;
    protected String doctorId;
    protected String doctorName;
    protected String date;
    protected String timeSlot;
    protected String serviceType;
    protected String status; // i.e., "Pending", "Accepted", "Medication pending", "Completed", "Cancelled"
    protected List<String> consultationNotes;
    protected List<String> prescriptions; // list of prescribed medications

    public enum Status {
        CANCELLED, PENDING, ACCEPTED, MEDICATIONPENDING, COMPLETED
    }    

    // constructor
    public Appointment(String patientId, String doctorId, String doctorName, String date, String timeSlot, String serviceType) {
        this.appointmentId = "A" + appointmentCounter++; // unique appointment ID
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.date = date;
        this.timeSlot = timeSlot;
        this.serviceType = serviceType;
        this.status = "Pending"; // Default status
        this.consultationNotes = new ArrayList<>(); // initialize an empty list of consultation notes
        this.prescriptions = new ArrayList<>(); // initialize an empty list of prescriptions
    }   
 
    // getter & setter methods
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public String getServiceType() { return serviceType; }
    public String getStatus() { return status; }
    public List<String> getConsultationNotes() { return consultationNotes; }
    public List<String> getPrescriptions() { return prescriptions; }

    public void setStatus(String status) { this.status = status; }
    public void setDate(String newDate) { this.date = newDate; }
    public void setTimeSlot(String newTime) { this.timeSlot = newTime; }
    public void setServiceType(String newType) { this.serviceType = newType; }
    
    // add a new consultation notes to the list
    public void addConsultationNotes(String notes) { 
        String[] cleanNotes = notes.split(",");
        consultationNotes.addAll(Arrays.asList(cleanNotes)); 
    }

    public void clearConsultationNotes() {consultationNotes.clear();}

    // add an existing medication from MedicationInventory as a prescription
    public void addPrescription(Medication medication) {
        if (medication != null && medication.getWarningQuantity() != -1) { // Check medication is valid and active
            prescriptions.add(medication.getMedicationName());
            System.out.println("Medication " + medication.getMedicationName() + " added to prescriptions.");
        } else {
            System.out.println("Error: Invalid medication.");
        }
    }

    public void setPrescription(List<String> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public void clearPrescription() {prescriptions.clear();}

    public static void resetAppointmentCounter() {
        appointmentCounter = 1;
    }

/**
 * Compares this appointment to another object for equality. 
 * Returns true if the given object is an instance of Appointment and 
 * all of the following fields match: patientId, doctorId, doctorName, 
 * date, timeSlot, and serviceType. Otherwise, returns false.
 *
 * @param o the object to compare to this appointment
 * @return true if the object is equal to this appointment, false otherwise
 */
    @Override
    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }

        if (!(o instanceof Appointment)) {
            return false;
        }

        Appointment a = (Appointment) o;

        return patientId.equals(a.getPatientId())
            && doctorId.equals(a.getDoctorId())
            && doctorName.equals(a.getDoctorName())
            && date.equals(a.getDate())
            && timeSlot.equals(a.getTimeSlot())
            && serviceType.equals(a.getServiceType());
    }
}
