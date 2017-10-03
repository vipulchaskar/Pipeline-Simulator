package com.company;

public class Pipeline {

    private static boolean halted = false;
    private static boolean branch = false;
    private static int targetAddress;
    private static FStage fs;
    private static DRFStage drfs;
    private static EXStage exs;
    private static MEMStage mems;
    private static WBStage wbs;

    public static void Setup() {
        fs = new FStage();
        drfs = new DRFStage(fs);
        exs = new EXStage();
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

        System.out.println(" F  | DRF|  EX| MEM|  WB|");
        for (int i = 1; i <= clockCycles; i++) {
            //if (halted) {
            //    System.out.println("Pipeline is halted!");
            //    return;
            //}

            //System.out.println("Cycle " + String.valueOf(i) + " started...");

            wbs.execute();

            mems.execute();

            exs.execute();

            drfs.execute();

            fs.execute();

            System.out.println(String.format("%1$4s", fs.getCurInstr()) + "|" + String.format("%1$4s", drfs.getCurInstr())
                    + "|" + String.format("%1$4s", exs.getCurInstr()) + "|" + String.format("%1$4s", mems.getCurInstr())
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

            if (! fs.isStalled())
                drfs.inputInstruction = fs.outputInstruction;
            exs.inputInstruction = drfs.outputInstruction;
            mems.inputInstruction = exs.outputInstruction;
            wbs.inputInstruction = mems.outputInstruction;


        }
    }
}
