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
public class TE extends Player {

    public String id;
    public String name;
    public String team;
    public double avg_fum;
    public double avg_rec;
    public double avg_rec_yds;
    public double avg_rec_tds;
    public double avg_rec_perc;

    public static final String TABLE_NAME = "tight_end";

    //Columns
    public static final String ID = "teid";
    public static final String NAME = "name";
    public static final String TEAM = "team";
    public static final String AVG_FUM = "avg_fum";
    public static final String AVG_REC = "avg_rec";
    public static final String AVG_REC_YDS = "avg_rec_yds";
    public static final String AVG_REC_TDS = "avg_rec_tds";
    public static final String AVG_REC_PERC = "avg_rec_perc";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            NAME + " VARCHAR(255)," +
            TEAM + " VARCHAR(255)," +
            AVG_FUM + " NUMERIC(8,5)," +
            AVG_REC + " NUMERIC(8,5)," +
            AVG_REC_YDS + " NUMERIC(8,5)," +
            AVG_REC_TDS + " NUMERIC(8,5)," +
            AVG_REC_PERC + " NUMERIC(8,5)," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TE(ResultSet resultSet){
        try {
            id = resultSet.getString(ID);
            name = resultSet.getString(NAME);
            team = resultSet.getString(TEAM);
            avg_fum = resultSet.getDouble(AVG_FUM);
            avg_rec = resultSet.getDouble(AVG_REC);
            avg_rec_yds = resultSet.getDouble(AVG_REC_YDS);
            avg_rec_tds = resultSet.getDouble(AVG_REC_TDS);
            avg_rec_perc = resultSet.getDouble(AVG_REC_PERC);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public TE(Row data){

        id = data.getCell(Database.ID_INDEX).getStringCellValue();
        name = data.getCell(Database.NAME_INDEX).getStringCellValue();
        team = data.getCell(Database.TEAM_INDEX).getStringCellValue().toLowerCase();
        avg_fum = 0;
        avg_rec = 0;
        avg_rec_yds = 0;
        avg_rec_tds = 0;
        avg_rec_perc = 0;

    }

    public double[] getData(){

        return new double[]{
                avg_fum,
                avg_rec,
                avg_rec_yds,
                avg_rec_tds,
                avg_rec_perc
        };

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + NAME + "," + TEAM + "," + AVG_FUM + "," + AVG_REC + "," +
                AVG_REC_YDS + "," + AVG_REC_TDS + "," + AVG_REC_PERC + ") VALUES (" +
                "\"" + id + "\",\"" + name + "\",\"" + team + "\"," + avg_fum + "," + avg_rec + "," +
                avg_rec_yds + "," + avg_rec_tds + "," + avg_rec_perc + ")";

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
