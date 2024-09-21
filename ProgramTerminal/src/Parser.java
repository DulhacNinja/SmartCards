import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;


public class Parser {
	public String file;
	public Parser(String file){
		this.file = file;
	}
	private byte[] processLine(String line) {
		List<Byte> byteList = new ArrayList<Byte>();
		String strByte;
		StringBuilder builder;
		byte myByte;
		int size = 0;
		StringTokenizer st = new StringTokenizer(line);
	    while (st.hasMoreTokens()) {
	    	size ++;
			strByte = st.nextToken();
			builder = new StringBuilder(strByte);
			builder.replace(0, 2, "");
			if(builder.charAt(0) == '0' && builder.length() > 1)
				builder.replace(0, 1, "");
			if(builder.length() == 3)
				if(builder.charAt(2) == ';')
					builder.deleteCharAt(2);
			// if the hex numeral in of form 0xH instead of 0xHH
			strByte = builder.toString();
			myByte = (byte) Integer.parseInt(strByte, 16);
			
			byteList.add(myByte);
		}
	     byte[] bytes = new byte[size];
	     for(int i = 0; i < size; i++) {
	    	 bytes[i] = byteList.get(i);
	     }
      return bytes;
		
	}
	public byte[][] getByteArray() {
		Path path = Paths.get(file);
		List<byte[]> byteList = new ArrayList<byte[]>();
		byte[] byteLine;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = Files.newBufferedReader(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String curLine;
        try {
			while ((curLine = bufferedReader.readLine()) != null){
			    if(!curLine.contains("//") && curLine.contains("0x")) {
			    	byteLine = processLine(curLine);

			    	byteList.add(byteLine);
			    }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[][] byteArray = byteList.toArray(new byte[0][0]);		
		return byteArray;
		
	}
	
}
