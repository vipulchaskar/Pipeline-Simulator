package com.company;

public class DataMemory {

    private static int baseAddress = Commons.dataBaseAddress;
    private static int [] dataArray;

    public static void initialize() {
        dataArray = new int[Commons.dataTotalLocations];
    }

    public static void writeToMemory(int value, int address) {

        if (address < Commons.dataBaseAddress || address > (Commons.dataBaseAddress + Commons.dataTotalLocations)) {
            System.out.println("Write to memory failed! Illegal address, " + address + " provided.");
            return;
            // TODO: Throw an exception in this case?
        }

        dataArray[address] = value;
    }

    public static int readFromMemory(int address) {

        if (address < Commons.dataBaseAddress || address > (Commons.dataBaseAddress + Commons.dataTotalLocations)) {
            System.out.println("Read from memory failed! Illegal address, " + address + " provided.");
            return -1;
            // TODO: Throw an exception in this case?
        }

        return dataArray[address];
    }
}
