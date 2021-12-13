package com.sathya.mobileotpauth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class CustomerSupportTest {


    @Rule
    public ActivityScenarioRule<CustomerSupport> activityRule
            = new ActivityScenarioRule<>(CustomerSupport.class);


    @Test
    public void queryButtonCheck() {

        onView(withId(R.id.editTextTextMultiLine))
                .perform(typeText("Hi, My payment got stuck :) PaymentId: 522389") , closeSoftKeyboard());

        onView(withId(R.id.submitQuery))
                .perform(click());
    }


}