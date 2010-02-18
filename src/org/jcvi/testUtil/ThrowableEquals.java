/*
 * Created on Apr 17, 2008
 *
 * @author dkatzel
 */
package org.jcvi.testUtil;

import org.easymock.IArgumentMatcher;
/**
 * <code>ThowableEquals</code> is an implementation
 * of EasyMock's {@link IArgumentMatcher} that will
 * allow EasyMock to correctly handle {@link Throwable}s
 * as expected parameters.
 * This code was taken from the EasyMock documentation
 *
 * @author dkatzel
 * @see <a href="http://easymock.org/EasyMock2_3_Documentation.html">
 * Easy Mock Documentation</a>
 *
 */
public class ThrowableEquals implements IArgumentMatcher {
    private Throwable expected;

    public ThrowableEquals(Throwable expected) {
        this.expected = expected;
    }

    public boolean matches(Object actual) {
        if (!(actual instanceof Throwable)) {
            return false;
        }
        String actualMessage = ((Throwable) actual).getMessage();
        return expected.getClass().equals(actual.getClass())
                && expected.getMessage().equals(actualMessage);
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("eqException(");
        buffer.append(expected.getClass().getName());
        buffer.append(" with message \"");
        buffer.append(expected.getMessage());
        buffer.append("\")");

    }
}
