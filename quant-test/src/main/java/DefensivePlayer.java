/**
 * Created by Souboro on 10/24/2015.
 */
public class DefensivePlayer {

    public String position;
    public String team;
    public String opponent;
    public int salary;
    public double fantasy_points;

    public DefensivePlayer(String team, String opponent, String salary, String fantasy_points){
        this.position = "DST";
        this.team = team;
        this.opponent = opponent;
        this.salary = Integer.parseInt(salary);
        this.fantasy_points = Double.parseDouble(fantasy_points);
    }

    public String toString(){
        return team + "," + position + "," + team + "," + opponent + "," + salary + "," + fantasy_points;
    }

}
