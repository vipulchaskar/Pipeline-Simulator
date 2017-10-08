package com.company;

public class Pipeline {

    private static boolean halted = false;
    private static boolean branch = false;
    private static int targetAddress;
    private static FStage fs;
    private static DRFStage drfs;
    private static EXStage exs;
    private static MUL1Stage mul1s;
    private static MUL2Stage mul2s;
    private static MEMStage mems;
    private static WBStage wbs;

    public static void Setup() {
        fs = new FStage();
        drfs = new DRFStage(fs);
        exs = new EXStage();
        mul1s = new MUL1Stage();
        mul2s = new MUL2Stage();
        mems = new MEMStage();
        wbs = new WBStage();
        halted = false;
        branch = false;
    }

    public static void TakeBranch(int newTargetAddress) {
        branch = true;
        targetAddress = newTargetAddress;
    }

    public static boolean IsBranching() {
        return branch;
    }

    public static boolean isHalted() {
        return halted;
    }

    public static void setHalted(boolean halted) {
        Pipeline.halted = halted;
    }

    public static void Simulate(int clockCycles) {

        System.out.println(" F  | DRF|  EX|MUL1|MUL2| MEM|  WB| Cycle no.");
        for (int i = 1; i <= clockCycles; i++) {

            wbs.execute();

            mems.execute();

            mul2s.execute();

            mul1s.execute();

            exs.execute();

            drfs.execute();

            fs.execute();

            System.out.println(String.format("%1$4s", fs.getCurInstr()) + "|" + String.format("%1$4s", drfs.getCurInstr())
                    + "|" + String.format("%1$4s", exs.getCurInstr()) + "|" + String.format("%1$4s", mul1s.getCurInstr())
                    + "|" + String.format("%1$4s", mul2s.getCurInstr()) + "|" + String.format("%1$4s", mems.getCurInstr())
                    + "|" + String.format("%1$4s", wbs.getCurInstr()) + "|   " + String.valueOf(i));

            if (drfs.isStalled()) {
                fs.setStalled(true);
            }
            else {
                fs.setStalled(false);
            }

            if (branch) {
                drfs.outputInstruction = null;
                fs.outputInstruction = null;
                fs.setNextInstAddress(targetAddress);
                branch = false;
            }

            if (halted) {
                //drfs.outputInstruction = null;
                fs.outputInstruction = null;
            }

            // WB <-- MEM
            wbs.inputInstruction = mems.outputInstruction;

            // MEM <-- MUL, EX
            // Selection Logic
            if (mul2s.outputInstruction == null && exs.outputInstruction == null) {
                mems.inputInstruction = null;
            } else if (mul2s.outputInstruction != null && exs.outputInstruction == null) {
                mems.inputInstruction = mul2s.outputInstruction;
                mul2s.outputInstruction = null;
            } else if (mul2s.outputInstruction == null && exs.outputInstruction != null) {
                exs.setStalled(false);
                mems.inputInstruction = exs.outputInstruction;
                exs.outputInstruction = null;
            } else {
                // Collision! Give preference to MUL instructions
                mems.inputInstruction = mul2s.outputInstruction;
                mul2s.outputInstruction = null;
                // Now stall EX and things before it.
                exs.setStalled(true);
            }

            // MUL2 <-- MUL1
            mul2s.inputInstruction = mul1s.outputInstruction;

            // MUL, EX <-- DRF
            // Splitting Logic
            if (drfs.outputInstruction != null) {

                if ((drfs.outputInstruction.getOpCode() == Commons.I.MUL
                        || drfs.outputInstruction.getOpCode() == Commons.I.DIV
                        || drfs.outputInstruction.getOpCode() == Commons.I.HALT)
                        && mul1s.inputInstruction != drfs.outputInstruction) {

                    mul1s.inputInstruction = drfs.outputInstruction;
                    exs.inputInstruction = null;

                } else if (exs.inputInstruction != drfs.outputInstruction) {
                    if ( ! exs.isStalled()) {
                        drfs.setExStalled(false);
                        exs.inputInstruction = drfs.outputInstruction;
                    }
                    else
                        drfs.setExStalled(true);
                    mul1s.inputInstruction = null;
                }
            }
            else {
                // This will create problems if they're stalled!
                mul1s.inputInstruction = null;
                if (! exs.isStalled())
                    exs.inputInstruction = null;
            }

            // DRF <-- F
            if (drfs.isExStalled())
                fs.setExStalled(true);
            else
                fs.setExStalled(false);

            if (! fs.isStalled() && ! fs.isExStalled())
                drfs.inputInstruction = fs.outputInstruction;
        }
    }

    public static void RemoveFlagSettingCapability() {
        if (exs.inputInstruction != null)
            exs.inputInstruction.setIsGonnaSetFlags(false);

        if (mul1s.inputInstruction != null)
            mul1s.inputInstruction.setIsGonnaSetFlags(false);

        if (mul2s.inputInstruction != null)
            mul2s.inputInstruction.setIsGonnaSetFlags(false);

        if (mems.inputInstruction != null)
            mems.inputInstruction.setIsGonnaSetFlags(false);

        if (wbs.inputInstruction != null)
            wbs.inputInstruction.setIsGonnaSetFlags(false);

    }

    public static void Display() {
        System.out.println("\nContents of Pipeline stages:");
        System.out.println("F stage : " + fs.getCurInstr() + " | " + fs.getCurInstrString());
        System.out.println("DRF stage : " + drfs.getCurInstr() + " | " + drfs.getCurInstrString());
        System.out.println("EX stage : " + exs.getCurInstr() + " | " + exs.getCurInstrString());
        System.out.println("MUL1 stage : " + mul1s.getCurInstr() + " | " + mul1s.getCurInstrString());
        System.out.println("MUL2 stage : " + mul2s.getCurInstr() + " | " + mul2s.getCurInstrString());
        System.out.println("MEM stage : " + mems.getCurInstr() + " | " + mems.getCurInstrString());
        System.out.println("WB stage : " + wbs.getCurInstr() + " | " + wbs.getCurInstrString());

        System.out.println("----------------------------------");

        System.out.println("Registers:");
        for (int i = 0; i < Commons.totalRegisters; i++) {
            System.out.println("R" + String.valueOf(i) + ": " + String.valueOf(RegisterFile.ReadFromRegister(i)));
        }

        System.out.println("----------------------------------");

        System.out.println("Memory locations:");
        for (int i = 0; i < 100; i++) {
            System.out.println("Mem[" + String.valueOf(i) + "] = " + String.valueOf(DataMemory.readFromMemory(i*Commons.dataAddressLength)));
        }

        System.out.println("----------------------------------");
    }
}
