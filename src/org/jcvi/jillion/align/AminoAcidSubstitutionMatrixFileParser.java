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
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;

final class AminoAcidSubstitutionMatrixFileParser extends AbstractSubstitutionMatrixFileParser<AminoAcid> implements AminoAcidSubstitutionMatrix{

	public static AminoAcidSubstitutionMatrixFileParser parse(File maxtrix) throws FileNotFoundException{
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(maxtrix));
			return new AminoAcidSubstitutionMatrixFileParser(in);
		}finally{
			if(in !=null){
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
	}
	
	public static AminoAcidSubstitutionMatrixFileParser parse(InputStream maxtrix) throws FileNotFoundException{
		InputStream in = null;
		try{
			in = new BufferedInputStream(maxtrix);
			return new AminoAcidSubstitutionMatrixFileParser(in);
		}finally{
			if(in !=null){
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
	}
	
	private AminoAcidSubstitutionMatrixFileParser(InputStream in) {
		super(in);
	}

	@Override
	protected AminoAcid parse(String s) {
		return AminoAcid.parse(s);
	}

	@Override
	protected int getNumberOfValues() {
		return AminoAcid.values().length;
	}

	@Override
	protected Sequence<AminoAcid> parseColumns(String columns) {
		return new ProteinSequenceBuilder(columns).build();
	}

}
