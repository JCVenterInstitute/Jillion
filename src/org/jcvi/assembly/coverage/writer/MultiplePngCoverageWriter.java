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
 * Created on Feb 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.writer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcvi.assembly.Placed;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MultiplePngCoverageWriter<T extends Placed> implements MultipleCoverageWriter<T>{
    private final File fileToWrite;
    private final String id;
    private final XYSeriesCollection dataSet;
    private final Dimension dim;
    
    public MultiplePngCoverageWriter(File fileToWrite, String id){
        this(fileToWrite, id, new Dimension(1000,500));
    }
    public MultiplePngCoverageWriter(File fileToWrite, String id, Dimension dim){
        this.fileToWrite = fileToWrite;
        this.id = id;
        this.dataSet = new XYSeriesCollection();
        this.dim = dim;
    }
    @Override
    public void add(String id,CoverageMap<CoverageRegion<T>> coverageMap) {
        dataSet.addSeries(createSeriesFor(id,coverageMap.getRegions()));
    }
    public void add(String id,List<CoverageRegion<T>> coverageMap) {
        dataSet.addSeries(createSeriesFor(id,coverageMap));
    }
    private XYSeries createSeriesFor(String id, List<CoverageRegion<T>> write) {
        XYSeries series = new XYSeries(id);
        for(CoverageRegion region : write){
            series.add(region.getStart(), region.getCoverage());
            series.add(region.getEnd(), region.getCoverage());
        }
        return series;
    }

    @Override
    public void close() throws IOException {
        JFreeChart chart = ChartFactory.createXYAreaChart("Coverage Map of "+id, "coordinate", "coverage depth", dataSet,
                PlotOrientation.VERTICAL, true, false, false);
        final XYPlot plot = chart.getXYPlot();
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(false);
        plot.setBackgroundPaint(Color.white);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setSeriesPaint(2, Color.green);
        renderer.setSeriesPaint(3, Color.BLACK);
        renderer.setSeriesPaint(4, Color.orange);
        
        renderer.setBaseStroke(new BasicStroke(3));
       
        ChartUtilities.saveChartAsPNG(fileToWrite, chart, dim.width,dim.height);
        
    }

}
