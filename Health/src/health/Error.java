package health;

public class Error {
    final static byte PIN_TRY_LIMIT = (byte) 0x03;
    final static byte MAX_PIN_SIZE = (byte) 0x08;
    final static short SW_VERIFICATION_FAILED = 0x6300;
    final static short SW_WRONG_TYPE = 0x6301;
    final static short SW_WRONG_VALUE = 0x6302;
    final static short SW_INVALID_CONSULTATION = 0x6303;
    final static short SW_INVALID_VACATION_DATE = 0x6304;
    final static short SW_MAX_VACATION_DAYS = 0X6305;
    final static short SW_PIN_VERIFICATION_REQUIRED = 0x6306;
    final static short SW_BAD_BLOOD = 0x6307;

    public Error() {}

    public static byte getPinTryLimit() {
        return PIN_TRY_LIMIT;
    }

    public static byte getMaxPinSize() {
        return MAX_PIN_SIZE;
    }

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
}
