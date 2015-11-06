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
public class TE_STATS {

    public String playerId;
    public int week;
    public int fum;
    public int rec;
    public int rec_yds;
    public int rec_tds;
    public double rec_perc;

    public static final String TABLE_NAME = "te_stats";

    //Columns
    public static final String PLAYER_ID = "tepid";
    public static final String WEEK = "week";
    public static final String FUM = "fum";
    public static final String REC = "rec";
    public static final String REC_YDS = "rec_yds";
    public static final String REC_TDS = "rec_tds";
    public static final String REC_PERC = "rec_perc";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            PLAYER_ID + " VARCHAR(255)," +
            WEEK + " INTEGER," +
            FUM + " INTEGER," +
            REC + " INTEGER," +
            REC_YDS + " INTEGER," +
            REC_TDS + " INTEGER," +
            REC_PERC + " NUMERIC(8,5)," +
            " CONSTRAINT fk_" + PLAYER_ID + " FOREIGN KEY (" + PLAYER_ID + ")" +
            " REFERENCES " + TE.TABLE_NAME + "(" + TE.ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TE_STATS(int week, Row data){

        playerId = data.getCell(Database.ID_INDEX).getStringCellValue();
        this.week = week;
        fum = (data.getCell(Database.FUM_INDEX) == null) ? 0 : (int)data.getCell(Database.FUM_INDEX).getNumericCellValue();
        rec = (data.getCell(Database.REC_INDEX) == null) ? 0 : (int)data.getCell(Database.REC_INDEX).getNumericCellValue();
        rec_yds = (data.getCell(Database.REC_YDS_INDEX) == null) ? 0 : (int)data.getCell(Database.REC_YDS_INDEX).getNumericCellValue();
        rec_tds = (data.getCell(Database.REC_TDS_INDEX) == null) ? 0 : (int)data.getCell(Database.REC_TDS_INDEX).getNumericCellValue();
        rec_perc = 0;

    }

    public TE_STATS(ResultSet resultSet){
        try{
            playerId = resultSet.getString(PLAYER_ID);
            week = resultSet.getInt(WEEK);
            fum = resultSet.getInt(FUM);
            rec = resultSet.getInt(REC);
            rec_yds = resultSet.getInt(REC_YDS);
            rec_tds = resultSet.getInt(REC_TDS);
            rec_perc = resultSet.getInt(REC_PERC);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean played(){
        boolean played = false;

        if(fum != 0 ||
                rec != 0 ||
                rec_yds != 0 ||
                rec_tds != 0) played = true;

        return played;
    }

    public double fantasyPoints(){

        double fantasyPoints = 0;

        fantasyPoints += fum*-2;
        fantasyPoints += rec_tds*6;
        fantasyPoints += rec_yds/25;
        fantasyPoints += rec;

        return fantasyPoints;

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                PLAYER_ID + "," + WEEK + "," + FUM + "," + REC + "," + REC_YDS + "," +
                REC_TDS + "," + REC_PERC + ") VALUES (" +
                "\"" + playerId + "\"," + week + "," + fum + "," + rec + "," + rec_yds + "," +
                rec_tds + "," + rec_perc + ")";

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
