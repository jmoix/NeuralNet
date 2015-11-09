package com.jasonmoix.nfldata2.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jmoix on 10/27/2015.
 */
public class Schedule {

    public String id;
    public String opponent;
    public int home;
    public int away;
    public int sunday;
    public int monday;
    public int thursday;
    public int days_since_played;
    public int games_since_bye;

    public static final String TABLE_NAME = "Schedule";

    public static final String ID = "sid";
    public static final String OPPONENT = "opp";
    public static final String HOME = "home";
    public static final String AWAY = "away";
    public static final String SUNDAY = "sunday";
    public static final String MONDAY = "monday";
    public static final String THURSDAY = "thursday";
    public static final String DAYS_SINCE_PLAYED = "days_since_last_game";
    public static final String GAMES_SINCE_BYE = "games_since_bye";

    public static final int ID_INDEX = 0;
    public static final int OPP_INDEX = 1;
    public static final int HOME_INDEX = 2;
    public static final int AWAY_INDEX = 3;
    public static final int SUNDAY_INDEX = 4;
    public static final int MONDAY_INDEX = 5;
    public static final int THURSDAY_INDEX = 6;
    public static final int DAYS_SINCE_PLAYED_INDEX = 7;
    public static final int GAMES_SINCE_BYE_INDEX= 8;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            OPPONENT + " VARCHAR(255)," +
            HOME + " INTEGER," +
            AWAY + " INTEGER," +
            SUNDAY + " INTEGER," +
            MONDAY + " INTEGER," +
            THURSDAY + " INTEGER," +
            DAYS_SINCE_PLAYED + " INTEGER," +
            GAMES_SINCE_BYE + " INTEGER," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Schedule(ResultSet resultSet){

        try {
            id = resultSet.getString(ID);
            opponent = resultSet.getString(OPPONENT);
            home = resultSet.getInt(HOME);
            away = resultSet.getInt(AWAY);
            sunday = resultSet.getInt(SUNDAY);
            monday = resultSet.getInt(MONDAY);
            thursday = resultSet.getInt(THURSDAY);
            days_since_played = resultSet.getInt(DAYS_SINCE_PLAYED);
            games_since_bye = resultSet.getInt(GAMES_SINCE_BYE);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Schedule(String[] data){

        id = data[ID_INDEX];
        opponent = data[OPP_INDEX];
        home = Integer.parseInt(data[HOME_INDEX]);
        away = Integer.parseInt(data[AWAY_INDEX]);
        sunday = Integer.parseInt(data[SUNDAY_INDEX]);
        monday = Integer.parseInt(data[MONDAY_INDEX]);
        thursday = Integer.parseInt(data[THURSDAY_INDEX]);
        days_since_played = Integer.parseInt(data[DAYS_SINCE_PLAYED_INDEX]);
        games_since_bye = Integer.parseInt(data[GAMES_SINCE_BYE_INDEX]);

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + OPPONENT + "," + HOME + "," + AWAY + "," + SUNDAY + "," + MONDAY + "," + THURSDAY + "," + DAYS_SINCE_PLAYED + "," + GAMES_SINCE_BYE + ") VALUES (" +
                "\"" + id + "\",\"" + opponent + "\"," + home + "," + away + "," + sunday + "," + monday + "," + thursday + "," + days_since_played + "," + games_since_bye + ")";

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

    public double[] getData(){

        double[] data = new double[7];

        data[0] = home;
        data[1] = away;
        data[2] = sunday;
        data[3] = monday;
        data[4] = thursday;
        data[5] = days_since_played;
        data[6] = games_since_bye;

        return data;

    }
}
