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
}
