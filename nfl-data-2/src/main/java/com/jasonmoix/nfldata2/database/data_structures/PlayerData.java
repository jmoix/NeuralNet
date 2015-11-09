package com.jasonmoix.nfldata2.database.data_structures;

/**
 * Created by jmoix on 11/5/2015.
 */
public class PlayerData {

    public double[] scheduleData;
    public double[] playerData;
    public double[] offensiveData;
    public double[] defensiveData;
    public double fantasyPoints;

    public PlayerData(double[] scheduleData, double[] playerData, double[] offensiveData, double[] defensiveData, double fantasyPoints){
        this.scheduleData = scheduleData;
        this.playerData = playerData;
        this.offensiveData = offensiveData;
        this.defensiveData = defensiveData;
        this.fantasyPoints = fantasyPoints;
    }

    public PlayerData(double[] scheduleData, double[] playerData, double[] offensiveData, double[]defensiveData){
        this.scheduleData = scheduleData;
        this.playerData = playerData;
        this.offensiveData = offensiveData;
        this.defensiveData = defensiveData;
        this.fantasyPoints = 0;
    }

    public double[] toTrainingArray(){

        double[] data = new double[playerData.length + offensiveData.length + defensiveData.length + scheduleData.length + 1];
        int j = 0;

        for(int i = 0; i < playerData.length; i++){
            data[j++] = playerData[i];
        }

        for(int i = 0; i < offensiveData.length; i++){
            data[j++] = offensiveData[i];
        }

        for(int i = 0; i < defensiveData.length; i++){
            data[j++] = defensiveData[i];
        }

        for(int i = 0; i < scheduleData.length; i++){
            data[j++] = scheduleData[i];
        }

        data[j] = fantasyPoints;

        return data;
    }

    public double[] toPredictionArray(){

        double[] data = new double[playerData.length + offensiveData.length + defensiveData.length + scheduleData.length];
        int j = 0;

        for(int i = 0; i < playerData.length; i++){
            data[j++] = playerData[i];
        }

        for(int i = 0; i < offensiveData.length; i++){
            data[j++] = offensiveData[i];
        }

        for(int i = 0; i < defensiveData.length; i++){
            data[j++] = defensiveData[i];
        }

        for(int i = 0; i < scheduleData.length; i++){
            data[j++] = scheduleData[i];
        }

        return data;
    }

}
