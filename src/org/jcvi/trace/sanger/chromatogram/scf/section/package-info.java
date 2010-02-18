/**
 * An SCF file is divided into several sections; each section
 * contains only certain fields of a Chromatogram.  Each Section
 * is formatted differently and some sections are even
 * formatted differently depending on the specification version.
 * Therefore, Section specific encoders and decoders are needed
 * to handle each Section.
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;