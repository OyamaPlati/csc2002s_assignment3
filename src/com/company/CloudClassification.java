package com.company;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class CloudClassification extends RecursiveAction {
  // Arguements
  int startStep, endStep;
  int fromRow, toRow;
  int fromCol, toCol;
  Vector[][][] advection;
  double[][][] convection;
  int[][][] classification;
  // ANSWER
  public int[][][] getCodeList() {return classification;}
  
  static final int SEQUENTIAL_CUTOFF = 81920;
  int dimension;
 
  public CloudClassification (Vector[][][] adv, double[][][] cnv, int[][][] cls, int timeStart, 
   int timeEnd, int startRow, int endRow, int startCol, int endCol, int dim) {
    advection = adv;
    convection = cnv;
    classification = cls;
    startStep = timeStart;
    endStep = timeEnd;
    fromRow = startRow;
    toRow = endRow;
    fromCol = startCol;
    toCol = endCol;
    dimension = dim;
  }
  
  @Override
  protected void compute () {
    if (dimension < SEQUENTIAL_CUTOFF) {
      for(int s = startStep; s < endStep; s++){
        for(int a = fromRow; a < toRow; a++)  {
          for(int b = fromCol; b < toCol; b++){
            // Local average
            double windMagnitude = localAve (advection, s, a, b, toRow, toCol); 
            // Classify
            if (windMagnitude > 0.2 && Math.abs(convection[s][a][b]) <= windMagnitude) {
                // CODE 1 - Wind Magnitude W greater than threshold
                // Code 1 striated stratus
                classification[s][a][b] = 1;
            } else if (Math.abs(convection[s][a][b]) > windMagnitude) {
                // CODE 0 - Absolute lift |u| greater than Wind Magnitude W
                // Code 0 cumulus
                classification[s][a][b] = 0;
            } else {
                // Code 2 armophous stratus
                classification[s][a][b] = 2;
            }
          }
        }
      } 
    }
    else {
      int midRow = fromRow + (toRow-fromRow)/2; 
      int midCol = fromCol + (toCol-fromCol)/2;
      dimension = dimension/4;
      
      // first quarter of matrix
      CloudClassification firstQuarter = new CloudClassification (advection, convection, classification, startStep, endStep, 
         fromRow, midRow, fromCol, midCol, dimension);
      // second quarter of matrix
      CloudClassification secondQuarter = new CloudClassification (advection, convection, classification, startStep, endStep, 
         fromRow, midRow, midCol+1, toCol, dimension);
      // third quarter of matrix
      CloudClassification thirdQuarter = new CloudClassification (advection, convection, classification, startStep, endStep, 
         midRow+1, toRow, fromCol, midCol, dimension);
      // fourth quarter of matrix
      CloudClassification fourthQuarter = new CloudClassification (advection, convection, classification, startStep, endStep, 
         midRow+1, toRow, midCol+1, toCol, dimension);
      
      ForkJoinTask.invokeAll(firstQuarter, secondQuarter, thirdQuarter, fourthQuarter);
    }
  }  
  
   private double localAve (Vector[][][] wind, int t, int i, int j, int xDimension, int yDimension) {
      Vector[][][] advection = wind;
      int dimx = xDimension;
      int dimy = yDimension;
      
      double localSumX = 0f;
      double localSumY = 0f;
      int numberOfNeighbours = 0;
      
      if (i==0 && j==0) {
         localSumX = advection[t][i][j].getX() + advection[t][i+1][j].getX() + advection[t][i][j+1].getX() +
                 advection[t][i+1][j+1].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i+1][j].getY() + advection[t][i][j+1].getY() +
                 advection[t][i+1][j+1].getY();
                 numberOfNeighbours = 4;
      }
      else if (i==(dimx-1) && j==0) {
         localSumX = advection[t][i][j].getX() + advection[t][i-1][j].getX() + advection[t][i-1][j+1].getX() +
                 advection[t][i][j+1].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i-1][j].getY() + advection[t][i-1][j+1].getY() +
                 advection[t][i][j+1].getY();
                  numberOfNeighbours = 4;

      }
      else if (i==(dimx-1) && j==(dimy-1)) {
         localSumX = advection[t][i][j].getX() + advection[t][i-1][j-1].getX() + advection[t][i][j-1].getX() +
                 advection[t][i-1][j].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i-1][j-1].getY() + advection[t][i][j-1].getY() +
                 advection[t][i-1][j].getY();
                  numberOfNeighbours = 4;
      }
      else if (i==0 && j==(dimy-1)) {
         localSumX = advection[t][i][j].getX() + advection[t][i][j-1].getX() + advection[t][i+1][j-1].getX() +
                 advection[t][i+1][j].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i][j-1].getY() + advection[t][i+1][j-1].getY() +
                 advection[t][i+1][j].getY();
                  numberOfNeighbours = 4;
      }
      else if (i==0 && ((j > 0) || (j < (dimy-1)))) {
         localSumX = advection[t][i][j].getX() + advection[t][i][j-1].getX() + advection[t][i+1][j-1].getX() +
                 advection[t][i+1][j].getX() + advection[t][i+1][j+1].getX() + advection[t][i][j+1].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i][j-1].getY() + advection[t][i+1][j-1].getY() +
                 advection[t][i+1][j].getY() + advection[t][i+1][j+1].getY() + advection[t][i][j+1].getY();
                  numberOfNeighbours = 6;
      }
      else if (((i > 0) || (i < (dimx-1))) && j==0) {
         localSumX = advection[t][i][j].getX() + advection[t][i-1][j].getX() + advection[t][i+1][j].getX() +
                 advection[t][i-1][j+1].getX() + advection[t][i][j+1].getX() + advection[t][i+1][j+1].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i-1][j].getY() + advection[t][i+1][j].getY() +
                 advection[t][i-1][j+1].getY() + advection[t][i][j+1].getY() + advection[t][i+1][j+1].getY();
                  numberOfNeighbours = 6;
      }
      else if (i==(dimx-1) && ((j > 0) || (j < (dimy-1)))) {
         localSumX = advection[t][i][j].getX() + advection[t][i][j-1].getX() + advection[t][i-1][j-1].getX() +
                 advection[t][i-1][j].getX() + advection[t][i-1][j+1].getX() + advection[t][i][j+1].getX();
         localSumY = advection[t][i][j].getY() + advection[t][i][j-1].getY() + advection[t][i-1][j-1].getY() +
                 advection[t][i-1][j].getY() + advection[t][i-1][j+1].getY() + advection[t][i][j+1].getY();
                  numberOfNeighbours = 6;
      }
      else if (i+1 < dimx && j+1 < dimy) {
         localSumX = advection[t][i-1][j-1].getX() + advection[t][i][j-1].getX() + advection[t][i+1][j-1].getX() +
                 advection[t][i-1][j].getX() + advection[t][i][j].getX() + advection[t][i+1][j].getX() +
                 advection[t][i-1][j+1].getX() + advection[t][i][j+1].getX() + advection[t][i+1][j+1].getX();
         localSumY = advection[t][i-1][j-1].getY() + advection[t][i][j-1].getY() + advection[t][i+1][j-1].getY() +
                 advection[t][i-1][j].getY() + advection[t][i][j].getY() + advection[t][i+1][j].getY() +
                 advection[t][i-1][j+1].getY() + advection[t][i][j+1].getY() + advection[t][i+1][j+1].getY();
                  numberOfNeighbours = 9;
      }

      double averageX = localSumX/numberOfNeighbours;
      double averageY = localSumY/numberOfNeighbours;
      return Math.sqrt(averageX*averageX + averageY*averageY);
   } 
}
