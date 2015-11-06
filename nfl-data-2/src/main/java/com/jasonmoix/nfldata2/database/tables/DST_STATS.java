package com.jasonmoix.nfldata2.database.tables;

import com.jasonmoix.nfldata2.database.Database;
import org.apache.poi.ss.usermodel.Row;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jmoix on 11/3/2015.
 */
public class DST_STATS {

    public String playerId;
    public int week;
    public int sacks;

    public static final String TABLE_NAME = "dst_stats";

    //Columns
    public static final String PLAYER_ID = "dpid";
    public static final String WEEK = "week";
    public static final String SACKS = "sacks";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            PLAYER_ID + " VARCHAR(255)," +
            WEEK + " INTEGER," +
            SACKS + " INTEGER," +
            " CONSTRAINT fk_" + PLAYER_ID + " FOREIGN KEY (" + PLAYER_ID + ")" +
            " REFERENCES " + DST.TABLE_NAME + "(" + DST.ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DST_STATS(int week, Row data){

        playerId = data.getCell(Database.ID_INDEX).getStringCellValue();
        this.week = week;

        sacks = (data.getCell(Database.SACK_INDEX) == null) ? 0 : (int)data.getCell(Database.SACK_INDEX).getNumericCellValue();

    }

    public void insert(Connection connection){

        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                PLAYER_ID + "," + WEEK + "," + SACKS + ") VALUES (" +
                "\"" + playerId + "\"," + week + "," + sacks + ")";

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
