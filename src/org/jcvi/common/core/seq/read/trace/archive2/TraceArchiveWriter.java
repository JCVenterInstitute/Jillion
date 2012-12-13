package org.jcvi.common.core.seq.read.trace.archive2;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogramBuilderVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFactory;
import org.jcvi.common.core.util.DateUtil;

/**
 * {@code TraceArchiveWriter} is a class
 * that can write out a complete Trace Archive folder structure which contains the 
 * following files:
 * <ul>
 * <li>TRACEINFO.XML</li>
 * <li>README</li>
 * <li>fasta folder: contains 1 fasta file per record</li>
 * <li>pos folder: contains 1 position fasta file per record</li>
 * <li>qual folder: contains 1 quality fasta file per record</li>
 * <li>traces folder: contains 1 binary trace per record</li>
 * </ul>
 * @author dkatzel
 *
 */
public final class TraceArchiveWriter implements Closeable{

	private static final String README_TEXT = 
			"JCVI Trace Volume Generated: %s%n"
			+ "In instances where two basecallers are reported both callers were used in%n"
			+ "conjuction to create the basecall and/or assign quality values.%n%n"

			+ "This volume contains:%n"
			+ ". MD5            MD5 file signatures%n"
			+ ". README         This file%n"
			+ ". TRACEINFO.XML  Auxiliary information in XML format%n"
			+ ". fasta          directory containing basecalls%n"
			+ ". peak           directory containing peak index values%n"
			+ ". qual           directory containing quality scores%n"
			+ ". traces         directory containing %d trace file(s)%n";
	
	private static final DateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US);
    
	private final File rootDir;
	private final TraceArchiveRecordCallback recordCallback;
	
	private final DefaultTraceArchiveInfo.Builder traceInfoBuilder;
	private final Set<String> traceNamesSeen= new HashSet<String>();
	
	/**
	 * Create a new TraceArchiveWriter without any common fields.
	 * This is the same as
	 * {@link #TraceArchiveWriter(File, Map, TraceArchiveRecordCallback) 
	 * TraceArchiveWriter(rootDir, Collections.<TraceInfoField, String>emptyMap(), recordFactory)}.
	 * @see #TraceArchiveWriter(File, Map, TraceArchiveRecordCallback)
	 */
	public TraceArchiveWriter(File rootDir,TraceArchiveRecordCallback recordFactory) throws IOException {
		this(rootDir, Collections.<TraceInfoField, String>emptyMap(), recordFactory);
	}
	/**
	 * Create a new TraceArchiveWriter.
	 * @param rootDir the root directory of where to write out the trace archive;
	 * if this parameter is null, then the current directory is used 
	 * (not recommended).  If the given directory does not
	 * exist, then it will be created.
	 * @param commonFields {@link Map} of common fields
	 * that should be included in all trace archive records;
	 * if no common fields should be used, then include
	 * and empty collection, can not be null.
	 * @param recordCallback an instance of {@link TraceArchiveRecordCallback}
	 * can not be null.
	 * @throws IOException if the root directory given does not
	 * exist, and an error occurs trying to create it.
	 * @throws NullPointerException if recordCallback, commonFields is null
	 * or if any key or value in commonFields is null.
	 */
	public TraceArchiveWriter(File rootDir,Map<TraceInfoField, String> commonFields, TraceArchiveRecordCallback recordCallback) throws IOException {
		//delete pre-existing files if any
		if(rootDir !=null && rootDir.exists()){
			for(File child : rootDir.listFiles()){
				IOUtil.recursiveDelete(child);
			}
		}
		IOUtil.mkdirs(rootDir);
		if(recordCallback ==null){
			throw new NullPointerException("TraceArchiveRecordFactory instance can not be null");
		}
		if(commonFields ==null){
			throw new NullPointerException("common fields Map can not be null");
		}
		this.rootDir = rootDir;
		this.recordCallback = recordCallback;
		traceInfoBuilder = new DefaultTraceArchiveInfo.Builder();
		for(Entry<TraceInfoField, String> entry: commonFields.entrySet()){
			traceInfoBuilder.addCommonField(entry.getKey(), entry.getValue());
		}
		IOUtil.mkdirs(new File(rootDir,"fasta"));
		IOUtil.mkdirs(new File(rootDir,"peak"));
		IOUtil.mkdirs(new File(rootDir,"qual"));
		IOUtil.mkdirs(new File(rootDir,"traces"));
	}
	/**
	 * 
	 * @param traceName
	 * @param traceFile
	 * @throws IOException if there is an exception parsing the given
	 * traceFile.
	 * @param NullPointerException if traceName or traceFile are null.
	 * @throws TraceArchiveRecordDataException 
	 * @throws IllegalArgumentException if a trace with the given traceName
	 * has already been added.
	 */
	public void addTrace(String traceName,File traceFile) throws IOException, TraceArchiveRecordDataException{
		if(traceName ==null){
			throw new NullPointerException("traceName can not be null");
		}
		if(traceFile ==null){
			throw new NullPointerException("traceFile can not be null");
		}
		TraceArchiveRecordBuilder recordBuilder = new DefaultTraceArchiveRecord.Builder();
		if(traceNamesSeen.contains(traceName)){
			throw new IllegalArgumentException("already added a trace with the trace name "+ traceName);
		}
		traceNamesSeen.add(traceName);
		recordBuilder.put(TraceInfoField.TRACE_NAME,traceName);
		Chromatogram chromo = parseChromatogram(traceName, traceFile);
		handleSeqFasta(recordBuilder, traceName, chromo);
		handleQualFasta(recordBuilder, traceName, chromo);		
		handlePeakFasta(recordBuilder, traceName, chromo);
		
		copyTraceFile(recordBuilder, traceName, traceFile);
		recordCallback.addMetaData(traceName, traceFile,recordBuilder);
		
		traceInfoBuilder.addRecord(recordBuilder.build());
	}
	private void copyTraceFile(TraceArchiveRecordBuilder recordBuilder, String traceName,
			File traceFile)
			throws FileNotFoundException, IOException {
		String pathToTraceFile = String.format("traces/%s.ztr", traceName);
		recordBuilder.put(TraceInfoField.TRACE_FILE, pathToTraceFile);
		InputStream in = null;
		OutputStream out = null;
		try{
			in = new FileInputStream(traceFile);
			out = new FileOutputStream(new File(rootDir, pathToTraceFile));
			IOUtil.copy(in, out);
		}finally{
			IOUtil.closeAndIgnoreErrors(in,out);
		}
	}
	
	private String createMd5For(File f) throws IOException{
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("could not compute MD5",e);
		}
		InputStream in = new FileInputStream(f);
		try{
			byte[] bytes =IOUtil.toByteArray(in);
			return toPaddedHexString(md5.digest(bytes));
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	private String toPaddedHexString(byte[] digest){
		StringBuilder builder = new StringBuilder(digest.length*2);
		//Integer.toHexString will only print out 
		//a single character if the value is <16
		// ex: "a" instead of "0a" so we
		//need to add padding if that happens.
		for(int i=0; i<digest.length; i++){
			String hex = Integer.toHexString(0xFF & digest[i]);
			if(hex.length() ==1){
				//padd
				builder.append('0');
			}
			builder.append(hex);
		}
		return builder.toString();
	}
	@Override
	public void close() throws IOException {
		TraceArchiveInfo info =traceInfoBuilder.build();
		File xmlFile = createTraceInfoFile(info);		
		File readMeFile = createReadmeFile();		
		createMd5File(xmlFile, readMeFile);
		
	}
	private File createTraceInfoFile(TraceArchiveInfo info)
			throws FileNotFoundException, IOException {
		File xmlFile = new File(rootDir, "TRACEINFO.XML");
		OutputStream out = new FileOutputStream(xmlFile);
		try{
			TraceInfoWriterUtil.writeTraceInfoXML(out, info);
		}finally{
			IOUtil.closeAndIgnoreErrors(out);
		}
		return xmlFile;
	}
	private File createReadmeFile() throws IOException {
		File readMeFile = new File(rootDir, "README");
		writeReadmeText(readMeFile);
		return readMeFile;
	}
	private void createMd5File(File xmlFile, File readMeFile)
			throws FileNotFoundException, IOException {
		PrintWriter md5Writer = new PrintWriter(new File(rootDir, "MD5"), IOUtil.UTF_8_NAME);
		try{
		md5Writer.println(createMd5For(xmlFile) +"\tTRACEINFO.XML");
		md5Writer.println(createMd5For(readMeFile) +"\tREADME");
		}finally{
			md5Writer.close();
		}
	}
	private void writeReadmeText(File readMeFile) throws IOException {
		PrintWriter writer = new PrintWriter(readMeFile, IOUtil.UTF_8_NAME);
		try{
			//must synchronize data formatter since 
			//it is badly designed and not multithreaded.
			synchronized(DATE_TIME_FORMATTER){
				writer.printf(README_TEXT, 
						DATE_TIME_FORMATTER.format(DateUtil.getCurrentDate()),
						traceNamesSeen.size());
			}
		}finally{
			writer.close();
		}
		
		
	}
	private void handleSeqFasta(TraceArchiveRecordBuilder recordBuilder, String traceName, Chromatogram chromo)
			throws FileNotFoundException, IOException {
		String fastaFilePath = String.format("fasta/%s.fasta", traceName);
		NucleotideSequenceFastaRecordWriter writer = new NucleotideSequenceFastaRecordWriterBuilder(new File(rootDir, fastaFilePath))
														.build();
		writer.write(traceName, chromo.getNucleotideSequence());
		writer.close();
		
		recordBuilder.put(TraceInfoField.BASE_FILE, fastaFilePath);
	}
	
	private void handleQualFasta(TraceArchiveRecordBuilder recordBuilder, String traceName, Chromatogram chromo)
			throws FileNotFoundException, IOException {
		String qualFastaFilePath = String.format("qual/%s.qual", traceName);
		QualitySequenceFastaRecordWriter writer = new QualitySequenceFastaRecordWriterBuilder(new File(rootDir, qualFastaFilePath))
														.build();
		writer.write(traceName, chromo.getQualitySequence());
		writer.close();
		
		recordBuilder.put(TraceInfoField.QUAL_FILE, qualFastaFilePath);
	}
	
	private void handlePeakFasta(TraceArchiveRecordBuilder recordBuilder, String traceName, Chromatogram chromo)
			throws FileNotFoundException, IOException {
		String peakFastaFilePath = String.format("peak/%s.peak", traceName);
		PositionSequenceFastaRecordWriter writer = new PositionSequenceFastaRecordWriterBuilder(new File(rootDir, peakFastaFilePath))
														.build();
		writer.write(traceName, chromo.getPositionSequence());
		writer.close();
		
		recordBuilder.put(TraceInfoField.PEAK_FILE, peakFastaFilePath);
	}
	
	private Chromatogram parseChromatogram(String traceName, File traceFile) throws IOException {
		BasicChromatogramBuilderVisitor chromoBuilder = new BasicChromatogramBuilderVisitor(traceName);
		ChromatogramFactory.parse(traceFile, chromoBuilder);
		return chromoBuilder.build();
	}
	/**
	 * {@code TraceArchiveRecordCallback} is a way to add
	 * additional metadata to specific {@link TraceArchiveRecord}s
	 * before they are built.  The {@link TraceArchiveWriter} will
	 * create an instance of {@link TraceArchiveRecordBuilder}
	 * for each record but only partially populate them.
	 * It is up to implementations of {@link TraceArchiveRecordCallback}
	 * to complete building each record by setting attribute
	 * key value pairs that a generic TraceArchiveWriter won't know how to set.
	 * For example, each trace often requires primer information
	 * to be set in a trace archive.  Implementations of {@link TraceArchiveRecordCallback}
	 * would know how to look up such information from the clients LIMS system.
	 *  
	 * @author dkatzel
	 *
	 */
	public interface TraceArchiveRecordCallback{
		/**
		 * Add any additional metadata to the given {@link TraceArchiveRecordBuilder}
		 * for the given traceName with the accompanying traceFile.
		 * The given builder already has several attributes already 
		 * including:
		 * <ul>
		 * <li> {@link TraceInfoField#TRACE_NAME} = traceName</li>
		 * <li> {@link TraceInfoField#TRACE_FILE} = path to a copy of traceFile</li>
		 * <li> {@link TraceInfoField#BASE_FILE} = path to nucleotide sequence fasta</li>
		 * <li> {@link TraceInfoField#PEAK_FILE} = path to peak positions sequence fasta</li>
		 * <li> {@link TraceInfoField#QUAL_FILE} = path to quality  sequence fasta</li>
		 * </ul>
		 * Additional metadata that is recommended to be added are attributes such
		 * as direction, primer and vector info etc.
		 * @param traceName the name that is used to set
		 * the {@link TraceInfoField#TRACE_NAME}.
		 * @param traceFile the actual trace file of this record.  A copy of this file
		 * is included in the archive and pointed to by {@link TraceInfoField#TRACE_FILE}.
		 * @param builder the builder to add extra metadata to such as direction, primer and vector info etc.
		 */
		void addMetaData(final String traceName,final File traceFile, final TraceArchiveRecordBuilder builder) throws TraceArchiveRecordDataException;
	}
	/**
	 * {@code TraceArchiveRecordDataException} is a checked
	 * {@link Exception} that is to be thrown
	 * when an error occurs while creating metadata for
	 * a {@link TraceArchiveRecord}.
	 * @author dkatzel
	 *
	 */
	public static class TraceArchiveRecordDataException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TraceArchiveRecordDataException(String message, Throwable cause) {
			super(message, cause);
		}

		public TraceArchiveRecordDataException(String message) {
			super(message);
		}
		
	}
}
