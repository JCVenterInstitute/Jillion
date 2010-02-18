/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.jcvi.assembly.Placed;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PngCoverageWriter<T extends Placed> implements CoverageWriter<T>{
    private File fileToWrite;
    private String title;
    private  Dimension dim;
    public PngCoverageWriter(File fileToWrite, String title){
        this(fileToWrite, title, new Dimension(500,1000));
    }
    public PngCoverageWriter(File fileToWrite, String title, Dimension dim){
        this.fileToWrite = fileToWrite;
        this.title = title;
        this.dim = dim;
    }
    @Override
    public void write(CoverageMap<CoverageRegion<T>> write) throws IOException {
        XYSeries series = new XYSeries("Coverage Map of");
        for(CoverageRegion<T> region : write){
            series.add(region.getStart(), region.getCoverage());
            series.add(region.getEnd(), region.getCoverage());
        }       

        XYDataset dataSet = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYAreaChart(title, "coordinate", "coverage depth", dataSet,
                PlotOrientation.VERTICAL, false, false, false);
        final XYPlot plot = chart.getXYPlot();
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        renderer.setSeriesPaint(0, Color.blue);
        
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(false);
        ChartUtilities.saveChartAsPNG(fileToWrite, chart, dim.width,dim.height);
        
        
        
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

}
