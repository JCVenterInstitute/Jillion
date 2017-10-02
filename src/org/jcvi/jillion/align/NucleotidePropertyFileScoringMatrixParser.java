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
package org.jcvi.jillion.align;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

final class NucleotidePropertyFileScoringMatrixParser extends AbstractSubstitutionMatrixFileParser<Nucleotide> implements NucleotideSubstitutionMatrix{

	public static NucleotidePropertyFileScoringMatrixParser parse(File maxtrix) throws FileNotFoundException{
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(maxtrix));
			return new NucleotidePropertyFileScoringMatrixParser(in);
		}finally{
			if(in !=null){
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
	}
	
	public static NucleotidePropertyFileScoringMatrixParser parse(InputStream maxtrix) throws FileNotFoundException{
		InputStream in = null;
		try{
			in = new BufferedInputStream(maxtrix);
			return new NucleotidePropertyFileScoringMatrixParser(in);
		}finally{
			if(in !=null){
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
	}
	
	private NucleotidePropertyFileScoringMatrixParser(InputStream in) {
		super(in);
	}

	@Override
	protected Nucleotide parse(String s) {
		return Nucleotide.parse(s);
	}

	@Override
	protected int getNumberOfValues() {
		return Nucleotide.getAllValues().size();
	}

	@Override
	protected Sequence<Nucleotide> parseColumns(String columns) {
		return new NucleotideSequenceBuilder(columns).build();
	}

}
