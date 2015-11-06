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
public class QB_STATS {

    public String playerId;
    public int week;
    public int pa_attempts;
    public int pa_cmp;
    public int interceptions;
    public int fum;
    public int pa_td;
    public int pa_yds;
    public double pa_perc;
    public int ru_attempts;
    public int ru_tds;
    public int ru_yds;
    public double ru_perc;

    public static final String TABLE_NAME = "qb_stats";

    //Columns
    public static final String PLAYER_ID = "qbpid";
    public static final String WEEK = "week";
    public static final String PA_ATTEMPTS = "pa_attempts";
    public static final String PA_CMP = "pa_cmp";
    public static final String INT = "interceptions";
    public static final String FUM = "fum";
    public static final String PA_TD = "pa_td";
    public static final String PA_YDS = "pa_yds";
    public static final String PA_PERC = "pa_perc";
    public static final String RU_ATTEMPTS = "ru_attempts";
    public static final String RU_TDS = "ru_tds";
    public static final String RU_YDS = "ru_yds";
    public static final String RU_PERC = "ru_perc";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            PLAYER_ID + " VARCHAR(255)," +
            WEEK + " INTEGER," +
            PA_ATTEMPTS + " INTEGER," +
            PA_CMP + " INTEGER," +
            INT + " INTEGER," +
            FUM + " INTEGER," +
            PA_TD + " INTEGER," +
            PA_YDS + " INTEGER," +
            PA_PERC + " NUMERIC(8,5)," +
            RU_ATTEMPTS + " INTEGER," +
            RU_TDS + " INTEGER," +
            RU_YDS + " INTEGER," +
            RU_PERC + " NUMERIC(8,5)," +
            " CONSTRAINT fk_" + PLAYER_ID + " FOREIGN KEY (" + PLAYER_ID + ")" +
            " REFERENCES " + QB.TABLE_NAME + "(" + QB.ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public QB_STATS(int week, Row data){
        playerId = data.getCell(Database.ID_INDEX).getStringCellValue();
        this.week = week;
        pa_attempts = (data.getCell(Database.PA_ATTEMPTS_INDEX) == null) ? 0 : (int)data.getCell(Database.PA_ATTEMPTS_INDEX).getNumericCellValue();
        pa_cmp = (data.getCell(Database.PA_CMP_INDEX) == null) ? 0 : (int)data.getCell(Database.PA_CMP_INDEX).getNumericCellValue();
        interceptions = (data.getCell(Database.INT_INDEX) == null) ? 0 : (int)data.getCell(Database.INT_INDEX).getNumericCellValue();
        fum = (data.getCell(Database.FUM_INDEX) == null) ? 0 : (int)data.getCell(Database.FUM_INDEX).getNumericCellValue();
        pa_td = (data.getCell(Database.PA_TD_INDEX) == null) ? 0 : (int)data.getCell(Database.PA_TD_INDEX).getNumericCellValue();
        pa_yds = (data.getCell(Database.PA_YDS_INDEX) == null) ? 0 : (int)data.getCell(Database.PA_YDS_INDEX).getNumericCellValue();
        pa_perc = 0;
        ru_attempts = (data.getCell(Database.RU_ATTEMPTS_INDEX) == null) ? 0 : (int)data.getCell(Database.RU_ATTEMPTS_INDEX).getNumericCellValue();
        ru_tds = (data.getCell(Database.RU_TDS_INDEX) == null) ? 0 : (int)data.getCell(Database.RU_TDS_INDEX).getNumericCellValue();
        ru_yds = (data.getCell(Database.RU_YDS_INDEX) == null) ? 0 : (int)data.getCell(Database.RU_YDS_INDEX).getNumericCellValue();
        ru_perc = 0;
    }

    public QB_STATS(ResultSet resultSet){
        try{
            playerId = resultSet.getString(PLAYER_ID);
            week = resultSet.getInt(WEEK);
            pa_attempts = resultSet.getInt(PA_ATTEMPTS);
            pa_cmp = resultSet.getInt(PA_CMP);
            interceptions = resultSet.getInt(INT);
            fum = resultSet.getInt(FUM);
            pa_td = resultSet.getInt(PA_TD);
            pa_yds = resultSet.getInt(PA_YDS);
            pa_perc = resultSet.getInt(PA_PERC);
            ru_attempts = resultSet.getInt(RU_ATTEMPTS);
            ru_tds = resultSet.getInt(RU_TDS);
            ru_yds = resultSet.getInt(RU_YDS);
            ru_perc = resultSet.getInt(RU_PERC);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean played(){
        boolean played = false;

        if(pa_attempts != 0 ||
                pa_cmp != 0 ||
                interceptions != 0 ||
                fum != 0 ||
                pa_td != 0 ||
                pa_yds != 0 ||
                ru_attempts != 0 ||
                ru_tds != 0 ||
                ru_yds != 0) played = true;

        return played;
    }

    public double fantasyPoints(){

        double fantasyPoints = 0;

        fantasyPoints += interceptions*-2;
        fantasyPoints += fum*-2;
        fantasyPoints += (pa_td+ru_tds)*6;
        fantasyPoints += pa_yds/25;
        fantasyPoints += ru_yds/25;

        return fantasyPoints;

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                PLAYER_ID + "," + WEEK + "," + PA_ATTEMPTS + "," + PA_CMP + "," + INT + "," +
                FUM + "," + PA_TD + "," + PA_YDS + "," + PA_PERC + "," + RU_ATTEMPTS + "," + RU_TDS + "," +
                RU_YDS + "," + RU_PERC + ") VALUES (" +
                "\"" + playerId + "\"," + week + "," + pa_attempts + "," + pa_cmp + "," + interceptions + "," +
                fum + "," + pa_td + "," + pa_yds + "," + pa_perc + "," + ru_attempts + "," + ru_tds + "," +
                ru_yds + "," + ru_perc + ")";

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
