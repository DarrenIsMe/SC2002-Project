public class Medication {
    
    private int medicationID;
    private String medicationName;
    private int stockLevel;
    private float medicationPrice;
    private int warningQuantity;
    private boolean replenishmentRequest;
    private boolean replenishmentRequestApproval; //True if already approved. False if not approved or not yet requested

    public Medication(int medicationID, String medicationName, int stockLevel, float medicationPrice, int warningQuantity, boolean replenishmentRequest, boolean replenishmentRequestApproval) {
        this.medicationID = medicationID;
        this.medicationName = medicationName;
        this.stockLevel = stockLevel;
        this.medicationPrice = medicationPrice;
        this.warningQuantity = warningQuantity;
        this.replenishmentRequest = replenishmentRequest;
        this.replenishmentRequestApproval = replenishmentRequestApproval;
    }

    public int getMedicationID() {
        return medicationID;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public int getStockLevel() {    
        return stockLevel;
    }

    public float getMedicationPrice() {
        return medicationPrice;
    }

    public int getWarningQuantity() {
        return warningQuantity;
    }

    public boolean getReplenishmentRequest() {
        return replenishmentRequest; //True or false
    }

    public boolean getReplenishmentRequestApproval() {
        return replenishmentRequestApproval;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public void setMedicationPrice(float medicationPrice) {
        this.medicationPrice = medicationPrice;
    }

    public void setWarningQuantity(int warningQuantity) {
        this.warningQuantity = warningQuantity;
    }

    public void setReplenishmentRequest(boolean replenishmentRequest) {        
        this.replenishmentRequest = replenishmentRequest;
    }

    public void setReplenishmentRequestApproval(boolean replenishmentRequestApproval) {
        this.replenishmentRequestApproval = replenishmentRequestApproval;
    }

}
