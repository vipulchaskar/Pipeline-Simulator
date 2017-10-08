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
            if (inputInstruction == null)
                outputInstruction = null;
            return;
        }

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
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                break;

            case MUL:
                break;

            case DIV:
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
                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral());
                    // Flags.setZero(false);
                }
                break;

            case BNZ:
                if ( ! Flags.getZero()) {
                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral());
                    // Flags.setZero(false);
                }
                break;

            case JUMP:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                Pipeline.TakeBranch(inputInstruction.getIntermResult());
                break;

            case HALT:
                break;

            case NOOP:
                break;

            default:
                System.out.println("Error! Unknown instruction opcode found in EX stage!");
                break;
        }

        // Let's give this instruction to output latch.
        outputInstruction = inputInstruction;

    }

    public String getCurInstr() {
        if (inputInstruction == null) {
            return "-";
        }
        return "I" + String.valueOf(inputInstruction.getSequenceNo());
    }

    public String getCurInstrString() {
        if (inputInstruction == null) {
            return "-";
        }
        return inputInstruction.getInsString();
    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }


}