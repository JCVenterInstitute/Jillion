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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.sanger.chromat.scf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.sanger.chromat.ChromatogramWriter;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfDecoderException;

/**
 * <code>SCFCodec</code> is used to encode and decode {@link ScfChromatogram}s.
 * @author dkatzel
 *
 *
 */
public interface SCFCodec extends ChromatogramWriter{
    
    void parse(InputStream in, ChromatogramFileVisitor visitor) throws ScfDecoderException;
    void parse(File scfFile, ChromatogramFileVisitor visitor) throws IOException,ScfDecoderException;
}
