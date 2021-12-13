package com.sathya.mobileotpauth;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class MapsActivityTest {

    @Test
    public void calculateFare(){

        int min                         = 1;
        int max                         = 99;
        Integer distance                = (int) Math.random() * (max-21+1)+21;
        Integer baseFare                = (int) Math.random() * (max-min+1)+min;
        Integer travelChargersBefore20  = (int) Math.random() * (max-min+1)+min;
        Integer travelChargersAfter20   = (int) Math.random() * (max-min+1)+min;

        Integer fare = baseFare + (travelChargersBefore20 * 20) + (((int) distance - 20) * travelChargersAfter20);

        assertEquals(">>>> Fare Calculator above 20 kms distance",fare,MapsActivity.fareCalculater(distance,baseFare,travelChargersBefore20,travelChargersAfter20));
    }
}