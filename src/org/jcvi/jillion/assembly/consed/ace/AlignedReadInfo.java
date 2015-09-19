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
/*
 * Created on Feb 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Direction;

/**
 * {@code AlignedReadInfo} defines where on the contig
 * a read starts to align and in which
 * orientation relative to the contig.
 * @author dkatzel
 *
 *
 */
final class AlignedReadInfo{
    private static final Direction[] DIR_VALUES = Direction.values();
	private final byte dirOrdinal;
    private final int startOffset;
    

    public AlignedReadInfo(int startOffset, Direction dir) {
        if(dir ==null){
            throw new NullPointerException("direction can not be null");
        }
        this.startOffset = startOffset;
        this.dirOrdinal = (byte)dir.ordinal();
    }
    

    public int getStartOffset() {
        return startOffset;
    }
    
    public Direction getDirection(){
        return DIR_VALUES[dirOrdinal];
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dirOrdinal;
		result = prime * result + startOffset;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AlignedReadInfo other = (AlignedReadInfo) obj;
		if (dirOrdinal != other.dirOrdinal) {
			return false;
		}
		if (startOffset != other.startOffset) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "AlignedReadInfo [dir=" + getDirection() + ", startOffset=" + startOffset
				+ "]";
	}
   
   
    
    
}
