package com.sathya.mobileotpauth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.sathya.mobileotpauth.Delay.waitFor;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.*;

import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;

public class MainActivityTest {


    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mobileNumberVerificationSuccess() {

        onView(isRoot()).perform(waitFor(5000));

        //Opening Keyboard

        onView(withId(R.id.editTextPhone))
                .perform(click());


        //Entering Mobile Number

        onView(withId(R.id.one))
                .perform(click());
        onView(withId(R.id.two))
                .perform(click());
        onView(withId(R.id.three))
                .perform(click());
        onView(withId(R.id.four))
                .perform(click());
        onView(withId(R.id.five))
                .perform(click());
        onView(withId(R.id.six))
                .perform(click());
        onView(withId(R.id.seven))
                .perform(click());
        onView(withId(R.id.eight))
                .perform(click());
        onView(withId(R.id.nine))
                .perform(click());
        onView(withId(R.id.zero))
                .perform(click());

        //Proceed Ahead

        onView(withId(R.id.imageView2))
                .perform(click());


        onView(withText("OTP Sent..")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));


    }


    @Test
    public void mobileNumberVerificationFail() {

        onView(isRoot()).perform(waitFor(5000));

        //Opening Keyboard

        onView(withId(R.id.editTextPhone))
                .perform(click());


        //Entering Mobile Number

        onView(withId(R.id.one))
                .perform(click());
        onView(withId(R.id.two))
                .perform(click());
        onView(withId(R.id.three))
                .perform(click());
        onView(withId(R.id.four))
                .perform(click());
        onView(withId(R.id.five))
                .perform(click());
        onView(withId(R.id.six))
                .perform(click());

        //Proceed Ahead

        onView(withId(R.id.imageView2))
                .perform(click());


        onView(withText("Enter Correct Number")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));


    }


//    @Test
//    public void mobileNumberVerificationNotSuccessWithWrongOTP() {
//
//        onView(isRoot()).perform(waitFor(5000));
//
//        //Opening Keyboard
//
//        onView(withId(R.id.editTextPhone))
//                .perform(click());
//
//
//        //Entering Mobile Number
//
//        onView(withId(R.id.one))
//                .perform(click());
//        onView(withId(R.id.two))
//                .perform(click());
//        onView(withId(R.id.three))
//                .perform(click());
//        onView(withId(R.id.four))
//                .perform(click());
//        onView(withId(R.id.five))
//                .perform(click());
//        onView(withId(R.id.six))
//                .perform(click());
//        onView(withId(R.id.seven))
//                .perform(click());
//        onView(withId(R.id.eight))
//                .perform(click());
//        onView(withId(R.id.nine))
//                .perform(click());
//        onView(withId(R.id.zero))
//                .perform(click());
//
//        //Proceed Ahead
//
//        onView(withId(R.id.imageView2))
//                .perform(click());
//
//
//        onView(withText("OTP Sent..")).inRoot(new ToastMatcher())
//                .check(matches(isDisplayed()));
//
//
//    }
//
//        //Opening Keyboard
//
//        onView(withId(R.id.show))
//                .perform(click());
//
//
//        //Entering Mobile Number
//
//        onView(withId(R.id.one))
//                .perform(click());
//        onView(withId(R.id.two))
//                .perform(click());
//        onView(withId(R.id.three))
//                .perform(click());
//        onView(withId(R.id.four))
//                .perform(click());
//        onView(withId(R.id.five))
//                .perform(click());
//        onView(withId(R.id.six))
//                .perform(click());
//        //Proceed Ahead
//
//        onView(withId(R.id.imageView2))
//                .perform(click());
//



//    }

    public class ToastMatcher extends TypeSafeMatcher<Root> {


        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }


        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText("is toast");
        }
    }
}