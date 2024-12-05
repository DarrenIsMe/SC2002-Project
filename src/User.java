import java.util.Scanner;

public class User {
	protected String id;
	protected String name;
	protected String password;
	protected boolean firstTimeLogin;
	protected String userType;
	
	public User(String id, String name, String userType) {
		this.id = id;
		this.name = name;
		this.password = "password"; // default password
		this.firstTimeLogin = true;
		this.userType = userType; // patient, doctor, admin
	}
	
	// methods
	public String getId() { return id; }

	public String getName() { return name; }
	
	public boolean isFirstTimeLogin() { 
		return this.password.equals("password");
	}
	
	public String getUserType() { return userType; }
	
	protected String getPassword() { return password; }

	public void setFirstTimeLogin(boolean newSet) {this.firstTimeLogin = newSet;}

	protected void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Changes the user's password to the given new password if it is valid.
	 * The new password must meet the following criteria to be considered valid:
	 * <ul>
	 * <li>At least 1 uppercase letter</li>
	 * <li>At least 1 lowercase letter</li>
	 * <li>At least 1 digit</li>
	 * <li>Minimum of 8 characters</li>
	 * </ul>
	 * If the new password is valid, the user's first time login status will be set to false
	 * and the new password will be set. The method will return true to indicate a successful
	 * password change. If the new password is invalid, the method will return false and
	 * will not change the user's password.
	 * @param newPassword The new password to set
	 * @return true if the password was changed successfully, false otherwise
	 */
	public boolean changePassword(String newPassword) {
		if (isValidPassword(newPassword)) {
			if (isFirstTimeLogin()) { this.setFirstTimeLogin(false); } // Indicates first login is complete
			this.setPassword(newPassword);
			System.out.println("Password changed successfully.");
			return true; // Successful password change
		} else {
				System.out.println("\nPassword change failed. Ensure it meets the following criteria:");
				System.out.println("- At least 1 uppercase letter");
				System.out.println("- At least 1 lowercase letter");
				System.out.println("- At least 1 digit");
				System.out.println("- Minimum of 8 characters");
				return false; // Failed password change
		}
	}

	// Helper method to validate the password
	public boolean isValidPassword(String password) {
		if (password.length() < 8) {
				return false; // Minimum length requirement
		}
		boolean hasUppercase = false;
		boolean hasLowercase = false;
		boolean hasDigit = false;

		for (char c : password.toCharArray()) {
				if (Character.isUpperCase(c)) {
						hasUppercase = true;
				} else if (Character.isLowerCase(c)) {
						hasLowercase = true;
				} else if (Character.isDigit(c)) {
						hasDigit = true;
				}
				// Early exit if all conditions are met
				if (hasUppercase && hasLowercase && hasDigit) {
						return true;
				}
		}
		return false; // If any condition is not met
	}

	// note: if user has not logged in, they should not be able to log out (code in main maybe?)
	public void logout() { System.out.println("User " + id + " has logged out."); }	
	
	public void displayMenu(Scanner sc) { System.out.println("Output user specific menu."); }
}
