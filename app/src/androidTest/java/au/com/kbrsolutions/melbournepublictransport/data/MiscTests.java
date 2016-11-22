package au.com.kbrsolutions.melbournepublictransport.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

@RunWith(AndroidJUnit4.class)
public class MiscTests {

    Context mContext;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Before
    public void setUp() throws Exception {
//        mContext = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
//        InstrumentationRegistry.getTargetContext();
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnCreate() throws Exception {

    }

    @Test
    public void testOnUpgrade() throws Exception {

    }

    @Test
    public void testReleaseVersion() throws Throwable {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(mContext);

        Utility.isReleaseVersion(mContext);
        Assert.assertTrue("Error: the value of the 'release version' has to be true",
                Utility.isReleaseVersion(mContext));
    }
}
