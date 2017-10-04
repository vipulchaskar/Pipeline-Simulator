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
            //System.out.println("MUL2 stage exiting...");
            outputInstruction = null;
            return;
        }

        //Fetch new instruction
        //System.out.println("MUL2 stage in execution, received instruction: " + inputInstruction.getInsString());

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
                System.out.println("Error! Unknown instruction opcode found in DRF stage!");
                break;
        }

        //System.out.println("MUL2 finished executing! Intermediate result is : " + inputInstruction.getIntermResult());

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
