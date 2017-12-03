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
                if (! inputInstruction.isLiteralPresent()) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetZFlag(inputInstruction.getdRegAddr(), (inputInstruction.getIntermResult() == 0));
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case SUB:
                if (! inputInstruction.isLiteralPresent()) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() - inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() - inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetZFlag(inputInstruction.getdRegAddr(), (inputInstruction.getIntermResult() == 0));
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case MUL:
            case DIV:
            case HALT:
            case NOOP:
                break;

            case LOAD:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());

                LSQ.GetForwardedAddress(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                break;

            case STORE:
                inputInstruction.setIntermResult(inputInstruction.getsReg2Val() + inputInstruction.getLiteral());

                LSQ.GetForwardedAddress(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                break;

            case MOVC:
                inputInstruction.setIntermResult(inputInstruction.getLiteral());
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case AND:
                if (! inputInstruction.isLiteralPresent()) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() & inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() & inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case OR:
                if (! inputInstruction.isLiteralPresent()) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() | inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() | inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case XOR:
                if (! inputInstruction.isLiteralPresent()) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() ^ inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() ^ inputInstruction.getLiteral());
                }
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case BZ:
                // Flags already forwarded & forwarded zero flag is true
                if ((inputInstruction.isFlagsForwarded() &&
                        inputInstruction.isForwardedZeroFlag())) {

                    System.out.println("Branch is going to be taken!");
                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getDispatchedClockCycle(), inputInstruction.getCFID(), inputInstruction.getPC());

                    //PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                }

                /*// Latest value of flags is in a physical register & That physical register is not busy &
                // zero flag of that register is true.
                else if (PhysicalRegisterFile.psw_rename_table_bit &&
                        PhysicalRegisterFile.GetRegisterStatus(PhysicalRegisterFile.psw_rename_table) &&
                        PhysicalRegisterFile.GetZFlag(PhysicalRegisterFile.psw_rename_table)) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getDispatchedClockCycle(), inputInstruction.getCFID());
                    PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                }

                // Latest value of flags is in architectural register and zero flag is set.
                else if (!Flags.getBusy() && Flags.getZero()) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getDispatchedClockCycle(), inputInstruction.getCFID());
                    PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                }*/

                else {
                    // Branch not going to be taken.
                    System.out.println("Branch is not going to be taken!");
                    int currentCFID = inputInstruction.getCFID();
                    CFIDQueue.removeFromDispatchedCFID(currentCFID);
                    CFIDQueue.addToFreeCFID(currentCFID);
                    // TODO: Delete backup?
                }

                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case BNZ:
                // Flags already forwarded & forwarded zero flag is false
                if ((inputInstruction.isFlagsForwarded() &&
                        !inputInstruction.isForwardedZeroFlag())) {

                    System.out.println("Branch is going to be taken!");

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getDispatchedClockCycle(), inputInstruction.getCFID(), inputInstruction.getPC());
                    //PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                }

                /*// Latest value of flags is in a physical register & That physical register is not busy &
                // zero flag of that register is false.
                else if (PhysicalRegisterFile.psw_rename_table_bit &&
                        PhysicalRegisterFile.GetRegisterStatus(PhysicalRegisterFile.psw_rename_table) &&
                        !PhysicalRegisterFile.GetZFlag(PhysicalRegisterFile.psw_rename_table)) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getDispatchedClockCycle(), inputInstruction.getCFID());
                    PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                }

                // Latest value of flags is in architectural register and zero flag is not set.
                else if (!Flags.getBusy() && !Flags.getZero()) {

                    Pipeline.TakeBranch(inputInstruction.getPC() + inputInstruction.getLiteral(),
                            inputInstruction.getDispatchedClockCycle(), inputInstruction.getCFID());
                    PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                }*/

                else {
                    // Branch not going to be taken.
                    System.out.println("Branch is not going to be taken!");
                    int currentCFID = inputInstruction.getCFID();
                    CFIDQueue.removeFromDispatchedCFID(currentCFID);
                    CFIDQueue.addToFreeCFID(currentCFID);
                    // TODO: Delete backup?
                }

                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case JUMP:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                Pipeline.TakeBranch(inputInstruction.getIntermResult(), inputInstruction.getDispatchedClockCycle(),
                        inputInstruction.getCFID(), inputInstruction.getPC());
                //PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());

                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case JAL:
                inputInstruction.setIntermResult(inputInstruction.getsReg1Val() + inputInstruction.getLiteral());
                Pipeline.TakeBranch(inputInstruction.getIntermResult(), inputInstruction.getDispatchedClockCycle(),
                        inputInstruction.getCFID(), inputInstruction.getPC());
                //PhysicalRegisterFile.restoreBackup(inputInstruction.getPC());
                PhysicalRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getPC() + Commons.codeInstructionLength);
                PhysicalRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getPC() + Commons.codeInstructionLength);
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
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