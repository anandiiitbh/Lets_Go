package com.sathya.mobileotpauth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.sathya.mobileotpauth.helper.Constants;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito.*;

class MainActivityTest {

    SharedPreferences sharedPreferences;
    MainActivity activity;
    @BeforeAll
    void initMocks(){
        sharedPreferences = mock(SharedPreferences.class);
        activity = new MainActivity();
    }

    @Test
    void checkLoginReturnTrueTest(){
        when(sharedPreferences.contains(Constants.IS_LOGIN)).thenReturn(true);
        when(sharedPreferences.getBoolean(Constants.IS_LOGIN,false)).thenReturn(true);
        assertTrue(activity.checkForLogin(sharedPreferences),">>>> User Logged In !!!");
    }

    @Test
    void checkLoginReturnFalseTest(){
        when(sharedPreferences.contains(Constants.IS_LOGIN)).thenReturn(false);
        when(sharedPreferences.getBoolean(Constants.IS_LOGIN,false)).thenReturn(false);
        assertFalse(activity.checkForLogin(sharedPreferences),">>>> User Logged In !!!");
    }
}