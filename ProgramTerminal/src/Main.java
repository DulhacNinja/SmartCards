import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadClientInterface;
import com.sun.javacard.apduio.CadDevice;
import com.sun.javacard.apduio.CadTransportException;

public class Main {  
	public static String host = "localhost";
	public static int port = 9025;
	public static Socket socket;
	public static OutputStream output;
	public static InputStream input;
	public static Apdu apdu;
	public static CadClientInterface cad;
	public static Process process;
	public static String crefFilePath = "C:\\Program Files (x86)\\Oracle\\Java Card Development Kit Simulator 3.1.0\\bin\\cref.bat";
	public static String installFilePath = "C:\\Users\\cooki\\OneDrive\\Desktop\\Smartcards\\Riccardo\\sc\\Health\\apdu_scripts\\cap-Health.script";
	public static String selectFilePath = "C:\\Users\\cooki\\OneDrive\\Desktop\\Smartcards\\Riccardo\\sc\\Health\\apdu_scripts\\select-Health.script";
	
	public static void openProcess() {

		try{
			process = Runtime.getRuntime().exec(crefFilePath);
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}// end of openProcess method
		
	public static void connect() {
		
		try
		{	socket = new Socket("localhost", 9025);
			output = socket.getOutputStream();
			input = socket.getInputStream();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}// end of connect method
	
	public static void powerUp() {
		cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, input, output);
		try{
			cad.powerUp();
		}
		catch (CadTransportException | IOException ex){
			ex.printStackTrace();
		}
	
	}// end of powerUp method

	public static void disconnect() {
		try{
			cad.powerDown();
			socket.close();
		} catch (CadTransportException | IOException e){
			e.printStackTrace();
		}
	}// end of disconnect method
		
	public static void killProcess() {
		
		if(process.isAlive()) {
			process.destroyForcibly();
		}
	}// end of killProcess

	public static void sendApdu() {
		try {
			cad.exchangeApdu(apdu);
		} catch (IOException | CadTransportException e){
			e.printStackTrace();
		}
	}// end of sendApdu

	public static void createApdu(byte[] input) {
		apdu = new Apdu();
		byte CLA = input[0];
		byte INS = input[1];
		byte P1 = input[2];
		byte P2 = input[3];
		byte LC = input[4];
		int length = input.length;
		byte LE = input[length - 1];

		byte[] data = new byte[length - 6];
		for(int i = 5; i < length - 1; i++) {
			data[i-5] = input[i];
		}
		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
	}// end of createApdu

	public static void executeCommand(String cmd) {
	    Command[] commands = Command.values();
	    for (Command command : commands) {
	        if (command.getCommand().equals(cmd)) {
	            switch (command) {
	                case EXIT:
	                    System.out.println("Exiting the program.");
	                    System.exit(0);
	                    break;
	                case VERIFY_PIN:
	                	verifyPin();
	                    break;
	                case UPDATE_PIN:
	                	updatePin();
	                    break;
	                case GET_PATIENT:
	                	getPatientData();
	                    break;
	                case SET_PATIENT:
	                	setPatientData();
	                    break;
	                case SET_CONSULT:
	                	setConsultData();
	                    break;
	                case SET_MEDICAL:
	                	setMedicalVacation();
	                    break;
				default:
					break;
	            }
	            return;
	        }
	    }
	    System.out.println("Not a recognized command, type h for commands.");
	}

	public static void runScript(byte[][] script) {
		for(int i = 0; i < script.length; i++) {
			createApdu(script[i]);
			sendApdu();
		}
	}
	
	public static void verifyPin() {
		System.out.println("INSERT PIN");
		String pin = Command.read();
		
		byte[] pinData = new byte[pin.length()];
		for(int i = 0 ; i < pin.length(); i++) {
			pinData[i] = (byte)(pin.charAt(i) - '0');
		}
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x20;
		byte P1 = 0x00;
		byte P2 = 0x00;
		byte LC = (byte) pinData.length;
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(pinData);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch(sw) {
			case 0x9000:
				System.out.println("AUTHENTICATED");
				break;
			case Error.SW_VERIFICATION_FAILED: 
				System.out.println("FAILED TO AUTHENTICATE");
				break;
			default:
				System.out.println("ERROR");
				break;
		}
		
	}
	
	public static void updatePin() {
		System.out.println("INSERT CURRENT PIN");
		String pin = Command.read();
		
		byte[] pinData = new byte[pin.length()];
		for(int i = 0 ; i < pinData.length; i++) {
			pinData[i] = (byte)(pin.charAt(i) - '0');
		}
		
		System.out.println("INSERT NEW PIN");
		String newPin = Command.read();
		
		byte[] newPinData = new byte[newPin.length()];
		for(int i = 0 ; i < newPinData.length; i++) {
			newPinData[i] = (byte)(newPin.charAt(i) - '0');
		}
		byte[] data = new byte[pinData.length + newPinData.length];
		for(int i = 0 ; i < pinData.length; i++) {
			data[i] = pinData[i];
		}
		for (int i = pinData.length; i < pinData.length + newPinData.length; i++) {
			data[i] = newPinData[i - pinData.length];
		}
		
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x30;
		byte P1 = 0x00;
		byte P2 = 0x00;
		byte LC = (byte) (pinData.length + newPinData.length);
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch (sw) {
		case 0x9000:
			System.out.println("UPDATED PIN");
			break;
		case Error.SW_VERIFICATION_FAILED: 
			System.out.println("WRONG PIN");
			break;
		case Error.SW_WRONG_LENGTH: 
			System.out.println("ILLEGAL LENGTH");
			break;
		case Error.SW_PIN_VERIFICATION_REQUIRED: 
			System.out.println("NOT AUTHENTICATED");
			break;
		default:
			System.out.println("ERROR");
			break;
		}
		
	}
	
	public static void getPatientData() {
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x40;
		byte P1 = 0x00;
		byte P2 = 0x00;
		byte LC = 0x00;
		byte LE = 0x1D;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		
		sendApdu();
		byte[] response = apdu.getDataOut();

		int sw = apdu.getStatus();
		switch (sw) {
		case 0x9000:
			System.out.println("Successfully got patient data.");
			System.out.println("Birthdate: " + response[0] + "." + response[1] + "." + response[2]);
			System.out.println("Blood type: " + response[3]);
			System.out.println("RH: " + response[4]);
			if(response[5] == 0x00)
				System.out.println("Diagnostic: " + response[5] + " (not chronic)");
			else 
				System.out.println("Diagnostic: " + response[5] + " (chronic)");
			if(response[6] == 0x00)
				System.out.println("Specialty: " + response[6] + " (not chronic)");
			else 
				System.out.println("Specialty: " + response[6] + " (chronic)");
			System.out.println("Donator: " + response[7]);
			
			System.out.println("Last 3 consultations (from most recent to least recent):");
			
			System.out.println("Consultation #3");
			System.out.println("Date: "+ response[8] + "." + response[9] + "." + response[10]);
			System.out.println("Diagnostic: " + response[11]);
			System.out.println("Specialty: " + response[12]);
			
			System.out.println("Consultation #2");
			System.out.println("Date: "+ response[13] + "." + response[14] + "." + response[15]);
			System.out.println("Diagnostic: " + response[16]);
			System.out.println("Specialty: " + response[17]);
			
			System.out.println("Consultation #1");
			System.out.println("Date: "+ response[18] + "." + response[19] + "." + response[20]);
			System.out.println("Diagnostic: " + response[21]);
			System.out.println("Specialty: " + response[22]);
			
			System.out.println("Last medical vacation beginning date: " + response[23] + "." + response[24] + "." + response[25]);
			System.out.println("Last medical vacation end date: " + response[26] + "." + response[27] + "." + response[28]);

			break;
		case Error.SW_PIN_VERIFICATION_REQUIRED: 
			System.out.println("NOT AUTHENTICATED");
			break;
		default:
			System.out.println("ERROR");
			break;
		}
	}
	
	public static void setDiagnostic() {
		System.out.println("SET NEW DIAGNOSTIC [0-255]: ");
		String cmd = Command.read();
		byte[] data = new byte[1];
		data[0] = (byte) Integer.parseInt(cmd);
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x41;
		byte P1 = 0x00;
		byte P2 = 0x00;
		byte LC = (byte) data.length;
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch(sw) {
			case 0x9000:
				System.out.println("Successfully changed diagnostic.");
				break;
			case Error.SW_PIN_VERIFICATION_REQUIRED:
				System.out.println("You need to be authenticated to use this command.");
				break;
			default:
				System.out.println(apdu);
				System.out.println("Unknown status code.");
				break;
		}
	}
	
	public static void setSpecialty() {
		System.out.println("SET NEW SPECIALTY [0-255]: ");
		String cmd = Command.read();
		byte[] data = new byte[1];
		data[0] = (byte) Integer.parseInt(cmd);
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x41;
		byte P1 = 0x01;
		byte P2 = 0x00;
		byte LC = (byte) data.length;
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch(sw) {
			case 0x9000:
				System.out.println("Successfully changed specialty.");
				break;
			case Error.SW_PIN_VERIFICATION_REQUIRED:
				System.out.println("You need to be authenticated to use this command.");
				break;
			default:
				System.out.println(apdu);
				System.out.println("Unknown status code.");
				break;
		}
	}
	
	public static void setDonator() {
		System.out.println("SET NEW DONATOR STATUS [0-1]: ");
		String cmd = Command.read();
		byte[] data = new byte[1];
		data[0] = (byte) Integer.parseInt(cmd);
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x41;
		byte P1 = 0x02;
		byte P2 = 0x00;
		byte LC = (byte) data.length;
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch(sw) {
			case 0x9000:
				System.out.println("Successfully changed donator status.");
				break;
			case Error.SW_PIN_VERIFICATION_REQUIRED:
				System.out.println("You need to be authenticated to use this command.");
				break;
			case Error.SW_WRONG_VALUE:
				System.out.println("Donator status must be [0-1] (negative or positive).");
				break;
			default:
				System.out.println(apdu);
				System.out.println("Unknown status code.");
				break;
		}
	}
	
	public static void setPatientData() {
		System.out.println("[DIAGNOSTIC]: (Change patient diagnostic): dg");
		System.out.println("[SPECIALTY]: (Change patient specialty): s");
		System.out.println("[DONATOR]: (Change if patient is donator): dn");
		System.out.println("SELECT FIELD:");
		
		String cmd = Command.read();
		
		switch(cmd) {
			case "dg":
				setDiagnostic();
				break;
			case "s":
				setSpecialty();
				break;
			case "dn":
				setDonator();
				break;
			default:
				System.out.println("ILLEGAL ARGUMENT");
				break;
		}
	}
	
	public static void setConsultData() {
		System.out.println("Type date of the consult [dd mm yy]: ");
		String cmd = Command.read();
		String stringBuffer = "";
		byte[] data = new byte[5];
		int j = 0;
		for (int i = 0; i < cmd.length(); i++) {
			if(cmd.charAt(i) == ' ' && stringBuffer != "") {
				data[j] = (byte) Integer.parseInt(stringBuffer);
				j++;
				stringBuffer = "";
				i++;
			}
			stringBuffer = stringBuffer + cmd.charAt(i);
		}
		data[j] = (byte) Integer.parseInt(stringBuffer);
		
		System.out.println("Type diagnostic of the consult [0-255]: ");
		cmd = Command.read();
		data[3] = (byte) Integer.parseInt(cmd);
		
		System.out.println("Type specialty of the consult [0-255]: ");
		cmd = Command.read();
		data[4] = (byte) Integer.parseInt(cmd);
		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x42;
		byte P1 = 0x00;
		byte P2 = 0x00;
		byte LC = (byte) data.length;
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch(sw) {
			case 0x9000:
				System.out.println("Successfully set new consultation.");
				break;
			case Error.SW_VERIFICATION_FAILED: 
				System.out.println("You need to be authenticated to use this command.");
				break;
			case Error.SW_INVALID_CONSULTATION:
				System.out.println("You are not allowed to make this consultation.");
				break;
			default:
				System.out.println(apdu);
				System.out.println("Unknown status code.");
				break;
		}
		
	}
	
	public static void setMedicalVacation() {
		System.out.println("SET BEGINNING VACATION [dd mm yy]: ");
		String cmd = Command.read();
		String stringBuffer = "";
		byte[] data = new byte[6];
		int j = 0;
		for (int i = 0; i < cmd.length(); i++) {
			if(cmd.charAt(i) == ' ' && stringBuffer != "") {
				data[j] = (byte) Integer.parseInt(stringBuffer);
				j++;
				stringBuffer = "";
				i++;
			}
			stringBuffer = stringBuffer + cmd.charAt(i);
		}
		data[j] = (byte) Integer.parseInt(stringBuffer);
		
		System.out.println("SET END VACATION [dd mm yy]: ");
		cmd = Command.read();
		stringBuffer = "";
		j = 3;
		for (int i = 0; i < cmd.length(); i++) {
			if(cmd.charAt(i) == ' ' && stringBuffer != "") {
				data[j] = (byte) Integer.parseInt(stringBuffer);
				j++;
				stringBuffer = "";
				i++;
			}
			stringBuffer = stringBuffer + cmd.charAt(i);
		}
		data[j] = (byte) Integer.parseInt(stringBuffer);

		apdu = new Apdu();
		byte CLA = (byte) 0x80;
		byte INS = (byte) 0x43;
		byte P1 = 0x00;
		byte P2 = 0x00;
		byte LC = (byte) data.length;
		byte LE = 0x7F;

		apdu.command = new byte[] {CLA, INS, P1, P2};
		apdu.setLc(LC);
		apdu.setLe(LE);
		apdu.setDataIn(data);
		
		sendApdu();
		int sw = apdu.getStatus();
		switch(sw) {
			case 0x9000:
				System.out.println("Successfully set new consultation.");
				break;
			case Error.SW_VERIFICATION_FAILED: 
				System.out.println("You need to be authenticated to use this command.");
				break;
			case Error.SW_MAX_VACATION_DAYS:
				System.out.println("You're trying to get more vacation days than possible.");
				break;
			default:
				System.out.println(apdu);
				System.out.println("Unknown status code.");
				break;
		}
	}
	
	public static void main(String[] args){
		
		openProcess();
		connect();
		powerUp();
		
		Parser parser = new Parser(installFilePath);
		byte[][] installScript = parser.getByteArray();
		
		parser = new Parser(selectFilePath);
		byte[][] selectScript = parser.getByteArray();
		
		runScript(installScript);
		runScript(selectScript);
		
		Command.printCommand(Command.HELP);
		boolean ok = true;
		while (ok) {
			String command = Command.read();
		    switch (command) {
		        case "h":
		            Command.printCommands();
		            break;
		        case "0":
		        	ok = false;
		        	break;
		        default:
		            executeCommand(command);
		            break;
		    }
		}
		disconnect();
		killProcess();
	}

}
