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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;


abstract class AbstractDefaultAceTag implements AceTag{
    private final String type;
    private final String creator;
    private final Date creationDate;
    private final String data;
    
   
    public AbstractDefaultAceTag(String type, String creator,
            Date creationDate, String data) {
    	if(type ==null){
    		throw new NullPointerException("type can not be null");
    	}
    	if(creator ==null){
    		throw new NullPointerException("creator can not be null");
    	}
    	if(creationDate ==null){
    		throw new NullPointerException("creationDate can not be null");
    	}
        this.type = type;
        this.creator = creator;
        this.creationDate = new Date(creationDate.getTime());
        this.data = data;
    }

    @Override
    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + creationDate.hashCode();
        result = prime * result + creator.hashCode();
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + type.hashCode();
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
        if (!(obj instanceof AbstractDefaultAceTag)) {
            return false;
        }
        AbstractDefaultAceTag other = (AbstractDefaultAceTag) obj;
       if (!creationDate.equals(other.creationDate)) {
            return false;
        }
        if (!creator.equals(other.creator)) {
            return false;
        }
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
