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

        switch (inputInstruction.getOpCode()) {
            case ADD:
                if (inputInstruction.getIsGonnaSetFlags()) {
                    if (inputInstruction.getIntermResult() == 0) {
                        System.out.println("Zero flag set by ADD instruction!");
                        Flags.setZero(true);
                    }
                    Flags.setBusy(false);
                }
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case SUB:
                if (inputInstruction.getIsGonnaSetFlags()) {
                    if (inputInstruction.getIntermResult() == 0) {
                        Flags.setZero(true);
                        System.out.println("Zero flag set by SUB instruction!");
                    }
                    Flags.setBusy(false);
                }
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case ADDC:
                if (inputInstruction.getIsGonnaSetFlags()) {
                    if (inputInstruction.getIntermResult() == 0) {
                        Flags.setZero(true);
                        System.out.println("Zero flag set by ADDC instruction!");
                    }
                    Flags.setBusy(false);
                }
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case MUL:
                if (inputInstruction.getIsGonnaSetFlags()) {
                    if (inputInstruction.getIntermResult() == 0) {
                        Flags.setZero(true);
                        System.out.println("Zero flag set by MUL instruction!");
                    }
                    Flags.setBusy(false);
                }
                RegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case DIV:
                if (inputInstruction.getIsGonnaSetFlags()) {
                    if (inputInstruction.getIntermResult() == 0) {
                        Flags.setZero(true);
                        System.out.println("Zero flag set by DIV instruction!");
                    }
                    Flags.setBusy(false);
                }
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
                break;

            case NOOP:
                break;

            default:
                System.out.println("Error! Unknown instruction opcode found in WB stage!");
                break;
        }

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