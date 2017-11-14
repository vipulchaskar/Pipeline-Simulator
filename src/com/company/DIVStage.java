package com.company;

public class DIVStage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;
    private boolean performsDivision;

    public DIVStage(boolean performsDivision) {
        stalled = false;
        this.performsDivision = performsDivision;
    }

    public DIVStage() {
        stalled = false;
        performsDivision = false;
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
                break;

            case DIV:
                if (performsDivision) {
                    if (inputInstruction.getsReg2Addr() != -1) {
                        inputInstruction.setIntermResult(inputInstruction.getsReg1Val() / inputInstruction.getsReg2Val());
                    } else {
                        inputInstruction.setIntermResult(inputInstruction.getsReg1Val() / inputInstruction.getLiteral());
                    }
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
                System.out.println("Error! Unknown instruction opcode found in DIV stage!");
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

}
