package com.company;

public class MEMStage {

    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public MEMStage() {
        stalled = false;
    }

    public void execute() {
        if (stalled || inputInstruction == null) {
            outputInstruction = null;
            return;
        }

        //Fetch new instruction
        //System.out.println("MEM stage in execution, received instruction: " + inputInstruction.getInsString());

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
                // TODO: Lol, will this even work? :D
                inputInstruction.setIntermResult(DataMemory.readFromMemory(inputInstruction.getIntermResult()));
                break;

            case STORE:
                DataMemory.writeToMemory(inputInstruction.getsReg1Val(), inputInstruction.getIntermResult());
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

        //System.out.println("MEM finished executing! Instruction it got was : " + inputInstruction.getInsString());

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