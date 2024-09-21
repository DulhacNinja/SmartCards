import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public enum Command {
	HELP("h", "[HELP] (Show all commands): "),
    EXIT("e", "[EXIT] (Exit the program): "),
    VERIFY_PIN("v", "[VERIFY] (Verify PIN): "),
    UPDATE_PIN("u", "[UPDATE_PIN] (Update PIN): "),
    GET_PATIENT("gp", "[GET_PATIENT]: (Get patient details): "),
    SET_PATIENT("sp", "[SET_PATIENT]: (Set patient details): "),
    SET_CONSULT("sc", "[SET_CONSULT]: (Set consultation details): "),
    SET_MEDICAL("sm", "[SET_VACATION]: (Set medical vacation): ");

    private final String command;
    private final String description;
    public static BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));

    Command(final String command, final String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
    
    public static String read() {
        String cmd = null;
        inputCommand();
		try {
			cmd = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cmd;
    }
    
    public static void printCommands() {
    	System.out.println();
    	System.out.println("_______________________________________________");
    	System.out.println("COMMAND PANEL");
    	System.out.println("_______________________________________________");
    	for(Command command: Command.values())
    		System.out.println(command.getDescription() + command.getCommand());
    	System.out.println("_______________________________________________");    	
    }
    
    public static void printCommand(Command command) {
    	System.out.println();
    	System.out.println(command.getDescription() + command.getCommand());
    }
    
    public static void inputCommand() {
    	System.out.println();
    	System.out.print("[CLIENT]: ");
    }
}
