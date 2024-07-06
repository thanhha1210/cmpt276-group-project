package cmpt276.group.demo.models.event;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eid;

    private int capacity;
    private int duration;
    private String description;
    private Date date;

    @Column(name="event_code")
    private String eventCode;

    @Column(name="event_name")
    private String eventName;

    @Column(name="current_num")
    private int currentNum;

    @Column(name="start_time")
    private Time startTime;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> patients = new ArrayList<>();

    @Column(name="is_past")
    private boolean isPast;

    public Event() {}

    public Event(String eventCode, String eventName, int capacity, String description, Date date, Time startTime, int duration) {
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.capacity = capacity;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.currentNum = 0;
        this.patients = null;
        this.isPast = false;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getPatients() {
        return patients;
    }

    public void setPatients(List<String> patients) {
        this.patients = patients;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean isPast) {
        this.isPast = isPast;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

}
