package com.company;

public class FStage {

    public InstructionInfo outputInstruction;
    public boolean stalled;
    private int nextInstAddress;

    public FStage() {
        nextInstAddress = 0;
        stalled = false;
    }

    public void setNextInstAddress(int newNextInstAddress) {
        // TODO: implement
        nextInstAddress = newNextInstAddress;
    }



    public void execute() {
        if (stalled) {
            //System.out.println("FStage is stalled. returning...");
            return;
        }

        //Fetch new instruction
        CodeLine cl = CodeMemory.getInstruction(nextInstAddress);
        //System.out.println("FStage fetched instruction " + cl.getInsString() + " from address " + cl.getAddress() + "!");

        InstructionInfo ii = new InstructionInfo(cl.getInsString(), cl.getAddress(), nextInstAddress+1);

        nextInstAddress++;

        outputInstruction = ii;

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
