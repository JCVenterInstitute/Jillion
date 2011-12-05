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
import java.util.Iterator;

import org.jcvi.common.core.datastore.DataStoreException;
/**
 * {@code TigrAssemblerWriter} writes out TIGR Assembler
 * formated files (.tasm).  This assembly format 
 * probably does not have much use outside of 
 * JCVI since the format is specially tailored to the 
 * legacy TIGR Project Database.
 * @author dkatzel
 *
 */
public final class TigrAssemblerWriter {

	private static final byte[] BLANK_LINE = "\n".getBytes();
	private static final byte[] CONTIG_SEPARATOR = "|\n".getBytes();
	
	public static void writeContigSeparator(OutputStream out) throws IOException{
	    out.write(CONTIG_SEPARATOR);
	}
	public static void write(TigrAssemblerContigDataStore datastore, OutputStream out) throws IOException{
		if(datastore==null){
			throw new NullPointerException("data store can not be null");
		}
		Iterator<String> contigIds;
		try {
			contigIds = datastore.getIds();
			
			while(contigIds.hasNext()){
				TigrAssemblerContig contig =datastore.get(contigIds.next());
				write(contig,out);
				if(contigIds.hasNext()){
					out.write(CONTIG_SEPARATOR);
				}
			}
		} catch (DataStoreException e) {
			throw new IOException("error writing tasm file",e);
		}
		
	}

	public static void write(TigrAssemblerContig contig, OutputStream out) throws IOException {
		for(TigrAssemblerContigAttribute contigAttribute : TigrAssemblerContigAttribute.values()){
		    if(contig.hasAttribute(contigAttribute)){
    		    String assemblyTableColumn = contigAttribute.getAssemblyTableColumn();
    			StringBuilder row = new StringBuilder(assemblyTableColumn);
    			int padding = 4-assemblyTableColumn.length()%4;
    			if(padding>0){
    				row.append("\t");
    			}
    				row.append(String.format("%s\n", 
    						contig.getAttributeValue(contigAttribute)));
				out.write(row.toString().getBytes());
			}
			
		}
		if(contig.getNumberOfReads()>0){
    		out.write(BLANK_LINE);
    		
    		Iterator<TigrAssemblerPlacedRead> placedReadIterator = contig.getPlacedReads().iterator();
    		while(placedReadIterator.hasNext()){
    			TigrAssemblerPlacedRead read = placedReadIterator.next();
    			for(TigrAssemblerReadAttribute readAttribute : TigrAssemblerReadAttribute.values()){
    				String assemblyTableColumn = readAttribute.getAssemblyTableColumn();
    				int padding = 4-assemblyTableColumn.length()%4;
    				StringBuilder row = new StringBuilder(assemblyTableColumn);
    				if(padding>0){
    					row.append("\t");
    				}
    				if(read.hasAttribute(readAttribute)){
    					row.append(String.format("%s\n", 
    							read.getAttributeValue(readAttribute)));
    				}else{
    					row.append("\n");
    				}
    				
    				out.write(row.toString().getBytes());				
    			}
    			if(placedReadIterator.hasNext()){
    				out.write(BLANK_LINE);
    			}
    		}
		}
		
	}
}
