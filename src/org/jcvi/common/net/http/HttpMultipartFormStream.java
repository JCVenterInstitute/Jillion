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

package org.jcvi.common.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * An <code>HttpMultipartFormStream</code> represents a stream of data being
 * written to an HTTP 1.1 POST request using a multi-part MIME Encoding.
 * <p>
 * This should be used to collect and format a multi-part request body.  The 
 * JRE doesn't supply any methods or classes to aid in doing this, but this 
 * class should be sufficient for quick and simple needs.  This class performs
 * all the necessary encoding and supplies a number of methods for posting
 * variables, long data, and even standards-compliant file uploads.
 *
 * @author jsitz@jcvi.org
 */
public class HttpMultipartFormStream
{
    /** The size of the buffer to use when copying file data. */
    private static final int TRANSFER_BUFFER_SIZE = 4096;
    
    /** The standard string used to bracket a multi-part separator line */
    private static final String SEPARATOR_MARK = "--";

    /** The padding string which starts the multi-part separator. */
    private static final String SEPARATOR_PAD = "--------------------------";
    
    private static final Random RANDOM_GENERATOR =new Random();
    /**
     * Generates a multi-part separator string.  This string consists of the
     * {@link #SEPARATOR_PAD} followed by a string of random characters.  This
     * separator is placed between MIME parts in the request body.
     * 
     * @return A non-bracketed separator string.
     */
    private static String generateSeparator()
    {
        return HttpMultipartFormStream.SEPARATOR_PAD + RANDOM_GENERATOR.nextLong();
    }

    /** The character encoding to use for text elements in the request body. */
    private static final Charset ASCII = Charset.forName("ASCII");

    /** The standard line termination sequence. */
    private static final byte[] CRLF =  {0x0D, 0x0A };

    /** The {@link OutputStream} to write the data to. */
    private final OutputStream out;
    
    /** The separator string to place between parts. */
    private final String separator;

    /**
     * Creates a new <code>HttpMultipartFormStream</code> attached to the ouput
     * of the given {@link URLConnection}.  When this constructor is used, the
     * {@link URLConnection} is initialized to use the correct content encoding
     * settings.
     * 
     * @param connection The connection to attach to.
     * @throws IOException If there is an error writing the data.
     */
    public HttpMultipartFormStream(URLConnection connection) throws IOException
    {
        super();

        this.separator = HttpMultipartFormStream.generateSeparator();

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.separator);

        this.out = connection.getOutputStream();

        this.initStream();
    }

    /**
     * Creates a new <code>HttpMultipartFormStream</code> attached to the 
     * given {@link OutputStream}.  This constructor is suitable for capturing
     * the encoded string either for secondary processing (by using something
     * like a {@link ByteArrayOutputStream}) or for debugging.
     * 
     * @param out The {@link OutputStream} to write the encoded data to.
     * @throws IOException If there is an error writing the data.
     */
    public HttpMultipartFormStream(OutputStream out) throws IOException
    {
        super();

        this.separator = HttpMultipartFormStream.generateSeparator();

        this.out = out;

        this.initStream();
    }

    /**
     * Performs any necessary initialization to the output stream.
     * 
     * @throws IOException If there is an error setting up the stream.
     */
    private void initStream() throws IOException
    {
        // Nothing to do here at the moment
    }

    /**
     * Writes a string to the output stream.  The string is encoded as US-ASCII.
     * 
     * @param s The {@link String} to write.
     * @throws IOException If there is an error writing to the output stream.
     */
    protected void writeString(String s) throws IOException
    {
        this.out.write(s.getBytes(HttpMultipartFormStream.ASCII));
    }

    /**
     * Writes the line termination characters to the output.  Whenever possible,
     * this method should be preferred to <code>writeString("\n")</code> as
     * the Java line terminator is operating system specific.
     * 
     * @throws IOException If there is an error writing to the output stream.
     */
    protected void writeCRLF() throws IOException
    {
        this.out.write(HttpMultipartFormStream.CRLF);
    }

    /**
     * Writes a multi-part MIME separator to the stream.  This needs to be done
     * between each part, but in nearly all cases, it should be handled 
     * automatically for you by {@link #writePartHeader(String, String[])} which
     * is called at the start of each of the public API methods.
     * 
     * @throws IOException If there is an error writing to the output stream.
     */
    protected void writeSeparator() throws IOException
    {
        this.writeString(HttpMultipartFormStream.SEPARATOR_MARK);
        this.writeString(this.separator);
        this.writeCRLF();
    }

    /**
     * Writes a multi-part MIME terminator to the stream.  This will signal the
     * end of the multi-part body.  This is done automatically as part of the 
     * {@link #close()} method.
     * 
     * @throws IOException If there is an error writing to the output stream.
     */
    protected void writeTerminator() throws IOException
    {
        this.writeString(HttpMultipartFormStream.SEPARATOR_MARK);
        this.writeString(this.separator);
        this.writeString(HttpMultipartFormStream.SEPARATOR_MARK);
        this.writeCRLF();
    }

    /**
     * Writes a standards-compliant file-upload record to the output stream.
     * This will put the raw contents of the {@link InputStream} into the 
     * request body along with a record name and suggested filename.  This
     * filename is commonly used to determine the type of file data that has
     * been provided. 
     * 
     * @param name The string identifier for this record.
     * @param filename The filename to attach to the inserted data.
     * @param data An open {@link InputStream} to read data from.
     * @throws IOException If there is an error writing to the output stream or 
     * reading from the input stream.
     */
    public void writeFile(String name, String filename, InputStream data) throws IOException
    {
        this.writePartHeader(name, new String[] {"filename", filename });
        this.writeString("Content-Type: application/octet-stream");
        this.writeCRLF();
        this.writeCRLF();
        
        final byte[] buffer = new byte[HttpMultipartFormStream.TRANSFER_BUFFER_SIZE];
        boolean done = false;
        while (!done)
        {
            final int bytesRead = data.read(buffer);
            if (bytesRead > 0)
            {
                this.out.write(buffer, 0, bytesRead);
            }
            else{
                done = true;
            }
        }
        this.writeCRLF();
    }

    /**
     * Writes a standard CGI-style variable to the output stream.  This is the
     * standard way of sending name/value pairs in the request body.  The 
     * name and value should not contain any " characters.
     * 
     * @param var The name of the variable.
     * @param value The value of the variable.
     * @throws IOException If there is an error writing to the output stream.
     */
    public void writeVariable(String var, String value) throws IOException
    {
        this.writePartHeader(var);
        this.writeCRLF();
        this.writeString(value);
        this.writeCRLF();
    }
    
    /**
     * Writes a part header to the output stream.  This contains both the 
     * separator string and a <code>Content-Disposition</code> subheader
     * declaring the part name and any additional fields.  The additional 
     * fields are supplied as a single list with alternating fields starting
     * with the field name and followed by that field's data.
     * 
     * @param partName The name of the part.
     * @param fields Additional content fields, in pairs.
     * @throws IOException
     */
    private void writePartHeader(String partName, String[] ... fields) throws IOException
    {
        this.writeSeparator();
        this.writeString("Content-Disposition: form-data; name=" + this.quoted(partName));
        
        for (final String[] field : fields)
        {
            this.writeString("; " + field[0] + "=" + this.quoted(field[1]));
        }
        this.writeCRLF();
    }
    
    /**
     * Quotes a string for use in part headers.
     * <p>
     * For now, this simply wraps the string in " characters.  This isn't as
     * thorough as it could be, but it will handle the vast majority of cases.
     * 
     * @param s The {@link String} to quote.
     * @return A quoted version of the string.
     */
    private String quoted(String s)
    {
        return new StringBuilder().append('"').append(s).append('"').toString();
    }

    /**
     * Terminates the multi-part body and signals the output stream that there 
     * is no further data to be written.  In the case where the output stream
     * is connected to a {@link URLConnection}, this may trigger further events
     * on the connection.
     * 
     * @throws IOException If there is an error writing to the output stream.
     */
    public void close() throws IOException
    {
        this.writeTerminator();

        this.out.flush();

        if (this.out != System.out)
        {
            this.out.close();
        }
    }
}
