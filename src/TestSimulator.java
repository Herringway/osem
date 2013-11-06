import java.util.Scanner;

/**
 * 
 * @author Cameron
 *
 */
public class TestSimulator {
	public static void main(String[] args) {
		Simulator s = new Simulator();
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String command = scanner.nextLine();
			String[] split = command.split(" ");
			if (split.length == 0)
				continue;
			try {
			switch (split[0]) {
				case "sub": runSub(s, split); break;
				case "plist": runPList(s); break;
				case "show": runShow(s, split); break;
				case "kill": runKill(s, split); break;
				case "run": runRun(s); break;
				case "runall": runRunAll(s); break;
				case "exit": scanner.close(); return;
			}
			} catch (NumberFormatException e) {
				System.err.println("Bad arguments given: expecting integers");
			}
		}
	}
	/**
	 * Executes the "runall" command. executes cpu time slices until nothing is left to run
	 * @param s the simulator instance
	 */
	private static void runRunAll(Simulator s) {
		while (!s.isAllTerminated())
			s.executeSlice();
	}

	/**
	 * Executes the "run" command. executes for one cpu time slice
	 * @param s the simulator instance
	 */
	private static void runRun(Simulator s) {
		s.executeSlice();
	}
	/**
	 * Executes the "kill" command. kills a non-terminated process
	 * @param s the simulator instance
	 * @param split the arguments provided
	 */
	private static void runKill(Simulator s, String[] split) {
		if (split.length < 2) {
			System.err.println("Usage: kill <PID>");
			return;
		}
		if (!s.killProcess(Integer.parseInt(split[1]))) {
			System.err.println("Process " + split[1] + " not found.");
		}
	}

	/**
	 * Executes the "show" command. shows details of non-terminated process
	 * @param s the simulator instance
	 * @param split the arguments provided
	 */
	private static void runShow(Simulator s, String[] split) {
		if (split.length < 2) {
			System.err.println("Usage: show <PID>");
			return;
		}
		if (!s.printStatus(Integer.parseInt(split[1]))) {
			System.err.println("Process " + split[1] + " not found.");
		}
	}

	/**
	 * Executes the "plist" command. lists details of all non-terminated processes
	 * @param s the simulator instance
	 */
	private static void runPList(Simulator s) {
		s.printStatus();
	}

	/**
	 * Executes the "sub" command. creates a new process
	 * @param s the simulator instance
	 * @param split the arguments provided
	 */
	private static void runSub(Simulator s, String[] split) {
		if (split.length < 5) {
			System.err.println("Usage: sub <userID> <priority> <max CPU units> <filename>");
			return;
		}
		if (!s.newProcess(Integer.parseInt(split[1]),Integer.parseInt(split[2]),Integer.parseInt(split[3]),split[4])) {
			System.err.println("Unable to create new process.");
		}
	}
}
