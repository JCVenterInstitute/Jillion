package org.jcvi.util;

import org.junit.Assert;

import org.junit.Test;

public class TestStringUtilities {

    @Test
    public void testToCamelCase() 
    {
        Assert.assertEquals("Standard Title", "thisIsCamelCase", StringUtilities.toCamelCase("This is Camel Case").toString());
        Assert.assertEquals("Single Word", "test", StringUtilities.toCamelCase("Test").toString());
        Assert.assertEquals("Leading whitespace", "thisIsATest", StringUtilities.toCamelCase("   This is a Test").toString());
        Assert.assertEquals("Trailing whitespace", "thisIsATest", StringUtilities.toCamelCase("This is a Test   ").toString());
        Assert.assertEquals("Digits", "digit89Test", StringUtilities.toCamelCase("digit 89 test").toString());
        Assert.assertEquals("Standard Title (InitialCap)", "ThisIsCamelCase", StringUtilities.toCamelCase("This is Camel Case", true).toString());
        Assert.assertEquals("Leading whitespace (InitialCap)", "ThisIsATest", StringUtilities.toCamelCase("   This is a Test", true).toString());
        Assert.assertEquals("Trailing whitespace (InitialCap)", "ThisIsATest", StringUtilities.toCamelCase("This is a Test   ", true).toString());
    }

}
