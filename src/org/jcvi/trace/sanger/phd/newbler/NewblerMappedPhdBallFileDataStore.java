/*
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd.newbler;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.sanger.phd.MemoryMappedPhdFileDataStore;
import org.jcvi.trace.sanger.phd.PhdTag;
import org.jcvi.util.MemoryMappedFileRange;

public class NewblerMappedPhdBallFileDataStore extends MemoryMappedPhdFileDataStore{

    private static final String FAKE_READ_TYPE = "type: fake\n";
    private static final String WHOLE_READ_TAG = "WR";
    public NewblerMappedPhdBallFileDataStore(File phdBall,
            MemoryMappedFileRange recordLocations) {
        super(phdBall, recordLocations);
    }

   

   
  

    @Override
    protected void visitPhd(String id, List<NucleotideGlyph> bases,
            List<PhredQuality> qualities, List<ShortGlyph> positions,
            Properties comments, List<PhdTag> tags) {
        boolean isFakeRead=false;
        for(PhdTag tag: tags){
            if(tag.getTagName().equals(WHOLE_READ_TAG)){
                if(tag.getTagValue().contains(FAKE_READ_TYPE)){
                    isFakeRead=true;
                    break;
                }
            }
        }
        if(!isFakeRead){
            super.visitPhd(id, bases, qualities, positions, comments,tags);
        }
    }

    @Override
    public synchronized void visitBeginSequence(String id) {
        
        super.visitBeginSequence(id);
    }

    
}
