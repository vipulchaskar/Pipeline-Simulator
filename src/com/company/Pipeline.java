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

            DataForwarding();

            // If DRF stage is stalled, Fetch is stalled too.
            if (drfs.isStalled()) {
                fs.setStalled(true);
            }
            else {
                fs.setStalled(false);
            }

            // In case of branching, flush the instructions in DRF and F stage and start fetching from target address
            if (branch) {
                drfs.outputInstruction = null;
                fs.outputInstruction = null;
                fs.setNextInstAddress(targetAddress);
                branch = false;
            }

            // If pipeline is halted, stop fetching.
            if (halted) {
                fs.outputInstruction = null;
            }

            // Logic for sending instructions from one stage to the next

            // WB <-- MEM
            wbs.inputInstruction = mems.outputInstruction;

            // MEM <-- MUL, EX, DIV
            // Selection Logic
            if (div4s.outputInstruction == null) {
                // No instruction at DIV4, check between MUL2 and INTFU

                if (mul2s.outputInstruction == null && exs.outputInstruction == null) {
                    // Both MUL2 and INTFU have no instructions
                    mems.inputInstruction = null;

                } else if (mul2s.outputInstruction != null && exs.outputInstruction == null) {
                    // There is instruction at MUL2
                    mul2s.setStalled(false);
                    mul1s.setStalled(false);
                    mems.inputInstruction = mul2s.outputInstruction;
                    mul2s.outputInstruction = null;

                } else if (mul2s.outputInstruction == null && exs.outputInstruction != null) {
                    // There is instruction at INTFU
                    exs.setStalled(false);
                    mems.inputInstruction = exs.outputInstruction;
                    exs.outputInstruction = null;

                } else {
                    // Both MUL2 and INTFU have instructions. Give preference to MUL instructions
                    mul1s.setStalled(false);
                    mul2s.setStalled(false);
                    mems.inputInstruction = mul2s.outputInstruction;
                    mul2s.outputInstruction = null;
                    exs.setStalled(true);
                }
            }
            else {
                // There is instruction at DIV4. Give preference to this DIV4 instruction.
                mems.inputInstruction = div4s.outputInstruction;
                div4s.outputInstruction = null;

                if (mul2s.outputInstruction != null)
                    mul2s.setStalled(true);
                if (mul1s.outputInstruction != null)
                    mul1s.setStalled(true);
                if (exs.outputInstruction != null)
                    exs.setStalled(true);
            }

            // MUL2 <-- MUL1
            if (! mul2s.isStalled()) {
                // Multiply units are not stalled. Instructions can be given from MUL1 to MUL2.
                mul2s.inputInstruction = mul1s.outputInstruction;
                mul1s.setStalled(false);
            }

            // DIV x+1 <-- DIV x
            div4s.inputInstruction = div3s.outputInstruction;
            div3s.inputInstruction = div2s.outputInstruction;
            div2s.inputInstruction = div1s.outputInstruction;

            // DIV, MUL, EX <-- DRF
            // Splitting Logic
            if (drfs.outputInstruction != null) {

                if ((drfs.outputInstruction.getOpCode() == Commons.I.DIV
                        || drfs.outputInstruction.getOpCode() == Commons.I.HALT)
                        && div1s.inputInstruction != drfs.outputInstruction) {
                    // This is a DIV or HALT instruction in DRF stage.
                    div1s.inputInstruction = drfs.outputInstruction;
                    if (! mul1s.isStalled())
                        mul1s.inputInstruction = null;
                    if (! exs.isStalled())
                        exs.inputInstruction = null;
                }

                else if ((drfs.outputInstruction.getOpCode() == Commons.I.MUL)
                        && mul1s.inputInstruction != drfs.outputInstruction) {
                    // This is a MUL instruction in DRF stage.
                    if ( ! mul1s.isStalled()) {
                        drfs.setMulStalled(false);
                        mul1s.inputInstruction = drfs.outputInstruction;
                    }
                    else {
                        drfs.setMulStalled(true);
                    }
                    if (! exs.isStalled())
                        exs.inputInstruction = null;
                    div1s.inputInstruction = null;
                }

                else if (exs.inputInstruction != drfs.outputInstruction) {
                    // All other instructions
                    if ( ! exs.isStalled()) {
                        drfs.setExStalled(false);
                        exs.inputInstruction = drfs.outputInstruction;
                    }
                    else
                        drfs.setExStalled(true);
                    if (! mul1s.isStalled())
                        mul1s.inputInstruction = null;
                    div1s.inputInstruction = null;
                }
            }
            else {
                // No instruction at output of DRF
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

            if (! fs.isStalled() && ! fs.isExStalled() && ! fs.isMulStalled()) {
                drfs.inputInstruction = fs.outputInstruction;
                fs.outputInstruction = null;
            }
        }
    }

    public static void DataForwarding() {
        int src1;
        int src2;
        boolean zeroFlag;

        if (drfs.inputInstruction == null || branch)
            return;

        src1 = drfs.inputInstruction.getsReg1Addr();
        src2 = drfs.inputInstruction.getsReg2Addr();

        // Do forwarding only if the instruction in DRF has not fetched all registers
        if (! drfs.inputInstruction.isRegistersFetched()) {

            // Forwarding from EX to DRF
            if (exs.outputInstruction != null && exs.outputInstruction.getOpCode() != Commons.I.LOAD) {
                // Forward the registers
                int exd = exs.outputInstruction.getdRegAddr();
                if (exd != -1) {
                    if (exd == src1) {
                        //System.out.println("Forwarding " + String.valueOf(exs.outputInstruction.getIntermResult()) + "from EX to src1 of DRF!");
                        drfs.inputInstruction.setsReg1Val(exs.outputInstruction.getIntermResult());
                        drfs.inputInstruction.setSrc1Forwarded(true);
                    }

                    if (exd == src2) {
                        //System.out.println("Forwarding " + String.valueOf(exs.outputInstruction.getIntermResult()) + "from EX to src2 of DRF!");
                        drfs.inputInstruction.setsReg2Val(exs.outputInstruction.getIntermResult());
                        drfs.inputInstruction.setSrc2Forwarded(true);
                    }
                }

                // Forward the flags
                if (exs.outputInstruction.getIsGonnaSetFlags() && drfs.inputInstruction.isFlagConsumer()) {
                    zeroFlag = (exs.outputInstruction.getIntermResult() == 0);
                    //System.out.println("Forwarding " + String.valueOf(zeroFlag) + " zero flag value from EX to DRF!");
                    drfs.inputInstruction.setForwardedZeroFlag(zeroFlag);
                    drfs.inputInstruction.setFlagsForwarded(true);
                }
            }

            // Forwarding from MUL2 to DRF
            if (mul2s.outputInstruction != null) {
                // Forward the registers
                int mul2d = mul2s.outputInstruction.getdRegAddr();
                if (mul2d != -1) {
                    if (mul2d == src1) {
                        //System.out.println("Forwarding " + String.valueOf(mul2s.outputInstruction.getIntermResult()) + "from MUL to src1 of DRF!");
                        drfs.inputInstruction.setsReg1Val(mul2s.outputInstruction.getIntermResult());
                        drfs.inputInstruction.setSrc1Forwarded(true);
                    }

                    if (mul2d == src2) {
                        //System.out.println("Forwarding " + String.valueOf(mul2s.outputInstruction.getIntermResult()) + "from MUL to src2 of DRF!");
                        drfs.inputInstruction.setsReg2Val(mul2s.outputInstruction.getIntermResult());
                        drfs.inputInstruction.setSrc2Forwarded(true);
                    }
                }

                // Forward the flags
                if (mul2s.outputInstruction.getIsGonnaSetFlags() && drfs.inputInstruction.isFlagConsumer()) {
                    zeroFlag = (mul2s.outputInstruction.getIntermResult() == 0);
                    //System.out.println("Forwarding " + String.valueOf(zeroFlag) + " zero flag value from MUL2 to DRF!");
                    drfs.inputInstruction.setForwardedZeroFlag(zeroFlag);
                    drfs.inputInstruction.setFlagsForwarded(true);
                }
            }

            // Forwarding from DIV4 to DRF
            if (div4s.outputInstruction != null) {
                // Forward the registers
                int div4d = div4s.outputInstruction.getdRegAddr();
                if (div4d != -1) {
                    if (div4d == src1) {
                        //System.out.println("Forwarding " + String.valueOf(div4s.outputInstruction.getIntermResult()) + " from DIV to src1 of DRF!");
                        drfs.inputInstruction.setsReg1Val(div4s.outputInstruction.getIntermResult());
                        drfs.inputInstruction.setSrc1Forwarded(true);
                    }

                    if (div4d == src2) {
                        //System.out.println("Forwarding " + String.valueOf(div4s.outputInstruction.getIntermResult()) + " from DIV to src2 of DRF!");
                        drfs.inputInstruction.setsReg2Val(div4s.outputInstruction.getIntermResult());
                        drfs.inputInstruction.setSrc2Forwarded(true);
                    }
                }

                // Forward the flags
                if (div4s.outputInstruction.getIsGonnaSetFlags() && drfs.inputInstruction.isFlagConsumer()) {
                    zeroFlag = (div4s.outputInstruction.getIntermResult() == 0);
                    //System.out.println("Forwarding " + String.valueOf(zeroFlag) + " zero flag value from DIV to DRF!");
                    drfs.inputInstruction.setForwardedZeroFlag(zeroFlag);
                    drfs.inputInstruction.setFlagsForwarded(true);
                }
            }

            // Trigger the interlocking logic to see if instruction can be moved ahead after forwarding
            drfs.InterLockingLogic();
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
