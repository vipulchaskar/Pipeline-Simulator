package com.company;

import com.company.Commons.*;

public class DRFStage {

    public InstructionInfo inputInstruction;
    public InstructionInfo outputInstruction;
    private FStage fs;

    public boolean stalled;
    public boolean exStalled;
    public boolean mulStalled;

    public DRFStage(FStage fsref) {
        stalled = false;
        exStalled = false;
        mulStalled = false;
        fs = fsref;
    }


    public void execute() {
        if (inputInstruction == null || Pipeline.IsBranching() || Pipeline.isHalted()) {
            inputInstruction = null;
            outputInstruction = null;
            return;
        }

        if (! inputInstruction.isDecoded()) {
            // Split the instruction
            char splitDelimeter;
            if (inputInstruction.getInsString().contains(" "))
                splitDelimeter = ' ';
            else
                splitDelimeter = ',';

            String[] parts = inputInstruction.getInsString().split(String.valueOf(splitDelimeter));
            // Remove commas, if needed
            if (splitDelimeter == ' ')
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].replaceAll(",", "");
                }

            // Populate the decoded instruction opcode
            if (parts[0].equals("ADD")) {
                inputInstruction.setOpCode(I.ADD);
            } else if (parts[0].equals("SUB")) {
                inputInstruction.setOpCode(I.SUB);
            } else if (parts[0].equals("ADDC")) {
                inputInstruction.setOpCode(I.ADDC);
            } else if (parts[0].equals("MUL")) {
                inputInstruction.setOpCode(I.MUL);
            } else if (parts[0].equals("DIV")) {
                inputInstruction.setOpCode(I.DIV);
            } else if (parts[0].equals("LOAD")) {
                inputInstruction.setOpCode(I.LOAD);
            } else if (parts[0].equals("STORE")) {
                inputInstruction.setOpCode(I.STORE);
            } else if (parts[0].equals("MOVC")) {
                inputInstruction.setOpCode(I.MOVC);
            } else if (parts[0].equals("BZ")) {
                inputInstruction.setOpCode(I.BZ);
            } else if (parts[0].equals("BNZ")) {
                inputInstruction.setOpCode(I.BNZ);
            } else if (parts[0].equals("JUMP")) {
                inputInstruction.setOpCode(I.JUMP);
            } else if (parts[0].equals("HALT")) {
                inputInstruction.setOpCode(I.HALT);
            } else if (parts[0].equals("AND")) {
                inputInstruction.setOpCode(I.AND);
            } else if (parts[0].equals("OR")) {
                inputInstruction.setOpCode(I.OR);
            } else if (parts[0].equals("XOR") || parts[0].equals("EXOR")) {
                inputInstruction.setOpCode(I.XOR);
            } else if (parts[0].equals("NOOP")) {
                inputInstruction.setOpCode(I.NOOP);
            } else if (parts[0].equals("JAL")) {
                inputInstruction.setOpCode(I.JAL);
            } else {
                System.out.println("Error! Unsupported opCode : " + parts[0] + " found!");
                return;
            }

            // Populate the decoded instruction operands
            switch (inputInstruction.getOpCode()) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                    inputInstruction.setdRegAddr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[2]));
                    if (parts[3].charAt(0) == '#') {
                        inputInstruction.setLiteral(getLiteralFromLitPart(parts[3]));
                    } else {
                        inputInstruction.setsReg2Addr(getRegAddrFromInsPart(parts[3]));
                    }
                    // This is an arithmetic instruction. Evaporate the capability of instructions already in pipeline
                    // to set the flags.
                    // Thou shalt set no more flags :D
                    Pipeline.RemoveFlagSettingCapability();
                    inputInstruction.setIsGonnaSetFlags(true);
                    inputInstruction.setDecoded(true);
                    break;

                case LOAD:
                    inputInstruction.setdRegAddr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[2]));
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[3]));
                    inputInstruction.setDecoded(true);
                    break;

                case STORE:
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setsReg2Addr(getRegAddrFromInsPart(parts[2]));
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[3]));
                    inputInstruction.setDecoded(true);
                    break;

                case MOVC:
                    inputInstruction.setdRegAddr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[2]));
                    inputInstruction.setDecoded(true);
                    break;

                case AND:
                case OR:
                case XOR:
                    inputInstruction.setdRegAddr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[2]));
                    if (parts[3].charAt(0) == '#') {
                        inputInstruction.setLiteral(getLiteralFromLitPart(parts[3]));
                    } else {
                        inputInstruction.setsReg2Addr(getRegAddrFromInsPart(parts[3]));
                    }
                    inputInstruction.setDecoded(true);
                    break;

                case BZ:
                case BNZ:
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[1]));
                    inputInstruction.setFlagConsumer(true);
                    inputInstruction.setDecoded(true);
                    break;

                case JUMP:
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[2]));
                    inputInstruction.setDecoded(true);
                    break;

                case HALT:
                    Pipeline.setHalted(true);
                    inputInstruction.setDecoded(true);
                    break;

                case NOOP:
                    inputInstruction.setDecoded(true);
                    break;

                case JAL:
                    inputInstruction.setdRegAddr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[2]));
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[3]));
                    inputInstruction.setDecoded(true);
                    break;

                default:
                    System.out.println("Error! Unknown instruction opcode found in DRF stage!");
                    break;
            }
        }

        StallingLogic();

    }

    public void StallingLogic() {
        if (! inputInstruction.isRegistersFetched() && inputInstruction != null) {
            // Stalling logic
            boolean freePhyRegAvailable = PhysicalRegisterFile.FreePhysicalRegisterAvailable();
            boolean freeIQEntryAvailable = ! IssueQueue.isIQFull();

            /*// Interlocking logic
            boolean DRegFree = (inputInstruction.getdRegAddr() == -1
                    || RegisterFile.GetRegisterStatus(inputInstruction.getdRegAddr()));

            boolean SReg1Free = (inputInstruction.getsReg1Addr() == -1
                    || RegisterFile.GetRegisterStatus(inputInstruction.getsReg1Addr())
                    || inputInstruction.isSrc1Forwarded());

            boolean SReg2Free = (inputInstruction.getsReg2Addr() == -1
                    || RegisterFile.GetRegisterStatus(inputInstruction.getsReg2Addr())
                    || inputInstruction.isSrc2Forwarded());

            boolean FlagsAvailable = (!inputInstruction.isFlagConsumer()
                    || !Flags.getBusy()
                    || inputInstruction.isFlagsForwarded());*/

            if (freePhyRegAvailable && freeIQEntryAvailable) {
            //if (DRegFree && SReg1Free && SReg2Free && FlagsAvailable) {

                // Stalling logic satisfied.
                //System.out.println("Stalling logic satisfied.");
                stalled = false;

                // So, the instruction is ready to go ahead. Fetch the register values, if we can.
                switch (inputInstruction.getOpCode()) {
                    //int src1PhyReg
                    case ADD:
                    case SUB:
                    case MUL:
                    case DIV:
                    case AND:
                    case OR:
                    case XOR:
                        // Step b.
                        int src1PhyRegAddr,src2PhyRegAddr = -1;

                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];
                        if (inputInstruction.getsReg2Addr() != -1) {
                            src2PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg2Addr()];
                        }

                        // Step b.
                        // Assign the physical register for destination and update the rename table
                        int newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step c.
                        // Read out the value of source 1
                        if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg1Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src1PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg1Val(PhysicalRegisterFile.ReadFromRegister(src1PhyRegAddr));
                            inputInstruction.setSrc1Forwarded(true);
                        }

                        // Step c.
                        // Read out the value of source 2
                        if (inputInstruction.getsReg2Addr() == -1) {
                            // Source 2 is a literal (This case takes care of step e)
                            inputInstruction.setSrc2Forwarded(true);
                        }
                        else if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg2Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                            inputInstruction.setSrc2Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src2PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg2Val(PhysicalRegisterFile.ReadFromRegister(src2PhyRegAddr));
                            inputInstruction.setSrc2Forwarded(true);
                        }

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);
                        inputInstruction.setsReg2Addr(src2PhyRegAddr);

                        // Step e taken care of by first case in step c.

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.

                        // Arithmetic instruction. Must also mark Flags as busy.
                        //Flags.setBusy(true);
                        break;

                    case LOAD:
                        // Step b.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];

                        // Step b.
                        // Assign the physical register for destination and update the rename table
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step c.
                        // Read out the value of source 1
                        if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg1Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src1PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg1Val(PhysicalRegisterFile.ReadFromRegister(src1PhyRegAddr));
                            inputInstruction.setSrc1Forwarded(true);
                        }

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);

                        // Step e.
                        inputInstruction.setSrc2Forwarded(true);

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.
                        break;

                    case STORE:
                        // Step b.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];
                        src2PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg2Addr()];

                        // Step b.
                        // Assign the physical register for destination and update the rename table - N.A.

                        // Step c.
                        // Read out the value of source 1
                        if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg1Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src1PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg1Val(PhysicalRegisterFile.ReadFromRegister(src1PhyRegAddr));
                            inputInstruction.setSrc1Forwarded(true);
                        }

                        // Step c.
                        // Read out the value of source 2
                        if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg2Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                            inputInstruction.setSrc2Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src2PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg2Val(PhysicalRegisterFile.ReadFromRegister(src2PhyRegAddr));
                            inputInstruction.setSrc2Forwarded(true);
                        }

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);
                        inputInstruction.setsReg2Addr(src2PhyRegAddr);

                        // Step e taken care of by first case in step c.

                        // Step f.
                        // Set the destination register field to the one allocated in step b. - N.A.

                        // Step h. Taken care of by IssueQueue.add() method.

                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        break;

                    case MOVC:
                        // Step b.
                        // Look up physical register address for source registers - N.A.
                        // Assign the physical register for destination and update the rename table
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step c. (Including step e)
                        // Read out the value of sources - N.A.
                        inputInstruction.setSrc1Forwarded(true);
                        inputInstruction.setSrc2Forwarded(true);

                        // Step d.
                        // Set the source register address field to the physical address of the source register. - N.A.
                        // Step e taken care of by first case in step c.

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.
                        break;

                    case BZ:
                    case BNZ:
                    case HALT:
                    case NOOP:
                        inputInstruction.setSrc1Forwarded(true);
                        inputInstruction.setSrc2Forwarded(true);
                        break;

                    case JUMP:
                        // Step b.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];

                        // Step b.
                        // Assign the physical register for destination and update the rename table - N.A.

                        // Step c.
                        // Read out the value of source 1
                        if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg1Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src1PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg1Val(PhysicalRegisterFile.ReadFromRegister(src1PhyRegAddr));
                            inputInstruction.setSrc1Forwarded(true);
                        }

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);

                        // Step e.
                        inputInstruction.setSrc2Forwarded(true);

                        // Step f.
                        // Set the destination register field to the one allocated in step b. - N. A.

                        // Step h. Taken care of by IssueQueue.add() method.
                        break;

                    case JAL:
                        // Step b.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];

                        // Step b.
                        // Assign the physical register for destination and update the rename table
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step c.
                        // Read out the value of source 1
                        if (! PhysicalRegisterFile.rename_table_bit[inputInstruction.getsReg1Addr()]) {
                            // Latest value for this source is in arch. register
                            inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.GetRegisterStatus(src1PhyRegAddr)) {
                            // Latest value for this source is in physical register AND IT IS VALID.
                            inputInstruction.setsReg1Val(PhysicalRegisterFile.ReadFromRegister(src1PhyRegAddr));
                            inputInstruction.setSrc1Forwarded(true);
                        }

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);

                        // Step e.
                        inputInstruction.setSrc2Forwarded(true);

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.
                        break;

                    default:
                        System.out.println("Error! Unknown instruction opcode found in DRF stage!");
                        break;
                }

                //inputInstruction.setRegistersFetched(true);

                // All input operands fetched. Let's give this instruction to output latch.
                outputInstruction = inputInstruction;
            } else {
                // Interlocking logic not satisfied. Still waiting for IQ or physical registers to get free.
                //System.out.println("Interlocking logic not satisfied for instruction " + inputInstruction.getInsString());
                stalled = true;
                //System.out.println(String.valueOf(DRegFree) + " " + String.valueOf(SReg1Free) + " " + String.valueOf(SReg2Free) +
                //        " " + String.valueOf(FlagsAvailable));
                outputInstruction = null;
            }
        }
    }

    private int getRegAddrFromInsPart(String part) {
        part = part.replaceAll("R", "");
        return Integer.parseInt(part);
    }

    private int getLiteralFromLitPart(String part) {
        part = part.replaceAll("#", "");
        return Integer.parseInt(part);
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

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public boolean isExStalled() {
        return exStalled;
    }

    public void setExStalled(boolean exStalled) {
        this.exStalled = exStalled;
    }

    public boolean isMulStalled() { return mulStalled; }

    public void setMulStalled(boolean mulStalled) { this.mulStalled = mulStalled; }

    public String getStalledStr() {
        if ((stalled || exStalled || mulStalled) && inputInstruction != null)
            return "Stalled";
        return "";
    }

}