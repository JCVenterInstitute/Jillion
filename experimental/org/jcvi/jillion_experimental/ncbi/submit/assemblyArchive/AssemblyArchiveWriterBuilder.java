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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.util.SliceMap;
import org.jcvi.jillion.assembly.util.SliceMapBuilder;
import org.jcvi.jillion.assembly.util.consensus.ConicConsensusCaller;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.assembly.util.consensus.ConsensusResult;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive.AssemblyArchiveWriter.TraceNameLookup;

public class AssemblyArchiveWriterBuilder {

	private final File outputDirectory;
	private final QualitySequenceDataStore qualityDataStore;
	private final AssemblyArchiveMetaData sampleMetaData;
	
	private AssemblyArchiveFilenameFactory filenamefactory=null;
	private boolean crossLinkContigSubmission=true;
	
	public AssemblyArchiveWriterBuilder(File outputDirectory, 
			QualitySequenceDataStore qualityDataStore,
			AssemblyArchiveMetaData sampleMetaData
			){
		if(outputDirectory==null){
			throw new NullPointerException("output directory can not be null");
		}
		
		if(qualityDataStore==null){
			throw new NullPointerException("quality sequence datastore can not be null");
		}
		if(sampleMetaData==null){
			throw new NullPointerException("sampleMetaData datastore can not be null");
		}
		
		this.outputDirectory = outputDirectory;
		this.qualityDataStore = qualityDataStore;
		this.sampleMetaData = sampleMetaData;
	}
	
	public AssemblyArchiveWriterBuilder writeContigDataToSeparateFiles(AssemblyArchiveFilenameFactory factory){
		filenamefactory= factory;
		return this;
	}
	/**
	 * Should the assembly consensus
	 * be cross linked with the genbank submission's
	 * accession.
	 * The consensus of the assembly is imported from the 
	 * completed genbank submission. 
	 * This is a requirement of the Assembly Archive- 
	 * they only want fully valid and crosslinked contigs uploaded.
	 * If this method is not called, then 
	 * by default cross-linking is set to {@code true}.
	 * @param value if {@code true} then the
	 * ungapped assembly consensus sequence is not specified
	 * in the assembly archive (the gaps will still be written)
	 * and instead a value of "ACCESSION" is used instead
	 * which will tell NCBI to cross link to the genbank accession sequence.
	 * @return this
	 */
	public AssemblyArchiveWriterBuilder crossLinkContigSubmission(boolean value){
		crossLinkContigSubmission = value;
		return this;
	}
	
	public AssemblyArchiveWriter build() throws IOException{
		return new Writer(this);
	}
	
	
	
	private static final class Writer implements AssemblyArchiveWriter{
		
		private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.US);
		
		private final File outputDirectory;
		private final AssemblyArchiveFilenameFactory filenamefactory;
		private final boolean crossLinkContigSubmission;
		private final AssemblyArchiveMetaData sampleMetaData;
		
		private final QualitySequenceDataStore qualityDataStore;
		
		private long numberOfTotalReads=0L;
		private long numberOfTotalReadBases=0L;
		private long numberOfTotalConsensusBases =0L;

		private List<String> contigNames = new ArrayList<String>();
		private List<File> extraFilesCreated = new ArrayList<File>();
		
		private final File tempFile;
		private final java.io.Writer tempOut;
		
		private AssemblyArchiveType overallType = AssemblyArchiveType.NEW;
		
		private static final String EOL = String.format("%n");
		
		public Writer(AssemblyArchiveWriterBuilder builder) throws IOException {
			this.outputDirectory = builder.outputDirectory;
			this.filenamefactory = builder.filenamefactory;
			this.crossLinkContigSubmission = builder.crossLinkContigSubmission;
			this.qualityDataStore = builder.qualityDataStore;
			this.sampleMetaData = builder.sampleMetaData;
			//make directory if it doesn't exist
			IOUtil.mkdirs(outputDirectory);
			
			tempFile = File.createTempFile("asmArchive", null, outputDirectory);
			tempOut = new BufferedWriter(new FileWriter(tempFile));
		}


		@Override
		public void close() throws IOException {
			//number of bases, avg cov, genomic segments ?
			//type for entire archive (can be computed by all types?)
			//close temp
			tempOut.close();
			File xmlFile = writeAssemblyFile();
			File manifestFile =writeManifestFile(xmlFile);	
			
		}


		private File writeManifestFile(File xmlFile) throws IOException {
			File manifestFile = new File(outputDirectory, "MANIFEST");
			PrintWriter writer = new PrintWriter(manifestFile);
			try{
				for(File f : extraFilesCreated){
					writer.printf("%s: %s%n", f.getName(), computeMd5(f));
				}
				writer.printf("%s: %s%n", xmlFile.getName(), computeMd5(xmlFile));
				return manifestFile;
			}finally{
				IOUtil.closeAndIgnoreErrors(writer);
			}
		}

		private String computeMd5(File f) throws IOException{
			final MessageDigest m;
			try {
				m = MessageDigest.getInstance("MD5");				
			} catch (NoSuchAlgorithmException e) {
				//shouldn't happen
				throw new IllegalStateException("error getting MD5 algorithm", e);
			}
			//not sure if we need to reset it
			//each time but can't hurt
			m.reset();
			m.update(IOUtil.toByteArray(f));
			byte[] digest = m.digest();
			String hexString =new BigInteger(1, digest).toString(16);
			return padString(hexString);
		}
		/**
		 * Adds leading 0s to hexString if string length is <32.
		 * @param hexString
		 * @return
		 */
		private String padString(String hexString) {
			int length = hexString.length();
			int padding = 32-length;
			if(padding==0){
				return hexString;
			}
			char[] pads = new char[padding];
			Arrays.fill(pads, '0');
			
			return new StringBuilder(32).append(pads).append(hexString).toString();
		}


		private File writeAssemblyFile()	throws IOException {
			//create main output file
			File xmlFile = new File(outputDirectory, "ASSEMBLY.xml");
			
			OutputStream out = new BufferedOutputStream(new FileOutputStream(xmlFile));
			try{
				double coverage = numberOfTotalReadBases/(double)numberOfTotalConsensusBases;
				String header = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(EOL)
									.append(String.format("<assembly submitter_reference=\"%s\" type=\"%s\">%n", 
															sampleMetaData.getSubmitterReference(),
															overallType))
									.append(createTag("center_name", sampleMetaData.getCenterName()))
									.append(createTag("date", formatDate(sampleMetaData.getSubmissionDate())))
									.append(createTag("organism", sampleMetaData.getOrganismName()))
									.append(createTag("description", sampleMetaData.getDescription()))
									.append(createTag("structure", sampleMetaData.getStructure()))
									.append(createTag("ncontigs", contigNames.size()))
									.append(createTag("nconbases", numberOfTotalConsensusBases))
									.append(createTag("ntraces", numberOfTotalReads))
									.append(createTag("nbasecalls", numberOfTotalReadBases))
									.append(createTag("coverage", String.format("%.2f", coverage)))
										.toString();
				out.write(header.getBytes(IOUtil.UTF_8));
				//re-open and append temp data to file
				InputStream tempIn = new FileInputStream(tempFile);
				try{
					IOUtil.copy(tempIn, out);
				}finally{
					IOUtil.closeAndIgnoreErrors(tempIn);
				}
				//TODO do we need to put readme files here?
				out.write(("</assembly>"+EOL).getBytes(IOUtil.UTF_8));
				//only delete temp file if everything worked
				IOUtil.delete(tempFile);
				return xmlFile;
			}finally{
				IOUtil.closeAndIgnoreErrors(out);
				
			}
		}

		private static synchronized String formatDate(Date date){
			return DATE_FORMATTER.format(date);
		}

		@Override
		public void write(
				String submitterReference,
				AssemblyArchiveConformation conformation,
				AssemblyArchiveType type, 
				Contig<? extends AssembledRead> superContig,
				TraceNameLookup lookup) throws IOException {
			if(submitterReference==null){
				throw new NullPointerException("submitter ref can not be null");
			}
			if(conformation==null){
				throw new NullPointerException("contig conformation can not be null");
			}
			if(type==null){
				throw new NullPointerException("contig submission type can not be null");
			}
			if(superContig ==null){
				throw new NullPointerException("super contig can not be null");
			}
			if(lookup==null){
				throw new NullPointerException("TraceNameLookup can not be null");
			}
			if(type !=AssemblyArchiveType.NEW){
				overallType = AssemblyArchiveType.UPDATE;
			}
			contigNames.add(superContig.getId());
			
			writeContigHeader(submitterReference, conformation, type);
			handleConsensus(submitterReference, conformation, type, superContig);
			handleReads(superContig, lookup);
			tempOut.write(String.format("</contig>%n"));
		}


		private void handleReads(Contig<? extends AssembledRead> superContig, TraceNameLookup lookup) throws IOException {
			NucleotideSequence consensusSequence =superContig.getConsensusSequence();
			
			SortedSet<AssembledRead> sortedReads = sortReads(superContig, lookup);
			for(AssembledRead read : sortedReads){
				Range validRange =read.getReadInfo().getValidRange();
				StringBuilder readRecord = new StringBuilder(300);
				long readStart = read.getGappedStartOffset();
				long readEnd = read.getGappedEndOffset();
				ReferenceMappedNucleotideSequence readSequence = read.getNucleotideSequence();
				List<Integer> readGapOffsets =readSequence.getGapOffsets();
				
				readRecord.append("<trace>").append(EOL)
						
						.append(createTag("trace_name",lookup.getTraceNameByContigReadId(read.getId()), 1))
							.append(createTag("nbasecalls", readSequence.getUngappedLength(),1))
							.append("\t<valid>").append(EOL)
								.append(createTag("start", validRange.getBegin()+1, 2))
								.append(createTag("stop", validRange.getEnd()+1, 2))
							.append("\t</valid>").append(EOL)
							//denote the gapped coordinates 1-based into the consensus
							.append(String.format("\t<tiling direction = \"%s\">", read.getDirection())).append(EOL)
								.append(createTag("start", readStart+1, 2))
								.append(createTag("stop", readEnd+1, 2))
							.append("\t</tiling>").append(EOL)
							//denote the consensus range of the trace in the ungapped consensus 
							.append("\t<traceconsensus>").append(EOL)
							.append(createTag("start", consensusSequence.getUngappedOffsetFor((int)readStart)+1, 2))
								.append(createTag("stop", consensusSequence.getUngappedOffsetFor((int)readEnd)+1, 2))
							.append("\t</traceconsensus>").append(EOL);
							//only print gap offsets if we have any
							if(!readGapOffsets.isEmpty()){
								readRecord.append(createTag("ntracegaps", readGapOffsets.size(), 1))
											.append(String.format("\t<tracegaps source=\"INLINE\">%s</tracegaps>%n",
														createDeltaGapString(readGapOffsets)));
							}
							readRecord.append("</trace>").append(EOL)
							;	
				
				tempOut.write(readRecord.toString());
			}
			
		}


		private SortedSet<AssembledRead> sortReads(
				Contig<? extends AssembledRead> superContig,
				TraceNameLookup lookup) {
			SortedSet<AssembledRead> sortedReads = new TreeSet<AssembledRead>(new ReadSorter(lookup));
			StreamingIterator<? extends AssembledRead> readIter = superContig.getReadIterator();
			try{
				while(readIter.hasNext()){
					sortedReads.add(readIter.next());
				}
				return sortedReads;
			}finally{
				IOUtil.closeAndIgnoreErrors(readIter);
			}
			
		}


		private void handleConsensus(String submitterReference,
				AssemblyArchiveConformation conformation,
				AssemblyArchiveType type,
				Contig<? extends AssembledRead> superContig)
				throws IOException, FileNotFoundException {
			
			long numberOfReads = superContig.getNumberOfReads();
			numberOfTotalReads += numberOfReads;
			tempOut.write(createTag("ntraces",numberOfReads));
			
			NucleotideSequence consensusSequence = superContig.getConsensusSequence();
			long ungappedLength = consensusSequence.getUngappedLength();
			numberOfTotalConsensusBases+= ungappedLength;
			
			tempOut.write(createTag("nconbases",ungappedLength));
			
			//TODO make this more efficient?
			long numberOfReadBases=getNumberOfReadBases(superContig);
			numberOfTotalReadBases += numberOfReadBases;
			tempOut.write(createTag("nbasecalls",numberOfReadBases));
			List<Integer> gapOffsets = consensusSequence.getGapOffsets();
			if(!gapOffsets.isEmpty()){
				tempOut.write(createTag("ncongaps", gapOffsets.size()));
			}
			String deltaGapString = createDeltaGapString(gapOffsets);
			String consensusQualities = computeConsensusQualities(superContig);
			if(writeContigDataToSeparateFiles()){
				if(!gapOffsets.isEmpty()){
					String consensusGapFilename = filenamefactory.createConsensusGapFileNameFor(superContig);
					File consensusGapFile = new File(outputDirectory, consensusGapFilename);
					extraFilesCreated.add(consensusGapFile);
					
					writeSourceFile(consensusGapFile, deltaGapString);
					tempOut.write(String.format("<congaps source=\"FILE\">%s</congaps>%n",consensusGapFile.getName()));
					
				}
				String consensusQualFilename = filenamefactory.createConsensusQualityFileNameFor(superContig);
				File consensusQualFile = new File(outputDirectory, consensusQualFilename);
				extraFilesCreated.add(consensusQualFile);
				
				writeSourceFile(consensusQualFile, consensusQualities);
				
				
				if(crossLinkContigSubmission){
					tempOut.write(String.format("<consensus source=\"ACCESSION\"></consensus>%n"));
				}else{
					String consensusFilename = filenamefactory.createConsensusSequenceFileNameFor(superContig);
					File consensusFile = new File(outputDirectory, consensusFilename);
					extraFilesCreated.add(consensusFile);
					writeSourceFile(consensusFile, new NucleotideSequenceBuilder(consensusSequence).ungap().toString());
				}
				tempOut.write(String.format("<conqualities source=\"FILE\">%s</conqualities>%n",consensusQualFile.getName()));
				
			}else{
				//inline everything
				if(!gapOffsets.isEmpty()){
					tempOut.write(String.format("<congaps source=\"INLINE\">%s</congaps>%n",deltaGapString));
				}
				if(crossLinkContigSubmission){
					tempOut.write("<consensus source=\"ACCESSION\"></consensus>%n");
				}else{
					tempOut.write(String.format("<consensus source=\"INLINE\">%s</consensus>%n", 
							new NucleotideSequenceBuilder(consensusSequence).ungap().toString()));
				}
				tempOut.write(String.format("<conqualities source=\"INLINE\">%s</conqualities>%n",consensusQualities));
			}
		}
		
		private String computeConsensusQualities(
				Contig<?> superContig) {
			if(superContig instanceof AceContig){
				//already computed
				QualitySequence quals= ((AceContig)superContig).getConsensusQualitySequence();
				StringBuilder builder = new StringBuilder((int)quals.getLength()*5);
				Iterator<PhredQuality> qualIter = quals.iterator();
				if(qualIter.hasNext()){
					
				}
				while(qualIter.hasNext()){
					builder.append(" " +qualIter.next().getQualityScore());
				}
				return builder.toString();
			}
			
			//have to compute it
			SliceMap sliceMap = new SliceMapBuilder(superContig, qualityDataStore)
									.build();
			NucleotideSequence consensusSequence = superContig.getConsensusSequence();
			GrowableIntArray gapOffsets =new GrowableIntArray(consensusSequence.getGapOffsets());
			
			StringBuilder builder = new StringBuilder((int)consensusSequence.getLength()*5);
			ConsensusCaller consensusCaller = new ConicConsensusCaller(PhredQuality.valueOf(30));
			for(int i=0; i<sliceMap.getSize(); i++){
				if(gapOffsets.binarySearch(i)<0){
					//not a gap
					ConsensusResult consensus = consensusCaller.callConsensus(sliceMap.getSlice(i));
					builder.append(" " +consensus.getConsensusQuality());
				}
			}
			//remove leading space?
			return builder.substring(1);
		}


		private void writeSourceFile(File file,
				String data) throws FileNotFoundException {
			PrintWriter pw = new PrintWriter(file);
			//legacy Perl code didn't put new line at end of file
			//so we won't either
			pw.print(data);
			pw.close();
			
		}


		private boolean writeContigDataToSeparateFiles(){
			return filenamefactory!=null;
		}
		


		private long getNumberOfReadBases(
				Contig<? extends AssembledRead> superContig) {
			StreamingIterator<? extends AssembledRead> readIter = superContig.getReadIterator();
			long numberOfReadBases=0L;
			try{
				while(readIter.hasNext()){
					numberOfReadBases+= readIter.next().getNucleotideSequence().getUngappedLength();
				}
			}finally{
				IOUtil.closeAndIgnoreErrors(readIter);
			}
			return numberOfReadBases;
		}

		private String createDeltaGapString(List<Integer> gapOffsets) {
	        int previous=0;
	        StringBuilder sb = new StringBuilder();
	        for(Integer index : gapOffsets){
	            sb.append(index - previous);
	            sb.append(" ");
	            previous = index+1;
	        }
	        return sb.toString().trim();
	    }

		private void writeContigHeader(String submitterReference,
				AssemblyArchiveConformation conformation,
				AssemblyArchiveType type) throws IOException{
			tempOut.write(String.format(
					"<contig submitter_reference=\"%s\" conformation=\"%s\" type =\"%s\">%n",
					submitterReference,
					conformation,
					type
					));
		}
		private static String createTag(String field, int value, int level){
			return createTag(field, Integer.toString(value),level);
		}
		private static String createTag(String field, int value){
			return createTag(field,value,0);
		}
		private static String createTag(String field, long value, int level){
			return createTag(field, Long.toString(value),level);
		}
		private static String createTag(String field, long value){
			return createTag(field, value,0);
		}
		private static String createTag(String field, String value){
			return createTag(field, value,0);
		}
		private static String createTag(String field, String value, int level){
			StringBuilder builder =  new StringBuilder(field.length() + value.length()+6 );
			for(int i=0; i<level; i++){
				builder.append("\t");
			}
	        return builder.append("<").append(field).append(">")
	                    .append(value)
            .append("</").append(field).append(">")
            .append(EOL)
            .toString();
	    }
		
		
	}
	
	
	public static interface AssemblyArchiveFilenameFactory{
		String createConsensusGapFileNameFor(Contig<?> contig);
		
		String createConsensusQualityFileNameFor(Contig<?> contig);
		
		String createConsensusSequenceFileNameFor(Contig<?> contig);
	}
	/**
	 * Sort reads by the method specified in the Assembly Archive RFC.
	 * 
	 * Traces are listed in order of their occurrence in the contig's tiling.
	 *  The first order is by trace.tiling.start, 
	 *  then by length of the tiling range 
	 *  (shortest first), else by trace.ti, else by trace.trace_name. 
	 * @author dkatzel
	 *
	 */
	private static class ReadSorter implements Comparator<AssembledRead>{

		private final TraceNameLookup lookup;
		
		public ReadSorter(TraceNameLookup lookup) {
			this.lookup = lookup;
		}

		@Override
		public int compare(AssembledRead o1, AssembledRead o2) {
			int rangeCmp =Range.Comparators.ARRIVAL.compare(o1.asRange(), o2.asRange());
			if(rangeCmp !=0){
				return rangeCmp;
			}
			String o1Ti =lookup.getTraceNameByContigReadId(o1.getId());
			
			String o2Ti =lookup.getTraceNameByContigReadId(o2.getId());
			int tiCmp = o1Ti.compareTo(o2Ti);
			if(tiCmp !=0){
				return tiCmp;
			}
			return o1.getId().compareTo(o2.getId());
		}
		
	}
}
