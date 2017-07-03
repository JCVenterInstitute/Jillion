package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class RollOverListIterator<T> implements Iterator<T> {

    int currentOffset;
    List<T> list;
    public RollOverListIterator(List<T> list){
        this(list, 0);
    }
    public RollOverListIterator(List<T> list, int startOffset){
        this.list = list;
        while(startOffset < 0){
            startOffset =  list.size()+ startOffset;
        }
        this.currentOffset = startOffset%list.size();
    }
    @Override
    public boolean hasNext() {
        return !list.isEmpty();
    }

    @Override
    public T next() {
       if(!hasNext()){
           throw new NoSuchElementException();
       }
       T ret = list.get(currentOffset++);
       if(currentOffset >= list.size()){
           currentOffset =0;
       }
        return ret;
    }

}
