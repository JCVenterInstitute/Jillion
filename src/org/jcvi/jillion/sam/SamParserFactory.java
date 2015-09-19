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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;

/**
 * {@code SamParserFactory} is a Factory class
 * for creating {@link SamParser}
 * instances depending of if the provided file
 * is a SAM or BAM encoded file.
 * @author dkatzel
 *
 */
public final class SamParserFactory {

	private SamParserFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link SamParser}
	 * instance for the given SAM or BAM file
	 * using the {@link ReservedAttributeValidator}
	 * to validate the {@link SamRecord}s to be parsed.
	 * This is the same as
	 * {@link #create(File, SamAttributeValidator) create(f, ReservedAttributeValidator.INSTANCE)}.
	 * @param f the SAM or BAM file to be parsed;
	 * can not be null, must exist and 
	 * the file must end in either ".sam"
	 * or ".bam" (ignoring case).
	 * @return a new {@link SamParser} instance
	 * will never be null.
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if any parameter is null.
	 * @throws IllegalArgumentException if the file's extension
	 * is not either ".sam" or ".bam" (ignoring case).
	 * @see #create(File, SamAttributeValidator)
	 */
	public static SamParser create(File f) throws IOException{
		return create(f, ReservedAttributeValidator.INSTANCE);
	}
	/**
	 * Create a new {@link SamParser}
	 * instance for the given SAM or BAM file.
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
	 */
	public static SamParser create(File f, SamAttributeValidator validator) throws IOException{
		if(f == null){
			throw new NullPointerException("file can not be null");
		}
		
		if(validator == null){
			throw new NullPointerException("validator can not be null");
		}
		if(!f.exists()){
			throw new FileNotFoundException(f.getAbsolutePath());
		}
		String extension = FileUtil.getExtension(f);
		if("sam".equalsIgnoreCase(extension)){
			return new SamFileParser(f,validator);
		}
		if("bam".equalsIgnoreCase(extension)){
			return new BamFileParser(f, validator);
		}
		throw new IllegalArgumentException("unknown file format " + f.getName());
	}
}
