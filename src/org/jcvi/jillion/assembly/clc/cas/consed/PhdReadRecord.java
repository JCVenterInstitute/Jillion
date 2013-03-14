package org.jcvi.jillion.assembly.clc.cas.consed;

import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.sanger.phd.Phd;

class PhdReadRecord implements Trace{

	private final Phd phd;
	private final PhdInfo phdInfo;
	
	public PhdReadRecord(Phd phd, PhdInfo phdInfo) {
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
		result = prime * result + ((phd == null) ? 0 : phd.hashCode());
		result = prime * result + ((phdInfo == null) ? 0 : phdInfo.hashCode());
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
		if (phd == null) {
			if (other.phd != null) {
				return false;
			}
		} else if (!phd.equals(other.phd)) {
			return false;
		}
		if (phdInfo == null) {
			if (other.phdInfo != null) {
				return false;
			}
		} else if (!phdInfo.equals(other.phdInfo)) {
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
