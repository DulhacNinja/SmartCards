package health;

public class Patient {
    private short[] birthDate;  //dd mm yy
    private short blood;
    private short rH;
    private short diagnostic;
    private short speciality;
    private short donator;
    private Consultation[] consultations;
    private short[] vacationStart;  //dd mm yy
    private short[] vacationEnd;  //dd mm yy
    
    public Patient() {
        this.birthDate = new short[] {0x00,0x00,0x00};
        this.blood = 0x00;
        this.rH = 0x00;
        this.diagnostic = 0x00;
        this.speciality = 0x00;
        this.donator = 0x00;
        this.consultations = new Consultation[3];
        for(short i = 0; i < 3; i++)
        	this.consultations[i] = new Consultation();
        this.vacationStart = new short[] {0x00,0x00,0x00};
        this.vacationEnd = new short[] {0x00,0x00,0x00};
    }
    
    public short[] getBirthDate() {
        return birthDate;
    }

    public short getBlood() {
        return blood;
    }

    public short getRH() {
        return rH;
    }

    public short getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(short diagnostic) {
        this.diagnostic = diagnostic;
    }

    public short getSpeciality() {
        return speciality;
    }

    public void setSpeciality(short speciality) {
        this.speciality = speciality;
    }

    public short getDonator() {
        return donator;
    }

    public void setDonator(short donator) {
        this.donator = donator;
    }

    public Consultation[] getConsultations() {
        return consultations;
    }

    public void setConsultations(Consultation[] consultations) {
        this.consultations = consultations;
    }

    public short[] getVacationStart() {
        return vacationStart;
    }

    public void setVacationStart(short[] vacationStart) {
        this.vacationStart = vacationStart;
    }

    public short[] getVacationEnd() {
        return vacationEnd;
    }

    public void setVacationEnd(short[] vacationEnd) {
        this.vacationEnd = vacationEnd;
    }
    
    public void setNewConsultation(Consultation consultation) {
    	Consultation second = this.consultations[1];
    	Consultation third = this.consultations[2];
    	this.consultations[0] = second;
    	this.consultations[1] = third;
    	this.consultations[2] = consultation;
    }
    
    public boolean invalidConsult(Consultation consultation) {
    	for (short i = 0; i < 3; i++) {
    		if(consultation.getSpeciality() == this.consultations[i].getSpeciality() && (this.diagnostic == 0x00 || (this.diagnostic == 0x01 && this.speciality != consultation.getSpeciality()))) {
    			short[] data = consultation.getDate();
    			short[] dataConsultations = this.consultations[i].getDate();
    			if(data[1] == dataConsultations[1] && data[2] == dataConsultations[2])
    				return true;
    		}
    	}
    	return false;
    }
}
