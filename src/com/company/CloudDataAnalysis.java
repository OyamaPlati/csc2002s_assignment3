package com.company;
public class CloudDataAnalysis {
   static double xAverage, yAverage; // Prevailing wind average
   private static void prevailingWindAve (CloudData data) {
      double sumX = 0;
      double sumY = 0;
      Vector [][][] wind = data.getAdvection();
    
      for(int s = 0; s < data.timestep(); s++){
          for(int a = 0; a < data.width(); a++){
              for(int b = 0; b < data.height(); b++){
                  sumX += wind[s][a][b].getX();
                  sumY += wind[s][a][b].getY();
              }
          }
      }
      
      xAverage = sumX/data.dim();
      yAverage = sumY/data.dim();
   }
   
   private static double localAve (CloudData data, int t, int i, int j) {
      Vector [][][] advection = data.getAdvection();
      int dimt = data.timestep();
      int dimx = data.width();
      int dimy = data.height();
      
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
   
   private static void classify (CloudData data) {
      double [][][] convection = data.getConvection();
      int [][][] classification = data.getClassification();
      for(int s = 0; s < data.timestep(); s++){
          for(int a = 0; a < data.width(); a++){
              for(int b = 0; b < data.height(); b++){
                  double windMagnitude = localAve (data, s, a, b);
                  // classify
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
   
   static long startTime = 0;
	
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
   
   public static void main (String args[]) {
      System.out.printf("Running Unthreaded Test\n");
      System.out.printf("Precision size of %d bits\n", Float.BYTES*8);
      String dataFile = args[0];
      String outputFile = args[1];
      CloudData cloudData = new CloudData ();
      cloudData.readData(dataFile);
      tick();
      prevailingWindAve(cloudData);
      float time = tock();
      System.out.printf("Time PREVAILING WIND COMPUTE: %f s\n", time);
      tick();
      classify(cloudData);
      time = tock();
      System.out.printf("Time CLASSIFICATION COMPUTE: %f s\n", time); 
      cloudData.writeData(outputFile, xAverage, yAverage);
      System.out.printf("End Unthreaded Test\n");
   }
}
