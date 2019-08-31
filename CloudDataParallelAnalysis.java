import java.util.concurrent.ForkJoinPool;

public class CloudDataParallelAnalysis {
   static long startTime = 0;
	
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
   
   static final ForkJoinPool fjPool = new ForkJoinPool();
   
   static void overallAverage(CloudData cloudData) {
      PrevailingWind windAnalysis = new PrevailingWind(cloudData.getAdvection(), 0, cloudData.timestep(), 0, cloudData.width(), 
            0, cloudData.height(), cloudData.dim());
      fjPool.invoke(windAnalysis);
      CloudData.xAverage = windAnalysis.getXSum()/cloudData.dim();
      CloudData.yAverage = windAnalysis.getYSum()/cloudData.dim();
   }
   
   static int[][][] classification(CloudData cloudData) {
      CloudClassification classifyAnalysis = 
         new CloudClassification(cloudData.getAdvection(), cloudData.getConvection(), cloudData.getClassification(), 
         0, cloudData.timestep(), 0, cloudData.width(), 0, cloudData.height(), cloudData.dim());
      fjPool.invoke(classifyAnalysis);
      return classifyAnalysis.getCodeList();
   }
   
   public static void main(String[] args) {
      System.out.printf("Running Threaded Test\n");
      System.out.printf("Precision size of %d bits\n", Float.BYTES*8);
      String dataFile = args[0];
      String outputFile = args[1];
      CloudData cloudData = new CloudData ();
      cloudData.readData(dataFile);
      tick();
      overallAverage(cloudData);
      float time = tock();
      System.out.printf("Time threaded PREVAILING WIND COMPUTE: %f s\n", time);
      tick();
      cloudData.setClassification(classification(cloudData));
      time = tock();
      System.out.printf("Time CLASSIFICATION COMPUTE: %f s\n", time); 
      cloudData.writeData(outputFile, CloudData.xAverage, CloudData.yAverage);
      System.out.printf("End Threaded Test\n");
   }
}

