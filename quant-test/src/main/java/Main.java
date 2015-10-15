import com.jasonmoix.neuralnet.mlpquant.MLP;
import com.jasonmoix.neuralnet.nfldata.DataParser;
import io.vertx.core.json.JsonArray;

import java.io.File;

/**
 * Created by jmoix on 10/14/2015.
 */
public class Main {
    public static void main(String[] args){

        /*double[][] inputValues = getInputValues();
        double[][] outputValues = getOutputValues(inputValues);
        MLP mlp = new MLP(inputValues,outputValues, 0.001, 0.9, 0.0001, 3, 2);
        mlp.saveWeights(new File("c:\\weights\\weights.txt"));
        MLP mlp = new MLP(new File("c:\\weights\\weights.txt"));
        System.out.println("Prediction, 1 + 2 + 1 = " + mlp.predict(new double[]{1, 2, 1}));*/
        getData();


    }

    public static void getData(){
        DataParser parser = new DataParser();
        //JsonArray defensivePlayerData = parser.fetchDefensiveData();
        //JsonArray kickerPlayerData = parser.fetchKickerData();
        //JsonArray offensivePlayerData = parser.fetchOffensiveData();
        //System.out.println(defensivePlayerData.encodePrettily());
        //System.out.println(kickerPlayerData.encodePrettily());
        //System.out.println(offensivePlayerData.encodePrettily());
    }

    public static double[][] getInputValues(){

        double[][] inputValues = new double[3][3];

        for(int i = 0; i < inputValues.length; i++){
            for(int j = 0; j < inputValues[0].length; j++){
                inputValues[i][j] = i + 1;
            }
        }
        return inputValues;

    }

    public static double[][] getOutputValues(double[][] inputValues){

        double[][] outputValues = new double[inputValues.length][1];

        for(int i = 0; i < inputValues.length; i++){
            for(int j = 0;  j < inputValues[0].length; j++){
                outputValues[i][0] += inputValues[i][j];
            }
        }

        return outputValues;
    }

    public static void printData(double[][] inputData, double[][] outputData){
        for(int i = 0; i < inputData.length; i++){
            for (int j = 0; j < inputData[0].length - 1; j++){
                System.out.print(inputData[i][j] + " + ");
            }
            System.out.println(inputData[i][inputData[0].length - 1] + " = " + outputData[i][0]);
        }
    }
}
