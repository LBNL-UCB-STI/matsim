/* *********************************************************************** *
 * project: org.matsim.*
 * DgAvgDeltaScoreIncomeGroupChart
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package playground.dgrether.analysis.charts;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.matsim.core.utils.collections.Tuple;

import playground.dgrether.analysis.population.DgAnalysisPopulation;
import playground.dgrether.analysis.population.DgIncomeClass;
import playground.dgrether.analysis.population.DgPersonData;
import playground.dgrether.utils.charts.DgColorScheme;


/**
 * @author dgrether
 *
 */
public class DgAvgDeltaMoneyGroupChart {
	
	private static final Logger log = Logger.getLogger(DgAvgDeltaMoneyGroupChart.class);
	
	protected DgAnalysisPopulation ana;
	
	protected int numberOfClasses = 10;
	
	protected int groupThreshold = 0;
	
	protected XYSeriesCollection dataset;
	
	protected DgAxisBuilder axisBuilder = new DgDefaultAxisBuilder();

	private LabelGenerator labelGenerator;
	
	public DgAvgDeltaMoneyGroupChart(DgAnalysisPopulation ana, int threshold) {
		this.ana = ana;
		this.labelGenerator = new LabelGenerator();
		this.groupThreshold = threshold;
		this.ana.calculateMinMaxIncome();
		this.dataset = this.createDatasets();
	}
	
	protected Tuple<XYSeries,List<String>> createXYSeries(String title, DgAnalysisPopulation pop) {
		// calculate thresholds for income classes
		DgIncomeClass[] incomeThresholds = new DgIncomeClass[this.numberOfClasses];
		DgAnalysisPopulation[] groups = new DgAnalysisPopulation[this.numberOfClasses];

		double deltaY = this.ana.getMaxIncome() / (this.numberOfClasses -1);
		for (int i = 0; i < incomeThresholds.length; i++) {
			incomeThresholds[i] = new DgIncomeClass((i *deltaY), ((i+1) * deltaY));
			groups[i] = new DgAnalysisPopulation();
		}
		
		for (DgPersonData d : pop.getPersonData().values()) {
			double y = d.getIncome().getIncome();
			int pos = (int) (y / deltaY);
			DgIncomeClass c = incomeThresholds[pos];
			if (!(c.getMin() <= y) && (y <= c.getMax())) {
				throw new IllegalStateException();
			}
			groups[pos].getPersonData().put(d.getPersonId(), d);
		}

		XYSeries series = new XYSeries(title, false, true);
		List<String> labels = new ArrayList<String>();
		for (int i = 0; i < groups.length; i++) {
			int groupSize = groups[i].getPersonData().size();
			if (groupSize < this.groupThreshold){
				continue;
			}
			Double avgScore = IncomeChartUtils.calcAverageMoneyDifference(groups[i], 0);
			if (avgScore != null) {
			  double incomeLocation = incomeThresholds[i].getMin() + (deltaY / 2.0);
			  series.add(incomeLocation, avgScore);
			  labels.add("Groupsize: " + groups[i].getPersonData().size());
			}
		}
		return new Tuple<XYSeries, List<String>>(series, labels);
	}
	

	
	protected XYSeriesCollection createDatasets() {
		XYSeriesCollection ds = new XYSeriesCollection();
		Tuple<XYSeries, List<String>> seriesLabels = this.createXYSeries("Mean "+  '\u0394' + "Delta Money", this.ana);
		ds.addSeries(seriesLabels.getFirst());
		this.labelGenerator.setLabels(0, seriesLabels.getSecond());
		return ds;
	}
	
	public JFreeChart createChart() {
		XYPlot plot = new XYPlot();
		ValueAxis xAxis = this.axisBuilder.createValueAxis("Income [Chf / Year]");
		ValueAxis yAxis = this.axisBuilder.createValueAxis("Delta Money [Chf]");
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		DgColorScheme colorScheme = new DgColorScheme();
		
		XYItemRenderer renderer2;
		renderer2 = new XYLineAndShapeRenderer(true, true);
		renderer2.setSeriesItemLabelsVisible(0, true);
		renderer2.setSeriesItemLabelGenerator(0, this.labelGenerator);
		plot.setDataset(0, this.dataset);
		renderer2.setSeriesStroke(0, new BasicStroke(2.0f));
		renderer2.setSeriesOutlineStroke(0, new BasicStroke(3.0f));
		renderer2.setSeriesPaint(0, colorScheme.getColor(1, "a"));
		plot.setRenderer(0, renderer2);
		
		JFreeChart chart = new JFreeChart("", plot);
		chart.setBackgroundPaint(ChartColor.WHITE);
		chart.getLegend().setItemFont(this.axisBuilder.getAxisFont());
		chart.setTextAntiAlias(true);
		return chart;
	}
	
	public XYSeriesCollection getDataset() {
		return dataset;
	}
	
	public int getNumberOfClasses() {
		return numberOfClasses;
	}

	public void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}
	
	/**
	  * A custom label generator.
	  */
	private static class LabelGenerator extends AbstractXYItemLabelGenerator
	                               implements XYItemLabelGenerator {

		private Map<Integer, List<String>> labels = new HashMap<Integer, List<String>>();

		public LabelGenerator() {
			
		}

		public void setLabels(int series, List<String> labels) {
			this.labels.put(series, labels);
		}

		public String generateLabel(XYDataset dataset, int series, int item) {
			return this.labels.get(series).get(item);
		}
	}

	
	
}
