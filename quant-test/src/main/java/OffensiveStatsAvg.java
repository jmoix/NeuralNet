/**
 * Created by Souboro on 10/18/2015.
 */
public class OffensiveStatsAvg {

    public double avg_pa;
    public double avg_te;
    public double avg_wr;
    public double avg_ru;
    public double avg_pts;
    public double avg_sacks_allowed;
    public double avg_safety_allowed;
    public double avg_fum_allowed;
    public double avg_int_allowed;
    public double avg_blk_allowed;
    public double avg_ret_allowed;

    public String toString(){
        return avg_pa + "," + avg_te + "," + avg_wr + "," + avg_ru + "," + avg_pts + ","
                + avg_sacks_allowed + "," + avg_safety_allowed + "," + avg_fum_allowed + ","
                + avg_int_allowed + "," + avg_blk_allowed + "," + avg_ret_allowed;
    }

}
