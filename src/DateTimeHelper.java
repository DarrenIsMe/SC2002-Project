import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utility class for handling date and time operations.
 * Provides methods to convert between Date objects and formatted strings,
 * parse date and time strings into Calendar fields, and retrieve the current date and time.
 * Utilizes SimpleDateFormat for formatting and parsing operations.
 */
public class DateTimeHelper {

    private static Calendar cal = Calendar.getInstance();
    private static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    /**
     * Retrieves the current date and time.
     * 
     * @return the current date and time as a Date object.
     */
    public static Date getCurrentDateTime() {
        cal = Calendar.getInstance();
        cal.setLenient(false);
        return cal.getTime();
    }

    /**
     * Returns a clone of the current Calendar instance with leniency set to false.
     * This ensures strict date and time validation.
     *
     * @return a cloned Calendar instance with leniency disabled.
     */
    public static Calendar getInstance() {
        cal.setLenient(false);
        return (Calendar) cal.clone();
    }

    /**
     * Converts date and time strings into a Date object.
     * Parses the provided date and time strings into individual fields,
     * sets these fields in a Calendar instance, and returns the corresponding Date object.
     * If the input strings are invalid, returns null.
     *
     * @param date the date string in the format "dd/MM/yyyy".
     * @param time the time string in the format "HH:mm".
     * @return the Date object representing the specified date and time, or null if invalid.
     */
    public static Date stringToDateTime(String date, String time) {
        List<Integer> dateFields = DateTimeHelper.dateStringToFields(date);
        List<Integer> timeFields = DateTimeHelper.timeStringToFields(time);
        if (dateFields==null || timeFields==null) {
            return null;
        }
        cal.set(Calendar.DATE, dateFields.get(0));
        cal.set(Calendar.MONTH, dateFields.get(1));
        cal.set(Calendar.YEAR, dateFields.get(2));
        cal.set(Calendar.HOUR_OF_DAY, timeFields.get(0));
        cal.set(Calendar.MINUTE, timeFields.get(1));
        try {
            return cal.getTime();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid DateTime!");
            return null;
        }
    }

    /**
     * Converts a Date object to a formatted string.
     * Utilizes the predefined dateTimeFormatter to format the Date
     * into a string representation in the format "dd/MM/yyyy, HH:mm".
     *
     * @param date the Date object to be formatted.
     * @return a string representation of the date and time.
     */
    public static String dateTimeToString(Date date) {
        return dateTimeFormatter.format(date);
    }

    /**
     * Converts a Date object to a string representation of the date.
     * Utilizes the dateFormatter to format the Date object into a string
     * with the pattern "dd/MM/yyyy".
     *
     * @param date the Date object to be converted.
     * @return a string representation of the date in "dd/MM/yyyy" format.
     */
    public static String dateTimeToDateString(Date date) {
        return dateFormatter.format(date);
    }

    /**
     * Converts a Date object to a time string in the format "HH:mm".
     *
     * @param date the Date object to be converted.
     * @return the formatted time string.
     */
    public static String dateTimeToTimeString(Date date) {
        return timeFormatter.format(date);
    }

    /**
     * Parses a date string in the format "dd/MM/yyyy" into a list of integers
     * representing day, month, and year. Adjusts the month value to be zero-based.
     * Returns null if the input string is invalid or does not contain exactly three fields.
     *
     * @param date the date string to be parsed.
     * @return a list of integers [day, month, year] or null if invalid.
     */
    public static List<Integer> dateStringToFields(String date) {
        List<Integer> fields = new ArrayList<>();
        String[] stringFields = date.split("[/]");
        for (String field : stringFields){
            try {
                fields.add(Integer.parseInt(field));
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid date!");
                return null;
            }
        }
        if (fields.size()!=3) {
            System.out.println("Invalid date!");
            return null;
        }
        fields.set(1, fields.get(1)-1);
        return fields;
    }

    /**
     * Parses a time string in the format "HH:mm" into a list of integers
     * representing hours and minutes. Returns null if the input string is
     * invalid or does not contain exactly two fields.
     *
     * @param time the time string to be parsed.
     * @return a list of integers [hour, minute] or null if invalid.
     */
    public static List<Integer> timeStringToFields(String time) {
        List<Integer> fields = new ArrayList<>();
        String[] stringFields = time.split("[:]");
        for (String field : stringFields){
            try {
                fields.add(Integer.parseInt(field));
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid time!");
                return null;
            }
        }
        if (fields.size()!=2) {
            System.out.println("Invalid time!");
            return null;
        }
        return fields;
    }

    /**
     * Formats a Date object into a string representation.
     * Utilizes the predefined dateTimeFormatter to convert the Date
     * into a string with the format "dd/MM/yyyy, HH:mm".
     *
     * @param date the Date object to be formatted.
     * @return a formatted string representation of the date and time in "dd/MM/yyyy, HH:mm" format.
     */
    public static String formatDateTime(Date date) {
        return dateTimeFormatter.format(date);
    }

    /**
     * Formats a Date object into a string representation of the date.
     * Utilizes the dateFormatter to convert the Date object into a string
     * with the pattern "dd/MM/yyyy".
     *
     * @param date the Date object to be formatted.
     * @return a string representation of the date in "dd/MM/yyyy" format.
     */
    public static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    /**
     * Formats a Date object into a string representation of the time.
     * Utilizes the dateFormatter to convert the Date object into a string
     * with the pattern "HH:mm".
     *
     * @param date the Date object to be formatted.
     * @return a string representation of the time in "HH:mm" format.
     */
    public static String formatTime(Date date) {
        return timeFormatter.format(date);
    }

}
