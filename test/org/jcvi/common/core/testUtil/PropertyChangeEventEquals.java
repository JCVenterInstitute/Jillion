/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Apr 17, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.testUtil;


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

