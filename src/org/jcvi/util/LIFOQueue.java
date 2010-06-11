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
/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.Deque;
import java.util.Queue;

/**
 * {@code LIFOQueue} is a Last In First Out {@link Queue} that
 * adds and removes elements from the head.
 * @author dkatzel
 *
 *
 */
public class LIFOQueue<E> extends AbstractFOQueue<E>{
  

    @Override
    protected boolean add(E e, Deque<E> deque) {
        deque.addFirst(e);
        return true;
    }

    @Override
    protected boolean offer(E e, Deque<E> deque) {
        return deque.offerFirst(e);
    }

}
