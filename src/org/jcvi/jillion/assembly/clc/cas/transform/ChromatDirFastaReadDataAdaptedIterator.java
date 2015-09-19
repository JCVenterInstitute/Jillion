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
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.clc.cas.transform.ReadData.Builder;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;

/**
 * @author dkatzel
 *
 *
 */
class ChromatDirFastaReadDataAdaptedIterator extends FastaReadDataAdaptedIterator{
	private static List<String> EXTENSION_LIST = Arrays.asList("",".scf",".ztr",".ab1");
    private final File chromatDir;
    /**
     * @param fastaIterator
     * @param fastaFile
     * @param phdDate
     * @param defaultQualityValue
     */
    public ChromatDirFastaReadDataAdaptedIterator(
            StreamingIterator<NucleotideFastaRecord> fastaIterator,
            File fastaFile, 
            File chromatDir) {
        super(fastaIterator, fastaFile);
        this.chromatDir = chromatDir;
    }
    
    


    @Override
	protected void updateField(Builder builder) {
		tryToParseFromChromatDir(builder);
		
	}




	/**
     * Look for an parse an scf/ztr or abi chromatogram with the given
     * id.  The file to be parsed will be named
     * id only without any file extensions and must be in SCF format (since 
     * this is what consed expects).
     * @param id the id to look for in the chromat directory.
     * @return a {@link Chromatogram} object if parsing
     * is a success; or null if no chromatogram is found.
     * @throws IllegalStateException if there is a problem 
     * parsing chromatogram file.
     */
	protected void tryToParseFromChromatDir(Builder builder) {
		String id = builder.getId();
		for(String extension : EXTENSION_LIST){
	        File chromatFile = new File(chromatDir,id+extension);
	        if(chromatFile.exists()){
	            try {
	            	Chromatogram chromo= ChromatogramFactory.create(id, chromatFile);
	                if(chromo !=null){
	                	builder.setNucleotideSequence(chromo.getNucleotideSequence())
	                	.setPositions(chromo.getPeakSequence())
	                	.setQualities(chromo.getQualitySequence())
	                	.setUri(chromatFile.toURI());
	                	return;
	                }
	            } catch (Exception e) {
	                throw new IllegalStateException("error parsing chromatogram for "+ id,e);
	            } 
	        }
		}
	}

    protected Phd createPhd( Map<String,String> requiredComments, NucleotideFastaRecord fasta,
            Chromatogram chromo) {
        final String id = fasta.getId();
       
        return new PhdBuilder(id, chromo.getNucleotideSequence(), chromo.getQualitySequence())
        						.peaks(chromo.getPeakSequence())
        						.comments(requiredComments)
        						.build();
    
    }


    

   
    
    

}
