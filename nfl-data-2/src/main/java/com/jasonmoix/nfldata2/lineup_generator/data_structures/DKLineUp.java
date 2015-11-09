package com.jasonmoix.nfldata2.lineup_generator.data_structures;

import java.util.ArrayList;

/**
 * Created by Souboro on 11/8/2015.
 */
public class DKLineUp {

    public Player qb;
    public Player rb1;
    public Player rb2;
    public Player wr1;
    public Player wr2;
    public Player wr3;
    public Player te;
    public Player flex;
    public Player dst;

    public double salary(){
        return qb.salary + rb1.salary + rb2.salary + wr1.salary + wr2.salary + wr3.salary + te.salary + flex.salary + dst.salary;
    }

    public double fantasyPoints(){
        return qb.fantasyPoints + rb1.fantasyPoints + rb2.fantasyPoints + wr1.fantasyPoints + wr2.fantasyPoints + wr3.fantasyPoints + te.fantasyPoints + flex.fantasyPoints + dst.fantasyPoints;
    }

    public boolean greaterThan(DKLineUp b){
        return fantasyPoints() > b.fantasyPoints();
    }

    public boolean equals(DKLineUp b){

        boolean equals = true;

        ArrayList<Player> players = new ArrayList<>();
        players.add(b.qb);
        players.add(b.rb1);
        players.add(b.rb2);
        players.add(b.wr1);
        players.add(b.wr2);
        players.add(b.wr3);
        players.add(b.te);
        players.add(b.flex);
        players.add(b.dst);

        if(!players.contains(qb) ||
                !players.contains(rb1) ||
                !players.contains(rb2) ||
                !players.contains(wr1) ||
                !players.contains(wr2) ||
                !players.contains(wr3) ||
                !players.contains(te) ||
                !players.contains(flex) ||
                !players.contains(dst)) equals = false;

        return equals;
    }
}
