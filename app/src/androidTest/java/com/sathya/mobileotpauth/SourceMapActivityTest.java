package com.sathya.mobileotpauth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;

public class SourceMapActivityTest {


    @Rule
    public ActivityScenarioRule<SourceMapActivity> activityRule
            = new ActivityScenarioRule<>(SourceMapActivity.class);


    @Test
    public void viewSourceActivityText() {

        onView(withText("Select Destination"))
                .check(matches(isDisplayed()));
    }




}