/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * {@code SamParserFactory} is a Factory class
 * for creating {@link SamParser}
 * instances depending of if the provided file
 * is a SAM or BAM encoded file.
 * 
 * 
 * @apiNote
 * <p>
 * To create a new {@link SamParser} from a Sam or Bam file:
 * 
 * <pre>
 * {@code 
 * File samFile =...
 * SamParser parser = SamParserFactory.create(samFile);}
 * </pre>
 * 
 * 
 * </p>
 * 
 * @author dkatzel
 *
 */
public final class SamParserFactory {

	@Data
	@Builder
	public static class Parameters{
		
		public static Parameters createDefault() {
			return builder().build();
		}
		@Getter(AccessLevel.NONE)
		private boolean ignoreBai;
		
		
		@Builder.Default
		private SamAttributeValidator attributeValidator = ReservedAttributeValidator.INSTANCE;
		
		public boolean shouldIgnoreBai() {
			return ignoreBai;
		}
	}
	private SamParserFactory(){
		//can not instantiate
	}
	/**
	 * Convenience method for {@link #create(File, SamAttributeValidator)}
	 * with the default attribute validator.
	 * 
	 * @see #create(File, SamAttributeValidator)
	 */
	public static SamParser create(File f) throws IOException{
		return create(f, ReservedAttributeValidator.INSTANCE);
	}
	/**
	 * Create a new {@link SamParser}
	 * instance for the given SAM or BAM file.
	 * <p>
	 * Since Jillion 5.0 if the given file is a coordinate sorted BAM file
	 * and there is an accompanying BAI file in the same directory named <code>f.getName() + ".bai"</code>,
	 * then a specialized {@link SamParser}
	 * that uses the index will be returned as if the call to
	 * {@link #createUsingIndex(File, File, SamAttributeValidator)} was used instead.
	 * </p>
	 * @param f the SAM or BAM file to be parsed;
	 * can not be null, must exist and 
	 * the file must end in either ".sam"
	 * or ".bam" (ignoring case).
	 * @param validator the {@link SamAttributeValidator}
	 * to use to validate the {@link SamRecord}s being parsed;
	 * can not be null.
	 * @return a new {@link SamParser} instance
	 * will never be null.
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if any parameter is null.
	 * @throws IllegalArgumentException if the file's extension
	 * is not either ".sam" or ".bam" (ignoring case).
	 * 
	 * @see #createUsingIndex(File, File, SamAttributeValidator)
	 */
	public static SamParser create(File f, SamAttributeValidator validator) throws IOException{
		
		if(validator == null){
			throw new NullPointerException("validator can not be null");
		}
		IOUtil.verifyIsReadable(f);
		
		String extension = FileUtil.getExtension(f);
		if("sam".equalsIgnoreCase(extension)){
			return new SamFileParser(f,validator);
		}
		if("bam".equalsIgnoreCase(extension)){
			return createFromBamFile(f, Parameters.builder().attributeValidator(validator).build());			
		}
		throw new IllegalArgumentException("unknown file format " + f.getName());
	}
	
	/**
	 * Create a new {@link SamParser}
	 * instance for the given SAM or BAM file.
	 * <p>
	 * Since Jillion 5.0 if the given file is a coordinate sorted BAM file
	 * and there is an accompanying BAI file in the same directory named <code>f.getName() + ".bai"</code>,
	 * then a specialized {@link SamParser}
	 * that uses the index will be returned as if the call to
	 * {@link #createUsingIndex(File, File, SamAttributeValidator)} was used instead.
	 * </p>
	 * @param f the SAM or BAM file to be parsed;
	 * can not be null, must exist and 
	 * the file must end in either ".sam"
	 * or ".bam" (ignoring case).
	 * @param validator the {@link SamAttributeValidator}
	 * to use to validate the {@link SamRecord}s being parsed;
	 * can not be null.
	 * @return a new {@link SamParser} instance
	 * will never be null.
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if any parameter is null.
	 * @throws IllegalArgumentException if the file's extension
	 * is not either ".sam" or ".bam" (ignoring case).
	 * 
	 * @see #createUsingIndex(File, File, SamAttributeValidator)
	 */
	public static SamParser create(File f, Parameters parameters) throws IOException{
		
		if(parameters.attributeValidator == null){
			throw new NullPointerException("validator can not be null");
		}
		IOUtil.verifyIsReadable(f);
		
		String extension = FileUtil.getExtension(f);
		if("sam".equalsIgnoreCase(extension)){
			return new SamFileParser(f,parameters.attributeValidator);
		}
		if("bam".equalsIgnoreCase(extension)){
			return createFromBamFile(f, parameters);			
		}
		throw new IllegalArgumentException("unknown file format " + f.getName());
	}
	
	
	private static SamParser createFromBamFile(File f, Parameters parameters) throws IOException {
		SamParser unsortedBamParser= new BamFileParser(f, parameters.attributeValidator);
		if(!parameters.shouldIgnoreBai() && unsortedBamParser.getHeader().getSortOrder() == SortOrder.COORDINATE){
			//is there an indexed bam file that goes with it?
			File bai = new File(f.getParentFile(), f.getName() +".bai");
			if(bai.exists()){
				return createUsingIndex(f, bai, parameters.attributeValidator);
			}
		
		}
		return unsortedBamParser;
	}
	
	/**
	 * Create a new {@link SamParser}
	 * instance for the Coordinate sorted BAM file
	 * with accompanying BAI encoded file and
	 * using the {@link ReservedAttributeValidator}
	 * to validate the {@link SamRecord}s to be parsed.
	 * This is the same as
	 * {@link #createUsingIndex(File, File, SamAttributeValidator) createUsingIndex(bam, bai, ReservedAttributeValidator.INSTANCE)}.
	 * @param bam the Coordinate sorted BAM file to be parsed;
	 * can not be null, must exist.
	 * 
	 * @param bamIndex the corresponding BAI encoded file to be parsed;
	 * can not be null, must exist.
	 * 
	 * @return a new {@link SamParser} instance
	 * will never be null.
	 * 
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if any parameter is null.
	 * @throws IllegalArgumentException if the file's extension
	 * is not either ".sam" or ".bam" (ignoring case).
	 * 
	 * @see #createUsingIndex(File, File, SamAttributeValidator)
	 * 
	 * @since 5.0
	 */
	public static SamParser createUsingIndex(File bam, File bamIndex) throws IOException{
		return new IndexedBamFileParser(bam, bamIndex, ReservedAttributeValidator.INSTANCE);
	}
	
	/**
	 * Create a new {@link SamParser}
	 * instance for the Coordinate sorted BAM file
	 * with accompanying BAI encoded file and
	 * using the given {@link SamAttributeValidator}
	 * to validate the {@link SamRecord}s to be parsed.
	 *
	 * @param bam the Coordinate sorted BAM file to be parsed;
	 * can not be null, must exist.
	 * 
	 * @param bamIndex the corresponding BAI encoded file to be parsed;
	 * can not be null, must exist.
	 * 
	 *  @param validator the {@link SamAttributeValidator}
	 * to use to validate the {@link SamRecord}s being parsed;
	 * can not be null.
	 * 
	 * @return a new {@link SamParser} instance
	 * will never be null.
	 * 
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if any parameter is null.
	 * @throws IllegalArgumentException if the file's extension
	 * is not either ".sam" or ".bam" (ignoring case).
	 * 
	 * @since 5.0
	 */
	public static SamParser createUsingIndex(File bam, File bamIndex, SamAttributeValidator validator) throws IOException{
		IOUtil.verifyIsReadable(bam);
		IOUtil.verifyIsReadable(bamIndex);
		if(validator == null){
			throw new NullPointerException("validator can not be null");
		}
		return new IndexedBamFileParser(bam, bamIndex, validator);
	}
}
