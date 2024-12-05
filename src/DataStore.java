
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The DataStore class manages the in-memory cache for various entities such as appointments, 
 * medical records, events, medications, patients, doctors, pharmacists, and administrators. 
 * It provides methods to read from and write to CSV files, ensuring data persistence. 
 * The class also offers functionalities to add, remove, and retrieve entities from the cache, 
 * maintaining synchronization with the underlying CSV data sources.
 */
public class DataStore {

    private HashMap<String, List<Appointment>> appointmentCashe_PatientId = new HashMap<>();
    private HashMap<String, List<Appointment>> appointmentCashe_DoctorId = new HashMap<>();
    private HashMap<String, List<String>> assignedPatientsCashe = new HashMap<>();
    private HashMap<String, MedicalRecord> medicalRecordCashe = new HashMap<>();
    private HashMap<String, List<Event>> eventCashe = new HashMap<>();
    private HashMap<String, Medication> medicationCashe = new HashMap<>();
    private HashMap<String, Patient> patientCashe = new HashMap<>();
    private HashMap<String, Doctor> doctorCashe = new HashMap<>();
    private HashMap<String, Pharmacist> pharmacistCashe = new HashMap<>();
    private HashMap<String, Administrator> adminCashe = new HashMap<>();
    private MedicationInventory medicationInventory;
    private static int eventCount = 1;
    

    private String[] patientHeader;
    private String[] staffHeader;
    private String[] appointmentHeader;
    private String[] medicalRecordHeader;
    private String[] medicationInventoryHeader;
    private String[] scheduleHeader;

    public DataStore() {
        try{
            readAllCashe();
            writeAllCashe();
        } catch(Exception e) {
            System.err.println("Error initializing DataStore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // read methods
    /**
     * Reads and initializes all caches by clearing existing data and 
     * loading fresh data from CSV files for events, medications, 
     * medical records, appointments, patients, and staff.
     */
    public void readAllCashe() {
        clearCashe();
        readEventCashe();
        readMedicationCashe();
        readMedicalRecordCashe();
        readAppointmentCashe();
        readPatientCashe();
        readStaffCashe();
    }

    private void clearCashe() {
        appointmentCashe_DoctorId.clear();
        appointmentCashe_PatientId.clear();
        assignedPatientsCashe.clear();
        medicalRecordCashe.clear();
        eventCashe.clear();
        medicationCashe.clear();
        patientCashe.clear();
        doctorCashe.clear();
        pharmacistCashe.clear();
        adminCashe.clear();
        medicationInventory=null;
    }

    public void readAppointmentCashe() {
        List<String[]> appointmentList;
        CSVHandler appointmentListCSV = new CSVHandler("./src/Appointment_List.csv");
        appointmentList = appointmentListCSV.readCSV();
        appointmentHeader = appointmentList.get(0);
        appointmentList.remove(0);

        Appointment.resetAppointmentCounter();
        appointmentCashe_PatientId.clear();
        appointmentCashe_DoctorId.clear();
        assignedPatientsCashe.clear();
        for (String[] row : appointmentList) {
            if (appointmentCashe_PatientId.get(row[3]) == null) {
                appointmentCashe_PatientId.put(row[3], new ArrayList<>());
            }
            if (appointmentCashe_DoctorId.get(row[1]) == null) {
                appointmentCashe_DoctorId.put(row[1], new ArrayList<>());
                assignedPatientsCashe.put(row[1], new ArrayList<>());
            }
            Appointment appointment = new Appointment(row[3], row[1], row[2], row[4], row[5], row[7]);
            if (DateTimeHelper.stringToDateTime(row[4], row[5]).before(DateTimeHelper.getCurrentDateTime()) && 
            !(row[8].equalsIgnoreCase("Completed") || 
            row[8].equalsIgnoreCase("MedicationPending") || 
            row[8].equalsIgnoreCase("Dispensed"))) {
                appointment.setStatus("Cancelled");
            }
            else{
                appointment.setStatus(row[8]);
            }
            if (!row[9].equals("\"\"")) {
                String[] consultationNotes = row[9].split("[|]");
                for (String note : consultationNotes) {
                    appointment.addConsultationNotes(note);
                }
            }
            if (!row[10].equals("\"\"")) {
            String[] medications = row[10].split("[|]");
            List<String> prescriptions = new ArrayList<>();
                for (String medicationName : medications) {
                    for (Medication medication : medicationCashe.values()) {
                        if (medication.getMedicationName().equals(medicationName)) {
                            prescriptions.add(medication.getMedicationName());
                            break;
                        }
                    }
                }
                appointment.setPrescription(prescriptions);
            }
            appointmentCashe_PatientId.get(row[3]).add(appointment);
            appointmentCashe_DoctorId.get(row[1]).add(appointment);
            
            if (!assignedPatientsCashe.get(row[1]).contains(row[3])){
                assignedPatientsCashe.get(row[1]).add(row[3]);
            }
        }
        writeAppointment();
    }

    public void readMedicalRecordCashe() {
        List<String[]> medicalRecordList;
        CSVHandler medicalRecordListCSV = new CSVHandler("./src/MedicalRecord.csv");
        medicalRecordList = medicalRecordListCSV.readCSV();
        medicalRecordHeader = medicalRecordList.get(0);
        medicalRecordList.remove(0);

        medicalRecordCashe.clear();
        for (String[] row : medicalRecordList) {
            List<String> pastDiagnosis = new ArrayList<>();
            List<String> treatments = new ArrayList<>();
            for (String diagnosis : row[1].split(";")) {
                pastDiagnosis.add(diagnosis);
            }
            for (String treatment : row[2].split(";")) {
                treatments.add(treatment);
            }
            for (int i=0; i<pastDiagnosis.size(); i++) {
                medicalRecordCashe.put(row[0], new MedicalRecord(pastDiagnosis, treatments));
            }
        }
    }

    public void readEventCashe() {
        List<String[]> eventList;
        CSVHandler eventListCSV = new CSVHandler("./src/Schedule_List.csv");
        eventList = eventListCSV.readCSV();
        scheduleHeader = eventList.get(0);
        eventList.remove(0);

        eventCashe.clear();
        eventCount=1;
        for (String[] row : eventList) {
            if (eventCashe.get(row[0]) == null) {
                eventCashe.put(row[0], new ArrayList<>());
            }
            Event event = new Event(row[2], row[3], row[4], row[5], row[6], Integer.parseInt(row[7]));
            event.setEventId("E" + eventCount++);
            eventCashe.get(row[0]).add(event);
        }
    }

    public void readMedicationCashe() {
        List<String[]> medicationList;
        CSVHandler medicationListCSV = new CSVHandler("./src/MedicationInventory.csv");
        medicationList = medicationListCSV.readCSV();
        medicationInventoryHeader = medicationList.get(0);
        medicationList.remove(0);

        medicationCashe.clear();
        medicationInventory = new MedicationInventory(); //TODO: check if medicationList.size() will always be maximum size or we should create a constant         medicationList.size()
        int i = 0;
        for (String[] row : medicationList) {
            medicationInventory.addMedication(Integer.parseInt(row[0]), row[1], Integer.parseInt(row[2]), Float.parseFloat(row[3]), Integer.parseInt(row[4]), Boolean.parseBoolean(row[5]), Boolean.parseBoolean(row[6]));
            medicationCashe.put(row[0], medicationInventory.getMedications()[i++]);
        }
    }

    public void readPatientCashe() {
        List<String[]> patientList;
        CSVHandler patientListCSV = new CSVHandler("./src/Patient_List.csv");
        patientList = patientListCSV.readCSV();
        patientHeader = patientList.get(0);
        patientList.remove(0);

        patientCashe.clear();
        for (String[] row : patientList) {
            patientCashe.put(row[0], new Patient(row[0], row[1], row[2], row[3], row[4], row[5], row[6]));
            if (!row[7].equals("password")) {
                patientCashe.get(row[0]).setPassword(row[7]);
            }
            // if patient does not have a medical record
            if (!medicalRecordCashe.containsKey(row[0])) {
                MedicalRecord emptyMedicalRecord = new MedicalRecord(new ArrayList<String>(), new ArrayList<String>());
                medicalRecordCashe.put(row[0], emptyMedicalRecord);
                patientCashe.get(row[0]).setMedicalRecord(emptyMedicalRecord);
            } 
            else {
                patientCashe.get(row[0]).setMedicalRecord(medicalRecordCashe.get(row[0]));
            }
            // if patient does not have any appointments
            if (!appointmentCashe_PatientId.containsKey(row[0])) {
                List<Appointment> emptyAppointments = new ArrayList<Appointment>();
                appointmentCashe_PatientId.put(row[0], emptyAppointments);
                patientCashe.get(row[0]).setAppointments(emptyAppointments);
            }
            else {
                patientCashe.get(row[0]).setAppointments(appointmentCashe_PatientId.get(row[0]));
            }
        }
        writeMedicalRecord();
        writeAppointment();
    }

    public void readStaffCashe() {
        List<String[]> staffList;
        CSVHandler staffListCSV = new CSVHandler("./src/Staff_List.csv");
        staffList = staffListCSV.readCSV();
        staffHeader = staffList.get(0);
        staffList.remove(0);

        doctorCashe.clear();
        pharmacistCashe.clear();
        adminCashe.clear();
        for (String[] row : staffList) {
            switch(row[0].charAt(0)) {
                case 'D':
                if (!eventCashe.containsKey(row[0])) {
                    eventCashe.put(row[0], new ArrayList<Event>());
                }
                if (!assignedPatientsCashe.containsKey(row[0])) {
                    assignedPatientsCashe.put(row[0], new ArrayList<String>());
                }
                if (!appointmentCashe_DoctorId.containsKey(row[0])) {
                    appointmentCashe_DoctorId.put(row[0], new ArrayList<Appointment>());
                }
                doctorCashe.put(row[0], new Doctor(row[0], row[1], row[3], Integer.parseInt(row[4]), new Schedule(eventCashe.get(row[0])), assignedPatientsCashe.get(row[0])));
                if (!row[5].equals("password")){
                    doctorCashe.get(row[0]).setPassword(row[5]);
                }
                break;
                case 'P':
                pharmacistCashe.put(row[0], new Pharmacist(row[0], row[1], row[3], row[4], medicationInventory, new CSVHandler("./src/MedicationInventory.csv"), new CSVHandler("./src/Staff_List.csv"), new CSVHandler("./src/Appointment_List.csv"), new ArrayList<>())); // TODO: remove CSVHandler
                if (!row[5].equals("password")){
                    pharmacistCashe.get(row[0]).setPassword(row[5]);
                }
                break;
                case 'A':
                
                adminCashe.put(row[0], new Administrator(row[0], row[1], row[3], Integer.parseInt(row[4]), medicationInventory, new CSVHandler("./src/MedicationInventory.csv"), new CSVHandler("./src/Staff_List.csv"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new CSVHandler("./src/Appointment_List.csv"), ""));
                if (!row[5].equals("password")){
                    adminCashe.get(row[0]).setPassword(row[5]);
                }
                break;
            }
        }
        writeSchedule();
        //writeStaff();
    }

    // write methods
    /**
     * Writes all cached data to their respective CSV files. This method sequentially
     * invokes the write methods for appointments, schedules, medications, patients,
     * and staff, ensuring that the current state of each cache is persisted to disk.
     */
    public void writeAllCashe() {
        writeAppointment();
        writeSchedule();
        writeMedication();
        writePatients();
        writeStaff();
    }

    public void writeAppointment() {
        List<String[]> dataToWrite = new ArrayList<>();
        for (List<Appointment> appointments : appointmentCashe_PatientId.values()) {
            for (Appointment appointment : appointments) {
                String[] entry = new String[appointmentHeader.length];
                entry[0] = appointment.getAppointmentId();
                entry[1] = appointment.getDoctorId();
                entry[2] = appointment.getDoctorName();
                entry[3] = appointment.getPatientId();
                entry[4] = appointment.getDate();
                entry[5] = appointment.getTimeSlot();
                Calendar cal = DateTimeHelper.getInstance();
                cal.setTime(DateTimeHelper.stringToDateTime(entry[4], entry[5]));
                cal.add(Calendar.MINUTE, App.AppointmentDuration);
                entry[6] = DateTimeHelper.dateTimeToTimeString(cal.getTime()); //TODO: confirm appointment class structure
                entry[7] = appointment.getServiceType().isEmpty() ? "\"\"" : appointment.getServiceType();
                entry[8] = appointment.getStatus().isEmpty() ? "\"\"" : appointment.getStatus();
                entry[9] = appointment.getConsultationNotes().isEmpty() ? "\"\"" : String.join("|", appointment.getConsultationNotes());
                entry[10] = appointment.getPrescriptions().isEmpty() ? "\"\"" : String.join("|", appointment.getPrescriptions());
                dataToWrite.add(entry);
            }
        }
        Collections.sort(dataToWrite, new Comparator<String[]>() {
            public int compare(String[] s1, String[] s2) {
                return s1[0].compareTo(s2[0]);
            }
        });
        dataToWrite.add(0,appointmentHeader);
        CSVHandler writer = new CSVHandler("./src/Appointment_List.csv");
        writer.writeCSV(dataToWrite);
    }

    public void writeMedicalRecord() {
        List<String[]> dataToWrite = new ArrayList<>();
        for (Patient patient : patientCashe.values()) {
            String[] entry = new String[medicalRecordHeader.length];
            entry[0] = patient.getId();
            List<String> pastDiagnosis = patient.getMedicalRecord().getPastDiagnosis();
            entry[1] = pastDiagnosis.isEmpty() ? "\"\"" : String.join(";", pastDiagnosis);
            List<String> treatmentList = patient.getMedicalRecord().getTreatmentList();
            entry[2] = treatmentList.isEmpty() ? "\"\"" : String.join(";", treatmentList);
            dataToWrite.add(entry);
        }
        Collections.sort(dataToWrite, new Comparator<String[]>() {
            public int compare(String[] s1, String[] s2) {
                return s1[0].compareTo(s2[0]);
            }
        });
        dataToWrite.add(0, medicalRecordHeader);
        CSVHandler writer = new CSVHandler("./src/MedicalRecord.csv");
        writer.writeCSV(dataToWrite);
    }

    public void writeSchedule() {
        List<String[]> dataToWrite = new ArrayList<>();
        for (String doctorId : eventCashe.keySet()) {
            for (Event event : eventCashe.get(doctorId)) {
                String[] entry = new String[scheduleHeader.length];
                entry[0] = doctorId;
                entry[1] = getDoctor(doctorId).getName();
                entry[2] = event.getEventName();
                entry[3] = event.getEventDescription();
                entry[4] = event.getEventType();
                entry[5] = event.getEventDate();
                entry[6] = event.getEventTime();
                entry[7] = String.valueOf(event.getEventDuration());
                dataToWrite.add(entry);
            }
        }
        Collections.sort(dataToWrite, new Comparator<String[]>() {
            public int compare(String[] s1, String[] s2) {
                return s1[0].compareTo(s2[0]);
            }
        });
        dataToWrite.add(0, scheduleHeader);
        CSVHandler writer = new CSVHandler("./src/Schedule_List.csv");
        writer.writeCSV(dataToWrite);
    }

    public void writeMedication() {
        List<String[]> dataToWrite = new ArrayList<>();
        dataToWrite.add(medicationInventoryHeader);
        for (Medication medication : medicationCashe.values()) {
            String[] entry = new String[medicationInventoryHeader.length];
            entry[0] = String.valueOf(medication.getMedicationID());
            entry[1] = medication.getMedicationName();
            entry[2] = String.valueOf(medication.getStockLevel());
            entry[3] = String.valueOf(medication.getMedicationPrice());
            entry[4] = String.valueOf(medication.getWarningQuantity());
            entry[5] = String.valueOf(medication.getReplenishmentRequest());
            entry[6] = String.valueOf(medication.getReplenishmentRequestApproval());
            dataToWrite.add(entry);
        }
        CSVHandler writer = new CSVHandler("./src/MedicationInventory.csv");
        writer.writeCSV(dataToWrite);

    }

    public void writePatients() {
        List<String[]> dataToWrite = new ArrayList<>();
        for (Patient patient : patientCashe.values()) {
            String[] entry = new String[patientHeader.length];
            entry[0] = patient.getId();
            entry[1] = patient.getName();
            entry[2] = patient.getDOB();
            entry[3] = patient.getGender();
            entry[4] = patient.getBloodType();
            entry[5] = patient.getEmail();
            entry[6] = patient.getPhoneNumber();
            entry[7] = patient.getPassword();
            dataToWrite.add(entry);
        }
        Collections.sort(dataToWrite, new Comparator<String[]>() {
            public int compare(String[] s1, String[] s2) {
                return s1[0].compareTo(s2[0]);
            }
        });
        dataToWrite.add(0, patientHeader);
        CSVHandler writer = new CSVHandler("./src/Patient_List.csv");
        writer.writeCSV(dataToWrite);
    }

    public void writeStaff() {
        List<String[]> dataToWrite = new ArrayList<>();
        for (Doctor doctor : doctorCashe.values()) {
            String[] entry = new String[staffHeader.length];
            entry[0] = doctor.getId();
            entry[1] = doctor.getName();
            entry[2] = "Doctor";
            entry[3] = doctor.getGender();
            entry[4] = String.valueOf(doctor.getAge());
            entry[5] = doctor.getPassword();
            dataToWrite.add(entry);
        }
        for (Pharmacist pharmacist : pharmacistCashe.values()) {
            String[] entry = new String[staffHeader.length];
            entry[0] = pharmacist.getId();
            entry[1] = pharmacist.getName();
            entry[2] = "Pharmacist";
            entry[3] = pharmacist.getGender();
            entry[4] = String.valueOf(pharmacist.getAge());
            entry[5] = pharmacist.getPassword();
            dataToWrite.add(entry);
        }
        for (Administrator admin : adminCashe.values()) {
            String[] entry = new String[staffHeader.length];
            entry[0] = admin.getId();
            entry[1] = admin.getName();
            entry[2] = "Administrator";
            entry[3] = admin.getGender();
            entry[4] = String.valueOf(admin.getAge());
            entry[5] = admin.getPassword();
            dataToWrite.add(entry);
        }
        Collections.sort(dataToWrite, new Comparator<String[]>() {
            public int compare(String[] s1, String[] s2) {
                return s1[0].compareTo(s2[0]);
            }
        });
        dataToWrite.add(0, staffHeader);
        CSVHandler writer = new CSVHandler("./src/Staff_List.csv");
        writer.writeCSV(dataToWrite);
    }

    // get Methods
    /**
     * Retrieves a list of appointments associated with a specific patient ID
     * from the in-memory cache.
     *
     * @param patientId the ID of the patient whose appointments are to be retrieved
     * @return a list of Appointment objects for the specified patient ID, or null if no appointments are found
     */
    public List<Appointment> getAppointmentPatientId(String patientId) {
        return appointmentCashe_PatientId.get(patientId);
    }

    /**
     * Retrieves a list of appointments associated with a specific doctor ID
     * from the in-memory cache.
     *
     * @param doctorId the ID of the doctor whose appointments are to be retrieved
     * @return a list of Appointment objects for the specified doctor ID, or null if no appointments are found
     */
    public List<Appointment> getAppointmentDoctorId(String doctorId) {
        return appointmentCashe_DoctorId.get(doctorId);
    }
    
    /**
     * Retrieves a list of patient IDs assigned to a specific doctor from the in-memory cache.
     *
     * @param doctorId the ID of the doctor whose assigned patients are to be retrieved
     * @return a list of patient IDs assigned to the specified doctor, or null if no patients are found
     */
    public List<String> getAssignedPatients(String doctorId) {
        return assignedPatientsCashe.get(doctorId);
    }

    /**
     * Retrieves the MedicationInventory instance associated with this DataStore.
     *
     * @return the current MedicationInventory object
     */
    public MedicationInventory getMedicationInventory() {
        return medicationInventory;
    }

    /**
     * Retrieves a list of all patient IDs from the in-memory cache.
     *
     * @return a list of strings representing the patient IDs.
     */
    public List<String> getPatients() {
        List<String> patientList = new ArrayList<>();
        patientList.addAll(patientCashe.keySet());
        return patientList;
    }

    /**
     * Retrieves a Patient object from the in-memory cache using the specified patient ID.
     *
     * @param patientId the unique identifier of the patient to be retrieved
     * @return the Patient object associated with the given patient ID, or null if not found
     */
    public Patient getPatient(String patientId) {
        return patientCashe.get(patientId);
    }
    
    /**
     * Retrieves a list of all doctor IDs from the in-memory cache.
     *
     * @return a list of strings representing the doctor IDs.
     */
    public List<String> getDoctorsString() {
        List<String> doctorList = new ArrayList<>();
        doctorList.addAll(doctorCashe.keySet());
        return doctorList;
    }

    /**
     * Retrieves a list of all Doctor objects from the in-memory cache.
     *
     * @return a list of Doctor objects representing all doctors stored in the cache.
     */
    public List<Doctor> getDoctors() {
        List<Doctor> doctorList = new ArrayList<>();
        doctorList.addAll(doctorCashe.values());
        return doctorList;
    }

    /**
     * Retrieves a Doctor object from the in-memory cache using the specified doctor ID.
     *
     * @param doctorId the unique identifier of the doctor to be retrieved
     * @return the Doctor object associated with the given doctor ID, or null if not found
     */
    public Doctor getDoctor(String doctorId) {
        return doctorCashe.get(doctorId);
    }

    /**
     * Retrieves a list of all pharmacist IDs from the in-memory cache.
     *
     * @return a list of strings representing the pharmacist IDs.
     */
    public List<String> getPharmacistsString() {
        List<String> pharmacistList = new ArrayList<>();
        pharmacistList.addAll(pharmacistCashe.keySet());
        return pharmacistList;
    }

    /**
     * Retrieves a Pharmacist object from the in-memory cache using the specified pharmacist ID.
     *
     * @param pharmacistId the unique identifier of the pharmacist to be retrieved
     * @return the Pharmacist object associated with the given pharmacist ID, or null if not found
     */
    public Pharmacist getPharmacist(String pharmacistId) {
        return pharmacistCashe.get(pharmacistId);
    }

    /**
     * Retrieves a list of all Pharmacist objects from the in-memory cache.
     *
     * @return a list of Pharmacist objects representing all pharmacists stored in the cache.
     */
    public List<Pharmacist> getPharmacists() {
        List<Pharmacist> pharmacistList = new ArrayList<>();
        pharmacistList.addAll(pharmacistCashe.values());
        return pharmacistList;
    }
    
    /**
     * Retrieves a list of all administrator IDs from the in-memory cache.
     *
     * @return a list of strings representing the administrator IDs.
     */
    public List<String> getAdministratorsString() {
        List<String> adminList = new ArrayList<>();
        adminList.addAll(adminCashe.keySet());
        return adminList;
    }

    /**
     * Retrieves an Administrator object from the in-memory cache using the specified administrator ID.
     *
     * @param adminId the unique identifier of the administrator to be retrieved
     * @return the Administrator object associated with the given admin ID, or null if not found
     */
    public Administrator getAdministrator(String adminId) {
        return adminCashe.get(adminId);
    }

    /**
     * Retrieves a list of all Administrator objects from the in-memory cache.
     *
     * @return a list of Administrator objects representing all administrators stored in the cache.
     */
    public List<Administrator> getAdministrators() {
        List<Administrator> adminList = new ArrayList<>();
        adminList.addAll(adminCashe.values());
        return adminList;
    }

    // add Methods. Returns true if successful
    // To use: Create a new appointment object first, then parse it through this method.
    /**
     * Adds a new appointment to the in-memory cache for both patient and doctor IDs.
     * If the appointment already exists, it returns false. Otherwise, it creates
     * an associated event in the doctor's schedule and writes the updated data to their respective CSV files.
     * 
     * @param appointment the Appointment object to be added
     * @return true if the appointment was successfully added, false if it already exists
     */
    public Boolean addAppointment(Appointment appointment) {
        if (appointmentCashe_PatientId.containsKey(appointment.getPatientId())){
            for (Appointment appt : appointmentCashe_PatientId.get(appointment.getPatientId())){
                if (appointment.equals(appt)){
                    System.out.println("Appointment already exists!");
                    return false;
                }
            }
        }
        if (appointmentCashe_PatientId.get(appointment.getPatientId()) == null) {
            appointmentCashe_PatientId.put(appointment.getPatientId(), new ArrayList<>());
        }
        if (appointmentCashe_DoctorId.get(appointment.getDoctorId()) == null) {
            appointmentCashe_DoctorId.put(appointment.getDoctorId(), new ArrayList<>());
            assignedPatientsCashe.put(appointment.getPatientId(), new ArrayList<>());
        }
        appointmentCashe_PatientId.get(appointment.getPatientId()).add(appointment);
        appointmentCashe_DoctorId.get(appointment.getDoctorId()).add(appointment);

        if (!assignedPatientsCashe.get(appointment.getDoctorId()).contains(appointment.getPatientId())) {
            assignedPatientsCashe.get(appointment.getDoctorId()).add(appointment.patientId);
        }

        String eventName = "Appointment with " + appointment.getPatientId();
        String eventDescription = "Appointment with Patient";
        String eventType = "Appointment";
        String eventDate = appointment.getDate();
        String eventTime = appointment.getTimeSlot();
        Event event = new Event(eventName, eventDescription, eventType, eventDate, eventTime, 15);
        addEvent(appointment.getDoctorId(), event);
        writeAppointment();
        return true;
    }
    
    /**
     * Adds a new event to the in-memory cache for a specified doctor.
     * If the event already exists, it returns false. Otherwise, it assigns
     * a unique event ID, sorts the events for the doctor, updates the schedule,
     * and returns true.
     *
     * @param doctorId the ID of the doctor to whom the event is to be added
     * @param event the Event object to be added
     * @return true if the event was successfully added, false if it already exists
     */
    public Boolean addEvent(String doctorId, Event event) {
        for (Event _event : eventCashe.get(doctorId)) {
            if (event.equals(_event)){
                System.out.println("Event already exists!");
                return false;
            }
        }
        event.setEventId("E" + eventCount++);
        eventCashe.get(doctorId).add(event);
        Collections.sort(eventCashe.get(doctorId));
        writeSchedule();
        return true;
    }

    /**
     * Adds a new patient to the in-memory cache and updates the CSV files.
     * If the provided patient is null, the method returns false.
     * Otherwise, it stores the patient and their medical record in the respective caches,
     * writes the updated patient and medical record data to their CSV files, and returns true.
     *
     * @param newPatient the Patient object to be added
     * @return true if the patient was successfully added, false if the patient is null
     */
    public Boolean addPatient(Patient newPatient) {
        if (newPatient==null) {
            return false;
        }
        patientCashe.put(newPatient.getId(), newPatient);
        medicalRecordCashe.put(newPatient.getId(), newPatient.getMedicalRecord());
        writePatients();
        writeMedicalRecord();
        return true;
    }

    /**
     * Adds a new Doctor object to the in-memory cache and updates the staff CSV file.
     * If the provided Doctor is null, the method returns false.
     * 
     * @param newDoctor the Doctor object to be added
     * @return true if the doctor was successfully added, false if the doctor is null
     */
    public Boolean addDoctor(Doctor newDoctor) {
        if (newDoctor==null) {
            return false;
        }
        doctorCashe.put(newDoctor.getId(), newDoctor);
        writeStaff();
        return true;
    }

    /**
     * Adds a new Pharmacist to the in-memory cache and updates the staff CSV file.
     * If the provided Pharmacist is null, the method returns false.
     * 
     * @param newPharmacist the Pharmacist object to be added
     * @return true if the Pharmacist was successfully added, false if the Pharmacist is null
     */
    public Boolean addPharmacist(Pharmacist newPharmacist) {
        if (newPharmacist==null) {
            return false;
        }
        pharmacistCashe.put(newPharmacist.getId(), newPharmacist);
        writeStaff();
        return true;
    }

    /**
     * Adds a new Administrator to the in-memory cache and updates the staff CSV file.
     * If the provided Administrator is null, the method returns false.
     * 
     * @param newAdmin the Administrator object to be added
     * @return true if the Administrator was successfully added, false if the Administrator is null
     */
    public Boolean addAdmin(Administrator newAdmin) {
        if (newAdmin==null) {
            return false;
        }
        adminCashe.put(newAdmin.getId(), newAdmin);
        writeStaff();
        return true;
    }

    /**
     * Cancels an existing appointment by removing it from the in-memory caches
     * for both patient and doctor IDs. If the appointment does not exist, it
     * returns false. Otherwise, it removes the associated event from the doctor's
     * schedule, checks if the patient is still considered assigned to the doctor,
     * updates the CSV files for appointments and schedules, and returns true.
     *
     * @param appointment the Appointment object to be canceled
     * @return true if the appointment was successfully canceled, false if it does not exist
     */
    // remove Methods. Returns true if successful
    public Boolean cancelAppointment(Appointment appointment) {
        if (!appointmentCashe_PatientId.containsKey(appointment.getPatientId())){
            System.out.println("Appointment does not exist!");
            return false;
        }
        appointmentCashe_PatientId.get(appointment.getPatientId()).remove(appointment); //.remove(appointment.getPatientId(), appointment);
        appointmentCashe_DoctorId.get(appointment.getDoctorId()).remove(appointment); //.remove(appointment.getDoctorId(), appointment);
        boolean containsPatient = false;
        for (Appointment appt : appointmentCashe_DoctorId.get(appointment.getDoctorId())) {
            if (appt.getPatientId().equals(appointment.getPatientId())) {
                containsPatient = true;
                break;
            }
        }
        if (!containsPatient) {
            assignedPatientsCashe.get(appointment.getDoctorId()).remove(appointment.getPatientId());
        }

        Event appointmentEvent = new Event("Appointment with " + appointment.getPatientId(), "Appointment with patient", "Appointment", appointment.getDate(), appointment.getTimeSlot(), App.AppointmentDuration);
        for (Event event : eventCashe.get(appointment.getDoctorId())) {
            if (event.equals(appointmentEvent)) {
                eventCashe.get(appointment.getDoctorId()).remove(event);
                break;
            }
        }
        writeAppointment();
        writeSchedule();
        return true;
    }

    /**
     * Removes a specified event from a doctor's schedule in the in-memory cache.
     * If the event does not exist, it prints a message and returns false.
     * After removal, the schedule is sorted and updated in the CSV file.
     *
     * @param doctor the Doctor object whose event is to be removed
     * @param event the Event object to be removed
     * @return true if the event was successfully removed, false if it does not exist
     */
    public Boolean removeEvent(Doctor doctor, Event event) {
        if (!eventCashe.get(doctor.getId()).contains(event)) {
            System.out.println("Event does not exist!");
            return false;
        }
        eventCashe.get(doctor.getId()).remove(event);
        Collections.sort(eventCashe.get(doctor.getId()));
        writeSchedule();
        return true;
    }

    /**
     * Removes a specified patient from the in-memory cache and updates the CSV file.
     * If the patient is null or does not exist in the cache, the method returns false.
     * Otherwise, it removes the patient and writes the updated data to the CSV file.
     *
     * @param patient the Patient object to be removed
     * @return true if the patient was successfully removed, false if the patient is null or does not exist
     */
    public Boolean removePatient(Patient patient) {
        if (patient==null || !patientCashe.containsKey(patient.getId())) {
            return false;
        }
        patientCashe.remove(patient.getId());
        writePatients();
        return true;
    }

    /**
     * Removes a specified Doctor from the in-memory cache and updates the staff CSV file.
     * If the Doctor is null or does not exist in the cache, the method returns false.
     * 
     * @param doctor the Doctor object to be removed
     * @return true if the Doctor was successfully removed, false otherwise
     */
    public Boolean removeDoctor(Doctor doctor) {
        if (doctor==null || !doctorCashe.containsKey(doctor.getId())) {
            return false;
        }
        doctorCashe.remove(doctor.getId());
        writeStaff();
        return true;
    }

    /**
     * Removes a specified Pharmacist from the in-memory cache and updates the staff CSV file.
     * If the provided Pharmacist is null or does not exist in the cache, the method returns false.
     * 
     * @param pharmacist the Pharmacist object to be removed
     * @return true if the Pharmacist was successfully removed, false if the Pharmacist is null or not found
     */
    public Boolean removePharmacist(Pharmacist pharmacist) {
        if (pharmacist==null || !pharmacistCashe.containsKey(pharmacist.getId())) {
            return false;
        }
        pharmacistCashe.remove(pharmacist.getId());
        writeStaff();
        return true;
    }

    /**
     * Removes an Administrator from the in-memory cache and updates the staff CSV file.
     * If the provided Administrator is null or does not exist in the cache, the method returns false.
     * 
     * @param admin the Administrator object to be removed
     * @return true if the Administrator was successfully removed, false otherwise
     */
    public Boolean removeAdmin(Administrator admin) {
        if (admin==null || !adminCashe.containsKey(admin.getId())) {
            return false;
        }
        adminCashe.remove(admin.getId());
        writeStaff();
        return true;
    }

    public void readAllCashePublic() {
        try {
            clearCashe();
            readEventCashe();
            readMedicationCashe();
            readMedicalRecordCashe();
            readAppointmentCashe();
            readPatientCashe();
            readStaffCashe();
        }
        catch (Exception e) {
            System.err.println("Error updating DataStore: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
