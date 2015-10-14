package com.jasonmoix.neuralnet.mlpquant;

/**
 * Created by jmoix on 10/14/2015.
 */
public class Node {

    double weight[];
    double learningRate;
    double moment;
    double previousWeightChange[];
    int index;

    public Node(int numInput, double learningRate, double moment, int index){
        weight = new double[numInput];
        previousWeightChange = new double[numInput];
        this.moment = moment;
        this.index = index;
        this.learningRate = learningRate;
        initWeights();
    }

    public Node(double[] weights, double learningRate, double moment, int index){
        weight = weights;
        previousWeightChange = new double[weights.length];
        this.learningRate = learningRate;
        this.moment = moment;
        this.index = index;
    }

    public double getOutput(double inputValues[]){

        double output = getInputProduct(inputValues);
        return output;

    }

    private double getInputProduct(double inputValues[]){
        double product = 0;
        for(int i = 0; i < inputValues.length; i++){
            product += weight[i]*inputValues[i];
        }
        return product;
    }

    private void initWeights(){

        double denominator = 0;
        for(int i = 0; i < weight.length; i++){
            denominator += (i+1);
        }

        //System.out.println();
        for(double i = 0; i < weight.length; i++){
            weight[(int)i] = (i+1)/denominator;
            //System.out.println(weight[(int)i]);
        }
        //System.out.println();

    }

    public void adjustOutputWeights(double[][] hiddenOutput, double[][] predictedOutput, double[][] expectedOutput){

        double[][] gradient = new double[hiddenOutput.length][weight.length];
        double[] avgGradient = new double[weight.length];

        for(int i = 0; i < hiddenOutput.length; i++){
            for(int j = 0; j < weight.length; j++){
                gradient[i][j] = (predictedOutput[i][0] - expectedOutput[i][0])*hiddenOutput[i][j];
            }
        }

        for(int j = 0; j < weight.length; j++){
            for(int i = 0; i < hiddenOutput.length; i++){
                avgGradient[j] += gradient[i][j];
            }
            avgGradient[j] /= hiddenOutput.length;
            if(previousWeightChange[j] == 0) {
                weight[j] += -1.0 * learningRate * avgGradient[j];
            }else {
                double weightChange = moment*previousWeightChange[j] - (1.0-moment)*learningRate*avgGradient[j];
                weight[j] += weightChange;
                previousWeightChange[j] = weightChange;
            }
            //System.out.println("Avg Gradient = " + avgGradient[j]);
        }


    }

    public void adjustInputWeights(double[][] inputValues, double[][] outputWeights, double[][] predictedOutput, double[][] expectedOutput){

        double[][] gradient = new double[inputValues.length][weight.length];
        double[] avgGradient = new double[weight.length];

        /*System.out.println();
        for(int i = 0; i < outputWeights.length; i++){
            for(int j = 0; j < outputWeights[0].length; j++){
                System.out.println(outputWeights[i][j]);
            }
        }
        System.out.println();*/

        for(int i = 0; i < inputValues.length; i++){
            double p = (predictedOutput[i][0] - expectedOutput[i][0]);
            for(int j = 0; j < weight.length; j++){
                gradient[i][j] = p*outputWeights[0][index]*inputValues[i][j];
            }
        }

        for(int j = 0; j < weight.length; j++){
            for(int i = 0; i < inputValues.length; i++){
                avgGradient[j] += gradient[i][j];
            }
            avgGradient[j] /= inputValues.length;
            if(previousWeightChange[j] == 0) {
                weight[j] += -1.0 * learningRate * avgGradient[j];
            }else {
                double weightChange = moment*previousWeightChange[j] - (1.0-moment)*learningRate*avgGradient[j];
                weight[j] += weightChange;
                previousWeightChange[j] = weightChange;
            }
            //System.out.println("Input Avg Gradient = " + avgGradient[j]);
        }

    }

    public double[] getWeights(){
        return weight;
    }

    public double getLearningRate(){
        return learningRate;
    }

    public double getMoment(){
        return moment;
    }
    
}
