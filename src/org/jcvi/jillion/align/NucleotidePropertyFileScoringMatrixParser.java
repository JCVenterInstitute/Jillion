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
