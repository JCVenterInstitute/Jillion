package org.jcvi.common.core.seq.read.trace.sanger;

import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.GlyphCodec;


final class EncodedPositionSequence implements PositionSequence{

    private final EncodedSequence<Position>  delegate;
    /**
     * @param codec
     * @param data
     */
    public EncodedPositionSequence(GlyphCodec<Position> codec,
            byte[] data) {
        delegate = new EncodedSequence<Position>(codec, data);
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public List<Position> asList() {
        return delegate.asList();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Position get(int index) {
        return delegate.get(index);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        return delegate.getLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((delegate == null) ? 0 : delegate.hashCode());
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
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        EncodedPositionSequence other = (EncodedPositionSequence) obj;
        if (delegate == null) {
            if (other.delegate != null){
                return false;
            }
        } else if (!delegate.equals(other.delegate)){
            return false;            
        }
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<Position> iterator() {
        return delegate.iterator();
    }

	@Override
	public Iterator<Position> iterator(Range range) {
		return delegate.iterator(range);
	}

	EncodedSequence<Position> getDelegate() {
		return delegate;
	}
    

}
