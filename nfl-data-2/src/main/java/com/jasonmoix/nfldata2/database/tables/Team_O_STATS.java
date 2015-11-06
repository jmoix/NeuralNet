package com.jasonmoix.nfldata2.database.tables;

import com.jasonmoix.nfldata2.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by jmoix on 10/27/2015.
 */
public class Team_O_STATS {

    public String teamId;
    public int week;
    public int o_fum;
    public int o_sack;
    public int o_int;
    public int o_re_yds;
    public int o_ru_yds;
    public int o_pa_yds;
    public int o_tds;
    public int o_wr_re_yds;
    public int o_wr_td;
    public int o_te_re_yds;
    public int o_te_td;
    public int o_rb_re_yds;
    public int o_rb_ru_yds;
    public int o_rb_td;
    public int o_qb_ru_yds;
    public int o_qb_td;
    public int o_fg_yds;
    public int o_fg_m;

    public static final String TABLE_NAME = "O_Stats";

    public static final String TEAM_ID = "otid";
    public static final String WEEK = "week";
    public static final String O_FUM = "o_fum";
    public static final String O_SACK = "o_sack";
    public static final String O_INT = "o_int";
    public static final String O_RE_YDS = "o_re_yds";
    public static final String O_RU_YDS = "o_ru_yds";
    public static final String O_PA_YDS = "o_pa_yds";
    public static final String O_TDS = "o_tds";
    public static final String O_WR_RE_YDS = "o_wr_re_yds";
    public static final String O_WR_TD = "o_wr_td";
    public static final String O_TE_RE_YDS = "o_te_re_yds";
    public static final String O_TE_TD = "o_te_td";
    public static final String O_RB_RE_YDS = "o_rb_re_yds";
    public static final String O_RB_RU_YDS = "o_rb_ru_yds";
    public static final String O_RB_TD = "o_rb_td";
    public static final String O_QB_RU_YDS = "o_qb_ru_yds";
    public static final String O_QB_TD = "o_qb_td";
    public static final String O_FG_YDS = "o_fg_yds";
    public static final String O_FG_M = "o_fg_m";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            TEAM_ID + " VARCHAR(255)," +
            WEEK + " INTEGER," +
            O_FUM + " INTEGER," +
            O_SACK + " INTEGER," +
            O_INT + " INTEGER," +
            O_RE_YDS + " INTEGER," +
            O_RU_YDS + " INTEGER," +
            O_PA_YDS + " INTEGER," +
            O_TDS + " INTEGER," +
            O_WR_RE_YDS + " INTEGER," +
            O_WR_TD + " INTEGER," +
            O_TE_RE_YDS + " INTEGER," +
            O_TE_TD + " INTEGER," +
            O_RB_RE_YDS + " INTEGER," +
            O_RB_RU_YDS + " INTEGER," +
            O_RB_TD + " INTEGER," +
            O_QB_RU_YDS + " INTEGER," +
            O_QB_TD + " INTEGER," +
            O_FG_YDS + " INTEGER," +
            O_FG_M + " INTEGER," +
            " CONSTRAINT fk_" + TEAM_ID + " FOREIGN KEY (" + TEAM_ID + ")" +
            " REFERENCES " + Team.TABLE_NAME + "(" + Team.ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Team_O_STATS(HashMap<String, Object> vals){

        teamId = vals.containsKey(TEAM_ID) ? vals.get(TEAM_ID).toString() : null;
        week = vals.containsKey(WEEK) ? Integer.parseInt(vals.get(WEEK).toString()) : 0;
        o_fum = vals.containsKey(O_FUM) ? Integer.parseInt(vals.get(O_FUM).toString()) : 0;
        o_sack = vals.containsKey(O_SACK) ? Integer.parseInt(vals.get(O_SACK).toString()) : 0;
        o_int = vals.containsKey(O_INT) ? Integer.parseInt(vals.get(O_INT).toString()) : 0;
        o_re_yds = vals.containsKey(O_RE_YDS) ? Integer.parseInt(vals.get(O_RE_YDS).toString()) : 0;
        o_ru_yds = vals.containsKey(O_RU_YDS) ? Integer.parseInt(vals.get(O_RU_YDS).toString()) : 0;
        o_pa_yds = vals.containsKey(O_PA_YDS) ? Integer.parseInt(vals.get(O_PA_YDS).toString()) : 0;
        o_tds = vals.containsKey(O_TDS) ? Integer.parseInt(vals.get(O_TDS).toString()) : 0;
        o_wr_re_yds = vals.containsKey(O_WR_RE_YDS) ? Integer.parseInt(vals.get(O_WR_RE_YDS).toString()) : 0;
        o_wr_td = vals.containsKey(O_WR_TD) ? Integer.parseInt(vals.get(O_WR_TD).toString()) : 0;
        o_te_re_yds = vals.containsKey(O_TE_RE_YDS) ? Integer.parseInt(vals.get(O_TE_RE_YDS).toString()) : 0;
        o_te_td = vals.containsKey(O_TE_TD) ? Integer.parseInt(vals.get(O_TE_TD).toString()) : 0;
        o_rb_re_yds = vals.containsKey(O_RB_RE_YDS) ? Integer.parseInt(vals.get(O_RB_RE_YDS).toString()) : 0;
        o_rb_ru_yds = vals.containsKey(O_RB_RU_YDS) ? Integer.parseInt(vals.get(O_RB_RU_YDS).toString()) : 0;
        o_rb_td = vals.containsKey(O_RB_TD) ? Integer.parseInt(vals.get(O_RB_TD).toString()) : 0;
        o_qb_ru_yds = vals.containsKey(O_QB_RU_YDS) ? Integer.parseInt(vals.get(O_QB_RU_YDS).toString()) : 0;
        o_qb_td = vals.containsKey(O_QB_TD) ? Integer.parseInt(vals.get(O_QB_TD).toString()) : 0;
        o_fg_yds = vals.containsKey(O_FG_YDS) ? Integer.parseInt(vals.get(O_FG_YDS).toString()) : 0;
        o_fg_m = vals.containsKey(O_FG_M) ? Integer.parseInt(vals.get(O_FG_M).toString()) : 0;

    }

    public Team_O_STATS(int week, String[] data){

        teamId = data[Database.TEAM_INDEX].toLowerCase();
        this.week = week;
        o_fum = 0;
        o_sack = 0;
        o_int = 0;
        o_re_yds = 0;
        o_ru_yds = 0;
        o_pa_yds = 0;
        o_tds = 0;
        o_wr_re_yds = 0;
        o_wr_td = 0;
        o_te_re_yds = 0;
        o_te_td = 0;
        o_rb_re_yds = 0;
        o_rb_ru_yds = 0;
        o_rb_td = 0;
        o_qb_ru_yds = 0;
        o_qb_td = 0;
        o_fg_yds = 0;
        o_fg_m = 0;

    }

    public Team_O_STATS(ResultSet resultSet){
        try{
            teamId = resultSet.getString(TEAM_ID);
            week = resultSet.getInt(WEEK);
            o_fum = resultSet.getInt(O_FUM);
            o_sack = resultSet.getInt(O_SACK);
            o_int = resultSet.getInt(O_INT);
            o_re_yds = resultSet.getInt(O_RE_YDS);
            o_ru_yds = resultSet.getInt(O_RU_YDS);
            o_pa_yds = resultSet.getInt(O_PA_YDS);
            o_tds = resultSet.getInt(O_TDS);
            o_wr_re_yds = resultSet.getInt(O_WR_RE_YDS);
            o_wr_td = resultSet.getInt(O_WR_TD);
            o_te_re_yds = resultSet.getInt(O_TE_RE_YDS);
            o_te_td = resultSet.getInt(O_TE_TD);
            o_rb_re_yds = resultSet.getInt(O_RB_RE_YDS);
            o_rb_ru_yds = resultSet.getInt(O_RB_RU_YDS);
            o_rb_td = resultSet.getInt(O_RB_TD);
            o_qb_ru_yds = resultSet.getInt(O_QB_RU_YDS);
            o_qb_td = resultSet.getInt(O_QB_TD);
            o_fg_yds = resultSet.getInt(O_FG_YDS);
            o_fg_m = resultSet.getInt(O_FG_M);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public double[] getData(){

        return new double[]{
                o_fum,
                o_sack,
                o_int,
                o_re_yds,
                o_ru_yds,
                o_pa_yds,
                o_tds,
                o_wr_re_yds,
                o_wr_td,
                o_te_re_yds,
                o_te_td,
                o_rb_re_yds,
                o_rb_ru_yds,
                o_rb_td,
                o_qb_ru_yds,
                o_qb_td,
                o_fg_yds,
                o_fg_m
        };
    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                TEAM_ID + "," + WEEK + "," + O_FUM + "," + O_SACK + "," + O_INT + "," + O_RE_YDS + "," + O_RU_YDS + "," + O_PA_YDS + "," +
                O_TDS + "," + O_WR_RE_YDS + "," + O_WR_TD + "," + O_TE_RE_YDS + "," + O_TE_TD + "," + O_RB_RE_YDS + "," +
                O_RB_RU_YDS + "," + O_RB_TD + "," + O_QB_RU_YDS + "," + O_QB_TD + "," + O_FG_YDS + "," + O_FG_M + ") VALUES (" +
                "\"" + teamId + "\"," + week + "," + o_fum + "," + o_sack + "," + o_int + "," + o_re_yds + "," + o_ru_yds + "," + o_pa_yds + "," +
                o_tds + "," + o_wr_re_yds + "," + o_wr_td + "," + o_te_re_yds + "," + o_te_td + "," + o_rb_re_yds + "," +
                o_rb_ru_yds + "," + o_rb_td + "," + o_qb_ru_yds + "," + o_qb_td + "," + o_fg_yds + "," + o_fg_m + ")";

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
