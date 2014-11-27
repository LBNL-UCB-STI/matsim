/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package playground.imob.feathers2forWithinDayReplanning;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.router.RoutingContext;
import org.matsim.core.router.TripRouterFactory;
import org.matsim.withinday.mobsim.WithinDayEngine;
import org.matsim.withinday.replanning.replanners.NextLegReplanner;
import org.matsim.withinday.replanning.replanners.interfaces.WithinDayDuringActivityReplanner;
import org.matsim.withinday.replanning.replanners.interfaces.WithinDayDuringActivityReplannerFactory;

/**
 * @author nagel
 *
 */
public class NextActivityAppendingReplannerFactory extends WithinDayDuringActivityReplannerFactory {
	private final Scenario scenario;
	private final TripRouterFactory tripRouterFactory;
	private final RoutingContext routingContext;
	
	public NextActivityAppendingReplannerFactory(Scenario scenario, WithinDayEngine withinDayEngine,
			TripRouterFactory tripRouterFactory, RoutingContext routingContext) {
		super(withinDayEngine);
		this.scenario = scenario;
		this.tripRouterFactory = tripRouterFactory;
		this.routingContext = routingContext;
	}

	@Override
	public WithinDayDuringActivityReplanner createReplanner() {
		WithinDayDuringActivityReplanner replanner = new NextActivityAppendingReplanner(super.getId(), scenario, 
				this.getWithinDayEngine().getInternalInterface(),
				this.tripRouterFactory.instantiateAndConfigureTripRouter(routingContext));
		return replanner;
	}

}
