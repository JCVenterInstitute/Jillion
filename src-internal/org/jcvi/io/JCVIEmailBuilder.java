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

package org.jcvi.io;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.jcvi.common.net.EmailBuilder;

/**
 * {@code JCVIEmailBuilder} is an implementation of 
 * {@link EmailBuilder} which sets the email
 * exchange server to JCVI's exchange server.
 * 
 * @author dkatzel
 *
 *
 */
public class JCVIEmailBuilder extends EmailBuilder {
    private static final String JCVI_EMAIL_SERVER = "exchange.jcvi.org";
    static final Properties PROPS = new Properties();
    static{
        PROPS.put("mail.smtp.host",JCVI_EMAIL_SERVER);
    }
    /**
     * Creates a new {@link JCVIEmailBuilder}.
     * @param from the e-mail address to say the email is from.
     * @throws AddressException if the email is not a valid form
     * @throws MessagingException
     */
    public JCVIEmailBuilder(String from)
            throws AddressException, MessagingException {
        super(JCVI_EMAIL_SERVER, from);
        
    }

}
