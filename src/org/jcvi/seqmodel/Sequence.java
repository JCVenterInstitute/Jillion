package org.jcvi.seqmodel;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

/**
 * {@code Sequence} is the default implementation of the {@link SequenceI} interface.  
 * 
 * The Sequence class stores sequence information as Strings. This can be useful
 * when you want to avoid specified glyph encodings, such as when you want case-
 * sensitive sequence characters, etc.  
 * 
 * @author naxelrod
 */
public class Sequence implements SequenceI {

	protected String id;
	protected String type;
	protected Long giNumber;
	protected String accession;
	protected String sequence;
	protected int start;
	protected int end;
	protected final String DEFAULT_TYPE = "GENERIC_FEATURE";
	
	public Sequence() {
		super();
		this.type = DEFAULT_TYPE;
	}
	
	// Used to convert a Sequence to a subclass, eg. new FluSequence from a Sequence
	public Sequence(SequenceI s) {
		super();
		this.id = s.getId();
		this.type = s.getType();
		this.giNumber = s.getGiNumber();
		this.accession = s.getAccession();
		this.sequence = s.getSequence();
		this.start = s.getStart();
		this.end = s.getEnd();
	}
	
	public Sequence(String sequence) {
		this();
		this.sequence = sequence;
	}
	public Sequence(String id, String sequence) {
		this();
		this.id = id;
		this.sequence = sequence;
	}
	public Sequence(String id, String accession, long giNumber, String sequence) {
		this(id, sequence);
		this.giNumber = giNumber;
		this.accession = accession;
	}
	public Sequence(EncodedGlyphs<Glyph> glyphs) {
		this();
        StringBuilder result = new StringBuilder();
        for (Glyph glyph : glyphs.decode()) {
        	result.append(glyph.toString());
        }
        this.sequence = result.toString();
    }
	public Sequence(String id, EncodedGlyphs<Glyph> glyphs) {
		this(glyphs);
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Long getGiNumber() {
		return giNumber;
	}

	@Override
	public void setGiNumber(long giNumber) {
		this.giNumber = giNumber;
	}

	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	@Override
	public String getType() {
		return type;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public int getStart() {
		return start;
	}
	
	@Override
	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setGiNumber(Long giNumber) {
		this.giNumber = giNumber;
	}

	@Override
	public boolean hasSequence() {
		return (getSequence().length() > 0);
	}
	@Override
	public int getLength() {
		return (int) getSequence().length();
	}
	
	public String getGiNumberAndAccession() {
		String acc = "";
		if (getGiNumber() != null) {
			acc = "gi|" + getGiNumber() + "|";
		}
		acc += "gb|" + getAccession();
		return acc;
	}

	public String getSequenceName() {
		return getGiNumberAndAccession();
	}
	
	public String toString() {
		return "id|" + getId() + "|gi|" + getGiNumber() + "|gb|" + getAccession();
	}
}
