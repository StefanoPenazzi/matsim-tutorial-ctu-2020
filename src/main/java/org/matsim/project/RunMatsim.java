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
package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.ctu2020.Ctu2020ScoringFunctionFactory;

/**
 * @author nagel
 *
 */
public class RunMatsim{

	public static void main(String[] args) {

		Config config;
		if ( args==null || args.length==0 || args[0]==null ){
			config = ConfigUtils.loadConfig( "scenarios/siouxfalls-2014/config_default.xml" );
		} else {
			config = ConfigUtils.loadConfig( args );
		}
		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

		// possibly modify config here
		
		// ---
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		
		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
		
		// possibly modify controler here
		
		/* After that a new Controler is created, it is possible
		 * to make the override of the default modules by using the 
		 * method addOverridingModule provided by the Controler.
		 * 
		 * addOverridingModule requires as input parameter an object
		 * of type AbstractModule. 
		 * 
		 * In the following code, a new object of type AbstractModule
		 * is directly created as a parameter of addOverridingModule.
		 * @Override the method install of the class AbstractModule adding
		 * the necessary bind.
		 * Several bind can be added inside the install method (in the
		 * example only one is used).
		 * 
		 * In this case 
		 * bind(ScoringFunctionFactory.class).to(Ctu2020ScoringFunctionFactory.class);
		 * replaces the default bind 
		 * bind(ScoringFunctionFactory.class).to(CharyparNagelScoringFunctionFactory.class);
		 * 
		 * Because of this bind, every time the interface ScoringFunctionFactory 
		 * is used as a parameter in a class constructor annotated with @Inject
		 * the implementation Ctu2020ScoringFunctionFactory will be used.
		 *  */
		
		controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                bind(ScoringFunctionFactory.class).to(Ctu2020ScoringFunctionFactory.class);
            }
        });
		// ---
		
		controler.run();
	}
	
}
