import java.io.BufferedWriter;

/**
 * Created by Souboro on 10/24/2015.
 */
public class LineUp {

    public OffensivePlayer QB;
    public OffensivePlayer RB1;
    public OffensivePlayer RB2;
    public OffensivePlayer WR1;
    public OffensivePlayer WR2;
    public OffensivePlayer WR3;
    public OffensivePlayer TE;
    public OffensivePlayer FLEX;
    public DefensivePlayer DST;

    public LineUp(){}

    public int salary(){
        return QB.salary + RB1.salary + RB2.salary + WR1.salary + WR2.salary + WR3.salary + TE.salary + FLEX.salary + DST.salary;
    }

    public double fantasy_points(){
        return QB.fantasy_points + RB1.fantasy_points + RB2.fantasy_points + WR1.fantasy_points + WR2.fantasy_points +
                WR3.fantasy_points + TE.fantasy_points + FLEX.fantasy_points + DST.fantasy_points;
    }

    public void print(BufferedWriter bw){

        try {

            bw.write("NAME,POSITION,TEAM,OPPONENT,SALARY,FANTASY RATING");
            bw.newLine();
            bw.write(QB.toString());
            bw.newLine();
            bw.write(RB1.toString());
            bw.newLine();
            bw.write(RB2.toString());
            bw.newLine();
            bw.write(WR1.toString());
            bw.newLine();
            bw.write(WR2.toString());
            bw.newLine();
            bw.write(WR3.toString());
            bw.newLine();
            bw.write(TE.toString());
            bw.newLine();
            bw.write(FLEX.toString());
            bw.newLine();
            bw.write(DST.toString());
            bw.newLine();
            bw.write("TOTAL,,,," + salary() + "," + fantasy_points());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean moreThan(LineUp b){
        if(salary() > b.salary()) return true;
        else return false;
    }
}
