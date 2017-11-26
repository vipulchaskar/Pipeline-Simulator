package com.company;

import java.util.ArrayList;

public class LSQ {

    private static ArrayList<LSQEntry> lsq = new ArrayList<>();

    public static void SetupLoadStoreQueue() {
        lsq = new ArrayList<>();
    }

    public static boolean isLSQFull() {
        return (lsq.size() >= Commons.totalLSQEntries);
    }

    public static int add(InstructionInfo newInstrn, int clockCycle) {

        LSQEntry newLSQEntry = new LSQEntry(newInstrn, clockCycle);

        lsq.add(newLSQEntry);

        return lsq.size()-1;
    }

    public static InstructionInfo getNextInstruction() {

        if (lsq.size() == 0)
            return null;

        LSQEntry earliestInstruction = lsq.get(0);

        // Return the first instruction if it has its address computed.

        System.out.println("Looking to see if I can send instruction " + earliestInstruction.getIns().getInsString()
                + " which has addr:" + String.valueOf(earliestInstruction.getAddress()) + " & " +
                String.valueOf(earliestInstruction.isAddressReady()));

        // Address should be ready AND (Either instruction should be LOAD, OR, (if it is STORE) source value should be available.
        if(earliestInstruction.isAddressReady() &&
                (earliestInstruction.getMemType() == Commons.MemType.LOAD || earliestInstruction.isSrcReady())) {

            // Copy the generated address into the InstructionInfo object to be sent to MEM stage.
            earliestInstruction.getIns().setIntermResult(earliestInstruction.getAddress());

            // If the instruction type is STORE, copy the value to be STOREd as well.
            if (earliestInstruction.getMemType() == Commons.MemType.STORE)
                earliestInstruction.getIns().setsReg1Val(earliestInstruction.getValue());

            lsq.remove(0);

            return earliestInstruction.getIns();
        }

        // Instruction at head of LSQ is not ready
        return null;
    }

    public static void GetForwardedData(int registerAddress, int data) {

        System.out.println("IQ got forward for register " + String.valueOf(registerAddress) + " with data "
                + String.valueOf(data));

        for(LSQEntry instruction : lsq) {

            if ((!instruction.isSrcReady()) && (instruction.getSrcPhyRegAddr() == registerAddress)) {
                    instruction.getIns().setsReg1Val(data);
                    instruction.getIns().setSrc1Forwarded(true);
                    instruction.setSrcReady(true);
            }
        }
    }

    private static int GetIndexByClockCycle(int clockCycle) {

        for (int i = 0; i < lsq.size(); i++) {
            if (lsq.get(i).getClockCycle() == clockCycle)
                return i;
        }

        System.out.println("Error! No instruction in LSQ for the given clock cycle " + String.valueOf(clockCycle) + " found!");
        return -1;
    }

    public static void GetForwardedAddress(int clockCycle, int address) {

        int lsqIndex = GetIndexByClockCycle(clockCycle);

        lsq.get(lsqIndex).setAddress(address);
        lsq.get(lsqIndex).setAddressReady(true);

        // LOAD Bypassing.
        if (lsq.get(lsqIndex).getMemType() == Commons.MemType.LOAD) {

            LSQEntry bypasserLoad = lsq.get(lsqIndex);

            boolean canBypass = true;
            boolean storesFound = false;

            // For all instructions ahead of LOAD,
            for (int i = lsqIndex-1; i >= 0; i--) {

                // If that instruction is STORE,
                if ((lsq.get(i).getMemType() == Commons.MemType.STORE)) {

                    storesFound = true;

                    // Do not bypass if the STORE doesn't have address ready or the addresses are matching.
                    if (!lsq.get(i).isAddressReady() || lsq.get(i).getAddress() == bypasserLoad.getAddress()) {

                        canBypass = false;
                        break;
                    }
                }
            }

            if (storesFound && canBypass) {
                // Found one or more STOREs to bypass

                lsq.remove(lsqIndex);
                lsq.add(0, bypasserLoad);
            }
        }

        // TODO: Load forwarding.
    }

    public static void FlushInstructions(int CFIDtoFlush) {

        for (LSQEntry instruction : lsq) {
            if (instruction.getIns().getCFID() == CFIDtoFlush) {
                lsq.remove(instruction);
            }
        }
    }

    public static String printCurrentInstructions() {

        StringBuilder outputString = new StringBuilder("[ ");

        for (LSQEntry instruction : lsq) {
            outputString.append(instruction.getIns().getInsString());
        }

        outputString.append(" ]");

        return outputString.toString();
    }

}
