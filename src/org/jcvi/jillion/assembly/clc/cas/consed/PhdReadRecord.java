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
package org.jcvi.jillion.assembly.clc.cas.consed;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.Trace;

class PhdReadRecord implements Trace{

	private final Phd phd;
	private final PhdInfo phdInfo;
	
	public PhdReadRecord(Phd phd, PhdInfo phdInfo) {
		if(phd==null){
			throw new NullPointerException("phd can not be null");
		}
		if(phdInfo==null){
			throw new NullPointerException("phd can not be null");
		}
		this.phd = phd;
		this.phdInfo = phdInfo;
	}


	public Phd getPhd() {
		return phd;
	}

	public PhdInfo getPhdInfo() {
		return phdInfo;
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + phd.hashCode();
		result = prime * result + phdInfo.hashCode();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PhdReadRecord)) {
			return false;
		}
		PhdReadRecord other = (PhdReadRecord) obj;
		if (!phd.equals(other.phd)) {
			return false;
		}
		if (!phdInfo.equals(other.phdInfo)) {
			return false;
		}
		return true;
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return getPhd().getId();
    }


	@Override
	public String getId() {
		return phd.getId();
	}


	@Override
	public NucleotideSequence getNucleotideSequence() {
		return phd.getNucleotideSequence();
	}


	@Override
	public QualitySequence getQualitySequence() {
		return phd.getQualitySequence();
	}

}
