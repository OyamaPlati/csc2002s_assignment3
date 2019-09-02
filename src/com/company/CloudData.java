package com.company;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CloudData {

    Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
    double [][][] convection; // vertical air movement strength, that evolves over time
    int [][][] classification; // cloud type per grid point, evolving over time
    int dimx, dimy, dimt; // data dimensions
    static double xAverage, yAverage;
    
    int width () {return dimx;}
    
    int height () {return dimy;}
    
    int timestep () {return dimt;}
    
    Vector [][][] getAdvection () {return advection;}
    double [][][] getConvection () {return convection;}
    int [][][] getClassification () {return classification;}
    void setClassification (int[][][] cl) {classification = cl;}
    
    // overall number of elements in the timeline grids
    int dim () {return dimt*dimx*dimy;}

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

            // Initialize and load advection (wind direction and strength) and convection
            advection = new Vector[dimt][dimx][dimy];
            convection = new double[dimt][dimx][dimy];
            for(int t = 0; t < dimt; t++) {
                for (int x = 0; x < dimx; x++) {
                    for (int y = 0; y < dimy; y++) {
                        advection[t][x][y] = new Vector();
                        advection[t][x][y].setX(Double.parseDouble(sc.next().trim()));
                        advection[t][x][y].setY(Double.parseDouble(sc.next().trim()));
                        convection[t][x][y] = Double.parseDouble(sc.next().trim());
                    }
                }
            }

            // load the classification codes for each layer element
            classification = new int[dimt][dimx][dimy];
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
    void writeData(String fileName, double x_ave, double y_ave) {
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
            printWriter.printf("%f %f\n", x_ave, y_ave);

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
}
