package com.jmv.frre.moduloestudiante.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.jmv.frre.moduloestudiante.activity.Accounts;
/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.jmv.frre.moduloestudiante.activity.AccountsTest \
 * com.jmv.frre.moduloestudiante.tests/android.test.InstrumentationTestRunner
 */
public class AccountsTest extends ActivityInstrumentationTestCase2<Accounts> {

    public AccountsTest() {
        super("com.jmv.frre.moduloestudiante", Accounts.class);
    }

}
