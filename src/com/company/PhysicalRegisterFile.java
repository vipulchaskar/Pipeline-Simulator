package com.company;

import java.util.ArrayList;

public class PhysicalRegisterFile {

    // Maps the architectural registers to their corresponding physical registers
    public static int [] rename_table = new int[Commons.totalRegisters];

    // Bit vector to indicate whether the latest value for an architectural register is in architectural register (false)
    // or in physical register (true)
    public static boolean [] rename_table_bit = new boolean[Commons.totalRegisters];

    // Points to the physical register containing latest value of PSW flags
    public static int psw_rename_table;

    // Bit to indicate whether latest value of PSW is in PSW architectural register (false) or
    // in some physical register (true)
    public static boolean psw_rename_table_bit;

    private static ArrayList<PhysicalRegister> physicalRegisters = new ArrayList<>();

    public static void SetupRegisters() {
        physicalRegisters = new ArrayList<>();

        for (int i = 0; i < Commons.totalPhysicalRegisters; i++) {
            physicalRegisters.add(i, new PhysicalRegister());
        }
    }

    public static void WriteToRegister(int index, int value) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error writing to physical register! Illegal index no. (" + index + ") given");
            return;
        }

        physicalRegisters.get(index).setValue(value);
    }

    public static int ReadFromRegister(int index) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error reading from physical register! Illegal index no. (" + index + ") given");
            return -1;
            // TODO: This return is wrong!
        }

        return physicalRegisters.get(index).getValue();
    }

    public static void SetRegisterStatus(int index, boolean status) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error setting physical register status! Illegal index no. (" + index + ") given");
            return;
        }

        physicalRegisters.get(index).setStatus(status);
    }

    public static boolean GetRegisterStatus(int index) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error getting physical register status! Illegal index no. (" + index + ") given");
            return false;
            // TODO: This is wrong!
        }

        return physicalRegisters.get(index).isStatus();
    }

    public static void SetAllocated(int index, boolean allocated) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error setting physical register allocation status! Illegal index no : " + index);
            return;
        }

        physicalRegisters.get(index).setAllocated(allocated);
    }

    public static boolean GetAllocated(int index) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error getting physical register allocation status! Illegal index no : " + index);
            return false;
        }

        return physicalRegisters.get(index).isAllocated();
    }

    public static void SetRenamed(int index, boolean renamed) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error setting physical register renamed status! Illegal index no : " + index);
            return;
        }

        physicalRegisters.get(index).setRenamed(renamed);
    }

    public static boolean GetRenamed(int index) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error getting physical register renamed status! Illegal index no : " + index);
            return false;
        }

        return physicalRegisters.get(index).isRenamed();
    }

    public static void SetZFlag(int index, boolean zFlag) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error setting physical register Z-Flag status! Illegal index no : " + index);
            return;
        }

        physicalRegisters.get(index).setzFlag(zFlag);
    }

    public static boolean GetZFlag(int index) {
        if (index < 0 || index >= Commons.totalPhysicalRegisters) {
            System.out.println("Error setting physical register Z-Flag status! Illegal index no : " + index);
            return false;
        }

        return physicalRegisters.get(index).iszFlag();
    }

}

