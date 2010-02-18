/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.issue.QualityClassContigMapIssue;
import org.jcvi.assembly.contig.QualityClassRegion;

import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassContigMapAnalysisProcess<P extends PlacedRead> extends AbstractContigAnalysisProcess<P>{


    private final QualityClass qualityClassThreshold;
    public QualityClassContigMapAnalysisProcess(ContigCheckReportBuilder builder,ContigCheckerStruct<P> struct, QualityClass qualityClassThreshold){
        super(struct, builder);
        this.qualityClassThreshold = qualityClassThreshold;
       
    }
    @Override
    public void run() {
        for(QualityClassRegion region :getStruct().getQualityClassMap().getQualityClassRegions()){
            if(region.getQualityClass().compareTo(qualityClassThreshold)>0){
                getBuilder().addAnalysisIssue(new QualityClassContigMapIssue(region));
            }
        }
        
    }


}
