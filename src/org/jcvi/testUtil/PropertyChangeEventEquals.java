/*
 * Created on Apr 17, 2008
 *
 * @author dkatzel
 */
package org.jcvi.testUtil;


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

