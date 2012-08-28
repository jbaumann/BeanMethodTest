package de.xinaris.junit;

import java.math.BigInteger;
import java.util.Map;

public class TestBean {
    public static enum TestEnum {
	Red
    }
    public static enum NoEnum {
    }
    
    private boolean booleanProp;
    private int intProp;
    private String string;
    private BigInteger bigInteger;
    private Map<String, String> map;
    private int [] intArray;
    private TestEnum color;
    private NoEnum nothing;
    private float exceptionWhenSet;
    
    public String getString() {
	return string;
    }

    public void setString(final String input) {
	this.string = input;
    }

    public BigInteger getBigInteger() {
	return bigInteger;
    }

    public void setBigInteger(final BigInteger bigInt) {
	this.bigInteger = bigInt;
    }

    public boolean isBooleanProp() {
	return booleanProp;
    }

    public void setBooleanProp(final boolean bProp) {
	this.booleanProp = bProp;
    }

    public int getIntProp() {
	return intProp;
    }

    public void setIntProp(final int intProp) {
	this.intProp = intProp;
    }

    /**
     * @return the map
     */
    public Map<String, String> getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(final Map<String, String> map) {
        this.map = map;
    }

    /**
     * @return the intArray
     */
    public int[] getIntArray() {
        return intArray.clone();
    }

    /**
     * @param intArray the intArray to set
     */
    public void setIntArray(final int[] intArray) {
        this.intArray = intArray.clone();
    }

    /**
     * @return the color
     */
    public TestEnum getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(final TestEnum color) {
        this.color = color;
    }

    /**
     * @return the dead
     */
    public float getExceptionWithGet() {
	throw new IllegalArgumentException("getException");
    }

    /**
     * @param dead the dead to set
     */
    public void setExceptionWithGet(final float dead) {
	@SuppressWarnings("unused")
	float ignoreMe = dead; 
    }

    /**
     * @return the setException
     */
    public float getExceptionWhenSet() {
        return exceptionWhenSet;
    }

    /**
     * @param setException the setException to set
     */
    public void setExceptionWhenSet(final float setException) {
        this.exceptionWhenSet = setException;
	throw new IllegalArgumentException("setException");
    }

    /**
     * @return the nothing
     */
    public NoEnum getNothing() {
        return nothing;
    }

    /**
     * @param nothing the nothing to set
     */
    public void setNothing(NoEnum nothing) {
        this.nothing = nothing;
    }
}
