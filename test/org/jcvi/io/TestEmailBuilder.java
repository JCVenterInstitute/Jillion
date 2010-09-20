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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.easymock.IAnswer;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestEmailBuilder {
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestEmailBuilder.class);
    private MimeMessage mockMessage;
    private final static String EMAIL_HOST = "fake.email.host";
    private final InternetAddress larry ;
    private final InternetAddress moe;
    private final InternetAddress curly;
    private final InternetAddress shemp ;
    
    private final String headerKey = "headerKey";
    private final String headerValue = "headerValue";
    
    private final String subject = "NYUK NYUK";
    private final String messageBody = "why I oughta...\nNYUK NYUK NYUK!";
    {
        try {
            larry = new InternetAddress("louis@stooge.com");
        
       moe = new InternetAddress("moses@stooge.com");
        curly = new InternetAddress("jerry@stooge.com");
       shemp = new InternetAddress("sam@stooge.com");
        } catch (AddressException e) {
            throw new IllegalStateException("could not initialize email addresses");
        }
    }
    private class EmailBuilderTestDouble extends EmailBuilder{
        private boolean messageSent=false;
        
       
        /**
         * @param emailHost
         * @param from
         * @throws AddressException
         * @throws MessagingException
         */
        public EmailBuilderTestDouble(String emailHost, String from)
                throws AddressException, MessagingException {
            super(emailHost, from);
        }
        /**
         * Return mock MimeMessage for testing
         */        
        @Override
        protected MimeMessage createMimeMessage(Properties props) {
            return mockMessage;
        }
        /**
         * Doesn't actually send any message,but records
         * that it was supposed to be "sent"
         */
        @Override
        protected void sendEmail(MimeMessage message) throws MessagingException {
            messageSent=true;
        }
        /**
         * Was the message successfully sent?
         * @return
         */
        public boolean wasMessageSent() {
            return messageSent;
        }
        
    }
    
    private static final Date dateTime = new DateTime(1932,4,5,10,25,0,0).toDate();
    
    @BeforeClass
    public static void freezeTime(){
        DateTimeUtils.setCurrentMillisFixed(dateTime.getTime());
    }
    
    @AfterClass
    public static void restoreTime(){
        DateTimeUtils.setCurrentMillisSystem();
    }
    
    @Before
    public void setup(){
        mockMessage = createMock(MimeMessage.class);
    }
    @Test
    public void toCCandBcc() throws AddressException, MessagingException{
        mockMessage.setFrom(larry);
        mockMessage.addRecipients(eq(RecipientType.TO), aryEq(new InternetAddress[]{moe}));
        mockMessage.addRecipients(eq(RecipientType.CC), aryEq(new InternetAddress[]{curly}));
        mockMessage.addRecipients(eq(RecipientType.BCC), aryEq(new InternetAddress[]{shemp}));
        mockMessage.setSubject(subject);
        mockMessage.setSentDate(dateTime);
        mockMessage.addHeader(headerKey, headerValue);
        mockMessage.setContent((Multipart)notNull());
        
        expectLastCall().andAnswer(new IAnswer<Object>() {
            
            @Override
            public Object answer() throws Throwable {
                Multipart multiPart = (Multipart)getCurrentArguments()[0];
                assertEquals(messageBody,multiPart.getBodyPart(0).getContent());
                return null;
            }
        });
        replay(mockMessage);
        EmailBuilderTestDouble sut = new EmailBuilderTestDouble(EMAIL_HOST, larry.getAddress());
        
        sut.addRecipients(moe.getAddress())
                .addCCRecipients(curly.getAddress())
                .addBCCRecipients(shemp.getAddress())
                .subject(subject)
                .message(messageBody)
                .addHeader(headerKey, headerValue)
                .send();
        assertTrue(sut.wasMessageSent());
        verify(mockMessage);
    }
    @Test
    public void attachment() throws AddressException, MessagingException, IOException{
        final File readMe = RESOURCES.getFile("fileServer/files/README.txt");
        mockMessage.setFrom(larry);
        mockMessage.addRecipients(eq(RecipientType.TO), aryEq(new InternetAddress[]{moe}));
        mockMessage.setSubject(subject);
        mockMessage.setSentDate(dateTime);
        mockMessage.setContent((Multipart)notNull());
        expectLastCall().andAnswer(new IAnswer<Object>() {
            
            @Override
            public Object answer() throws Throwable {
                Multipart multiPart = (Multipart)getCurrentArguments()[0];
                assertEquals(messageBody,multiPart.getBodyPart(0).getContent());
                final FileInputStream fileInputStream = new FileInputStream(readMe);
                try{
                assertEquals(
                        IOUtils.toString(fileInputStream), 
                        multiPart.getBodyPart(1).getContent());
                return null;
                }finally{
                    IOUtil.closeAndIgnoreErrors(fileInputStream);
                }
            }
        });
        replay(mockMessage);
        EmailBuilderTestDouble sut = new EmailBuilderTestDouble(EMAIL_HOST, larry.getAddress());
        
        sut.addRecipients(moe.getAddress())
                .attachFile(readMe)
                .subject(subject)
                .message(messageBody)
                .send();
        assertTrue(sut.wasMessageSent());
        verify(mockMessage);
    }
    
    @Test(expected = MessagingException.class)
    public void subjectHasLineFeedShouldThrowError() throws AddressException, MessagingException{
        EmailBuilderTestDouble sut = new EmailBuilderTestDouble(EMAIL_HOST, larry.getAddress());
        sut.subject(messageBody);
    }
    
    @Test(expected = NullPointerException.class)
    public void messageBodyIsNullShouldThrowError() throws AddressException, MessagingException{
        EmailBuilderTestDouble sut = new EmailBuilderTestDouble(EMAIL_HOST, larry.getAddress());
        sut.message(null);
    }
}
