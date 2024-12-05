/**
 * The DBLocator class provides a singleton-like access to a shared instance of the DataStore.
 * It ensures that only one instance of DataStore is used throughout the application.
 * 
 * This class cannot be instantiated and provides a static method to retrieve the DataStore instance.
 */
public class DBLocator {

    static DataStore dataStore = new DataStore();

    private DBLocator() {}

    public static DataStore getDB() {return dataStore;}

}
