package com.company;
import java.util.*;
import java.io.*;
import java.text.*;
import java.math.*;
import java.lang.Math;

public class generateDataset {
	static final Scanner scanner = new Scanner(System.in);
	public static void main(String [] args) {
		String fileName = "/home/p/pltoya001/Documents/src/com/company/windGenerated.txt";
		int dimt = 25;
		int dimx = 600;
		int dimy = 600;
		final int VALUES = 3;
		int dim = dimt*dimy*dimx;
		int span = dimy*dimx;

		Locale.setDefault(Locale.CANADA);

		try{
			FileWriter fileWriter = new FileWriter(fileName);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			// write
			int count = 0;
			do {
				for (int i = 0; i < span; i++) {
					for (int k = 0; k < VALUES; k++) {
						printWriter.printf("%f ", (Math.random() * 6) - 3);
					}
				}
				printWriter.printf("\n");			
				count++;
			}while(count < dim/span);

            		printWriter.close();
        	}
        	catch (IOException e){
            		System.out.println("Unable to open output file "+fileName);
            		e.printStackTrace();
        	}						
	}
}
