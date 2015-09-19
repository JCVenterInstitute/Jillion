/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 17, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.testUtil;

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
