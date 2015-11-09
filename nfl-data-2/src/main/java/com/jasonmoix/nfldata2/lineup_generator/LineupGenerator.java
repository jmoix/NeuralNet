package com.jasonmoix.nfldata2.lineup_generator;

import com.jasonmoix.nfldata2.lineup_generator.data_structures.DKLineUp;
import com.jasonmoix.nfldata2.lineup_generator.data_structures.Player;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Souboro on 11/8/2015.
 */
public class LineupGenerator {

    ArrayList<Player> QBS;
    ArrayList<Player> RBS;
    ArrayList<Player> WRS;
    ArrayList<Player> TES;
    ArrayList<Player> FLEX;
    ArrayList<Player> DSTS;
    ArrayList<DKLineUp> lineUps;
    int lineupIndex;

    public LineupGenerator(){

        QBS = new ArrayList<>();
        RBS = new ArrayList<>();
        WRS = new ArrayList<>();
        TES = new ArrayList<>();
        FLEX = new ArrayList<>();
        DSTS = new ArrayList<>();
        lineUps = new ArrayList<>();
        lineupIndex = 0;
        initArrayLists();
        generateLineUps();
        outputLineUps();
    }

    public void initArrayLists(){

        File data = new File("C:\\data\\input\\rankings.xlsx");

        try{

            FileInputStream inputStream = new FileInputStream(data);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                Player player = new Player(row);

                switch (player.position){
                    case "QB":
                        QBS.add(player);
                        break;
                    case "WR":
                        WRS.add(player);
                        FLEX.add(player);
                        break;
                    case "TE":
                        TES.add(player);
                        FLEX.add(player);
                        break;
                    case "RB":
                        RBS.add(player);
                        FLEX.add(player);
                        break;
                    case "DST":
                        DSTS.add(player);
                        break;
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void generateLineUps(){

        DKLineUp lineUp;

        for(int i = 0; i < QBS.size(); i++){
            for(int j = 0; j < RBS.size(); j++){
                ArrayList<Player> RB2 = nextPlayerList(RBS, RBS.get(j));
                for(int k = 0; k < RB2.size(); k++){
                    for(int l = 0; l < WRS.size(); l++){
                        ArrayList<Player> WR2 = nextPlayerList(WRS, WRS.get(l));
                        for(int m = 0; m < WR2.size(); m++){
                            ArrayList<Player> WR3 = nextPlayerList(WR2, WR2.get(m));
                            for(int n = 0; n < WR3.size(); n++){
                                for(int o = 0; o < TES.size(); o++){
                                    ArrayList<Player> flex = getFlexList(
                                            RBS.get(j),
                                            RB2.get(k),
                                            WRS.get(l),
                                            WR2.get(m),
                                            WR3.get(n),
                                            TES.get(o)
                                    );
                                    for(int p = 0; p < flex.size(); p++){
                                        for(int q = 0; q < DSTS.size(); q++){
                                            lineUp = new DKLineUp();
                                            lineUp.qb = QBS.get(i);
                                            lineUp.rb1 = RBS.get(j);
                                            lineUp.rb2 = RB2.get(k);
                                            lineUp.wr1 = WRS.get(l);
                                            lineUp.wr2 = WR2.get(m);
                                            lineUp.wr3 = WR3.get(n);
                                            lineUp.te = TES.get(o);
                                            lineUp.flex = flex.get(p);
                                            lineUp.dst = DSTS.get(q);
                                            if(lineUp.salary() <= 50000 && !lineupExists(lineUp)) push(lineUp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void push(DKLineUp lineUp){

        if(lineupIndex < 5){

            lineUps.add(lineUp);
            lineupIndex++;
            sort();

        }else{

            if(lineUp.greaterThan(lineUps.get(lineupIndex - 1))) lineUps.set(lineupIndex - 1, lineUp);
            sort();

        }
    }

    public void outputLineUps(){

        File output = new File("C:\\data\\output\\lineups.xlsx");

        Workbook workbook = new XSSFWorkbook();

        for(int i = 0; i < lineUps.size(); i++){
            DKLineUp lineUp = lineUps.get(i);
            Sheet sheet = workbook.createSheet("Lineup " + (i+1));
            int rowIndex = 0;
            int cellIndex = 0;

            Row row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue("POSITION");
            row.createCell(cellIndex++).setCellValue("NAME");
            row.createCell(cellIndex++).setCellValue("SALARY");
            row.createCell(cellIndex).setCellValue("FANTASY RANK");

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.qb.position);
            row.createCell(cellIndex++).setCellValue(lineUp.qb.name);
            row.createCell(cellIndex++).setCellValue(lineUp.qb.salary);
            row.createCell(cellIndex).setCellValue(lineUp.qb.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.rb1.position);
            row.createCell(cellIndex++).setCellValue(lineUp.rb1.name);
            row.createCell(cellIndex++).setCellValue(lineUp.rb1.salary);
            row.createCell(cellIndex).setCellValue(lineUp.rb1.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.rb2.position);
            row.createCell(cellIndex++).setCellValue(lineUp.rb2.name);
            row.createCell(cellIndex++).setCellValue(lineUp.rb2.salary);
            row.createCell(cellIndex).setCellValue(lineUp.rb2.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.wr1.position);
            row.createCell(cellIndex++).setCellValue(lineUp.wr1.name);
            row.createCell(cellIndex++).setCellValue(lineUp.wr1.salary);
            row.createCell(cellIndex).setCellValue(lineUp.wr1.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.wr2.position);
            row.createCell(cellIndex++).setCellValue(lineUp.wr2.name);
            row.createCell(cellIndex++).setCellValue(lineUp.wr2.salary);
            row.createCell(cellIndex).setCellValue(lineUp.wr2.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.wr3.position);
            row.createCell(cellIndex++).setCellValue(lineUp.wr3.name);
            row.createCell(cellIndex++).setCellValue(lineUp.wr3.salary);
            row.createCell(cellIndex).setCellValue(lineUp.wr3.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.te.position);
            row.createCell(cellIndex++).setCellValue(lineUp.te.name);
            row.createCell(cellIndex++).setCellValue(lineUp.te.salary);
            row.createCell(cellIndex).setCellValue(lineUp.te.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.flex.position);
            row.createCell(cellIndex++).setCellValue(lineUp.flex.name);
            row.createCell(cellIndex++).setCellValue(lineUp.flex.salary);
            row.createCell(cellIndex).setCellValue(lineUp.flex.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(lineUp.dst.position);
            row.createCell(cellIndex++).setCellValue(lineUp.dst.name);
            row.createCell(cellIndex++).setCellValue(lineUp.dst.salary);
            row.createCell(cellIndex).setCellValue(lineUp.dst.fantasyPoints);

            cellIndex = 0;
            row = sheet.createRow(rowIndex);
            row.createCell(cellIndex++).setCellValue("Total Salary");
            row.createCell(cellIndex++).setCellValue(lineUp.salary());
            row.createCell(cellIndex++).setCellValue("Lineup Rank");
            row.createCell(cellIndex).setCellValue(lineUp.fantasyPoints());

        }

        try{
            FileOutputStream out = new FileOutputStream(output);
            workbook.write(out);
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void sort(){

        for(int i = lineupIndex - 1; i > 0; i--){

            DKLineUp lineUp = lineUps.get(i);
            int j = i - 1;

            while (j > 0 && lineUp.greaterThan(lineUps.get(j))){
                DKLineUp buffer = lineUps.get(j);
                lineUps.set(j, lineUp);
                lineUps.set(j + 1, buffer);
            }

        }

    }

    public boolean lineupExists(DKLineUp lineUp){

        boolean exists = false;

        for(int i = 0; i < lineupIndex; i++){
            if(lineUps.get(i).equals(lineUp)) exists = true;
        }

        return exists;
    }

    public ArrayList<Player> nextPlayerList(ArrayList<Player> players, Player remove){

        ArrayList<Player> newPlayers = new ArrayList<>();

        for(int i = 0; i < players.size(); i++){
            newPlayers.add(players.get(i));
        }

        newPlayers.remove(remove);

        return newPlayers;

    }

    public ArrayList<Player> getFlexList(Player rb1, Player rb2, Player wr1, Player wr2, Player wr3, Player te){

        ArrayList<Player> players = new ArrayList<>();

        for(int i = 0; i < FLEX.size(); i++){
            players.add(FLEX.get(i));
        }

        players.remove(rb1);
        players.remove(rb2);
        players.remove(wr1);
        players.remove(wr2);
        players.remove(wr3);
        players.remove(te);

        return players;
    }
}
