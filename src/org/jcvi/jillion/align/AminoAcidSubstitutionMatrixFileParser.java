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
