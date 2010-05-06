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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.joda.time.DateTimeUtils;

/**
 * {@code EmailBuilder} is a class that can build and send generic text e-mails
 * with attachments.  This class abstracts the implementation details
 * for building a multipart MIME message. 
 * 
 * @author dkatzel
 *
 *
 */
public class EmailBuilder {

    private List<String> recipients = new ArrayList<String>();
    private List<String> ccs = new ArrayList<String>();
    private List<String> bccs = new ArrayList<String>();
    private Map<String,File> attachments = new HashMap<String,File>();
    private MimeMessage msg;
    private String message;
    /**
     * 
     * @param emailHost
     * @param from
     * @throws AddressException
     * @throws MessagingException
     */
    public EmailBuilder(String emailHost, String from) throws AddressException, MessagingException{
        this(emailHost, from,System.getProperties());
    }
    public EmailBuilder(String emailHost,String from, Properties props) throws AddressException, MessagingException{
        props.put("mail.smtp.host",emailHost);
        Session session = Session.getDefaultInstance(props);
        msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
    }
    /**
     * Set the subject of this email can not contain line breaks ({@code \n or \r} ).
     * @param subject a non-null message, single line string.
     * @return this
     * @throws MessagingException if the text in the subject can not be 
     * encoded in an e-mail.
     * @throws NullPointerException if subject is null.
     */
    public EmailBuilder subject(String subject) throws MessagingException{
        if(subject.contains("\n") || subject.contains("\r")){
            throw new MessagingException("subject contains line breaks");
        }
       msg.setSubject(subject);
        return this;
    }
    /**
     * Set the e-mail message body text.
     * @param message the message body (can not be null).
     * @return this
     * @throws NullPointerException if message is null.
     */
    public EmailBuilder message(String message){
        if(message ==null){
            throw new NullPointerException("message body can not be null");
        }
       this.message = message;
       return this;
    }
    /**
     * Add the given recipient email addresses to the "TO" list.
     * @param recipients email addresses to send this message to.
     * @return this.
     */
    public EmailBuilder addRecipients(String... recipients){
        for(String recipient: recipients){
            this.recipients.add(recipient.trim());
        }
        return this;
    }
    /**
     * Add the given recipient email addresses to the "CC" list.
     * @param recipients email addresses to CC this message to.
     * @return this.
     */
    public EmailBuilder addCCRecipients(String... ccs){
        for(String cc : ccs){
            this.ccs.add(cc.trim());
        }
        
        return this;
    }
    /**
     * Add the given recipient email addresses to the "BCC" list.
     * @param recipients email addresses to BCC this message to.
     * @return this.
     */
    public EmailBuilder addBCCRecipients(String... bccs){
        for(String bcc : bccs){
            this.bccs.add(bcc.trim());
        }
       
        return this;
    }
    /**
     * Attach the given file to the email. This is the same as
     * {@link #attachFile(File, String) attachFile(attachment, attachment.getName())}
     * @param attachment the File to attach.
     * @return this.
     */
    public EmailBuilder attachFile(File attachment){
        return attachFile(attachment, attachment.getName());
    }
    /**
     * Attach the given file to the email and name it with the given filename.
     * @param attachment the File to attach.
     * @param filenameToUse the name to call this attachment in the email.
     * @return this.
     */
    public EmailBuilder attachFile(File attachment, String filenameToUse){
        attachments.put(filenameToUse,attachment);
        return this;
    }
    /**
     * Add given header key value pair to the e-mail.
     * @param key header key.
     * @param value header value.
     * @return this
     * @throws MessagingException if key and value are not ASCII.
     */
    public EmailBuilder addHeader(String key, String value) throws MessagingException{
        msg.addHeader(key, value);
        return this;
    }
    /**
     * Builds and sends the message.
     * @throws MessagingException if there are any problems formatting
     * the email.
     * @throws SendFailedException if the message could not be send to any of the recipients.
     */
    public void send() throws MessagingException{
        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setText(message);
        if(!recipients.isEmpty()){
            msg.addRecipients(Message.RecipientType.TO, createInternetAddressesFor(recipients));
        }
        if(!ccs.isEmpty()){
            msg.addRecipients(Message.RecipientType.CC, createInternetAddressesFor(ccs));
        }
        if(!bccs.isEmpty()){
            msg.addRecipients(Message.RecipientType.BCC, createInternetAddressesFor(bccs));
        }
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(messagePart);
        for(Entry<String, File> entry : attachments.entrySet()){
            MimeBodyPart attachment = new MimeBodyPart();
            FileDataSource datasource = new FileDataSource(entry.getValue());
            attachment.setDataHandler(new DataHandler(datasource));
            attachment.setFileName(entry.getKey());
            mp.addBodyPart(attachment);
        }
        msg.setContent(mp);
        msg.setSentDate(new Date(DateTimeUtils.currentTimeMillis()));
        Transport.send(msg);
    }
    
    private InternetAddress[] createInternetAddressesFor(List<String> addresses) throws AddressException{
        InternetAddress[] array = new InternetAddress[addresses.size()];
        for(int i=0; i<addresses.size(); i++){
            array[i] = new InternetAddress(addresses.get(i));
        }
        return array;
    }
}
