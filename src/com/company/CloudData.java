package com.company;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CloudData {

    Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
    float [][][] convection; // vertical air movement strength, that evolves over time
    int [][][] classification; // cloud type per grid point, evolving over time
    int dimx, dimy, dimt; // data dimensions
    float xAverage, yAverage; // Prevailing wind average

    // overall number of elements in the timeline grids
    int dim(){
        return dimt*dimx*dimy;
    }

    // convert linear position into 3D location in simulation grid
    void locate(int pos, int [] ind)
    {
        ind[0] = (int) pos / (dimx*dimy); // t
        ind[1] = (pos % (dimx*dimy)) / dimy; // x
        ind[2] = pos % (dimy); // y
    }

    // read cloud simulation data from file
    void readData(String fileName){
        try{
            Scanner sc = new Scanner(new File(fileName), "UTF-8");

            // input grid dimensions and simulation duration in timesteps
            dimt = sc.nextInt();
            //System.err.printf("t = %d\n", dimt); // Lets see what we're actually getting from this file
            dimx = sc.nextInt();
            //System.err.printf("x = %d\n", dimx); // Lets see what we're actually getting from this file
            dimy = sc.nextInt();
            //System.err.printf("y = %d\n", dimy); // Lets see what we're actually getting from this file

            // initialize and load advection (wind direction and strength) and convection
            advection = new Vector[dimt][dimx][dimy];
            convection = new float[dimt][dimx][dimy];
            for(int t = 0; t < dimt; t++) {
                for (int x = 0; x < dimx; x++) {
                    for (int y = 0; y < dimy; y++) {
                        advection[t][x][y] = new Vector();
                        advection[t][x][y].setX(Float.parseFloat(sc.next().trim()));
                        advection[t][x][y].setY(Float.parseFloat(sc.next().trim()));
                        convection[t][x][y] = Float.parseFloat(sc.next().trim());

                        // Lets see what we're actually getting from this file
                        //System.err.printf("%f %f %f ", advection[t][x][y].getX(), advection[t][x][y].getY(), convection[t][x][y]);
                    }
                }
                System.err.printf("\n");
            }

            System.out.println();

            // load the classification codes for each layer element
            classification = new int[dimt][dimx][dimy];
            classify();

            sc.close();
        }
        catch (IOException e){
            System.out.println("Unable to open input file "+fileName);
            e.printStackTrace();
        }
        catch (java.util.InputMismatchException e){
            System.out.println("Malformed input file "+fileName);
            e.printStackTrace();
        }
    }

    // write classification output to file
    void writeData(String fileName){
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
            //printWriter.printf("%f %f\n", wind.x, wind.y);

            // get and write the prevailing wind direction averages
            prevailingWindAve();
            printWriter.printf("%f %f\n", xAverage, yAverage);

            // write classifications
            for(int t = 0; t < dimt; t++){
                for(int x = 0; x < dimx; x++){
                    for(int y = 0; y < dimy; y++){
                        printWriter.printf("%d ", classification[t][x][y]);
                    }
                }
                printWriter.printf("\n");
            }

            printWriter.close();
        }
        catch (IOException e){
            System.out.println("Unable to open output file "+fileName);
            e.printStackTrace();
        }
    }

    // calculate the prevailing wind average for x direction and y direction
    void prevailingWindAve() {
        float sumX = 0;
        float sumY = 0;
        for(int s = 0; s < dimt; s++){
            for(int a = 0; a < dimx; a++){
                for(int b = 0; b < dimy; b++){
                    //System.err.printf("Adding x wind direction: %f\t", advection[s][a][b].getX());
                    sumX += advection[s][a][b].getX();
                    //System.err.printf("Adding y wind direction: %f\n", advection[s][a][b].getY());
                    sumY += advection[s][a][b].getY();
                }
            }

            //System.err.printf("Timestep %d\n", s);
        }

        //System.err.printf("Total sums x = %f and y = %f\n", sumX, sumY);
        xAverage = (float) sumX/dim();
        yAverage = (float)sumY/dim();
    }

    // calculate local average of 3x3 window for each i,j of this matrix and classification in code 0 or code 1 or code 2
    void classify () {
        for(int t = 0; t < dimt; t++) {
            for (int i = 0; i < dimx; i++) {
                for (int j = 0; j < dimy; j++) {

                    // find local average for each element in the air layer and classify
                    float localSumX = 0f;
                    float localSumY = 0f;
                    int numberOfNeighbours = 8;

                    if (i==0 && j==0) {
                        localSumX = advection[t][i][j].getX() + advection[t][i+1][j].getX() + advection[t][i][j+1].getX() +
                                advection[t][i+1][j+1].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i+1][j].getY() + advection[t][i][j+1].getY() +
                                advection[t][i+1][j+1].getY();
                    }
                    else if (i==(dimx-1) && j==0) {
                        localSumX = advection[t][i][j].getX() + advection[t][i-1][j].getX() + advection[t][i-1][j+1].getX() +
                                advection[t][i][j+1].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i-1][j].getY() + advection[t][i-1][j+1].getY() +
                                advection[t][i][j+1].getY();

                    }
                    else if (i==(dimx-1) && j==(dimy-1)) {
                        localSumX = advection[t][i][j].getX() + advection[t][i-1][j-1].getX() + advection[t][i][j-1].getX() +
                                advection[t][i-1][j].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i-1][j-1].getY() + advection[t][i][j-1].getY() +
                                advection[t][i-1][j].getY();
                    }
                    else if (i==0 && j==(dimy-1)) {
                        localSumX = advection[t][i][j].getX() + advection[t][i][j-1].getX() + advection[t][i+1][j-1].getX() +
                                advection[t][i+1][j].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i][j-1].getY() + advection[t][i+1][j-1].getY() +
                                advection[t][i+1][j].getY();
                    }
                    else if (i==0 && ((j > 0) || (j < (dimy -1)))) {
                        localSumX = advection[t][i][j].getX() + advection[t][i][j-1].getX() + advection[t][i+1][j-1].getX() +
                                advection[t][i+1][j].getX() + advection[t][i+1][j+1].getX() + advection[t][i][j+1].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i][j-1].getY() + advection[t][i+1][j-1].getY() +
                                advection[t][i+1][j].getY() + advection[t][i+1][j+1].getY() + advection[t][i][j+1].getY();
                    }
                    else if (((i > 0) || (i < (dimx -1))) && j==0) {
                        localSumX = advection[t][i][j].getX() + advection[t][i-1][j].getX() + advection[t][i+1][j].getX() +
                                advection[t][i-1][j+1].getX() + advection[t][i][j+1].getX() + advection[t][i+1][j+1].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i-1][j].getY() + advection[t][i+1][j].getY() +
                                advection[t][i-1][j+1].getY() + advection[t][i][j+1].getY() + advection[t][i+1][j+1].getY();
                    }
                    else if (i==(dimx-1) && ((j > 0) || (j < (dimy -1)))) {
                        localSumX = advection[t][i][j].getX() + advection[t][i][j-1].getX() + advection[t][i-1][j-1].getX() +
                                advection[t][i-1][j].getX() + advection[t][i-1][j+1].getX() + advection[t][i][j+1].getX();
                        localSumY = advection[t][i][j].getY() + advection[t][i][j-1].getY() + advection[t][i-1][j-1].getY() +
                                advection[t][i-1][j].getY() + advection[t][i-1][j+1].getY() + advection[t][i][j+1].getY();
                    }
                    else {
                        localSumX = advection[t][i-1][j-1].getX() + advection[t][i][j-1].getX() + advection[t][i+1][j-1].getX() +
                                advection[t][i-1][j].getX() + advection[t][i][j].getX() + advection[t][i+1][j].getX() +
                                advection[t][i-1][j+1].getX() + advection[t][i][j+1].getX() + advection[t][i+1][j+1].getX();
                        localSumY = advection[t][i-1][j-1].getY() + advection[t][i][j-1].getY() + advection[t][i+1][j-1].getY() +
                                advection[t][i-1][j].getY() + advection[t][i][j].getY() + advection[t][i+1][j].getY() +
                                advection[t][i-1][j+1].getY() + advection[t][i][j+1].getY() + advection[t][i+1][j+1].getY();
                    }

                    float averageX = localSumX/numberOfNeighbours;
                    float averageY = localSumY/numberOfNeighbours;

                    float windMagnitude = (float)Math.sqrt(averageX*averageX + averageY*averageY);

                    // classify
                    if (Math.abs(convection[t][i][j]) > windMagnitude) {
                        //System.err.printf("CODE 0 - Absolute lift |u| = %f : Wind Magnitude W = %f\n", Math.abs(convection[t][i][j]), windMagnitude);
                        // Code 0 cumulus
                        classification[t][i][j] = 0;
                    } else if (windMagnitude > 0.2) {
                        //System.err.printf("CODE 1 - Wind Magnitude W = %f\n", windMagnitude);
                        // Code 1 striated stratus
                        classification[t][i][j] = 1;

                    } else {
                        //System.err.printf("CODE 2\n");
                        // Code 2 armophous stratus
                        classification[t][i][j] = 2;

                    }
                }
            }
        }
    } // End of subroutine classify
}