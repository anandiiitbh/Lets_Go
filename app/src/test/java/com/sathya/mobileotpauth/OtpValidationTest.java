package com.sathya.mobileotpauth;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.sathya.mobileotpauth.helper.Constants;

import org.junit.Test;

public class OtpValidationTest {

    @Test
    public void mobileNumberIsValid(){

        String number = "7461007111";

        assertTrue(">>>> Number is Valid !!!", OtpValidation.isValid(number));
    }

    @Test
    public void mobileNumberIsNotValid(){

        String number = "93045541723";

        assertFalse(">>>> Number is Not Valid !!!", OtpValidation.isValid(number));
    }

}