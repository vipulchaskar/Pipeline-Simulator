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
            } else {
                System.out.println("Error! Unsupported opCode : " + parts[0] + " found!");
                return;
            }

            // Populate the decoded instruction operands
            switch (inputInstruction.getOpCode()) {
                case ADD:
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

                case SUB:
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

                case ADDC:
                    inputInstruction.setdRegAddr(getRegAddrFromInsPart(parts[1]));
                    inputInstruction.setsReg1Addr(getRegAddrFromInsPart(parts[2]));
                    inputInstruction.setLiteral(getLiteralFromLitPart(parts[3]));
                    inputInstruction.setDecoded(true);
                    break;

                case MUL:
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

                case OR:
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

                case XOR:
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

                default:
                    System.out.println("Error! Unknown instruction opcode found in DRF stage!");
                    break;
            }
        }

        if (! inputInstruction.isRegistersFetched()) {
            // Interlocking logic
            boolean DRegFree = (inputInstruction.getdRegAddr() == -1 || RegisterFile.GetRegisterStatus(inputInstruction.getdRegAddr()));
            boolean SReg1Free = (inputInstruction.getsReg1Addr() == -1 || RegisterFile.GetRegisterStatus(inputInstruction.getsReg1Addr()));
            boolean SReg2Free = (inputInstruction.getsReg2Addr() == -1 || RegisterFile.GetRegisterStatus(inputInstruction.getsReg2Addr()));
            boolean FlagsAvailable = (!inputInstruction.isFlagConsumer() || !Flags.getBusy());

            if (DRegFree && SReg1Free && SReg2Free && FlagsAvailable) {
                // Interlocking logic satisfied.
                //System.out.println("Interlocking logic satisfied.");
                stalled = false;

                // So, the instruction is ready to go ahead.Fetch the register values
                switch (inputInstruction.getOpCode()) {
                    case ADD:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        // Arithmetic instruction. Must also mark Flags as busy.
                        Flags.setBusy(true);
                        break;

                    case SUB:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        // Arithmetic instruction. Must also mark Flags as busy.
                        Flags.setBusy(true);
                        break;

                    case ADDC:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        break;

                    case MUL:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        // Arithmetic instruction. Must also mark Flags as busy.
                        Flags.setBusy(true);
                        break;

                    case DIV:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        // Arithmetic instruction. Must also mark Flags as busy.
                        Flags.setBusy(true);
                        break;


                    case LOAD:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        break;

                    case STORE:
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        break;

                    case MOVC:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        break;

                    case AND:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        break;

                    case OR:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        break;

                    case XOR:
                        RegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), false);
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        if (inputInstruction.getsReg2Addr() != -1) {
                            inputInstruction.setsReg2Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg2Addr()));
                        }
                        break;

                    case BZ:
                    case BNZ:
                        break;

                    case JUMP:
                        inputInstruction.setsReg1Val(RegisterFile.ReadFromRegister(inputInstruction.getsReg1Addr()));
                        break;

                    case HALT:
                        break;

                    case NOOP:
                        break;

                    default:
                        System.out.println("Error! Unknown instruction opcode found in DRF stage!");
                        break;
                }

                inputInstruction.setRegistersFetched(true);

                // All input operands fetched. Let's give this instruction to output latch.
                outputInstruction = inputInstruction;
            } else {
                // Interlocking logic not satisfied. Still waiting for registers to free.
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
        if (stalled || exStalled || mulStalled)
            return "Stalled";
        return "";
    }

}