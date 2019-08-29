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
            dimx = sc.nextInt();
            dimy = sc.nextInt();

            // initialize and load advection (wind direction and strength) and convection
            advection = new Vector[dimt][dimx][dimy];
            convection = new float[dimt][dimx][dimy];
            for(int t = 0; t < dimt; t++) {
                for (int x = 0; x < dimx; x++) {
                    for (int y = 0; y < dimy; y++) {
                        advection[t][x][y] = new Vector();
                        advection[t][x][y].x = sc.nextFloat();
                        advection[t][x][y].y = sc.nextFloat();
                        convection[t][x][y] = sc.nextFloat();
                    }
                }
            }

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
        for(int t = 0; t < dimt; t++){
            for(int x = 0; x < dimx; x++){
                for(int y = 0; y < dimy; y++){
                    sumX += advection[t][x][y].x;
                    sumY += advection[t][x][y].y;
                }
            }
        }

        xAverage = (float) sumX/dim();
        yAverage = (float)sumY/dim();
    }

    // calculate local average of 3x3 window for each i,j of this matrix and classification in code 0 or code 1 or code 2
    void classify () {
        for(int t = 0; t < dimt; t++) {
            for (int i = 0; i < dimx; i++) {
                for (int j = 0; j < dimy; j++) {

                    // find local average for each element in the air layer and classify
                    float windMagnitude = localAverage(t, i, j);

                    // classify
                    if (Math.abs(convection[t][i][j]) > Math.abs(windMagnitude)) {
                        // Code 0 cumulus
                        classification[t][i][j] = 0;
                    } else if (Math.abs(windMagnitude) > 0.2 || Math.abs(windMagnitude) >= Math.abs(convection[t][i][j])) {
                        // Code 1 striated stratus
                        classification[t][i][j] = 1;

                    } else {
                        // Code 2 armophous stratus
                        classification[t][i][j] = 1;

                    }
                }
            }
        }
    }

    float localAverage(int t, int x, int y) {
        // (x,y) local average neighbours W_ave i.e to each layer element
        // formula result = sqrt(averageX^2, averageY^2)
        float localSumX = 0f;
        float localSumY = 0f;
        int numberOfNeighbours = 0;

        for (int i = Math.max(0,x-1); i < Math.min(dimx,x+2); i++) {
            for (int j = Math.max(0, y-1); j < Math.min(dimy, y+2); j++) {
                localSumX += advection[t][x][y].x;
                localSumY += advection[t][x][y].y;
                numberOfNeighbours++;
            }
        }

        float averageX = localSumX/numberOfNeighbours;
        float averageY = localSumY/numberOfNeighbours;

        return (float)Math.sqrt(averageX*averageX + averageY*averageY);
    }
}