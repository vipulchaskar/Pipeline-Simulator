package com.company;

public class WBStage {

    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public WBStage() {
        stalled = false;
    }

    public void execute() {
        if (stalled || inputInstruction == null)
            return;

        //Fetch new instruction
        //System.out.println("WB stage in execution, received instruction: " + inputInstruction.getInsString());

        switch (inputInstruction.getOpCode()) {
            case ADD:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case SUB:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case ADDC:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case MUL:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case LOAD:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case STORE:
                break;

            case MOVC:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getLiteral());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                //System.out.println("Register " + String.valueOf(inputInstruction.getdRegAddr()) + " set free.");
                break;

            case AND:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case OR:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;


            case XOR:
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;


            case BZ:
            case BNZ:
                break;

            case JUMP:
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

        //System.out.println("WB finished executing!");

    }

    public String getCurInstr() {
        if (inputInstruction == null) {
            return "-";
        }
        return "I" + String.valueOf(inputInstruction.getSequenceNo());
    }

}