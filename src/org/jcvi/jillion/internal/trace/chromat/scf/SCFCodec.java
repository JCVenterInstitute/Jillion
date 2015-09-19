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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;

/**
 * <code>SCFCodec</code> is used to encode and decode {@link ScfChromatogram}s.
 * @author dkatzel
 *
 *
 */
public interface SCFCodec {
    
    void parse(InputStream in, ChromatogramFileVisitor visitor) throws ScfDecoderException;
    void parse(File scfFile, ChromatogramFileVisitor visitor) throws IOException,ScfDecoderException;
    void write(Chromatogram chromo, OutputStream out) throws IOException;
}
