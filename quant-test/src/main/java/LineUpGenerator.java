import java.io.*;
import java.util.ArrayList;

/**
 * Created by Souboro on 10/24/2015.
 */
public class LineUpGenerator {

    public ArrayList<String> blacklist;

    ArrayList<OffensivePlayer> QB;
    ArrayList<OffensivePlayer> RB;
    ArrayList<OffensivePlayer> WR;
    ArrayList<OffensivePlayer> TE;
    ArrayList<OffensivePlayer> FLEX;
    ArrayList<DefensivePlayer> DST;
    ArrayList<LineUp> lineups;

    public LineUpGenerator(){

        initBlackList();

        QB = new ArrayList<>();
        RB = new ArrayList<>();
        WR = new ArrayList<>();
        TE = new ArrayList<>();
        FLEX = new ArrayList<>();
        DST = new ArrayList<>();

        File qbInput = new File("C:\\GameData\\PredictionOutput\\qb_sorted.csv");
        File rbInput = new File("C:\\GameData\\PredictionOutput\\rb_sorted.csv");
        File wrInput = new File("C:\\GameData\\PredictionOutput\\wr_sorted.csv");
        File teInput = new File("C:\\GameData\\PredictionOutput\\te_sorted.csv");
        File defInput = new File("C:\\GameData\\PredictionOutput\\def_sorted.csv");

        try {

            String buffer;

            FileReader qbReader = new FileReader(qbInput);
            BufferedReader qb = new BufferedReader(qbReader);
            qb.readLine();
            while ((buffer = qb.readLine()) != null){
                String[] vals = buffer.split(",");
                if(!blacklist.contains(vals[0]))
                    QB.add(new OffensivePlayer(vals[0], "QB", vals[1], vals[2], vals[3], vals[4]));
            }

            FileReader rbReader = new FileReader(rbInput);
            BufferedReader rb = new BufferedReader(rbReader);
            rb.readLine();
            while ((buffer = rb.readLine()) != null){
                String[] vals = buffer.split(",");
                if(!blacklist.contains(vals[0]))
                    RB.add(new OffensivePlayer(vals[0], "RB", vals[1], vals[2], vals[3], vals[4]));
            }

            FileReader wrReader = new FileReader(wrInput);
            BufferedReader wr = new BufferedReader(wrReader);
            wr.readLine();
            while ((buffer = wr.readLine()) != null){
                String[] vals = buffer.split(",");
                if(!blacklist.contains(vals[0]))
                    WR.add(new OffensivePlayer(vals[0], "WR", vals[1], vals[2], vals[3], vals[4]));
            }

            FileReader teReader = new FileReader(teInput);
            BufferedReader te = new BufferedReader(teReader);
            te.readLine();
            while ((buffer = te.readLine()) != null){
                String[] vals = buffer.split(",");
                if(!blacklist.contains(vals[0]))
                    TE.add(new OffensivePlayer(vals[0], "TE", vals[1], vals[2], vals[3], vals[4]));
            }

            FileReader defReader = new FileReader(defInput);
            BufferedReader def = new BufferedReader(defReader);
            def.readLine();
            while((buffer = def.readLine()) != null){
                String[] vals = buffer.split(",");
                DST.add(new DefensivePlayer(vals[0],vals[2],vals[2],vals[3]));
            }

            for(int i = 0; i < WR.size(); i++){
                FLEX.add(WR.get(i));
            }

            for(int i = 0; i < RB.size(); i++){
                FLEX.add(RB.get(i));
            }

            for(int i = 0; i < TE.size(); i++){
                FLEX.add(TE.get(i));
            }

            qb.close();
            te.close();
            wr.close();
            rb.close();
            def.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initBlackList(){
        blacklist = new ArrayList<>();
        blacklist.add("Luke McCown");
    }

    public void generateLineUps(){

        System.out.println("Generating Lineups...");

        lineups = new ArrayList<>();

        for(int i = 0; i < QB.size(); i++){
            LineUp lineUp = new LineUp();
            lineUp.QB = QB.get(i);
            for(int j = 0; j < RB.size(); j++){
                lineUp.RB1 = RB.get(j);
                ArrayList<OffensivePlayer> rb2 = RB;
                rb2.remove(RB.get(j));
                for(int l = 0; l < rb2.size(); l++){
                    lineUp.RB2 = rb2.get(l);
                    for(int m = 0; m < WR.size(); m++){
                        lineUp.WR1 = WR.get(m);
                        ArrayList<OffensivePlayer> wr2 = WR;
                        wr2.remove(WR.get(m));
                        for(int n = 0; n < wr2.size(); n++){
                            lineUp.WR2 = wr2.get(n);
                            ArrayList<OffensivePlayer> wr3 = wr2;
                            wr3.remove(wr2.get(n));
                            for(int o = 0; o < wr3.size(); o++){
                                lineUp.WR3 = wr3.get(o);
                                for(int p = 0; p < TE.size(); p++){
                                    lineUp.TE = TE.get(p);
                                    ArrayList<OffensivePlayer> flex = FLEX;
                                    flex.remove(lineUp.WR1);
                                    flex.remove(lineUp.WR2);
                                    flex.remove(lineUp.WR3);
                                    flex.remove(lineUp.RB1);
                                    flex.remove(lineUp.RB2);
                                    flex.remove(lineUp.TE);
                                    for(int q = 0; q < flex.size(); q++){
                                        lineUp.FLEX = flex.get(i);
                                        for(int r = 0; r < DST.size(); r++){
                                            lineUp.DST = DST.get(i);
                                            if (lineUp.salary() <= 50000) lineups.add(lineUp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println(lineups.size() + " Lineups Generated!");

        sortLineUps();
    }

    public void sortLineUps(){

        System.out.println("Sorting Lineups...");

        for(int i = 1; i < lineups.size(); i++){

            int j = i;
            while(lineups.get(j).moreThan(lineups.get(j-1))){
                LineUp bufferObject = lineups.get(j-1);
                lineups.set(j-1, lineups.get(j));
                lineups.set(j, bufferObject);
                j--;
                if(j==0) break;
            }

        }

        System.out.println("Printing Lineups to csv...");

        for(int i = 0; i < 10; i++){
            if(i == lineups.size()) break;
            LineUp lineUp = lineups.get(i);

            try{

                FileWriter writer = new FileWriter(new File("C:\\GameData\\PredictionOutput\\Lineup" + (i+1) + ".csv"));
                BufferedWriter bw = new BufferedWriter(writer);

                lineUp.print(bw);

                bw.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

}
