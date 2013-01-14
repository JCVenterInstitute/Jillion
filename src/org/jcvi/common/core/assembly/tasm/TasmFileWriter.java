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

package org.jcvi.common.core.assembly.tasm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code TasmFileWriter} writes out TIGR Assembler
 * formated files (.tasm).  This assembly format 
 * probably does not have much use outside of 
 * JCVI since the format is specially tailored to the 
 * legacy TIGR Project Database.
 * @author dkatzel
 *
 */
public final class TasmFileWriter {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final byte[] BLANK_LINE = "\n".getBytes(UTF_8);
	private static final byte[] CONTIG_SEPARATOR = "|\n".getBytes(UTF_8);
	private static final String EOL = "\n";
	private TasmFileWriter(){
		//can not instantiate 
	}
	public static void writeContigSeparator(OutputStream out) throws IOException{
	    out.write(CONTIG_SEPARATOR);
	}
	public static void write(TasmContigDataStore datastore, OutputStream out) throws IOException{
		if(datastore==null){
			throw new NullPointerException("data store can not be null");
		}
		Iterator<String> contigIds;
		try {
			contigIds = datastore.idIterator();
			
			while(contigIds.hasNext()){
				TasmContig contig =datastore.get(contigIds.next());
				write(contig,out);
				if(contigIds.hasNext()){
					out.write(CONTIG_SEPARATOR);
				}
			}
		} catch (DataStoreException e) {
			throw new IOException("error writing tasm file",e);
		}
		
	}

	public static void write(TasmContig contig, OutputStream out) throws IOException {
		for(TasmContigAttribute contigAttribute : TasmContigAttribute.values()){
		    if(contig.hasAttribute(contigAttribute)){
    		    String assemblyTableColumn = contigAttribute.getAssemblyTableColumn();
    			StringBuilder row = new StringBuilder(assemblyTableColumn);
    			int padding = 4-assemblyTableColumn.length()%4;
    			if(padding>0){
    				row.append('\t');
    			}
    				row.append(String.format("%s%s", 
    						contig.getAttributeValue(contigAttribute),
    						EOL));
				out.write(row.toString().getBytes(UTF_8));
			}
			
		}
		if(contig.getNumberOfReads()>0){
    		out.write(BLANK_LINE);
    		
    		StreamingIterator<TasmAssembledRead> placedReadIterator=null;
    		try{
	    		placedReadIterator= contig.getReadIterator();
	    		while(placedReadIterator.hasNext()){
	    			TasmAssembledRead read = placedReadIterator.next();
	    			for(TasmReadAttribute readAttribute : TasmReadAttribute.values()){
	    				String assemblyTableColumn = readAttribute.getAssemblyTableColumn();
	    				int padding = 4-assemblyTableColumn.length()%4;
	    				StringBuilder row = new StringBuilder(assemblyTableColumn);
	    				if(padding>0){
	    					row.append('\t');
	    				}
	    				if(read.hasAttribute(readAttribute)){
	    					row.append(String.format("%s%s", 
	    							read.getAttributeValue(readAttribute),
	    							EOL));
	    				}else{
	    					row.append(EOL);
	    				}
	    				
	    				out.write(row.toString().getBytes(UTF_8));				
	    			}
	    			if(placedReadIterator.hasNext()){
	    				out.write(BLANK_LINE);
	    			}
	    		}
    		}finally{
    			IOUtil.closeAndIgnoreErrors(placedReadIterator);
    		}
		}
		
	}
}
