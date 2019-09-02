import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class PrevailingWind extends RecursiveAction {
   // Arguements
   int startStep, endStep;
   int fromRow, toRow;
   int fromCol, toCol;
   Vector[][][] arr;
   
   // Answers
   float xSum = 0;
   float ySum = 0;
   
   float getXSum() {return xSum;}
   float getYSum() {return ySum;}
   
   static final int SEQUENTIAL_CUTOFF = 81920;
   int dimension;
   
   public PrevailingWind (Vector[][][] wind, int start, int end, int fromRow, int toRow, int fromCol, int toCol, int dim) {
      arr = wind;
      startStep = start;
      endStep = end;
      this.fromRow = fromRow;
      this.toRow = toRow;
      this.fromCol = fromCol;
      this.toCol = toCol;
      dimension = dim;
   }
   
   @Override
   protected void compute () {
      if (dimension < SEQUENTIAL_CUTOFF) {    
          for(int s = startStep; s < endStep; s++){
            for(int a = fromRow; a < toRow; a++)  {
              for(int b = fromCol; b < toCol; b++){
                  xSum += arr[s][a][b].getX();
                  ySum += arr[s][a][b].getY();
              }
            }
          }    
      }
      else {
         int j = fromRow + (toRow-fromRow)/2; 
         int k = fromCol + (toCol-fromCol)/2;
         dimension = dimension/4;
         
         // first quarter of matrix
         PrevailingWind firstQuarter = new PrevailingWind(arr, startStep, endStep, 
            fromRow, j, fromCol, k, dimension);
         // second quarter of matrix 
         PrevailingWind secondQuarter = new PrevailingWind(arr, startStep, endStep, 
            fromRow, j, k+1, toCol, dimension);
         // third quarter of matrix 
         PrevailingWind thirdQuarter = new PrevailingWind(arr, startStep, endStep, 
            j+1, toRow, fromCol, k, dimension);
         // fourth quarter of matrix
         PrevailingWind fourthQuarter = new PrevailingWind(arr, startStep, endStep,
            j+1, toRow, k+1, toCol, dimension);
         
         ForkJoinTask.invokeAll(firstQuarter, secondQuarter, thirdQuarter, fourthQuarter);
           
         xSum = firstQuarter.xSum + secondQuarter.xSum + thirdQuarter.xSum + fourthQuarter.xSum;
         ySum = firstQuarter.ySum + secondQuarter.ySum + thirdQuarter.ySum + fourthQuarter.ySum;
      }
   }
}