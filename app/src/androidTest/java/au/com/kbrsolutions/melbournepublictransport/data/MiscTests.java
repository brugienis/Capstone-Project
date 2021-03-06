package au.com.kbrsolutions.melbournepublictransport.data;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

@SuppressWarnings("unused")
@RunWith(AndroidJUnit4.class)
public class MiscTests {

    private Context mContext;

    @SuppressWarnings("unused")
    private final String TAG = ((Object) this).getClass().getSimpleName();

    @SuppressWarnings("RedundantThrows")
    @Before
    public void setUp() throws Exception {
//        mContext = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
//        InstrumentationRegistry.getTargetContext();
        mContext = InstrumentationRegistry.getTargetContext();
    }

//    @After
//    public void tearDown() throws Exception {
//
//    }

//    @Test
//    public void testOnCreate() throws Exception {
//
//    }

//    @Test
//    public void testOnUpgrade() throws Exception {
//
//    }

    @Test
    public void testReleaseVersion() throws Throwable {
        SharedPreferencesUtility.isReleaseVersion(mContext);
        Assert.assertTrue("Error: the value of the 'release version' has to be true",
                SharedPreferencesUtility.isReleaseVersion(mContext));
    }
}
