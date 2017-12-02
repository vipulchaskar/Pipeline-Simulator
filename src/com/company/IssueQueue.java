package com.company;

import java.util.ArrayList;
import java.util.Iterator;

public class IssueQueue {

    private static ArrayList<IQEntry> issueQueue = new ArrayList<>();

    public static void SetupIssueQueue() {
        issueQueue = new ArrayList<>();

    }

    public static boolean isIQFull() {
        return (issueQueue.size() >= Commons.totalIssueQueueEntries);
    }

    public static void add(InstructionInfo newInstrn, int clockCycle) {

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

        // Record the clock Cycle when this instruction was dispatched.
        newIQEntry.setClockCycle(clockCycle);


        // Set the actual instruction to one that was passed
        newIQEntry.setIns(newInstrn);

        issueQueue.add(newIQEntry);
    }

    public static InstructionInfo getNextInstruction(Commons.FU fuType) {

        int earliestClockCycle = 100000;
        IQEntry earliestInstruction = null;

        // Return the first instruction which requires the "fuType" FU and has fetched all operands.
        for(IQEntry instruction : issueQueue) {

            System.out.println("Looking to see if I can send instruction " + instruction.getIns().getInsString()
            + " which has " + String.valueOf(instruction.isSrc1Ready()) + " & " + String.valueOf(instruction.isSrc2Ready()));

            if(instruction.getFuType() == fuType && instruction.isSrc1Ready() && instruction.isSrc2Ready()
                    && instruction.getClockCycle() < earliestClockCycle) {

                earliestClockCycle = instruction.getClockCycle();
                earliestInstruction = instruction;
            }
        }

        if (earliestInstruction != null) {
            InstructionInfo ins = earliestInstruction.getIns();
            issueQueue.remove(earliestInstruction);
            return ins;
        }

        return null;
    }

    public static void GetForwardedData(int registerAddress, int data) {

        System.out.println("IQ got forward for register " + String.valueOf(registerAddress) + " with data "
        + String.valueOf(data));

        for(IQEntry instruction : issueQueue) {
            if(! instruction.getIns().isRegistersFetched()) {

                if (Commons.isFlagConsumerInstruction(instruction.getIns()) &&
                        (!instruction.isSrc1Ready()) && (instruction.getIns().getsReg1Addr() == registerAddress)) {
                    instruction.getIns().setFlagsForwarded(true);
                    instruction.getIns().setForwardedZeroFlag(PhysicalRegisterFile.GetZFlag(registerAddress));
                    instruction.setSrc1Ready(true);
                    instruction.getIns().setSrc1Forwarded(true);
                }
                else if ((!instruction.isSrc1Ready()) && (instruction.getIns().getsReg1Addr() == registerAddress)) {
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

    public static void FlushInstructions(int CFIDtoFlush) {

        Iterator<IQEntry> iterator = issueQueue.iterator();

        while(iterator.hasNext()) {

            IQEntry instruction = iterator.next();
            if(instruction.getIns().getCFID() == CFIDtoFlush)
                iterator.remove();
        }
    }

    public static String printCurrentInstructions() {

        StringBuilder outputString = new StringBuilder("[ ");

        for (IQEntry instruction : issueQueue) {
            outputString.append(instruction.getIns().getInsString() + ", ");
        }

        outputString.append(" ]");

        return outputString.toString();
    }
}
