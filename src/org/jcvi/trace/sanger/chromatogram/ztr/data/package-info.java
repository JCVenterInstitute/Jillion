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
package org.jcvi.trace.sanger.chromatogram.ztr.data;