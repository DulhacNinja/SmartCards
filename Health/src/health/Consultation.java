package health;

public class Consultation {
	private short[] date;  //dd mm yy
	private short diagnostic;
	private short speciality;
	
	public Consultation() {
		this.date = new short[] {0x00, 0x00, 0x00}; 
		this.diagnostic = 0x00;
		this.speciality = 0x00;
	}
	
	public void setDate(short[] date) {
		this.date = date;
	}
	
	public short[] getDate() {
		return this.date;
	}
	
	public void setDiagnostic(short diagnostic) {
		this.diagnostic = diagnostic;
	}
	
	public short getDiagnostic() {
		return this.diagnostic;
	}
	
	public void setSpeciality(short speciality) {
		this.speciality = speciality;
	}
	
	public short getSpeciality() {
		return this.speciality;
	}
	
}
