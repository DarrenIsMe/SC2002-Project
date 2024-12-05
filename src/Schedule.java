import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class Schedule {
    private List<Event> events;

    public Schedule(List<Event> events) {
        this.events = events;
        Collections.sort(this.events);
        List<Event> oldEvents = getEventsBefore(DateTimeHelper.getCurrentDateTime());
        if (oldEvents!=null && !oldEvents.isEmpty())
            this.events.removeAll(getEventsBefore(DateTimeHelper.getCurrentDateTime()));
    }

    /**
     * Returns the list of events in the schedule, sorted by event start date/time. Note that events before the current date/time are automatically removed from the schedule when the Schedule object is created.
     * @return the list of events
     */
    public List<Event> getEvents() {
        return events;
    }

    // Get methods

    /**
     * Returns the list of events in the schedule that start before the specified date.
     * @param date the date to check against
     * @return the list of events before the specified date
     */
    public List<Event> getEventsBefore(Date date) {
        if (events == null || events.isEmpty())
            return new ArrayList<>(); // or return null, depending on your requirements
        Collections.sort(events);
    
        if (date.before(events.get(0).getEventStartDate()))
            return new ArrayList<>(); // or return null, depending on your requirements
        int i = events.size();
        do {
            i--;
        } while (date.before(events.get(i).getEventStartDate()));
        if (i==0) {
            return events.subList(0, 1);
        }
        else {
            return events.subList(0, i);
        }
    }

    /**
     * Returns the list of events in the schedule that start after the specified date.
     * @param date the date to check against
     * @return the list of events after the specified date
     */
    public List<Event> getEventsAfter(Date date) {
        if (events==null || events.isEmpty())
            return null;
        Collections.sort(events);
        if (date.after(events.get(events.size()-1).getEventStartDate()))
            return null;
        int i=-1;
        do{
            i++;
        } while (date.before(events.get(i).getEventStartDate()));
        if (i==events.size()-1) {
            return events.subList(events.size()-1, events.size());
        }
        else {
            return events.subList(i, events.size());
        }
    }

    /**
     * Returns the list of events in the schedule that are of type "Personal".
     * @return the list of events
     */
    public List<Event> getPersonalEvents() {
        if (events==null)
            return null;
        return events.stream().filter(events -> events.getEventType().equalsIgnoreCase("Personal")).collect(Collectors.toList());
    }

    /**
     * Returns the list of events in the schedule that are of type "Appointment".
     * @return the list of events
     */
    public List<Event> getAppointments() {
        if (events==null)
            return null;
            return events.stream().filter(events -> events.getEventType().equalsIgnoreCase("Appointment")).collect(Collectors.toList());
    }

    /**
     * Returns a list of Date objects representing available timeslots starting from the specified startDate and startTime
     * that are not overlapping with any events in the schedule. The maximum number of available timeslots returned
     * is specified by max. The duration of each available timeslot is specified by duration.
     * @param startDate The start date of the date range to search for available timeslots.
     * @param startTime The start time of the date range to search for available timeslots.
     * @param duration The duration of each available timeslot in minutes.
     * @param max The maximum number of available timeslots to return.
     * @return A list of Date objects representing available timeslots starting from the specified startDate and startTime
     * that are not overlapping with any events in the schedule.
     */
    public List<Date> getAvailableTimeslots(String startDate, String startTime, int duration, int max) {
        Calendar cal = DateTimeHelper.getInstance();
        cal.setTime(DateTimeHelper.stringToDateTime(startDate, startTime)); // set date to specified date
        // if specified date is before current date, set start date to current date
        if (cal.getTime().before(DateTimeHelper.getCurrentDateTime())) {
            cal.setTime(DateTimeHelper.getCurrentDateTime());
        }
        // adjust minutes to 0, 15, 30 or 45 rounded up.
        if (cal.get(Calendar.MINUTE)>0 && cal.get(Calendar.MINUTE)<15) {
            cal.set(Calendar.MINUTE, 15);
        }
        else if (cal.get(Calendar.MINUTE)>15 && cal.get(Calendar.MINUTE)<30) {
            cal.set(Calendar.MINUTE, 30);
        }
        else if (cal.get(Calendar.MINUTE)>30 && cal.get(Calendar.MINUTE)<45) {
            cal.set(Calendar.MINUTE, 45);
        }
        else if (cal.get(Calendar.MINUTE)>45 && cal.get(Calendar.MINUTE)<60) {
            cal.setLenient(true);
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+1);
            cal.setLenient(false);
            cal.set(Calendar.MINUTE, 0);
        }
        // define opening hour and closing hour
        Calendar open = DateTimeHelper.getInstance();
        open.set(Calendar.HOUR_OF_DAY, 8);
        open.set(Calendar.MINUTE, 0);
        Calendar close = DateTimeHelper.getInstance();
        close.set(Calendar.HOUR_OF_DAY, 17);
        close.set(Calendar.MINUTE, 30);
        List<Date> availableTimeSlots = new ArrayList<>();
        int i=0;
        // check for available timeslots between specified date and last event date.
        while (i<events.size() && availableTimeSlots.size()<max) {
            open.set(Calendar.DATE, cal.get(Calendar.DATE));
            close.set(Calendar.DATE, cal.get(Calendar.DATE));
            if (events.get(i).getEventEndDate().compareTo(cal.getTime()) <= 0) {
                i++;
            }
            else {
                if (!events.get(i).checkOverlap(cal.getTime(), duration) && !cal.getTime().before(open.getTime()) && !cal.getTime().after(close.getTime())) {
                    availableTimeSlots.add(cal.getTime());
                }
                cal.add(Calendar.MINUTE, duration);
            }
        }
        // fill remainding timeslots
        while (availableTimeSlots.size()<max) {
            open.set(Calendar.DATE, cal.get(Calendar.DATE));
            close.set(Calendar.DATE, cal.get(Calendar.DATE));
            if (!cal.getTime().before(open.getTime()) && !cal.getTime().after(close.getTime())) {
                availableTimeSlots.add(cal.getTime());
            }
            cal.add(Calendar.MINUTE, duration);
        }
        return availableTimeSlots;

    }

    /**
     * Returns a list of Date objects representing available timeslots starting from the specified startDate
     * that are not overlapping with any events in the schedule. The maximum number of available timeslots returned
     * is specified by max. The duration of each available timeslot is specified by duration.
     * @param startDate The Date object representing the start date to search for available timeslots.
     * @param duration The duration of each available timeslot in minutes.
     * @param max The maximum number of available timeslots to return.
     * @return A list of Date objects representing available timeslots starting from the specified startDate
     * that are not overlapping with any events in the schedule.
     */
    public List<Date> getAvailableTimeslots(Date startDate, int duration, int max) {
        Calendar cal = DateTimeHelper.getInstance();
        cal.setTime(startDate);
        if (cal.get(Calendar.MINUTE)>0 && cal.get(Calendar.MINUTE)<15) {
            cal.set(Calendar.MINUTE, 15);
        }
        else if (cal.get(Calendar.MINUTE)>15 && cal.get(Calendar.MINUTE)<30) {
            cal.set(Calendar.MINUTE, 30);
        }
        else if (cal.get(Calendar.MINUTE)>30 && cal.get(Calendar.MINUTE)<45) {
            cal.set(Calendar.MINUTE, 45);
        }
        else if (cal.get(Calendar.MINUTE)>45 && cal.get(Calendar.MINUTE)<60) {
            cal.setLenient(true);
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+1);
            cal.setLenient(false);
            cal.set(Calendar.MINUTE, 0);
        }
        Calendar open = DateTimeHelper.getInstance();
        open.setTime(startDate);
        open.set(Calendar.HOUR_OF_DAY, 8);
        open.set(Calendar.MINUTE, 0);
        Calendar close = DateTimeHelper.getInstance();
        close.setTime(startDate);
        close.set(Calendar.HOUR_OF_DAY, 17);
        close.set(Calendar.MINUTE, 30);
        List<Date> availableTimeSlots = new ArrayList<>();
        int i=0;
        while (i<events.size() && availableTimeSlots.size()<max) {
            open.set(Calendar.DATE, cal.get(Calendar.DATE));
            close.set(Calendar.DATE, cal.get(Calendar.DATE));
            if (events.get(i).getEventEndDate().compareTo(cal.getTime()) <= 0) {
                i++;
            }
            else {
                if (!events.get(i).checkOverlap(cal.getTime(), duration) && !cal.getTime().before(open.getTime()) && !cal.getTime().after(close.getTime())) {
                    availableTimeSlots.add(cal.getTime());
                }
                cal.add(Calendar.MINUTE, duration);
            }
        }
        while (availableTimeSlots.size()<max) {
            open.set(Calendar.DATE, cal.get(Calendar.DATE));
            close.set(Calendar.DATE, cal.get(Calendar.DATE));
            if (!cal.getTime().before(open.getTime()) && !cal.getTime().after(close.getTime())) {
                availableTimeSlots.add(cal.getTime());
            }
            cal.add(Calendar.MINUTE, duration);
        }
        return availableTimeSlots;

    }

    /**
     * Checks if a given date and time is available for a given duration in minutes.
     * The method first converts the given date and time to a Date object using
     * DateTimeHelper.stringToDateTime. It subtracts the given duration in minutes from the given date and time, 
     * and checks if the resulting date and time is available by calling getAvailableTimeslots.
     * If the resulting date and time is available, the method returns true, otherwise
     * it returns false.
     * @param date The date to check for availability.
     * @param time The time to check for availability.
     * @param duration The duration in minutes to check for availability.
     * @return True if the given date and time is available for the given duration, false otherwise.
     */
    public Boolean checkAvailableTimeslot(String date, String time, int duration) {
        Calendar cal = DateTimeHelper.getInstance();
        Date timeslot = DateTimeHelper.stringToDateTime(date, time);
        if (timeslot==null) {
            return false;
        }
        cal.setTime(timeslot);
        cal.add(Calendar.MINUTE, -duration);
        List<Date> availableSlots = getAvailableTimeslots(cal.getTime(), duration, 3);
        return (availableSlots.contains(timeslot));
    }

    // Assumption: dateTime is valid non-lenient Date
    public Boolean checkAvailableTimeslot(Date dateTime, int duration) {
        Calendar cal = DateTimeHelper.getInstance();
        cal.setTime(dateTime);
        cal.add(Calendar.MINUTE, -duration);
        List<Date> availableSlots = getAvailableTimeslots(cal.getTime(), duration, 3);
        return (availableSlots.contains(dateTime));
    }

    /**
     * Prints the details of an event at the given index in the schedule.
     * The method first checks if the given index is valid. If the index is
     * invalid or if the schedule is empty, the method prints an appropriate
     * error message and returns. Otherwise, the method prints the event name,
     * event description, event start time, event end time, and event duration
     * of the event at the given index.
     * @param index The index of the event to print.
     */
    public void printEvent(int index) {
        if (events.isEmpty()) {
            System.out.println("No available events found!");
            return;
        }
        else if (index<0 || index>=events.size()) {
            System.out.println("Event not found!");
            return;
        }
        Event event = events.get(index);
        System.out.println("Event Name: " + event.getEventName());
        System.out.println("Event Description: " + event.getEventDescription());
        System.out.println("Event Start Time: " + event.getEventStartString());
        System.out.println("Event End Time: " + event.getEventEndString());
        System.out.println("Duration: " + event.getEventDuration() + " Minutes");
    }
    
}
