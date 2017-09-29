package com.company;

import java.util.ArrayList;

public class RegisterFile {

    public static ArrayList<Register> registers = new ArrayList<>();

    public static void SetupRegisters() {

        for (int i = 0; i < Commons.totalRegisters; i++) {
            registers.add(i, new Register());
        }
    }

    public static void WriteToRegister(int index, int value) {
        if (index < 0 || index >= Commons.totalRegisters) {
            System.out.println("Error writing to register! Illegal index no. (" + index + ") given");
            return;
        }

        registers.get(index).setValue(value);
    }

    public static int ReadFromRegister(int index) {
        if (index < 0 || index >= Commons.totalRegisters) {
            System.out.println("Error reading from register! Illegal index no. (" + index + ") given");
            return -1;
            // TODO: This return is wrong!
        }

        return registers.get(index).getValue();
    }

    public static void SetRegisterStatus(int index, boolean status) {
        if (index < 0 || index >= Commons.totalRegisters) {
            System.out.println("Error setting register status! Illegal index no. (" + index + ") given");
            return;
        }

        registers.get(index).setValid(status);
    }

    public static boolean GetRegisterStatus(int index) {
        if (index < 0 || index >= Commons.totalRegisters) {
            System.out.println("Error getting register status! Illegal index no. (" + index + ") given");
            return false;
            // TODO: This is wrong!
        }

        return registers.get(index).isValid();
    }
}
