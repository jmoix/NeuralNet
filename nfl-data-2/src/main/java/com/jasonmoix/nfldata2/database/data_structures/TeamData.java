package com.jasonmoix.nfldata2.database.data_structures;

/**
 * Created by jmoix on 11/5/2015.
 */
public class TeamData {

    public int home;
    public double[] offensiveData;
    public double[] defensiveData;
    public double fantasyPoints;

    public TeamData(int home, double[] offensiveData, double[] defensiveData, double fantasyPoints){
        this.home = home;
        this.offensiveData = offensiveData;
        this.defensiveData = defensiveData;
        this.fantasyPoints = fantasyPoints;
    }

    public TeamData(int home, double[] offensiveData, double[] defensiveData){
        this.home = home;
        this.offensiveData = offensiveData;
        this.defensiveData = defensiveData;
        this.fantasyPoints = 0;
    }

    public double[] toTrainingArray(){

        double[] data = new double[offensiveData.length + defensiveData.length + 2];
        int j = 0;

        for(int i = 0; i < offensiveData.length; i++){
            data[j++] = offensiveData[i];
        }

        for(int i = 0; i < defensiveData.length; i++){
            data[j++] = defensiveData[i];
        }

        data[j++] = home;

        data[j] = fantasyPoints;

        return data;
    }

    public double[] toPredictionArray(){

        double[] data = new double[offensiveData.length + defensiveData.length + 1];
        int j = 0;

        for(int i = 0; i < offensiveData.length; i++){
            data[j++] = offensiveData[i];
        }

        for(int i = 0; i < defensiveData.length; i++){
            data[j++] = defensiveData[i];
        }

        data[j] = home;

        return data;

    }

}
