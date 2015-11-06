/**
 * Created by Souboro on 10/18/2015.
 */
public class DefensiveStatsAvg {

    public double avg_pa_allowed;
    public double avg_te_allowed;
    public double avg_wr_allowed;
    public double avg_ru_allowed;
    public double avg_pts_allowed;
    public double avg_sacks;
    public double avg_safety;
    public double avg_fum_rec;
    public double avg_int;
    public double avg_blk;
    public double avg_ret;

    public String toString(){
        return avg_pa_allowed + "," + avg_te_allowed + "," + avg_wr_allowed + "," + avg_ru_allowed + ","
                + avg_pts_allowed + "," + avg_sacks + "," + avg_safety + "," + avg_fum_rec + ","
                + avg_int + "," + avg_blk + "," + avg_ret;
    }

}
