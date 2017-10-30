package com.company;

public class MUL1Stage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public MUL1Stage() {
        stalled = false;
    }

    public void execute() {
        if (stalled || inputInstruction == null) {
            outputInstruction = null;
            return;
        }

        switch (inputInstruction.getOpCode()) {
            case ADD:
                break;

            case SUB:
                break;

            case ADDC:
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
                break;

            case STORE:
                break;

            case MOVC:
                break;

            case AND:
                break;

            case OR:
                break;

            case XOR:
                break;

            case BZ:
                break;

            case BNZ:
                break;

            case JUMP:
                break;

            case HALT:
                break;

            case NOOP:
                break;

            default:
                System.out.println("Error! Unknown instruction opcode found in MUL1 stage!");
                break;
        }

        // Let's give this instruction to output latch.
        outputInstruction = inputInstruction;

    }

    public String getCurInstr() {
        if (inputInstruction == null) {
            return "";
        }
        return "(I" + String.valueOf(inputInstruction.getSequenceNo()) + ")";
    }

    public String getCurInstrString() {
        if (inputInstruction == null) {
            return "Empty";
        }
        return inputInstruction.getInsString();
    }

    public String getStalledStr() {
        if (stalled)
            return "Stalled";
        return "";
    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

}
