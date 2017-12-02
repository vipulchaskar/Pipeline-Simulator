package com.company;

public class DIVStage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;
    private boolean performsDivision;
    private boolean writesResult;

    public DIVStage(boolean performsDivision, boolean writesResult) {
        stalled = false;
        this.performsDivision = performsDivision;
        this.writesResult = writesResult;
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
            case SUB:
            case MUL:
            case LOAD:
            case STORE:
            case MOVC:
            case AND:
            case OR:
            case XOR:
            case BZ:
            case BNZ:
            case JUMP:
            case NOOP:
                break;

            case HALT:
                if (writesResult)
                    ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case DIV:
                if (performsDivision) {
                    if (! inputInstruction.isLiteralPresent()) {
                        inputInstruction.setIntermResult(inputInstruction.getsReg1Val() / inputInstruction.getsReg2Val());
                    } else {
                        inputInstruction.setIntermResult(inputInstruction.getsReg1Val() / inputInstruction.getLiteral());
                    }
                }
                if (writesResult) {
                    PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                    PhysicalRegisterFile.SetZFlag(inputInstruction.getdRegAddr(), (inputInstruction.getIntermResult() == 0));
                    PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                    ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                    ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                }
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
