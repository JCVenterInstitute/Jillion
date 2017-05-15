package org.jcvi.jillion.core;

import java.util.Objects;

final class DeflineImpl implements Defline{

    private final String id, comment;
    
    DeflineImpl(String id, String comment) {
        this.id = Objects.requireNonNull(id);
        this.comment = comment;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
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
        if (!(obj instanceof DeflineImpl)) {
            return false;
        }
        DeflineImpl other = (DeflineImpl) obj;
        
        if (!id.equals(other.id)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        
        return true;
    }
    
    

}
