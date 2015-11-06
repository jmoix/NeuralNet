import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jmoix on 10/16/2015.
 */
public class NFLDB {

    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost/nfldata";
    private HashMap<String, Integer> gameNumbers;
    private HashMap<String, DefensiveStatsAvg> defensiveAverages;
    private HashMap<String, OffensiveStatsAvg> offensiveAverages;

    public NFLDB(){

        try{
            connection = DriverManager.getConnection(DB_URL, "root", "xxxx");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void closeConnection(){

        try{
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double calculateOffenseFantasyPoints(int pa_yds, int pa_td, int ru_yds, int ru_td, int re_yds, int re_td, int fum, int two_pt, int interception, int rec, int rt_td){
        double fantasy_points = 0;

        if(pa_yds > 300) fantasy_points += 3;
        if(ru_yds > 100) fantasy_points += 3;
        if(re_yds > 100) fantasy_points += 3;

        fantasy_points += pa_td*4;
        fantasy_points += ru_td*6;
        fantasy_points += re_td*6;
        fantasy_points += pa_yds*0.04;
        fantasy_points += ru_yds*0.1;
        fantasy_points += re_yds*0.1;
        fantasy_points -= interception;
        fantasy_points -= fum;
        fantasy_points += rec;
        fantasy_points += two_pt*2;
        fantasy_points += rt_td*6;


        return fantasy_points;
    }

    public double calculateDefenseFantasyPoints(int sack, int interception, int fum_rec, int td, int safety, int blk, int points_allowed){
        double fantasy_pts = 0;

        if(points_allowed == 0) fantasy_pts += 10;
        else if(points_allowed < 6) fantasy_pts += 7;
        else if(points_allowed < 13) fantasy_pts += 4;
        else if(points_allowed < 20) fantasy_pts += 1;
        else if(points_allowed < 27) fantasy_pts += 0;
        else if(points_allowed < 34) fantasy_pts -= 1;
        else if(points_allowed >= 35) fantasy_pts -= 4;

        fantasy_pts += sack;
        fantasy_pts += interception*2;
        fantasy_pts += fum_rec*2;
        fantasy_pts += td*6;
        fantasy_pts += safety*2;
        fantasy_pts += blk*2;

        return fantasy_pts;
    }

    public void initAverages(){

        defensiveAverages = new HashMap<>();
        offensiveAverages = new HashMap<>();

        try{
            Statement statement = connection.createStatement();
            ResultSet defensiveStats = statement.executeQuery("SELECT * FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_WEEK + "=1");
            while (defensiveStats.next()){
                DefensiveStatsAvg avg = new DefensiveStatsAvg();
                avg.avg_pa_allowed = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_PA);
                avg.avg_te_allowed = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_TE);
                avg.avg_wr_allowed = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_WR);
                avg.avg_ru_allowed = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_RU);
                avg.avg_pts_allowed = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_PTS);
                avg.avg_sacks = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_SACKS);
                avg.avg_safety = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_SAFETY);
                avg.avg_fum_rec = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_FUM);
                avg.avg_int = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_INT);
                avg.avg_blk = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_BLK);
                avg.avg_ret = defensiveStats.getDouble(DefensiveStatsTable.COLUMN_AVG_RET);
                defensiveAverages.put(defensiveStats.getString(DefensiveStatsTable.COLUMN_TEAM_ID), avg);
            }
            defensiveStats.close();

            ResultSet offensiveStats = statement.executeQuery("SELECT * FROM " + OffensiveStatsTable.TABLE_NAME + " WHERE " + OffensiveStatsTable.COLUMN_WEEK + "=1");
            while (offensiveStats.next()){
                OffensiveStatsAvg avg = new OffensiveStatsAvg();
                avg.avg_pa = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_PA);
                avg.avg_te = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_TE);
                avg.avg_wr = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_WR);
                avg.avg_ru = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_RU);
                avg.avg_pts = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_PTS);
                avg.avg_sacks_allowed = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_SACKS);
                avg.avg_safety_allowed = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_SAFETY);
                avg.avg_fum_allowed = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_FUM);
                avg.avg_int_allowed = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_INT);
                avg.avg_blk_allowed = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_BLK);
                avg.avg_ret_allowed = offensiveStats.getDouble(OffensiveStatsTable.COLUMN_AVG_RET);
                offensiveAverages.put(offensiveStats.getString(OffensiveStatsTable.COLUMN_TEAM_ID), avg);
            }
            offensiveStats.close();

            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getOffensePlayerList(){

        ArrayList<String> players = new ArrayList<>();

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                    " FROM " + OffenseTable.TABLE_NAME + " ORDER BY " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID + " ASC ");

            while(resultSet.next()){
                players.add(resultSet.getString(OffenseTable.COLUMN_PID));
            }
            resultSet.close();


        }catch (Exception e){
            e.printStackTrace();
        }

        return players;
    }

    public void generatePlayerTrainingData(){

        System.out.println("Generating Offensive Player Data...");

        try{

            ArrayList<String> playerId = getOffensePlayerList();
            Statement statement = connection.createStatement();

            for(int i = 0; i < playerId.size(); i++){

                FileWriter writer = new FileWriter(new File("C:\\GameData\\PlayerTrainingData\\" + playerId.get(i) + ".csv"));
                BufferedWriter bw = new BufferedWriter(writer);
                System.out.println("    Fetching player data for " + playerId.get(i));

                ResultSet players = statement.executeQuery("SELECT * FROM " + OffenseStatsTable.TABLE_NAME +
                        " INNER JOIN " + OffenseTable.TABLE_NAME + " ON " +
                        OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" +
                        OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID + " WHERE " +
                        OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID + "='" + playerId.get(i) + "'");

                while (players.next()){
                    String team = players.getString(OffenseTable.COLUMN_TEAM_ID);
                    String opponent = players.getString(OffenseStatsTable.COLUMN_OPPONENT);
                    String position = players.getString(OffenseTable.COLUMN_POSITION);
                    if(!opponent.equals("bye")){
                        double fantasy_points = calculateOffenseFantasyPoints(
                                players.getInt(OffenseStatsTable.COLUMN_PA_YDS),
                                players.getInt(OffenseStatsTable.COLUMN_PA_TDS),
                                players.getInt(OffenseStatsTable.COLUMN_RU_YDS),
                                players.getInt(OffenseStatsTable.COLUMN_RU_TDS),
                                players.getInt(OffenseStatsTable.COLUMN_RE_YDS),
                                players.getInt(OffenseStatsTable.COLUMN_RE_TD),
                                players.getInt(OffenseStatsTable.COLUMN_FUM),
                                players.getInt(OffenseStatsTable.COLUMN_2PT),
                                players.getInt(OffenseStatsTable.COLUMN_PA_INT),
                                players.getInt(OffenseStatsTable.COLUMN_RE_REC),
                                players.getInt(OffenseStatsTable.COLUMN_RT_TD)
                        );

                        double avg_pa = players.getDouble(OffenseTable.COLUMN_AVG_PA);
                        double avg_pa_td = players.getDouble(OffenseTable.COLUMN_AVG_PA_TD);
                        double avg_ru = players.getDouble(OffenseTable.COLUMN_AVG_RU);
                        double avg_ru_td = players.getDouble(OffenseTable.COLUMN_AVG_RU_TD);
                        double avg_re = players.getDouble(OffenseTable.COLUMN_AVG_RE);
                        double avg_re_td = players.getDouble(OffenseTable.COLUMN_AVG_RE_TD);
                        double avg_int = players.getDouble(OffenseTable.COLUMN_AVG_INT);
                        double avg_fum = players.getDouble(OffenseTable.COLUMN_AVG_FUM);
                        double avg_tgt = players.getDouble(OffenseTable.COLUMN_AVG_TGT);
                        double avg_rec = players.getDouble(OffenseTable.COLUMN_AVG_REC);
                        double avg_att = players.getDouble(OffenseTable.COLUMN_AVG_ATT);

                        if(avg_pa == 0 && avg_pa_td == 0 && avg_ru == 0 && avg_ru_td == 0 && avg_re == 0 && avg_re_td == 0 && avg_int == 0 &&
                                avg_fum == 0 && avg_tgt == 0 && avg_rec == 0 && avg_att == 0){
                            System.out.println("    Non-playing player");
                        }else {

                            switch (position){
                                case "qb":
                                    bw.write(avg_pa + "," +
                                            avg_pa_td + "," +
                                            avg_ru_td + "," +
                                            avg_ru + "," +
                                            avg_int + "," +
                                            avg_fum + "," +
                                            avg_att + "," +
                                            defensiveAverages.get(opponent).toString() + "," +
                                            offensiveAverages.get(team).toString() + "," +
                                            fantasy_points);
                                    bw.newLine();
                                    break;
                                case "te":
                                    bw.write(avg_re + "," +
                                            avg_re_td + "," +
                                            avg_fum + "," +
                                            avg_tgt + "," +
                                            avg_rec + "," +
                                            defensiveAverages.get(opponent).toString() + "," +
                                            offensiveAverages.get(team).toString() + "," +
                                            fantasy_points);
                                    bw.newLine();
                                    break;
                                case "wr":
                                    bw.write(avg_re + "," +
                                            avg_re_td + "," +
                                            avg_fum + "," +
                                            avg_tgt + "," +
                                            avg_rec + "," +
                                            defensiveAverages.get(opponent).toString() + "," +
                                            offensiveAverages.get(team).toString() + "," +
                                            fantasy_points);
                                    bw.newLine();
                                    break;
                                case "rb":
                                    bw.write(avg_ru_td + "," +
                                            avg_ru + "," +
                                            avg_re + "," +
                                            avg_re_td + "," +
                                            avg_fum + "," +
                                            avg_tgt + "," +
                                            avg_rec + "," +
                                            avg_att + "," +
                                            defensiveAverages.get(opponent).toString() + "," +
                                            offensiveAverages.get(team).toString() + "," +
                                            fantasy_points);
                                    bw.newLine();
                                    break;
                                default:
                                    bw.write(avg_pa + "," +
                                            avg_pa_td + "," +
                                            avg_ru_td + "," +
                                            avg_ru + "," +
                                            avg_re + "," +
                                            avg_re_td + "," +
                                            avg_int + "," +
                                            avg_fum + "," +
                                            avg_tgt + "," +
                                            avg_rec + "," +
                                            avg_att + "," +
                                            defensiveAverages.get(opponent).toString() + "," +
                                            offensiveAverages.get(team).toString() + "," +
                                            fantasy_points);
                                    bw.newLine();
                                    break;
                            }

                        }
                    }
                }

                bw.close();
                players.close();

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getDefensivePlayerList(){

        ArrayList<String> players = new ArrayList<>();

        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select " + TeamTable.TABLE_NAME + "." + TeamTable.COLUMN_TID +
                    " FROM " + TeamTable.TABLE_NAME + " ORDER BY " + TeamTable.TABLE_NAME + "." + TeamTable.COLUMN_TID + " ASC ");

            while(resultSet.next()){
                players.add(resultSet.getString(TeamTable.COLUMN_TID));
            }
            resultSet.close();


        }catch (Exception e){
            e.printStackTrace();
        }

        return players;

    }

    public void generateDefensivePlayerTrainingData(){

        ArrayList<String> playerId = getDefensivePlayerList();

        System.out.println("Generating Defensive Player Data...");

        try{

            Statement statement = connection.createStatement();

            for(int i = 0; i < playerId.size(); i++){

                System.out.println("    Fetching player data for " + playerId.get(i));

                ResultSet result = statement.executeQuery("SELECT * FROM " + DefensiveStatsTable.TABLE_NAME +
                        " WHERE " + DefensiveStatsTable.TABLE_NAME + "." + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" +
                        playerId.get(i) + "'");

                File trainingOutput = new File("C:\\GameData\\PlayerTrainingData\\" + playerId.get(i) + ".csv");
                FileWriter writer = new FileWriter(trainingOutput);
                BufferedWriter bw = new BufferedWriter(writer);

                while(result.next()){

                    String opponent = result.getString(DefensiveStatsTable.COLUMN_OPPONENT);

                    if(!opponent.equals("bye")){

                        double fantasy_points = calculateDefenseFantasyPoints(result.getInt(DefensiveStatsTable.COLUMN_SACK),result.getInt(DefensiveStatsTable.COLUMN_INT),
                                result.getInt(DefensiveStatsTable.COLUMN_FUM),result.getInt(DefensiveStatsTable.COLUMN_TD),result.getInt(DefensiveStatsTable.COLUMN_SAFETY),
                                result.getInt(DefensiveStatsTable.COLUMN_BLK),result.getInt(DefensiveStatsTable.COLUMN_PTS_ALLOWED));

                        bw.write(defensiveAverages.get(playerId.get(i)) + "," + offensiveAverages.get(opponent) + "," + fantasy_points);
                        bw.newLine();
                    }

                }

                result.close();
                bw.close();
            }

            statement.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void generateKickerTrainingData(){

        File outputFile = new File("C:\\GameData\\TrainingData\\kicker.csv");
        System.out.println("Generating Player Training Data for Position kicker...");

        try{

            FileWriter writer = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(writer);

            Statement statement = connection.createStatement();
            ResultSet players = statement.executeQuery("SELECT * FROM " + KickerStatsTable.TABLE_NAME +
                    " INNER JOIN " + KickerTable.TABLE_NAME + " ON " +
                    KickerStatsTable.TABLE_NAME + "." + KickerStatsTable.COLUMN_PLAYER_ID + "=" +
                    KickerTable.TABLE_NAME + "." + KickerTable.COLUMN_PID);

            while(players.next()){
                String team = players.getString(KickerTable.COLUMN_TEAM_ID);
                String opponent = players.getString(KickerStatsTable.COLUMN_OPPONENT);
                if(!opponent.equals("bye")){
                    bw.write(players.getDouble(KickerTable.COLUMN_AVG_0_19) + "," +
                            players.getDouble(KickerTable.COLUMN_AVG_20_29) + "," +
                            players.getDouble(KickerTable.COLUMN_AVG_30_39) + "," +
                            players.getDouble(KickerTable.COLUMN_AVG_40_49) + "," +
                            players.getDouble(KickerTable.COLUMN_AVG_50) + "," +
                            players.getDouble(KickerTable.COLUMN_AVG_MADE) + "," +
                            defensiveAverages.get(opponent).toString() + "," +
                            offensiveAverages.get(team).toString());
                    bw.newLine();
                }
            }

            bw.close();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void initTables(){

        Statement sqlStatement;

        try {

            sqlStatement = connection.createStatement();

            System.out.println("Dropping Tables..");
            sqlStatement.execute("DROP TABLE IF EXISTS " + OffensiveStatsTable.TABLE_NAME);
            sqlStatement.execute("DROP TABLE IF EXISTS " + DefensiveStatsTable.TABLE_NAME);
            sqlStatement.execute("DROP TABLE IF EXISTS " + OffenseStatsTable.TABLE_NAME);
            sqlStatement.execute("DROP TABLE IF EXISTS " + KickerStatsTable.TABLE_NAME);
            sqlStatement.execute("DROP TABLE IF EXISTS " + OffenseTable.TABLE_NAME);
            sqlStatement.execute("DROP TABLE IF EXISTS " + KickerTable.TABLE_NAME);
            sqlStatement.execute("DROP TABLE IF EXISTS " + TeamTable.TABLE_NAME);

            System.out.println("Creating Tables..");
            System.out.println("    " + TeamTable.CREATE_TABLE);
            sqlStatement.execute(TeamTable.CREATE_TABLE);

            System.out.println("    " + DefensiveStatsTable.CREATE_TABLE);
            sqlStatement.execute(DefensiveStatsTable.CREATE_TABLE);

            System.out.println("    " + OffensiveStatsTable.CREATE_TABLE);
            sqlStatement.execute(OffensiveStatsTable.CREATE_TABLE);

            System.out.println("    " + OffenseTable.CREATE_TABLE);
            sqlStatement.execute(OffenseTable.CREATE_TABLE);

            System.out.println("    " + KickerTable.CREATE_TABLE);
            sqlStatement.execute(KickerTable.CREATE_TABLE);

            System.out.println("    " + OffenseStatsTable.CREATE_TABLE);
            sqlStatement.execute(OffenseStatsTable.CREATE_TABLE);

            System.out.println("    " + KickerStatsTable.CREATE_TABLE);
            sqlStatement.execute(KickerStatsTable.CREATE_TABLE);

            System.out.println("    Tables Created!");

            sqlStatement.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadTeamData(JsonArray data){

        JsonArray teamData = new JsonArray();
        ArrayList<String> teams = new ArrayList<>();
        JsonArray defensiveData = new JsonArray();

        System.out.println("Creating Data Objects...");
        for(int i = 0; i < data.size(); i++){
            JsonObject dataObject = data.getJsonObject(i);
            String id = dataObject.getString("id").toLowerCase();
            JsonObject player = new JsonObject()
                    .put(DefensiveStatsTable.COLUMN_TEAM_ID, id)
                    .put(DefensiveStatsTable.COLUMN_WEEK, dataObject.getInteger("week"))
                    .put(DefensiveStatsTable.COLUMN_OPPONENT, dataObject.getString("opponent").toLowerCase())
                    .put(DefensiveStatsTable.COLUMN_PTS_ALLOWED, dataObject.getInteger("pts_allowed"))
                    .put(DefensiveStatsTable.COLUMN_SACK, dataObject.getInteger("sack"))
                    .put(DefensiveStatsTable.COLUMN_SAFETY, dataObject.getInteger("safety"))
                    .put(DefensiveStatsTable.COLUMN_INT, dataObject.getInteger("interception"))
                    .put(DefensiveStatsTable.COLUMN_FUM, dataObject.getInteger("fum_rec"))
                    .put(DefensiveStatsTable.COLUMN_TD, dataObject.getInteger("td"))
                    .put(DefensiveStatsTable.COLUMN_BLK, dataObject.getInteger("blk_kick"))
                    .put(DefensiveStatsTable.COLUMN_RET, dataObject.getInteger("ret_td"));
            defensiveData.add(player);
            if(!teams.contains(id)){
                JsonObject team = new JsonObject()
                        .put(TeamTable.COLUMN_TID, id)
                        .put(TeamTable.COLUMN_NAME, dataObject.getString("name"))
                        .put(TeamTable.COLUMN_LOCATION, dataObject.getString("location"));
                teamData.add(team);
                teams.add(id);
            }
        }

        try{

            Statement statement = connection.createStatement();

            System.out.println("Inserting data to Team table: ");
            for(int i = 0; i < teamData.size(); i++){
                JsonObject team = teamData.getJsonObject(i);
                String insertStatement = "INSERT INTO " + TeamTable.TABLE_NAME + "(" +
                        TeamTable.COLUMN_TID + "," +
                        TeamTable.COLUMN_NAME + "," +
                        TeamTable.COLUMN_LOCATION +
                        ") VALUES (" +
                        "'" + team.getString(TeamTable.COLUMN_TID) + "'," +
                        "'" + team.getString(TeamTable.COLUMN_NAME) + "'," +
                        "'" + team.getString(TeamTable.COLUMN_LOCATION) + "'" +
                ")";
                System.out.println("    " + insertStatement);
                statement.execute(insertStatement);
            }

            System.out.println("Inserting data to DefensiveStats table: ");
            for(int i = 0; i < defensiveData.size(); i++){
                JsonObject playerData = defensiveData.getJsonObject(i);
                String insertStatement = "INSERT INTO " + DefensiveStatsTable.TABLE_NAME + "(" +
                        DefensiveStatsTable.COLUMN_TEAM_ID + "," +
                        DefensiveStatsTable.COLUMN_WEEK + "," +
                        DefensiveStatsTable.COLUMN_OPPONENT + "," +
                        DefensiveStatsTable.COLUMN_PTS_ALLOWED + "," +
                        DefensiveStatsTable.COLUMN_SACK + "," +
                        DefensiveStatsTable.COLUMN_SAFETY + "," +
                        DefensiveStatsTable.COLUMN_INT + "," +
                        DefensiveStatsTable.COLUMN_FUM + "," +
                        DefensiveStatsTable.COLUMN_TD + "," +
                        DefensiveStatsTable.COLUMN_BLK + "," +
                        DefensiveStatsTable.COLUMN_RET + "," +
                        DefensiveStatsTable.COLUMN_AVG_PA + "," +
                        DefensiveStatsTable.COLUMN_AVG_TE + "," +
                        DefensiveStatsTable.COLUMN_AVG_WR + "," +
                        DefensiveStatsTable.COLUMN_AVG_RU + "," +
                        DefensiveStatsTable.COLUMN_AVG_PTS + "," +
                        DefensiveStatsTable.COLUMN_AVG_SACKS + "," +
                        DefensiveStatsTable.COLUMN_AVG_SAFETY + "," +
                        DefensiveStatsTable.COLUMN_AVG_FUM + "," +
                        DefensiveStatsTable.COLUMN_AVG_INT + "," +
                        DefensiveStatsTable.COLUMN_AVG_BLK + "," +
                        DefensiveStatsTable.COLUMN_AVG_RET +
                        ") VALUES (" +
                        "'" + playerData.getString(DefensiveStatsTable.COLUMN_TEAM_ID) + "'," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_WEEK) + "," +
                        "'" + playerData.getString(DefensiveStatsTable.COLUMN_OPPONENT) + "'," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_PTS_ALLOWED) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_SACK) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_SAFETY) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_INT) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_FUM) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_TD) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_BLK) + "," +
                        playerData.getInteger(DefensiveStatsTable.COLUMN_RET) + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 +
                        ")";
                System.out.println("    " + insertStatement);
                statement.execute(insertStatement);

                insertStatement = "INSERT INTO " + OffensiveStatsTable.TABLE_NAME + "(" +
                        OffensiveStatsTable.COLUMN_TEAM_ID + "," +
                        OffensiveStatsTable.COLUMN_WEEK + "," +
                        OffensiveStatsTable.COLUMN_OPPONENT + "," +
                        OffensiveStatsTable.COLUMN_AVG_PA + "," +
                        OffensiveStatsTable.COLUMN_AVG_TE + "," +
                        OffensiveStatsTable.COLUMN_AVG_WR + "," +
                        OffensiveStatsTable.COLUMN_AVG_RU + "," +
                        OffensiveStatsTable.COLUMN_AVG_PTS + "," +
                        OffensiveStatsTable.COLUMN_AVG_SACKS + "," +
                        OffensiveStatsTable.COLUMN_AVG_SAFETY + "," +
                        OffensiveStatsTable.COLUMN_AVG_FUM + "," +
                        OffensiveStatsTable.COLUMN_AVG_INT + "," +
                        OffensiveStatsTable.COLUMN_AVG_BLK + "," +
                        OffensiveStatsTable.COLUMN_AVG_RET +
                        ") VALUES (" +
                        "'" + playerData.getString(OffensiveStatsTable.COLUMN_TEAM_ID) + "'," +
                        playerData.getInteger(OffensiveStatsTable.COLUMN_WEEK) + "," +
                        "'" + playerData.getString(OffensiveStatsTable.COLUMN_OPPONENT) + "'," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 + "," +
                        0 +
                        ")";

                System.out.println("    Create OffensiveStat Entry: " + insertStatement);
                statement.execute(insertStatement);
            }

            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void loadKickerData(JsonArray data){

        JsonArray kickers = new JsonArray();
        ArrayList<String> ids = new ArrayList<>();
        JsonArray kickerStats = new JsonArray();

        for(int i = 0; i < data.size(); i++){
            JsonObject dataObject = data.getJsonObject(i);
            String team = dataObject.getString("team").toLowerCase();
            String id = dataObject.getString("first_initial").toLowerCase() + dataObject.getString("last_name").toLowerCase() + team;
            if(!ids.contains(id)){
                JsonObject kicker = new JsonObject()
                        .put(KickerTable.COLUMN_PID, id)
                        .put(KickerTable.COLUMN_FNAME, dataObject.getString("first_initial"))
                        .put(KickerTable.COLUMN_LNAME, dataObject.getString("last_name"))
                        .put(KickerTable.COLUMN_TEAM_ID, team);
                kickers.add(kicker);
                ids.add(id);
            }
            JsonObject kickerStat = new JsonObject()
                    .put(KickerStatsTable.COLUMN_PLAYER_ID, id)
                    .put(KickerStatsTable.COLUMN_WEEK, dataObject.getInteger("week"))
                    .put(KickerStatsTable.COLUMN_OPPONENT, dataObject.getString("opponent").toLowerCase())
                    .put(KickerStatsTable.COLUMN_19, dataObject.getInteger("less_19"))
                    .put(KickerStatsTable.COLUMN_20_29, dataObject.getInteger("bw_20_29"))
                    .put(KickerStatsTable.COLUMN_30_39, dataObject.getInteger("bw_30_39"))
                    .put(KickerStatsTable.COLUMN_40_49, dataObject.getInteger("bw_40_49"))
                    .put(KickerStatsTable.COLUMN_50, dataObject.getInteger("more_50"))
                    .put(KickerStatsTable.MADE, dataObject.getInteger("made"));
            kickerStats.add(kickerStat);
        }

        try{

            Statement statement = connection.createStatement();

            System.out.println("Inserting data to kicker table:");
            for(int i = 0; i < kickers.size(); i++){
                JsonObject kicker = kickers.getJsonObject(i);
                String insertStatement = "INSERT INTO " + KickerTable.TABLE_NAME + "(" +
                        KickerTable.COLUMN_PID + "," +
                        KickerTable.COLUMN_FNAME + "," +
                        KickerTable.COLUMN_LNAME + "," +
                        KickerTable.COLUMN_TEAM_ID + "," +
                        KickerTable.COLUMN_AVG_0_19 + "," +
                        KickerTable.COLUMN_AVG_20_29 + "," +
                        KickerTable.COLUMN_AVG_30_39 + "," +
                        KickerTable.COLUMN_AVG_40_49 + "," +
                        KickerTable.COLUMN_AVG_50 + "," +
                        KickerTable.COLUMN_AVG_MADE +
                        ") VALUES (" +
                        "'" + kicker.getString(KickerTable.COLUMN_PID) + "'," +
                        "'" + kicker.getString(KickerTable.COLUMN_FNAME) + "'," +
                        "'" + kicker.getString(KickerTable.COLUMN_LNAME) + "'," +
                        "'" + kicker.getString(KickerTable.COLUMN_TEAM_ID) + "'," +
                        "0,0,0,0,0,0)";
                System.out.println("    " + insertStatement);
                statement.execute(insertStatement);
            }

            System.out.println("Inserting data to kicker stats table:");
            for(int i = 0; i < kickerStats.size(); i++){
                JsonObject kickerStat = kickerStats.getJsonObject(i);
                String insertStatement = "INSERT INTO " + KickerStatsTable.TABLE_NAME + "(" +
                        KickerStatsTable.COLUMN_PLAYER_ID + "," +
                        KickerStatsTable.COLUMN_WEEK + "," +
                        KickerStatsTable.COLUMN_OPPONENT + "," +
                        KickerStatsTable.COLUMN_19 + "," +
                        KickerStatsTable.COLUMN_20_29 + "," +
                        KickerStatsTable.COLUMN_30_39 + "," +
                        KickerStatsTable.COLUMN_40_49 + "," +
                        KickerStatsTable.COLUMN_50 + "," +
                        KickerStatsTable.MADE +
                        ") VALUES (" +
                        "'" + kickerStat.getString(KickerStatsTable.COLUMN_PLAYER_ID) + "'," +
                        kickerStat.getInteger(KickerStatsTable.COLUMN_WEEK) + "," +
                        "'" + kickerStat.getString(KickerStatsTable.COLUMN_OPPONENT) + "'," +
                        kickerStat.getInteger(KickerStatsTable.COLUMN_19) + "," +
                        kickerStat.getInteger(KickerStatsTable.COLUMN_20_29) + "," +
                        kickerStat.getInteger(KickerStatsTable.COLUMN_30_39) + "," +
                        kickerStat.getInteger(KickerStatsTable.COLUMN_40_49) + "," +
                        kickerStat.getInteger(KickerStatsTable.COLUMN_50) + "," +
                        kickerStat.getInteger(KickerStatsTable.MADE) +
                        ")";
                System.out.println("    " + insertStatement);
                statement.execute(insertStatement);
            }

            statement.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public double[] getDefensiveInputValues(String team, String opponent){

        double[] inputValues = null;

        String inputData = defensiveAverages.get(team) + "," + offensiveAverages.get(opponent);
        String vals[] = inputData.split(",");

        inputValues = new double[vals.length];

        for(int i = 0; i < vals.length; i++){
            inputValues[i] = Double.parseDouble(vals[i]);
        }

        return inputValues;
    }

    public double[] getOffenseInputValues(String playerId, String position, String team, String opponent){

        double[] inputValues = null;

        System.out.println("Fetching Input Values For " + playerId);
        playerId = playerId.replace("'","");

        try{

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM " + OffenseTable.TABLE_NAME +
                    " WHERE " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID + " = '" + playerId + "'";
            ResultSet resultSet = statement.executeQuery(query);

            if(!resultSet.first()){
                System.out.println("Requerying...");
                resultSet.close();
                resultSet = statement.executeQuery(query);
            }

            if(!resultSet.first()){
                System.out.println("Requerying Again...");
                resultSet.close();
                resultSet = statement.executeQuery(query);
            }else {

                double avg_pa = resultSet.getDouble(OffenseTable.COLUMN_AVG_PA);
                double avg_pa_td = resultSet.getDouble(OffenseTable.COLUMN_AVG_PA_TD);
                double avg_ru = resultSet.getDouble(OffenseTable.COLUMN_AVG_RU);
                double avg_ru_td = resultSet.getDouble(OffenseTable.COLUMN_AVG_RU_TD);
                double avg_re = resultSet.getDouble(OffenseTable.COLUMN_AVG_RE);
                double avg_re_td = resultSet.getDouble(OffenseTable.COLUMN_AVG_RE_TD);
                double avg_int = resultSet.getDouble(OffenseTable.COLUMN_AVG_INT);
                double avg_fum = resultSet.getDouble(OffenseTable.COLUMN_AVG_FUM);
                double avg_tgt = resultSet.getDouble(OffenseTable.COLUMN_AVG_TGT);
                double avg_rec = resultSet.getDouble(OffenseTable.COLUMN_AVG_REC);
                double avg_att = resultSet.getDouble(OffenseTable.COLUMN_AVG_ATT);

                String inputData;

                switch (position) {
                    case "qb":
                        inputData = avg_pa + "," +
                                avg_pa_td + "," +
                                avg_ru_td + "," +
                                avg_ru + "," +
                                avg_int + "," +
                                avg_fum + "," +
                                avg_att + "," +
                                defensiveAverages.get(opponent).toString() + "," +
                                offensiveAverages.get(team).toString();
                        break;
                    case "te":
                        inputData = avg_re + "," +
                                avg_re_td + "," +
                                avg_fum + "," +
                                avg_tgt + "," +
                                avg_rec + "," +
                                defensiveAverages.get(opponent).toString() + "," +
                                offensiveAverages.get(team).toString();
                        break;
                    case "wr":
                        inputData = avg_re + "," +
                                avg_re_td + "," +
                                avg_fum + "," +
                                avg_tgt + "," +
                                avg_rec + "," +
                                defensiveAverages.get(opponent).toString() + "," +
                                offensiveAverages.get(team).toString();
                        break;
                    case "rb":
                        inputData = avg_ru_td + "," +
                                avg_ru + "," +
                                avg_re + "," +
                                avg_re_td + "," +
                                avg_fum + "," +
                                avg_tgt + "," +
                                avg_rec + "," +
                                avg_att + "," +
                                defensiveAverages.get(opponent).toString() + "," +
                                offensiveAverages.get(team).toString();
                        break;
                    default:
                        inputData = avg_ru_td + "," +
                                avg_ru + "," +
                                avg_re + "," +
                                avg_re_td + "," +
                                avg_fum + "," +
                                avg_tgt + "," +
                                avg_rec + "," +
                                avg_att + "," +
                                defensiveAverages.get(opponent).toString() + "," +
                                offensiveAverages.get(team).toString();
                        break;

                }

                String[] vals = inputData.split(",");

                inputValues = new double[vals.length];

                for (int i = 0; i < vals.length; i++) {
                    inputValues[i] = Double.parseDouble(vals[i]);
                }

            }

            resultSet.close();
            statement.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return inputValues;
    }

    public void loadOffenseData(JsonArray data){

        JsonArray players = new JsonArray();
        JsonArray playerStats = new JsonArray();
        ArrayList<String> ids = new ArrayList<>();

        for(int i = 0; i < data.size(); i++){
            JsonObject dataObject = data.getJsonObject(i);
            String team = dataObject.getString("team").toLowerCase();
            String id = dataObject.getString("first_initial").toLowerCase() + dataObject.getString("last_name").toLowerCase() + team;
            JsonObject playerStat = new JsonObject()
                    .put(OffenseStatsTable.COLUMN_PLAYER_ID, id)
                    .put(OffenseStatsTable.COLUMN_WEEK, dataObject.getInteger("week"))
                    .put(OffenseStatsTable.COLUMN_OPPONENT, dataObject.getString("opponent").toLowerCase())
                    .put(OffenseStatsTable.COLUMN_PA_YDS, dataObject.getInteger("pa_yds"))
                    .put(OffenseStatsTable.COLUMN_PA_TDS, dataObject.getInteger("pa_tds"))
                    .put(OffenseStatsTable.COLUMN_PA_INT, dataObject.getInteger("pa_int"))
                    .put(OffenseStatsTable.COLUMN_PA_40_YD_C, dataObject.getInteger("pa_40_yd_comp"))
                    .put(OffenseStatsTable.COLUMN_PA_40_YD_TD, dataObject.getInteger("pa_40_yd_td"))
                    .put(OffenseStatsTable.COLUMN_RU_ATT, dataObject.getInteger("ru_att"))
                    .put(OffenseStatsTable.COLUMN_RU_YDS, dataObject.getInteger("ru_yds"))
                    .put(OffenseStatsTable.COLUMN_RU_TDS, dataObject.getInteger("ru_tds"))
                    .put(OffenseStatsTable.COLUMN_RU_40_YD_ATT, dataObject.getInteger("ru_40_yd_att"))
                    .put(OffenseStatsTable.COLUMN_RU_40_YD_TD, dataObject.getInteger("ru_40_yd_td"))
                    .put(OffenseStatsTable.COLUMN_RE_TGT, dataObject.getInteger("re_tgt"))
                    .put(OffenseStatsTable.COLUMN_RE_REC, dataObject.getInteger("re_rec"))
                    .put(OffenseStatsTable.COLUMN_RE_YDS, dataObject.getInteger("re_yds"))
                    .put(OffenseStatsTable.COLUMN_RE_TD, dataObject.getInteger("re_td"))
                    .put(OffenseStatsTable.COLUMN_RE_40_YD_REC, dataObject.getInteger("re_40_yd_rec"))
                    .put(OffenseStatsTable.COLUMN_RE_40_YD_TD, dataObject.getInteger("re_40_yd_td"))
                    .put(OffenseStatsTable.COLUMN_RT_TD, dataObject.getInteger("rt_td"))
                    .put(OffenseStatsTable.COLUMN_2PT, dataObject.getInteger("two_pt"))
                    .put(OffenseStatsTable.COLUMN_FUM, dataObject.getInteger("fum"));
            if(!id.equals("twatsoncin") && !id.equals("jmorganno") && !id.equals("rsmithdal") && !id.equals("jbrownari")) playerStats.add(playerStat);
            if(!ids.contains(id) && !id.equals("twatsoncin") && !id.equals("jmorganno") && !id.equals("rsmithdal") && !id.equals("jbrownari")){
                JsonObject player = new JsonObject()
                        .put(OffenseTable.COLUMN_PID, id)
                        .put(OffenseTable.COLUMN_TEAM_ID, team)
                        .put(OffenseTable.COLUMN_FNAME, dataObject.getString("first_initial"))
                        .put(OffenseTable.COLUMN_LNAME, dataObject.getString("last_name"))
                        .put(OffenseTable.COLUMN_POSITION, dataObject.getString("position").toLowerCase());
                players.add(player);
                ids.add(id);
            }
        }

        try{

            Statement statement = connection.createStatement();

            System.out.println("Inserting player data: ");
            for(int i = 0; i < players.size(); i++){
                JsonObject player = players.getJsonObject(i);
                String insertStatement = "INSERT INTO " + OffenseTable.TABLE_NAME + "(" +
                        OffenseTable.COLUMN_PID + "," +
                        OffenseTable.COLUMN_FNAME + "," +
                        OffenseTable.COLUMN_LNAME + "," +
                        OffenseTable.COLUMN_TEAM_ID + "," +
                        OffenseTable.COLUMN_POSITION + "," +
                        OffenseTable.COLUMN_AVG_PA + "," +
                        OffenseTable.COLUMN_AVG_PA_TD + "," +
                        OffenseTable.COLUMN_AVG_RU + "," +
                        OffenseTable.COLUMN_AVG_RU_TD + "," +
                        OffenseTable.COLUMN_AVG_RE + "," +
                        OffenseTable.COLUMN_AVG_RE_TD + "," +
                        OffenseTable.COLUMN_AVG_INT + "," +
                        OffenseTable.COLUMN_AVG_FUM + "," +
                        OffenseTable.COLUMN_AVG_TGT + "," +
                        OffenseTable.COLUMN_AVG_REC + "," +
                        OffenseTable.COLUMN_AVG_ATT +
                        ") VALUES (" +
                        "'" + player.getString(OffenseTable.COLUMN_PID) + "'," +
                        "'" + player.getString(OffenseTable.COLUMN_FNAME) + "'," +
                        "'" + player.getString(OffenseTable.COLUMN_LNAME) + "'," +
                        "'" + player.getString(OffenseTable.COLUMN_TEAM_ID) + "'," +
                        "'" + player.getString(OffenseTable.COLUMN_POSITION) + "'," +
                        "0,0,0,0,0,0,0,0,0,0,0)";
                System.out.println("    " + insertStatement);
                statement.execute(insertStatement);
            }

            System.out.println("Inserting player stats: ");
            for(int i = 0; i < playerStats.size(); i++){
                JsonObject playerStat = playerStats.getJsonObject(i);
                String insertStatement = "INSERT INTO " + OffenseStatsTable.TABLE_NAME + "(" +
                        OffenseStatsTable.COLUMN_PLAYER_ID + "," +
                        OffenseStatsTable.COLUMN_WEEK + "," +
                        OffenseStatsTable.COLUMN_OPPONENT + "," +
                        OffenseStatsTable.COLUMN_PA_YDS + "," +
                        OffenseStatsTable.COLUMN_PA_TDS + "," +
                        OffenseStatsTable.COLUMN_PA_INT + "," +
                        OffenseStatsTable.COLUMN_PA_40_YD_C + "," +
                        OffenseStatsTable.COLUMN_PA_40_YD_TD + "," +
                        OffenseStatsTable.COLUMN_RU_ATT + "," +
                        OffenseStatsTable.COLUMN_RU_YDS + "," +
                        OffenseStatsTable.COLUMN_RU_TDS + "," +
                        OffenseStatsTable.COLUMN_RU_40_YD_ATT + "," +
                        OffenseStatsTable.COLUMN_RU_40_YD_TD + "," +
                        OffenseStatsTable.COLUMN_RE_TGT + "," +
                        OffenseStatsTable.COLUMN_RE_REC + "," +
                        OffenseStatsTable.COLUMN_RE_YDS + "," +
                        OffenseStatsTable.COLUMN_RE_TD + "," +
                        OffenseStatsTable.COLUMN_RE_40_YD_REC + "," +
                        OffenseStatsTable.COLUMN_RE_40_YD_TD + "," +
                        OffenseStatsTable.COLUMN_RT_TD + "," +
                        OffenseStatsTable.COLUMN_2PT + "," +
                        OffenseStatsTable.COLUMN_FUM +
                        ") VALUES (" +
                        "'" + playerStat.getString(OffenseStatsTable.COLUMN_PLAYER_ID) + "'," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_WEEK) + "," +
                        "'" + playerStat.getString(OffenseStatsTable.COLUMN_OPPONENT) + "'," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_PA_YDS) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_PA_TDS) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_PA_INT) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_PA_40_YD_C) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_PA_40_YD_TD) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RU_ATT) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RU_YDS) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RU_TDS) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RU_40_YD_ATT) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RU_40_YD_TD) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RE_TGT) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RE_REC) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RE_YDS) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RE_TD) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RE_40_YD_REC) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RE_40_YD_TD) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_RT_TD) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_2PT) + "," +
                        playerStat.getInteger(OffenseStatsTable.COLUMN_FUM) +
                        ")";
                System.out.println("    " + insertStatement);
                statement.execute(insertStatement);
            }

            statement.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void initGameNumbers(){

        gameNumbers = new HashMap<>();

        try{

            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            ResultSet teams = statement.executeQuery("SELECT " + TeamTable.COLUMN_TID + " FROM " + TeamTable.TABLE_NAME);
            ResultSet games;

            while(teams.next()){
                String teamId = teams.getString(TeamTable.COLUMN_TID);
                games = statement2.executeQuery(
                        "SELECT " + OffensiveStatsTable.COLUMN_OPPONENT +
                                " FROM " + OffensiveStatsTable.TABLE_NAME +
                                " WHERE " + OffensiveStatsTable.COLUMN_TEAM_ID + " = '" + teamId + "'" +
                                " AND NOT " + OffensiveStatsTable.COLUMN_OPPONENT + " = 'bye'");
                games.last();
                gameNumbers.put(teamId, games.getRow());
                games.close();
            }

            //System.out.println(gameNumbers);

            teams.close();
            statement.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateKickerAverages(){

        double less_19;
        double bw_20_29;
        double bw_30_39;
        double bw_40_49;
        double more_50;
        double made;

        try {
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            Statement statement3 = connection.createStatement();

            ResultSet kickers = statement.executeQuery("Select " + KickerTable.COLUMN_PID + "," + KickerTable.COLUMN_TEAM_ID + " FROM " + KickerTable.TABLE_NAME);

            System.out.println("Updating Kicker Averages: ");
            while(kickers.next()){

                String id = kickers.getString(KickerTable.COLUMN_PID);
                String team = kickers.getString(KickerTable.COLUMN_TEAM_ID);
                //System.out.println(id + ":");

                ResultSet stat = statement2.executeQuery("Select SUM(" + KickerStatsTable.COLUMN_19 + ") FROM " + KickerStatsTable.TABLE_NAME + " WHERE " + KickerStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                stat.first();
                less_19 = stat.getDouble(1)/(double)gameNumbers.get(team);
                stat.close();
                //System.out.println("    Less than 19 yds = " + less_19);

                stat = statement2.executeQuery("Select SUM(" + KickerStatsTable.COLUMN_20_29 + ") FROM " + KickerStatsTable.TABLE_NAME + " WHERE " + KickerStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                stat.first();
                bw_20_29 = stat.getDouble(1)/(double)gameNumbers.get(team);
                stat.close();
                //System.out.println("    between 20 and 29 yds = " + bw_20_29);

                stat = statement2.executeQuery("Select SUM(" + KickerStatsTable.COLUMN_30_39 + ") FROM " + KickerStatsTable.TABLE_NAME + " WHERE " + KickerStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                stat.first();
                bw_30_39 = stat.getDouble(1)/(double)gameNumbers.get(team);
                stat.close();
                //System.out.println("    between 30 and 39 yds = " + bw_30_39);

                stat = statement2.executeQuery("Select SUM(" + KickerStatsTable.COLUMN_40_49 + ") FROM " + KickerStatsTable.TABLE_NAME + " WHERE " + KickerStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                stat.first();
                bw_40_49 = stat.getDouble(1)/(double)gameNumbers.get(team);
                stat.close();
                //System.out.println("    between 40 and 49 yds = " + bw_40_49);

                stat = statement2.executeQuery("Select SUM(" + KickerStatsTable.COLUMN_50 + ") FROM " + KickerStatsTable.TABLE_NAME + " WHERE " + KickerStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                stat.first();
                more_50 = stat.getDouble(1)/(double)gameNumbers.get(team);
                stat.close();
                //System.out.println("    more than 50 yds = " + more_50);

                stat = statement2.executeQuery("Select SUM(" + KickerStatsTable.MADE + ") FROM " + KickerStatsTable.TABLE_NAME + " WHERE " + KickerStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                stat.first();
                made = stat.getDouble(1)/(double)gameNumbers.get(team);
                stat.close();
                //System.out.println("    made = " + made);

                String updateStatement = "UPDATE " + KickerTable.TABLE_NAME + " SET " +
                        KickerTable.COLUMN_AVG_0_19 + "=" + less_19 + "," +
                        KickerTable.COLUMN_AVG_20_29 + "=" + bw_20_29 + "," +
                        KickerTable.COLUMN_AVG_30_39 + "=" + bw_30_39 + "," +
                        KickerTable.COLUMN_AVG_40_49 + "=" + bw_40_49 + "," +
                        KickerTable.COLUMN_AVG_50 + "=" + more_50 + "," +
                        KickerTable.COLUMN_AVG_MADE + "=" + made +
                        " WHERE " + KickerTable.COLUMN_PID + "='" + id + "'";

                System.out.println("    " + updateStatement);
                statement3.execute(updateStatement);

            }

            kickers.close();
            statement.close();
            statement2.close();
            statement3.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateTeamAverages(){

        Object[] teams = gameNumbers.keySet().toArray();

        double avg_pa;
        double avg_te_pa;
        double avg_wr_pa;
        double avg_ru;
        double avg_pts;
        double avg_sacks_allowed;
        double avg_safety_allowed;
        double avg_fum_allowed;
        double avg_int_allowed;
        double avg_blk_allowed;
        double avg_ret_allowed;

        double avg_pa_allowed;
        double avg_te_pa_allowed;
        double avg_wr_pa_allowed;
        double avg_ru_allowed;
        double avg_pts_allowed;
        double avg_sacks;
        double avg_safety;
        double avg_fum;
        double avg_int;
        double avg_blk;
        double avg_ret;

        try {
            Statement statement = connection.createStatement();
            ResultSet result;

            System.out.println("Updating Team Averages: ");
            for (int i = 0; i < teams.length; i++) {
                String team = teams[i].toString();
                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_PA_YDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_pa_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg passing yards allowed = " + avg_pa_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_PA_YDS +
                        ") FROM " + OffenseStatsTable.TABLE_NAME + " INNER JOIN " + OffenseTable.TABLE_NAME +
                        " ON " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                        " WHERE " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_pa = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg passing yards = " + avg_pa);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_YDS +
                        ") FROM " + OffenseStatsTable.TABLE_NAME + " INNER JOIN " + OffenseTable.TABLE_NAME +
                        " ON " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                        " WHERE " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_OPPONENT + " = '" + team +
                        "' AND " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_POSITION + " = 'te'");
                result.first();
                avg_te_pa_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg te passing yards allowed = " + avg_te_pa_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_YDS +
                        ") FROM " + OffenseStatsTable.TABLE_NAME + " INNER JOIN " + OffenseTable.TABLE_NAME +
                        " ON " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                        " WHERE " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_TEAM_ID + " = '" + team +
                        "' AND " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_POSITION + " = 'te'");
                result.first();
                avg_te_pa = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg te passing yards = " + avg_te_pa);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_YDS +
                        ") FROM " + OffenseStatsTable.TABLE_NAME + " INNER JOIN " + OffenseTable.TABLE_NAME +
                        " ON " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                        " WHERE " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_OPPONENT + " = '" + team +
                        "' AND " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_POSITION + " = 'wr'");
                result.first();
                avg_wr_pa_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg wr passing yards allowed = " + avg_wr_pa_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_YDS +
                        ") FROM " + OffenseStatsTable.TABLE_NAME + " INNER JOIN " + OffenseTable.TABLE_NAME +
                        " ON " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                        " WHERE " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_TEAM_ID + " = '" + team +
                        "' AND " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_POSITION + " = 'wr'");
                result.first();
                avg_wr_pa = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg wr passing yards = " + avg_wr_pa);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RU_YDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_ru_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg rushing yards allowed = " + avg_ru_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RU_YDS +
                        ") FROM " + OffenseStatsTable.TABLE_NAME + " INNER JOIN " + OffenseTable.TABLE_NAME +
                        " ON " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + "=" + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_PID +
                        " WHERE " + OffenseTable.TABLE_NAME + "." + OffenseTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_ru = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg rushing yards = " + avg_ru);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_PTS_ALLOWED + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_pts_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg points allowed = " + avg_pts_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_PTS_ALLOWED + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_pts = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg points allowed = " + avg_pts);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_SACK + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_sacks = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg sacks = " + avg_sacks);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_SACK + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_sacks_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg sacks allowed = " + avg_sacks_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_SAFETY + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_safety = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg safety = " + avg_safety);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_SAFETY + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_safety_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg safety allowed = " + avg_safety_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_FUM + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_fum = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg fumble = " + avg_fum);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_FUM + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_fum_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg fumble allowed = " + avg_fum_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_INT + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_int = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg interceptions = " + avg_int);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_INT + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_int_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg interceptions allowed = " + avg_int_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_BLK + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_blk = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg blocked kicks = " + avg_blk);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_BLK + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_blk_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg blocked kicks allowed = " + avg_blk_allowed);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_RET + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + " = '" + team + "'");
                result.first();
                avg_ret = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg ret = " + avg_ret);
                result.close();

                result = statement.executeQuery("SELECT SUM(" + DefensiveStatsTable.COLUMN_RET + ") FROM " + DefensiveStatsTable.TABLE_NAME + " WHERE " + DefensiveStatsTable.COLUMN_OPPONENT + " = '" + team + "'");
                result.first();
                avg_ret_allowed = result.getDouble(1)/(double)gameNumbers.get(team);
                //System.out.println(team + " avg ret allowed = " + avg_ret_allowed);
                result.close();

                String updateStatement = "UPDATE " + DefensiveStatsTable.TABLE_NAME + " SET " +
                        DefensiveStatsTable.COLUMN_AVG_PA + "=" + avg_pa_allowed + "," +
                        DefensiveStatsTable.COLUMN_AVG_TE + "=" + avg_te_pa_allowed + "," +
                        DefensiveStatsTable.COLUMN_AVG_WR + "=" + avg_wr_pa_allowed + "," +
                        DefensiveStatsTable.COLUMN_AVG_RU + "=" + avg_ru_allowed + "," +
                        DefensiveStatsTable.COLUMN_AVG_PTS + "=" + avg_pts_allowed + "," +
                        DefensiveStatsTable.COLUMN_AVG_SACKS + "=" + avg_sacks + "," +
                        DefensiveStatsTable.COLUMN_AVG_SAFETY + "=" + avg_safety + "," +
                        DefensiveStatsTable.COLUMN_AVG_FUM + "=" + avg_fum + "," +
                        DefensiveStatsTable.COLUMN_AVG_INT + "=" + avg_int + "," +
                        DefensiveStatsTable.COLUMN_AVG_BLK + "=" + avg_blk + "," +
                        DefensiveStatsTable.COLUMN_AVG_RET + "=" + avg_ret +
                        " WHERE " + DefensiveStatsTable.COLUMN_TEAM_ID + "='" + team + "'";

                System.out.println("    " + updateStatement);
                statement.execute(updateStatement);

                updateStatement = "UPDATE " + OffensiveStatsTable.TABLE_NAME + " SET " +
                        OffensiveStatsTable.COLUMN_AVG_PA + "=" + avg_pa + "," +
                        OffensiveStatsTable.COLUMN_AVG_TE + "=" + avg_te_pa + "," +
                        OffensiveStatsTable.COLUMN_AVG_WR + "=" + avg_wr_pa + "," +
                        OffensiveStatsTable.COLUMN_AVG_RU + "=" + avg_ru + "," +
                        OffensiveStatsTable.COLUMN_AVG_PTS + "=" + avg_pts + "," +
                        OffensiveStatsTable.COLUMN_AVG_SACKS + "=" + avg_sacks_allowed + "," +
                        OffensiveStatsTable.COLUMN_AVG_SAFETY + "=" + avg_safety_allowed + "," +
                        OffensiveStatsTable.COLUMN_AVG_FUM + "=" + avg_fum_allowed + "," +
                        OffensiveStatsTable.COLUMN_AVG_INT + "=" + avg_int_allowed + "," +
                        OffensiveStatsTable.COLUMN_AVG_BLK + "=" + avg_blk_allowed + "," +
                        OffensiveStatsTable.COLUMN_AVG_RET + "=" + avg_ret_allowed +
                        " WHERE " + OffensiveStatsTable.COLUMN_TEAM_ID + "='" + team + "'";

                System.out.println("    " + updateStatement);
                statement.execute(updateStatement);

            }

            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateOffenseAverages(){

        double avg_pa;
        double avg_pa_td;
        double avg_ru;
        double avg_ru_td;
        double avg_re;
        double avg_re_td;
        double avg_int;
        double avg_fum;
        double avg_tgt;
        double avg_rec;
        double avg_att;


        try{

            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            Statement statement3 = connection.createStatement();
            ResultSet players = statement.executeQuery("Select " + OffenseTable.COLUMN_PID + "," + OffenseTable.COLUMN_TEAM_ID + " FROM " + OffenseTable.TABLE_NAME);

            System.out.println("Updating Player Averages: ");
            while(players.next()){
                String id = players.getString(OffenseTable.COLUMN_PID);
                //System.out.println(id + ": ");

                double gamesPlayed = 0;

                Statement stats = connection.createStatement();
                ResultSet games = stats.executeQuery("SELECT * FROM " + OffenseStatsTable.TABLE_NAME +
                        " WHERE " + OffenseStatsTable.TABLE_NAME + "." + OffenseStatsTable.COLUMN_PLAYER_ID + " = '" + id + "'");

                while (games.next()){

                    if(games.getInt(OffenseStatsTable.COLUMN_2PT) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_FUM) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_PA_40_YD_C) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_PA_40_YD_TD) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_PA_INT) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_PA_TDS) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_PA_YDS) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RE_40_YD_REC) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RE_40_YD_TD) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RE_REC) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RE_TGT) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RE_YDS) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RU_40_YD_ATT) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RU_40_YD_TD) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RU_ATT) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RU_TDS) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RU_YDS) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RT_TD) != 0 ||
                            games.getInt(OffenseStatsTable.COLUMN_RE_TD) != 0) gamesPlayed++;

                }
                games.close();
                stats.close();

                if(gamesPlayed != 0) {
                    ResultSet stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_PA_YDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_pa = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average passing yards: " + avg_pa);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_PA_TDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_pa_td = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average passing touchdowns: " + avg_pa_td);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_YDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_re = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average receiving yards: " + avg_re);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_TD + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_re_td = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average receiving touchdowns: " + avg_re_td);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RU_YDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_ru = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average rushing yards: " + avg_ru);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RU_TDS + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_ru_td = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average rushing touchdowns: " + avg_ru_td);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_PA_INT + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_int = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average interceptions: " + avg_int);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_FUM + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_fum = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average fumbles: " + avg_fum);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_TGT + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_tgt = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average receiving targets: " + avg_tgt);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RE_REC + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_rec = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average receptions: " + avg_rec);

                    stat = statement2.executeQuery("SELECT SUM(" + OffenseStatsTable.COLUMN_RU_ATT + ") FROM " + OffenseStatsTable.TABLE_NAME + " WHERE " + OffenseStatsTable.COLUMN_PLAYER_ID + "='" + id + "'");
                    stat.first();
                    avg_att = stat.getDouble(1) / gamesPlayed;
                    stat.close();
                    //System.out.println("    average rushing attempts: " + avg_att);
                }else{
                    avg_pa = 0;
                    avg_pa_td = 0;
                    avg_ru = 0;
                    avg_ru_td = 0;
                    avg_re = 0;
                    avg_re_td = 0;
                    avg_int = 0;
                    avg_fum = 0;
                    avg_tgt = 0;
                    avg_rec = 0;
                    avg_att = 0;
                }

                String updateStatement = "UPDATE " + OffenseTable.TABLE_NAME + " SET " +
                        OffenseTable.COLUMN_AVG_PA + "=" + avg_pa + "," +
                        OffenseTable.COLUMN_AVG_PA_TD + "=" + avg_pa_td + "," +
                        OffenseTable.COLUMN_AVG_RU + "=" + avg_ru + "," +
                        OffenseTable.COLUMN_AVG_RU_TD + "=" + avg_ru_td + "," +
                        OffenseTable.COLUMN_AVG_RE + "=" + avg_re + "," +
                        OffenseTable.COLUMN_AVG_RE_TD + "=" + avg_re_td + "," +
                        OffenseTable.COLUMN_AVG_INT + "=" + avg_int + "," +
                        OffenseTable.COLUMN_AVG_FUM + "=" + avg_fum + "," +
                        OffenseTable.COLUMN_AVG_TGT + "=" + avg_tgt + "," +
                        OffenseTable.COLUMN_AVG_REC + "=" + avg_rec + "," +
                        OffenseTable.COLUMN_AVG_ATT + "=" + avg_att +
                        " WHERE " + OffenseTable.COLUMN_PID + "='" + id + "'";

                System.out.println("    " + updateStatement);
                statement3.execute(updateStatement);
            }

            players.close();
            statement.close();
            statement2.close();
            statement3.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class TeamTable{

        public static final String TABLE_NAME = "Team";
        public static final String COLUMN_TID = "tid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LOCATION = "location";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_TID + " VARCHAR(3)," +
                COLUMN_NAME + " VARCHAR(255)," +
                COLUMN_LOCATION + " VARCHAR(255)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TID + "_pk PRIMARY KEY (" + COLUMN_TID + "))";

    }

    public static class DefensiveStatsTable{

        public static final String TABLE_NAME = "DefensiveStats";
        public static final String COLUMN_TEAM_ID = "teamId";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_OPPONENT = "opponent";
        public static final String COLUMN_PTS_ALLOWED = "pts_allowed";
        public static final String COLUMN_SACK = "sack";
        public static final String COLUMN_SAFETY = "safety";
        public static final String COLUMN_INT = "interception";
        public static final String COLUMN_FUM = "fum_rec";
        public static final String COLUMN_TD = "td";
        public static final String COLUMN_BLK = "blk_kick";
        public static final String COLUMN_RET = "ret_td";
        public static final String COLUMN_AVG_PA = "avg_pa_yds";
        public static final String COLUMN_AVG_TE = "avg_te_yds";
        public static final String COLUMN_AVG_WR = "avg_wr_yds";
        public static final String COLUMN_AVG_RU = "avg_ru_yds";
        public static final String COLUMN_AVG_PTS = "avg_pts_allowed";
        public static final String COLUMN_AVG_SACKS = "avg_sacks_allowed";
        public static final String COLUMN_AVG_SAFETY = "avg_safety_allowed";
        public static final String COLUMN_AVG_FUM = "avg_fum_rec";
        public static final String COLUMN_AVG_INT = "avg_int";
        public static final String COLUMN_AVG_BLK = "avg_blk";
        public static final String COLUMN_AVG_RET = "avg_ret";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_TEAM_ID + " VARCHAR(3)," +
                COLUMN_WEEK + " INT(2)," +
                COLUMN_OPPONENT + " VARCHAR(3)," +
                COLUMN_PTS_ALLOWED + " INT(5)," +
                COLUMN_SACK + " INT(2)," +
                COLUMN_SAFETY + " INT(2)," +
                COLUMN_INT + " INT(2)," +
                COLUMN_FUM + " INT(2)," +
                COLUMN_TD + " INT(2)," +
                COLUMN_BLK + " INT(2)," +
                COLUMN_RET + " INT(3)," +
                COLUMN_AVG_PA + " FLOAT(7,3)," +
                COLUMN_AVG_TE + " FLOAT(7,3)," +
                COLUMN_AVG_WR + " FLOAT(7,3)," +
                COLUMN_AVG_RU + " FLOAT(7,3)," +
                COLUMN_AVG_PTS + " FLOAT(7,3)," +
                COLUMN_AVG_SACKS + " FLOAT(7,3)," +
                COLUMN_AVG_SAFETY + " FLOAT(7,3)," +
                COLUMN_AVG_FUM + " FLOAT(7,3)," +
                COLUMN_AVG_INT + " FLOAT(7,3)," +
                COLUMN_AVG_BLK + " FLOAT(7,3)," +
                COLUMN_AVG_RET + " FLOAT(7,3)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TEAM_ID + "_" + COLUMN_WEEK + "_pk PRIMARY KEY (" + COLUMN_TEAM_ID + "," + COLUMN_WEEK  + ")," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TEAM_ID + "_fk " +
                "FOREIGN KEY (" + COLUMN_TEAM_ID + ")" +
                " REFERENCES " + TeamTable.TABLE_NAME + " (" + TeamTable.COLUMN_TID +"))";

    }

    public static class OffensiveStatsTable{

        public static final String TABLE_NAME = "OffensiveStats";
        public static final String COLUMN_TEAM_ID = "teamId";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_OPPONENT = "opponent";
        public static final String COLUMN_AVG_PA = "avg_pa_yds";
        public static final String COLUMN_AVG_TE = "avg_te_yds";
        public static final String COLUMN_AVG_WR = "avg_wr_yds";
        public static final String COLUMN_AVG_RU = "avg_ru_yds";
        public static final String COLUMN_AVG_PTS = "avg_pts_allowed";
        public static final String COLUMN_AVG_SACKS = "avg_sacks_allowed";
        public static final String COLUMN_AVG_SAFETY = "avg_safety_allowed";
        public static final String COLUMN_AVG_FUM = "avg_fum_rec";
        public static final String COLUMN_AVG_INT = "avg_int";
        public static final String COLUMN_AVG_BLK = "avg_blk";
        public static final String COLUMN_AVG_RET = "avg_ret";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_TEAM_ID + " VARCHAR(3)," +
                COLUMN_WEEK + " INT(2)," +
                COLUMN_OPPONENT + " VARCHAR(3)," +
                COLUMN_AVG_PA + " FLOAT(7,3)," +
                COLUMN_AVG_TE + " FLOAT(7,3)," +
                COLUMN_AVG_WR + " FLOAT(7,3)," +
                COLUMN_AVG_RU + " FLOAT(7,3)," +
                COLUMN_AVG_PTS + " FLOAT(7,3)," +
                COLUMN_AVG_SACKS + " FLOAT(7,3)," +
                COLUMN_AVG_SAFETY + " FLOAT(7,3)," +
                COLUMN_AVG_FUM + " FLOAT(7,3)," +
                COLUMN_AVG_INT + " FLOAT(7,3)," +
                COLUMN_AVG_BLK + " FLOAT(7,3)," +
                COLUMN_AVG_RET + " FLOAT(7,3)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TEAM_ID + "_" + COLUMN_WEEK + "_pk PRIMARY KEY (" + COLUMN_TEAM_ID + "," + COLUMN_WEEK  + ")," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TEAM_ID + "_fk " +
                "FOREIGN KEY (" + COLUMN_TEAM_ID + ")" +
                " REFERENCES " + TeamTable.TABLE_NAME + " (" + TeamTable.COLUMN_TID +"))";
    }

    public static class OffenseTable{

        public static final String TABLE_NAME = "Offense";
        public static final String COLUMN_PID = "pid";
        public static final String COLUMN_FNAME = "fname";
        public static final String COLUMN_LNAME = "lname";
        public static final String COLUMN_TEAM_ID = "teamId";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_AVG_PA = "avg_pa";
        public static final String COLUMN_AVG_PA_TD = "avg_pa_td";
        public static final String COLUMN_AVG_RU = "avg_ru";
        public static final String COLUMN_AVG_RU_TD = "avg_ru_td";
        public static final String COLUMN_AVG_RE = "avg_re";
        public static final String COLUMN_AVG_RE_TD = "avg_re_td";
        public static final String COLUMN_AVG_INT = "avg_int";
        public static final String COLUMN_AVG_FUM = "avg_fum";
        public static final String COLUMN_AVG_TGT = "avg_tgt";
        public static final String COLUMN_AVG_REC = "avg_rec";
        public static final String COLUMN_AVG_ATT = "avg_att";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PID + " VARCHAR(255)," +
                COLUMN_TEAM_ID + " VARCHAR(3)," +
                COLUMN_FNAME + " VARCHAR(255)," +
                COLUMN_LNAME + " VARCHAR(255)," +
                COLUMN_POSITION + " VARCHAR(10)," +
                COLUMN_AVG_PA + " FLOAT(7,3)," +
                COLUMN_AVG_PA_TD + " FLOAT(7,3)," +
                COLUMN_AVG_RU + " FLOAT(7,3)," +
                COLUMN_AVG_RU_TD + " FLOAT(7,3)," +
                COLUMN_AVG_RE + " FLOAT(7,3)," +
                COLUMN_AVG_RE_TD + " FLOAT(7,3)," +
                COLUMN_AVG_INT + " FLOAT(7,3)," +
                COLUMN_AVG_FUM + " FLOAT(7,3)," +
                COLUMN_AVG_TGT + " FLOAT(7,3)," +
                COLUMN_AVG_REC + " FLOAT(7,3)," +
                COLUMN_AVG_ATT + " FLOAT(7,3)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_PID + "_pk PRIMARY KEY (" + COLUMN_PID + ")," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TEAM_ID + "_fk " +
                "FOREIGN KEY (" + COLUMN_TEAM_ID + ")" +
                " REFERENCES " + TeamTable.TABLE_NAME + " (" + TeamTable.COLUMN_TID +"))";

    }

    public static class KickerTable{

        public static final String TABLE_NAME = "Kicker";
        public static final String COLUMN_PID = "pid";
        public static final String COLUMN_FNAME = "fname";
        public static final String COLUMN_LNAME = "lname";
        public static final String COLUMN_TEAM_ID = "teamId";
        public static final String COLUMN_AVG_0_19 = "avg_less_19";
        public static final String COLUMN_AVG_20_29 = "avg_20_29";
        public static final String COLUMN_AVG_30_39 = "avg_30_39";
        public static final String COLUMN_AVG_40_49 = "avg_40_49";
        public static final String COLUMN_AVG_50 = "avg_50";
        public static final String COLUMN_AVG_MADE = "avg_made";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PID + " VARCHAR(255)," +
                COLUMN_TEAM_ID + " VARCHAR(3)," +
                COLUMN_FNAME + " VARCHAR(255)," +
                COLUMN_LNAME + " VARCHAR(255)," +
                COLUMN_AVG_0_19 + " FLOAT(7,3)," +
                COLUMN_AVG_20_29 + " FLOAT(7,3)," +
                COLUMN_AVG_30_39 + " FLOAT(7,3)," +
                COLUMN_AVG_40_49 + " FLOAT(7,3)," +
                COLUMN_AVG_50 + " FLOAT(7,3)," +
                COLUMN_AVG_MADE + " FLOAT(7,3)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_PID + "_pk PRIMARY KEY (" + COLUMN_PID + ")," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_TEAM_ID + "_fk " +
                "FOREIGN KEY (" + COLUMN_TEAM_ID + ")" +
                " REFERENCES " + TeamTable.TABLE_NAME + " (" + TeamTable.COLUMN_TID +"))";

    }

    public class OffenseStatsTable{

        public static final String TABLE_NAME = "OffenseStats";
        public static final String COLUMN_PLAYER_ID = "playerId";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_OPPONENT = "opponent";
        public static final String COLUMN_PA_YDS = "pa_yds";
        public static final String COLUMN_PA_TDS = "pa_tds";
        public static final String COLUMN_PA_INT = "pa_int";
        public static final String COLUMN_PA_40_YD_C = "pa_40_yd_comp";
        public static final String COLUMN_PA_40_YD_TD = "pa_40_yd_td";
        public static final String COLUMN_RU_ATT = "ru_att";
        public static final String COLUMN_RU_YDS = "ru_yds";
        public static final String COLUMN_RU_TDS = "ru_tds";
        public static final String COLUMN_RU_40_YD_ATT = "ru_40_yd_att";
        public static final String COLUMN_RU_40_YD_TD = "ru_40_yd_td";
        public static final String COLUMN_RE_TGT = "re_tgt";
        public static final String COLUMN_RE_REC = "re_rec";
        public static final String COLUMN_RE_YDS = "re_yds";
        public static final String COLUMN_RE_TD = "re_td";
        public static final String COLUMN_RE_40_YD_REC = "re_40_yd_rec";
        public static final String COLUMN_RE_40_YD_TD = "re_40_yd_td";
        public static final String COLUMN_RT_TD = "rt_td";
        public static final String COLUMN_2PT = "2pt";
        public static final String COLUMN_FUM = "fum";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PLAYER_ID + " VARCHAR(255)," +
                COLUMN_WEEK + " INT(2)," +
                COLUMN_OPPONENT + " VARCHAR(3)," +
                COLUMN_PA_YDS + " INT(3)," +
                COLUMN_PA_TDS + " INT(3)," +
                COLUMN_PA_INT + " INT(2)," +
                COLUMN_PA_40_YD_C + " INT(3)," +
                COLUMN_PA_40_YD_TD + " INT(3)," +
                COLUMN_RU_ATT + " INT(3)," +
                COLUMN_RU_YDS + " INT(3)," +
                COLUMN_RU_TDS + " INT(3)," +
                COLUMN_RU_40_YD_ATT + " INT(3)," +
                COLUMN_RU_40_YD_TD + " INT(3)," +
                COLUMN_RE_TGT + " INT(3)," +
                COLUMN_RE_REC + " INT(3)," +
                COLUMN_RE_YDS + " INT(3)," +
                COLUMN_RE_TD + " INT(3)," +
                COLUMN_RE_40_YD_REC + " INT(3)," +
                COLUMN_RE_40_YD_TD + " INT(3)," +
                COLUMN_RT_TD + " INT(3)," +
                COLUMN_2PT + " INT(3)," +
                COLUMN_FUM + " INT(3)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_PLAYER_ID + "_" + COLUMN_WEEK + "_pk PRIMARY KEY (" + COLUMN_PLAYER_ID + "," + COLUMN_WEEK  + ")," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_PLAYER_ID + "_fk " +
                "FOREIGN KEY (" + COLUMN_PLAYER_ID + ")" +
                " REFERENCES " + OffenseTable.TABLE_NAME + " (" + OffenseTable.COLUMN_PID +"))";

    }

    public class KickerStatsTable{

        public static final String TABLE_NAME = "KickerStats";
        public static final String COLUMN_PLAYER_ID = "playerId";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_OPPONENT = "opponent";
        public static final String COLUMN_19 = "less_19";
        public static final String COLUMN_20_29 = "bw_20_29";
        public static final String COLUMN_30_39 = "bw_30_39";
        public static final String COLUMN_40_49 = "bw_40_49";
        public static final String COLUMN_50 = "more_50";
        public static final String MADE = "made";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PLAYER_ID + " VARCHAR(255)," +
                COLUMN_WEEK + " INT(2)," +
                COLUMN_OPPONENT + " VARCHAR(3)," +
                COLUMN_19 + " INT(2)," +
                COLUMN_20_29 + " INT(2)," +
                COLUMN_30_39 + " INT(2)," +
                COLUMN_40_49 + " INT(2)," +
                COLUMN_50 + " INT(2)," +
                MADE + " INT(2)," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_PLAYER_ID + "_" + COLUMN_WEEK + "_pk PRIMARY KEY (" + COLUMN_PLAYER_ID + "," + COLUMN_WEEK  + ")," +
                "CONSTRAINT " + TABLE_NAME + "_" + COLUMN_PLAYER_ID + "_fk " +
                "FOREIGN KEY (" + COLUMN_PLAYER_ID + ")" +
                " REFERENCES " + KickerTable.TABLE_NAME + " (" + KickerTable.COLUMN_PID +"))";

    }

}
