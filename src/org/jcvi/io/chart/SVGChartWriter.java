/*
 * Created on Mar 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.chart;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
public class SVGChartWriter implements ChartWriter {
    private File outfile;
    private Rectangle bounds;
    
    public SVGChartWriter(File outFile, Dimension chartDimensions){
        this.outfile = outFile;
        this.bounds = new Rectangle(chartDimensions);
    }
    @Override
    public void write(JFreeChart chart) throws IOException {
     // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // draw the chart in the SVG generator
        chart.draw(svgGenerator, bounds);

        // Write svg file
        OutputStream outputStream = new FileOutputStream(outfile);
        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
        svgGenerator.stream(out, true /* use css */);                       
        outputStream.flush();
        outputStream.close();


    }

}
