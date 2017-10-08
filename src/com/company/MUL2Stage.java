package com.company;

public class MUL2Stage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public MUL2Stage() {
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
                break;

            case DIV:
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
                System.out.println("Error! Unknown instruction opcode found in MUL2 stage!");
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
}
