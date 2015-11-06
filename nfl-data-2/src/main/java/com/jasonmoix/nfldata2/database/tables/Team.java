package com.jasonmoix.nfldata2.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by jmoix on 10/27/2015.
 */
public class Team {

    public String id;
    public String name;
    public double avg_d_fum;
    public double avg_d_sack;
    public double avg_d_int;
    public double avg_d_re_yds;
    public double avg_d_ru_yds;
    public double avg_d_pa_yds;
    public double avg_d_tds;
    public double avg_d_wr_re_yds;
    public double avg_d_wr_td;
    public double avg_d_te_re_yds;
    public double avg_d_te_td;
    public double avg_d_rb_re_yds;
    public double avg_d_rb_ru_yds;
    public double avg_d_rb_td;
    public double avg_d_qb_ru_yds;
    public double avg_d_qb_td;
    public double avg_d_fg_yds;
    public double avg_d_fg_m;
    public double avg_o_fum;
    public double avg_o_sack;
    public double avg_o_int;
    public double avg_o_re_yds;
    public double avg_o_ru_yds;
    public double avg_o_pa_yds;
    public double avg_o_tds;
    public double avg_o_wr_re_yds;
    public double avg_o_wr_td;
    public double avg_o_te_re_yds;
    public double avg_o_te_td;
    public double avg_o_rb_re_yds;
    public double avg_o_rb_ru_yds;
    public double avg_o_rb_td;
    public double avg_o_qb_ru_yds;
    public double avg_o_qb_td;
    public double avg_o_fg_yds;
    public double avg_o_fg_m;

    public static final String TABLE_NAME = "Team";

    public static final String ID = "tid";
    public static final String NAME = "name";
    public static final String AVG_D_FUM = "avg_d_fum";
    public static final String AVG_D_SACK = "avg_d_sack";
    public static final String AVG_D_INT = "avg_d_int";
    public static final String AVG_D_RE_YDS = "avg_d_re_yds";
    public static final String AVG_D_RU_YDS = "avg_d_ru_yds";
    public static final String AVG_D_PA_YDS = "avg_d_pa_yds";
    public static final String AVG_D_TDS = "avg_d_tds";
    public static final String AVG_D_WR_RE_YDS = "avg_d_wr_re_yds";
    public static final String AVG_D_WR_TD = "avg_d_wr_td";
    public static final String AVG_D_TE_RE_YDS = "avg_d_te_re_yds";
    public static final String AVG_D_TE_TD = "avg_d_te_td";
    public static final String AVG_D_RB_RE_YDS = "avg_d_rb_re_yds";
    public static final String AVG_D_RB_RU_YDS = "avg_d_rb_ru_yds";
    public static final String AVG_D_RB_TD = "avg_d_rb_td";
    public static final String AVG_D_QB_RU_YDS = "avg_d_qb_ru_yds";
    public static final String AVG_D_QB_TD = "avg_d_qb_td";
    public static final String AVG_D_FG_YDS = "avg_d_fg_yds";
    public static final String AVG_D_FG_M = "avg_d_fg_m";
    public static final String AVG_O_FUM = "avg_o_fum";
    public static final String AVG_O_SACK = "avg_o_sack";
    public static final String AVG_O_INT = "avg_o_int";
    public static final String AVG_O_RE_YDS = "avg_o_re_yds";
    public static final String AVG_O_RU_YDS = "avg_o_ru_yds";
    public static final String AVG_O_PA_YDS = "avg_o_pa_yds";
    public static final String AVG_O_TDS = "avg_o_tds";
    public static final String AVG_O_WR_RE_YDS = "avg_o_wr_re_yds";
    public static final String AVG_O_WR_TD = "avg_o_wr_td";
    public static final String AVG_O_TE_RE_YDS = "avg_o_te_re_yds";
    public static final String AVG_O_TE_TD = "avg_o_te_td";
    public static final String AVG_O_RB_RE_YDS = "avg_o_rb_re_yds";
    public static final String AVG_O_RB_RU_YDS = "avg_o_rb_ru_yds";
    public static final String AVG_O_RB_TD = "avg_o_rb_td";
    public static final String AVG_O_QB_RU_YDS = "avg_o_qb_ru_yds";
    public static final String AVG_O_QB_TD = "avg_o_qb_td";
    public static final String AVG_O_FG_YDS = "avg_o_fg_yds";
    public static final String AVG_O_FG_M = "avg_o_fg_m";

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " VARCHAR(255)," +
            NAME + " VARCHAR(255)," +
            AVG_D_FUM + " NUMERIC(8,5)," +
            AVG_D_SACK + " NUMERIC(8,5)," +
            AVG_D_INT + " NUMERIC(8,5)," +
            AVG_D_RE_YDS + " NUMERIC(8,5)," +
            AVG_D_RU_YDS + " NUMERIC(8,5)," +
            AVG_D_PA_YDS + " NUMERIC(8,5)," +
            AVG_D_TDS + " NUMERIC(8,5)," +
            AVG_D_WR_RE_YDS + " NUMERIC(8,5)," +
            AVG_D_WR_TD + " NUMERIC(8,5)," +
            AVG_D_TE_RE_YDS + " NUMERIC(8,5)," +
            AVG_D_TE_TD + " NUMERIC(8,5)," +
            AVG_D_RB_RE_YDS + " NUMERIC(8,5)," +
            AVG_D_RB_RU_YDS + " NUMERIC(8,5)," +
            AVG_D_RB_TD + " NUMERIC(8,5)," +
            AVG_D_QB_RU_YDS + " NUMERIC(8,5)," +
            AVG_D_QB_TD + " NUMERIC(8,5)," +
            AVG_D_FG_YDS + " NUMERIC(8,5)," +
            AVG_D_FG_M + " NUMERIC(8,5)," +
            AVG_O_FUM + " NUMERIC(8,5)," +
            AVG_O_SACK + " NUMERIC(8,5)," +
            AVG_O_INT + " NUMERIC(8,5)," +
            AVG_O_RE_YDS + " NUMERIC(8,5)," +
            AVG_O_RU_YDS + " NUMERIC(8,5)," +
            AVG_O_PA_YDS + " NUMERIC(8,5)," +
            AVG_O_TDS + " NUMERIC(8,5)," +
            AVG_O_WR_RE_YDS + " NUMERIC(8,5)," +
            AVG_O_WR_TD + " NUMERIC(8,5)," +
            AVG_O_TE_RE_YDS + " NUMERIC(8,5)," +
            AVG_O_TE_TD + " NUMERIC(8,5)," +
            AVG_O_RB_RE_YDS + " NUMERIC(8,5)," +
            AVG_O_RB_RU_YDS + " NUMERIC(8,5)," +
            AVG_O_RB_TD + " NUMERIC(8,5)," +
            AVG_O_QB_RU_YDS + " NUMERIC(8,5)," +
            AVG_O_QB_TD + " NUMERIC(8,5)," +
            AVG_O_FG_YDS + " NUMERIC(8,5)," +
            AVG_O_FG_M + " NUMERIC(8,5)," +
            " CONSTRAINT pk_" + ID + " PRIMARY KEY (" + ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Team(ResultSet resultSet){

        try{

            id = resultSet.getString(ID);
            name = resultSet.getString(NAME);
            avg_d_fum = resultSet.getDouble(AVG_D_FUM);
            avg_d_sack = resultSet.getDouble(AVG_D_SACK);
            avg_d_int = resultSet.getDouble(AVG_D_INT);
            avg_d_re_yds = resultSet.getDouble(AVG_D_RE_YDS);
            avg_d_ru_yds = resultSet.getDouble(AVG_D_RU_YDS);
            avg_d_pa_yds = resultSet.getDouble(AVG_D_PA_YDS);
            avg_d_tds = resultSet.getDouble(AVG_D_TDS);
            avg_d_wr_re_yds = resultSet.getDouble(AVG_D_WR_RE_YDS);
            avg_d_wr_td = resultSet.getDouble(AVG_D_WR_TD);
            avg_d_te_re_yds = resultSet.getDouble(AVG_D_TE_RE_YDS);
            avg_d_te_td = resultSet.getDouble(AVG_D_TE_TD);
            avg_d_rb_re_yds = resultSet.getDouble(AVG_D_RB_RE_YDS);
            avg_d_rb_ru_yds = resultSet.getDouble(AVG_D_RB_RU_YDS);
            avg_d_rb_td = resultSet.getDouble(AVG_D_RB_TD);
            avg_d_qb_ru_yds = resultSet.getDouble(AVG_D_QB_RU_YDS);
            avg_d_qb_td = resultSet.getDouble(AVG_D_QB_TD);
            avg_d_fg_yds = resultSet.getDouble(AVG_D_FG_YDS);
            avg_d_fg_m = resultSet.getDouble(AVG_D_FG_M);
            avg_o_fum = resultSet.getDouble(AVG_O_FUM);
            avg_o_sack = resultSet.getDouble(AVG_O_SACK);
            avg_o_int = resultSet.getDouble(AVG_O_INT);
            avg_o_re_yds = resultSet.getDouble(AVG_O_RE_YDS);
            avg_o_ru_yds = resultSet.getDouble(AVG_O_RU_YDS);
            avg_o_pa_yds = resultSet.getDouble(AVG_O_PA_YDS);
            avg_o_tds = resultSet.getDouble(AVG_O_TDS);
            avg_o_wr_re_yds = resultSet.getDouble(AVG_O_WR_RE_YDS);
            avg_o_wr_td = resultSet.getDouble(AVG_O_WR_TD);
            avg_o_te_re_yds = resultSet.getDouble(AVG_O_TE_RE_YDS);
            avg_o_te_td = resultSet.getDouble(AVG_O_TE_TD);
            avg_o_rb_re_yds = resultSet.getDouble(AVG_O_RB_RE_YDS);
            avg_o_rb_ru_yds = resultSet.getDouble(AVG_O_RB_RU_YDS);
            avg_o_rb_td = resultSet.getDouble(AVG_O_RB_TD);
            avg_o_qb_ru_yds = resultSet.getDouble(AVG_O_QB_RU_YDS);
            avg_o_qb_td = resultSet.getDouble(AVG_O_QB_TD);
            avg_o_fg_yds = resultSet.getDouble(AVG_O_FG_YDS);
            avg_o_fg_m = resultSet.getDouble(AVG_O_FG_M);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Team(String[] data){

        id = data[ID_INDEX].toLowerCase();
        name = data[NAME_INDEX];
        name = name.replace(" ", "_");
        avg_d_fum = 0;
        avg_d_sack = 0;
        avg_d_int = 0;
        avg_d_re_yds = 0;
        avg_d_ru_yds = 0;
        avg_d_pa_yds = 0;
        avg_d_tds = 0;
        avg_d_wr_re_yds = 0;
        avg_d_wr_td = 0;
        avg_d_te_re_yds = 0;
        avg_d_te_td = 0;
        avg_d_rb_re_yds = 0;
        avg_d_rb_ru_yds = 0;
        avg_d_rb_td = 0;
        avg_d_qb_ru_yds = 0;
        avg_d_qb_td = 0;
        avg_d_fg_yds = 0;
        avg_d_fg_m = 0;
        avg_o_fum = 0;
        avg_o_sack = 0;
        avg_o_int = 0;
        avg_o_re_yds = 0;
        avg_o_ru_yds = 0;
        avg_o_pa_yds = 0;
        avg_o_tds = 0;
        avg_o_wr_re_yds = 0;
        avg_o_wr_td = 0;
        avg_o_te_re_yds = 0;
        avg_o_te_td = 0;
        avg_o_rb_re_yds = 0;
        avg_o_rb_ru_yds = 0;
        avg_o_rb_td = 0;
        avg_o_qb_ru_yds = 0;
        avg_o_qb_td = 0;
        avg_o_fg_yds = 0;
        avg_o_fg_m = 0;

    }

    public double[] getOffensiveData(){

        return new double[]{
                avg_o_fum,
                avg_o_sack,
                avg_o_int,
                avg_o_re_yds,
                avg_o_ru_yds,
                avg_o_pa_yds,
                avg_o_tds,
                avg_o_wr_re_yds,
                avg_o_wr_td,
                avg_o_te_re_yds,
                avg_o_te_td,
                avg_o_rb_re_yds,
                avg_o_rb_ru_yds,
                avg_o_rb_td,
                avg_o_qb_ru_yds,
                avg_o_qb_td,
                avg_o_fg_yds,
                avg_o_fg_m
        };

    }

    public double[] getDefensiveData(){

        return new double[]{
                avg_d_fum,
                avg_d_sack,
                avg_d_int,
                avg_d_re_yds,
                avg_d_ru_yds,
                avg_d_pa_yds,
                avg_d_tds,
                avg_d_wr_re_yds,
                avg_d_wr_td,
                avg_d_te_re_yds,
                avg_d_te_td,
                avg_d_rb_re_yds,
                avg_d_rb_ru_yds,
                avg_d_rb_td,
                avg_d_qb_ru_yds,
                avg_d_qb_td,
                avg_d_fg_yds,
                avg_d_fg_m
        };

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                ID + "," + NAME + "," + AVG_D_FUM + "," + AVG_D_SACK + "," + AVG_D_INT + "," + AVG_D_RE_YDS + "," + AVG_D_RU_YDS + "," + AVG_D_PA_YDS + "," +
                AVG_D_TDS + "," + AVG_D_WR_RE_YDS + "," + AVG_D_WR_TD + "," + AVG_D_TE_RE_YDS + "," + AVG_D_TE_TD + "," + AVG_D_RB_RE_YDS + "," +
                AVG_D_RB_RU_YDS + "," + AVG_D_RB_TD + "," + AVG_D_QB_RU_YDS + "," + AVG_D_QB_TD + "," + AVG_D_FG_YDS + "," + AVG_D_FG_M + "," +
                AVG_O_FUM + "," + AVG_O_SACK + "," + AVG_O_INT + "," + AVG_O_RE_YDS + "," + AVG_O_RU_YDS + "," + AVG_O_PA_YDS + "," +
                AVG_O_TDS + "," + AVG_O_WR_RE_YDS + "," + AVG_O_WR_TD + "," + AVG_O_TE_RE_YDS + "," + AVG_O_TE_TD + "," + AVG_O_RB_RE_YDS + "," +
                AVG_O_RB_RU_YDS + "," + AVG_O_RB_TD + "," + AVG_O_QB_RU_YDS + "," + AVG_O_QB_TD + "," + AVG_O_FG_YDS + "," + AVG_O_FG_M +
                ") VALUES (" +
                "\"" + id + "\",\"" + name + "\"," + avg_d_fum + "," + avg_d_sack + "," + avg_d_int + "," + avg_d_re_yds + "," + avg_d_ru_yds + "," + avg_d_pa_yds + "," +
                avg_d_tds + "," + avg_d_wr_re_yds + "," + avg_d_wr_td + "," + avg_d_te_re_yds + "," + avg_d_te_td + "," + avg_d_rb_re_yds + "," +
                avg_d_rb_ru_yds + "," + avg_d_rb_td + "," + avg_d_qb_ru_yds + "," + avg_d_qb_td + "," + avg_d_fg_yds + "," + avg_d_fg_m + "," +
                avg_o_fum + "," + avg_o_sack + "," + avg_o_int + "," + avg_o_re_yds + "," + avg_o_ru_yds + "," + avg_d_pa_yds + "," +
                avg_d_tds + "," + avg_d_wr_re_yds + "," + avg_d_wr_td + "," + avg_d_te_re_yds + "," + avg_d_te_td + "," + avg_d_rb_re_yds + "," +
                avg_d_rb_ru_yds + "," + avg_d_rb_td + "," + avg_d_qb_ru_yds + "," + avg_d_qb_td + "," + avg_d_fg_yds + "," + avg_d_fg_m +
                ")";

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
