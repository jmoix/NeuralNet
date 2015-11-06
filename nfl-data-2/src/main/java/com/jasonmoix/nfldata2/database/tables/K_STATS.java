package com.jasonmoix.nfldata2.database.tables;

import com.jasonmoix.nfldata2.database.Database;
import org.apache.poi.ss.usermodel.Row;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jmoix on 10/27/2015.
 */
public class K_STATS {

    public String playerId;
    public int week;
    public int fg_attempts;
    public int fg_made;
    public int fg_yds;
    public int xp_attempts;
    public int xp_made;

    public static final String TABLE_NAME = "k_stats";

    //Columns
    public static final String PLAYER_ID = "kpid";
    public static final String WEEK = "week";
    public static final String FG_ATTEMPTS = "fga";
    public static final String FG_MADE = "fgm";
    public static final String FG_YDS = "fgy";
    public static final String XP_ATTEMPTS = "xpa";
    public static final String XP_MADE = "xpm";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            PLAYER_ID + " VARCHAR(255)," +
            WEEK + " INTEGER," +
            FG_ATTEMPTS + " INTEGER," +
            FG_MADE + " INTEGER," +
            FG_YDS + " INTEGER," +
            XP_ATTEMPTS + " INTEGER," +
            XP_MADE + " INTEGER," +
            " CONSTRAINT fk_" + PLAYER_ID + " FOREIGN KEY (" + PLAYER_ID + ")" +
            " REFERENCES " + K.TABLE_NAME + "(" + K.ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public K_STATS(int week, Row data){

        playerId = data.getCell(Database.ID_INDEX).getStringCellValue();
        this.week = week;

        fg_attempts = (data.getCell(Database.FG_ATTEMPTS_INDEX) == null) ? 0 : (int)data.getCell(Database.FG_ATTEMPTS_INDEX).getNumericCellValue();
        fg_made = (data.getCell(Database.FG_MADE_INDEX) == null) ? 0 : (int)data.getCell(Database.FG_MADE_INDEX).getNumericCellValue();
        fg_yds = (data.getCell(Database.FG_YDS_INDEX) == null) ? 0 : (int)data.getCell(Database.FG_YDS_INDEX).getNumericCellValue();
        xp_attempts = (data.getCell(Database.XP_ATTEMPTS_INDEX) == null) ? 0 : (int)data.getCell(Database.XP_ATTEMPTS_INDEX).getNumericCellValue();
        xp_made = (data.getCell(Database.XP_MADE_INDEX) == null) ? 0 : (int)data.getCell(Database.XP_MADE_INDEX).getNumericCellValue();

    }

    public K_STATS(ResultSet resultSet){
        try {
            playerId = resultSet.getString(PLAYER_ID);
            week = resultSet.getInt(WEEK);
            fg_attempts = resultSet.getInt(FG_ATTEMPTS);
            fg_made = resultSet.getInt(FG_MADE);
            fg_yds = resultSet.getInt(FG_YDS);
            xp_attempts = resultSet.getInt(XP_ATTEMPTS);
            xp_made = resultSet.getInt(XP_MADE);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean played(){

        boolean played = false;

        if(fg_attempts != 0 ||
                fg_made != 0 ||
                fg_yds != 0 ||
                xp_attempts != 0 ||
                xp_made != 0) played = true;

        return played;
    }

    public void insert(Connection connection){

        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                PLAYER_ID + "," + WEEK + "," + FG_ATTEMPTS + "," + FG_MADE + "," + FG_YDS + "," +
                XP_ATTEMPTS + "," + XP_MADE + ") VALUES (" +
                "\"" + playerId + "\"," + week + "," + fg_attempts + "," + fg_made + "," + fg_yds + "," +
                xp_attempts + "," + xp_made + ")";

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
}
