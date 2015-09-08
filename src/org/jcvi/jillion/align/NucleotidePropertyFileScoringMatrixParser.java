/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
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
		return Nucleotide.VALUES.size();
	}

	@Override
	protected Sequence<Nucleotide> parseColumns(String columns) {
		return new NucleotideSequenceBuilder(columns).build();
	}

}
