package org.jcvi.seqmodel;

/**
 * {@code SequenceI} is the base sequence interface.  
 * 
 * With regards to the sequence attribute of this class, this
 * was intentionally designed to allow implementations of this interface
 * that DO OR DO NOT implement any type of EncodedGlyph compression.
 *
 * @author naxelrod
 */

public interface SequenceI {

	public String 		getId();
	public void 		setId(String id);
	public String		getType();
	public void			setType(String type);
	public String		getAccession();
	public void			setAccession(String acc);
	public Long			getGiNumber();
	public void			setGiNumber(long giNumber);
	public int			getStart();
	public void			setStart(int start);
	public int			getEnd();
	public void			setEnd(int end);
	public String		getSequence();
	public void			setSequence(String seq);
	public int			getLength();
	public boolean 		hasSequence();
	public String		getSequenceName(); // giNumber and accession
}
