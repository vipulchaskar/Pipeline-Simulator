package com.company;

import java.nio.channels.Pipe;

public class FStage {

    public InstructionInfo outputInstruction;
    public boolean stalled;
    public boolean exStalled;
    private int nextInstAddress;

    public FStage() {
        nextInstAddress = 0;
        stalled = false;
        exStalled = false;
    }

    public void setNextInstAddress(int newNextInstAddress) {
        int temp = (newNextInstAddress - Commons.codeBaseAddress) / Commons.codeInstructionLength;
        nextInstAddress = temp;
    }



    public void execute() {
        if (stalled || exStalled || Pipeline.IsBranching() || Pipeline.isHalted()) {
            //System.out.println("FStage is stalled. returning...");
            return;
        }

        //Fetch new instruction
        CodeLine cl = CodeMemory.getInstruction(nextInstAddress);

        if (cl == null) {
            // Instructions finished.
            outputInstruction = null;
        }
        else {

            InstructionInfo ii = new InstructionInfo(cl.getInsString(), cl.getAddress(), nextInstAddress);

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
            return "";
        }
        return "(I" + String.valueOf(outputInstruction.getSequenceNo()) + ")";
    }

    public String getCurInstrString() {
        if (outputInstruction == null) {
            return "Empty";
        }
        return outputInstruction.getInsString();
    }

    public boolean isExStalled() {
        return exStalled;
    }

    public void setExStalled(boolean exStalled) {
        this.exStalled = exStalled;
    }

    public String getStalledStr() {
        if (stalled || exStalled)
            return "Stalled";
        return "";
    }



}
