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
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ctg.CtgFileWriter;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestContigFileWriter {
    ByteArrayOutputStream out;
    CtgFileWriter sut;
    static CtgContigDataStore dataStore;
    private static String pathToFile = "files/gcv_23918.contig";
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestContigFileWriter.class);
    
    @BeforeClass
    public static void parseContigs() throws IOException{
       dataStore = DefaultContigFileDataStore.create(RESOURCES.getFile(pathToFile));
    }
    @Before
    public void setup(){
        out = new ByteArrayOutputStream();
        sut = new CtgFileWriter(out);
    }
    
    @Test
    public void write() throws IOException, DataStoreException{
    	StreamingIterator<Contig<AssembledRead>> iter = dataStore.iterator();
    	try{
        while(iter.hasNext()){
        	Contig<AssembledRead> contig = iter.next();
            sut.write(contig);
        }
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
        InputStream inputStream=null;
        try{
	        inputStream= RESOURCES.getFileAsStream(pathToFile);
			byte[] expected =IOUtil.toByteArray(inputStream);
	        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
	        fileOut.write(out.toByteArray());
	        assertEquals(new String(expected), new String(out.toByteArray()));
        }finally{
        	IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

}
