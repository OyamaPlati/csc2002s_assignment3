package com.company;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
import java.io.PrintWriter;

class AutoMarker{
    public static void main(String []args){
        try{ 
			Scanner file_original = new Scanner(new File(args[0]), "UTF-8");
			Scanner file_generated = new Scanner(new File(args[1]), "UTF-8");
			
			/*if (file_original.length != file_generated.size){
			    System.out.println("file lenghts inconsistent.");
			    System.exit(0);
			}*/
			int i =0;
			//for (int i=0; i<file_original.size(); i++){
			
			file_original.nextLine();
			file_original.nextLine();
			file_generated.nextLine();
			file_generated.nextLine();
						
			while (file_original.hasNext()){
			    //System.out.println(file_original.nextInt());
			    //System.out.println(file_generated.nextInt());
			    
			    if (file_original.nextInt() != file_generated.nextInt()){
			        System.out.println(i);
			    }
			    /*if (file_original.nextFloat() != file_generated.nextFloat()){
			        System.out.println(i);
			    }*/
			    i++;
			}			
		}
		catch(Exception e){
		    e.printStackTrace();
		}
    }
}
