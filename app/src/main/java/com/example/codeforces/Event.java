package com.example.codeforces;

/**
 * {@Event} represents an earthquake event. It holds the details
 * of that event such as title (which contains magnitude and location
 * of the earthquake), as well as time, and whether or not a tsunami
 * alert was issued during the earthquake.
 */
public class Event {

    public String handle,currentRank,maxRank,firstName,lastName;
    public int currentRating,maxRating;
    public Long lastOnline;

    public Event(String Handle,String CurrentRank,String MaxRank,String FirstName,String LastName,
                 int CurrentRating,int MaxRating,Long LastOnline) {
        handle=Handle;
                 currentRank=CurrentRank;
                 maxRank=MaxRank;
                 firstName=FirstName;
                 lastName=LastName;
                 currentRating=CurrentRating;
                 maxRating=MaxRating;
                 lastOnline=LastOnline;
    }
}