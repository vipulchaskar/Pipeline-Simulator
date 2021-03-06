package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

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

    // Stores the clock cycle when last flag producer instruction was dispatched.
    public static int last_flag_producer_clock_cycle = -1;

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

    private static PhysicalRegister CopyPhysicalRegister(PhysicalRegister src) {
        PhysicalRegister dest = new PhysicalRegister();

        dest.setValue(src.getValue());
        dest.setAllocated(src.isAllocated());
        dest.setRenamed(src.isStatus());
        dest.setStatus(src.isStatus());
        dest.setzFlag(src.iszFlag());

        return dest;
    }

    private static ArrayList<PhysicalRegister> DeepCopyPhysicalRegisters(ArrayList<PhysicalRegister> phyRegs) {
        ArrayList<PhysicalRegister> clone = new ArrayList<>();
        for(PhysicalRegister p : phyRegs)
            clone.add(CopyPhysicalRegister(p));

        return clone;
    }

    public static void takeBackup(int PC) {
        PhysicalRegisterBackup newBackup = new PhysicalRegisterBackup(Arrays.copyOf(rename_table, rename_table.length),
                Arrays.copyOf(rename_table_bit, rename_table_bit.length),
                psw_rename_table, psw_rename_table_bit, DeepCopyPhysicalRegisters(physicalRegisters));

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
        System.out.println(Arrays.toString(rename_table));
        System.out.println(Arrays.toString(rename_table_bit));
    }

    public static String renameInstruction(InstructionInfo inputInstruction) {

        switch (inputInstruction.getOpCode()) {

            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case AND:
            case OR:
            case XOR:
                inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getdRegAddr()) + ",");

                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                if (inputInstruction.isLiteralPresent()) {
                    inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                }
                else {
                    if (inputInstruction.getsReg2Addr() != -1)
                        inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg2Addr()));
                    else
                        inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc2()) + ",");
                }

                break;

            case LOAD:
                inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getdRegAddr()) + ",");

                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case STORE:
                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                if (inputInstruction.getsReg2Addr() != -1)
                    inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg2Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc2()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case MOVC:
                inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getdRegAddr()) + ",");
                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case BZ:
            case BNZ:
                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case HALT:
            case NOOP:
                break;

            case JUMP:
                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case JAL:
                inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getdRegAddr()) + ",");

                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("P" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            default:
                break;
        }

        
        return inputInstruction.getRenamedInsString();
    }

    
    public static String printRenameTableEntries() {

	    StringBuilder outputString = new StringBuilder("");
	    boolean flag = true;
	    for (int index = 0; index < Commons.totalRegisters; index++) 
	    {
		    if ( rename_table_bit[index] == true )
		    {
			    flag = false;
			    outputString.append("\n* R" + index  + " : P" + rename_table[index] );
		    }
	    }
	    
	    if ( flag == true )
	    {
		    return " Empty";
	    }
	    
	    return outputString.toString();
    }
}

