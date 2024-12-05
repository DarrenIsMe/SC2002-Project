import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Event implements Comparable<Event> {

    private String eventId;
    private String name;
    private String description;
    private String type;
    private String date;
    private String time;
    private String eventStartString;
    private int duration;
    private String eventEndString;
    private Date eventStart;
    private Date eventEnd;
    private static Calendar cal = DateTimeHelper.getInstance();
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

    public Event(String name, String description, String type, String date, String time, int duration) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.date = date;
        this.time = time;
        this.duration = duration;
        setEventStartDate(date);
        setEventStartTime(time);
        setEventDuration(duration);
    }

    // Get methods
    public String getEventId() {
        return eventId;
    }

    public String getEventName(){
        return name;
    }
    public String getEventDescription() {
        return description;
    }
    public String getEventType(){
        return type;
    }
    public String getEventDate() {
        return date;
    }
    public String getEventTime() {
        return time;
    }
    public int getEventDuration() {
        return duration;
    }
    public String getEventStartString() {
        return eventStartString;
    }
    public String getEventEndString() {
        return eventEndString;
    }
    public Date getEventStartDate() {
        return eventStart;
    }
    public Date getEventEndDate() {
        return eventEnd;
    }

    // Set Methods
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String name) {
        this.name = name;
    }

    public void setEventDescription(String description){
        this.description = description;
    }

    public void setEventType(String type) {
        this.type = type;
    }

    /**
     * Sets the event's start date using the provided date string.
     * Updates the eventStart, eventStartString, and date fields accordingly.
     * If the date string is invalid, the method exits without making changes.
     * 
     * @param date The date string in the format "dd/MM/yyyy".
     */
    public void setEventStartDate(String date) {
        if (eventStart != null)
            cal.setTime(eventStart);
        else
            cal.setTime(cal.getTime());
        List<Integer> dateFields = DateTimeHelper.dateStringToFields(date);
        if (dateFields==null) {
            return;
        }
        cal.set(Calendar.DATE, dateFields.get(0));
        cal.set(Calendar.MONTH, dateFields.get(1));
        cal.set(Calendar.YEAR, dateFields.get(2));
        eventStart = cal.getTime();
        eventStartString = DateTimeHelper.dateTimeToString(eventStart);
        this.date = date;
        updateEvent();
    }

    /**
     * Sets the event's start time using the provided time string.
     * Updates the eventStart, eventStartString, and time fields accordingly.
     * If the time string is invalid, the method exits without making changes.
     * 
     * @param time The time string in the format "HH:mm".
     */
    public void setEventStartTime(String time) {
        if (eventStart != null)
            cal.setTime(eventStart);
        else
            cal.setTime(cal.getTime());
        List<Integer> timeFields = DateTimeHelper.timeStringToFields(time);
        if (timeFields==null) {
            return;
        }
        cal.set(Calendar.HOUR_OF_DAY, timeFields.get(0));
        cal.set(Calendar.MINUTE, timeFields.get(1));
        eventStart = cal.getTime();
        eventStartString = DateTimeHelper.dateTimeToString(eventStart);
        this.time = time;
        updateEvent();
    }

    /**
     * Sets the event's duration in minutes.
     * Updates the eventEnd and eventEndString fields accordingly.
     * @param duration The event's duration in minutes.
     */
    public void setEventDuration(int duration) {
        this.duration = duration;
        updateEvent();
    }

    /**
     * Updates the eventEnd and eventEndString fields based on the current eventStart and duration.
     * If eventStart is null, the current time is used as the event start time.
     */
    private void updateEvent() {
        if (eventStart != null)
            cal.setTime(eventStart);
        else
            cal.setTime(cal.getTime());
        cal.add(Calendar.MINUTE, duration);
        eventEnd = cal.getTime();
        eventEndString = DateTimeHelper.dateTimeToString(eventEnd);
    }

    /**
     * Prints out the event's details in a formatted manner to the console.
     */
    public void printEvent() {
        System.out.println("\nEvent ID: " + eventId);
        System.out.println("Event Name: " + name);
        System.out.println("Event Description: " + description);
        System.out.println("Event Start Time: " + eventStartString);
        System.out.println("Event End Time: " + eventEndString);
        System.out.println("Duration: " + duration + " Minutes");
    }






    // return true if parsed event overlaps with current event
    public boolean checkOverlap(Date start, int duration){
        if (start.compareTo(eventStart) >= 0 && start.compareTo(eventEnd) < 0)
            return true;
        cal.setTime(start);
        cal.add(Calendar.MINUTE, duration);
        Date end = cal.getTime();
        return (end.compareTo(eventStart) > 0 && end.compareTo(eventEnd) <= 0);
    }

    // for sorting
    @Override
    public int compareTo(Event event) {
        return getEventStartDate().compareTo(event.getEventStartDate());
    }

    // for comparing
    @Override
    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }

        if (!(o instanceof Event)) {
            return false;
        }

        Event a = (Event) o;

        return name.equalsIgnoreCase(a.getEventName())
            && description.equalsIgnoreCase(a.getEventDescription())
            && type.equalsIgnoreCase(a.getEventType())
            && date.equals(a.getEventDate())
            && time.equals(a.getEventTime())
            && duration == a.getEventDuration();
    }

}
