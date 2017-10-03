package com.company;

import java.nio.channels.Pipe;

public class FStage {

    public InstructionInfo outputInstruction;
    public boolean stalled;
    private int nextInstAddress;

    public FStage() {
        nextInstAddress = 0;
        stalled = false;
    }

    public void setNextInstAddress(int newNextInstAddress) {
        int temp = (newNextInstAddress - Commons.codeBaseAddress) / Commons.codeInstructionLength;
        //System.out.println("Setting " + String.valueOf(temp) + " as new address in Fetch stage.");
        nextInstAddress = temp;
    }



    public void execute() {
        if (stalled || Pipeline.IsBranching() || Pipeline.isHalted()) {
            //System.out.println("FStage is stalled. returning...");
            return;
        }

        //Fetch new instruction
        CodeLine cl = CodeMemory.getInstruction(nextInstAddress);
        //System.out.println("FStage fetched instruction " + cl.getInsString() + " from address " + cl.getAddress() + "!");

        if (cl == null) {
            // Instructions finished.
            outputInstruction = null;
        }
        else {

            InstructionInfo ii = new InstructionInfo(cl.getInsString(), cl.getAddress(), nextInstAddress + 1);

            nextInstAddress++;

            outputInstruction = ii;
        }

    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public String getCurInstr() {
        if (outputInstruction == null) {
            return "-";
        }
        return "I" + String.valueOf(outputInstruction.getSequenceNo());
    }

}
