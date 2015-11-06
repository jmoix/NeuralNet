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
public class Team_D_STATS {

    public String teamId;
    public int week;
    public int d_fum;
    public int d_sack;
    public int d_int;
    public int d_re_yds;
    public int d_ru_yds;
    public int d_pa_yds;
    public int d_tds;
    public int d_wr_re_yds;
    public int d_wr_td;
    public int d_te_re_yds;
    public int d_te_td;
    public int d_rb_re_yds;
    public int d_rb_ru_yds;
    public int d_rb_td;
    public int d_qb_ru_yds;
    public int d_qb_td;
    public int d_fg_yds;
    public int d_fg_m;

    public static final String TABLE_NAME = "D_Stats";

    public static final String TEAM_ID = "dtid";
    public static final String WEEK = "week";
    public static final String D_FUM = "d_fum";
    public static final String D_SACK = "d_sack";
    public static final String D_INT = "d_int";
    public static final String D_RE_YDS = "d_re_yds";
    public static final String D_RU_YDS = "d_ru_yds";
    public static final String D_PA_YDS = "d_pa_yds";
    public static final String D_TDS = "d_tds";
    public static final String D_WR_RE_YDS = "d_wr_re_yds";
    public static final String D_WR_TD = "d_wr_td";
    public static final String D_TE_RE_YDS = "d_te_re_yds";
    public static final String D_TE_TD = "d_te_td";
    public static final String D_RB_RE_YDS = "d_rb_re_yds";
    public static final String D_RB_RU_YDS = "d_rb_ru_yds";
    public static final String D_RB_TD = "d_rb_td";
    public static final String D_QB_RU_YDS = "d_qb_ru_yds";
    public static final String D_QB_TD = "d_qb_td";
    public static final String D_FG_YDS = "d_fg_yds";
    public static final String D_FG_M = "d_fg_m";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            TEAM_ID + " VARCHAR(255)," +
            WEEK + " INTEGER," +
            D_FUM + " INTEGER," +
            D_SACK + " INTEGER," +
            D_INT + " INTEGER," +
            D_RE_YDS + " INTEGER," +
            D_RU_YDS + " INTEGER," +
            D_PA_YDS + " INTEGER," +
            D_TDS + " INTEGER," +
            D_WR_RE_YDS + " INTEGER," +
            D_WR_TD + " INTEGER," +
            D_TE_RE_YDS + " INTEGER," +
            D_TE_TD + " INTEGER," +
            D_RB_RE_YDS + " INTEGER," +
            D_RB_RU_YDS + " INTEGER," +
            D_RB_TD + " INTEGER," +
            D_QB_RU_YDS + " INTEGER," +
            D_QB_TD + " INTEGER," +
            D_FG_YDS + " INTEGER," +
            D_FG_M + " INTEGER," +
            " CONSTRAINT fk_" + TEAM_ID + " FOREIGN KEY (" + TEAM_ID + ")" +
            " REFERENCES " + Team.TABLE_NAME + "(" + Team.ID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Team_D_STATS(HashMap<String, Object> vals){

        teamId = vals.containsKey(TEAM_ID) ? vals.get(TEAM_ID).toString() : null;
        week = vals.containsKey(WEEK) ? Integer.parseInt(vals.get(WEEK).toString()) : 0;
        d_fum = vals.containsKey(D_FUM) ? Integer.parseInt(vals.get(D_FUM).toString()) : 0;
        d_sack = vals.containsKey(D_SACK) ? Integer.parseInt(vals.get(D_SACK).toString()) : 0;
        d_int = vals.containsKey(D_INT) ? Integer.parseInt(vals.get(D_INT).toString()) : 0;
        d_re_yds = vals.containsKey(D_RE_YDS) ? Integer.parseInt(vals.get(D_RE_YDS).toString()) : 0;
        d_ru_yds = vals.containsKey(D_RU_YDS) ? Integer.parseInt(vals.get(D_RU_YDS).toString()) : 0;
        d_pa_yds = vals.containsKey(D_PA_YDS) ? Integer.parseInt(vals.get(D_PA_YDS).toString()) : 0;
        d_tds = vals.containsKey(D_TDS) ? Integer.parseInt(vals.get(D_TDS).toString()) : 0;
        d_wr_re_yds = vals.containsKey(D_WR_RE_YDS) ? Integer.parseInt(vals.get(D_WR_RE_YDS).toString()) : 0;
        d_wr_td = vals.containsKey(D_WR_TD) ? Integer.parseInt(vals.get(D_WR_TD).toString()) : 0;
        d_te_re_yds = vals.containsKey(D_TE_RE_YDS) ? Integer.parseInt(vals.get(D_TE_RE_YDS).toString()) : 0;
        d_te_td = vals.containsKey(D_TE_TD) ? Integer.parseInt(vals.get(D_TE_TD).toString()) : 0;
        d_rb_re_yds = vals.containsKey(D_RB_RE_YDS) ? Integer.parseInt(vals.get(D_RB_RE_YDS).toString()) : 0;
        d_rb_ru_yds = vals.containsKey(D_RB_RU_YDS) ? Integer.parseInt(vals.get(D_RB_RU_YDS).toString()) : 0;
        d_rb_td = vals.containsKey(D_RB_TD) ? Integer.parseInt(vals.get(D_RB_TD).toString()) : 0;
        d_qb_ru_yds = vals.containsKey(D_QB_RU_YDS) ? Integer.parseInt(vals.get(D_QB_RU_YDS).toString()) : 0;
        d_qb_td = vals.containsKey(D_QB_TD) ? Integer.parseInt(vals.get(D_QB_TD).toString()) : 0;
        d_fg_yds = vals.containsKey(D_FG_YDS) ? Integer.parseInt(vals.get(D_FG_YDS).toString()) : 0;
        d_fg_m = vals.containsKey(D_FG_M) ? Integer.parseInt(vals.get(D_FG_M).toString()) : 0;

    }

    public Team_D_STATS(int week, String[] data){

        teamId = data[Database.TEAM_INDEX].toLowerCase();
        this.week = week;
        d_fum = 0;
        d_sack = 0;
        d_int = 0;
        d_re_yds = 0;
        d_ru_yds = 0;
        d_pa_yds = 0;
        d_tds = 0;
        d_wr_re_yds = 0;
        d_wr_td = 0;
        d_te_re_yds = 0;
        d_te_td = 0;
        d_rb_re_yds = 0;
        d_rb_ru_yds = 0;
        d_rb_td = 0;
        d_qb_ru_yds = 0;
        d_qb_td = 0;
        d_fg_yds = 0;
        d_fg_m = 0;

    }

    public Team_D_STATS(ResultSet resultSet){
        try{
            teamId = resultSet.getString(TEAM_ID);
            week = resultSet.getInt(WEEK);
            d_fum = resultSet.getInt(D_FUM);
            d_sack = resultSet.getInt(D_SACK);
            d_int = resultSet.getInt(D_INT);
            d_re_yds = resultSet.getInt(D_RE_YDS);
            d_ru_yds = resultSet.getInt(D_RU_YDS);
            d_pa_yds = resultSet.getInt(D_PA_YDS);
            d_tds = resultSet.getInt(D_TDS);
            d_wr_re_yds = resultSet.getInt(D_WR_RE_YDS);
            d_wr_td = resultSet.getInt(D_WR_TD);
            d_te_re_yds = resultSet.getInt(D_TE_RE_YDS);
            d_te_td = resultSet.getInt(D_TE_TD);
            d_rb_re_yds = resultSet.getInt(D_RB_RE_YDS);
            d_rb_ru_yds = resultSet.getInt(D_RB_RU_YDS);
            d_rb_td = resultSet.getInt(D_RB_TD);
            d_qb_ru_yds = resultSet.getInt(D_QB_RU_YDS);
            d_qb_td = resultSet.getInt(D_QB_TD);
            d_fg_yds = resultSet.getInt(D_FG_YDS);
            d_fg_m = resultSet.getInt(D_FG_M);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public double[] getData(){

        return new double[]{
                d_fum,
                d_sack,
                d_int,
                d_re_yds,
                d_ru_yds,
                d_pa_yds,
                d_tds,
                d_wr_re_yds,
                d_wr_td,
                d_te_re_yds,
                d_te_td,
                d_rb_re_yds,
                d_rb_ru_yds,
                d_rb_td,
                d_qb_ru_yds,
                d_qb_td,
                d_fg_yds,
                d_fg_m
        };
    }

    public double fantasyPoints(){

        double fantasyPoints = 10;

        fantasyPoints += d_fum*2;
        fantasyPoints += d_sack;
        fantasyPoints += d_int*2;
        if(d_tds < 4){
            fantasyPoints += d_tds*-3;
        }
        fantasyPoints += d_fg_m*-1;

        return fantasyPoints;

    }

    public void insert(Connection connection){
        Statement statement = null;
        String insertStatement = "INSERT INTO " + TABLE_NAME + "(" +
                TEAM_ID + "," + WEEK + "," + D_FUM + "," + D_SACK + "," + D_INT + "," + D_RE_YDS + "," + D_RU_YDS + "," + D_PA_YDS + "," +
                D_TDS + "," + D_WR_RE_YDS + "," + D_WR_TD + "," + D_TE_RE_YDS + "," + D_TE_TD + "," + D_RB_RE_YDS + "," +
                D_RB_RU_YDS + "," + D_RB_TD + "," + D_QB_RU_YDS + "," + D_QB_TD + "," + D_FG_YDS + "," + D_FG_M + ") VALUES (" +
                "\"" + teamId + "\"," + week + "," + d_fum + "," + d_sack + "," + d_int + "," + d_re_yds + "," + d_ru_yds + "," + d_pa_yds + "," +
                d_tds + "," + d_wr_re_yds + "," + d_wr_td + "," + d_te_re_yds + "," + d_te_td + "," + d_rb_re_yds + "," +
                d_rb_ru_yds + "," + d_rb_td + "," + d_qb_ru_yds + "," + d_qb_td + "," + d_fg_yds + "," + d_fg_m + ")";

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
