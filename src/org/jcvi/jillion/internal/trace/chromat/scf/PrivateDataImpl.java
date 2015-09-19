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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf;

import java.util.Arrays;

import org.jcvi.jillion.trace.chromat.scf.PrivateData;

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
public final class PrivateDataImpl implements PrivateData {

    private final byte[] data;

    public PrivateDataImpl(byte[] data){
        this.data =Arrays.copyOf(data, data.length);
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
	public byte[] getBytes() {
    	//defensive copy
        return Arrays.copyOf(data, data.length);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof PrivateData)){
            return false;
        }
        final PrivateData other = (PrivateData) obj;
        return
         Arrays.equals(data, other.getBytes());

    }




}
