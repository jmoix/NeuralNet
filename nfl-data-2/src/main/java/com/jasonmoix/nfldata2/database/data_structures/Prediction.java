package com.jasonmoix.nfldata2.database.data_structures;

/**
 * Created by Souboro on 11/8/2015.
 */
public class Prediction {

    public String name;
    public String team;
    public String position;
    public String opponent;
    public double fantasyPoints;

    public Prediction(String name, String team, String position, String opponent, double fantasyPoints){
        this.name = name;
        this.team = team;
        this.position = position;
        this.opponent = opponent;
        this.fantasyPoints = fantasyPoints;
    }

    public boolean moreThan(Prediction b){
        return (fantasyPoints > b.fantasyPoints);
    }

}
