package com.company;

public class Commons {

    public static final int codeBaseAddress = 4000;
    public static final int codeInstructionLength = 4;
    public static final int dataBaseAddress = 0;
    public static final int dataTotalLocations = 4000;
    public static final int totalRegisters = 16;

    public enum I {
        //TODO: Add DIV instruction?
        ADD,
        SUB,
        ADDC,
        MUL,
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
        NOOP
    }
}
