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
/**
 * The data package contains all the classes needed to decode the different
 * data formats in the ZTR specification.  
 * <p/>
 * The actual chromatogram data in a ZTR file
 * is compressed and/or encoded.  There are many different methods
 * and it is common for different Data encodings to be chained together
 * to make the data even more compact.
 * 
 *@author dkatzel
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;
