package de.xinaris.junit;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;

/**
 * This helper class executes all bean methods exposed by the object under test.
 * This allows coverage tests to ignore these simple methods without real loss
 * to the provided information. Setter and getter are tested. If a property does not
 * offer both methods, then this property cannot be tested automatically by this
 * class. This is by design to guarantee that only those getters and setters are 
 * automatically tested which are definitely no special cases.
 * 
 * EasyMock is used to generate objects of those classes and
 * interfaces for which no simple way to create them is available.
 * 
 * Java 1.5 is assumed to be used. If you do not use Java 1.5, please remove the
 * lines between the comments ***JDK1.5***
 * 
 * Usage is quite simple:
 * <pre>
 * BeanMEthodTestHelper bmth;
 * bmth = new BeanMethodTestHelper(oUT);
 * bmth.executeBeanMethods();
 * </pre>
 * 
 * You can exclude specific bean properties from the test by calling the method
 * <code>excludeProperty(String)</code>. All properties not specified are tested. One
 * exception is made: The method getClass() is explicitly excluded from any tests 
 * whatsoever.
 * <pre>
 * BeanMEthodTestHelper bmth;
 * bmth = new BeanMethodTestHelper(oUT);
 * bmth.excludeProperty("untestedProperty");
 * bmth.executeBeanMethods();
 * </pre>
 * 
 * Alternatively you can create the BeanMEthodTestHelper with a second parameter, that
 * changes the test mode to a mode in which only those properties are tested that are
 * registered using the method <code>includeProperty(String)</code>.
 * <pre>
 * BeanMEthodTestHelper bmth;
 * bmth = new BeanMethodTestHelper(oUT, BeanMethodTestHelper.TestMode.Include);
 * bmth.includeProperty("testedProperty");
 * bmth.executeBeanMethods();
 * </pre>
 * 
 * This code is available under the conditions of the ASF license as specified
 * under the following link: 
 * <pre>http://www.apache.org/licenses/</pre>
 * 
 * The newest version of this  class can be found under the following url:
 * 
 * <pre>http://www.xinaris.de</pre>
 * 
 * The idea to this class has been taken from Steven Grimm who wrote a class
 * providing something similar. Since I thought I could do this better, I set 
 * out to do so. What the resulting class provides is something different, and not
 * something better, but it is clearly a showcase for arrogance :-)
 * 
 * @author Joachim Baumann
 * @version 1.0
 */
public class BeanMethodTestHelper {

    public static enum TestMode {
	Exclude, Include
    }

    private Object oUT;

    private transient BeanInfo info;

    private boolean exclusionMode;

    private final Map<String, String> propertyMap = new HashMap<String, String>();

    private static final Map<String, Object> primitiveValues = new HashMap<String, Object>();
    static {
	primitiveValues.put("boolean", Boolean.FALSE); //$NON-NLS-1$
	primitiveValues.put("byte", Byte.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put("char", new Character((char) 0)); //$NON-NLS-1$
	primitiveValues.put("short", Short.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put("int", Integer.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put("long", Long.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put("float", Float.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put("double", Double.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	// add some of the basic types
	primitiveValues.put(Boolean.class.getName(), Boolean.FALSE); //$NON-NLS-1$
	primitiveValues.put(Byte.class.getName(), Byte.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put(Character.class.getName(), new Character((char) 0)); //$NON-NLS-1$
	primitiveValues.put(Short.class.getName(), Short.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put(Integer.class.getName(), Integer.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put(Long.class.getName(), Long.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put(Float.class.getName(), Float.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$
	primitiveValues.put(Double.class.getName(), Double.valueOf("0")); //$NON-NLS-1$ //$NON-NLS-2$

	primitiveValues.put(String.class.getName(), ""); //$NON-NLS-1$
	primitiveValues.put(BigInteger.class.getName(), BigInteger.ONE); //$NON-NLS-1$
	primitiveValues.put(BigDecimal.class.getName(), BigDecimal.ONE); //$NON-NLS-1$
    }

    /**
     * The constructor takes as first argument the object under test for
     * which the bean methods are to be executed. The second argument is
     * either the constant EXCLUDE or INCLUDE. If EXCLUDE is chosen, then
     * every property not in the list is tested. If INCLUDE is chosen, only
     * those properties in the list are tested.
     * 
     * @param oUT
     * @param testMode
     */
    @SuppressWarnings("static-access")
    public BeanMethodTestHelper(Object oUT, TestMode testMode) {
	if (oUT == null) {
	    throw new IllegalArgumentException("Object under test must not be null");	    
	}

	this.oUT = oUT;

	try {
	    info = Introspector.getBeanInfo(oUT.getClass());
	} catch (IntrospectionException e) {
	    throw new IllegalArgumentException(e); // cannot test this
	}
	exclusionMode = testMode == testMode.Exclude;
	if (exclusionMode) {
	    propertyMap.put("class", "class");	    
	}
    }

    /**
     * The constructor takes as an argument the object under test for which
     * the bean methods are to be executed. The modus when using this
     * constructor is EXCLUDE mode i.e., every property not in the property
     * list is tested.
     * 
     * @param oUT
     * @param testMode
     */
    public BeanMethodTestHelper(Object oUT) {
	this(oUT, TestMode.Exclude);
    }

    /**
     * Exclude a property from the read and write tests
     * 
     * @param property
     */
    public void excludeProperty(final String property) {
	if (!exclusionMode) {
	    throw new IllegalArgumentException(
	    "Cannot accept exclusion properties in INCLUDE mode");	    
	}
	propertyMap.put(property, property);

    }

    /**
     * Include a property in the read and write tests
     * 
     * @param property
     */
    public void includeProperty(final String property) {
	if (exclusionMode) {
	    throw new IllegalArgumentException(
		    "Cannot accept exclusion properties in INCLUDE mode");
	}
	propertyMap.put(property, property);
    }

    /**
     * Execute the tests
     * 
     */
    public void executeBeanMethods() {

	final PropertyDescriptor[] properties = info.getPropertyDescriptors();
	Object [] objArray = new Object[1];
	
	for (PropertyDescriptor pd : properties) {
	    if ((propertyMap.get(pd.getName()) == null) == exclusionMode) {
		// The set method
		Method method = pd.getWriteMethod();
		objArray[0] = createParam(method.getParameterTypes()[0]);
		try {
		    method.invoke(oUT, objArray);
		} catch (InvocationTargetException e) {
		    // Throw the original exception to allow the unit test to react to it
		    throw (RuntimeException)e.getTargetException();
		} catch (IllegalAccessException e) { // NOPMD
		    // This cannot happen; we only test properties
		}		
		// the get method
		method = pd.getReadMethod();
		try {
		    method.invoke(oUT, (Object[]) null);
		} catch (InvocationTargetException e) {
		    // Throw the original exception to allow the unit test to react to it
		    throw (RuntimeException)e.getTargetException();
		} catch (IllegalAccessException e) { // NOPMD
		    // This cannot happen; we only test properties
		}
	    }
	}
    }
    
    /**
     * Create an object of the needed parameter class for the setter
     * @param clazz
     * @return parameter object
     */
    private Object createParam(final Class<?> clazz) {
	Object res = null;

	//***JDK1.5***
	if (clazz.isEnum()) {
	    try {
		// get all values of this enum definition
		final Method values = clazz.getMethod("values", new Class[0]);
		final Object[] allValues = (Object[]) values.invoke(null, new Object[0]);
		// simply take the first enum value
		res = allValues[0];
	    } catch (Exception e) {
		// should not happen
		throw new RuntimeException(e); //NOPMD
	    }
	    return res;
	}
	//***JDK1.5***
	
	// try to identify a primitive or system type
	res = primitiveValues.get(clazz.getName());

	if (res != null) {
	    return res;
	}

	// in the case of an array just return an empty array of same type
	if (clazz.isArray()) {
	    return java.lang.reflect.Array.newInstance(clazz.getComponentType(), 0);
	}
	res = EasyMock.createMock(clazz);
	return res;
    }

    /**
     * Bean Method
     * 
     * @return the oUT
     */
    public Object getOUT() {
	return oUT;
    }

    /**
     * Bean Method
     * 
     * @param out
     *                the oUT to set
     */
    public void setOUT(final Object out) {
	oUT = out;
    }

    /**
     * @return the exclusionMode
     */
    public boolean isExclusionMode() {
        return exclusionMode;
    }

}
