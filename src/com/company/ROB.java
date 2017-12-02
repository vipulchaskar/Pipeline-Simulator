package com.company;

import java.util.ArrayList;

public class ROB {

    private static ArrayList<ROBEntry> rob = new ArrayList<>();

        public static void SetupROB() {
            rob = new ArrayList<>();
        }

        public static boolean isROBFull() {
            return (rob.size() >= Commons.totalROBEntries);
        }

        public static int add(InstructionInfo newInstrn, int dest_arch_register, int dest_phy_register, int clockCycle) {

            ROBEntry newROBEntry = new ROBEntry();
            newROBEntry.setDest_arch_register(dest_arch_register);
            newROBEntry.setDest_phy_register(dest_phy_register);

            // Set the actual instruction to one that was passed
            newROBEntry.setIns(newInstrn);

            newROBEntry.setClockCycle(clockCycle);

            rob.add(newROBEntry);

            return rob.size()-1;
        }

        public static void commit(int commitNo) {

            if (rob.size() == 0)
                return;
            else if (! rob.get(0).isStatus()) {
                System.out.println("Instruction at head of ROB not ready.");
                return;
            }
            else {
                ROBEntry head = rob.get(0);

                // Check if the instruction produces a result
                if (Commons.generatesResult(head.getIns())) {
                    // The result is to be committed to architectural registers.

                    // Write result to architectural register
                    RegisterFile.WriteToRegister(head.getDest_arch_register(), head.getResult());
                    RegisterFile.SetRegisterZFlag(head.getDest_arch_register(),
                            PhysicalRegisterFile.GetZFlag(head.getIns().getdRegAddr()));

                    // Set the rename table entry to indicate that result is committed to architectural register
                    // Only if the physical register is the most recent instance of architectural register.
                    if (PhysicalRegisterFile.rename_table[head.getDest_arch_register()] == head.getDest_phy_register()) {
                        PhysicalRegisterFile.rename_table[head.getDest_arch_register()] = -1;
                        PhysicalRegisterFile.rename_table_bit[head.getDest_arch_register()] = false;
                    }

                    if (head.getClockCycle() == PhysicalRegisterFile.last_flag_producer_clock_cycle) {
                        // This was the last instruction that produced flags. Time to store the flags in arch registers.
                        PhysicalRegisterFile.psw_rename_table_bit = false;
                        PhysicalRegisterFile.psw_rename_table = head.getDest_arch_register();
                    }

                    // Free the physical register
                    PhysicalRegisterFile.SetAllocated(head.getDest_phy_register(), false);
                    PhysicalRegisterFile.SetRegisterStatus(head.getDest_phy_register(), false);

                }

                rob.remove(head);

                if (commitNo == 1) {
                    // This was the first instruction commitment. Try committing the following instruction too.
                    commit(2);
                }
            }
        }

        private static int GetInstructionIndexByClockCycle(int clockCycle) {

            for (int i = 0; i < rob.size(); i++) {
                if (rob.get(i).getClockCycle() == clockCycle)
                    return i;
            }

            System.out.println("Error! No instruction in ROB for the given clock cycle " + String.valueOf(clockCycle) + " found!");
            return -1;

        }

        public static int GetDispatchedClockCycleOfHead() {
            if (rob.size() == 0)
                return -1;
            return rob.get(0).getClockCycle();
        }

        public static void setStatus(int clockCycle, boolean status) {
            rob.get(GetInstructionIndexByClockCycle(clockCycle)).setStatus(status);
        }

        public static boolean getStatus(int clockCycle) {
            return rob.get(GetInstructionIndexByClockCycle(clockCycle)).isStatus();
        }

        public static void setResult(int clockCycle, int result) {
            rob.get(GetInstructionIndexByClockCycle(clockCycle)).setResult(result);
        }

        public static int getResult(int clockCycle) {
            return rob.get(GetInstructionIndexByClockCycle(clockCycle)).getResult();
        }

        public static void setClockCycle(int index, int newClockCycle) {
            rob.get(index).setClockCycle(newClockCycle);
        }

        public static int getClockCycle(int index) {
            return rob.get(index).getClockCycle();
        }

        public static String printCurrentInstructions() {

            StringBuilder outputString = new StringBuilder("[ ");

            for (ROBEntry instruction : rob) {
                outputString.append(instruction.getIns().getInsString() + ", ");
            }

            outputString.append(" ]");

            return outputString.toString();
        }

        public static void FlushInstructions(int branchClockCycle) {

            int startIndex = GetInstructionIndexByClockCycle(branchClockCycle);
            int remainingInstructions = rob.size() - startIndex;

            for (int i=0;i < remainingInstructions; i++) {
                System.out.println("Currently startIndex is " + String.valueOf(startIndex) + " and ROB size is " + String.valueOf(rob.size()));
                System.out.println("Looking to see if I can flush ROB entry " + String.valueOf(rob.get(startIndex).getIns().getInsString()));
                rob.remove(startIndex);
            }
        }

        public static void RemoveEntry(int dispatchedClockCycle) {

            int victimEntryIndex = GetInstructionIndexByClockCycle(dispatchedClockCycle);

            rob.remove(victimEntryIndex);   // Goodbye dear friend! :'(
        }

    }
