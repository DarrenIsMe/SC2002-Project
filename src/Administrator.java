import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Administrator extends User { 

    private static DataStore dataStore = DBLocator.getDB();
    private String ID;
    private String Gender;
    private int Age;
    private MedicationInventory inventory;
    private CSVHandler medicationinventorycsv;
    private CSVHandler staffListcsv;
    private List<String[]> staffList;
    private List<Doctor> doctors;
    private List<Pharmacist> pharmacists;
    private CSVHandler appointmentlist;
    private String currentid;

    public Administrator(String ID, String Name, String Gender, int Age, MedicationInventory inventory, CSVHandler medicationinventorycsv, CSVHandler staffListcsv, List<String[]> staffList, List<Doctor> doctors, List<Pharmacist> pharmacists, CSVHandler appointmentlist, String currentid)  //pass in the initialized MedicationInventory from App.Java
    {
        super(ID, Name, "Admin");
        this.inventory = inventory;
        this.Gender = Gender;
        this.Age = Age;
        this.medicationinventorycsv = medicationinventorycsv;
        this.staffListcsv = staffListcsv;
        this.staffList = staffList;
        this.pharmacists = pharmacists;
        this.doctors = doctors;
        this.appointmentlist = appointmentlist;
        this.currentid = currentid;

    }

    protected String getGender() {return Gender;}
    protected int getAge() {return Age;}

    /**
     * This function contains the entire menu for administrators. It provides options to
     * view and manage hospital staff, view appointment details, view and manage medication inventory,
     * approve replenishment requests, and logout.
     * The user is prompted to enter their choice and the function will execute the corresponding action.
     * The CSV file will be overwritten and the DataStore will be updated after each action.
     */
    @Override
    public void displayMenu(Scanner sc)       //display the entire admin menu in a loop.
    {
        dataStore = DBLocator.getDB();
        int choice = 0;
        boolean validInput = false;
        boolean loopiftrue = true;
        while(loopiftrue == true)
        {   
            validInput = false;
            while(!validInput)
            {
                GetAlert();
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("                                                                      ADMINISTRATOR MENU                                                                                                   "); 
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointment details");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. Logout");
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");

            
            

            
                try {choice = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}
            }
            switch(choice)
            {
                case 1:
                    //GetAlert();
                    displayViewAndManageHospitalStaffMenu();
                    break;
                case 2:
                    //GetAlert();
                    ViewAppointments();
                    break;
                case 3:
                    //GetAlert();
                    manageInventory();
                    break;
                case 4:
                    //GetAlert();
                    approveReplenishment(sc);
                    overwriteMedicationInventoryCSV();
                    dataStore.readMedicationCashe();
                    break;
                case 5:
                    super.logout();  
                    return;
            }
        }
    }

    /**
     * This function contains the entire menu for viewing and managing hospital staff. It provides options to
     * view all staffs, add new staff, update staff, remove staff, and exit.
     * The user is prompted to enter their choice and the function will execute the corresponding action.
     * The CSV file will be overwritten and the DataStore will be updated after each action.
     */
    public void displayViewAndManageHospitalStaffMenu()
    {
        dataStore = DBLocator.getDB();
        boolean loopiftrue = true;
        int choice = 0;
        Scanner sc = new Scanner(System.in);
        while(loopiftrue == true)
        {
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("                                                                      ADMINISTRATOR MENU : VIEW AND MANAGE HOSPITAL STAFF                                                                  ");                            
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("1. View all staffs       -1. Filter staffs");
            System.out.println("2. Add new staff");
            System.out.println("3. Update staff");
            System.out.println("4. Remove staff");
            System.out.println("0. Exit");

            try {choice = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}

            switch(choice)
            {
                case -1:
                    viewAllStaffsFiltered(sc);
                    break;
                case 1: //View All Staffs
                    viewAllStaffs();
                    break;
                case 2: //Add New Staff
                    AddStaff(sc);
                    overwriteStaffListCSV();
                    dataStore.readStaffCashe();
                    break;
                case 3: //Update Staff
                    UpdateStaff(sc);
                    overwriteStaffListCSV();
                    dataStore.readStaffCashe();
                    break;
                case 4: //Remove Staff
                    RemoveStaff(sc);
                    overwriteStaffListCSV();
                    dataStore.readStaffCashe();
                    break;
                case 0: //Exit
                    loopiftrue = false;
                    return;
                    //break;
            }
        }
        //sc.close();
    }

    /**
     * Prints out all staffs in the hospital in the format of
     * ID | Name            | Account Type   | Gender       | Age        | Password
     */
    public void viewAllStaffs()
    {
        dataStore = DBLocator.getDB();
        System.out.println("ID   |Name            |Account Type   |Gender       |Age        |Password   ");

        /* 
        for(int i=1; i<staffList.size(); i++) //start from 1 cuz 0 is the header
        {
            System.out.println(staffList.get(i)[0] + " " + staffList.get(i)[1] + "          " + staffList.get(i)[2]);
        }
        */
        for (Administrator administrator : dataStore.getAdministrators()) {
            //System.out.println(administrator.getId() + ". " + administrator.getName() + "         Administrator" + "         " + administrator.getGender() + "         " + administrator.getAge() + "         " + administrator.getPassword());
            System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", administrator.getId(), administrator.getName(), "Administrator", administrator.getGender(), administrator.getAge(), administrator.getPassword());
        }   
        /* 
        List<Administrator> adminList = dataStore.getAdministrators();
        for(int i = 0; i < adminList.size(); i++)
        {
            System.out.println(adminList.get(i));
        }
        */
        for(Doctor doctors : dataStore.getDoctors())
        {
            //System.out.println(doctors.getId() + ". " + doctors.getName() + "         Doctor         " + doctors.getGender() + "         " + doctors.getAge() + "         " + doctors.getPassword());
            System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", doctors.getId(), doctors.getName(), "Doctor", doctors.getGender(), doctors.getAge(), doctors.getPassword());
        }

        for(Pharmacist pharmacists : dataStore.getPharmacists())
        {
            //System.out.println(pharmacists.getId() + ". " + pharmacists.getName() + "         Pharmacist" + "         " + pharmacists.getGender() + "         " + pharmacists.getAge() + "         " + pharmacists.getPassword());
            System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", pharmacists.getId(), pharmacists.getName(), "Pharmacist", pharmacists.getGender(), pharmacists.getAge(), pharmacists.getPassword());
        }

    }

    public void viewAllStaffsFiltered(Scanner sc)
    {
        dataStore = DBLocator.getDB();
        boolean validInput = false;
        String filterchoice = "";
        String rolechoice = "";
        String genderchoice = "";
        Integer minimumAge = 0;
        Integer maximumAge = 100;
        boolean loopiftrue = true;

        //First, prompt the user what to filter by 
        sc.nextLine();

        while(loopiftrue == true)
        {

        

        do { 
            System.out.print("What do you wish to filter by? (1) Role (2) Gender (3) Age (0) Exit: ");
            filterchoice = sc.nextLine();
            if(filterchoice.equals("0")) {return;}
            if(filterchoice.equals("1") || filterchoice.equals("2") || filterchoice.equals("3")) {validInput = true; break;}
            else{
                System.out.println("Invalid input. Please try again.");
            }
        } while (validInput == false);

        if(filterchoice.equals("1")) //Filter by role
        {
            validInput = false;
            do { 
                System.out.print("What role do you want only? (1) Doctor (2) Pharmacist (3) Administrator (0) Exit: ");
                rolechoice = sc.nextLine();
                if(rolechoice.equals("0")) {return;}
                if(rolechoice.equals("1") || rolechoice.equals("2") || rolechoice.equals("3")) {validInput = true; break;}
            } while (validInput == false);
            System.out.println("_______________________________________________________________________________");
            System.out.println("ID   |Name            |Account Type   |Gender       |Age        |Password   ");
            if(rolechoice.equals("1"))
            {
                for(Doctor doctors : dataStore.getDoctors())
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", doctors.getId(), doctors.getName(), "Doctor", doctors.getGender(), doctors.getAge(), doctors.getPassword());
                }
            }
            else if(rolechoice.equals("2"))
            {
                for(Pharmacist pharmacists : dataStore.getPharmacists())
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", pharmacists.getId(), pharmacists.getName(), "Pharmacist", pharmacists.getGender(), pharmacists.getAge(), pharmacists.getPassword());
                }
            }
            else if(rolechoice.equals("3"))
            {
                for(Administrator administrator : dataStore.getAdministrators())
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", administrator.getId(), administrator.getName(), "Administrator", administrator.getGender(), administrator.getAge(), administrator.getPassword());
                }
            }
            System.out.println("_______________________________________________________________________________");
        }










        else if(filterchoice.equals("2")) //Filter by Gender
        {
            validInput = false;
            do { 
                System.out.print("Which Gender do you want to filter by? (1) Male (2) Female (0) Exit: ");
                rolechoice = sc.nextLine();
                if(rolechoice.equals("0")) {return;}
                if(rolechoice.equals("1") || rolechoice.equals("2")) {validInput = true; break;}
            } while (validInput == false);
            String genderchoicebutstring = "";
            if(rolechoice.equals("1")) {genderchoicebutstring = "Male";}
            else if(rolechoice.equals("2")) {genderchoicebutstring = "Female";}




            System.out.println("_______________________________________________________________________________");
            System.out.println("ID   |Name            |Account Type   |Gender       |Age        |Password   ");
            for(Doctor doctors : dataStore.getDoctors())
            {
                if(doctors.getGender().equals(genderchoicebutstring))
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", doctors.getId(), doctors.getName(), "Doctor", doctors.getGender(), doctors.getAge(), doctors.getPassword());
                }
            }
            for(Pharmacist pharmacists : dataStore.getPharmacists())
            {
                if(pharmacists.getGender().equals(genderchoicebutstring))
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", pharmacists.getId(), pharmacists.getName(), "Pharmacist", pharmacists.getGender(), pharmacists.getAge(), pharmacists.getPassword());
                }
            }
            for(Administrator administrator : dataStore.getAdministrators())
            {
                if(administrator.getGender().equals(genderchoicebutstring))
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", administrator.getId(), administrator.getName(), "Administrator", administrator.getGender(), administrator.getAge(), administrator.getPassword());
                }
            }
            System.out.println("_______________________________________________________________________________");
        }
        else if(filterchoice.equals("3"))
        {
            validInput = false;
            do { 
                System.out.print("Please enter MINIMUM age to filter from (Or Enter -1 to exit): ");
                try{minimumAge = sc.nextInt();validInput = true;} catch(Exception e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}
                if(minimumAge == -1) {return;}
                else if(minimumAge <0) {System.out.println("Minimum age cannot be less than 0.");validInput = false;}
                else if(minimumAge > 100) {System.out.println("Minimum age cannot be greater than 100.");validInput = false;}
            } while (validInput == false);

            validInput = false;
            do { 
                System.out.print("Please enter MAXIMUM age to filter from (Or Enter -1 to exit): ");
                try{maximumAge = sc.nextInt();validInput = true;} catch(Exception e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}
                if(maximumAge == -1) {return;}
                else if (minimumAge > maximumAge) {System.out.println("Maximum age cannot be less than minimum age.");validInput = false;}
                else if (maximumAge > 100) {System.out.println("Maximum age cannot be greater than 100.");validInput = false;}
            } while (validInput == false);

            System.out.println("_______________________________________________________________________________");
            System.out.println("ID   |Name            |Account Type   |Gender       |Age        |Password   ");
            for(Doctor doctors : dataStore.getDoctors())
            {
                if((doctors.getAge() >= minimumAge) && (doctors.getAge() <= maximumAge))
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", doctors.getId(), doctors.getName(), "Doctor", doctors.getGender(), doctors.getAge(), doctors.getPassword());
                }
            }
            for(Pharmacist pharmacists : dataStore.getPharmacists())
            {
                if((pharmacists.getAge() >= minimumAge) && (pharmacists.getAge() <= maximumAge))
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", pharmacists.getId(), pharmacists.getName(), "Pharmacist", pharmacists.getGender(), pharmacists.getAge(), pharmacists.getPassword());
                }
            }
            for(Administrator administrator : dataStore.getAdministrators())
            {
                if((administrator.getAge() >= minimumAge) && (administrator.getAge() <= maximumAge))
                {
                    System.out.printf("%-5s %-16s %-15s %-13s %-11s %-15s\n", administrator.getId(), administrator.getName(), "Administrator", administrator.getGender(), administrator.getAge(), administrator.getPassword());
                }
            }
            System.out.println("_______________________________________________________________________________");
            sc.nextLine();
        }
        }

        

    }

    /**
     * Allows the user to add a new staff member to the system. 
     * Prompts the user to input the staff type, ID number, name, gender, age, and password of the staff member to add.
     * Checks that the ID number is not already in use and that the passwords match.
     * Adds the staff member to the list of staff members if the input is valid.
     * @param sc The scanner object used to get user input.
     */
    public void AddStaff(Scanner sc)
    {
        int accountType;
        String IDnumberonly = "";
        String accountRoleName = "";
        String accountTypeString = "";
        int looplock = 1;
        String Gender = "";
        String password = "";
        String confirmpassword = "";
        boolean duplicateIDfound = false;
        String ID = "";
        int age;
        

        do
        {
            System.out.println("Please Choose the Account Type of the staff member you wish to add: (1) Doctor (2) Pharmacist (3) Administrator (0) Exit");

            accountType = 0;

            try{accountType = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}; 
            if(accountType == 0) {return;}
            if(accountType != 1 && accountType != 2 && accountType != 3) {System.out.println("Invalid Account Type Number Selected, please try again.");}
        }while(accountType != 1 && accountType != 2 && accountType != 3);
        
        if(accountType == 1)
        {
            accountTypeString = "D";
            accountRoleName = "Doctor";
        }
        else if (accountType == 2)
        {
            accountTypeString = "P";
            accountRoleName = "Pharmacist";
        }
        else if (accountType == 3)
        {
            accountTypeString = "A";
            accountRoleName = "Administrator";
        }
        sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
        while(looplock == 1)
        {
            duplicateIDfound = false;
            boolean validInput = false;
            
            do {
                System.out.println("Please Enter the ID of the staff member you wish to add (NUMBER ONLY!!!) (0 to exit): ");
                IDnumberonly = sc.nextLine();
                if (!IDnumberonly.matches("\\d+")) {
                    System.out.println("Invalid Number, please try again.");
                    continue;
                }
                else if(IDnumberonly.equals("0")) {return;}
                else if(Integer.parseInt(IDnumberonly) < 0){System.out.println("Number cannot be negative, please try again.");}
                else if(Integer.parseInt(IDnumberonly) > 9999){System.out.println("Please enter a number smaller than 9999, please try again.");} //LIMIT THE MAXIMUM ID NUMBER TO 9999
                else {validInput = true;}
            } while (validInput == false);            
            ID = accountTypeString + IDnumberonly;

            for(int i=0; i<staffList.size(); i++)
            {
                
                if(staffList.get(i)[0].equals(ID))
                {
                    System.out.println("ID already exists, please try again.");
                    duplicateIDfound = true;
                }
            }
            if(duplicateIDfound == false) {looplock = 0;}
            else {looplock = 1;}
        }
        boolean validInput = false;
        
        String Name = "";
        do { 
            System.out.println("Please Enter the Name of the staff member you wish to add (0 to exit): ");
            Name = sc.nextLine(); 
            if(Name.equals("0")) {return;}
            else if(Name.equals("")){System.out.println("Name cannot be empty, please try again.");}
            else {validInput = true;}
        } while (validInput == false);
        

        do{
            System.out.println("Please Enter Gender of the staff member you wish to add (M:Male, F:Female, 0:Exit): ");
            Gender = sc.nextLine(); if(Gender.equals("0")){return;}
        }while((!Gender.equals("M")) && (!Gender.equals("F")) && (!Gender.equals("m")) && (!Gender.equals("f")) && (!Gender.equals("Male")) && (!Gender.equals("Female")));
        if(Gender.equals("M") || Gender.equals("m") || Gender.equals("Male")) 
        {
            Gender = "Male";
        }
        else if(Gender.equals("F") || Gender.equals("f") || Gender.equals("Female")) 
        {
            Gender = "Female";
        }
        age = -1;
        do{
            System.out.println("What is the age of the new user? (0 to exit): ");
            try{age = sc.nextInt();} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();};
            if(age == 0) {return;}
            else if(age < 0) {System.out.println("Invalid age entered, please try again.");}
            else if(age > 100) {System.out.println("Invalid age entered, cannot be more than 100.");}
        }while(age < 0 || age > 100);
        
        sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
        do{
            System.out.println("Please Enter Password Of New User: (0 to exit)");
            password = sc.nextLine(); if(password.equals("0")){return;}
            System.out.println("Confirm Password: (0 to exit)");
            confirmpassword = sc.nextLine(); if(confirmpassword.equals("0")){return;}
            if(!password.equals(confirmpassword)) {System.out.println("Passwords do not match, please try again.");}    
        }while(!password.equals(confirmpassword));

        System.out.println("Success! StaffID " + ID + " has been added.");


        //System.out.println("Debug code 3: " + ID + " " + Name + " " + accountRoleName + " " + Gender + " " + Integer.toString(age) + " " + password);
        staffList.add(new String[] {ID, Name, accountRoleName, Gender, Integer.toString(age), password});
        //System.out.println("debug code 4");

    }














/**
 * Updates the information of an existing staff member. The user is prompted to enter the ID of the staff member they wish to update. If the ID is valid, the user can choose to update several fields such as ID, Name, Role, Gender, Age, or Password. Each field has its own validation to ensure correct input. The user may exit the update process at any time by entering '0'.
 *
 * @param sc The Scanner object used to get the user's input.
 */
    public void UpdateStaff(Scanner sc)
    {
        boolean loopiftrue = true;
        boolean nestedloopiftrue = true;
        boolean notfound = true;
        int indextoupdate = 0;
        viewAllStaffs();

        
        sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
        do { 
            System.out.print("Please Enter the ID of the staff member you wish to update (Enter 0 to exit): ");
            String ID = sc.nextLine();
            if(ID.equals("0")) {return;}
            for(int i=0; i<staffList.size(); i++)
            {
                if(staffList.get(i)[0].equals(ID))
                {
                    indextoupdate = i;
                    notfound = false;
                    break;
                }
            }
            if(notfound == true) {System.out.println("ID not found, please try again.");}
            else if(ID.equals("0")) {return;}
        } while (notfound == true);


        
        do { 
            boolean cannotexithere = false;
            System.out.print("Please enter what you wish to update for this staff member : (1) ID (2) Name (3) Role (4) Gender (5) Age (6) Password (0) Exit: ");
            boolean validInput = false;
            int choice = 0;
            do{
                try{choice = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();};
            }while(validInput == false);
            
            switch(choice)
            {
                case 3: //Update Role
                    nestedloopiftrue = true;
                    int newRole = 0;
                    String newRoleAlphabet = "";
                    do{
                        System.out.print("Please enter the new role (1) Doctor (2) Pharmacist (3) Administrator (0) Exit: ");
                        validInput = false;
                        do{
                            try{newRole = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();};
                        }while(validInput == false);
                        
                        if(newRole == 1) {staffList.get(indextoupdate)[2] = "Doctor"; newRoleAlphabet = "D"; nestedloopiftrue = false; cannotexithere = true;}
                        else if(newRole == 2) {staffList.get(indextoupdate)[2] = "Pharmacist"; newRoleAlphabet = "P"; nestedloopiftrue = false;cannotexithere = true;}
                        else if(newRole == 3) {staffList.get(indextoupdate)[2] = "Administrator"; newRoleAlphabet = "A"; nestedloopiftrue = false;cannotexithere = true;}
                        else if(newRole == 0) {break;}
                    }while(nestedloopiftrue == true);
                   
                    if(newRole == 0) {break;}
                    else{
                        int IDbutnumberonly = Integer.parseInt(staffList.get(indextoupdate)[0].substring(1));
                        staffList.get(indextoupdate)[0] = newRoleAlphabet + IDbutnumberonly;
                        System.out.println("Role Updated. Please select new ID for selected account");
                    }
                //no break here because I want user to select new ID after updating role.
                case 1: //Update ID
                    nestedloopiftrue = true;
                    sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
                    String Role = staffList.get(indextoupdate)[2];
                    Role = Role.substring(0, 1).toUpperCase();
                    String newID;
                    do { 
                        notfound = true;
                        System.out.print("Please enter the new ID (ENTER NUMBER ONLY) (0 to exit): ");
                        newID = sc.nextLine();
                        try { Integer.parseInt(newID); } catch (NumberFormatException e) { System.out.println("ID must be a number, please try again."); continue; } //This is to check if the string is a number
                        if(cannotexithere == true && newID.equals("0")) {System.out.println("Error Occurred: Cannot Exit Till You Select New ID!"); continue;}
                        else if(newID.equals("0")) {return;}
                        else if(newID.equals("")) {System.out.println("ID cannot be empty, please try again."); continue;}  //If the new ID is empty
                        for(int i=0; i<staffList.size(); i++)
                        {
                            if(staffList.get(i)[0].equals(Role + newID))  //if the new ID is already being used
                            {
                                System.out.println("ID already exists, please try again."); 
                                notfound = false;
                                //nestedloopiftrue = true;
                                break;
                            }
                        }
                        if(notfound == true)
                        {
                            nestedloopiftrue = false;
                        }

                    } while (nestedloopiftrue == true);

                    staffList.get(indextoupdate)[0] = Role + newID;
                    System.out.println("Success! ID has been updated.");
                    break;
                case 2: //Update Name
                    nestedloopiftrue = true;
                    String newName = "";
                    sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
                    do{
                        System.out.print("Please enter the new name (0 to exit): ");
                        newName = sc.nextLine();
                        if(newName.equals("0")) {break;}
                        else if (newName.equals("")) {System.out.println("Name cannot be empty, please try again."); continue;}
                        else {nestedloopiftrue = false; staffList.get(indextoupdate)[1] = newName; System.out.println("Success! Name has been updated.");}
                    }while(nestedloopiftrue == true);

                    break;
                
                case 4: //Update Gender
                    nestedloopiftrue = true;
                    int newGender = 0;

                    do{
                        System.out.print("Selected ID's current gender is " + staffList.get(indextoupdate)[3] + ". Switch Gender? (1) Yes (2) No ");
                        do{
                            try{newGender = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();};
                        }while(validInput == false);
                        
                        if(newGender == 1) {nestedloopiftrue = false;}
                        else if(newGender == 2) {nestedloopiftrue = false;}
                        else{System.out.println("Invalid input, please try again.");}
                    }while(nestedloopiftrue == true);
                    if(newGender == 2) {break;}
                    else{
                        if((staffList.get(indextoupdate)[3]).equals("Male"))
                        {
                            staffList.get(indextoupdate)[3] = "Female";
                        }
                        else{staffList.get(indextoupdate)[3] = "Male";}
                    }
                   
                    
                    
                    System.out.println("Gender Updated.");
                    break;
                case 5: //Update Age
                    nestedloopiftrue = true;
                    int newAge = 0;
                    do{
                        System.out.print("Selected ID's current age is " + staffList.get(indextoupdate)[4] + ". What is the user's new age? (0 to exit): ");
                        do{
                            try{newAge = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();};
                        }while(validInput == false);
                        if(newAge == 0) {break;}
                        if(newAge < 0 || newAge > 100) {System.out.println("Age must be between 0 and 100, please try again.");}
                        else{ staffList.get(indextoupdate)[4] = Integer.toString(newAge); System.out.println("Age Updated."); }
                    }while(newAge < 0 || newAge > 100);
                    
                    break;
                case 6: //Update Password
                    nestedloopiftrue = true;
                    String newPassword = "";
                    String confirmNewPassword = "";
                    sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
                    do{
                        System.out.print("Please enter the new password (0 to exit): ");
                        newPassword = sc.nextLine();
                        System.out.print("Please re-enter the new password for confirmation (0 to exit): ");
                        confirmNewPassword = sc.nextLine();
                        if(newPassword.equals("0") || confirmNewPassword.equals("0")) {break;}
                        else if(!newPassword.equals(confirmNewPassword)) {System.out.println("Passwords do not match, please try again."); continue;}
                        else if (newPassword.equals("")) {System.out.println("Password cannot be empty, please try again."); continue;}
                        else {nestedloopiftrue = false; staffList.get(indextoupdate)[5] = newPassword; System.out.println("Success! Password has been updated.");}
                    }while(nestedloopiftrue == true);
                break;
                case 0:
                    System.out.println("Saving Changes......");
                    return;
                
            }
        } while (loopiftrue == true);
        
    }





    public String getCurrentID() 
    {
        return currentid;
    }








    /**
     * Removes a staff account based on the ID input by the user. User will be prompted to enter the ID of the staff they want to remove. If the ID is invalid, the user will be prompted to try again. If the ID is valid, the staff account will be removed from the system and the user will be notified of the success. If the user enters 0, the function will exit.
     * @param sc The Scanner object used to get the user's input.
     */
    public void RemoveStaff(Scanner sc)
    {
        boolean loopiftrue = true;
        viewAllStaffs();
        sc.nextLine(); //keep this as it will skip my next input if not included for some reason, thanks for nothing Java! 
        do { 
            System.out.print("Please enter the ID of the staff you want to remove (0 to exit): ");
            String ID = sc.nextLine();
            if(ID.equals("0")) {break;}
            for(int i=0; i<staffList.size(); i++)
            {
                if(staffList.get(i)[0].equals(ID))
                {
                    if(ID.equals(getCurrentID())) {System.out.println("You cannot remove your own account!!!!!!!"); return;}
                    staffList.remove(i);
                    loopiftrue = false;
                    System.out.println("Success! Staff Account has been removed.");
                    return;
                }
                
            }
            System.out.println("Invalid ID, please try again");
        } while (loopiftrue == true);
        
    }

    /**
     * View all appointments stored in the system. This function will be modified
     * later to sort the appointments by date and time, and also to include filtering
     * options. For now, it just prints out all the appointments in the system.
     */
    public void ViewAppointments()
    {
        //waiting for other classes to be done before i can proceed with this
        List<String[]> appointments = appointmentlist.readCSV(); 
        for(int i=0; i<appointments.size(); i++)
        {
            //System.out.println(appointments.get(i)[0] + " " + appointments.get(i)[1] + " " + appointments.get(i)[2] + " " + appointments.get(i)[3] + " " + appointments.get(i)[4] + " " + appointments.get(i)[5] + " " + appointments.get(i)[6] + " " + appointments.get(i)[7] + " " + appointments.get(i)[8] + " " + appointments.get(i)[9] + " " + appointments.get(i)[10]);
            System.out.printf("%-15s %-10s %-20s %-10s %-15s %-12s %-12s %-20s %-20s %-25s %-10s\n",appointments.get(i)[0], appointments.get(i)[1], appointments.get(i)[2], appointments.get(i)[3], appointments.get(i)[4], appointments.get(i)[5], appointments.get(i)[6], appointments.get(i)[7], appointments.get(i)[8], appointments.get(i)[9], appointments.get(i)[10]);
        }

    }

    /**
     * This function contains the entire menu for managing inventory. It provides options to
     * view all medications, update medication information, add new medication, remove medication, and exit.
     * The user is prompted to enter their choice and the function will execute the corresponding action.
     * The CSV file will be overwritten and the DataStore will be updated after each action.
     */
    public void manageInventory()
    {
        //CONTAINS THE ENTIRE MENU FOR MANAGING INVENTORY 

        dataStore = DBLocator.getDB();
        boolean loopiftrue = true;
        Scanner sc = new Scanner(System.in);
        while(loopiftrue == true)
        {
            //menu
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("                                                                      ADMINISTRATOR MENU : VIEW AND MANAGE MEDICATION INVENTORY                                                            ");                            
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("1. View all medications");
            System.out.println("2. Update medication information");
            System.out.println("3. Add new medication");
            System.out.println("4. Remove medication");
            System.out.println("5. Exit");

            boolean validInput = false;
            int choice = 0;
            do{
                try{choice = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();};
            }while(!validInput);
            

            switch(choice)
            {
                case 1:
                    inventory.viewAllMedication();
                    break;
                case 2:
                    inventory.updateMedication(sc);
                    overwriteMedicationInventoryCSV(); //overwrite the CSV File
                    dataStore.readMedicationCashe();
                    break;
                case 3:
                    inventory.addMedication(sc);
                    overwriteMedicationInventoryCSV(); //overwrite the CSV File
                    dataStore.readMedicationCashe();
                    break;
                case 4:
                    inventory.removeMedication(sc);
                    overwriteMedicationInventoryCSV(); //overwrite the CSV File
                    dataStore.readMedicationCashe();
                    break;
                case 5:
                    return;
            }
        }
        //sc.close();
    }

        /*
         * Allows the Administrator to view a list of all medications that have their replenishmentRequest set to true and approve the replenishment of the selected medication by restocking it by the amount entered.
         *  Scanner object to read input from the user
         */
    public void approveReplenishment(Scanner sc) //untested
    {
        boolean nothingtoreplenish = true;
        boolean loopiftrue = true;
        while(loopiftrue == true)
        {
            
            
            //Check if any of the medications have their replenishmentRequest set to true. If so, display them all in a list. 
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            System.out.println("                                                                      ADMINISTRATOR MENU : APPROVE REPLENISHMENT REQUESTS                                                                  ");                            
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            Medication[] tempinventory = inventory.getMedications();

            System.out.println("ID |  Name          | Stock Level | Price | Warning Quantity"); //print menu header
            for(int i=0; i<inventory.getNumberOfMedications(); i++)
            {
                if(tempinventory[i].getReplenishmentRequest() == true)
                {
                    nothingtoreplenish = false;
                    //System.out.println(tempinventory[i].getMedicationID() + " | " + tempinventory[i].getMedicationName() + " | " + tempinventory[i].getStockLevel() + " | $" + tempinventory[i].getMedicationPrice() + " | " + tempinventory[i].getWarningQuantity());
                    System.out.printf("%-3s %-16s %-13s %-7s %-10s\n", tempinventory[i].getMedicationID(), tempinventory[i].getMedicationName(), tempinventory[i].getStockLevel(), tempinventory[i].getMedicationPrice(), tempinventory[i].getWarningQuantity());
                }
            }
            if(nothingtoreplenish == true) {System.out.println("Error! No medications have requested replenishment!!!"); return;}
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________");
            
            int choice = 0;
            boolean validInput = false;
            do { 
                System.out.println("Enter the ID of the medication you wish to approve replenishment for (or 0 to exit):");
                try {choice = sc.nextInt(); validInput = true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}
                if(choice == 0) {return;}
            } while (validInput == false);

            if(choice == 0) //if user wishes to exit
            {
                loopiftrue = false;
                return;
            }
            if(choice < 1 || choice > tempinventory.length) //if user enters an ID outside of range of possible IDs
            {
                System.out.println("Error Occurred: Medication ID not found");
                return;
            }
            else if(tempinventory[choice-1].getReplenishmentRequest() == false) //if user enters an ID that is not yet requested for replenishment
            {
                System.out.println("Error Occurred: Medication has not requested replenishment");
                return;
            }
            else if(tempinventory[choice-1].getReplenishmentRequest() == true)
            {
                validInput = false;
                int numberofunits = 0;
                do { 
                    System.out.print("How many units of this medication would you like to replenish? (0 to Exit): ");
                    try{numberofunits = sc.nextInt();validInput=true;} catch (InputMismatchException e) {System.out.println("Invalid input. Please enter an integer."); sc.nextLine();}
                    if(numberofunits == 0) {return;}
                } while (validInput == false);
                
                tempinventory[choice-1].setReplenishmentRequest(false);
                tempinventory[choice-1].setStockLevel(tempinventory[choice-1].getStockLevel() + numberofunits);
                return;
                
            }
            

            //Once that is done, the admin will get to choose using MedicationID on which to re-stock, and by how much. Result: replenishmentRequest set back to false for that particular medication and the stock for said medication will increase by the amount entered. 
        }
        
    }


    /*
     * Overwrites the existing MedicationInventory.csv with the current inventory's information
     * This method is called whenever the inventory is modified, in order to save the new state of the inventory
     */
    public void overwriteMedicationInventoryCSV()
    {
        List<String[]> dataToWrite = new ArrayList<>();

        //rewrite the header back in
        String[] header = new String[7];
        header[0] = "ID";
        header[1] = "Medication Name";
        header[2] = "Stock Level";
        header[3] = "Medication Price";
        header[4] = "Warning Quantity";
        header[5] = "Replenishment Request";
        header[6] = "Replenishment Approval";
        dataToWrite.add(header);

        for(int i=0; i<inventory.getNumberOfMedications(); i++)
        {
            String[] temp = new String[7];
            temp[0] = Integer.toString(inventory.getMedications()[i].getMedicationID());
            temp[1] = inventory.getMedications()[i].getMedicationName();
            temp[2] = Integer.toString(inventory.getMedications()[i].getStockLevel());
            temp[3] = Float.toString(inventory.getMedications()[i].getMedicationPrice());
            temp[4] = Integer.toString(inventory.getMedications()[i].getWarningQuantity());
            temp[5] = Boolean.toString(inventory.getMedications()[i].getReplenishmentRequest());
            temp[6] = Boolean.toString(inventory.getMedications()[i].getReplenishmentRequestApproval());
            dataToWrite.add(temp);
        }
        medicationinventorycsv.writeCSV(dataToWrite);
    }

    public void overwriteStaffListCSV()
    {
        List<String[]> dataToWrite = new ArrayList<>();
        dataToWrite.addAll(staffList);
        staffListcsv.writeCSV(dataToWrite);
    }


    /*
     * Prints a warning message if any of the medications in the inventory are
     * low on stock. If no medications are low on stock, prints a message
     * indicating that.
     */
    public void GetAlert()
    {   
        boolean anywarning = false;
        boolean nothingtoreplenish = true;
        System.out.println(" ____________________________________________________________________");
        for(int i=0; i<inventory.getNumberOfMedications(); i++)
        {
            if(inventory.getMedications()[i].getStockLevel() <= inventory.getMedications()[i].getWarningQuantity())
            {
                anywarning = true;
                String medicationwarning = " Warning!: Medication "+ inventory.getMedications()[i].getMedicationName() +" is low on stock. ";
                System.out.printf("|%-50s                  |\n",medicationwarning);
            }
            if(inventory.getMedications()[i].getReplenishmentRequest() == true)
            {
                nothingtoreplenish = false;
            }
        }    
        if(nothingtoreplenish == false) {System.out.println("| One or more medications requested by Pharmacists for replenishment |");}
        if(anywarning == false) {System.out.println("| No medications are low on stock.                                   |");}
        System.out.println("|____________________________________________________________________|");
        
    }

}