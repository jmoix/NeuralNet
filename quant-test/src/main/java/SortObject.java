/**
 * Created by Souboro on 10/22/2015.
 */
public class SortObject {

    public double fantasyPoints;
    public String data;

    public SortObject(double fantasyPoints, String data){
        this.fantasyPoints = fantasyPoints;
        this.data = data;
    }

    public boolean moreThan(SortObject b){
        if(fantasyPoints > b.fantasyPoints) return true;
        else return false;
    }

}
