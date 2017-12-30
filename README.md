# Pipeline-Simulator
Microprocessor instruction processing pipeline simulator (Developed as part of CS520)
This program simulates execution of an out-of-order, multi-FU processor pipeline for the given stream of (a subset of RISC-like) assembly instructions. The simulator has following features:
* Load-Store instructions to interact with memory
* Load-Store instruction queue to serialize access to memory with support for LOAD-Forwarding and LOAD-Bypassing
* Reorder buffer (ROB) for inorder writes to registers
* Physical registers and register renaming
* Issue Queue for waiting instructions
* Data forwarding from FUs and memory unit to Issue Queue and LSQ
* Support for conditional and unconditional branch instructions
* Support for flag-dependent instructions

=====
All the source code is placed under src/ directory.

To compile the java files, go to src/com/company and type:
	$ javac *.java

To run the program, run the following command from src/ directory:
	$ java com.company.Main <input_file>
e.g.
	$ java com.company.Main input.txt

inputs/ directory has sample input files. 

On running the program, first select "1. Initialize" from the menu to initialize data structures. Then select "2. Simulate" and enter the number of CPU clock cycles to simulate. It will print out cycle-by-cycle contents of all the stages of pipeline along with related information.
