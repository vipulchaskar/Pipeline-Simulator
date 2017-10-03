package com.company;

public class EXStage {

    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public EXStage() {
        stalled = false;
    }

    public void execute() {
        if (stalled || inputInstruction == null) {
            //System.out.println("EX stage exiting...");
            outputInstruction = null;
            return;
        }

        //Fetch new instruction
        //System.out.println("EX stage in execution, received instruction: " + inputInstruction.getInsString());

        switch (inputInstruction.getOpCode()) {
            case ADD:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                }
                break;

            case SUB:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() - inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() - inputInstruction.getLiteral());
                }
                break;

            case ADDC:
                // TODO: Does ADDC mean ADD with carry?? :O :O :O
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                break;

            case MUL:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() * inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() * inputInstruction.getLiteral());
                }
                break;

            case DIV:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() / inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() / inputInstruction.getLiteral());
                }
                break;

            case LOAD:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                break;

            case STORE:
                inputInstruction.setIntermResult(inputInstruction.getsReg2Val() + inputInstruction.getLiteral());
                break;

            case MOVC:
                break;

            case AND:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() & inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() & inputInstruction.getLiteral());
                }
                break;

            case OR:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() | inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() | inputInstruction.getLiteral());
                }
                break;


            case XOR:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() ^ inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() ^ inputInstruction.getLiteral());
                }
                break;


            case BZ:
                if (Flags.getZero()) {
                    //System.out.println("Gonna take a branch to address : " + String.valueOf(
                    //        inputInstruction.getPC() + inputInstruction.getLiteral()));
                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral());
                    // Flags.setZero(false);
                }
                break;

            case BNZ:
                if ( ! Flags.getZero()) {
                    //System.out.println("Gonna take a branch to address : " + String.valueOf(
                    //        inputInstruction.getPC() + inputInstruction.getLiteral()));
                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral());
                    // Flags.setZero(false);
                }
                break;

            case JUMP:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                //System.out.println("Gonna take a branch to address : " + String.valueOf(inputInstruction.getIntermResult()));
                // TODO: Clarify this.
                Pipeline.TakeBranch(Commons.codeBaseAddress + inputInstruction.getIntermResult());
                break;

            case HALT:
                // TODO: Set the halt logic here.
                break;

            case NOOP:
                break;

            default:
                System.out.println("Error! Unknown instruction opcode found in DRF stage!");
                break;
        }

        //System.out.println("EX finished executing! Intermediate result is : " + inputInstruction.getIntermResult());

        // Let's give this instruction to output latch.
        outputInstruction = inputInstruction;

    }

    public String getCurInstr() {
        if (inputInstruction == null) {
            return "-";
        }
        return "I" + String.valueOf(inputInstruction.getSequenceNo());
    }

}