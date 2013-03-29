/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;
import java.util.Deque;
import java.util.Queue;

import org.jcvi.jillion.internal.core.util.AbstractFOQueue;


/**
 * {@code FIFOQueue} is a First In First Out {@link Queue} that
 * adds elements to the tail and removes elements from
 * the head.
 * @author dkatzel
 *
 *
 */
public final class FIFOQueue<E> extends AbstractFOQueue<E>{

    @Override
    protected boolean add(E e, Deque<E> deque) {
        return deque.add(e);
    }
    @Override
    protected boolean offer(E e, Deque<E> deque) {
        return deque.offer(e);
    }
    
    
}
