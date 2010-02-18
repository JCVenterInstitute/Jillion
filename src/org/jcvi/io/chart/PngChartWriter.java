/*
 * Created on Mar 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.chart;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class PngChartWriter implements ChartWriter {
    private File outfile;
    private Dimension chartDimensions;
    
    public PngChartWriter(File outFile, Dimension chartDimensions){
        this.outfile = outFile;
        this.chartDimensions = chartDimensions;
    }
    @Override
    public void write(JFreeChart chart) throws IOException {
        ChartUtilities.saveChartAsPNG(outfile, chart, 
               chartDimensions.width, chartDimensions.height);

    }

}
