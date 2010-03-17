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

package org.jcvi.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * An <code>HttpPostStream</code> represents a stream of data to be sent 
 * along with an HTTP POST request.  This uses the default method declared by 
 * the CGI 1.1 spec (<code>application/x-www-form-urlencoded</code>).
 *
 * @author jsitz@jcvi.org
 */
public class HttpPostStream
{

    /** The stream to write data to. */
    private final OutputStream out;
    /** The number of variables written to the stream. */
    private final int varCount;

    /** The name of the character encoding used. */
    private final String encodingName;

    /**
     * Creates a new <code>HttpPostStream</code> attached to an existing 
     * {@link URLConnection}.  This will initialize the connection and use the
     * connection's {@link OutputStream} as the destination for all data.
     * 
     * @param connection The {@link URLConnection} to attach to.
     * @param dataCharset The {@link Charset} the data was generated in.
     * @throws IOException If there is an error writing to the stream.
     */
    public HttpPostStream(URLConnection connection, Charset dataCharset) throws IOException
    {
        super();

        this.out = connection.getOutputStream();
        this.encodingName = dataCharset.name();
        this.varCount = 0;
    }
    /**
     * Creates a new <code>HttpPostStream</code> attached to an existing 
     * {@link URLConnection} using the ASCII Charset.  This is equivalent
     * to {@link #HttpPostStream(URLConnection, Charset) new HttpPostStream(connection, HttpUtil.ASCII)}
     * 
     * @param connection The {@link URLConnection} to attach to.
     * @throws IOException If there is an error writing to the stream.
     * @see #HttpPostStream(URLConnection, Charset)
     */
    public HttpPostStream(URLConnection connection) throws IOException{
       this(connection, HttpUtil.ASCII);
    }
    /**
     * Writes a single variable with its optional value to the output stream.
     * The variable will be encoded as necessary.
     * 
     * @param var The name of the variable to write.
     * @param value The value of the variable or <code>null</code> if no 
     * value exists for this variable.
     * @return this.
     * @throws IOException If there is an error writing to the stream.
     */
    public HttpPostStream writeVariable(String var, Object value) throws IOException
    {
        if (this.varCount > 0)
        {
            this.out.write(HttpUtil.VAR_SEPARATOR_BYTES);
        }

        this.writeURLEncoded(var);
        if (value != null)
        {
            this.out.write(HttpUtil.VALUE_SEPARATOR_BYTES);
            this.writeURLEncoded(value.toString());
        }
        return this;
    }
    
    /**
     * Writes a single unvalued variable to the output stream.
     * The variable will be encoded as necessary.  This is actually a simple
     * delegation to {@link #writeVariable(String, String)} with a 
     * <code>null</code> value.
     * 
     * @param var The name of the variable to write.
     * @return this.
     * @throws IOException If there is an error writing to the stream.
     * @see #writeVariable(String, String)
     */
    public HttpPostStream writeVariable(String var) throws IOException
    {
        return this.writeVariable(var, null);
    }

    /**
     * Writes the given string as a URL encoded string.
     * 
     * @param data The data to write.
     * @throws IOException  If there is an error writing to the stream.
     */
    private void writeURLEncoded(String data) throws IOException
    {
        this.out.write(URLEncoder.encode(data, this.encodingName).getBytes(HttpUtil.ASCII));
    }

    /**
     * Closes the output stream and signals that there is no more data to be
     * written.  When attached to a {@link URLConnection}, this may trigger 
     * additional events on the connection.
     * 
     * @throws IOException If there is an error flushing or closing the stream.
     */
    public void close() throws IOException
    {
        this.out.flush();
        this.out.close();
    }
}
