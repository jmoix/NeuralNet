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

    public static final String TABLE_NAME = "Schedule";

    public static final String ID = "sid";
    public static final String OPPONENT = "opp";
    public static final String HOME = "home";

    public static final int ID_INDEX = 0;
    public static final int OPP_INDEX = 1;
    public static final int HOME_INDEX = 2;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            OPPONENT + " VARCHAR(255)," +
            HOME + " INTEGER," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Schedule(ResultSet resultSet){

        try {
            id = resultSet.getString(ID);
            opponent = resultSet.getString(OPPONENT);
            home = resultSet.getInt(HOME);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Schedule(String[] data){

        id = data[ID_INDEX];
        opponent = data[OPP_INDEX];
        home = Integer.parseInt(data[HOME_INDEX]);

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + OPPONENT + "," + HOME + ") VALUES (" +
                "\"" + id + "\",\"" + opponent + "\"," + home + ")";

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
