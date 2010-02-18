/*
 * Created on Mar 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.chart;

import java.io.IOException;

import org.jfree.chart.JFreeChart;

public interface ChartWriter {

    void write(JFreeChart chart)throws IOException;
}
