package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Objects;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.fasta.FastaUtil;
/**
 * Class that will create a Fasta Index file ({@code .fai} file)
 * from an already existing fasta file.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
public final class FaiNucleotideWriterBuilder {

	private final File inputFasta;
	
	private File outputFai;
	
	private String eol = String.format("%n");

	private Charset charset = Charset.defaultCharset();
	/**
	 * Create a new Builder object that will write a fai file
	 * for the given input fasta file.  By default, 
	 * the output fai file to be written will follow
	 * Samtools naming conventions and be named
	 * $inputFasta#getName().fai and will be written to the same directory
	 * where the inputFasta is location.
	 * 
	 * @param inputFasta the input fasta file to parse; must exist and can not be null.
	 * May be compressed using any method supported by {@link org.jcvi.jillion.core.io.InputStreamSupplier#forFile(File)}.
	 * 
	 * @throws IOException if inputFasta does not exist or is not readable.
	 * @throws NullPointerException if inputFasta is null.
	 */
	public FaiNucleotideWriterBuilder(File inputFasta) throws IOException {
		IOUtil.verifyIsReadable(inputFasta);
		this.inputFasta = inputFasta;
		outputFai = new File(inputFasta.getParentFile(), inputFasta.getName() +".fai");
	}
	/**
	 * Change the output fai file to be written to the given
	 * output file.
	 * If this method is not called, then this writer will follow
	 * Samtools naming conventions and be named
	 * $inputFasta#getName().fai and will be written to the same directory
	 * where the inputFasta is location.
	 * 
	 * @param outputFai the output file to write to; can not be null.  If the file
	 * or parent directories do not exist,they will be created during {@link #build()}.
	 * 
	 * @return this.
	 * 
	 * @throws NullPointerException if eol is null.
	 */
	public FaiNucleotideWriterBuilder outputFile(File outputFai){
		Objects.requireNonNull(outputFai);
		this.outputFai = outputFai;
		
		return this;
	}
	/**
	 * Change the end of line character(s) used for each line of the output
	 * fai file.  If this method is not specified, then the system default
	 * character(s) are used.
	 * 
	 * @param eol the eol character to use, can not be null.
	 * 
	 * @return this.
	 * 
	 * @throws NullPointerException if eol is null.
	 */
	public FaiNucleotideWriterBuilder eol(String eol){
		Objects.requireNonNull(eol);
		this.eol = eol;
		
		return this;
	}
	/**
	 * Change the {@link Charset} used when writing the output
	 * fai file.  If this method is not specified, then the system default
	 * is used.
	 * 
	 * @param charset the {@link Charset} to use, can not be null.
	 * 
	 * @return this.
	 * 
	 * @throws NullPointerException if charset is null.
	 */
	public FaiNucleotideWriterBuilder charset(Charset charset){
		Objects.requireNonNull(charset);
		this.charset = charset;
		return this;
		
	}
	/**
	 * Parse the input fasta file and write out a new fai file
	 * using the configuration given.
	 * 
	 * @return the output fai File that was written; will never be null.
	 * 
	 * @throws IOException if there was a problem parsing the 
	 * input fasta file or writing the output fai file.
	 */
	public File build() throws IOException{
		IOUtil.mkdirs(outputFai.getParentFile());
		try(PrintWriter writer = new PrintWriter(outputFai, charset.name())){
			FastaUtil.createIndex(inputFasta, writer, eol, (line) -> (int) new NucleotideSequenceBuilder(line).getLength());
		}
		return outputFai;
	}
}
