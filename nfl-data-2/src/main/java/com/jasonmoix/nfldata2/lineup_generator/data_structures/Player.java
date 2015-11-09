package com.jasonmoix.nfldata2.lineup_generator.data_structures;

import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;

/**
 * Created by jmoix on 10/30/2015.
 */
public class Player {
    public String name;
    public String position;
    public double salary;
    public double fantasyPoints;

    public Player(Row row){
        name = row.getCell(1).getStringCellValue();
        position = row.getCell(0).getStringCellValue();
        salary = row.getCell(2).getNumericCellValue();
        fantasyPoints = row.getCell(3).getNumericCellValue();
    }

}
