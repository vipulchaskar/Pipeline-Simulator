package com.company;

import com.company.Commons.*;

public class DRFStage {

    public InstructionInfo inputInstruction;
    public InstructionInfo outputInstruction;

    public boolean stalled;
    public boolean exStalled;
    public boolean mulStalled;

    private InstructionInfo addToROBIns;
    private int addToROBdestArchReg;
    private int addToROBdestPhyReg;
    private int addToROBclockCycle;

    private InstructionInfo addToLSQ;
    private int addToLSQclockCycle;

    public DRFStage() {
        stalled = false;
        exStalled = false;
        mulStalled = false;
    }


    public void execute(int clockCycle) {
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
                        inputInstruction.setLiteralPresent(true);
                    } else {
                        inputInstruction.setsReg2Addr(getRegAddrFromInsPart(parts[3]));
                        inputInstruction.setLiteralPresent(false);
                    }
                    // This is an arithmetic instruction. Evaporate the capability of instructions already in pipeline
                    // to set the flags.
                    // Thou shalt set no more flags :D
                    // Pipeline.RemoveFlagSettingCapability();
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
                        inputInstruction.setLiteralPresent(true);
                    } else {
                        inputInstruction.setsReg2Addr(getRegAddrFromInsPart(parts[3]));
                        inputInstruction.setLiteralPresent(false);
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

        StallingLogic(clockCycle);

    }

    public void StallingLogic(int clockCycle) {
        if (! inputInstruction.isRegistersFetched() && inputInstruction != null) {
            // Stalling logic

            // Either the instruction does not require a physical register, or if it does, a physical register is available.
            boolean freePhyRegAvailable = (! Commons.generatesResult(inputInstruction) ||
                    PhysicalRegisterFile.FreePhysicalRegisterAvailable());

            // Issue Queue is not full, there is space.
            boolean freeIQEntryAvailable = ! IssueQueue.isIQFull();

            // Free ROB slot available.
            boolean freeROBSlotAvailable = ! ROB.isROBFull();

            // Either the instruction is not a memory instruction, or if it is, a slot in LSQ is available.
            boolean freeLSQSlotAvailable = (!Commons.isMemoryInstruction(inputInstruction) || !LSQ.isLSQFull());

            // Either the instruction is not a branch instruction, or if it is, a free CFID label is available.
            boolean freeCFIDAvailable = (!Commons.isBranchInstruction(inputInstruction) || CFIDQueue.isFreeCFIDAvailable());


            if (freePhyRegAvailable && freeIQEntryAvailable && freeROBSlotAvailable &&
                    freeLSQSlotAvailable && freeCFIDAvailable) {

                // Stalling logic satisfied.
                stalled = false;

                // So, the instruction is ready to go ahead. Fetch the register values, if we can.
                switch (inputInstruction.getOpCode()) {

                    case ADD:
                    case SUB:
                    case MUL:
                    case DIV:
                        int src1PhyRegAddr;
                        int src2PhyRegAddr = -1;
                        // Step a.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];
                        if (inputInstruction.getsReg2Addr() != -1) {
                            src2PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg2Addr()];
                        }

                        // Step b. part 1.
                        // Assign the physical register for destination
                        int newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        int destArchRegAddr = inputInstruction.getdRegAddr();

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
                        if (inputInstruction.isLiteralPresent()) {
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

                        // Step b. Part 2.
                        // ... and update the rename table
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;


                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);
                        inputInstruction.setsReg2Addr(src2PhyRegAddr);

                        // Step e taken care of by first case in step c.

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.

                        inputInstruction.setCFID(CFIDQueue.lastCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, destArchRegAddr, newPhyRegAddr, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = destArchRegAddr;
                        addToROBdestPhyReg = newPhyRegAddr;
                        addToROBclockCycle = clockCycle;

                        // Arithmetic instruction. Must also make a rename table entry for flags.
                        // Latest value of flags will be found in a physical register (true)
                        PhysicalRegisterFile.psw_rename_table_bit = true;
                        // This is where latest value of flags will be found in case of physical register.
                        PhysicalRegisterFile.psw_rename_table = newPhyRegAddr;
                        // Store this instruction as the last flag producer instruction.
                        PhysicalRegisterFile.last_flag_producer_clock_cycle = clockCycle;

                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case AND:
                    case OR:
                    case XOR:
                        src2PhyRegAddr = -1;        // Things you do to make compiler happy
                        // Step a.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];
                        if (inputInstruction.getsReg2Addr() != -1) {
                            src2PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg2Addr()];
                        }

                        // Step b. Part 1.
                        // Assign the physical register for destination
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        destArchRegAddr = inputInstruction.getdRegAddr();

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
                        if (inputInstruction.isLiteralPresent()) {
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

                        // Step b. Part 2.
                        // and update the rename table
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);
                        inputInstruction.setsReg2Addr(src2PhyRegAddr);

                        // Step e taken care of by first case in step c.

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.

                        inputInstruction.setCFID(CFIDQueue.lastCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, destArchRegAddr, newPhyRegAddr, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = destArchRegAddr;
                        addToROBdestPhyReg = newPhyRegAddr;
                        addToROBclockCycle = clockCycle;

                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case LOAD:
                        // Step a.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];

                        // Step b. Part 1
                        // Assign the physical register for destination
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        destArchRegAddr = inputInstruction.getdRegAddr();

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

                        // Step b. Part 2
                        // and update the rename table
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);

                        // Step e.
                        inputInstruction.setSrc2Forwarded(true);

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.

                        inputInstruction.setCFID(CFIDQueue.lastCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, destArchRegAddr, newPhyRegAddr, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = destArchRegAddr;
                        addToROBdestPhyReg = newPhyRegAddr;
                        addToROBclockCycle = clockCycle;

                        // Create an LSQ entry.
                        //LSQ.add(inputInstruction, clockCycle);
                        addToLSQ = inputInstruction;
                        addToLSQclockCycle = clockCycle;

                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case STORE:
                        // Step a.
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

                        inputInstruction.setCFID(CFIDQueue.lastCFID);

                        // Create an ROB entry.
                        // STORE instructions don't have destination registers, so it doesn't matter what we enter as
                        // destination arch and physical registers.
                        //ROB.add(inputInstruction, -1, -1, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = -1;
                        addToROBdestPhyReg = -1;
                        addToROBclockCycle = clockCycle;


                        // Create an LSQ entry.
                        //LSQ.add(inputInstruction, clockCycle);
                        addToLSQ = inputInstruction;
                        addToLSQclockCycle = clockCycle;

                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case MOVC:
                        // Step a.
                        // Look up physical register address for source registers - N.A.
                        // Assign the physical register for destination and update the rename table
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        destArchRegAddr = inputInstruction.getdRegAddr();
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

                        inputInstruction.setCFID(CFIDQueue.lastCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, destArchRegAddr, newPhyRegAddr, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = destArchRegAddr;
                        addToROBdestPhyReg = newPhyRegAddr;
                        addToROBclockCycle = clockCycle;


                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case BZ:
                    case BNZ:
                        /* For branch instructions, src1 will act as flags source.
                           3 cases-
                           Flag value available in a physical register and that register is available.
                           Flag value in a physical register but that register is unavailable.
                           Flag value in an architectural register.
                         */
                        if (PhysicalRegisterFile.psw_rename_table_bit &&
                                PhysicalRegisterFile.GetRegisterStatus(PhysicalRegisterFile.psw_rename_table)) {
                            inputInstruction.setForwardedZeroFlag(PhysicalRegisterFile.GetZFlag(PhysicalRegisterFile.psw_rename_table));
                            inputInstruction.setFlagsForwarded(true);
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        else if (PhysicalRegisterFile.psw_rename_table_bit &&
                                !PhysicalRegisterFile.GetRegisterStatus(PhysicalRegisterFile.psw_rename_table)) {
                            inputInstruction.setsReg1Addr(PhysicalRegisterFile.psw_rename_table);
                            inputInstruction.setSrc1Forwarded(false);
                        }
                        else if (!PhysicalRegisterFile.psw_rename_table_bit) {
                            inputInstruction.setForwardedZeroFlag(RegisterFile.GetRegisterZFlag(PhysicalRegisterFile.psw_rename_table));
                            inputInstruction.setFlagsForwarded(true);
                            inputInstruction.setSrc1Forwarded(true);
                        }
                        inputInstruction.setSrc2Forwarded(true);

                        inputInstruction.setCFID(CFIDQueue.lastCFID);
                        int newCFID = CFIDQueue.getFreeCFID();
                        CFIDQueue.lastCFID = newCFID;
                        CFIDQueue.addToDispatchedCFID(newCFID);

                        PhysicalRegisterFile.takeBackup(inputInstruction.getPC());

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, -1, -1, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = -1;
                        addToROBdestPhyReg = -1;
                        addToROBclockCycle = clockCycle;

                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case HALT:
                    case NOOP:
                        inputInstruction.setSrc1Forwarded(true);
                        inputInstruction.setSrc2Forwarded(true);

                        inputInstruction.setCFID(CFIDQueue.lastCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, -1, -1, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = -1;
                        addToROBdestPhyReg = -1;
                        addToROBclockCycle = clockCycle;

                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case JUMP:
                        // Step a.
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

                        // Take backup of the physical registers and rename table for branch instructions
                        PhysicalRegisterFile.takeBackup(inputInstruction.getPC());

                        inputInstruction.setCFID(CFIDQueue.lastCFID);
                        newCFID = CFIDQueue.getFreeCFID();
                        CFIDQueue.lastCFID = newCFID;
                        CFIDQueue.addToDispatchedCFID(newCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, -1, -1, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = -1;
                        addToROBdestPhyReg = -1;
                        addToROBclockCycle = clockCycle;


                        inputInstruction.setDispatchedClockCycle(clockCycle);
                        break;

                    case JAL:
                        // Step a.
                        // Look up physical register address for source registers
                        src1PhyRegAddr = PhysicalRegisterFile.rename_table[inputInstruction.getsReg1Addr()];

                        // Step b. Part 1
                        // Assign the physical register for destination
                        newPhyRegAddr = PhysicalRegisterFile.GetNewPhysicalRegister();
                        destArchRegAddr = inputInstruction.getdRegAddr();

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

                        // Step b. Part 2
                        // ...  and update the rename table
                        PhysicalRegisterFile.rename_table[inputInstruction.getdRegAddr()] = newPhyRegAddr;
                        PhysicalRegisterFile.rename_table_bit[inputInstruction.getdRegAddr()] = true;

                        // Step d.
                        // Set the source register address field to the physical address of the source register.
                        inputInstruction.setsReg1Addr(src1PhyRegAddr);

                        // Step e.
                        inputInstruction.setSrc2Forwarded(true);

                        // Step f.
                        // Set the destination register field to the one allocated in step b.
                        inputInstruction.setdRegAddr(newPhyRegAddr);

                        // Step h. Taken care of by IssueQueue.add() method.

                        // Take backup of the physical registers and rename table for branch instructions
                        PhysicalRegisterFile.takeBackup(inputInstruction.getPC());

                        inputInstruction.setCFID(CFIDQueue.lastCFID);
                        newCFID = CFIDQueue.getFreeCFID();
                        CFIDQueue.lastCFID = newCFID;
                        CFIDQueue.addToDispatchedCFID(newCFID);

                        // Create an ROB entry.
                        //ROB.add(inputInstruction, destArchRegAddr, newPhyRegAddr, clockCycle);
                        addToROBIns = inputInstruction;
                        addToROBdestArchReg = destArchRegAddr;
                        addToROBdestPhyReg = newPhyRegAddr;
                        addToROBclockCycle = clockCycle;


                        inputInstruction.setDispatchedClockCycle(clockCycle);
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
                stalled = true;
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

    public void addToROB() {
        if (addToROBIns != null) {
            ROB.add(addToROBIns, addToROBdestArchReg, addToROBdestPhyReg, addToROBclockCycle);

            addToROBIns = null;
            addToROBdestArchReg = -1;
            addToROBdestPhyReg = -1;
            addToROBclockCycle = -1;

        }
    }

    public void addToLSQ() {
        if (addToLSQ != null) {
            LSQ.add(addToLSQ, addToLSQclockCycle);
            addToLSQ = null;
            addToLSQclockCycle = -1;
        }
    }

}