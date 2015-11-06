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
public class K extends Player {

    public String id;
    public String name;
    public String team;
    public double avg_fg_attempts;
    public double avg_fg_made;
    public double avg_fg_yds;
    public double avg_xp_attempts;
    public double avg_xp_made;

    public static final String TABLE_NAME = "kicker";

    //Columns
    public static final String ID = "kid";
    public static final String NAME = "name";
    public static final String TEAM = "team";
    public static final String AVG_FG_ATTEMPTS = "avg_fga";
    public static final String AVG_FG_MADE = "avg_fgm";
    public static final String AVG_FG_YDS = "avg_fgy";
    public static final String AVG_XP_ATTEMPTS = "avg_xpa";
    public static final String AVG_XP_MADE = "avg_xpm";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            NAME + " VARCHAR(255)," +
            TEAM + " VARCHAR(255)," +
            AVG_FG_ATTEMPTS + " NUMERIC(8,5)," +
            AVG_FG_MADE + " NUMERIC(8,5)," +
            AVG_FG_YDS + " NUMERIC(8,5)," +
            AVG_XP_ATTEMPTS + " NUMERIC(8,5)," +
            AVG_XP_MADE + " NUMERIC(8,5)," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public K(ResultSet resultSet){

        try{
            id = resultSet.getString(ID);
            name = resultSet.getString(NAME);
            team = resultSet.getString(TEAM);
            avg_fg_attempts = resultSet.getDouble(AVG_FG_ATTEMPTS);
            avg_fg_made = resultSet.getDouble(AVG_FG_MADE);
            avg_fg_yds = resultSet.getDouble(AVG_FG_YDS);
            avg_xp_attempts = resultSet.getDouble(AVG_XP_ATTEMPTS);
            avg_xp_made = resultSet.getDouble(AVG_XP_MADE);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public K(Row data){
        id = data.getCell(Database.ID_INDEX).getStringCellValue();
        name = data.getCell(Database.NAME_INDEX).getStringCellValue();
        team = data.getCell(Database.TEAM_INDEX).getStringCellValue().toLowerCase();
        avg_fg_attempts = 0;
        avg_fg_made = 0;
        avg_fg_yds = 0;
        avg_xp_attempts = 0;
        avg_xp_made = 0;
    }

    public double[] getData(){

        return new double[]{
                avg_fg_attempts,
                avg_fg_made,
                avg_fg_yds,
                avg_xp_attempts,
                avg_xp_made
        };

    }

    public void insert(Connection connection){

        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + NAME + "," + TEAM + "," + AVG_FG_ATTEMPTS + "," + AVG_FG_MADE + "," +
                AVG_FG_YDS + "," + AVG_XP_ATTEMPTS + "," + AVG_XP_MADE + ") VALUES (" +
                "\"" + id + "\",\"" + name + "\",\"" + team + "\"," + avg_fg_attempts + "," + avg_fg_made + "," +
                avg_fg_yds + "," + avg_xp_attempts + "," + avg_xp_made + ")";

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
