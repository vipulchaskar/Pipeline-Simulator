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

        public static int add(InstructionInfo newInstrn, int dest_arch_register, int dest_phy_register) {

            ROBEntry newROBEntry = new ROBEntry();
            newROBEntry.setDest_arch_register(dest_arch_register);
            newROBEntry.setDest_phy_register(dest_phy_register);

            // Set the actual instruction to one that was passed
            newROBEntry.setIns(newInstrn);

            rob.add(newROBEntry);

            return rob.size()-1;
        }

        public static void commit() {

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

                    // Free the physical register
                    PhysicalRegisterFile.SetAllocated(head.getDest_phy_register(), false);
                    PhysicalRegisterFile.SetRegisterStatus(head.getDest_phy_register(), false);

                }

                rob.remove(head);
            }
        }

        public static void setStatus(int index, boolean status) {
            rob.get(index).setStatus(status);
        }

        public static boolean getStatus(int index) {
            return rob.get(index).isStatus();
        }

        public static void setResult(int index, int result) {
            rob.get(index).setResult(result);
        }

        public static int getResult(int index) {
            return rob.get(index).getResult();
        }

        public static void setExcodes(int index, int excodes) {
            rob.get(index).setExcodes(excodes);
        }

        public static int getExcode(int index) {
            return rob.get(index).getExcodes();
        }

        public static String printCurrentInstructions() {

            StringBuilder outputString = new StringBuilder("[ ");

            for (ROBEntry instruction : rob) {
                outputString.append(instruction.getIns().getInsString());
            }

            outputString.append(" ]");

            return outputString.toString();
        }

    }
