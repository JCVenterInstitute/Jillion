package org.jcvi.jillion.trace.sanger.chromat.scf;
/**
 * PrivateData is a wrapper around a byte array
 * for additional optional data in an scf file.
 * The scf file specification puts no limitations
 * on what this data can contain and is implementation 
 * specific.  It is up to different scf writer implementations
 * to decide what data to put here (if any) and how to encode it. 
 * 
 * @author dkatzel
 *
 */
public interface PrivateData {

	/**
	 * @return the data
	 */
	byte[] getBytes();

	/**
	 * {@inheritDoc}
	 */
	int hashCode();

	/**
	 * Two PrivateData instances are equal
	 * if they both contain the same data.
	 */
	boolean equals(Object obj);

}