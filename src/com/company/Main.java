package com.company;

public class Main {

    public static void main(String[] args) {
        // Take the following command-line arguments <data file name> <output file name>
        String dataFile = args[0];
        String outputFile = args[1];

        CloudData cloudData = new CloudData();
        cloudData.readData(dataFile);
        cloudData.writeData(outputFile);
    }
}
