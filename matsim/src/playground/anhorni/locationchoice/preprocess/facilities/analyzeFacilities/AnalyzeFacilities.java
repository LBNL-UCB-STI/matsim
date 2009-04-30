/* *********************************************************************** *
 * project: org.matsim.*
 * CreateSelectedPlansTables.java
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

package playground.anhorni.locationchoice.preprocess.facilities.analyzeFacilities;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.core.api.facilities.Facilities;
import org.matsim.core.facilities.FacilitiesReaderMatsimV1;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.world.World;

public class AnalyzeFacilities {

	private Facilities facilities;
	private final static Logger log = Logger.getLogger(AnalyzeFacilities.class);
	
	
	public static void main(final String[] args) {

		Gbl.startMeasurement();
		final AnalyzeFacilities analyzer = new AnalyzeFacilities();
		
		World world = Gbl.createWorld();
		log.info("reading the facilities ...");
		Facilities facilities =(Facilities)world.createLayer(Facilities.LAYER_TYPE, null);
		new FacilitiesReaderMatsimV1(facilities).readFile("input/facilities/facilities_KTIYear2.xml.gz");
				
		analyzer.run(facilities);
		Gbl.printElapsedTime();
	}
	
	
		
	public void run(Facilities facilities) {
		this.facilities = facilities;
		this.write("./output/valid/facilities/");
		log.info("finished");
	}
	
	private void write(String outpath) {
			
			CapacityCalculator capacityCalculator = new CapacityCalculator();
			capacityCalculator.calcCapacities(this.facilities);
			try {
				final BufferedWriter outShop = 
					IOUtils.getBufferedWriter(outpath + "facilities_shop_activities.txt");
				final BufferedWriter outGroceryShop = 
					IOUtils.getBufferedWriter(outpath + "facilities_groceryshop_activities.txt");
				final BufferedWriter outLeisure = 
					IOUtils.getBufferedWriter(outpath + "facilities_leisure_activities.txt");
				
				String header = "Type\t#activity facilities\tcapacity\tavg. capacity\tmin capacity\tmax capacity\n";
				
				outShop.write(header);
				outLeisure.write(header);
				outGroceryShop.write(header);
				
				outShop.flush();
				outLeisure.flush();
				outGroceryShop.flush();
				
				this.write(capacityCalculator.getNogaShopFacilities(), outShop);
				this.write(capacityCalculator.getNogaGroceryShopFacilities(), outGroceryShop);
				this.write(capacityCalculator.getNogaLeisureFacilities(), outLeisure);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
	}
	
	
	private void write(TreeMap<String, CapacityPerNOGAType> nogaFacilities,
			BufferedWriter out) {
		
		DecimalFormat formatter = new DecimalFormat("0.00");
		
		try {
			Iterator<CapacityPerNOGAType> cap_it = nogaFacilities.values().iterator();
			while (cap_it.hasNext()) {
				CapacityPerNOGAType capacityPerNOGAType = cap_it.next();
				out.write(capacityPerNOGAType.getType() + "\t" + 
						capacityPerNOGAType.getCnt() + "\t" + 
						capacityPerNOGAType.getSumCapacity() + "\t" + 
						formatter.format(capacityPerNOGAType.getSumCapacity()/capacityPerNOGAType.getCnt()) + "\t" +
						capacityPerNOGAType.getMinCapacity() + "\t" + capacityPerNOGAType.getMaxCapacity() + "\n");
			}
			out.flush();
			out.close();
		}
		catch (final IOException e) {
			Gbl.errorMsg(e);
		}
	}
}
