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


import java.beans.PropertyChangeEvent;

import org.easymock.IArgumentMatcher;

 /**
 * <code>PropertyChangeEventEquals</code> is an implementation
 * of EasyMock's {@link IArgumentMatcher} that will
 * allow EasyMock to correctly handle {@link PropertyChangeEvent}s
 * as expected parameters.
 *
 * @author dkatzel
 * @see <a href="http://easymock.org/EasyMock2_3_Documentation.html">
 * Easy Mock Documentation</a>
 *
 */
public class PropertyChangeEventEquals implements IArgumentMatcher{

    private PropertyChangeEvent expected;

    public PropertyChangeEventEquals(PropertyChangeEvent expected){
        this.expected = expected;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("eqPropertyChangeEvent(");
        buffer.append(expected.getClass().getName());
        buffer.append(" with source \"");
        buffer.append(expected.getSource());
        buffer.append("\",");

        buffer.append(" with property \"");
        buffer.append(expected.getPropertyName());
        buffer.append("\",");

        buffer.append(" with new value \"");
        buffer.append(expected.getNewValue());
        buffer.append("\",");

    }

    public boolean matches(Object actual) {
        if (!(actual instanceof PropertyChangeEvent)) {
            return false;
        }
        PropertyChangeEvent actualPropertyChangeEvent = ((PropertyChangeEvent) actual);
        return expected.getClass().equals(actual.getClass())
                && expected.getSource().equals(actualPropertyChangeEvent.getSource())
                && expected.getPropertyName().equals(actualPropertyChangeEvent.getPropertyName())
                && expected.getNewValue().equals(actualPropertyChangeEvent.getNewValue());
    }

}

