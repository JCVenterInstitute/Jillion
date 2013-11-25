package org.jcvi.jillion.align;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;

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
		return new AminoAcidSequenceBuilder(columns).build();
	}

}
