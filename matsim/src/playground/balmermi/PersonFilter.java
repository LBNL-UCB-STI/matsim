/* *********************************************************************** *
 * project: org.matsim.*
 * ScenarioParsing.java
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.matsim.basic.v01.Id;
import org.matsim.gbl.Gbl;
import org.matsim.network.Link;
import org.matsim.network.NetworkLayer;
import org.matsim.network.Node;
import org.matsim.plans.MatsimPlansReader;
import org.matsim.plans.Plans;
import org.matsim.plans.PlansReaderI;
import org.matsim.plans.PlansWriter;
import org.matsim.plans.filters.PersonIntersectAreaFilter;
import org.matsim.utils.geometry.shared.Coord;

public class PersonFilter {

	//////////////////////////////////////////////////////////////////////
	// test run 01
	//////////////////////////////////////////////////////////////////////

	public static void dilZhFilter(String[] args) {

		System.out.println("running dilZhFilter... " + (new Date()));

		Gbl.createConfig(args);
//		Scenario.setUpScenarioConfig();
//		World world = Scenario.readWorld();
//		Facilities facilities = Scenario.readFacilities();
		NetworkLayer network = Scenario.readNetwork();
//		Counts counts = Scenario.readCounts();
//		Matrices matrices = Scenario.readMatrices();
//		Plans plans = Scenario.readPlans();

		//////////////////////////////////////////////////////////////////////

		double radius = 30000.0;
		final Coord center = new Coord(683518.0,246836.0);
		final Map<Id, Link> areaOfInterest = new HashMap<Id, Link>();
		System.out.println("  => area of interest (aoi): center=" + center + "; radius=" + radius);

		System.out.println("  extracting links of the aoi... " + (new Date()));
		for (Link link : network.getLinks().values()) {
			final Node from = link.getFromNode();
			final Node to = link.getToNode();
			if ((from.getCoord().calcDistance(center) <= radius) || (to.getCoord().calcDistance(center) <= radius)) {
				System.out.println("    link " + link.getId().toString());
				areaOfInterest.put(link.getId(),link);
			}
		}
		System.out.println("  done. " + (new Date()));
		System.out.println("  => aoi contains: " + areaOfInterest.size() + " links.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("  reading, filtering and writing population... " + (new Date()));
		Plans plans = new Plans(Plans.USE_STREAMING);
		PlansReaderI plansReader = new MatsimPlansReader(plans);
		PlansWriter plansWriter = new PlansWriter(plans);
		PersonIntersectAreaFilter filter = new PersonIntersectAreaFilter(plansWriter,areaOfInterest);
		filter.setAlternativeAOI(center,radius);
		plans.addAlgorithm(filter);
		plansReader.readFile(Gbl.getConfig().plans().getInputFile());
		plansWriter.writeEndPlans();
		plans.printPlansCount();
		System.out.println("  done. " + (new Date()));
		System.out.println("  => filtered persons: " + filter.getCount());

		//////////////////////////////////////////////////////////////////////

//		Scenario.writePlans(plans);
//		Scenario.writeMatrices(matrices);
//		Scenario.writeCounts(counts);
//		Scenario.writeNetwork(network);
//		Scenario.writeFacilities(facilities);
//		Scenario.writeWorld(world);
//		Scenario.writeConfig();

		System.out.println("done. " + (new Date()));
	}

	//////////////////////////////////////////////////////////////////////
	// main
	//////////////////////////////////////////////////////////////////////

	public static void main(final String[] args) {
		dilZhFilter(args);
	}
}
