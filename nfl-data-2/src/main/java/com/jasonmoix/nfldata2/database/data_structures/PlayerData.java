package com.jasonmoix.nfldata2.database.data_structures;

/**
 * Created by jmoix on 11/5/2015.
 */
public class PlayerData {

    public double[] scheduleData;
    public double[] oppScheduleData;
    public double[] playerData;
    public double[] offensiveData;
    public double[] defensiveData;
    public double fantasyPoints;

    public PlayerData(double[] scheduleData, double[] oppScheduleData, double[] playerData, double[] offensiveData, double[] defensiveData, double fantasyPoints){
        this.scheduleData = scheduleData;
        this.oppScheduleData = oppScheduleData;
        this.playerData = playerData;
        this.offensiveData = offensiveData;
        this.defensiveData = defensiveData;
        this.fantasyPoints = fantasyPoints;
    }

    public PlayerData(double[] scheduleData, double[] oppScheduleData, double[] playerData, double[] offensiveData, double[]defensiveData){
        this.scheduleData = scheduleData;
        this.oppScheduleData = oppScheduleData;
        this.playerData = playerData;
        this.offensiveData = offensiveData;
        this.defensiveData = defensiveData;
        this.fantasyPoints = 0;
    }

    public double[] toTrainingArray(){

        double[] data = new double[playerData.length + offensiveData.length + defensiveData.length + scheduleData.length + oppScheduleData.length + 1];
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

        for(int i = 0; i < oppScheduleData.length; i++){
            data[j++] = oppScheduleData[i];
        }

        data[j] = fantasyPoints;

        return data;
    }

    public double[] toPredictionArray(){

        double[] data = new double[playerData.length + offensiveData.length + defensiveData.length + scheduleData.length + oppScheduleData.length];
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

        for(int i = 0; i < oppScheduleData.length; i++){
            data[j++] = oppScheduleData[i];
        }

        return data;
    }

}
