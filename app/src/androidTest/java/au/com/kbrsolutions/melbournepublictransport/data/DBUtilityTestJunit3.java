package au.com.kbrsolutions.melbournepublictransport.data;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by business on 21/08/2016.
 */
public class DBUtilityTestJunit3 extends TestCase {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Test
    public void silyTest() {
        Log.v(TAG, "running Unit Test 3");
        Assert.assertTrue("Should be true", false);
    }
}