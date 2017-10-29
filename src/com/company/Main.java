package com.company;

import java.util.Scanner;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        int choice = 0;
        Scanner in = new Scanner(System.in);

        if (args.length != 1) {
            System.out.println("Please pass a file name as input.");
            System.exit(1);
        }

        File f = new File(args[0]);
        if (!f.exists() || f.isDirectory()) {
            System.out.println("Error! File " + args[0] + " not found.");
            System.exit(1);
        }

        while (choice != 4) {
            System.out.println("\nMenu:");
            System.out.println("(input file: " + args[0] + ")");
            System.out.println("1. Initialize");
            System.out.println("2. Simulate");
            System.out.println("3. Display");
            System.out.println("4. Exit");
            System.out.println("Please enter your choice: ");

            choice = in.nextInt();

            switch (choice) {
                case 1:
                    RegisterFile.SetupRegisters();
                    CodeMemory.readFromFile(args[0]);
                    DataMemory.initialize();
                    Pipeline.Setup();
                    System.out.println("Initialization successful.");
                    break;

                case 2:
                    System.out.println("Enter the number of cycles to simulate: ");
                    int cycles = in.nextInt();
                    Pipeline.Simulate(cycles);
                    break;

                case 3:
                    Pipeline.Display();
                    break;

                case 4:
                    break;

                default:
                    System.out.println("Your input was not understood. Please enter again.");
            }
        }


        // TEST CODE----------------
        /*DataMemory.writeToMemory(100, 0);
        DataMemory.writeToMemory(200, 5);
        DataMemory.writeToMemory(300, 5000); //This should throw error

        System.out.println(DataMemory.readFromMemory(0));
        System.out.println(DataMemory.readFromMemory(5));
        System.out.println(DataMemory.readFromMemory(5000)); //This should throw error

        RegisterFile.WriteToRegister(0, 123);
        RegisterFile.WriteToRegister(5, 456);
        RegisterFile.WriteToRegister(20, 789); //This should throw error
        RegisterFile.SetRegisterStatus(1, true);
        RegisterFile.SetRegisterStatus(2, false);
        RegisterFile.SetRegisterStatus(30, false); //This should throw error

        System.out.println(RegisterFile.ReadFromRegister(0));
        System.out.println(RegisterFile.ReadFromRegister(5));
        System.out.println(RegisterFile.ReadFromRegister(20)); //This should throw error
        System.out.println(RegisterFile.GetRegisterStatus(1));
        System.out.println(RegisterFile.GetRegisterStatus(2));
        System.out.println(RegisterFile.GetRegisterStatus(30)); //This should throw error

        Flags.setCarry(true);
        Flags.setNegative(false);
        Flags.setZero(true);

        assert(Flags.getCarry());
        assert(!Flags.getNegative());
        assert(Flags.getZero());*/
        // TEST CODE END-------------

    }
}
