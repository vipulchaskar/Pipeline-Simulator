package com.company;

public class Commons {

    public static final int codeBaseAddress = 4000;
    public static final int codeInstructionLength = 4;
    public static final int dataBaseAddress = 0;
    public static final int dataAddressLength = 4;
    public static final int dataTotalLocations = 4000;
    public static final int totalRegisters = 16;
    public static final int totalPhysicalRegisters = 32;
    public static final int totalIssueQueueEntries = 16;
    public static final int totalROBEntries = 32;
    public static final int totalLSQEntries = 32;

    public enum I {
        ADD,
        SUB,
        ADDC,
        MUL,
        DIV,
        LOAD,
        STORE,
        MOVC,
        BZ,
        BNZ,
        JUMP,
        HALT,
        AND,
        OR,
        XOR,
        NOOP,
        JAL
    }

    public enum FU {
        DIV,
        MUL,
        INT
    }

    public enum MemType {
        LOAD,
        STORE
    }

    public static boolean generatesResult(InstructionInfo ins) {

        return (ins.getOpCode() == I.ADD ||
                ins.getOpCode() == I.SUB ||
                ins.getOpCode() == I.MUL ||
                ins.getOpCode() == I.DIV ||
                ins.getOpCode() == I.LOAD ||
                ins.getOpCode() == I.MOVC ||
                ins.getOpCode() == I.AND ||
                ins.getOpCode() == I.OR ||
                ins.getOpCode() == I.XOR ||
                ins.getOpCode() == I.JAL);
    }

    public static boolean isMemoryInstruction(InstructionInfo ins) {

        return (ins.getOpCode() == I.LOAD ||
                ins.getOpCode() == I.STORE);
    }

    public static boolean isBranchInstruction(InstructionInfo ins) {

        return (ins.getOpCode() == I.BZ ||
                ins.getOpCode() == I.BNZ ||
                ins.getOpCode() == I.JUMP ||
                ins.getOpCode() == I.JAL);
    }

}
