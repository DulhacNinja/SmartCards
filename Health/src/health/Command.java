package health;

public class Command {
    public final static byte Health_CLA     = (byte) 0x80;
    public final static byte VERIFY         = (byte) 0x20;
    public final static byte UPDATE_PIN     = (byte) 0x30; 
    public final static byte GET_PATIENT    = (byte) 0x40;
    public final static byte SET_PATIENT    = (byte) 0x41;
    public final static byte SET_CONSULT    = (byte) 0x42;
    public final static byte SET_VACATION   = (byte) 0x43;
    
    private Command() {}
}