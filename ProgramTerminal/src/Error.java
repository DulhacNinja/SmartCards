
public class Error {
	final static short SW_VERIFICATION_FAILED = 0x6300;
    final static short SW_WRONG_TYPE = 0x6301;
    final static short SW_WRONG_VALUE = 0x6302;
    final static short SW_INVALID_CONSULTATION = 0x6303;
    final static short SW_INVALID_VACATION_DATE = 0x6304;
    final static short SW_MAX_VACATION_DAYS = 0X6305;
    final static short SW_PIN_VERIFICATION_REQUIRED = 0x6306;
    final static short SW_BAD_BLOOD = 0x6307;    
    final static short SW_WRONG_LENGTH = 0x6700;

    public Error() {}

    public static short getSwVerificationFailed() {
        return SW_VERIFICATION_FAILED;
    }

    public static short getSwWrongType() {
        return SW_WRONG_TYPE;
    }

    public static short getSwWrongValue() {
        return SW_WRONG_VALUE;
    }

    public static short getSwInvalidConsultation() {
        return SW_INVALID_CONSULTATION;
    }

    public static short getSwInvalidVacationDate() {
        return SW_INVALID_VACATION_DATE;
    }

    public static short getSwMaxVacationDays() {
        return SW_MAX_VACATION_DAYS;
    }

    public static short getSwPinVerificationRequired() {
        return SW_PIN_VERIFICATION_REQUIRED;
    }

    public static short getSwBadBlood() {
        return SW_BAD_BLOOD;
    }

    public static short getSwWrongLength() {
        return SW_WRONG_LENGTH;
    }
}
