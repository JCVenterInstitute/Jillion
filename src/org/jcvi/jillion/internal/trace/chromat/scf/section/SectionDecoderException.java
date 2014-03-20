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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;
/**
 * <code>SectionDecoderException</code> is a subclass
 * of {@link ScfDecoderException} that will be thrown
 * if there is a problem parsing an SCF {@link Section}.
 * @author dkatzel
 *
 *
 */
public class SectionDecoderException extends ScfDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = 7865788143804921065L;

  
    public SectionDecoderException(String message, Throwable cause) {
        super(message, cause);
    }
 
    public SectionDecoderException(String message) {
        super(message);
    }

}
