import java.util.Scanner;


public class MedicationInventory {
    

    private int numberOfMedications;
    private Medication[] medications;
    

    public MedicationInventory()               //int numberOfMedications
    {
        CSVHandler medicationInventoryCSV = new CSVHandler("./src/MedicationInventory.csv");
        int numberOfMedications = 0;
        /* 
        List<String[]> data = medicationInventoryCSV.readCSV();
        for(int i = 1; i < data.size(); i++) //start from 1 because 0 will be the header in the csv file
        {
            numberOfMedications++;
        }
        */
        this.numberOfMedications = numberOfMedications;
        Medication[] medications = new Medication[1000];
        
        this.medications = medications;

        
        for(int i = 0; i< medications.length; i++)
        {
            medications[i] = new Medication(i+1, "                 ", 0, 0.00f, -1, false, false); //check if warning quantity is at negative 1, if it is, then the slot is not occupied
        }
        
    }

    public int getNumberOfMedications(int numberOfMedications) {
        return numberOfMedications;
    }

    public Medication[] getMedications()
    {
        return medications;
    }

    public int getNumberOfMedications()
    {
        return numberOfMedications;
    }

    /**
     * Prints out all the medications in the inventory in a table format.
     * The columns are: MedicationID, Medication Name, Stock Level, Medication Price, Warning Quantity
     */
    public void viewAllMedication()  //View all the medications 
    {
        System.out.println("================================================================================================================================");
        System.out.println("MedicationID |Medication Name                        |Stock Level  |Medication Price  |Warning Quantity");
        //Print out everything in the array
        for(int i=0; i<numberOfMedications; i++)
        {
            //System.out.println(medications[i].getMedicationID() + "            | " + medications[i].getMedicationName() + "                      | " + medications[i].getStockLevel() + "           | " + medications[i].getMedicationPrice() + "              | " + medications[i].getWarningQuantity());
            System.out.printf("%-13s %-39s %-13s %-18s %-15s\n", medications[i].getMedicationID(), medications[i].getMedicationName(), medications[i].getStockLevel(), medications[i].getMedicationPrice(), medications[i].getWarningQuantity());
        }
        System.out.println("================================================================================================================================");
    }

    /**
     * Allows the user to update the details of a medication in the inventory, including 
     * the name, stock level, price and warning quantity. The user is prompted to enter the
     * ID of the medication to update, and then the details to update. If the user enters
     * invalid input, they are asked to enter the input again. If the user wishes to exit
     * the menu, they can do so by entering 0 when prompted for the medication ID.
     * @param sc the Scanner object to read input from the user
     */
    public void updateMedication(Scanner sc)
    {
        //Scanner sc = new Scanner(System.in);
        boolean loopiftrue = true;
        boolean validInput = false;
        int option = 0;
        while (loopiftrue == true)
        {
            
        
            //print all the medications 
            viewAllMedication();
            
            //prompt the user for MedicationID 
            System.out.print("Please enter the ID of the medication you wish to update (or 0 to exit) :"); 
            int choice = 0;
            do { 
                try{choice = sc.nextInt(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
            } while (validInput == false);
            
            if(choice == 0) //if user wants to exit here
            {
                loopiftrue = false;
                break;
            }
            else if(choice<1 || choice>numberOfMedications)
            {
                System.out.println("Error Occurred: Medication not found");
                continue;
            }
            else if(medications[choice-1].getWarningQuantity() == -1)
            {
                System.out.println("Error Occurred: Medication not found");
            }
            else
            {
                System.out.println("================================================================================================================================");
                System.out.println("What do you wish to update?");
                System.out.println("1. Medication Name   2. Stock Level   3. Medication Price   4. Warning Quantity   0. Exit");
                System.out.println("================================================================================================================================");

                validInput = false;
                do { 
                    try {option = sc.nextInt(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                } while (validInput == false);
                //option = sc.nextInt();
                sc.nextLine(); //THIS IS NECESSARY TO CONSUME THE NEWLINE CHARACTER !!!!! DO NOT REMOVE!



                switch(option)
                {
                    case 1: //Update name

                        validInput = false;
                        String name = "";
                        do{ 
                            System.out.print("Please enter the new name (0 to exit):");
                            name = sc.nextLine();
                            if(name.equals("0")) {return;}
                            else if(name.equals("")) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                            else {validInput = true;}
                        }while(validInput == false);
                        
                        medications[choice-1].setMedicationName(name);
                        break;
                    case 2: //Update stock
                        validInput = false;
                        int stockLevel = 0; 
                        do { 
                            System.out.print("Please enter the new stock level (-1 to cancel):");
                            try{stockLevel = sc.nextInt(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                            if(stockLevel == -1){return;}
                            else if(stockLevel < -1){System.out.println("Error Occurred: Stock Level cannot be in the negatives!!!");validInput = false;}
                        } while (validInput == false);
                        
                        
                        medications[choice-1].setStockLevel(stockLevel);
                        break;
                    case 3:
                        validInput = false;
                        float price = (float) 0.0;
                        do { 
                            System.out.print("Please enter the new price (-1 to exit):");
                            try{price = sc.nextFloat(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                            if(price == -1){return;}
                            else if(price<0){System.out.println("Error Occurred: Invalid Input");validInput = false;}
                        } while (validInput == false);
    
                        medications[choice-1].setMedicationPrice(price);
                        break;
                    case 4:
                        validInput = false;
                        int warningQuantity = -1;
                        do { 
                            System.out.print("Please enter the new warning quantity (0 to exit):");
                            try{warningQuantity = sc.nextInt();validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                            if(warningQuantity == 0){return;}
                            else if(warningQuantity < 0) {System.out.println("Error Occurred: Warning Quantity cannot be in the negatives!!!"); validInput = false;}
                        } while (validInput == false);
                        
                        medications[choice-1].setWarningQuantity(warningQuantity);
                        break;
                    case 0:
                        loopiftrue = false;
                        break;
                }
                //maybe can print out success message and show what was updated if you want ?
            }
        }
        
        //sc.close(); //causes error
        
    }










    /**
     * Adds a new medication into the inventory
     * @param MedicationID The ID of the new medication
     * @param name The name of the new medication
     * @param stockLevel The initial stock level of the new medication
     * @param price The price of each unit of the new medication
     * @param warningQuantity The quantity level which will trigger a replenishment request
     * @param replenishmentRequest Whether the medication has a replenishment request
     * @param replenishmentRequestApproval Whether the replenishment request for the medication has been approved
     */
    public void addMedication(int MedicationID, String name, int stockLevel, float price, int warningQuantity, boolean replenishmentRequest, boolean replenishmentRequestApproval)
    {
        medications[MedicationID-1] = new Medication(MedicationID, name, stockLevel, price, warningQuantity, replenishmentRequest, replenishmentRequestApproval);
        numberOfMedications++;
    }






/**
 * Allows the user to add a new medication into the inventory.
 * The user is prompted to enter a medication ID slot to insert the new medication.
 * If the slot is valid and unoccupied, the user is then prompted to enter details
 * for the new medication, including name, stock level, price, and warning quantity.
 * The function includes validation to ensure inputs are valid and within acceptable ranges.
 * The process can be exited at any point by entering the specified exit value.
 * 
 * @param sc The Scanner object used to read user input
 */
    public void addMedication(Scanner sc)     //untested
    { 

        boolean loopiftrue = true;
        //Scanner sc = new Scanner(System.in);
        while(loopiftrue == true)
        {
            
            boolean validInput = false;
            int choice = 0;
            //firstly, show the list of medications in the inventory
            viewAllMedication();

            //prompt the user for MedicationID to slot new medication into
            
            
            do { 
                System.out.print("Please enter the ID slot of the medication you wish to add (or 0 to exit) :");
                try{choice = sc.nextInt(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}

            } while (validInput == false);
            

            if(choice == 0) //if the user wishes to cancel
            {
                loopiftrue = false;
                break;
            }
            else if(choice<1 || choice>999) //if user enters a number outside the range of medicationIDs  // || choice>numberOfMedications
            {
                System.out.println("Error Occurred: Invalid Medication ID Selected, Please choose a number between 1 and 999");
                continue;
            }
            else if(medications[choice-1].getWarningQuantity() != -1) //if the slot is already occupied
            {
                System.out.println("Error Occurred: MedicationID already in use, please pick another medicationID!");
                continue;
            }
            else
            {
                //prompt the user for all the information
                sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 

                validInput = false;
                String name = "";
                do { 
                    System.out.println("Please enter the name of the medication (0 to exit):");
                    name = sc.nextLine();
                    if(name.equals("0")) {return;}
                    else if(name.equals("")) {System.out.println("Error Occurred: Name cannot be empty!"); continue;}
                    else {validInput = true;}
                } while (validInput == false);
                

                validInput = false;
                int stockLevel = 0;
                do { 
                    System.out.println("Please enter the stock level of the medication (-1 to exit):");
                    try{stockLevel = sc.nextInt(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                    if(stockLevel == -1) {return;}
                    else if(stockLevel < -1) {System.out.println("Error Occurred: Stock Level cannot be in the negatives!!!"); validInput = false;}
                    else if(stockLevel > 99999) {System.out.println("Error Occurred: Stock Level cannot be greater than 99999!!!"); validInput = false;}
                } while (validInput == false);


                validInput = false;
                float price = (float) -1.0;
                do { 
                    System.out.println("Please enter the price of the medication (0 to exit):");
                    try{price = sc.nextFloat(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                    if(price == 0) {return;}
                } while (validInput == false);
                
                validInput = false;
                int warningQuantity = -2;
                do {
                    System.out.println("Please enter the warning quantity level of the medication (0 to exit):");
                    try{warningQuantity = sc.nextInt(); validInput = true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
                    if(warningQuantity == 0) {return;}
                    else if(warningQuantity < 0) {System.out.println("Error Occurred: Warning Quantity cannot be in the negatives!!!"); validInput = false;}
                    else if(warningQuantity > 99999) {System.out.println("Error Occurred: Warning Quantity Level cannot be greater than 99999!!!"); validInput = false;}
                } while (validInput == false);
                
                


                medications[choice-1].setMedicationName(name);
                medications[choice-1].setStockLevel(stockLevel);
                medications[choice-1].setMedicationPrice(price);
                medications[choice-1].setWarningQuantity(warningQuantity);
                numberOfMedications++;
                System.out.println("Done!");
                
            }
        }
        //sc.close();
    }


























        /**
         * Remove a medication from the inventory
         * @param sc the Scanner object used to retrieve user input
         */
       
    public void removeMedication(Scanner sc)    //untested
    {

        boolean loopiftrue = true;
        //Scanner sc = new Scanner(System.in);
        while(loopiftrue == true)
        {
            
            boolean validInput = false;
            int choice = 0;
            //prompt the user for MedicationID 
            do { 
                System.out.print("Please enter the ID of the medication you wish to remove (or 0 to exit) :"); 
                try{choice = sc.nextInt();validInput=true;} catch (Exception e) {System.out.println("Error Occurred: Invalid Input"); sc.nextLine();}
            } while (validInput == false);
            

            if (choice == 0)
            {
                loopiftrue = false;
                break;
            }
            else if(choice<1 || choice>numberOfMedications)
            {
                System.out.println("Error Occurred: Invalid Medication ID Selected");
                continue;
            }
            else if(medications[choice-1].getWarningQuantity() == -1)
            {
                System.out.println("Error Occurred: Medication not found");
                continue;
            }
            else
            {
                medications[choice-1].setMedicationName("                 ");
                medications[choice-1].setStockLevel(0);
                medications[choice-1].setMedicationPrice(0);
                medications[choice-1].setWarningQuantity(-1);
                medications[choice-1].setReplenishmentRequest(false);
                medications[choice-1].setReplenishmentRequestApproval(false);

                System.out.println("Done!");
                numberOfMedications--;
            }


            
        }
        //sc.close();
    }
}
