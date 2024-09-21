/** 
 * Copyright (c) 1998, 2021, Oracle and/or its affiliates. All rights reserved.
 * 
 */


package health;

import javacard.framework.*;
import javacardx.annotations.*;

/**
 * Applet class
 * 
 * @author <user>
 */

@StringPool(value = {
    @StringDef(name = "Package", value = "health"),
    @StringDef(name = "AppletName", value = "Health")},
    name = "HealthStrings")
public class Health extends Applet {
    Command command;
    Error error;
    private OwnerPIN pin;
    short pinLength = 0x00;
    Patient patient;

    private Health(byte[] bArray, short bOffset, byte bLength) {
        error = new Error();
        patient = new Patient();
        
        pin = new OwnerPIN(Error.getPinTryLimit(), Error.getMaxPinSize());

        byte iLen = bArray[bOffset];
        bOffset = (short) (bOffset + iLen + 1);
        byte cLen = bArray[bOffset]; 
        bOffset = (short) (bOffset + cLen + 1);
        byte aLen = bArray[bOffset]; 

        pin.update(bArray, (short) (bOffset + 1), aLen);
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Health(bArray, bOffset, bLength);
    }

    @Override
    public boolean select() {
        if (pin.getTriesRemaining() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void deselect() {
        pin.reset();
    }

    @Override
    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        if (apdu.isISOInterindustryCLA()) {
            if (buffer[ISO7816.OFFSET_INS] == (byte) (0xA4)) {
                return;
            }
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        if (buffer[ISO7816.OFFSET_CLA] != Command.Health_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case Command.VERIFY:
                verify(apdu);
                return;
            case Command.UPDATE_PIN:
                updatePin(apdu);
                return;
            case Command.GET_PATIENT:
                getPatientData(apdu);
                return;
            case Command.SET_PATIENT:
                setPatientData(apdu);
                return;
            case Command.SET_CONSULT:
                setConsultData(apdu);
                return;
            case Command.SET_VACATION:
                setMedicalVacation(apdu);
                return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void verify(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if (pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false) {
            ISOException.throwIt(Error.getSwVerificationFailed());
        }
        pinLength = byteRead;
    }

    private void updatePin(APDU apdu) {
        if (!pin.isValidated()) {
            ISOException.throwIt(Error.getSwPinVerificationRequired());
        }

        byte[] buffer = apdu.getBuffer();
        byte byteRead = (byte) (apdu.setIncomingAndReceive());
        byte numBytes = buffer[ISO7816.OFFSET_LC];

        if ((numBytes > pinLength + Error.getMaxPinSize()) || (byteRead > pinLength + Error.getMaxPinSize())) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        byte[] oldPin = new byte[pinLength];
        short newPinLength = (short) (byteRead - pinLength);
        byte[] newPin = new byte[newPinLength];

        for (short i = 0; i < pinLength; i++) {
            oldPin[i] = buffer[(short) (ISO7816.OFFSET_CDATA + i)];
        }

        for (short i = pinLength; i < byteRead; i++) {
            newPin[(short) (i - pinLength)] = buffer[(short) (ISO7816.OFFSET_CDATA + i)];
        }

        if (pin.check(buffer, ISO7816.OFFSET_CDATA, (byte) pinLength) == false) {
            ISOException.throwIt(Error.getSwVerificationFailed());
        }

        pin.update(newPin, (short) 0, (byte) newPinLength);
        pinLength = newPinLength;
    }

    private void getPatientData(APDU apdu) {
        if (!pin.isValidated()) {
            ISOException.throwIt(Error.getSwPinVerificationRequired());
        }

        byte[] buffer = apdu.getBuffer();
        short le = apdu.setOutgoing();

        if (le < 29) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        apdu.setOutgoingLength((byte) 29);

        short i = 0;
        short[] birthDate = patient.getBirthDate();
        for (short k = 0; k < 3; k++) {
            buffer[i++] = (byte) birthDate[k];
        }

        buffer[i++] = (byte) patient.getBlood();
        buffer[i++] = (byte) patient.getRH();
        buffer[i++] = (byte) patient.getDiagnostic();
        buffer[i++] = (byte) patient.getSpeciality();
        buffer[i++] = (byte) patient.getDonator();

        Consultation[] consultations = patient.getConsultations();
        for (short j = 0; j < 3; j++) {
            short[] date = consultations[j].getDate();
            for (short k = 0; k < 3; k++) {
                buffer[i++] = (byte) date[k];
            }
            buffer[i++] = (byte) (consultations[j].getDiagnostic());
            buffer[i++] = (byte) (consultations[j].getSpeciality());
        }

        short[] vacationStart = patient.getVacationStart();
        for (short k = 0; k < 3; k++) {
            buffer[i++] = (byte) vacationStart[k];
        }

        short[] vacationEnd = patient.getVacationEnd();
        for (short k = 0; k < 3; k++) {
            buffer[i++] = (byte) vacationEnd[k];
        }

        apdu.sendBytes((short) 0, (short) 29);
    }

    private void setPatientData(APDU apdu) {
        if (!pin.isValidated()) {
            ISOException.throwIt(Error.getSwPinVerificationRequired());
        }

        byte[] buffer = apdu.getBuffer();
        byte numBytes = buffer[ISO7816.OFFSET_LC];
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        byte value = buffer[ISO7816.OFFSET_CDATA];
        byte type = buffer[ISO7816.OFFSET_P1];

        if (type > 0x02) {
            ISOException.throwIt(Error.getSwWrongType());
        }
        if (type == 0x00) {
            patient.setDiagnostic(value);
        }
        if (type == 0x01) {
            patient.setSpeciality(value);
        }
        if (type == 0x02) {
            if (value != 0x00 && value != 0x01) {
                ISOException.throwIt(Error.getSwWrongValue());
            }
            patient.setDonator(value);
        }
    }

    private void setConsultData(APDU apdu) {
        if (!pin.isValidated()) {
            ISOException.throwIt(Error.getSwPinVerificationRequired());
        }

        byte[] buffer = apdu.getBuffer();
        byte numBytes = buffer[ISO7816.OFFSET_LC];
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if ((numBytes != 5) || (byteRead != 5)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        Consultation consult = new Consultation();
        short[] dateRead = {0x00, 0x00, 0x00};
        for (short k = 0; k < 3; k++) {
            dateRead[k] = buffer[(short) (ISO7816.OFFSET_CDATA + k)];
        }
        consult.setDate(dateRead);
        consult.setDiagnostic(buffer[ISO7816.OFFSET_CDATA + 3]);
        consult.setSpeciality(buffer[ISO7816.OFFSET_CDATA + 4]);

        if (patient.invalidConsult(consult) == true)
            ISOException.throwIt(Error.getSwInvalidConsultation());

        patient.setNewConsultation(consult);
    }

    private void setMedicalVacation(APDU apdu) {
        if (!pin.isValidated()) {
            ISOException.throwIt(Error.getSwPinVerificationRequired());
        }

        byte[] buffer = apdu.getBuffer();
        byte numBytes = buffer[ISO7816.OFFSET_LC];
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if ((numBytes != 6) || (byteRead != 6)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        short[] beginDateRead = {0x00, 0x00, 0x00};
        short[] endDateRead = {0x00, 0x00, 0x00};

        for (short k = 0; k < 3; k++) {
            beginDateRead[k] = buffer[(short) (ISO7816.OFFSET_CDATA + k)];
            endDateRead[k] = buffer[(short) (ISO7816.OFFSET_CDATA + k + 3)];
        }

        if (beginDateRead[0] > endDateRead[0])
            ISOException.throwIt(Error.getSwInvalidVacationDate());
        if (beginDateRead[1] != endDateRead[1])
            ISOException.throwIt(Error.getSwInvalidVacationDate());
        if (beginDateRead[2] != endDateRead[2])
            ISOException.throwIt(Error.getSwInvalidVacationDate());

        short days = 0x00;
        short oldDays = 0x00;

        short[] vacationBegin = patient.getVacationStart();
        short[] vacationEnd = patient.getVacationEnd();
        if (beginDateRead[1] == vacationBegin[1] && beginDateRead[2] == vacationBegin[2])
            oldDays = (short) (vacationEnd[0] - vacationBegin[0] + 1);

        days = (short) (endDateRead[0] - beginDateRead[0] + 1);

        if (days + oldDays > 10 && patient.getDiagnostic() == 0x00)
            ISOException.throwIt(Error.getSwMaxVacationDays());

        patient.setVacationStart(beginDateRead);
        patient.setVacationEnd(endDateRead);
    }
}
