package com.company;

public class Pipeline {

    private static boolean halted = false;
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
    }


    public static void Simulate(int clockCycles) {

        System.out.println("F   | DRF   | EX   | MEM   | WB    |");
        for (int i = 1; i <= clockCycles; i++) {
            if (halted) {
                System.out.println("Pipeline is halted!");
                return;
            }

            //System.out.println("Cycle " + String.valueOf(i) + " started...");

            wbs.execute();

            mems.execute();

            exs.execute();

            drfs.execute();

            fs.execute();

            System.out.println(fs.getCurInstr() + " | " + drfs.getCurInstr() + " | " + exs.getCurInstr() + " | " +
                    mems.getCurInstr() + " | " + wbs.getCurInstr() + " |");

            if (drfs.isStalled()) {
                fs.setStalled(true);
            }
            else {
                fs.setStalled(false);
            }

            if (! fs.isStalled())
                drfs.inputInstruction = fs.outputInstruction;
            exs.inputInstruction = drfs.outputInstruction;
            mems.inputInstruction = exs.outputInstruction;
            wbs.inputInstruction = mems.outputInstruction;


        }
    }
}
