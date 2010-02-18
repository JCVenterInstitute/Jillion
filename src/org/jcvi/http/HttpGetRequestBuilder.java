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
 * Created on Sep 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;



public class HttpGetRequestBuilder  {

    private final StringBuilder urlBuilder = new StringBuilder();
    int numberOfProperties =0;
    public HttpGetRequestBuilder(String urlbase){
        urlBuilder.append(urlbase);
    }
    
    public synchronized HttpGetRequestBuilder addVariable(String key, Object value) throws UnsupportedEncodingException{
        if(key ==null){
            throw new NullPointerException("key can not be null");
        }
        if(numberOfProperties ==0){
            urlBuilder.append("?");
        }
        else{
            urlBuilder.append(HttpUtil.VAR_SEPARATOR);
        }
        urlBuilder.append(HttpUtil.urlEncode(key));
        if(value !=null){
            urlBuilder.append(HttpUtil.VALUE_SEPARATOR)
                    .append(HttpUtil.urlEncode(value.toString()));
        }
        numberOfProperties++;
        return this;
    }
    public synchronized HttpGetRequestBuilder addVariable(String key) throws UnsupportedEncodingException{
        return addVariable(key, null);
    }


    public synchronized HttpURLConnection build() throws IOException {
        return (HttpURLConnection)new URL(urlBuilder.toString()).openConnection();
    }
   
}
