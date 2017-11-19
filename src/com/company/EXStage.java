package com.company;

public class EXStage {

    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public EXStage() {
        stalled = false;
    }

    public void execute() {
        if (stalled && outputInstruction != null)
            return;

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
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetZFlag(inputInstruction.getdRegAddr(), (inputInstruction.getIntermResult() == 0));
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case SUB:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() - inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() - inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetZFlag(inputInstruction.getdRegAddr(), (inputInstruction.getIntermResult() == 0));
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case MUL:
            case DIV:
            case HALT:
            case NOOP:
                break;

            case LOAD:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                break;

            case STORE:
                inputInstruction.setIntermResult(inputInstruction.getsReg2Val() + inputInstruction.getLiteral());
                break;

            case MOVC:
                inputInstruction.setIntermResult(inputInstruction.getLiteral());
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case AND:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() & inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() & inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case OR:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() | inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() | inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case XOR:
                if (inputInstruction.getsReg2Addr() != -1) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() ^ inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() ^ inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
                break;

            case BZ:
                // Flags already forwarded & forwarded zero flag is true
                if ((inputInstruction.isFlagsForwarded() &&
                        inputInstruction.isForwardedZeroFlag())) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getPC());
                }

                // Latest value of flags is in a physical register & That physical register is not busy &
                // zero flag of that register is true.
                else if (PhysicalRegisterFile.psw_rename_table_bit &&
                        PhysicalRegisterFile.GetRegisterStatus(PhysicalRegisterFile.psw_rename_table) &&
                        PhysicalRegisterFile.GetZFlag(PhysicalRegisterFile.psw_rename_table)) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getPC());
                }

                // Latest value of flags is in architectural register and zero flag is set.
                else if (!Flags.getBusy() && Flags.getZero()) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getPC());
                }
                PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());
                break;

            case BNZ:
                // Flags already forwarded & forwarded zero flag is false
                if ((inputInstruction.isFlagsForwarded() &&
                        !inputInstruction.isForwardedZeroFlag())) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getPC());
                }

                // Latest value of flags is in a physical register & That physical register is not busy &
                // zero flag of that register is false.
                else if (PhysicalRegisterFile.psw_rename_table_bit &&
                        PhysicalRegisterFile.GetRegisterStatus(PhysicalRegisterFile.psw_rename_table) &&
                        !PhysicalRegisterFile.GetZFlag(PhysicalRegisterFile.psw_rename_table)) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getPC());
                }

                // Latest value of flags is in architectural register and zero flag is not set.
                else if (!Flags.getBusy() && !Flags.getZero()) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getPC());
                }
                PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());
                break;

            case JUMP:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                Pipeline.TakeBranch(inputInstruction.getIntermResult(), inputInstruction.getPC());
                PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());
                break;

            case JAL:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                Pipeline.TakeBranch(inputInstruction.getIntermResult(), inputInstruction.getPC());
                PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);
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

    public boolean isStalled() {
        return stalled;
    }

    public String getStalledStr() {
        if (stalled && inputInstruction != null)
            return "Stalled";
        return "";
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }



}