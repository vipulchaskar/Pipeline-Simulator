package com.company;


public class Main {

    public static void main(String[] args) {

        RegisterFile.SetupRegisters();

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

        CodeMemory.readFromFile("test.txt");
        //CodeMemory.printCodeLines();

        Pipeline.Setup();
        Pipeline.Simulate(27);
    }
}
