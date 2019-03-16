package com.ronan.carpanion.entitites;

import java.io.Serializable;

public class User implements Serializable
{
    private String firstName;
    private String lastName;
    private String profileImage;
    //0 - Driver; 1 - Passenger
    private int userType;
    private int userScore;
    private String pushToken;

    public User(String firstName, String lastName, String profileImage, int userType, int userScore, String pushToken)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.userType = userType;
        this.userScore = userScore;
        this.setPushToken(pushToken);
    }

    public User()
    {

    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public int getUserType()
    {
        return userType;
    }

    public void setUserType(int userType)
    {
        this.userType = userType;
    }

    public String getProfileImage()
    {
        return profileImage;
    }

    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }

    public int getUserScore()
    {
        return userScore;
    }

    public void setUserScore(int userScore)
    {
        this.userScore = userScore;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
