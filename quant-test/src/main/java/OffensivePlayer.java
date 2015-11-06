/**
 * Created by Souboro on 10/24/2015.
 */
public class OffensivePlayer {

    public String name;
    public String position;
    public String team;
    public String opponent;
    public int salary;
    public double fantasy_points;

    public OffensivePlayer(String name, String position, String team, String opponent, String salary, String fantasy_points){
        this.name = name;
        this.position = position;
        this.team = team;
        this.opponent = opponent;
        this.salary = Integer.parseInt(salary);
        this.fantasy_points = Double.parseDouble(fantasy_points);
    }

    public String toString(){
        return name + "," + position + "," + team + "," + opponent + "," + salary + "," + fantasy_points;
    }

}
