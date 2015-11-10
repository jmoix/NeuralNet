package com.jasonmoix.nfldata2.database;

import com.jasonmoix.neuralnet.mlpquant.MLP;
import com.jasonmoix.nfldata2.database.tables.*;
import com.jasonmoix.nfldata2.database.data_structures.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by jmoix on 10/27/2015.
 */
public class Database {

    public static final int NAME_INDEX = 0;
    public static final int ID_INDEX = 1;
    public static final int TEAM_INDEX = 3;
    public static final int POSITION_INDEX = 4;
    public static final int SACK_INDEX = 8;
    public static final int FUM_INDEX = 10;
    public static final int FG_ATTEMPTS_INDEX = 15;
    public static final int FG_MADE_INDEX = 16;
    public static final int FG_YDS_INDEX = 17;
    public static final int XP_ATTEMPTS_INDEX = 19;
    public static final int XP_MADE_INDEX = 21;
    public static final int PA_ATTEMPTS_INDEX = 29;
    public static final int PA_CMP_INDEX = 30;
    public static final int INT_INDEX = 31;
    public static final int PA_TD_INDEX = 32;
    public static final int PA_YDS_INDEX = 35;
    public static final int REC_INDEX = 48;
    public static final int REC_YDS_INDEX = 52;
    public static final int REC_TDS_INDEX = 49;
    public static final int RU_ATTEMPTS_INDEX = 53;
    public static final int RU_TDS_INDEX = 56;
    public static final int RU_YDS_INDEX = 59;

    public static final String[] excludedPos = new String[]{"DE","CB","MLB","OLB","DB","DT","SS","FS","NT","ILB","LB","SAF"};
    public ArrayList<String> excludedFromTraining;
    public static final String UN = "root";
    public static final String PW = "xxxx";
    public static final String Address = "jdbc:mysql://localhost:3306/nfldata2";

    public static final int LAST_WEEK = 8;

    public enum positions{
        QB,
        RB,
        WR,
        TE,
        K,
        FB
    }

    private Connection connection;

    public Database(){

        try {
            connection = DriverManager.getConnection(Address, UN, PW);
            initExcluded();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void initExcluded(){
        excludedFromTraining = new ArrayList<>();
        excludedFromTraining.add("");
    }

    public void initData(){
        dropTables();
        createTables();
        parseTeamData();
        parseDataFiles();
        getTeamStats();
        updatePercentages();
        updateAverages();
    }

    public void close(){
        try{
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createTables(){

        try{
            Statement statement = connection.createStatement();
            System.out.println("Creating New Tables...");
            System.out.println("    " + Team.CREATE_TABLE);
            statement.execute(Team.CREATE_TABLE);
            System.out.println("    " + Schedule.CREATE_TABLE);
            statement.execute(Schedule.CREATE_TABLE);
            System.out.println("    " + Team_O_STATS.CREATE_TABLE);
            statement.execute(Team_O_STATS.CREATE_TABLE);
            System.out.println("    " + Team_D_STATS.CREATE_TABLE);
            statement.execute(Team_D_STATS.CREATE_TABLE);
            System.out.println("    " + RB.CREATE_TABLE);
            statement.execute(RB.CREATE_TABLE);
            System.out.println("    " + RB_STATS.CREATE_TABLE);
            statement.execute(RB_STATS.CREATE_TABLE);
            System.out.println("    " + TE.CREATE_TABLE);
            statement.execute(TE.CREATE_TABLE);
            System.out.println("    " + TE_STATS.CREATE_TABLE);
            statement.execute(TE_STATS.CREATE_TABLE);
            System.out.println("    " + WR.CREATE_TABLE);
            statement.execute(WR.CREATE_TABLE);
            System.out.println("    " + WR_STATS.CREATE_TABLE);
            statement.execute(WR_STATS.CREATE_TABLE);
            System.out.println("    " + QB.CREATE_TABLE);
            statement.execute(QB.CREATE_TABLE);
            System.out.println("    " + QB_STATS.CREATE_TABLE);
            statement.execute(QB_STATS.CREATE_TABLE);
            System.out.println("    " + K.CREATE_TABLE);
            statement.execute(K.CREATE_TABLE);
            System.out.println("    " + K_STATS.CREATE_TABLE);
            statement.execute(K_STATS.CREATE_TABLE);
            System.out.println("    " + DST.CREATE_TABLE);
            statement.execute(DST.CREATE_TABLE);
            System.out.println("    " + DST_STATS.CREATE_TABLE);
            statement.execute(DST_STATS.CREATE_TABLE);

            statement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void dropTables(){

        try {
            Statement statement = connection.createStatement();

            System.out.println("Dropping Previous Tables...");
            System.out.println("    " + DST_STATS.DROP_TABLE);
            statement.execute(DST_STATS.DROP_TABLE);
            System.out.println("    " + DST.DROP_TABLE);
            statement.execute(DST.DROP_TABLE);
            System.out.println("    " + K_STATS.DROP_TABLE);
            statement.execute(K_STATS.DROP_TABLE);
            System.out.println("    " + K.DROP_TABLE);
            statement.execute(K.DROP_TABLE);
            System.out.println("    " + QB_STATS.DROP_TABLE);
            statement.execute(QB_STATS.DROP_TABLE);
            System.out.println("    " + QB.DROP_TABLE);
            statement.execute(QB.DROP_TABLE);
            System.out.println("    " + WR_STATS.DROP_TABLE);
            statement.execute(WR_STATS.DROP_TABLE);
            System.out.println("    " + WR.DROP_TABLE);
            statement.execute(WR.DROP_TABLE);
            System.out.println("    " + TE_STATS.DROP_TABLE);
            statement.execute(TE_STATS.DROP_TABLE);
            System.out.println("    " + TE.DROP_TABLE);
            statement.execute(TE.DROP_TABLE);
            System.out.println("    " + RB_STATS.DROP_TABLE);
            statement.execute(RB_STATS.DROP_TABLE);
            System.out.println("    " + RB.DROP_TABLE);
            statement.execute(RB.DROP_TABLE);
            System.out.println("    " + Team_D_STATS.DROP_TABLE);
            statement.execute(Team_D_STATS.DROP_TABLE);
            System.out.println("    " + Team_O_STATS.DROP_TABLE);
            statement.execute(Team_O_STATS.DROP_TABLE);
            System.out.println("    " + Schedule.DROP_TABLE);
            statement.execute(Schedule.DROP_TABLE);
            System.out.println("    " + Team.DROP_TABLE);
            statement.execute(Team.DROP_TABLE);

            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public double[] getTeamOffensiveData(String teamId){

        double[] data = null;

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + Team.TABLE_NAME + " WHERE " + Team.ID + " ='" + teamId + "'");
            if(resultSet.first()){
                Team team = new Team(resultSet);
                data = team.getOffensiveData();
            }
            statement.close();
            resultSet.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return data;

    }

    public double[] getTeamDefensiveData(String teamId){

        double[] data = null;

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + Team.TABLE_NAME + " WHERE " + Team.ID + " ='" + teamId + "'");
            if(resultSet.first()){
                Team team = new Team(resultSet);
                data = team.getDefensiveData();
            }
            statement.close();
            resultSet.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return data;

    }

    public double[] getPlayerData(String tableName, String id){

        double[] data = null;

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet;

            switch (tableName){
                case K.TABLE_NAME:
                    resultSet = statement.executeQuery("SELECT * FROM " + K.TABLE_NAME + " WHERE " + K.ID + " ='" + id + "'");
                    if(resultSet.first()){
                        K player = new K(resultSet);
                        data = player.getData();
                    }
                    resultSet.close();
                    break;
                case QB.TABLE_NAME:
                    resultSet = statement.executeQuery("SELECT * FROM " + QB.TABLE_NAME + " WHERE " + QB.ID + " ='" + id + "'");
                    if(resultSet.first()){
                        QB player = new QB(resultSet);
                        data = player.getData();
                    }
                    resultSet.close();
                    break;
                case RB.TABLE_NAME:
                    resultSet = statement.executeQuery("SELECT * FROM " + RB.TABLE_NAME + " WHERE " + RB.ID + " ='" + id + "'");
                    if(resultSet.first()){
                        RB player = new RB(resultSet);
                        data = player.getData();
                    }
                    resultSet.close();
                    break;
                case TE.TABLE_NAME:
                    resultSet = statement.executeQuery("SELECT * FROM " + TE.TABLE_NAME + " WHERE " + TE.ID + " ='" + id + "'");
                    if(resultSet.first()){
                        TE player = new TE(resultSet);
                        data = player.getData();
                    }
                    resultSet.close();
                    break;
                case WR.TABLE_NAME:
                    resultSet = statement.executeQuery("SELECT * FROM " + WR.TABLE_NAME + " WHERE " + WR.ID + " ='" + id + "'");
                    if(resultSet.first()){
                        WR player = new WR(resultSet);
                        data = player.getData();
                    }
                    resultSet.close();
                    break;
            }

            statement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return data;

    }

    public double[][] getQBTrainingData(){

        double[][] trainingData = null;


        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + QB.TABLE_NAME + " INNER JOIN " + QB_STATS.TABLE_NAME +
                    " ON " + QB.TABLE_NAME + "." + QB.ID + "=" + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID);
            ArrayList<PlayerData> data = new ArrayList<>();

            while(resultSet.next()){
                QB player = new QB(resultSet);
                QB_STATS stats = new QB_STATS(resultSet);
                String opponent = getOpponent(player.team, resultSet.getInt(QB_STATS.WEEK));
                if(!opponent.equals("bye") && stats.played()){
                    data.add(new PlayerData(getScheduleData(player.team, resultSet.getInt(QB_STATS.WEEK)), getScheduleData(opponent, resultSet.getInt(QB_STATS.WEEK)), getPlayerData(QB.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent), stats.fantasyPoints()));
                }
            }

            trainingData = PlayerArrayListToDouble(data);

        }catch (SQLException e){
            e.printStackTrace();
        }


        return trainingData;

    }

    public double[][] getRBTrainingData(){

        double[][] trainingData = null;


        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + RB.TABLE_NAME + " INNER JOIN " + RB_STATS.TABLE_NAME +
                    " ON " + RB.TABLE_NAME + "." + RB.ID + "=" + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID);
            ArrayList<PlayerData> data = new ArrayList<>();

            while(resultSet.next()){
                RB player = new RB(resultSet);
                RB_STATS stats = new RB_STATS(resultSet);
                String opponent = getOpponent(player.team, resultSet.getInt(RB_STATS.WEEK));
                if(!opponent.equals("bye") && stats.played()){
                    data.add(new PlayerData(getScheduleData(player.team, resultSet.getInt(RB_STATS.WEEK)), getScheduleData(opponent, resultSet.getInt(RB_STATS.WEEK)), getPlayerData(RB.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent), stats.fantasyPoints()));
                }
            }

            trainingData = PlayerArrayListToDouble(data);

        }catch (SQLException e){
            e.printStackTrace();
        }


        return trainingData;

    }

    public double[][] getWRTrainingData(){

        double[][] trainingData = null;


        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + WR.TABLE_NAME + " INNER JOIN " + WR_STATS.TABLE_NAME +
                    " ON " + WR.TABLE_NAME + "." + WR.ID + "=" + WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID);
            ArrayList<PlayerData> data = new ArrayList<>();

            while(resultSet.next()){
                WR player = new WR(resultSet);
                WR_STATS stats = new WR_STATS(resultSet);
                String opponent = getOpponent(player.team, resultSet.getInt(WR_STATS.WEEK));
                if(!opponent.equals("bye") && stats.played()){
                    data.add(new PlayerData(getScheduleData(player.team, resultSet.getInt(WR_STATS.WEEK)), getScheduleData(opponent, resultSet.getInt(WR_STATS.WEEK)), getPlayerData(WR.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent), stats.fantasyPoints()));
                }
            }

            trainingData = PlayerArrayListToDouble(data);

        }catch (SQLException e){
            e.printStackTrace();
        }


        return trainingData;

    }

    public double[][] getTETrainingData(){

        double[][] trainingData = null;


        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TE.TABLE_NAME + " INNER JOIN " + TE_STATS.TABLE_NAME +
                    " ON " + TE.TABLE_NAME + "." + TE.ID + "=" + TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID);
            ArrayList<PlayerData> data = new ArrayList<>();

            while(resultSet.next()){
                TE player = new TE(resultSet);
                TE_STATS stats = new TE_STATS(resultSet);
                String opponent = getOpponent(player.team, resultSet.getInt(TE_STATS.WEEK));
                if(!opponent.equals("bye") && stats.played()){
                    data.add(new PlayerData(getScheduleData(player.team, resultSet.getInt(TE_STATS.WEEK)), getScheduleData(opponent, resultSet.getInt(TE_STATS.WEEK)), getPlayerData(TE.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent), stats.fantasyPoints()));
                }
            }

            trainingData = PlayerArrayListToDouble(data);

        }catch (SQLException e){
            e.printStackTrace();
        }


        return trainingData;

    }

    public double[][] getDefenseTrainingData(){

        double[][] trainingData = null;


        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + Team.TABLE_NAME + " INNER JOIN " + Team_D_STATS.TABLE_NAME +
                    " ON " + Team.TABLE_NAME + "." + Team.ID + "=" + Team_D_STATS.TABLE_NAME + "." + Team_D_STATS.TEAM_ID);
            ArrayList<TeamData> data = new ArrayList<>();

            while(resultSet.next()){
                Team team = new Team(resultSet);
                Team_D_STATS stats = new Team_D_STATS(resultSet);
                String opponent = getOpponent(team.id, resultSet.getInt(Team_D_STATS.WEEK));
                if(!opponent.equals("bye")){
                    data.add(new TeamData(getScheduleData(team.id, resultSet.getInt(Team_D_STATS.WEEK)), getScheduleData(opponent, resultSet.getInt(Team_D_STATS.WEEK)), getTeamOffensiveData(opponent), getTeamDefensiveData(team.id), stats.fantasyPoints()));
                }
            }

            trainingData = TeamArrayListToDouble(data);

        }catch (SQLException e){
            e.printStackTrace();
        }


        return trainingData;

    }

    public void getPredictions(){

        File output = new File("C:\\data\\output\\predictions.xlsx");
        ArrayList<Prediction> qb = sort(generateQBPredictions());
        ArrayList<Prediction> rb = sort(generateRBPredictions());
        ArrayList<Prediction> wr = sort(generateWRPredictions());
        ArrayList<Prediction> te = sort(generateTEPredictions());
        ArrayList<Prediction> dst = sort(generateDefensePredictions());

        Workbook workbook = new XSSFWorkbook();

        Sheet qbSheet = workbook.createSheet("QB");
        int rowIndex = 0;
        int cellIndex = 0;
        Row row = qbSheet.createRow(rowIndex++);
        row.createCell(cellIndex++).setCellValue("POSITION");
        row.createCell(cellIndex++).setCellValue("NAME");
        row.createCell(cellIndex++).setCellValue("TEAM");
        row.createCell(cellIndex++).setCellValue("OPPONENT");
        row.createCell(cellIndex).setCellValue("FANTASY RANK");

        for(Prediction prediction : qb){
            cellIndex = 0;
            row = qbSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(prediction.position);
            row.createCell(cellIndex++).setCellValue(prediction.name);
            row.createCell(cellIndex++).setCellValue(prediction.team);
            row.createCell(cellIndex++).setCellValue(prediction.opponent);
            row.createCell(cellIndex).setCellValue(prediction.fantasyPoints);
        }

        Sheet rbSheet = workbook.createSheet("RB");
        rowIndex = 0;
        cellIndex = 0;
        row = rbSheet.createRow(rowIndex++);
        row.createCell(cellIndex++).setCellValue("POSITION");
        row.createCell(cellIndex++).setCellValue("NAME");
        row.createCell(cellIndex++).setCellValue("TEAM");
        row.createCell(cellIndex++).setCellValue("OPPONENT");
        row.createCell(cellIndex).setCellValue("FANTASY RANK");

        for(Prediction prediction : rb){
            cellIndex = 0;
            row = rbSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(prediction.position);
            row.createCell(cellIndex++).setCellValue(prediction.name);
            row.createCell(cellIndex++).setCellValue(prediction.team);
            row.createCell(cellIndex++).setCellValue(prediction.opponent);
            row.createCell(cellIndex).setCellValue(prediction.fantasyPoints);
        }

        Sheet wrSheet = workbook.createSheet("WR");
        rowIndex = 0;
        cellIndex = 0;
        row = wrSheet.createRow(rowIndex++);
        row.createCell(cellIndex++).setCellValue("POSITION");
        row.createCell(cellIndex++).setCellValue("NAME");
        row.createCell(cellIndex++).setCellValue("TEAM");
        row.createCell(cellIndex++).setCellValue("OPPONENT");
        row.createCell(cellIndex).setCellValue("FANTASY RANK");

        for(Prediction prediction : wr){
            cellIndex = 0;
            row = wrSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(prediction.position);
            row.createCell(cellIndex++).setCellValue(prediction.name);
            row.createCell(cellIndex++).setCellValue(prediction.team);
            row.createCell(cellIndex++).setCellValue(prediction.opponent);
            row.createCell(cellIndex).setCellValue(prediction.fantasyPoints);
        }

        Sheet teSheet = workbook.createSheet("TE");
        rowIndex = 0;
        cellIndex = 0;
        row = teSheet.createRow(rowIndex++);
        row.createCell(cellIndex++).setCellValue("POSITION");
        row.createCell(cellIndex++).setCellValue("NAME");
        row.createCell(cellIndex++).setCellValue("TEAM");
        row.createCell(cellIndex++).setCellValue("OPPONENT");
        row.createCell(cellIndex).setCellValue("FANTASY RANK");

        for(Prediction prediction : te){
            cellIndex = 0;
            row = teSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(prediction.position);
            row.createCell(cellIndex++).setCellValue(prediction.name);
            row.createCell(cellIndex++).setCellValue(prediction.team);
            row.createCell(cellIndex++).setCellValue(prediction.opponent);
            row.createCell(cellIndex).setCellValue(prediction.fantasyPoints);
        }

        Sheet dstSheet = workbook.createSheet("DST");
        rowIndex = 0;
        cellIndex = 0;
        row = dstSheet.createRow(rowIndex++);
        row.createCell(cellIndex++).setCellValue("POSITION");
        row.createCell(cellIndex++).setCellValue("NAME");
        row.createCell(cellIndex++).setCellValue("TEAM");
        row.createCell(cellIndex++).setCellValue("OPPONENT");
        row.createCell(cellIndex).setCellValue("FANTASY RANK");

        for(Prediction prediction : dst){
            cellIndex = 0;
            row = dstSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(prediction.position);
            row.createCell(cellIndex++).setCellValue(prediction.name);
            row.createCell(cellIndex++).setCellValue(prediction.team);
            row.createCell(cellIndex++).setCellValue(prediction.opponent);
            row.createCell(cellIndex).setCellValue(prediction.fantasyPoints);
        }

        try {

            FileOutputStream out = new FileOutputStream(output);
            workbook.write(out);
            out.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public ArrayList<Prediction> sort(ArrayList<Prediction> predictions){

        for(int i = 1; i < predictions.size(); i++){
            Prediction prediction = predictions.get(i);
            int j = i - 1;
            while (j >= 0 && prediction.moreThan(predictions.get(j))){
                Prediction buffer = predictions.get(j);
                predictions.set(j, prediction);
                predictions.set(j + 1, buffer);
                j--;
            }
        }

        return predictions;
    }

    public ArrayList<Prediction> generateQBPredictions(){

        ArrayList<Prediction> predictions = new ArrayList<>();
        MLP mlp = new MLP(new File("C:\\data\\mlps\\qb.txt"));

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + QB.TABLE_NAME);

            while(resultSet.next()){
                QB player = new QB(resultSet);
                String opponent = getOpponent(player.team, LAST_WEEK + 1);
                if(!opponent.equals("bye")) {
                    double[] input = (new PlayerData(getScheduleData(player.team, LAST_WEEK + 1), getScheduleData(opponent, LAST_WEEK + 1), getPlayerData(QB.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent))).toPredictionArray();
                    predictions.add(new Prediction(player.name, player.team, "QB", opponent, mlp.predict(input)));
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return predictions;
    }

    public ArrayList<Prediction> generateRBPredictions(){

        ArrayList<Prediction> predictions = new ArrayList<>();
        MLP mlp = new MLP(new File("C:\\data\\mlps\\rb.txt"));

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + RB.TABLE_NAME);

            while(resultSet.next()){
                RB player = new RB(resultSet);
                String opponent = getOpponent(player.team, LAST_WEEK + 1);
                if(!opponent.equals("bye")) {
                    double[] input = (new PlayerData(getScheduleData(player.team, LAST_WEEK + 1), getScheduleData(opponent, LAST_WEEK + 1), getPlayerData(RB.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent))).toPredictionArray();
                    predictions.add(new Prediction(player.name, player.team, "RB", opponent, mlp.predict(input)));
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return predictions;

    }

    public ArrayList<Prediction> generateWRPredictions(){

        ArrayList<Prediction> predictions = new ArrayList<>();
        MLP mlp = new MLP(new File("C:\\data\\mlps\\wr.txt"));

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + WR.TABLE_NAME);

            while(resultSet.next()){
                WR player = new WR(resultSet);
                String opponent = getOpponent(player.team, LAST_WEEK + 1);
                if(!opponent.equals("bye")) {
                    double[] input = (new PlayerData(getScheduleData(player.team, LAST_WEEK + 1), getScheduleData(opponent, LAST_WEEK + 1), getPlayerData(WR.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent))).toPredictionArray();
                    predictions.add(new Prediction(player.name, player.team, "WR", opponent, mlp.predict(input)));
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return predictions;

    }

    public ArrayList<Prediction> generateTEPredictions(){

        ArrayList<Prediction> predictions = new ArrayList<>();
        MLP mlp = new MLP(new File("C:\\data\\mlps\\te.txt"));

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TE.TABLE_NAME);

            while(resultSet.next()){
                TE player = new TE(resultSet);
                String opponent = getOpponent(player.team, LAST_WEEK + 1);
                if(!opponent.equals("bye")) {
                    double[] input = (new PlayerData(getScheduleData(player.team, LAST_WEEK + 1), getScheduleData(opponent, LAST_WEEK + 1), getPlayerData(TE.TABLE_NAME, player.id), getTeamOffensiveData(player.team), getTeamDefensiveData(opponent))).toPredictionArray();
                    predictions.add(new Prediction(player.name, player.team, "TE", opponent, mlp.predict(input)));
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return predictions;

    }

    public ArrayList<Prediction> generateDefensePredictions(){

        ArrayList<Prediction> predictions = new ArrayList<>();
        MLP mlp = new MLP(new File("C:\\data\\mlps\\dst.txt"));

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + Team.TABLE_NAME);

            while(resultSet.next()){
                Team team = new Team(resultSet);
                String opponent = getOpponent(team.id, LAST_WEEK + 1);
                if(!opponent.equals("bye")) {
                    double[] input = (new TeamData(getScheduleData(team.id, LAST_WEEK + 1), getScheduleData(opponent, LAST_WEEK + 1), getTeamOffensiveData(opponent), getTeamDefensiveData(team.id))).toPredictionArray();
                    predictions.add(new Prediction(team.name, team.id, "DST", opponent, mlp.predict(input)));
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return predictions;

    }

    public double[][] PlayerArrayListToDouble(ArrayList<PlayerData> data){

        PlayerData playerData = data.get(0);

        double[][] trainingData = new double[data.size()][playerData.playerData.length + playerData.defensiveData.length + playerData.defensiveData.length + 1];

        for(int i = 0; i < data.size(); i++){
            trainingData[i] = data.get(i).toTrainingArray();
        }

        return trainingData;
    }

    public double[][] TeamArrayListToDouble(ArrayList<TeamData> data){

        TeamData teamData = data.get(0);

        double[][] trainingData = new double[data.size()][teamData.defensiveData.length + teamData.defensiveData.length + 1];

        for(int i = 0; i < data.size(); i++){
            trainingData[i] = data.get(i).toTrainingArray();
        }

        return trainingData;
    }

    public ArrayList<String> getExcludedPositions(){

        ArrayList<String> positions = new ArrayList<>();

        for(int i = 0; i < excludedPos.length; i++){
            positions.add(excludedPos[i]);
        }

        return positions;

    }

    public void parseDataFiles(){

        File dataDir = new File("C:\\data\\data");
        File[] dataFile = dataDir.listFiles();
        ArrayList<String> excludedPositions = getExcludedPositions();
        System.out.println("Fetching Player Data...");

        try{
            for(int i = 0; i < dataFile.length; i++){
                int week = Integer.parseInt(dataFile[i].getName().replace(".xlsx", ""));
                FileInputStream inputStream = new FileInputStream(dataFile[i]);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                while(rowIterator.hasNext()){
                    Row data = rowIterator.next();
                    QB qb;
                    QB_STATS qb_stats;
                    K k;
                    K_STATS k_stats;
                    RB rb;
                    RB_STATS rb_stats;
                    WR wr;
                    WR_STATS wr_stats;
                    TE te;
                    TE_STATS te_stats;
                    Cell position = data.getCell(POSITION_INDEX);

                    if(position != null) {
                        try {
                            switch (positions.valueOf(position.getStringCellValue())) {
                                case QB:
                                    qb = new QB(data);
                                    qb_stats = new QB_STATS(week,data);
                                    if(recordExists(QB.TABLE_NAME, qb.id)){
                                        qb_stats.insert(connection);
                                    }else{
                                        qb.insert(connection);
                                        qb_stats.insert(connection);
                                    }
                                    //qb.insert(connection);
                                    //System.out.println("QB");
                                    break;
                                case K:
                                    k = new K(data);
                                    k_stats = new K_STATS(week,data);
                                    if(recordExists(K.TABLE_NAME, k.id)){
                                        k_stats.insert(connection);
                                    }else{
                                        k.insert(connection);
                                        k_stats.insert(connection);
                                    }
                                    //System.out.println("K");
                                    break;
                                case RB:
                                    rb = new RB(data);
                                    rb_stats = new RB_STATS(week,data);
                                    if(recordExists(RB.TABLE_NAME, rb.id)){
                                        rb_stats.insert(connection);
                                    }else{
                                        rb.insert(connection);
                                        rb_stats.insert(connection);
                                    }
                                    //System.out.println("RB");
                                    break;
                                case FB:
                                    rb = new RB(data);
                                    rb_stats = new RB_STATS(week, data);
                                    if(recordExists(RB.TABLE_NAME, rb.id)){
                                        rb_stats.insert(connection);
                                    }else{
                                        rb.insert(connection);
                                        rb_stats.insert(connection);
                                    }
                                    //System.out.println("RB");
                                    break;
                                case WR:
                                    wr = new WR(data);
                                    wr_stats = new WR_STATS(week, data);
                                    if(recordExists(WR.TABLE_NAME, wr.id)){
                                        wr_stats.insert(connection);
                                    }else{
                                        wr.insert(connection);
                                        wr_stats.insert(connection);
                                    }
                                    //System.out.println("WR");
                                    break;
                                case TE:
                                    te = new TE(data);
                                    te_stats = new TE_STATS(week, data);
                                    if(recordExists(TE.TABLE_NAME, te.id)){
                                        te_stats.insert(connection);
                                    }else{
                                        te.insert(connection);
                                        te_stats.insert(connection);
                                    }
                                    //System.out.println("TE");
                                    break;
                            }
                        } catch (IllegalArgumentException e) {
                            //if(!excludedPositions.contains(data[POSITION_INDEX])) System.out.println("Excluded Position: " + data[POSITION_INDEX]);
                        }

                        if(excludedPositions.contains(position.getStringCellValue())){
                            DST dst = new DST(data);
                            DST_STATS dst_stats = new DST_STATS(week, data);
                            if(recordExists(DST.TABLE_NAME, dst.id)){
                                dst_stats.insert(connection);
                            }else{
                                dst.insert(connection);
                                dst_stats.insert(connection);
                            }
                        }
                    }

                }

                inputStream.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void getTeamStats(){

        System.out.println("Fetching Team Stats...");

        ArrayList<String> teamIds = getTeamIds();

        for(int i = 0; i < teamIds.size(); i++){
            for(int j = 1; j <= LAST_WEEK; j++){
                getOStats(teamIds.get(i), j);
                getDStats(teamIds.get(i), j);
            }
        }

    }

    public int numberOfGamesPlayed(String table, String id){

        int games = 0;

        try{

            Statement gameList = connection.createStatement();
            ResultSet data;

            switch (table){
                case QB.TABLE_NAME:
                    data = gameList.executeQuery("SELECT * FROM " + QB_STATS.TABLE_NAME +
                            " INNER JOIN " + QB.TABLE_NAME +
                            " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    while (data.next()){
                        String opponent = getOpponent(data.getString(QB.TEAM), data.getInt(QB_STATS.WEEK));
                        if(!opponent.equals("bye")){
                            QB_STATS stats = new QB_STATS(data);
                            if(stats.played()) games++;
                        }
                    }
                    data.close();
                    break;
                case RB.TABLE_NAME:
                    data = gameList.executeQuery("SELECT * FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME +
                            " ON " + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    while (data.next()){
                        String opponent = getOpponent(data.getString(RB.TEAM), data.getInt(RB_STATS.WEEK));
                        if(!opponent.equals("bye")){
                            RB_STATS stats = new RB_STATS(data);
                            if(stats.played()) games++;
                        }
                    }
                    data.close();
                    break;
                case WR.TABLE_NAME:
                    data = gameList.executeQuery("SELECT * FROM " + WR_STATS.TABLE_NAME +
                            " INNER JOIN " + WR.TABLE_NAME +
                            " ON " + WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "'");
                    while (data.next()){
                        String opponent = getOpponent(data.getString(WR.TEAM), data.getInt(WR_STATS.WEEK));
                        if(!opponent.equals("bye")){
                            WR_STATS stats = new WR_STATS(data);
                            if(stats.played()) games++;
                        }
                    }
                    data.close();
                    break;
                case TE.TABLE_NAME:
                    data = gameList.executeQuery("SELECT * FROM " + TE_STATS.TABLE_NAME +
                            " INNER JOIN " + TE.TABLE_NAME +
                            " ON " + TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "'");
                    while (data.next()){
                        String opponent = getOpponent(data.getString(TE.TEAM), data.getInt(TE_STATS.WEEK));
                        if(!opponent.equals("bye")){
                            TE_STATS stats = new TE_STATS(data);
                            if(stats.played()) games++;
                        }
                    }
                    data.close();
                    break;
                case K.TABLE_NAME:
                    data = gameList.executeQuery("SELECT * FROM " + K_STATS.TABLE_NAME +
                            " INNER JOIN " + K.TABLE_NAME +
                            " ON " + K_STATS.TABLE_NAME + "." + K_STATS.PLAYER_ID + "=" + K.TABLE_NAME + "." + K.ID +
                            " WHERE " + K_STATS.PLAYER_ID + "='" + id + "'");
                    while (data.next()){
                        String opponent = getOpponent(data.getString(K.TEAM), data.getInt(K_STATS.WEEK));
                        if(!opponent.equals("bye")){
                            K_STATS stats = new K_STATS(data);
                            if(stats.played()) games++;
                        }
                    }
                    data.close();
                    break;
                case Team.TABLE_NAME:
                    data = gameList.executeQuery("SELECT * FROM " + Schedule.TABLE_NAME +
                            " WHERE " + Schedule.ID + " LIKE '" + id + "%'");
                    while (data.next()){
                        String opponent = data.getString(Schedule.OPPONENT);
                        if(!opponent.equals("bye")){
                            games++;
                        }
                    }
                    data.close();
                    break;
            }

            gameList.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return games;

    }

    public String getOpponent(String teamId, int week){
        String sid = teamId + week;
        String opponent = null;

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + Schedule.OPPONENT + " FROM " + Schedule.TABLE_NAME + " WHERE " + Schedule.ID + " ='" + sid + "'");
            if(resultSet.first()){
                opponent = resultSet.getString(Schedule.OPPONENT);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return opponent;
    }

    public int getHomeAway(String teamId, int week){
        String sid = teamId + week;
        int home = 0;

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + Schedule.HOME + " FROM " + Schedule.TABLE_NAME + " WHERE " + Schedule.ID + " ='" + sid + "'");
            if(resultSet.first()){
                home = resultSet.getInt(Schedule.HOME);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return home;
    }

    public double[] getScheduleData(String teamId, int week){

        String sid = teamId + week;
        double[] data = null;

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + Schedule.TABLE_NAME + " WHERE " + Schedule.ID + " ='" + sid + "'");
            if(resultSet.first()){
                Schedule schedule = new Schedule(resultSet);
                data = schedule.getData();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return data;

    }

    public void updatePercentages(){
        updateQBPercentages();
        updateRBPercentages();
        updateWRPercentages();
        updateTEPercentages();
    }

    public void updateAverages(){
        updateQBAverages();
        updateRBAverages();
        updateWRAverages();
        updateTEAverages();
        updateKAverages();
        updateTeamAverages();
    }

    public void updateQBAverages(){

        try{

            Statement qbs = connection.createStatement();
            Statement dataQ = connection.createStatement();
            ResultSet qb = qbs.executeQuery("SELECT " + QB.ID + " FROM " + QB.TABLE_NAME);
            ResultSet data;

            while (qb.next()){

                String id = qb.getString(QB.ID);
                double games = numberOfGamesPlayed(QB.TABLE_NAME, id);
                double pa_attempts = 0;
                double pa_cmp = 0;
                double interceptions = 0;
                double fum = 0;
                double pa_td = 0;
                double pa_yds = 0;
                double pa_perc = 0;
                double ru_attempts = 0;
                double ru_tds = 0;
                double ru_yds = 0;
                double ru_perc = 0;

                if(games != 0) {
                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.PA_ATTEMPTS + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        pa_attempts = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.PA_CMP + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        pa_cmp = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.INT + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        interceptions = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.FUM + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fum = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.PA_TD + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        pa_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.PA_YDS + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        pa_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.PA_PERC + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        pa_perc = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.RU_ATTEMPTS + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_attempts = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.RU_TDS + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.RU_YDS + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + QB_STATS.RU_PERC + ") FROM " + QB_STATS.TABLE_NAME +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_perc = data.getInt(1);
                    }
                    data.close();

                    String updateStatement = "UPDATE " + QB.TABLE_NAME + " SET " +
                            QB.AVG_PA_ATTEMPTS + "=" + (pa_attempts / games) + "," +
                            QB.AVG_PA_CMP + "=" + (pa_cmp / games) + "," +
                            QB.AVG_INT + "=" + (interceptions / games) + "," +
                            QB.AVG_FUM + "=" + (fum / games) + "," +
                            QB.AVG_PA_TD + "=" + (pa_td / games) + "," +
                            QB.AVG_PA_YDS + "=" + (pa_yds / games) + "," +
                            QB.AVG_PA_PERC + "=" + (pa_perc / games) + "," +
                            QB.AVG_RU_ATTEMPTS + "=" + (ru_attempts / games) + "," +
                            QB.AVG_RU_TDS + "=" + (ru_tds / games) + "," +
                            QB.AVG_RU_YDS + "=" + (ru_yds / games) + "," +
                            QB.AVG_RU_PERC + "=" + (ru_perc / games) +
                            " WHERE " + QB.ID + "='" + id + "'";
                    System.out.println("    " + updateStatement);
                    dataQ.execute(updateStatement);
                }

            }

            qb.close();
            dataQ.close();
            qbs.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateRBAverages(){

        try{

            Statement players = connection.createStatement();
            Statement dataQ = connection.createStatement();
            ResultSet player = players.executeQuery("SELECT " + RB.ID + " FROM " + RB.TABLE_NAME);
            ResultSet data;

            while (player.next()){

                String id = player.getString(RB.ID);
                double games = numberOfGamesPlayed(RB.TABLE_NAME, id);
                double fum = 0;
                double ru_attempts = 0;
                double ru_tds = 0;
                double ru_yds = 0;
                double ru_perc = 0;
                double rec = 0;
                double rec_yds = 0;
                double rec_tds = 0;
                double rec_perc = 0;

                if(games != 0) {
                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.FUM + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fum = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.RU_ATTEMPTS + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_attempts = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.RU_TDS + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.RU_YDS + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.RU_PERC + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        ru_perc = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.REC + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.REC_YDS + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.REC_TDS + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + RB_STATS.REC_PERC + ") FROM " + RB_STATS.TABLE_NAME +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_perc = data.getInt(1);
                    }
                    data.close();

                    String updateStatement = "UPDATE " + RB.TABLE_NAME + " SET " +
                            RB.AVG_FUM + "=" + (fum / games) + "," +
                            RB.AVG_RU_ATTEMPTS + "=" + (ru_attempts / games) + "," +
                            RB.AVG_RU_TDS + "=" + (ru_tds / games) + "," +
                            RB.AVG_RU_YDS + "=" + (ru_yds / games) + "," +
                            RB.AVG_RU_PERC + "=" + (ru_perc / games) + "," +
                            RB.AVG_REC + "=" + (rec / games) + "," +
                            RB.AVG_REC_TDS + "=" + (rec_tds / games) + "," +
                            RB.AVG_REC_YDS + "=" + (rec_yds / games) + "," +
                            RB.AVG_REC_PERC + "=" + (rec_perc / games) +
                            " WHERE " + RB.ID + "='" + id + "'";
                    System.out.println("    " + updateStatement);
                    dataQ.execute(updateStatement);
                }

            }

            player.close();
            dataQ.close();
            players.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void updateWRAverages(){

        try{

            Statement players = connection.createStatement();
            Statement dataQ = connection.createStatement();
            ResultSet player = players.executeQuery("SELECT " + WR.ID + " FROM " + WR.TABLE_NAME);
            ResultSet data;

            while (player.next()){

                String id = player.getString(WR.ID);
                double games = numberOfGamesPlayed(WR.TABLE_NAME, id);
                double fum = 0;
                double rec = 0;
                double rec_yds = 0;
                double rec_tds = 0;
                double rec_perc = 0;

                if(games != 0) {
                    data = dataQ.executeQuery("SELECT SUM(" + WR_STATS.FUM + ") FROM " + WR_STATS.TABLE_NAME +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fum = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + WR_STATS.REC + ") FROM " + WR_STATS.TABLE_NAME +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + WR_STATS.REC_YDS + ") FROM " + WR_STATS.TABLE_NAME +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + WR_STATS.REC_TDS + ") FROM " + WR_STATS.TABLE_NAME +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + WR_STATS.REC_PERC + ") FROM " + WR_STATS.TABLE_NAME +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_perc = data.getInt(1);
                    }
                    data.close();

                    String updateStatement = "UPDATE " + WR.TABLE_NAME + " SET " +
                            WR.AVG_FUM + "=" + (fum / games) + "," +
                            WR.AVG_REC + "=" + (rec / games) + "," +
                            WR.AVG_REC_TDS + "=" + (rec_tds / games) + "," +
                            WR.AVG_REC_YDS + "=" + (rec_yds / games) + "," +
                            WR.AVG_REC_PERC + "=" + (rec_perc / games) +
                            " WHERE " + WR.ID + "='" + id + "'";
                    System.out.println("    " + updateStatement);
                    dataQ.execute(updateStatement);
                }

            }

            player.close();
            dataQ.close();
            players.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void updateTEAverages(){

        try{

            Statement players = connection.createStatement();
            Statement dataQ = connection.createStatement();
            ResultSet player = players.executeQuery("SELECT " + TE.ID + " FROM " + TE.TABLE_NAME);
            ResultSet data;

            while (player.next()){

                String id = player.getString(TE.ID);
                double games = numberOfGamesPlayed(TE.TABLE_NAME, id);
                double fum = 0;
                double rec = 0;
                double rec_yds = 0;
                double rec_tds = 0;
                double rec_perc = 0;

                if(games != 0) {
                    data = dataQ.executeQuery("SELECT SUM(" + TE_STATS.FUM + ") FROM " + TE_STATS.TABLE_NAME +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fum = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + TE_STATS.REC + ") FROM " + TE_STATS.TABLE_NAME +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + TE_STATS.REC_YDS + ") FROM " + TE_STATS.TABLE_NAME +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + TE_STATS.REC_TDS + ") FROM " + TE_STATS.TABLE_NAME +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + TE_STATS.REC_PERC + ") FROM " + TE_STATS.TABLE_NAME +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        rec_perc = data.getInt(1);
                    }
                    data.close();

                    String updateStatement = "UPDATE " + TE.TABLE_NAME + " SET " +
                            TE.AVG_FUM + "=" + (fum / games) + "," +
                            TE.AVG_REC + "=" + (rec / games) + "," +
                            TE.AVG_REC_TDS + "=" + (rec_tds / games) + "," +
                            TE.AVG_REC_YDS + "=" + (rec_yds / games) + "," +
                            TE.AVG_REC_PERC + "=" + (rec_perc / games) +
                            " WHERE " + TE.ID + "='" + id + "'";
                    System.out.println("    " + updateStatement);
                    dataQ.execute(updateStatement);
                }

            }

            player.close();
            dataQ.close();
            players.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void updateKAverages(){

        try{

            Statement players = connection.createStatement();
            Statement dataQ = connection.createStatement();
            ResultSet player = players.executeQuery("SELECT " + K.ID + " FROM " + K.TABLE_NAME);
            ResultSet data;

            while (player.next()){

                String id = player.getString(K.ID);
                double games = numberOfGamesPlayed(K.TABLE_NAME, id);
                double fg_attempts = 0;
                double fg_made = 0;
                double fg_yds = 0;
                double xp_attempts = 0;
                double xp_made = 0;

                if(games != 0) {
                    data = dataQ.executeQuery("SELECT SUM(" + K_STATS.FG_ATTEMPTS + ") FROM " + K_STATS.TABLE_NAME +
                            " WHERE " + K_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fg_attempts = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + K_STATS.FG_MADE + ") FROM " + K_STATS.TABLE_NAME +
                            " WHERE " + K_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fg_made = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + K_STATS.FG_YDS + ") FROM " + K_STATS.TABLE_NAME +
                            " WHERE " + K_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        fg_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + K_STATS.XP_ATTEMPTS + ") FROM " + K_STATS.TABLE_NAME +
                            " WHERE " + K_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        xp_attempts = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + K_STATS.XP_MADE + ") FROM " + K_STATS.TABLE_NAME +
                            " WHERE " + K_STATS.PLAYER_ID + "='" + id + "'");
                    if (data.first()) {
                        xp_made = data.getInt(1);
                    }
                    data.close();

                    String updateStatement = "UPDATE " + K.TABLE_NAME + " SET " +
                            K.AVG_FG_ATTEMPTS + "=" + (fg_attempts / games) + "," +
                            K.AVG_FG_YDS + "=" + (fg_yds / games) + "," +
                            K.AVG_FG_MADE + "=" + (fg_made / games) + "," +
                            K.AVG_XP_ATTEMPTS + "=" + (xp_attempts / games) + "," +
                            K.AVG_XP_MADE + "=" + (xp_made / games) +
                            " WHERE " + K.ID + "='" + id + "'";
                    System.out.println("    " + updateStatement);
                    dataQ.execute(updateStatement);
                }

            }

            player.close();
            dataQ.close();
            players.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void updateTeamAverages(){

        try{

            Statement players = connection.createStatement();
            Statement dataQ = connection.createStatement();
            ResultSet player = players.executeQuery("SELECT " + Team.ID + " FROM " + Team.TABLE_NAME);
            ResultSet data;

            while (player.next()){

                String id = player.getString(Team.ID);
                double games = numberOfGamesPlayed(Team.TABLE_NAME, id);
                double d_fum = 0;
                double d_sack = 0;
                double d_int = 0;
                double d_re_yds = 0;
                double d_ru_yds = 0;
                double d_pa_yds = 0;
                double d_tds = 0;
                double d_wr_re_yds = 0;
                double d_wr_td = 0;
                double d_te_re_yds = 0;
                double d_te_td = 0;
                double d_rb_re_yds = 0;
                double d_rb_ru_yds = 0;
                double d_rb_td = 0;
                double d_qb_ru_yds = 0;
                double d_qb_td = 0;
                double d_fg_yds = 0;
                double d_fg_m = 0;
                double o_fum = 0;
                double o_sack = 0;
                double o_int = 0;
                double o_re_yds = 0;
                double o_ru_yds = 0;
                double o_pa_yds = 0;
                double o_tds = 0;
                double o_wr_re_yds = 0;
                double o_wr_td = 0;
                double o_te_re_yds = 0;
                double o_te_td = 0;
                double o_rb_re_yds = 0;
                double o_rb_ru_yds = 0;
                double o_rb_td = 0;
                double o_qb_ru_yds = 0;
                double o_qb_td = 0;
                double o_fg_yds = 0;
                double o_fg_m = 0;

                if(games != 0) {
                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_FUM + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_fum = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_SACK + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_sack = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_INT + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_int = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_RE_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_RU_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_PA_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_pa_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_TDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_WR_RE_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_wr_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_WR_TD + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_wr_td = data.getInt(1);
                    }
                    data.close();
                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_TE_RE_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_te_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_TE_TD + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_te_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_RB_RE_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_rb_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_RB_RU_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_rb_ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_RB_TD + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_rb_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_QB_RU_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_qb_ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_QB_TD + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_qb_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_FG_YDS + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_fg_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_D_STATS.D_FG_M + ") FROM " + Team_D_STATS.TABLE_NAME +
                            " WHERE " + Team_D_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        d_fg_m = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_FUM + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_fum = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_SACK + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_sack = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_INT + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_int = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_RE_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_RU_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_PA_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_pa_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_TDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_tds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_WR_RE_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_wr_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_WR_TD + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_wr_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_TE_RE_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_te_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_TE_TD + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_te_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_RB_RE_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_rb_re_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_RB_RU_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_rb_ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_RB_TD + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_rb_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_QB_RU_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_qb_ru_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_QB_TD + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_qb_td = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_FG_YDS + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_fg_yds = data.getInt(1);
                    }
                    data.close();

                    data = dataQ.executeQuery("SELECT SUM(" + Team_O_STATS.O_FG_M + ") FROM " + Team_O_STATS.TABLE_NAME +
                            " WHERE " + Team_O_STATS.TEAM_ID + "='" + id + "'");
                    if (data.first()) {
                        o_fg_m = data.getInt(1);
                    }
                    data.close();

                    String updateStatement = "UPDATE " + Team.TABLE_NAME + " SET " +
                            Team.AVG_D_FUM + "=" + (d_fum/games) + "," +
                            Team.AVG_D_SACK + "=" + (d_sack/games) + "," +
                            Team.AVG_D_INT + "=" + (d_int/games) + "," +
                            Team.AVG_D_RE_YDS + "=" + (d_re_yds/games) + "," +
                            Team.AVG_D_RU_YDS + "=" + (d_ru_yds/games) + "," +
                            Team.AVG_D_PA_YDS + "=" + (d_pa_yds/games) + "," +
                            Team.AVG_D_TDS + "=" + (d_tds/games) + "," +
                            Team.AVG_D_WR_RE_YDS + "=" + (d_wr_re_yds/games) + "," +
                            Team.AVG_D_WR_TD + "=" + (d_wr_td/games) + "," +
                            Team.AVG_D_TE_RE_YDS + "=" + (d_te_re_yds/games) + "," +
                            Team.AVG_D_TE_TD + "=" + (d_te_td/games) + "," +
                            Team.AVG_D_RB_RE_YDS + "=" + (d_rb_re_yds/games) + "," +
                            Team.AVG_D_RB_RU_YDS + "=" + (d_rb_ru_yds/games) + "," +
                            Team.AVG_D_RB_TD + "=" + (d_rb_td/games) + "," +
                            Team.AVG_D_QB_RU_YDS + "=" + (d_qb_ru_yds/games) + "," +
                            Team.AVG_D_QB_TD + "=" + (d_qb_td/games) + "," +
                            Team.AVG_D_FG_YDS + "=" + (d_fg_yds/games) + "," +
                            Team.AVG_D_FG_M + "=" + (d_fg_m/games) + "," +
                            Team.AVG_O_FUM + "=" + (o_fum/games) + "," +
                            Team.AVG_O_SACK + "=" + (o_sack/games) + "," +
                            Team.AVG_O_INT + "=" + (o_int/games) + "," +
                            Team.AVG_O_RE_YDS + "=" + (o_re_yds/games) + "," +
                            Team.AVG_O_RU_YDS + "=" + (o_ru_yds/games) + "," +
                            Team.AVG_O_PA_YDS + "=" + (o_pa_yds/games) + "," +
                            Team.AVG_O_TDS + "=" + (o_tds/games) + "," +
                            Team.AVG_O_WR_RE_YDS + "=" + (o_wr_re_yds/games) + "," +
                            Team.AVG_O_WR_TD + "=" + (o_wr_td/games) + "," +
                            Team.AVG_O_TE_RE_YDS + "=" + (o_te_re_yds/games) + "," +
                            Team.AVG_O_TE_TD + "=" + (o_te_td/games) + "," +
                            Team.AVG_O_RB_RE_YDS + "=" + (o_rb_re_yds/games) + "," +
                            Team.AVG_O_RB_RU_YDS + "=" + (o_rb_ru_yds/games) + "," +
                            Team.AVG_O_RB_TD + "=" + (o_rb_td/games) + "," +
                            Team.AVG_O_QB_RU_YDS + "=" + (o_qb_ru_yds/games) + "," +
                            Team.AVG_O_QB_TD + "=" + (o_qb_td/games) + "," +
                            Team.AVG_O_FG_YDS + "=" + (o_fg_yds/games) + "," +
                            Team.AVG_O_FG_M + "=" + (o_fg_m/games) +
                            " WHERE " + Team.ID + "='" + id + "'";
                    System.out.println("    " + updateStatement);
                    dataQ.execute(updateStatement);
                }

            }

            player.close();
            dataQ.close();
            players.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateQBPercentages(){

        try{

            Statement playerList = connection.createStatement();
            Statement teamData = connection.createStatement();
            ResultSet players = playerList.executeQuery("SELECT " + QB.ID + "," + QB.TEAM + " FROM " + QB.TABLE_NAME);
            ResultSet data;

            while(players.next()){

                double pa_perc = 0;
                double ru_perc = 0;
                double pa = 0;
                double ru = 0;
                double total_pa = 0;
                double total_ru = 0;

                String id = players.getString(QB.ID);
                String team = players.getString(QB.TEAM);

                for(int i = 1; i <= LAST_WEEK; i++){

                    data = teamData.executeQuery("SELECT " + QB_STATS.TABLE_NAME + "." + QB_STATS.PA_ATTEMPTS + " FROM " + QB_STATS.TABLE_NAME +
                            " INNER JOIN " + QB.TABLE_NAME + " ON " +
                            QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                            " WHERE " + QB.TABLE_NAME + "." + QB.ID + " ='" + id + "' AND " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        pa = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.PA_ATTEMPTS + ") FROM " + QB_STATS.TABLE_NAME +
                            " INNER JOIN " + QB.TABLE_NAME + " ON " +
                            QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                            " WHERE " + QB.TABLE_NAME + "." + QB.TEAM + " ='" + team + "' AND " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_pa = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT " + QB_STATS.TABLE_NAME + "." + QB_STATS.RU_ATTEMPTS + " FROM " + QB_STATS.TABLE_NAME +
                            " INNER JOIN " + QB.TABLE_NAME + " ON " +
                            QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                            " WHERE " + QB.TABLE_NAME + "." + QB.ID + " ='" + id + "' AND " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        ru = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.RU_ATTEMPTS + ") FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.TEAM + " ='" + team + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_ru += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.RU_ATTEMPTS + ") FROM " + QB_STATS.TABLE_NAME +
                            " INNER JOIN " + QB.TABLE_NAME + " ON " +
                            QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                            " WHERE " + QB.TABLE_NAME + "." + QB.TEAM + " ='" + team + "' AND " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_ru += data.getInt(1);
                    }
                    data.close();

                    if(total_pa == 0){
                        pa_perc = 0;
                    }else {
                        pa_perc = pa/total_pa;
                    }

                    if(total_ru == 0){
                        ru_perc = 0;
                    }else{
                        ru_perc = ru/total_ru;
                    }

                    String updateStatement = "UPDATE " + QB_STATS.TABLE_NAME + " SET " + QB_STATS.PA_PERC + "=" + pa_perc + "," + QB_STATS.RU_PERC + "=" + ru_perc +
                            " WHERE " + QB_STATS.PLAYER_ID + "='" + id + "' AND " + QB_STATS.WEEK + "=" + i;
                    System.out.println("    " + updateStatement);
                    teamData.execute(updateStatement);
                }

            }

            teamData.close();
            players.close();
            playerList.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateRBPercentages(){

        try{

            Statement playerList = connection.createStatement();
            Statement teamData = connection.createStatement();
            ResultSet players = playerList.executeQuery("SELECT " + RB.ID + "," + RB.TEAM + " FROM " + RB.TABLE_NAME);
            ResultSet data;

            while(players.next()){

                double ru_perc = 0;
                double ru = 0;
                double total_ru = 0;
                double re_perc = 0;
                double re = 0;
                double total_re = 0;

                String id = players.getString(RB.ID);
                String team = players.getString(RB.TEAM);

                for(int i = 1; i <= LAST_WEEK; i++){


                    data = teamData.executeQuery("SELECT " + RB_STATS.TABLE_NAME + "." + RB_STATS.RU_ATTEMPTS + " FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.ID + " ='" + id + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        ru = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.RU_ATTEMPTS + ") FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.TEAM + " ='" + team + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_ru += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.RU_ATTEMPTS + ") FROM " + QB_STATS.TABLE_NAME +
                            " INNER JOIN " + QB.TABLE_NAME + " ON " +
                            QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                            " WHERE " + QB.TABLE_NAME + "." + QB.TEAM + " ='" + team + "' AND " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_ru += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT " + RB_STATS.TABLE_NAME + "." + RB_STATS.REC + " FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.ID + " ='" + id + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        re = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.REC + ") FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.TEAM + " ='" + team + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + WR_STATS.TABLE_NAME + "." + WR_STATS.REC + ") FROM " + WR_STATS.TABLE_NAME +
                            " INNER JOIN " + WR.TABLE_NAME + " ON " +
                            WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                            " WHERE " + WR.TABLE_NAME + "." + WR.TEAM + " ='" + team + "' AND " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + TE_STATS.TABLE_NAME + "." + TE_STATS.REC + ") FROM " + TE_STATS.TABLE_NAME +
                            " INNER JOIN " + TE.TABLE_NAME + " ON " +
                            TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                            " WHERE " + TE.TABLE_NAME + "." + TE.TEAM + " ='" + team + "' AND " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    if(total_re == 0){
                        re_perc = 0;
                    }else{
                        re_perc = re/total_re;
                    }

                    if(total_ru == 0){
                        ru_perc = 0;
                    }else{
                        ru_perc = ru/total_ru;
                    }

                    String updateStatement = "UPDATE " + RB_STATS.TABLE_NAME + " SET " + RB_STATS.RU_PERC + "=" + ru_perc + "," + RB_STATS.REC_PERC + "=" + re_perc +
                            " WHERE " + RB_STATS.PLAYER_ID + "='" + id + "' AND " + RB_STATS.WEEK + "=" + i;
                    System.out.println("    " + updateStatement);
                    teamData.execute(updateStatement);
                }

            }

            teamData.close();
            players.close();
            playerList.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void updateWRPercentages(){

        try{

            Statement playerList = connection.createStatement();
            Statement teamData = connection.createStatement();
            ResultSet players = playerList.executeQuery("SELECT " + WR.ID + "," + WR.TEAM + " FROM " + WR.TABLE_NAME);
            ResultSet data;

            while(players.next()){

                String id = players.getString(WR.ID);
                String team = players.getString(WR.TEAM);

                for(int i = 1; i <= LAST_WEEK; i++){

                    double re_perc = 0;
                    double re = 0;
                    double total_re = 0;

                    data = teamData.executeQuery("SELECT " + WR_STATS.TABLE_NAME + "." + WR_STATS.REC + " FROM " + WR_STATS.TABLE_NAME +
                            " INNER JOIN " + WR.TABLE_NAME + " ON " +
                            WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                            " WHERE " + WR.TABLE_NAME + "." + WR.ID + " ='" + id + "' AND " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + i);
                    if(data.first()){
                        re = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.REC + ") FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.TEAM + " ='" + team + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + WR_STATS.TABLE_NAME + "." + WR_STATS.REC + ") FROM " + WR_STATS.TABLE_NAME +
                            " INNER JOIN " + WR.TABLE_NAME + " ON " +
                            WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                            " WHERE " + WR.TABLE_NAME + "." + WR.TEAM + " ='" + team + "' AND " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + TE_STATS.TABLE_NAME + "." + TE_STATS.REC + ") FROM " + TE_STATS.TABLE_NAME +
                            " INNER JOIN " + TE.TABLE_NAME + " ON " +
                            TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                            " WHERE " + TE.TABLE_NAME + "." + TE.TEAM + " ='" + team + "' AND " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    if(total_re == 0){
                        re_perc = 0;
                    }else{
                        re_perc = re/total_re;
                    }

                    String updateStatement = "UPDATE " + WR_STATS.TABLE_NAME + " SET " + WR_STATS.REC_PERC + "=" + re_perc +
                            " WHERE " + WR_STATS.PLAYER_ID + "='" + id + "' AND " + WR_STATS.WEEK + "=" + i;
                    System.out.println("    " + updateStatement);
                    teamData.execute(updateStatement);
                }

            }

            teamData.close();
            players.close();
            playerList.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void updateTEPercentages(){

        try{

            Statement playerList = connection.createStatement();
            Statement teamData = connection.createStatement();
            ResultSet players = playerList.executeQuery("SELECT " + TE.ID + "," + TE.TEAM + " FROM " + TE.TABLE_NAME);
            ResultSet data;

            while(players.next()){

                String id = players.getString(TE.ID);
                String team = players.getString(TE.TEAM);

                for(int i = 1; i <= LAST_WEEK; i++){

                    double re_perc = 0;
                    double re = 0;
                    double total_re = 0;


                    data = teamData.executeQuery("SELECT " + TE_STATS.TABLE_NAME + "." + TE_STATS.REC + " FROM " + TE_STATS.TABLE_NAME +
                            " INNER JOIN " + TE.TABLE_NAME + " ON " +
                            TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                            " WHERE " + TE.TABLE_NAME + "." + TE.ID + " ='" + id + "' AND " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + i);
                    if(data.first()){
                        re = data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.REC + ") FROM " + RB_STATS.TABLE_NAME +
                            " INNER JOIN " + RB.TABLE_NAME + " ON " +
                            RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                            " WHERE " + RB.TABLE_NAME + "." + RB.TEAM + " ='" + team + "' AND " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + WR_STATS.TABLE_NAME + "." + WR_STATS.REC + ") FROM " + WR_STATS.TABLE_NAME +
                            " INNER JOIN " + WR.TABLE_NAME + " ON " +
                            WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                            " WHERE " + WR.TABLE_NAME + "." + WR.TEAM + " ='" + team + "' AND " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    data = teamData.executeQuery("SELECT SUM(" + TE_STATS.TABLE_NAME + "." + TE_STATS.REC + ") FROM " + TE_STATS.TABLE_NAME +
                            " INNER JOIN " + TE.TABLE_NAME + " ON " +
                            TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                            " WHERE " + TE.TABLE_NAME + "." + TE.TEAM + " ='" + team + "' AND " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + i);
                    if(data.first()){
                        total_re += data.getInt(1);
                    }
                    data.close();

                    if(total_re == 0){
                        re_perc = 0;
                    }else{
                        re_perc = re/total_re;
                    }

                    String updateStatement = "UPDATE " + TE_STATS.TABLE_NAME + " SET " + TE_STATS.REC_PERC + "=" + re_perc +
                            " WHERE " + TE_STATS.PLAYER_ID + "='" + id + "' AND " + TE_STATS.WEEK + "=" + i;
                    System.out.println("    " + updateStatement);
                    teamData.execute(updateStatement);
                }

            }

            teamData.close();
            players.close();
            playerList.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void getDStats(String teamId, int week){

        int d_fum = 0;
        int d_sack = 0;
        int d_int = 0;
        int d_re_yds = 0;
        int d_ru_yds = 0;
        int d_pa_yds = 0;
        int d_tds = 0;
        int d_wr_re_yds = 0;
        int d_wr_td = 0;
        int d_te_re_yds = 0;
        int d_te_td = 0;
        int d_rb_re_yds = 0;
        int d_rb_ru_yds = 0;
        int d_rb_td = 0;
        int d_qb_ru_yds = 0;
        int d_qb_td = 0;
        int d_fg_yds = 0;
        int d_fg_m = 0;

        String opponent = getOpponent(teamId, week);

        if(opponent != "bye"){

            try{
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT " +
                    Team_O_STATS.O_FUM + "," +
                    Team_O_STATS.O_SACK + "," +
                    Team_O_STATS.O_INT + "," +
                    Team_O_STATS.O_RE_YDS + "," +
                    Team_O_STATS.O_RU_YDS + "," +
                    Team_O_STATS.O_PA_YDS + "," +
                    Team_O_STATS.O_TDS + "," +
                    Team_O_STATS.O_WR_RE_YDS + "," +
                    Team_O_STATS.O_WR_TD + "," +
                    Team_O_STATS.O_TE_RE_YDS + "," +
                    Team_O_STATS.O_TE_TD + "," +
                    Team_O_STATS.O_RB_RE_YDS + "," +
                    Team_O_STATS.O_RB_RU_YDS + "," +
                    Team_O_STATS.O_RB_TD + "," +
                    Team_O_STATS.O_QB_RU_YDS + "," +
                    Team_O_STATS.O_QB_TD + "," +
                    Team_O_STATS.O_FG_YDS + "," +
                    Team_O_STATS.O_FG_M + " FROM " + Team_O_STATS.TABLE_NAME + " WHERE " + Team_O_STATS.WEEK + "=" + week +
                        " AND " + Team_O_STATS.TEAM_ID + " ='" + opponent + "'");

                if(resultSet.first()){

                    d_fum = Integer.parseInt(resultSet.getString(Team_O_STATS.O_FUM));
                    d_sack = Integer.parseInt(resultSet.getString(Team_O_STATS.O_SACK));
                    d_int = Integer.parseInt(resultSet.getString(Team_O_STATS.O_INT));
                    d_re_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_RE_YDS));
                    d_ru_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_RU_YDS));
                    d_pa_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_PA_YDS));
                    d_tds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_TDS));
                    d_wr_re_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_WR_RE_YDS));
                    d_wr_td = Integer.parseInt(resultSet.getString(Team_O_STATS.O_WR_TD));
                    d_te_re_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_TE_RE_YDS));
                    d_te_td = Integer.parseInt(resultSet.getString(Team_O_STATS.O_TE_TD));
                    d_rb_re_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_RB_RE_YDS));
                    d_rb_ru_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_RB_RU_YDS));
                    d_rb_td = Integer.parseInt(resultSet.getString(Team_O_STATS.O_RB_TD));
                    d_qb_ru_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_QB_RU_YDS));
                    d_qb_td = Integer.parseInt(resultSet.getString(Team_O_STATS.O_QB_TD));
                    d_fg_yds = Integer.parseInt(resultSet.getString(Team_O_STATS.O_FG_YDS));
                    d_fg_m = Integer.parseInt(resultSet.getString(Team_O_STATS.O_FG_M));

                }

                resultSet.close();
                statement.close();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }

        HashMap<String, Object> contentValues = new HashMap<>();
        contentValues.put(Team_D_STATS.TEAM_ID,teamId);
        contentValues.put(Team_D_STATS.WEEK, week);
        contentValues.put(Team_D_STATS.D_FUM, d_fum);
        contentValues.put(Team_D_STATS.D_INT,d_int);
        contentValues.put(Team_D_STATS.D_SACK,d_sack);
        contentValues.put(Team_D_STATS.D_RB_RE_YDS,d_rb_re_yds);
        contentValues.put(Team_D_STATS.D_WR_RE_YDS,d_wr_re_yds);
        contentValues.put(Team_D_STATS.D_TE_RE_YDS,d_te_re_yds);
        contentValues.put(Team_D_STATS.D_RE_YDS,d_re_yds);
        contentValues.put(Team_D_STATS.D_RB_RU_YDS,d_rb_ru_yds);
        contentValues.put(Team_D_STATS.D_QB_RU_YDS,d_qb_ru_yds);
        contentValues.put(Team_D_STATS.D_RU_YDS,d_ru_yds);
        contentValues.put(Team_D_STATS.D_PA_YDS,d_pa_yds);
        contentValues.put(Team_D_STATS.D_QB_TD,d_qb_td);
        contentValues.put(Team_D_STATS.D_RB_TD,d_rb_td);
        contentValues.put(Team_D_STATS.D_WR_TD,d_wr_td);
        contentValues.put(Team_D_STATS.D_TE_TD, d_te_td);
        contentValues.put(Team_D_STATS.D_TDS, d_tds);
        contentValues.put(Team_D_STATS.D_FG_YDS, d_fg_yds);
        contentValues.put(Team_D_STATS.D_FG_M, d_fg_m);
        Team_D_STATS stats = new Team_D_STATS(contentValues);
        stats.insert(connection);

    }

    public void getOStats(String teamId, int week){

        int o_fum = 0;
        int o_sack = 0;
        int o_int = 0;
        int o_re_yds = 0;
        int o_ru_yds = 0;
        int o_pa_yds = 0;
        int o_tds = 0;
        int o_wr_re_yds = 0;
        int o_wr_td = 0;
        int o_te_re_yds = 0;
        int o_te_td = 0;
        int o_rb_re_yds = 0;
        int o_rb_ru_yds = 0;
        int o_rb_td = 0;
        int o_qb_ru_yds = 0;
        int o_qb_td = 0;
        int o_fg_yds = 0;
        int o_fg_m = 0;

        try{

            Statement statement = connection.createStatement();
            ResultSet data;
            String sqlString;
            String opponent = getOpponent(teamId, week);

            //Get Sacks
            sqlString = "SELECT SUM(" + DST_STATS.TABLE_NAME + "." + DST_STATS.SACKS + ") FROM " + DST_STATS.TABLE_NAME +
                    " INNER JOIN " + DST.TABLE_NAME + " ON " + DST_STATS.TABLE_NAME + "." + DST_STATS.PLAYER_ID + "=" + DST.TABLE_NAME + "." + DST.ID +
                    " WHERE " + DST_STATS.TABLE_NAME + "." + DST_STATS.WEEK + "=" + week + " AND " + DST.TABLE_NAME + "." + DST.TEAM + "='" + opponent + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_sack += data.getInt(1);
            }
            data.close();

            //Get Fumbles
            sqlString = "SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.FUM + ") FROM " + QB_STATS.TABLE_NAME +
                    " INNER JOIN " + QB.TABLE_NAME + " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                    " WHERE " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + week + " AND " + QB.TABLE_NAME + "." + QB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_fum += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.FUM + ") FROM " + RB_STATS.TABLE_NAME +
                    " INNER JOIN " + RB.TABLE_NAME + " ON " + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                    " WHERE " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + week + " AND " + RB.TABLE_NAME + "." + RB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_fum += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + WR_STATS.TABLE_NAME + "." + WR_STATS.FUM + ") FROM " + WR_STATS.TABLE_NAME +
                    " INNER JOIN " + WR.TABLE_NAME + " ON " + WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                    " WHERE " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + week + " AND " + WR.TABLE_NAME + "." + WR.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_fum += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + TE_STATS.TABLE_NAME + "." + TE_STATS.FUM + ") FROM " + TE_STATS.TABLE_NAME +
                    " INNER JOIN " + TE.TABLE_NAME + " ON " + TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                    " WHERE " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + week + " AND " + TE.TABLE_NAME + "." + TE.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_fum += data.getInt(1);
            }
            data.close();

            //Get Interceptions
            sqlString = "SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.INT + ") FROM " + QB_STATS.TABLE_NAME +
                    " INNER JOIN " + QB.TABLE_NAME + " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                    " WHERE " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + week + " AND " + QB.TABLE_NAME + "." + QB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_int += data.getInt(1);
            }
            data.close();

            //Get Receiving Yards
            sqlString = "SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.REC_YDS + ") FROM " + RB_STATS.TABLE_NAME +
                    " INNER JOIN " + RB.TABLE_NAME + " ON " + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                    " WHERE " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + week + " AND " + RB.TABLE_NAME + "." + RB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_rb_re_yds += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + WR_STATS.TABLE_NAME + "." + WR_STATS.REC_YDS + ") FROM " + WR_STATS.TABLE_NAME +
                    " INNER JOIN " + WR.TABLE_NAME + " ON " + WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                    " WHERE " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + week + " AND " + WR.TABLE_NAME + "." + WR.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_wr_re_yds += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + TE_STATS.TABLE_NAME + "." + TE_STATS.REC_YDS + ") FROM " + TE_STATS.TABLE_NAME +
                    " INNER JOIN " + TE.TABLE_NAME + " ON " + TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                    " WHERE " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + week + " AND " + TE.TABLE_NAME + "." + TE.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_te_re_yds += data.getInt(1);
            }
            data.close();

            //Get Rushing Yards
            sqlString = "SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.RU_YDS + ") FROM " + RB_STATS.TABLE_NAME +
                    " INNER JOIN " + RB.TABLE_NAME + " ON " + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                    " WHERE " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + week + " AND " + RB.TABLE_NAME + "." + RB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_rb_ru_yds += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.RU_YDS + ") FROM " + QB_STATS.TABLE_NAME +
                    " INNER JOIN " + QB.TABLE_NAME + " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                    " WHERE " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + week + " AND " + QB.TABLE_NAME + "." + QB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_qb_ru_yds += data.getInt(1);
            }
            data.close();

            //Passing Yards
            sqlString = "SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.PA_YDS + ") FROM " + QB_STATS.TABLE_NAME +
                    " INNER JOIN " + QB.TABLE_NAME + " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                    " WHERE " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + week + " AND " + QB.TABLE_NAME + "." + QB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_pa_yds += data.getInt(1);
            }
            data.close();

            //Touchdowns
            sqlString = "SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.PA_TD + ") FROM " + QB_STATS.TABLE_NAME +
                    " INNER JOIN " + QB.TABLE_NAME + " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                    " WHERE " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + week + " AND " + QB.TABLE_NAME + "." + QB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_qb_td += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + QB_STATS.TABLE_NAME + "." + QB_STATS.RU_TDS + ") FROM " + QB_STATS.TABLE_NAME +
                    " INNER JOIN " + QB.TABLE_NAME + " ON " + QB_STATS.TABLE_NAME + "." + QB_STATS.PLAYER_ID + "=" + QB.TABLE_NAME + "." + QB.ID +
                    " WHERE " + QB_STATS.TABLE_NAME + "." + QB_STATS.WEEK + "=" + week + " AND " + QB.TABLE_NAME + "." + QB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_qb_td += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.REC_TDS + ") FROM " + RB_STATS.TABLE_NAME +
                    " INNER JOIN " + RB.TABLE_NAME + " ON " + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                    " WHERE " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + week + " AND " + RB.TABLE_NAME + "." + RB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_rb_td += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + RB_STATS.TABLE_NAME + "." + RB_STATS.RU_TDS + ") FROM " + RB_STATS.TABLE_NAME +
                    " INNER JOIN " + RB.TABLE_NAME + " ON " + RB_STATS.TABLE_NAME + "." + RB_STATS.PLAYER_ID + "=" + RB.TABLE_NAME + "." + RB.ID +
                    " WHERE " + RB_STATS.TABLE_NAME + "." + RB_STATS.WEEK + "=" + week + " AND " + RB.TABLE_NAME + "." + RB.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_rb_td += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + WR_STATS.TABLE_NAME + "." + WR_STATS.REC_TDS + ") FROM " + WR_STATS.TABLE_NAME +
                    " INNER JOIN " + WR.TABLE_NAME + " ON " + WR_STATS.TABLE_NAME + "." + WR_STATS.PLAYER_ID + "=" + WR.TABLE_NAME + "." + WR.ID +
                    " WHERE " + WR_STATS.TABLE_NAME + "." + WR_STATS.WEEK + "=" + week + " AND " + WR.TABLE_NAME + "." + WR.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_wr_td += data.getInt(1);
            }
            data.close();

            sqlString = "SELECT SUM(" + TE_STATS.TABLE_NAME + "." + TE_STATS.REC_TDS + ") FROM " + TE_STATS.TABLE_NAME +
                    " INNER JOIN " + TE.TABLE_NAME + " ON " + TE_STATS.TABLE_NAME + "." + TE_STATS.PLAYER_ID + "=" + TE.TABLE_NAME + "." + TE.ID +
                    " WHERE " + TE_STATS.TABLE_NAME + "." + TE_STATS.WEEK + "=" + week + " AND " + TE.TABLE_NAME + "." + TE.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_te_td += data.getInt(1);
            }
            data.close();

            //Field Goal Yards
            sqlString = "SELECT SUM(" + K_STATS.TABLE_NAME + "." + K_STATS.FG_YDS + ") FROM " + K_STATS.TABLE_NAME +
                    " INNER JOIN " + K.TABLE_NAME + " ON " + K_STATS.TABLE_NAME + "." + K_STATS.PLAYER_ID + "=" + K.TABLE_NAME + "." + K.ID +
                    " WHERE " + K_STATS.TABLE_NAME + "." + K_STATS.WEEK + "=" + week + " AND " + K.TABLE_NAME + "." + K.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_fg_yds += data.getInt(1);
            }
            data.close();

            //Field Goals Made
            sqlString = "SELECT SUM(" + K_STATS.TABLE_NAME + "." + K_STATS.FG_MADE + ") FROM " + K_STATS.TABLE_NAME +
                    " INNER JOIN " + K.TABLE_NAME + " ON " + K_STATS.TABLE_NAME + "." + K_STATS.PLAYER_ID + "=" + K.TABLE_NAME + "." + K.ID +
                    " WHERE " + K_STATS.TABLE_NAME + "." + K_STATS.WEEK + "=" + week + " AND " + K.TABLE_NAME + "." + K.TEAM + "='" + teamId + "'";
            //System.out.println(sqlString);
            data = statement.executeQuery(sqlString);
            if(data.first()){
                o_fg_m += data.getInt(1);
            }
            data.close();

            o_tds = o_qb_td + o_rb_td + o_wr_td + o_te_td;
            o_re_yds = o_rb_re_yds + o_wr_re_yds + o_te_re_yds;
            o_ru_yds = o_rb_ru_yds + o_qb_ru_yds;

            HashMap<String, Object> contentValues = new HashMap<>();
            contentValues.put(Team_O_STATS.TEAM_ID,teamId);
            contentValues.put(Team_O_STATS.WEEK, week);
            contentValues.put(Team_O_STATS.O_FUM, o_fum);
            contentValues.put(Team_O_STATS.O_INT,o_int);
            contentValues.put(Team_O_STATS.O_SACK,o_sack);
            contentValues.put(Team_O_STATS.O_RB_RE_YDS,o_rb_re_yds);
            contentValues.put(Team_O_STATS.O_WR_RE_YDS,o_wr_re_yds);
            contentValues.put(Team_O_STATS.O_TE_RE_YDS,o_te_re_yds);
            contentValues.put(Team_O_STATS.O_RE_YDS,o_re_yds);
            contentValues.put(Team_O_STATS.O_RB_RU_YDS,o_rb_ru_yds);
            contentValues.put(Team_O_STATS.O_QB_RU_YDS,o_qb_ru_yds);
            contentValues.put(Team_O_STATS.O_RU_YDS,o_ru_yds);
            contentValues.put(Team_O_STATS.O_PA_YDS,o_pa_yds);
            contentValues.put(Team_O_STATS.O_QB_TD,o_qb_td);
            contentValues.put(Team_O_STATS.O_RB_TD,o_rb_td);
            contentValues.put(Team_O_STATS.O_WR_TD,o_wr_td);
            contentValues.put(Team_O_STATS.O_TE_TD, o_te_td);
            contentValues.put(Team_O_STATS.O_TDS, o_tds);
            contentValues.put(Team_O_STATS.O_FG_YDS, o_fg_yds);
            contentValues.put(Team_O_STATS.O_FG_M, o_fg_m);

            Team_O_STATS stats = new Team_O_STATS(contentValues);
            stats.insert(connection);

            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTeamIds(){

        ArrayList<String> teamIds = new ArrayList<>();

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + Team.ID + " FROM " + Team.TABLE_NAME);

            while(resultSet.next()){
                teamIds.add(resultSet.getString(Team.ID));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return teamIds;
    }

    public void parseTeamData(){

        File teamFile = new File("C:\\data\\teamdata\\teams.csv");
        File scheduleFile = new File("C:\\data\\teamdata\\schedule.csv");
        String buffer;

        try{
            System.out.println("Fetching Team Data...");
            BufferedReader br = new BufferedReader(new FileReader(teamFile));
            br.readLine();
            while((buffer = br.readLine()) != null){
                Team team = new Team(buffer.split(","));
                team.insert(connection);
            }
            br.close();

            System.out.println("Fetching Schedule Data...");
            br = new BufferedReader(new FileReader(scheduleFile));
            br.readLine();
            while ((buffer = br.readLine()) != null){
                Schedule schedule = new Schedule(buffer.split(","));
                schedule.insert(connection);
            }
            br.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean recordExists(String table, String id){

        Statement statement = null;
        ResultSet resultSet = null;
        String checkQuery;
        boolean exists = false;

        try{
            statement = connection.createStatement();
            switch (table){
                case K.TABLE_NAME:
                    checkQuery = "SELECT " + K.ID + " FROM " + K.TABLE_NAME + " WHERE " + K.ID + " = '" + id + "'";
                    //System.out.println(checkQuery);
                    resultSet = statement.executeQuery(checkQuery);
                    exists = resultSet.first();
                    break;
                case QB.TABLE_NAME:
                    checkQuery = "SELECT " + QB.ID + " FROM " + QB.TABLE_NAME + " WHERE " + QB.ID + " = '" + id + "'";
                    resultSet = statement.executeQuery(checkQuery);
                    exists = resultSet.first();
                    break;
                case RB.TABLE_NAME:
                    checkQuery = "SELECT " + RB.ID + " FROM " + RB.TABLE_NAME + " WHERE " + RB.ID + " = '" + id + "'";
                    //System.out.println(checkQuery);
                    resultSet = statement.executeQuery(checkQuery);
                    exists = resultSet.first();
                    break;
                case TE.TABLE_NAME:
                    checkQuery = "SELECT " + TE.ID + " FROM " + TE.TABLE_NAME + " WHERE " + TE.ID + " = '" + id + "'";
                    //System.out.println(checkQuery);
                    resultSet = statement.executeQuery(checkQuery);
                    exists = resultSet.first();
                    break;
                case WR.TABLE_NAME:
                    checkQuery = "SELECT " + WR.ID + " FROM " + WR.TABLE_NAME + " WHERE " + WR.ID + " = '" + id + "'";
                    //System.out.println(checkQuery);
                    resultSet = statement.executeQuery(checkQuery);
                    exists = resultSet.first();
                    break;
                case DST.TABLE_NAME:
                    checkQuery = "SELECT " + DST.ID + " FROM " + DST.TABLE_NAME + " WHERE " + DST.ID + " = '" + id + "'";
                    //System.out.println(checkQuery);
                    resultSet = statement.executeQuery(checkQuery);
                    exists = resultSet.first();
                    break;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {

            try{
                resultSet.close();
                statement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return exists;
    }



}
