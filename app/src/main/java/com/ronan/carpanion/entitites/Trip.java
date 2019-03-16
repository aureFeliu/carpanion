package com.ronan.carpanion.entitites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.ServerValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class Trip implements Serializable
{
    private int tripID;
    private String driverID;
    private int numberOfPassengers;
    private String departureTime;
    private String passengerIDs;
    private String destinationLat;
    private String destinationLong;
    private String departureLat;
    private String departureLong;
    private String tripComplete;
    private ArrayList<String> passengers;
    @JsonProperty
    private Object timestamp;

    public Trip()
    {

    }

    public Trip(ArrayList<String> passengers)
    {
        this.setPassengers(passengers);
    }

    public Trip(String driverID, int numberOfPassengers, String departureTime, String destinationLat, String destinationLong, String departureLat, String departureLong)
    {
        this.driverID = driverID;
        this.numberOfPassengers = numberOfPassengers;
        this.departureTime = departureTime;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.departureLat = departureLat;
        this.departureLong = departureLong;
        this.passengers = new ArrayList<String>();
        this.setTimestamp(ServerValue.TIMESTAMP);
    }

    public Trip(String driverID, int numberOfPassengers, String departureTime, String destinationLat, String destinationLong, String departureLat, String departureLong, Object timestamp, ArrayList<String> passengers)
    {
        this.driverID = driverID;
        this.numberOfPassengers = numberOfPassengers;
        this.departureTime = departureTime;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.departureLat = departureLat;
        this.departureLong = departureLong;
        this.passengers = passengers;
        this.timestamp = timestamp;
    }

    public int getTripID()
    {
        return tripID;
    }

    public void setTripID(int tripID)
    {
        this.tripID = tripID;
    }

    public String getDriverID()
    {
        return driverID;
    }

    public void setDriverID(String driverID)
    {
        this.driverID = driverID;
    }

    public String getPassengerIDs()
    {
        return passengerIDs;
    }

    public void setPassengerIDs(String passengerIDs)
    {
        this.passengerIDs = passengerIDs;
    }

    public int getNumberOfPassengers()
    {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers)
    {
        this.numberOfPassengers = numberOfPassengers;
    }

    public String getDepartureTime()
    {
        return departureTime;
    }

    public void setDepartureTime(String departureTime)
    {
        this.departureTime = departureTime;
    }

    @JsonIgnore
    public Long getCreatedTimestamp() {
        if (getTimestamp() instanceof Long) {
            return (Long) getTimestamp();
        }
        else {
            return null;
        }
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(String destinationLat) {
        this.destinationLat = destinationLat;
    }

    public String getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(String destinationLong) {
        this.destinationLong = destinationLong;
    }

    public String getDepartureLat() {
        return departureLat;
    }

    public void setDepartureLat(String departureLat) {
        this.departureLat = departureLat;
    }

    public String getDepartureLong() {
        return departureLong;
    }

    public void setDepartureLong(String departureLong) {
        this.departureLong = departureLong;
    }

    public ArrayList<String> getPassengers()
    {
        return passengers;
    }

    public void setPassengers(ArrayList<String> passengers)
    {
        this.passengers = passengers;
    }

    public String getTripComplete() {
        return tripComplete;
    }

    public void setTripComplete(String tripComplete) {
        this.tripComplete = tripComplete;
    }
}
