package de.xinaris.junit;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class GTTest
    extends TestCase
{
    private TestBean oUT;

    private BeanMethodTestHelper bmth;

    @Before
    public void setUp()
    {
        oUT = new TestBean();
    }
    
    @Test
    public void testBeanIsNull() {
        try {
            bmth = new BeanMethodTestHelper(null);
            fail("An exception should be thrown for null object");
        } catch (IllegalArgumentException e) {
            assertEquals("Unexpected Exception message", "Object under test must not be null", e.getMessage());
        }        
    }
    
    @Test
    public void testBeanGetterSetter() {
	final TestBean testBean = new TestBean();
        bmth = new BeanMethodTestHelper(testBean);
        assertEquals("The object under test isn't equal", testBean, bmth.getOUT());
        bmth.setOUT(oUT);
        assertEquals("The object under test isn't equal", oUT, bmth.getOUT());
    }
    
    @Test
    public void testAllBeanMethodsDefaultMode() {
        bmth = new BeanMethodTestHelper(oUT);
        assertTrue("Test mode should be Exclude", bmth.isExclusionMode());
        
        bmth.excludeProperty("exceptionWithGet");
        bmth.excludeProperty("exceptionWhenSet");
        bmth.excludeProperty("nothing");
        try {
            bmth.includeProperty("whatever");
            fail("We should not be able to add include properties in exclude mode");
        } catch (Exception e) { // NOPMD
            // exactly what we want
        }        
        bmth.executeBeanMethods();
    }

    @Test
    public void testBeanMethodsIncludeMode() {
        bmth = new BeanMethodTestHelper(oUT, BeanMethodTestHelper.TestMode.Include);
        try {
            bmth.excludeProperty("whatever");
            fail("We should not be able to add include properties in exclude mode");
        } catch (Exception e) { // NOPMD
            // exactly what we want
        }
        bmth.includeProperty("string");
        bmth.executeBeanMethods();
    }

    @Test
    public void testBeanMethodsIncludeModeTestExclude() {
        bmth = new BeanMethodTestHelper(oUT, BeanMethodTestHelper.TestMode.Include);
        try {
            bmth.excludeProperty("whatever");
            fail("We should not be able to add include properties in exclude mode");
        } catch (Exception e) { // NOPMD
            // exactly what we want
        }
    }
    
    @Test
    public void testBeanMethodsIncludeModeTestExcludeGetException() {
        bmth = new BeanMethodTestHelper(oUT, BeanMethodTestHelper.TestMode.Include);
        bmth.includeProperty("exceptionWithGet");
        try {
            bmth.executeBeanMethods();
            fail("An IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Unexpected Exception message", "getException", e.getMessage());
        }
    }

    @Test
    public void testBeanMethodsIncludeModeTestExcludeSetException() {
        bmth = new BeanMethodTestHelper(oUT, BeanMethodTestHelper.TestMode.Include);
        bmth.includeProperty("exceptionWhenSet");
        try {
            bmth.executeBeanMethods();
            fail("An IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Unexpected Exception message", "setException", e.getMessage());
        }
    }

    @Test
    public void testBeanMethodsExceptionWithNullEnum() {
        bmth = new BeanMethodTestHelper(oUT, BeanMethodTestHelper.TestMode.Include);
        assertFalse("Test mode should be Include", bmth.isExclusionMode());
        
        bmth.includeProperty("nothing");
        try {
            bmth.executeBeanMethods();
            fail("An ArrayIndexOutOfBoundsException should have been thrown");
        } catch (Exception e) {
            assertEquals("Unexpected Exception message", "java.lang.ArrayIndexOutOfBoundsException: 0", e.getMessage());
        }        
    }

}
