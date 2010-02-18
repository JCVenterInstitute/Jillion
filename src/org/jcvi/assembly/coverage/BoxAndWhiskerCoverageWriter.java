/*
 * Created on Feb 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jcvi.assembly.Placed;
import org.jcvi.io.IOUtil;
import org.jcvi.io.chart.ChartWriter;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;

public class BoxAndWhiskerCoverageWriter<T extends Placed> implements MultipleCoverageWriter<T> {
    private File rawDataFile;
    private String id;
    private DefaultBoxAndWhiskerXYDataset dataSet;
    private ConcurrentHashMap<Integer, List<Integer>> coverageMapList;
    private ChartWriter chartWriter;
    public BoxAndWhiskerCoverageWriter(String filePrefix, String id, ChartWriter chartWriter){
        rawDataFile= new File(filePrefix+".boxAndWiskards_data.xls");
        this.id = id;
        this.chartWriter = chartWriter;
        this.dataSet =createBoxAndWhiskerXYDataset(id);
        coverageMapList = new ConcurrentHashMap<Integer, List<Integer>>();
    }

    protected DefaultBoxAndWhiskerXYDataset createBoxAndWhiskerXYDataset(String id) {
         return new DefaultBoxAndWhiskerXYDataset("Coverage for "+ id);
    }
    
    @Override
    public void add(String id, CoverageMap<CoverageRegion<T>> coverageMap) {
        for(CoverageRegion<T>region : coverageMap){
            Integer coverage = Integer.valueOf(region.getCoverage());
            for(long i=region.getStart(); i<= region.getEnd(); i++){
                Integer coordinate = Integer.valueOf((int)i);
                coverageMapList.putIfAbsent(coordinate, new ArrayList<Integer>());
                coverageMapList.get(coordinate).add(Integer.valueOf(coverage));
            }
        }
        
        
       
        
    }

    @Override
    public void close() throws IOException {
        System.out.println(id + " coverage Map list size = " + coverageMapList.size());
        for(Entry<Integer, List<Integer>> entry : getSortedEntrySet()){
            BoxAndWhiskerItem item = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(entry.getValue());
            dataSet.add(new Date(entry.getKey()), item);
        }
        final XYBoxAndWhiskerRenderer renderer = createXYBoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setSeriesPaint(0, Color.blue);
        final NumberAxis xAxis = new NumberAxis("Coordinate");
        final NumberAxis yAxis = new NumberAxis("Coverage");
        final Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        final Font tickFont = new Font("SansSerif", Font.BOLD, 10);       
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelFont(labelFont);
        final XYPlot plot = new XYPlot(dataSet, xAxis, yAxis, renderer);

        
        
        final JFreeChart chart = new JFreeChart(
            "Coverage Box Plot for " +id,
            labelFont,
            plot,
            false
        );
        chartWriter.write(chart);
      /*  ChartUtilities.saveChartAsPNG(fileToWrite, chart, 
                coverageMapList.size()*10, maxCoverage/2);
                */
        HSSFWorkbook wb = new HSSFWorkbook();
        // create a new sheet
        HSSFSheet s = wb.createSheet();      
        HSSFRow row =s.createRow(0);
        row.createCell(0).setCellValue(new HSSFRichTextString("coordinate"));
        row.createCell(1).setCellValue(new HSSFRichTextString("min outlier"));
        row.createCell(2).setCellValue(new HSSFRichTextString("min regular value"));
        row.createCell(3).setCellValue(new HSSFRichTextString("25 percentile"));
        row.createCell(4).setCellValue(new HSSFRichTextString("mean"));
        row.createCell(5).setCellValue(new HSSFRichTextString("median"));
        row.createCell(6).setCellValue(new HSSFRichTextString("75 percentile"));
        row.createCell(7).setCellValue(new HSSFRichTextString("max regular value"));
        row.createCell(8).setCellValue(new HSSFRichTextString("max outlier"));
        row.createCell(9).setCellValue(new HSSFRichTextString("number of Samples"));
        row.createCell(10).setCellValue(new HSSFRichTextString("raw values"));
        int i=1;
        for(Entry<Integer, List<Integer>> entry : getSortedEntrySet()){  
            final Integer coordinate = entry.getKey();            
            BoxAndWhiskerItem item =dataSet.getItem(0, coordinate);
            HSSFRow itemRow =s.createRow(i);
            itemRow.createCell(0).setCellValue(coordinate.intValue());
            itemRow.createCell(1).setCellValue(item.getMinOutlier().floatValue());
            itemRow.createCell(2).setCellValue(item.getMinRegularValue().floatValue());
            itemRow.createCell(3).setCellValue(item.getQ1().floatValue());
            itemRow.createCell(4).setCellValue(item.getMean().floatValue());
            itemRow.createCell(5).setCellValue(item.getMedian().floatValue());
            itemRow.createCell(6).setCellValue(item.getQ3().floatValue());
            itemRow.createCell(7).setCellValue(item.getMaxRegularValue().floatValue());
            itemRow.createCell(8).setCellValue(item.getMaxOutlier().floatValue());
            final List<Integer> list = entry.getValue();
            Collections.sort(list);
            itemRow.createCell(9).setCellValue( list.size());
            itemRow.createCell(10).setCellValue( new HSSFRichTextString(list.toString()));
            i++;
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(rawDataFile);
       try{
           wb.write(fileOutputStream);
       }finally{
           IOUtil.closeAndIgnoreErrors(fileOutputStream);
       }
    }

    protected XYBoxAndWhiskerRenderer createXYBoxAndWhiskerRenderer() {
        final XYBoxAndWhiskerRenderer renderer = new XYBoxAndWhiskerRenderer();
        return renderer;
    }

    private TreeSet<Entry<Integer, List<Integer>>> getSortedEntrySet() {
        TreeSet<Entry<Integer, List<Integer>>>sortedSet = new TreeSet(new Comparator<Entry<Integer, List<Integer>>>(){

            @Override
            public int compare(Entry<Integer, List<Integer>> o1,
                    Entry<Integer, List<Integer>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
            
        });
        sortedSet.addAll(coverageMapList.entrySet());
        return sortedSet;
    }

    public static void main(String args[]){
        BoxAndWhiskerItem item = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(Arrays.asList(
                4.0, 19.0, 16.0, 38.0, 24.0, 68.0, 10.0, 25.0, 20.0, 48.0, 17.0, 61.0, 16.0, 58.0, 24.0, 2.0, 2.0, 23.0, 61.0, 58.0, 52.0, 141.0, 101.0, 26.0, 37.0, 47.0, 6.0, 1.0, 35.0, 185.0
                ));
       
        System.out.println(item.getMinOutlier());
        System.out.println(item.getMinRegularValue());
        System.out.println(item.getQ1());
        System.out.println(item.getMean());
        System.out.println(item.getMedian());
        System.out.println(item.getQ3());
        System.out.println(item.getMaxRegularValue());
        System.out.println(item.getMaxOutlier());
      
    
    }
}
