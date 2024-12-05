import java.util.List;
import java.util.Scanner;

public class MedicalRecord {
    private List<String> pastDiagnosis;
    private List<String> treatment;

    public MedicalRecord(List<String> pastDiagnosis, List<String> treatment) {
        this.pastDiagnosis = pastDiagnosis;
        this.treatment = treatment;
    }

    public List<String> getPastDiagnosis() {
        return pastDiagnosis;
    }

    public List<String> getTreatmentList() {
        return treatment;
    }

/**
 * Prompts the user to enter a new diagnosis and treatment, which are then
 * added to the patient's medical record. The diagnosis and treatment are
 * appended to the respective lists of past diagnoses and treatments.
 */
    public void addDiagnosis() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Diagnosis: ");
        String diagnosis = sc.nextLine();
        System.out.print("\nTreatment: ");
        String treatment = sc.nextLine();
        System.out.println();
        this.pastDiagnosis.add(diagnosis);
        this.treatment.add(treatment);
        //sc.close();
    }

/**
 * Prints the medical record of the patient, including a list of past diagnoses
 * and their corresponding treatments. If there are no past records, a message
 * indicating the absence of past records is displayed.
 */
    public void printMedicalRecord() {
        if (pastDiagnosis.isEmpty()) {
            System.out.println("No past records available.");
            return;
        }
        System.out.println("\nPast Diagnoses and Treatments:");
        for (int i=0; i<pastDiagnosis.size(); i++) {
            System.out.println((i+1) + ". Diagnosis: " + pastDiagnosis.get(i) + " | Treatment: " + treatment.get(i));
        }
        System.out.println("End of Medical Record.");
    }

}
