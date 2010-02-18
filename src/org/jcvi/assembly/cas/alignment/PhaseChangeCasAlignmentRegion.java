/*
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

public class PhaseChangeCasAlignmentRegion implements CasAlignmentRegion{
    private final byte phaseChange;
    public PhaseChangeCasAlignmentRegion(byte phaseChange){
        this.phaseChange = phaseChange;
    }
    @Override
    public long getLength() {
        //length is always 0?
        return 0L;
    }

    @Override
    public CasAlignmentRegionType getType() {
        return CasAlignmentRegionType.PHASE_CHANGE;
    }
    
    public byte getPhaseChange() {
        return phaseChange;
    }

}
