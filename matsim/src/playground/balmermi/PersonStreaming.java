/* *********************************************************************** *
 * project: org.matsim.*
 * PersonStreaming.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package playground.balmermi;

import org.matsim.gbl.Gbl;
import org.matsim.network.NetworkLayer;
import org.matsim.plans.MatsimPlansReader;
import org.matsim.plans.Plans;
import org.matsim.plans.PlansReaderI;
import org.matsim.plans.PlansWriter;

public class PersonStreaming {

	public static void run(String[] args) {

		System.out.println("person streaming...");
		
		Gbl.createConfig(args);
//		Scenario.setUpScenarioConfig();
		NetworkLayer network = Scenario.readNetwork();

		//////////////////////////////////////////////////////////////////////

		System.out.println("  setting up plans objects...");
		Plans plans = new Plans(Plans.USE_STREAMING);
		PlansWriter plansWriter = new PlansWriter(plans);
		PlansReaderI plansReader = new MatsimPlansReader(plans);
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("  adding person modules... ");
//		PersonSubTourAnalysis psta = new PersonSubTourAnalysis();
//		plans.addAlgorithm(psta);
//		PersonInitDemandSummaryTable pidst = new PersonInitDemandSummaryTable("output/output_persons.txt");
//		plans.addAlgorithm(pidst);
//		plans.addAlgorithm(new PersonCalcTripDistances());
//		PersonTripSummaryTable ptst = new PersonTripSummaryTable("output/output_trip-summary-table.txt");
//		plans.addAlgorithm(ptst);
//		final FreespeedTravelTimeCost timeCostCalc = new FreespeedTravelTimeCost();
//		PreProcessLandmarks preprocess = new PreProcessLandmarks(timeCostCalc);
//		preprocess.run(network);
//		plans.addAlgorithm(new PlansCalcRouteLandmarks(network, preprocess, timeCostCalc, timeCostCalc));
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("  reading, processing, writing plans...");
		plans.addAlgorithm(plansWriter);
		plansReader.readFile(Gbl.getConfig().plans().getInputFile());
		plans.printPlansCount();
		plansWriter.write();
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

//		System.out.println("  finishing algorithms... ");
//		psta.writeSubtourTripCntVsModeCnt("output/TripsPerSubtourVsModeCnt.txt");
//		psta.writeSubtourDistVsModeCnt("output/SubtourDistVsModeCnt.txt");
//		psta.writeSubtourTripCntVsSubtourCnt("output/SubtourTripCntVsSubtourCnt.txt");
//		psta.writeSubtourDistVsModeDistSum("output/SubtourDistVsModeDistSum.txt");
//		pidst.close();
//		ptst.close();
//		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("done.");
		System.out.println();
	}

	//////////////////////////////////////////////////////////////////////
	// main
	//////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		Gbl.startMeasurement();

		run(args);

		Gbl.printElapsedTime();
	}
}
