package com.jasonmoix.nfldata2.database.tables;

import com.jasonmoix.nfldata2.database.Database;
import org.apache.poi.ss.usermodel.Row;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jmoix on 11/3/2015.
 */
public class DST {

    public String id;
    public String name;
    public String team;

    public static final String TABLE_NAME = "defense";

    //Columns
    public static final String ID = "did";
    public static final String NAME = "name";
    public static final String TEAM = "team";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            NAME + " VARCHAR(255)," +
            TEAM + " VARCHAR(255)," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DST(Row data){
        id = data.getCell(Database.ID_INDEX).getStringCellValue();
        name = data.getCell(Database.NAME_INDEX).getStringCellValue();
        team = data.getCell(Database.TEAM_INDEX).getStringCellValue().toLowerCase();
    }

    public void insert(Connection connection){

        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + NAME + "," + TEAM + ") VALUES (" +
                "\"" + id + "\",\"" + name + "\",\"" + team + "\")";

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
