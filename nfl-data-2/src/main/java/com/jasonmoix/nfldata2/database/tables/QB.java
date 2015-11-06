package com.jasonmoix.nfldata2.database.tables;

import com.jasonmoix.nfldata2.database.Database;
import com.jasonmoix.nfldata2.database.data_structures.Player;
import org.apache.poi.ss.usermodel.Row;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by jmoix on 10/27/2015.
 */
public class QB extends Player {

    public String id;
    public String name;
    public String team;
    public double avg_pa_attempts;
    public double avg_pa_cmp;
    public double avg_int;
    public double avg_fum;
    public double avg_pa_td;
    public double avg_pa_yds;
    public double avg_pa_perc;
    public double avg_ru_attempts;
    public double avg_ru_tds;
    public double avg_ru_yds;
    public double avg_ru_perc;

    public static final String TABLE_NAME = "quarterback";

    //Columns
    public static final String ID = "qbid";
    public static final String NAME = "name";
    public static final String TEAM = "team";
    public static final String AVG_PA_ATTEMPTS = "avg_pa_attempts";
    public static final String AVG_PA_CMP = "avg_pa_cmp";
    public static final String AVG_INT = "avg_int";
    public static final String AVG_FUM = "avg_fum";
    public static final String AVG_PA_TD = "avg_pa_td";
    public static final String AVG_PA_YDS = "avg_pa_yds";
    public static final String AVG_PA_PERC = "avg_pa_perc";
    public static final String AVG_RU_ATTEMPTS = "avg_ru_attempts";
    public static final String AVG_RU_TDS = "avg_ru_tds";
    public static final String AVG_RU_YDS = "avg_ru_yds";
    public static final String AVG_RU_PERC = "avg_ru_perc";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            NAME + " VARCHAR(255)," +
            TEAM + " VARCHAR(255)," +
            AVG_PA_ATTEMPTS + " NUMERIC(8,5)," +
            AVG_PA_CMP + " NUMERIC(8,5)," +
            AVG_INT + " NUMERIC(8,5)," +
            AVG_FUM + " NUMERIC(8,5)," +
            AVG_PA_TD + " NUMERIC(8,5)," +
            AVG_PA_YDS + " NUMERIC(8,5)," +
            AVG_PA_PERC + " NUMERIC(8,5)," +
            AVG_RU_ATTEMPTS + " NUMERIC(8,5)," +
            AVG_RU_TDS + " NUMERIC(8,5)," +
            AVG_RU_YDS + " NUMERIC(8,5)," +
            AVG_RU_PERC + " NUMERIC(8,5)," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public QB(ResultSet resultSet){
        try {
            id = resultSet.getString(ID);
            name = resultSet.getString(NAME);
            team = resultSet.getString(TEAM);
            avg_pa_attempts = resultSet.getDouble(AVG_PA_ATTEMPTS);
            avg_pa_cmp = resultSet.getDouble(AVG_PA_CMP);
            avg_int = resultSet.getDouble(AVG_INT);
            avg_fum = resultSet.getDouble(AVG_FUM);
            avg_pa_td = resultSet.getDouble(AVG_PA_TD);
            avg_pa_yds = resultSet.getDouble(AVG_PA_YDS);
            avg_pa_perc = resultSet.getDouble(AVG_RU_PERC);
            avg_ru_attempts = resultSet.getDouble(AVG_RU_ATTEMPTS);
            avg_ru_tds = resultSet.getDouble(AVG_RU_TDS);
            avg_ru_yds = resultSet.getDouble(AVG_RU_YDS);
            avg_ru_perc = resultSet.getDouble(AVG_RU_PERC);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public QB(Row data){

        id = data.getCell(Database.ID_INDEX).getStringCellValue();
        name = data.getCell(Database.NAME_INDEX).getStringCellValue();
        team = data.getCell(Database.TEAM_INDEX).getStringCellValue().toLowerCase();
        avg_pa_attempts = 0;
        avg_pa_cmp = 0;
        avg_int = 0;
        avg_fum = 0;
        avg_pa_td = 0;
        avg_pa_yds = 0;
        avg_pa_perc = 0;
        avg_ru_attempts = 0;
        avg_ru_tds = 0;
        avg_ru_yds = 0;
        avg_ru_perc = 0;

    }

    public double[] getData(){

        return new double[]{
                avg_pa_attempts,
                avg_pa_cmp,
                avg_int,
                avg_fum,
                avg_pa_td,
                avg_pa_yds,
                avg_pa_perc,
                avg_ru_attempts,
                avg_ru_tds,
                avg_ru_yds,
                avg_ru_perc
        };

    }

    public void insert(Connection connection){

        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + NAME + "," + TEAM + "," + AVG_PA_ATTEMPTS + "," + AVG_PA_CMP + "," + AVG_INT + "," +
                AVG_FUM + "," + AVG_PA_TD + "," + AVG_PA_YDS + "," + AVG_PA_PERC + "," + AVG_RU_ATTEMPTS + "," + AVG_RU_TDS + "," +
                AVG_RU_YDS + "," + AVG_RU_PERC + ") VALUES (" +
                "\"" + id + "\",\"" + name + "\",\"" + team + "\"," + avg_pa_attempts + "," + avg_pa_cmp + "," + avg_int + "," +
                avg_fum + "," + avg_pa_td + "," + avg_pa_yds + "," + avg_pa_perc + "," + avg_ru_attempts + "," + avg_ru_tds + "," +
                avg_ru_yds + "," + avg_ru_perc + ")";

        try{
            statement = connection.createStatement();
            System.out.println("    " + insertStatement);
            statement.execute(insertStatement);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                statement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updateAverages(HashMap<String, String> avgs, Connection connection){
        Statement statement = null;
        Object[] keys = avgs.keySet().toArray();

        String updateStatement = "UPDATE " + TABLE_NAME + " SET " + keys[0].toString() + "=" + avgs.get(keys[0].toString());

        for(int i = 1; i < keys.length; i++){
            updateStatement = updateStatement + "," + keys[i].toString() + "=" + avgs.get(keys[i].toString());
        }

        updateStatement = updateStatement + " WHERE " + ID + "='" + avgs.get(ID) + "'";

        try{
            statement = connection.createStatement();
            System.out.println("    " + updateStatement);
            statement.execute(updateStatement);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                statement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
