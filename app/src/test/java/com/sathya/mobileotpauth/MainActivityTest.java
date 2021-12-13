package com.sathya.mobileotpauth;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.sathya.mobileotpauth.helper.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito.*;

public class MainActivityTest {


    SharedPreferences sharedPreferences;

    @Before
    public void initMocks(){
        sharedPreferences = mock(SharedPreferences.class);
    }

    @Test
    public void checkLoginReturnTrueTest(){

        //Mocking SharedPreferences as if credentials are stored.
        when(sharedPreferences.contains(Constants.IS_LOGIN)).thenReturn(true);
        when(sharedPreferences.getBoolean(Constants.IS_LOGIN,false)).thenReturn(true);


        //                                                         checkForLogin --return-->  true
        assertTrue(">>>> User Logged In !!!", MainActivity.checkForLogin(sharedPreferences));
    }

    @Test
    public void checkLoginReturnFalseTest(){

        //Mocking SharedPreferences as no credentials are stored.
        when(sharedPreferences.contains(Constants.IS_LOGIN)).thenReturn(false);
        when(sharedPreferences.getBoolean(Constants.IS_LOGIN,false)).thenReturn(false);

        //                                                             checkForLogin --return-->  false
        assertFalse(">>>> User Not Logged In Previously!!!", MainActivity.checkForLogin(sharedPreferences));
    }
}