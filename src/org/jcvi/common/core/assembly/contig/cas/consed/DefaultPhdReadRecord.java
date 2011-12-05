/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.contig.cas.consed;

import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class DefaultPhdReadRecord implements PhdReadRecord {

	private final Phd phd;
	private final PhdInfo phdInfo;
	
	public DefaultPhdReadRecord(Phd phd, PhdInfo phdInfo) {
		this.phd = phd;
		this.phdInfo = phdInfo;
	}

	@Override
	public Phd getPhd() {
		return phd;
	}

	@Override
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
		if (!(obj instanceof DefaultPhdReadRecord)) {
			return false;
		}
		DefaultPhdReadRecord other = (DefaultPhdReadRecord) obj;
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

    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return phd.getId();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getBasecalls() {
        return phd.getBasecalls();
    }

}
