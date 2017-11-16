package com.company;

import java.util.ArrayList;
import java.util.HashMap;

class PhysicalRegisterBackup {
    public int [] bak_rename_table;
    public boolean [] bak_rename_table_bit;
    public int bak_psw_rename_table;
    public boolean bak_psw_rename_table_bit;
    public ArrayList<PhysicalRegister> bak_physicalRegisters;

    PhysicalRegisterBackup(int [] bak_rename_table, boolean [] bak_rename_table_bit, int bak_psw_rename_table,
                           boolean bak_psw_rename_table_bit, ArrayList<PhysicalRegister> bak_physicalRegisters) {
        this.bak_rename_table = bak_rename_table;
        this.bak_rename_table_bit = bak_rename_table_bit;
        this.bak_psw_rename_table = bak_psw_rename_table;
        this.bak_psw_rename_table_bit = bak_psw_rename_table_bit;
        this.bak_physicalRegisters = bak_physicalRegisters;
    }
}

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

    private static HashMap<Integer, PhysicalRegisterBackup> backups = new HashMap<>();

    public static void SetupRegisters() {
        physicalRegisters = new ArrayList<>();

        for (int i = 0; i < Commons.totalPhysicalRegisters; i++) {
            physicalRegisters.add(i, new PhysicalRegister());
        }
    }

    public static boolean FreePhysicalRegisterAvailable() {

        for (int i = 0; i < Commons.totalPhysicalRegisters; i++) {
            if (! physicalRegisters.get(i).isAllocated())
                return true;
        }
        return false;
    }

    public static int GetNewPhysicalRegister() {

        boolean found = false;
        int newIndex;

        for (newIndex = 0; newIndex < Commons.totalPhysicalRegisters; newIndex++) {
            if (! physicalRegisters.get(newIndex).isAllocated()) {
                found = true;
                break;
            }
        }

        if (! found)
            return -1;

        physicalRegisters.get(newIndex).setAllocated(true);
        physicalRegisters.get(newIndex).setRenamed(false);
        physicalRegisters.get(newIndex).setStatus(false);
        physicalRegisters.get(newIndex).setzFlag(false);

        return newIndex;
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

    public static void takeBackup(int PC) {
        PhysicalRegisterBackup newBackup = new PhysicalRegisterBackup(rename_table.clone(), rename_table_bit.clone(),
                psw_rename_table, psw_rename_table_bit, (ArrayList<PhysicalRegister>)physicalRegisters.clone());

        backups.put(PC, newBackup);
    }

    public static void restoreBackup(int PC) {
        PhysicalRegisterBackup backupToRestore = backups.get(PC);

        rename_table = backupToRestore.bak_rename_table;
        rename_table_bit = backupToRestore.bak_rename_table_bit;
        psw_rename_table = backupToRestore.bak_psw_rename_table;
        psw_rename_table_bit = backupToRestore.bak_psw_rename_table_bit;
        physicalRegisters = backupToRestore.bak_physicalRegisters;
    }

    public static void printAll() {
        System.out.println(String.valueOf(rename_table));
    }

}

