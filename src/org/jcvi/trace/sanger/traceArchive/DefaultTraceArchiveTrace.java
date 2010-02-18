/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.IOException;
import java.io.InputStream;

import org.jcvi.fasta.DefaultNucleotideFastaFileDataStore;
import org.jcvi.fasta.DefaultPositionFastaFileDataStore;
import org.jcvi.fasta.DefaultQualityFastaFileDataStore;
import org.jcvi.fasta.FastaParser;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Peaks;

public class DefaultTraceArchiveTrace extends AbstractTraceArchiveTrace {
    
    public DefaultTraceArchiveTrace(TraceArchiveRecord record,String rootDirPath){
        super(record, rootDirPath);
    }
    @Override
    public Peaks getPeaks() {
        InputStream in=null;
        DefaultPositionFastaFileDataStore datastore =new DefaultPositionFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.PEAK_FILE);
            datastore =new DefaultPositionFastaFileDataStore();
            FastaParser.parseFasta(in, datastore);
            return new Peaks(datastore.iterator().next().getValues().decode());
        } catch (IOException e) {
            throw new IllegalArgumentException("peak file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        InputStream in=null;
        DefaultNucleotideFastaFileDataStore datastore = new DefaultNucleotideFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.BASE_FILE);
            FastaParser.parseFasta(in, datastore);
            return datastore.iterator().next().getValues();
        } catch (IOException e) {
            throw new IllegalArgumentException("basecall file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        InputStream in=null;
        DefaultQualityFastaFileDataStore datastore = new DefaultQualityFastaFileDataStore();
        try{
            in = getInputStreamFor(TraceInfoField.QUAL_FILE);
            FastaParser.parseFasta(in, datastore);
            return datastore.iterator().next().getValues();
        } catch (IOException e) {
            throw new IllegalArgumentException("quality file not valid",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(datastore);
        }
    }
}
