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
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;


public class CommentSectionCodec implements SectionCodec {

    private static final String NULL ="\0";
    @Override
    public long decode(DataInputStream in,long currentOffset, SCFHeader header, SCFChromatogramBuilder c)
            throws SectionDecoderException {
        long bytesToSkip = Math.max(0, header.getCommentOffset() - currentOffset);
        try {
            IOUtil.blockingSkip(in,bytesToSkip);
            byte[] comments = new byte[header.getCommentSize()];
            try{
            	IOUtil.blockingRead(in,comments, 0, comments.length);
            }catch(EOFException e){
            	throw new SectionDecoderException("could not read entire comment section");
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(
                        new ByteArrayInputStream(comments),
                        IOUtil.UTF_8));
            //SCF has a \0 at the end of the comment section
            //java will interpret this as an extra property
            //remove it
            props.remove(NULL);
            Map<String,String> map = new HashMap<String, String>();
            for(Entry<Object,Object> entry : props.entrySet()){
                map.put((String)entry.getKey(), (String) entry.getValue());
            }
            c.properties(map);
            return currentOffset+bytesToSkip+comments.length;
        } catch (IOException e) {
            throw new SectionDecoderException("error parsing Comment",e);
        }

    }

    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {
        Map<String,String> props =c.getComments();
        if(props ==null){
            header.setCommentSize(0);
            return new EncodedSection(null,Section.COMMENTS);
        }
        StringBuilder builder = new StringBuilder();
        for(Entry<String, String> entry :props.entrySet()){
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
            builder.append('\n');
        }
        builder.append(NULL);
        ByteBuffer buffer = ByteBuffer.wrap(builder.toString().getBytes(IOUtil.UTF_8));
        header.setCommentSize(builder.length());
        return new EncodedSection(buffer,Section.COMMENTS);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor c)
            throws SectionDecoderException {
        long bytesToSkip = header.getCommentOffset() - currentOffset;
        try {
            IOUtil.blockingSkip(in,bytesToSkip);
            byte[] comments = new byte[header.getCommentSize()];
            try{
            	IOUtil.blockingRead(in,comments, 0, comments.length);
            }catch(EOFException e){
            	throw new SectionDecoderException("could not read entire comment section");
            }
            
            Properties props = new Properties();
            props.load(new InputStreamReader(
                        new ByteArrayInputStream(comments),
                        IOUtil.UTF_8));
            //SCF has a \0 at the end of the comment section
            //java will interpret this as an extra property
            //remove it
            props.remove(NULL);
            Map<String,String> map = new HashMap<String, String>();
            for(Entry<Object,Object> entry : props.entrySet()){
                map.put((String)entry.getKey(), (String) entry.getValue());
            }
            c.visitComments(map);
            return currentOffset+bytesToSkip+comments.length;
        } catch (IOException e) {
            throw new SectionDecoderException("error parsing Comment",e);
        }
    }

}
