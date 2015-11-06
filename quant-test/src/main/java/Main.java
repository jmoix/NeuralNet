import com.jasonmoix.neuralnet.mlpquant.MLP;
import com.jasonmoix.neuralnet.nfldata.DataParser;
import io.vertx.core.json.JsonArray;
import javax.swing.*;
import io.vertx.core.json.JsonObject;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by jmoix on 10/14/2015.
 */
public class Main {
    public static void main(String[] args){


        final NFLDB db = new NFLDB();
        //db.initTables();

        //getData(db);
        db.initGameNumbers();
        db.updateTeamAverages();
        db.updateOffenseAverages();
        db.updateKickerAverages();
        db.initAverages();
        db.generatePlayerTrainingData();
        db.generateDefensivePlayerTrainingData();
        trainMLPS(db.getOffensePlayerList(), 0.0000001, 0.9, 3, 17);
        trainMLPS(db.getDefensivePlayerList(), 0.000001, 0.9, 3, 12);
        generatePredictionData(db);

        db.closeConnection();

        sortPredictions();

        //LineUpGenerator generator = new LineUpGenerator();
        //generator.generateLineUps();
        coallatePredictions();


    }

    public static void coallatePredictions(){

        File qbInput = new File("C:\\GameData\\PredictionOutput\\qb_sorted.csv");
        File rbInput = new File("C:\\GameData\\PredictionOutput\\rb_sorted.csv");
        File wrInput = new File("C:\\GameData\\PredictionOutput\\wr_sorted.csv");
        File teInput = new File("C:\\GameData\\PredictionOutput\\te_sorted.csv");
        File defInput = new File("C:\\GameData\\PredictionOutput\\def_sorted.csv");
        File output = new File("C:\\GameData\\PredictionOutput\\output.xlsx");

        try{

            Workbook workbook = new XSSFWorkbook();
            String buffer;
            int rowIndex = 0;

            FileReader qbReader = new FileReader(qbInput);
            BufferedReader qb = new BufferedReader(qbReader);

            Sheet qbSheet = workbook.createSheet("QB");
            rowIndex = 0;
            while((buffer = qb.readLine()) != null){
                Row row = qbSheet.createRow(rowIndex++);
                String[] vals = buffer.split(",");
                for(int i = 0; i < vals.length; i++){
                    row.createCell(i).setCellValue(vals[i]);
                }
            }

            FileReader rbReader = new FileReader(rbInput);
            BufferedReader rb = new BufferedReader(rbReader);

            Sheet rbSheet = workbook.createSheet("RB");
            rowIndex = 0;
            while((buffer = rb.readLine()) != null){
                Row row = rbSheet.createRow(rowIndex++);
                String[] vals = buffer.split(",");
                for(int i = 0; i < vals.length; i++){
                    row.createCell(i).setCellValue(vals[i]);
                }
            }

            FileReader wrReader = new FileReader(wrInput);
            BufferedReader wr = new BufferedReader(wrReader);

            Sheet wrSheet = workbook.createSheet("WR");
            rowIndex = 0;
            while((buffer = wr.readLine()) != null){
                Row row = wrSheet.createRow(rowIndex++);
                String[] vals = buffer.split(",");
                for(int i = 0; i < vals.length; i++){
                    row.createCell(i).setCellValue(vals[i]);
                }
            }

            FileReader teReader = new FileReader(teInput);
            BufferedReader te = new BufferedReader(teReader);

            Sheet teSheet = workbook.createSheet("TE");
            rowIndex = 0;
            while((buffer = te.readLine()) != null){
                Row row = teSheet.createRow(rowIndex++);
                String[] vals = buffer.split(",");
                for(int i = 0; i < vals.length; i++){
                    row.createCell(i).setCellValue(vals[i]);
                }
            }

            FileReader defReader = new FileReader(defInput);
            BufferedReader def = new BufferedReader(defReader);

            Sheet defSheet = workbook.createSheet("DST");
            rowIndex = 0;
            while((buffer = def.readLine()) != null){
                Row row = defSheet.createRow(rowIndex++);
                String[] vals = buffer.split(",");
                for(int i = 0; i < vals.length; i++){
                    row.createCell(i).setCellValue(vals[i]);
                }
            }

            FileOutputStream fos = new FileOutputStream(output);
            workbook.write(fos);

            fos.close();
            qb.close();
            te.close();
            wr.close();
            rb.close();
            def.close();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void generatePredictionData(NFLDB nfldb){

        try{

            File input = new File("C:\\GameData\\PredictionInput\\input.csv");
            File qbOutputFile = new File("C:\\GameData\\PredictionOutput\\qb.csv");
            File rbOutputFile = new File("C:\\GameData\\PredictionOutput\\rb.csv");
            File wrOutputFile = new File("C:\\GameData\\PredictionOutput\\wr.csv");
            File teOutputFile = new File("C:\\GameData\\PredictionOutput\\te.csv");
            File defOutputFile = new File("C:\\GameData\\PredictionOutput\\def.csv");

            FileReader reader = new FileReader(input);
            FileWriter qbWriter = new FileWriter(qbOutputFile);
            FileWriter rbWriter = new FileWriter(rbOutputFile);
            FileWriter wrWriter = new FileWriter(wrOutputFile);
            FileWriter teWriter = new FileWriter(teOutputFile);
            FileWriter defWriter = new FileWriter(defOutputFile);

            BufferedReader br = new BufferedReader(reader);
            BufferedWriter qb = new BufferedWriter(qbWriter);
            qb.write("NAME,TEAM,OPPONENT,SALARY,FANTASY RATING");
            qb.newLine();
            BufferedWriter rb = new BufferedWriter(rbWriter);
            rb.write("NAME,TEAM,OPPONENT,SALARY,FANTASY RATING");
            rb.newLine();
            BufferedWriter wr = new BufferedWriter(wrWriter);
            wr.write("NAME,TEAM,OPPONENT,SALARY,FANTASY RATING");
            wr.newLine();
            BufferedWriter te = new BufferedWriter(teWriter);
            te.write("NAME,TEAM,OPPONENT,SALARY,FANTASY RATING");
            te.newLine();
            BufferedWriter def = new BufferedWriter(defWriter);
            def.write("TEAM,OPPONENT,SALARY,FANTASY RATING");
            def.newLine();

            String buffer;

            br.readLine();
            while ((buffer = br.readLine()) != null){
                buffer = buffer.replace("\"", "");
                String[] data = buffer.split(",");
                String position = data[0].toLowerCase();
                String team = data[data.length - 1].toLowerCase();
                String opponent = data[3].toLowerCase();
                opponent = opponent.replace("@","");
                opponent = opponent.replace(team,"");
                opponent = opponent.substring(0, opponent.indexOf(" "));
                String salary = data[2];
                String id;
                String[] name;
                double[] inputValues;
                double prediction;
                File mlpFile;
                MLP mlp;
                switch (position){
                    case "qb":
                        name = data[1].split(" ");
                        id = (name[0].charAt(0) + name[1] + team).toLowerCase();
                        inputValues = nfldb.getOffenseInputValues(id, position, team, opponent);
                        mlpFile = new File("C:\\GameData\\PlayerMLPs\\" + id + ".txt");
                        if(mlpFile.exists()){
                            mlp = new MLP(mlpFile);
                            prediction = mlp.predict(inputValues);
                            qb.write(data[1] + "," + team + "," + opponent + "," + salary + "," + prediction);
                            qb.newLine();
                        }
                        break;
                    case "wr":
                        name = data[1].split(" ");
                        id = (name[0].charAt(0) + name[1] + team).toLowerCase();
                        inputValues = nfldb.getOffenseInputValues(id, position, team, opponent);
                        mlpFile = new File("C:\\GameData\\PlayerMLPs\\" + id + ".txt");
                        if(mlpFile.exists()){
                            mlp = new MLP(mlpFile);
                            prediction = mlp.predict(inputValues);
                            wr.write(data[1] + "," + team + "," + opponent + "," + salary + "," + prediction);
                            wr.newLine();
                        }
                        break;
                    case "rb":
                        name = data[1].split(" ");
                        id = (name[0].charAt(0) + name[1] + team).toLowerCase();
                        inputValues = nfldb.getOffenseInputValues(id, position, team, opponent);
                        mlpFile = new File("C:\\GameData\\PlayerMLPs\\" + id + ".txt");
                        if(mlpFile.exists()){
                            mlp = new MLP(mlpFile);
                            prediction = mlp.predict(inputValues);
                            rb.write(data[1] + "," + team + "," + opponent + "," + salary + "," + prediction);
                            rb.newLine();
                        }
                        break;
                    case "te":
                        name = data[1].split(" ");
                        id = (name[0].charAt(0) + name[1] + team).toLowerCase();
                        inputValues = nfldb.getOffenseInputValues(id, position, team, opponent);
                        mlpFile = new File("C:\\GameData\\PlayerMLPs\\" + id + ".txt");
                        if(mlpFile.exists()){
                            mlp = new MLP(mlpFile);
                            prediction = mlp.predict(inputValues);
                            te.write(data[1] + "," + team + "," + opponent + "," + salary + "," + prediction);
                            te.newLine();
                        }
                        break;
                    case "dst":
                        inputValues = nfldb.getDefensiveInputValues(team, opponent);
                        mlpFile = new File("C:\\GameData\\PlayerMLPs\\" + team + ".txt");
                        if(mlpFile.exists()){
                            mlp = new MLP(mlpFile);
                            prediction = mlp.predict(inputValues);
                            if(prediction < 0) prediction = 0;
                            def.write(team + "," + opponent + "," + salary + "," + prediction);
                            def.newLine();
                        }
                        break;
                }
            }

            br.close();
            qb.close();
            rb.close();
            wr.close();
            te.close();
            def.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void trainMLPS(ArrayList<String> playerIds, double learningRate, double moment, double accuracy, int hiddenLayerSize){

        try{

            for(int k = 0; k < playerIds.size(); k++){

                System.out.println("Training MLP for " + playerIds.get(k));

                File trainingInput = new File("C:\\GameData\\PlayerTrainingData\\" + playerIds.get(k) + ".csv");
                File mlpOutput = new File("C:\\GameData\\PlayerMLPs\\" + playerIds.get(k) + ".txt");
                FileReader reader = new FileReader(trainingInput);
                BufferedReader br = new BufferedReader(reader);

                ArrayList<String> data = new ArrayList<>();

                String line;

                while((line = br.readLine()) != null){
                    data.add(line);
                }

                if(data.size() != 0) {

                    String[] dataLine = data.get(0).split(",");

                    double[][] inputValues = new double[data.size()][dataLine.length - 1];
                    double[][] expectedValues = new double[data.size()][1];

                    for (int i = 0; i < data.size(); i++) {

                        dataLine = data.get(i).split(",");
                        for (int j = 0; j < inputValues[0].length; j++) {
                            inputValues[i][j] = Double.parseDouble(dataLine[j]);
                        }
                        expectedValues[i][0] = Double.parseDouble(dataLine[dataLine.length - 1]);

                    }

                    MLP mlp = new MLP(inputValues, expectedValues, learningRate, moment, accuracy, inputValues[0].length, hiddenLayerSize, mlpOutput, playerIds.get(k));

                }

                br.close();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sortPredictions(){
        File qbInput = new File("C:\\GameData\\PredictionOutput\\qb.csv");
        File rbInput = new File("C:\\GameData\\PredictionOutput\\rb.csv");
        File wrInput = new File("C:\\GameData\\PredictionOutput\\wr.csv");
        File teInput = new File("C:\\GameData\\PredictionOutput\\te.csv");
        File defInput = new File("C:\\GameData\\PredictionOutput\\def.csv");

        File qbOutput = new File("C:\\GameData\\PredictionOutput\\qb_sorted.csv");
        File rbOutput = new File("C:\\GameData\\PredictionOutput\\rb_sorted.csv");
        File wrOutput = new File("C:\\GameData\\PredictionOutput\\wr_sorted.csv");
        File teOutput = new File("C:\\GameData\\PredictionOutput\\te_sorted.csv");
        File defOutput = new File("C:\\GameData\\PredictionOutput\\def_sorted.csv");

        sort(qbInput,qbOutput);
        sort(rbInput,rbOutput);
        sort(wrInput,wrOutput);
        sort(teInput,teOutput);
        sort(defInput,defOutput);
    }

    public static void sort(File in, File out){

        ArrayList<SortObject> objects = new ArrayList<>();

        try{
            FileReader reader = new FileReader(in);
            BufferedReader br = new BufferedReader(reader);

            FileWriter writer = new FileWriter(out);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(br.readLine());
            bw.newLine();
            String buffer;
            while((buffer = br.readLine()) != null){
                String[] vals = buffer.split(",");
                objects.add(new SortObject(Double.parseDouble(vals[vals.length - 1]), buffer));
            }

            for(int i = 1; i < objects.size(); i++){

                int j = i;
                while(objects.get(j).moreThan(objects.get(j-1))){
                    SortObject bufferObject = objects.get(j-1);
                    objects.set(j-1, objects.get(j));
                    objects.set(j, bufferObject);
                    j--;
                    if(j==0) break;
                }

            }

            for(int i = 0; i < objects.size(); i++){
                bw.write(objects.get(i).data);
                bw.newLine();
            }

            br.close();
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getData(NFLDB nfldb){
        DataParser parser = new DataParser();

        System.out.println("Parsing Defense HTML...");
        JsonArray defensivePlayerData = parser.fetchDefensiveData();
        nfldb.loadTeamData(defensivePlayerData);

        System.out.println("Parsing Kicker HTML...");
        JsonArray kickerPlayerData = parser.fetchKickerData();
        nfldb.loadKickerData(kickerPlayerData);

        System.out.println("Parsing Offense HTML...");
        JsonArray offensivePlayerData = parser.fetchOffensiveData();
        nfldb.loadOffenseData(offensivePlayerData);

    }

}
