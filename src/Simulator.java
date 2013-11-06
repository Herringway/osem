import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simulates an operating system's process management subsystem.
 * @author Cameron
 *
 */
public class Simulator {
	private final int instructionsPerSlice = 3; //Number of instructions to be executed per CPU time slice
	private final int maxReadyQueue = 5; //Maximum size of ready queue
	private final int maxSimultaneousProcesses = 100; //Maximum number of processes
	private PImage[] processList; //a list of processes.
	private int processListCounter; //The number of processes that have been created thus far
	private PImage readyQueueEnd; //End of the ready queue
	private PImage readyQueueBegin; //Beginning of the ready queue
	private PImage newQueueEnd;   //End of the new queue
	private PImage newQueueBegin; //Beginning of the new queue
	private int currentPID; //PID of the currently executing process
	public Simulator() {
		readyQueueEnd = readyQueueBegin = newQueueEnd = newQueueBegin = null;
		currentPID = -1;
		processList = new PImage[maxSimultaneousProcesses];
		processListCounter = 0;
	}
	/**
	 * Pushes a process into the new queue.
	 * @param in the process to be added.
	 */
	public void pushNew(PImage in) {
		in.setState(0);
		if (newQueueBegin != null)
			newQueueBegin.setNextImage(in);
		if (newQueueEnd == null)
			newQueueEnd = in;
		newQueueBegin = in;
	}
	/**
	 * Pushes a process into the ready queue.
	 * @param in the process to be added.
	 */
	public void pushReady(PImage in) {
		in.setState(1);
		if (readyQueueBegin != null)
			readyQueueBegin.setNextImage(in);
		if (readyQueueEnd == null)
			readyQueueEnd = in;
		readyQueueBegin = in;
	}
	/**
	 * Pops a process from the new queue.
	 * @return the process popped.
	 */
	public PImage popNew() {
	    PImage temp = newQueueEnd;
	    if (newQueueEnd.getNextImage() == null)
	    	newQueueEnd = newQueueBegin = null;
	    else
	    	newQueueEnd = newQueueEnd.getNextImage();
	    temp.setNextImage(null);
		return temp;
	}
	/**
	 * Pops a process from the ready queue.
	 * @return the process popped.
	 */
	public PImage popReady() {
	    PImage temp = readyQueueEnd;
	    if (readyQueueEnd.getNextImage() == null)
	    	readyQueueEnd = readyQueueBegin = null;
	    else
	    	readyQueueEnd = readyQueueEnd.getNextImage();
	    temp.setNextImage(null);
		return temp;
	}
	/**
	 * Creates a new process and adds it to the new queue.
	 * @param userID an identifier representing a user.
	 * @param procPriority the priority of the process.
	 * @param maxCPU maximum number of CPU cycles available for this process.
	 * @param filename a path to a binary to load.
	 * @return true if process creation successful
	 */
	public boolean newProcess(int userID, int procPriority, int maxCPU, String filename) {
		if (processListCounter == maxSimultaneousProcesses)
			return false;
		PImage newImage = new PImage();
		newImage.setName(filename);
		try {
			FileInputStream execImage = new FileInputStream(filename);
			int[] code = new int[newImage.codeSize];
			int i = 0;
			int bytebuf = 0;
			while ((bytebuf = execImage.read()) != -1) {
				if (i > newImage.codeSize)
					break;
				code[i++] = bytebuf;
			}
			newImage.setCode(code);
			execImage.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		newImage.setMaxCycles(maxCPU);
		newImage.setPriority(procPriority);
		newImage.setUID(userID);
		newImage.setPID(processListCounter);
		processList[processListCounter++] = newImage;
		pushNew(newImage);
		return true;
	}
	/**
	 * Gets the status of a particular non-terminated process.
	 * @param proc process to retrieve statistics for.
	 * @return statistics in pretty string format.
	 */
	private String getStatus(PImage proc) {
		String output = "";
		output += String.format("PID = %d\n", proc.getPID());
		output += String.format("userID = %d\n", proc.getUID());
		output += String.format("state = %s\n", proc.getState());
		output += String.format("priority = %s\n", proc.getPriority());
		output += String.format("max CPU cycles = %d\n", proc.getMaxCycles());
		output += String.format("CPU cycles used = %d\n", proc.getExecutedCycles());
		output += String.format("filename = %s\n", proc.getName());
		output += String.format("PC = %d\n", proc.getProgramCounter());
		output += String.format("AC = %d\n", proc.getAccumulator());
		int i = 0;
		for(int instruction : proc.getCode()) {
			output += String.format("%02X ", instruction);
			if (++i%8 == 0)
				output += "\n";
		}
		
		output += "\n";
		return output;
	}
	/**
	 * Prints out the status of all non-terminated processes.
	 */
	public void printStatus() {
		String output = " PID   userID  state  priority  max CPU  CPU used  filename\n" +
						"-----  ------  -----  --------  -------  --------  --------\n";
		for (int i = 0; i < processListCounter; i++) {
			if (processList[i] == null)
				continue;
			output += String.format("% 5d  ", processList[i].getPID());
			output += String.format("% 6d  ", processList[i].getUID());
			output += String.format("%5s  ", processList[i].getState());
			output += String.format("%8s  ", processList[i].getPriority());
			output += String.format("% 7d  ", processList[i].getMaxCycles());
			output += String.format("% 8d  ", processList[i].getExecutedCycles());
			output += String.format("%8s", processList[i].getName());
			output += "\n";
		}
		System.out.println(output);
	}
	/**
	 * Prints out the status of a single process
	 * @param PID process to print status for.
	 * @return true if process exists.
	 */
	public boolean printStatus(int PID) {
		if (PID >= processListCounter)
			return false;
		if (processList[PID] == null)
			return false;
		System.out.println(getStatus(processList[PID]));
		return true;
	}
	/**
	 * Kills a process.
	 * @param PID identifier for the process
	 * @return true if the process could be killed, false otherwise
	 */
	public boolean killProcess(int PID) {
		if (PID > processList.length)
			return false;
		if (processList[PID] == null)
			return false;
		writeCoreDump(processList[PID], "Process killed");
		terminateProcess(PID);
		return true;
	}
	/**
	 * Executes a single instruction.
	 * @return true if the process did not halt.
	 */
	public boolean execOneInstruction() {
		PImage currentProcess = processList[currentPID];
		int currentInstruction = currentProcess.getCode()[currentProcess.getProgramCounter()];
		currentProcess.setProgramCounter(currentProcess.getProgramCounter()+1);
		currentProcess.setExecutedCycles(currentProcess.getExecutedCycles()+1);
		switch ((currentInstruction&0b11100000)>>5) {
			case 0b000: //STOP 
				//System.out.println("STOP");
				return false;
			case 0b001: //LOAD
				//System.out.println(String.format("LOAD %02X from %02X", currentProcess.getAccumulator(), currentInstruction&0b00011111));
				currentProcess.setAccumulator(currentProcess.getCode()[currentInstruction&0b00011111]);
				break;
			case 0b010: //STORE
				//System.out.println(String.format("STORE %02X to %02X", currentProcess.getAccumulator(), currentInstruction&0b00011111));
				currentProcess.getCode()[currentInstruction&0b00011111] = currentProcess.getAccumulator();
				break;
			case 0b011: //ADD
				//System.out.println(String.format("ADD %02X to %02X", currentProcess.getCode()[currentInstruction&0b00011111], currentProcess.getAccumulator()));
				int sum = (currentProcess.getAccumulator() + currentProcess.getCode()[currentInstruction&0b00011111]);
				if (sum > 0xFF)
					currentProcess.setPSW(currentProcess.getPSW()|0b100);
				if (sum < 0)
					currentProcess.setPSW(currentProcess.getPSW()|0b010);
				if (sum == 0)
					currentProcess.setPSW(currentProcess.getPSW()|0b001);
				currentProcess.setAccumulator(sum&0xFF);
				break;
			case 0b100: //SUBTRACT
				//System.out.println(String.format("SUB %02X from %02X", currentProcess.getCode()[currentInstruction&0b00011111], currentProcess.getAccumulator()));
				int difference = currentProcess.getAccumulator() - currentProcess.getCode()[currentInstruction&0b00011111];
				if (difference > 0xFF)
					currentProcess.setPSW(currentProcess.getPSW()|0b100);
				if (difference < 0)
					currentProcess.setPSW(currentProcess.getPSW()|0b010);
				if (difference == 0)
					currentProcess.setPSW(currentProcess.getPSW()|0b001);
				currentProcess.setAccumulator(difference&0xFF);
				break;
			case 0b101: //BRANCH (if positive)
				//System.out.println(String.format("BRA-P TO %02X", currentInstruction&0b00011111));
				if ((currentProcess.getPSW()&0b011) == 0b000)
					currentProcess.setProgramCounter(currentInstruction&0b00011111);
				break;
			case 0b110: //BRANCH (if negative)
				//System.out.println(String.format("BRA-N TO %02X", currentInstruction&0b00011111));
				if ((currentProcess.getPSW()&0b011) == 0b010)
					currentProcess.setProgramCounter(currentInstruction&0b00011111);
				break;
			case 0b111: //BRANCH (if zero)
				//System.out.println(String.format("BRA-Z TO %02X", currentInstruction&0b00011111));
				if ((currentProcess.getPSW()&0b011) == 0b001)
					currentProcess.setProgramCounter(currentInstruction&0b00011111);
				break;
		}
		//System.out.println(String.format("%08b", currentInstruction));
		return true;
	}
	/**
	 * Determines if there are any processes left that can execute.
	 * @return true if no processes left
	 */
	public boolean isAllTerminated() {
		if (currentPID != -1) {
			return false;
		}
		if ((readyQueueBegin != null) || (readyQueueEnd != null)) {
			return false;
		}
		if ((newQueueBegin != null) || (newQueueEnd != null)) {
			return false;
		}
		return true;
	}
	/**
	 * Executes a single CPU time slice. If there are any processes waiting in the new queue
	 * and there is space available, one will be added to the ready queue. If a process is in
	 * the ready queue after that, it will execute until the instructions per slice is exhausted,
	 * until a STOP instruction is executed, or until its cpu cycle limit is reached, whichever comes
	 * first. 
	 */
	public void executeSlice() {
		if (!readyQueueIsFull() && (newQueueEnd != null)) {
			PImage temp = popNew();
			while (temp.isDead() && (newQueueEnd != null)) //skip over dead processes.
				temp = popNew();
			if (temp != null)
				pushReady(temp);
		}
		if (currentPID == -1) {
			if (readyQueueEnd == null)
				return;
			PImage temp = popReady();
			while (temp.isDead() && (readyQueueEnd != null)) //skip over dead processes.
				temp = popReady();
			if (temp.isDead() && readyQueueEnd == null)
				return;
			if (temp != null)
				setRunning(temp);
		}
		for (int i = 0; i < instructionsPerSlice; i++) {
			if (!execOneInstruction()) { //execution complete
				writeCoreDump(processList[currentPID], "Process completed normally");
				terminateProcess(currentPID);
				break;
			}
			if (processList[currentPID].getExecutedCycles() > processList[currentPID].getMaxCycles()) { //you've had too many cycles
				writeCoreDump(processList[currentPID], "Process reached CPU time limit");
				terminateProcess(currentPID);
				break;
			}
		}
		if (currentPID != -1)
			pushReady(processList[currentPID]);
		if (readyQueueEnd != null)
			setRunning(popReady());
	}
	/**
	 * Marks process as currently-running process.
	 * @param proc process to mark
	 */
	private void setRunning(PImage proc) {
		currentPID = proc.getPID();
		proc.setState(2);
	}
	/**
	 * Determines if there is space available in the ready queue.
	 * @return true if space is full.
	 */
	private boolean readyQueueIsFull() {
		PImage curProc = readyQueueEnd;
		for (int i = 0; i < maxReadyQueue; i++) {
			if (curProc == null)
				return false;
			curProc = curProc.getNextImage();
		}
		return true;
	}
	/**
	 * Internal function for terminating a process.
	 * @param PID identifier for process to kill.
	 */
	private void terminateProcess(int PID) {
		if (PID == currentPID) {
			currentPID = -1;
		}
		processList[PID].kill();
		processList[PID] = null;
	}
	/**
	 * Writes a core dump to a file with specified message.
	 * @param image Process to write core dump for.
	 * @param message string to append to the end of the core dump.
	 */
	private void writeCoreDump(PImage image, String message) {
		try {
			FileWriter writer = new FileWriter(image.getName() + ".core");
			writer.append(getStatus(image));
			writer.append(message);
			writer.close();
		} catch (IOException e) {
		}
	}
}
