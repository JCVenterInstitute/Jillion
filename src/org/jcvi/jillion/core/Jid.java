package org.jcvi.jillion.core;
/**
 * {@code Jid} is an object that 
 * represents some kind of Id.
 * 
 * @author dkatzel
 *
 */
public interface Jid {

	/**
	 * Return this Id as a String.
	 * @return This Jid as a String.
	 */
	@Override
	public String toString();
	/**
	 * Two JIds are equal if they
	 * have the same String representation.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj);

}
