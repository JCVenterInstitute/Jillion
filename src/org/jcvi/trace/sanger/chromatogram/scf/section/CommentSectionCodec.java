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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Map.Entry;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;


public class CommentSectionCodec implements SectionCodec {

    private static final String NULL ="\0";
    @Override
    public long decode(DataInputStream in,long currentOffset, SCFHeader header, SCFChromatogramBuilder c)
            throws SectionDecoderException {
        long bytesToSkip = header.getCommentOffset() - currentOffset;
        try {
            IOUtil.blockingSkip(in,bytesToSkip);
            byte[] comments = new byte[header.getCommentSize()];
            int bytesRead = IOUtil.blockingRead(in,comments, 0, comments.length);
            if(bytesRead != comments.length){
                throw new SectionDecoderException("could not read entire comment section");
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(
                        new ByteArrayInputStream(comments)));
            //SCF has a \0 at the end of the comment section
            //java will interpret this as an extra property
            //remove it
            props.remove(NULL);
            c.properties(props);
            return currentOffset+bytesToSkip+comments.length;
        } catch (IOException e) {
            throw new SectionDecoderException("error parsing Comment",e);
        }

    }

    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {
        Properties props =c.getProperties();
        if(props ==null){
            header.setCommentSize(0);
            return new EncodedSection(null,Section.COMMENTS);
        }
        StringBuilder builder = new StringBuilder();
        for(Entry<Object, Object> entry :props.entrySet()){
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.append(NULL);
        ByteBuffer buffer = ByteBuffer.wrap(builder.toString().getBytes());
        header.setCommentSize(builder.length());
        return new EncodedSection(buffer,Section.COMMENTS);
    }

}
