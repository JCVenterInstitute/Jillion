package org.jcvi.glyph.encoder;

import org.jcvi.testUtil.AbstractThrowableTest;


public class TestIllegalEncodedValueException extends AbstractThrowableTest<IllegalEncodedValueException> {

    

    @Override
    protected IllegalEncodedValueException createThrowable(String message) {
        return new IllegalEncodedValueException(message);
    }

    @Override
    protected IllegalEncodedValueException createThrowable(String message, Throwable cause) {
        return new IllegalEncodedValueException(message,cause);
    }

}
