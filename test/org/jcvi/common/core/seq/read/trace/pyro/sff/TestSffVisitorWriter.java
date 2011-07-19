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
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFDecoderException;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileVisitor;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffParser;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffVisitorWriter;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSffVisitorWriter {

    ResourceFileServer resources = new ResourceFileServer(TestSffVisitorWriter.class);
    
    
    @Test
    public void write() throws IOException, SFFDecoderException{
        InputStream in = resources.getFileAsStream("files/5readExample.sff");
        byte[] expectedBytes =IOUtil.readStreamAsBytes(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        SffFileVisitor sut = new SffVisitorWriter(out);
        SffParser.parseSFF(new ByteArrayInputStream(expectedBytes), sut);

        final byte[] actualBytes = out.toByteArray();
        //must do a sub array because real sff has extra metadata at the end
        //which isn't documented
        assertArrayEquals(Arrays.copyOf(expectedBytes, actualBytes.length), actualBytes);
    }
}
