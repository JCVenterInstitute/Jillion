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
 * This package can decode Applied Biosystems AB1 formatted
 * trace files into {@link org.jcvi.jillion.trace.sanger.chromat.Chromatogram} objects.
 * Most of the code to parse this format 
 * was based on information from the Clark Tibbetts paper
 * "Raw Data File Formats and the Digital and Analog Raw Data Streams
 * of the ABI PRISM 377 DNA Sequencer" which paritally reverse engineered
 * the Applied Biosystems 377 DNA Sequencer AB1 file format.
 * @author dkatzel
 * @see <a href = "http://www-2.cs.cmu.edu/afs/cs/project/genome/WWW/Papers/clark.html">
  Raw Data File Formats and the Digital and Analog Raw Data Streams
  of the ABI PRISM 377 DNA Sequencer</a>
 */
package org.jcvi.jillion.trace.chromat.abi;
