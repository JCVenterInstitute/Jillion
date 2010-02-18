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
 * Created on Jan 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.InputStream;

import java.net.URL;

public final class NCBIUtil {

    private NCBIUtil(){}
    
    
    public static InputStream getResponseFromNcbi(String url)
                                            throws Exception {
        int tries=0;
        while(true){
            try{
              //sleep required between calls to ncbi 
                Thread.sleep(3000);
                return new URL(url).openStream();
            }catch(Exception e){
                //error from ncbi try again
                System.out.println("tries..."+ tries);
                tries++;
                if(tries >4){
                    throw e;
                }
            }
        }
    }
}
