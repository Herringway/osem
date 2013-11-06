/**
 * 
 * @author Cameron
 *
 */
public class PImage {
	public final int codeSize = 32;
	
	private processPriorities priority;
	private processStates state;
	private int UID;
	private int PID;
	private int ProgramCounter;
	private int ExecutedCycles;
	private int MaxCycles;
	private int Accumulator;
	private int PSW;
	private String Name;
	private PImage nextImage;
	private int[] code;
	private boolean isDead;

	public enum processStates { New, Ready, Running }
	public enum processPriorities { Low, Normal, High }
	public PImage() {
		UID = 0;
		PID = 0;
		priority = processPriorities.Normal;
		state = processStates.New;
		ProgramCounter = 0;
		MaxCycles = 0;
		Accumulator = 0;
		PSW = 0;
		Name = null;
		nextImage = null;
		code = new int[codeSize];
		isDead = false;
	}
	/**
	 * @return the executedCycles
	 */
	public int getExecutedCycles() {
		return ExecutedCycles;
	}
	/**
	 * @param executedCycles the executedCycles to set
	 */
	public void setExecutedCycles(int executedCycles) {
		ExecutedCycles = executedCycles;
	}
	/**
	 * Get the userID for this process.
	 * @return the userID for this process.
	 */
	public int getUID() {
		return UID;
	}
	/**
	 * Set the userID for this process.
	 * @param uID the new userID for this process.
	 */
	public void setUID(int uID) {
		UID = uID;
	}
	/**
	 * Get the process ID for this process.
	 * @return the process ID for this process.
	 */
	public int getPID() {
		return PID;
	}
	/**
	 * Set the process ID for this process.
	 * @param pID the new process ID for this process.
	 */
	public void setPID(int pID) {
		if (pID < 0)
			PID = 0;
		else
			PID = pID;
	}
	/**
	 * Get the program counter for this process.
	 * @return the program counter for this process.
	 */
	public int getProgramCounter() {
		return ProgramCounter;
	}
	/**
	 * Set the program counter for this process.
	 * @param programCounter the new program counter for this process.
	 */
	public void setProgramCounter(int programCounter) {
		if (programCounter < 0)
			ProgramCounter = 0;
		else
			ProgramCounter = programCounter;
	}
	/**
	 * Get the max cpu cycles for this process.
	 * @return the max cpu cycles for this process.
	 */
	public int getMaxCycles() {
		return MaxCycles;
	}
	/**
	 * Set the max cpu cycles for this process.
	 * @param maxCycles the new max cycles for this process.
	 */
	public void setMaxCycles(int maxCycles) {
		MaxCycles = maxCycles;
	}
	/**
	 * Get the accumulator for this process.
	 * @return the accumulator for this process.
	 */
	public int getAccumulator() {
		return Accumulator;
	}
	/**
	 * Set the accumulator for this process.
	 * @param accumulator the new accumulator for this process.
	 */
	public void setAccumulator(int accumulator) {
		Accumulator = accumulator;
	}
	/**
	 * Get the processor status word for this process.
	 * @return the processor status word for this process.
	 */
	public int getPSW() {
		return PSW;
	}
	/**
	 * Set the processor status word for this process.
	 * @param status the new processor status word for this process.
	 */
	public void setPSW(int status) {
		PSW = status;
	}
	/**
	 * Get the name for this process.
	 * @return the name for this process.
	 */
	public String getName() {
		return Name;
	}
	/**
	 * Set the name for this process.
	 * @param name the new name for this process.
	 */
	public void setName(String name) {
		Name = name;
	}
	/**
	 * Get the next process in line.
	 * @return the next process in line.
	 */
	public PImage getNextImage() {
		return nextImage;
	}
	/**
	 * Set the next process in line.
	 * @param nextImage the next process in line.
	 */
	public void setNextImage(PImage nextImage) {
		this.nextImage = nextImage;
	}
	/**
	 * Get the binary code for this process.
	 * @return the binary code for this process.
	 */
	public int[] getCode() {
		return code;
	}
	/**
	 * Set the binary code for this process.
	 * @param code the new code for this process.
	 */
	public void setCode(int[] code) {
		this.code = code;
	}
	/**
	 * Set the priority of this process.
	 * @param priority the new priority for this process.
	 */
	public void setPriority(int priority) {
		if (priority > processPriorities.values().length)
			priority = 0;
		this.priority = processPriorities.values()[priority];
	}
	/**
	 * Set the state of this process.
	 * @param state the new state for the process.
	 */
	public void setState(int state) {
		if (state > processStates.values().length)
			state = 0;
		this.state = processStates.values()[state];
	}
	/**
	 * Get the state of this process.
	 * @return the process's state.
	 */
	public processStates getState() {
		return this.state;
	}
	/**
	 * Get the priority of this process.
	 * @return the priority of the process.
	 */
	public processPriorities getPriority() {
		return this.priority;
	}
	/**
	 * Mark a process as dead. will be skipped over at execution time.
	 */
	public void kill() {
		this.isDead = true;
	}
	/**
	 * Whether or not the process has been killed.
	 * @return whether or not the process has been marked as dead
	 */
	public boolean isDead() {
		return this.isDead;
	}
}
