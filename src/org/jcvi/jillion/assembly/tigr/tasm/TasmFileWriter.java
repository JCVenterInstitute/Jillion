/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
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
		StreamingIterator<TasmContig> iter=null;
		try {
			iter = datastore.iterator();
			
			while(iter.hasNext()){
				TasmContig contig =iter.next();
				write(contig,out);
				if(iter.hasNext()){
					out.write(CONTIG_SEPARATOR);
				}
			}
		} catch (DataStoreException e) {
			throw new IOException("error writing tasm file",e);
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		
	}

	public static void write(TasmContig contig, OutputStream out) throws IOException {
		Map<TasmContigAttribute, String> currentContigAttributes = createContigAttributes(contig);
		for(TasmContigAttribute contigAttribute : TasmContigAttribute.values()){
		    if(currentContigAttributes.containsKey(contigAttribute)){
    		    String assemblyTableColumn = contigAttribute.getAssemblyTableColumn();
    			StringBuilder row = new StringBuilder(assemblyTableColumn);
    			int padding = 4-assemblyTableColumn.length()%4;
    			if(padding>0){
    				row.append('\t');
    			}
    				row.append(String.format("%s%s", 
    						currentContigAttributes.get(contigAttribute),
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
	    			Map<TasmReadAttribute, String> currentAttributes = createReadAttributes(read);
	    			for(TasmReadAttribute readAttribute : TasmReadAttribute.values()){
	    				String assemblyTableColumn = readAttribute.getAssemblyTableColumn();
	    				int padding = 4-assemblyTableColumn.length()%4;
	    				StringBuilder row = new StringBuilder(assemblyTableColumn);
	    				if(padding>0){
	    					row.append('\t');
	    				}
	    				if(currentAttributes.containsKey(readAttribute)){
	    					row.append(String.format("%s%s", 
	    							currentAttributes.get(readAttribute),
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
	
	private static Map<TasmReadAttribute, String> createReadAttributes(TasmAssembledRead read){
		Map<TasmReadAttribute, String> map = new EnumMap<TasmReadAttribute, String>(TasmReadAttribute.class);
		map.put(TasmReadAttribute.NAME, read.getId());
		map.put(TasmReadAttribute.CONTIG_START_OFFSET, Long.toString(read.getGappedStartOffset()));
		ReferenceMappedNucleotideSequence gappedSequence = read.getNucleotideSequence();
		map.put(TasmReadAttribute.GAPPED_SEQUENCE, gappedSequence.toString());
		
		Range validRange = read.getReadInfo().getValidRange();
		//seq_lend and seq_rend are swapped if seq is reversed.
		if(read.getDirection() == Direction.FORWARD){
			map.put(TasmReadAttribute.SEQUENCE_LEFT, Long.toString(validRange.getBegin(CoordinateSystem.RESIDUE_BASED)));
			map.put(TasmReadAttribute.SEQUENCE_RIGHT, Long.toString(validRange.getEnd(CoordinateSystem.RESIDUE_BASED)));
		}else{
			map.put(TasmReadAttribute.SEQUENCE_RIGHT, Long.toString(validRange.getBegin(CoordinateSystem.RESIDUE_BASED)));
			map.put(TasmReadAttribute.SEQUENCE_LEFT, Long.toString(validRange.getEnd(CoordinateSystem.RESIDUE_BASED)));
		
		}
		NucleotideSequence consensus = gappedSequence.getReferenceSequence();
		map.put(TasmReadAttribute.CONTIG_LEFT, Long.toString(1 + consensus.getUngappedOffsetFor((int)read.getGappedStartOffset())));
		map.put(TasmReadAttribute.CONTIG_RIGHT, Long.toString(1 + consensus.getUngappedOffsetFor((int)read.getGappedEndOffset())));
	
		return map;
	}
	
	protected static long getNumberOfReads(TasmContig contig){
		return contig.getNumberOfReads();
	}
	
	protected static double getAvgCoverage(TasmContig contig){
		return contig.getAvgCoverage();
	}
	
	protected static String getEditPerson(TasmContig contig){
		return contig.getEditPerson();
	}
	
	protected static Date getEditDate(TasmContig contig){
		return contig.getEditDate();
	}
	
	private static Map<TasmContigAttribute, String> createContigAttributes(TasmContig contig){
		Map<TasmContigAttribute, String> map = new EnumMap<TasmContigAttribute, String>(TasmContigAttribute.class);
		NucleotideSequenceBuilder nucleotideSequenceBuilder = new NucleotideSequenceBuilder(contig.getConsensusSequence());
		
		double numNs = nucleotideSequenceBuilder.getNumNs();
		map.put(TasmContigAttribute.PERCENT_N, String.format("%.2f", numNs/nucleotideSequenceBuilder.getLength()));
		map.put(TasmContigAttribute.UNGAPPED_CONSENSUS, nucleotideSequenceBuilder
																.ungap()
																.toString());
		map.put(TasmContigAttribute.GAPPED_CONSENSUS, contig.getConsensusSequence().toString());
		map.put(TasmContigAttribute.NUMBER_OF_READS, Long.toString(getNumberOfReads(contig)));
		map.put(TasmContigAttribute.AVG_COVERAGE, String.format("%.2f", getAvgCoverage(contig)));	
		
		
		map.put(TasmContigAttribute.EDIT_PERSON, getEditPerson(contig));
		map.put(TasmContigAttribute.EDIT_DATE, TasmUtil.formatEditDate(getEditDate(contig)));
		map.put(TasmContigAttribute.IS_CIRCULAR, contig.isCircular()? "1":"0");
		
		putOptionalValue(map, TasmContigAttribute.BAC_ID,contig.getSampleId());
		putOptionalValue(map, TasmContigAttribute.ASMBL_ID,contig.getTigrProjectAssemblyId());
		putOptionalValue(map, TasmContigAttribute.CA_CONTIG_ID,contig.getCeleraAssemblerId());
		putOptionalValue(map, TasmContigAttribute.COMMENT,contig.getComment());
		putOptionalValue(map, TasmContigAttribute.COM_NAME,contig.getCommonName());
		putOptionalValue(map, TasmContigAttribute.METHOD,contig.getAssemblyMethod());
		
		return map;
	}
	private static void putOptionalValue(Map<TasmContigAttribute, String> map, TasmContigAttribute attribute, Object value){
		if(value !=null){
			map.put(attribute, value.toString());
		}
	}
}
