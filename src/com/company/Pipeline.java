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

        System.out.println(" F  | DRF|  EX|MUL1|MUL2| MEM|  WB|");
        for (int i = 1; i <= clockCycles; i++) {
            //if (halted) {
            //    System.out.println("Pipeline is halted!");
            //    return;
            //}

            //System.out.println("Cycle " + String.valueOf(i) + " started...");

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
                    + "|" + String.format("%1$4s", wbs.getCurInstr()) + "|");

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
                drfs.outputInstruction = null;
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
                mems.inputInstruction = exs.outputInstruction;
                exs.outputInstruction = null;
            } else {
                // Collision!
                if (mul2s.outputInstruction.getPC() < exs.outputInstruction.getPC()) {
                    mems.inputInstruction = mul2s.outputInstruction;
                    mul2s.outputInstruction = null;
                }
                else {
                    mems.inputInstruction = exs.outputInstruction;
                    exs.outputInstruction = null;
                }
            }

            // MUL2 <-- MUL1
            mul2s.inputInstruction = mul1s.outputInstruction;

            // MUL, EX <-- DRF
            // Splitting Logic
            if (drfs.outputInstruction != null) {

                if ((drfs.outputInstruction.getOpCode() == Commons.I.MUL
                        || drfs.outputInstruction.getOpCode() == Commons.I.DIV)
                        && mul1s.inputInstruction != drfs.outputInstruction) {

                    mul1s.inputInstruction = drfs.outputInstruction;
                    exs.inputInstruction = null;

                } else if (exs.inputInstruction != drfs.outputInstruction) {
                        exs.inputInstruction = drfs.outputInstruction;
                    mul1s.inputInstruction = null;
                }
            }
            else {
                // This will create problems if they're stalled!
                mul1s.inputInstruction = null;
                exs.inputInstruction = null;
            }

            // DRF <-- F
            if (! fs.isStalled())
                drfs.inputInstruction = fs.outputInstruction;
        }
    }
}
