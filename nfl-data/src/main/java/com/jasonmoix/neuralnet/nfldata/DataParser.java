package com.jasonmoix.neuralnet.nfldata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * Created by jmoix on 10/15/2015.
 */
public class DataParser {

    private static final String[] teamKeys = new String[]{
            "Mia","SF","TB", "Ten", "Min", "Cin", "Jax", "Det",
            "SD", "Ind", "Dal", "Was", "Atl", "NO", "Pit", "Chi", "Car",
            "Sea", "NYJ", "Den", "Bal", "StL", "NYG", "KC", "Buf", "Phi",
            "Ari", "NE", "GB", "Hou", "Cle", "Oak"};

    private static final String[] teamLocation = new String[]{
            "Miami", "San Francisco", "Tampa Bay", "Tennessee", "Minnesota", "Cincinnati", "Jacksonville",
            "Detroit", "San Diego", "Indianapolis", "Dallas", "Washington", "Atlanta", "New Orleans", "Pittsburgh",
            "Chicago", "Carolina", "Seattle", "New York", "Denver", "Baltimore", "St. Louis", "New York",
            "Kansas City", "Buffalo", "Philadelphia", "Arizona", "New England", "Green Bay", "Houston", "Cleveland",
            "Oakland"};

    private static final String[] teamName = new String[]{
            "Dolphins", "49ers", "Buccaneers", "Titans", "Vikings", "Bengals", "Jaguars", "Lions", "Chargers", "Colts",
            "Cowboys", "Redskins", "Falcons", "Saints", "Steelers", "Bears", "Panthers", "Seahawks", "Jets", "Broncos",
            "Ravens", "Rams", "Giants", "Chiefs", "Bills", "Eagles", "Cardinals", "Patriots", "Packers", "Texans", "Browns", "Raiders"};

    private HashMap<String, String> teamLocations;
    private HashMap<String, String> teamNames;

    public DataParser(){
        getTeamHashMap();
    }

    public JsonArray fetchOffensiveData(){

        JsonArray offensiveData = new JsonArray();

        File[] files = (new File("C:\\GameData\\Offense")).listFiles();

        for(File week : files) {

            File[] dataFiles = week.listFiles();

            for(File data : dataFiles) {

                try {

                    Document document = Jsoup.parse(data, "UTF-8", "");

                    Elements playertable = document.getElementsByClass("players");

                    String playerData = playertable.get(0).text();
                    playerData = playerData.replace("Notes", "Note");

                    String[] players = (playerData.split("Note"));

                    for (int i = 1; i < players.length; i++) {
                        players[i] = removeIllegalChars(players[i]);
                        offensiveData.add(parseOffensivePlayerData(players[i], Integer.parseInt(week.getName())));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        return offensiveData;

    }

    public JsonArray fetchDefensiveData(){

        JsonArray defensiveData = new JsonArray();
        File[] files = (new File("C:\\GameData\\Defense")).listFiles();

        for(File week : files) {

            File[] dataFiles = week.listFiles();

            for(File data : dataFiles) {

                try {

                    Document document = Jsoup.parse(data, "UTF-8", "");

                    Elements playertable = document.getElementsByClass("players");

                    String playerData = playertable.get(0).text();
                    playerData = playerData.replace("Notes", "Note");

                    String[] players = (playerData.split("Note"));

                    for (int i = 1; i < players.length; i++) {
                        players[i] = removeIllegalChars(players[i]);
                        defensiveData.add(parseDefensivePlayerData(players[i], Integer.parseInt(week.getName())));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return defensiveData;

    }

    public JsonArray fetchKickerData(){

        JsonArray kickerData = new JsonArray();

        File[] files = (new File("C:\\GameData\\Kicker")).listFiles();

        for(File week : files) {

            File[] dataFiles = week.listFiles();

            for(File data : dataFiles) {
                try {

                    Document document = Jsoup.parse(data, "UTF-8", "");

                    Elements playertable = document.getElementsByClass("players");

                    String playerData = playertable.get(0).text();
                    playerData = playerData.replace("Notes", "Note");

                    String[] players = (playerData.split("Note"));

                    for (int i = 1; i < players.length; i++) {
                        players[i] = removeIllegalChars(players[i]);
                        kickerData.add(parseKickerPlayerData(players[i], Integer.parseInt(week.getName())));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return kickerData;
    }

    public String removeIllegalChars(String player){
        player = player.replace(" P ", " ");
        player = player.replace(" Q ", " ");
        player = player.replace(" O ", " ");
        player = player.replace(" D ", " ");
        player = player.replace(" IR ", " ");
        player = player.replace(" @ ", " ");
        player = player.replace(" vs ", " ");
        player = player.replace(" Final ", " ");
        player = player.replace(" (W) ", " ");
        player = player.replace(" (L) ", " ");
        player = player.replace(" (T) ", " ");
        player = player.replace(" Player ", " ");
        player = player.replace(" Video ", " ");
        player = player.replace(" Playlist ", " ");
        player = player.replace(" New ", " ");
        player = player.replace(" No ", " ");
        player = player.replace(" new ", " ");
        player = player.replace(" player ", " ");
        player = player.replace(" Sr. ", " ");
        player = player.replace(" Jr. ", " ");
        player = player.replace(" II ", " ");
        player = player.replace(" III ", " ");
        player = player.replace(" IV ", " ");
        player = player.replace("San Francisco ", "SF ");
        player = player.replace("San Diego ", "SD ");
        player = player.replace("Tampa Bay ", "TB ");
        player = player.replace("New England ", "NE ");
        player = player.replace("St. Louis ", "StL ");
        player = player.replace("Green Bay ", "GB ");
        player = player.replace("Kansas City ", "KC ");
        player = player.replace(" Injured Reserve ", " ");
        player = player.replace(" Questionable ", " ");
        player = player.replace(" Probable ", " ");
        player = player.replace(" Battered Midgets ", " FA ");
        player = player.replace(" Linda's Expert Team ", " FA ");
        player = player.replace(" Dezpicable Me ", " FA ");
        player = player.replace(" Luck Dynasty ", " FA ");
        player = player.replace(" ROMOn Empire ", " FA ");
        player = player.replace(" Stillnofarverite ", " FA ");
        player = player.replace(" Cazoid ", " FA ");
        player = player.replace(" AckJasses ", " FA ");
        player = player.replace(" FeBrees ", " FA ");
        player = player.replace(" DeLong ", " FA ");
        player = player.replace(" Suckerp*nch'd! ", " FA ");
        player = player.replace(" crapflappers ", " FA ");
        return player;
    }

    public JsonObject parseOffensivePlayerData(String player, int week){

        JsonObject playerData = new JsonObject()
                .put("week", week)
                .put("first_initial", player.substring(1, 2));

        player = player.substring(4);
        //System.out.println(player);

        String[] playerStats = player.split(" ");

        playerData.put("last_name", playerStats[0])
                .put("team", playerStats[1]);

        String[] position = playerStats[3].split(",");

        if(position.length == 1) playerData.put("position", position[0]);
        else if (position.length == 2){
            playerData.put("position", position[0])
                    .put("position_2", position[1]);
        }else {
            playerData.put("position", position[0])
                    .put("position_2", position[1])
                    .put("position_3", position[2]);
        }

        String[] statLabels = new String[]{"pa_yds","pa_tds","pa_int","pa_40_yd_comp",
                "pa_40_yd_td","ru_att","ru_yds","ru_tds","ru_40_yd_att","ru_40_yd_td","re_tgt","re_rec","re_yds"
                ,"re_td","re_40_yd_rec","re_40_yd_td","rt_td","2pt","fum"};

        if(playerStats[4].equals("Bye")){
            playerData.put("opponent", playerStats[4]);

            for(int i = 13; i < 32; i++){
                playerData.put(statLabels[i-13], 0);
            }

        }else {
            playerData.put("opponent", playerStats[5]);

            for(int i = 13; i < 32; i++){
                if(playerStats[i].equals("-")) playerData.put(statLabels[i-13], 0);
                else playerData.put(statLabels[i-13], Integer.parseInt(playerStats[i]));
            }
        }

        return playerData;
    }

    public JsonObject parseDefensivePlayerData(String player, int week){

        JsonObject playerData = new JsonObject();

        //System.out.println(player);

        String[] playerStats = player.split(" ");

        playerData.put("id", playerStats[2])
                .put("week", week)
                .put("position", "DEF")
                .put("location",teamLocations.get(playerStats[2]))
                .put("name", teamNames.get(playerStats[2]));


        String[] statLabels = new String[]{"pts_allowed","sack","safety","int","fum_rec","td","blk_kick","ret_td"};

        if(playerStats[5].equals("Bye")){

            playerData.put("opponent", playerStats[5]);

            for(int i = 14; i < 22; i++){
                playerData.put(statLabels[i-14], 0);
            }

        }else{

            playerData.put("opponent", playerStats[6]);

            for(int i = 14; i < 22; i++){
                if(playerStats[i].equals("-")) playerData.put(statLabels[i-14], 0);
                else playerData.put(statLabels[i-14], Integer.parseInt(playerStats[i]));
            }
        }

        return playerData;

    }

    public JsonObject parseKickerPlayerData(String player, int week){

        JsonObject playerData = new JsonObject()
                .put("position", "K")
                .put("week", week)
                .put("first_initial", player.substring(1, 2));

        //System.out.println(player);

        String[] playerStats = player.split(" ");

        playerData.put("last_name", playerStats[2])
                .put("team", playerStats[3]);

        String[] statLabels = new String[]{"0-19", "20-29", "30-39", "40-49", "50", "made"};

        if(playerStats[6].equals("Bye")){

            playerData.put("opponent", "Bye");

            for (int i = 15; i < 21; i++) {
                playerData.put(statLabels[i - 15], 0);
            }

        }else {

            playerData.put("opponent", playerStats[7]);

            for (int i = 15; i < 21; i++) {
                if (playerStats[i].equals("-")) playerData.put(statLabels[i - 15], 0);
                else playerData.put(statLabels[i - 15], Integer.parseInt(playerStats[i]));
            }

        }

        return playerData;
    }

    public void getTeamHashMap(){
        teamLocations = new HashMap<String,String>();
        teamNames = new HashMap<String,String>();

        for(int i = 0; i < teamKeys.length; i++){
            teamLocations.put(teamKeys[i], teamLocation[i]);
            teamNames.put(teamKeys[i], teamName[i]);
        }

    }

}
