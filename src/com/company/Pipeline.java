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
    private static DIVStage div1s;
    private static DIVStage div2s;
    private static DIVStage div3s;
    private static DIVStage div4s;
    private static MEMStage mems;
    private static WBStage wbs;

    public static void Setup() {
        fs = new FStage();
        drfs = new DRFStage(fs);
        exs = new EXStage();
        mul1s = new MUL1Stage();
        mul2s = new MUL2Stage();
        div1s = new DIVStage(true);
        div2s = new DIVStage(false);
        div3s = new DIVStage(false);
        div4s = new DIVStage(false);
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

        //System.out.println(" F  | DRF|  EX|MUL1|MUL2| MEM|  WB| Cycle no.");
        for (int i = 1; i <= clockCycles; i++) {

            wbs.execute();

            mems.execute();

            div4s.execute();

            div3s.execute();

            div2s.execute();

            div1s.execute();

            mul2s.execute();

            mul1s.execute();

            exs.execute();

            drfs.execute();

            fs.execute();

            //System.out.println(String.format("%1$4s", fs.getCurInstr()) + "|" + String.format("%1$4s", drfs.getCurInstr())
            //        + "|" + String.format("%1$4s", exs.getCurInstr()) + "|" + String.format("%1$4s", mul1s.getCurInstr())
            //        + "|" + String.format("%1$4s", mul2s.getCurInstr()) + "|" + String.format("%1$4s", mems.getCurInstr())
            //        + "|" + String.format("%1$4s", wbs.getCurInstr()) + "|   " + String.valueOf(i));
            System.out.println("Cycle " + String.valueOf(i) + ":");
            System.out.println("Fetch       : " + fs.getCurInstr() + " " + fs.getCurInstrString() + " " + fs.getStalledStr());
            System.out.println("DRF         : " + drfs.getCurInstr() + " " + drfs.getCurInstrString() + " " + drfs.getStalledStr());
            System.out.println("INTFU       : " + exs.getCurInstr() + " " + exs.getCurInstrString() + " " + exs.getStalledStr());
            System.out.println("MUL1        : " + mul1s.getCurInstr() + " " + mul1s.getCurInstrString() + " " + mul1s.getStalledStr());
            System.out.println("MUL2        : " + mul2s.getCurInstr() + " " + mul2s.getCurInstrString() + " " + mul2s.getStalledStr());
            System.out.println("DIV1        : " + div1s.getCurInstr() + " " + div1s.getCurInstrString() + " " + div1s.getStalledStr());
            System.out.println("DIV2        : " + div2s.getCurInstr() + " " + div2s.getCurInstrString() + " " + div2s.getStalledStr());
            System.out.println("DIV3        : " + div3s.getCurInstr() + " " + div3s.getCurInstrString() + " " + div3s.getStalledStr());
            System.out.println("DIV4        : " + div4s.getCurInstr() + " " + div4s.getCurInstrString() + " " + div4s.getStalledStr());
            System.out.println("MEM         : " + mems.getCurInstr() + " " + mems.getCurInstrString() + " " + mems.getStalledStr());
            System.out.println("WB          : " + wbs.getCurInstr() + " " + wbs.getCurInstrString() + " " + wbs.getStalledStr());
            System.out.println("");

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

            // MEM <-- MUL, EX, DIV
            // Selection Logic
            if (div4s.outputInstruction == null) {
                if (mul2s.outputInstruction == null && exs.outputInstruction == null) {
                    mems.inputInstruction = null;
                } else if (mul2s.outputInstruction != null && exs.outputInstruction == null) {
                    mul2s.setStalled(false);
                    mul1s.setStalled(false);
                    mems.inputInstruction = mul2s.outputInstruction;
                    mul2s.outputInstruction = null;
                } else if (mul2s.outputInstruction == null && exs.outputInstruction != null) {
                    exs.setStalled(false);
                    mems.inputInstruction = exs.outputInstruction;
                    exs.outputInstruction = null;
                } else {
                    // Collision! Give preference to MUL instructions
                    mul1s.setStalled(false);
                    mul2s.setStalled(false);
                    mems.inputInstruction = mul2s.outputInstruction;
                    mul2s.outputInstruction = null;
                    // Now stall EX and things before it.
                    exs.setStalled(true);
                }
            }
            else {
                mems.inputInstruction = div4s.outputInstruction;
                div4s.outputInstruction = null;
                //TODO: Is the If-Condition really required?
                if (mul2s.outputInstruction != null) {
                    mul2s.setStalled(true);
                    mul1s.setStalled(true);
                }
                if (exs.outputInstruction != null) {
                    exs.setStalled(true);
                }
            }

            // MUL2 <-- MUL1
            mul2s.inputInstruction = mul1s.outputInstruction;

            // DIV x+1 <-- DIV x
            div4s.inputInstruction = div3s.outputInstruction;
            div3s.inputInstruction = div2s.outputInstruction;
            div2s.inputInstruction = div1s.outputInstruction;

            // MUL, EX <-- DRF
            // Splitting Logic
            if (drfs.outputInstruction != null) {

                if ((drfs.outputInstruction.getOpCode() == Commons.I.DIV
                        || drfs.outputInstruction.getOpCode() == Commons.I.HALT)
                        && div1s.inputInstruction != drfs.outputInstruction) {
                    div1s.inputInstruction = drfs.outputInstruction;
                    mul1s.inputInstruction = null;
                    exs.inputInstruction = null;
                }
                else if ((drfs.outputInstruction.getOpCode() == Commons.I.MUL)
                        && mul1s.inputInstruction != drfs.outputInstruction) {
                    if ( ! mul1s.isStalled()) {
                        drfs.setMulStalled(false);
                        mul1s.inputInstruction = drfs.outputInstruction;
                    }
                    else
                        drfs.setMulStalled(true);
                    div1s.inputInstruction = null;
                    exs.inputInstruction = null;

                } else if (exs.inputInstruction != drfs.outputInstruction) {
                    if ( ! exs.isStalled()) {
                        drfs.setExStalled(false);
                        exs.inputInstruction = drfs.outputInstruction;
                    }
                    else
                        drfs.setExStalled(true);
                    mul1s.inputInstruction = null;
                    drfs.inputInstruction = null;
                }
            }
            else {
                // This will create problems if they're stalled!
                div1s.inputInstruction = null;
                if (! mul1s.isStalled())
                    mul1s.inputInstruction = null;
                if (! exs.isStalled())
                    exs.inputInstruction = null;
            }

            // DRF <-- F
            if (drfs.isExStalled())
                fs.setExStalled(true);
            else
                fs.setExStalled(false);

            if (drfs.isMulStalled())
                fs.setMulStalled(true);
            else
                fs.setMulStalled(false);

            if (! fs.isStalled() && ! fs.isExStalled() && ! fs.isMulStalled())
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

        if (div1s.inputInstruction != null)
            div1s.inputInstruction.setIsGonnaSetFlags(false);

        if (div2s.inputInstruction != null)
            div2s.inputInstruction.setIsGonnaSetFlags(false);

        if (div3s.inputInstruction != null)
            div3s.inputInstruction.setIsGonnaSetFlags(false);

        if (div4s.inputInstruction != null)
            div4s.inputInstruction.setIsGonnaSetFlags(false);

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
        System.out.println("DIV1 stage : " + div1s.getCurInstr() + " | " + div1s.getCurInstrString());
        System.out.println("DIV2 stage : " + div2s.getCurInstr() + " | " + div2s.getCurInstrString());
        System.out.println("DIV3 stage : " + div3s.getCurInstr() + " | " + div3s.getCurInstrString());
        System.out.println("DIV4 stage : " + div4s.getCurInstr() + " | " + div4s.getCurInstrString());
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
