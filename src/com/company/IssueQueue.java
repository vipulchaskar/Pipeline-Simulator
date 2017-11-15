package com.company;

import java.util.ArrayList;

public class IssueQueue {

    private static ArrayList<IQEntry> issueQueue = new ArrayList<>();

    public static void SetupIssueQueue() {
        issueQueue = new ArrayList<>();

    }

    public static boolean isIQFull() {
        return (issueQueue.size() >= Commons.totalIssueQueueEntries);
    }

    public static void add(InstructionInfo newInstrn) {

        IQEntry newIQEntry = new IQEntry();

        // Set the type of FU needed for this instruction
        if (newInstrn.getOpCode() == Commons.I.DIV || newInstrn.getOpCode() == Commons.I.HALT)
            newIQEntry.setFuType(Commons.FU.DIV);
        else if (newInstrn.getOpCode() == Commons.I.MUL)
            newIQEntry.setFuType(Commons.FU.MUL);
        else
            newIQEntry.setFuType(Commons.FU.INT);

        // Set the ready bit flags as appropriate
        newIQEntry.setSrc1Ready(newInstrn.isSrc1Forwarded());
        newIQEntry.setSrc2Ready(newInstrn.isSrc2Forwarded());

        // Set the actual instruction to one that was passed
        newIQEntry.setIns(newInstrn);

        issueQueue.add(newIQEntry);
    }

    public static InstructionInfo getNextInstruction(Commons.FU fuType) {

        // Return the first instruction which requires the "fuType" FU and has fetched all operands.
        for(IQEntry instruction : issueQueue) {
            if(instruction.getFuType() == fuType && instruction.isSrc1Ready() && instruction.isSrc2Ready()) {
                InstructionInfo ins = instruction.getIns();
                issueQueue.remove(instruction);
                return ins;
            }
        }

        return null;
    }

    public static void GetForwardedData(int registerAddress, int data) {

        for(IQEntry instruction : issueQueue) {
            if(! instruction.getIns().isRegistersFetched()) {

                if ((!instruction.isSrc1Ready()) && (instruction.getIns().getsReg1Addr() == registerAddress)) {
                    instruction.getIns().setsReg1Val(data);
                    instruction.getIns().setSrc1Forwarded(true);
                    instruction.setSrc1Ready(true);
                }

                if ((!instruction.isSrc2Ready()) && (instruction.getIns().getsReg2Addr() == registerAddress)) {
                    instruction.getIns().setsReg2Val(data);
                    instruction.getIns().setSrc2Forwarded(true);
                    instruction.setSrc2Ready(true);
                }

            }
        }
    }
}
