/*
 * Created on Jan 29, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jcvi.Distance;
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.encoder.TigrQualitiesEncodedGyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.Library;
import org.jcvi.sequence.Mated;

public class Frg2Writer {
    private static final TigrQualitiesEncodedGyphCodec QUALITY_CODEC = TigrQualitiesEncodedGyphCodec.getINSTANCE();
      private static final String FRG_VERSION_2_TAG = "{VER\nver:2\n}\n";
    private static final String LIBRARY_FORMAT = "{LIB\nact:A\nacc:%s\nori:%s\nmea:%.2f\nstd:%.2f\nsrc:\n.\nnft:0\nfea:\n.\n}\n";
    
    private static final String FRG_2_FORMAT = "{FRG\nact:A\nacc:%s\nrnd:1\nsta:G\nlib:%s\npla:0\nloc:0\nsrc:\n%s\n.\nseq:\n%s\n.\nqlt:\n%s\n.\nhps:\n.\nclv:%d,%d\nclr:%d,%d\n}\n";
    private static final String LKG_MESSAGE = "{LKG\nact:A\nfrg:%s\nfrg:%s\n}\n";

    
    /**
     * Create a mated FRG 2 file. 
     * @param matedFrags list of mated fragments to write
     * @param out outputStream to write data to.
     * @throws IOException
     */
    public void writeFrg2(final List<Mated<Fragment>> matedFrags, final List<Fragment> unmatedFrgs,OutputStream out) throws IOException{
      //  ExecutorService executor = Executors.newFixedThreadPool(2);
        writeVersion(out);
        
        new Runnable(){

            @Override
            public void run() {
                OutputStream temp;
                try {
                    temp = new FileOutputStream("tmp.lib");
                    writeLibraries(matedFrags, unmatedFrgs, temp); 
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                              
            }
        }.run();
        new Runnable(){

            @Override
            public void run() {
                OutputStream temp;
                try {
                    temp = new FileOutputStream("tmp.frag");
                    writeFragments(matedFrags, unmatedFrgs, temp); 
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                              
            }
        }.run();
        new Runnable(){

            @Override
            public void run() {
                OutputStream temp;
                try {
                    temp = new FileOutputStream("tmp.link");
                    writeLinkages(matedFrags,temp);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                              
            }
        }.run();
    /*    executor.execute(LibWriter);
        executor.execute(fragWriter);
        executor.execute(LinkWriter);
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }
    
    
    private void writeLinkages(List<Mated<Fragment>> matedFragsLists,
            OutputStream out) throws IOException {
        for(Mated<Fragment> matedFrgs: matedFragsLists){
            writeLinkageMessage(matedFrgs.getMates(), out);
        }
        
    }
    private void writeLinkageMessage(List<Fragment> mates,OutputStream out) throws IOException{
        out.write(String.format(LKG_MESSAGE, mates.get(0).getId(),mates.get(1).getId()).getBytes());
    }
    private void writeFragments(List<Mated<Fragment>> matedFragsList, List<Fragment> unmatedFrgs,OutputStream out) throws IOException {
        for(Mated<Fragment> matedFrags: matedFragsList){
            for(Fragment frag: matedFrags.getMates()){
                writeFrag(frag,out);
            }
        }
        for(Fragment unmatedFrag: unmatedFrgs){
            writeFrag(unmatedFrag,out);
        }
        
    }
    private void writeFrag(Fragment frag, OutputStream out) throws IOException {
        Range clearRange = frag.getValidRange().convertRange(CoordinateSystem.SPACE_BASED);
        Range vectorClearRange = frag.getVectorClearRange().convertRange(CoordinateSystem.SPACE_BASED);
        Library library = frag.getLibrary();
       out.write(String.format(FRG_2_FORMAT, frag.getId(),
               library.getId(),
               writeSourceComment(frag),
               NucleotideGlyph.convertToString(frag.getBasecalls().decode()),
               new String(QUALITY_CODEC.encode(frag.getQualities().decode())),
               vectorClearRange.getLocalStart(),vectorClearRange.getLocalEnd(),
               clearRange.getLocalStart(),clearRange.getLocalEnd()
               ).getBytes());
        
    }
    private String writeSourceComment(Fragment frag) {
        return frag.getComment();
    }

    private void writeVersion(OutputStream out) throws IOException {
        out.write(FRG_VERSION_2_TAG.getBytes());
    }
    
    private void writeLibraries(List<Mated<Fragment>> matedFrags, List<Fragment> unmatedFrgs, OutputStream out) throws IOException {
        Set<String> seen = new HashSet<String>();
        for(Fragment frag: unmatedFrgs){
            writeLibraryIfNotYetSeen(out, seen, frag);
        }
        for(Mated<Fragment> mated : matedFrags){
            for(Fragment frag: mated.getMates()){
                writeLibraryIfNotYetSeen(out, seen, frag);
            }
        }
        
    }
    private  void writeLibraryIfNotYetSeen(OutputStream out, Set<String> seen,
            Fragment frag) throws IOException {
        if(!seen.contains(frag.getLibraryId())){
            writeLibrary(frag.getLibrary(), out);
            seen.add(frag.getLibraryId());
        }
    }
    private void writeLibrary(Library library, OutputStream out)
            throws IOException {
        final Distance distance = library.getDistance();
        out.write(String.format(LIBRARY_FORMAT,
                library.getId(), library.getMateOrientation().getCharacter(),
                distance.getMean(), distance.getStdDev()).getBytes());
    }
}
