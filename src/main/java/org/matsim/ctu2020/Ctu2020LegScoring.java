/**
 * 
 */
package org.matsim.ctu2020;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scoring.functions.ModeUtilityParameters;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.core.utils.misc.Time;


/**
 * @author stefanopenazzi
 *
 */
public class Ctu2020LegScoring implements org.matsim.core.scoring.SumScoringFunction.LegScoring, org.matsim.core.scoring.SumScoringFunction.ArbitraryEventScoring { {

}

	private static final Logger log = Logger.getLogger( Ctu2020LegScoring.class ) ;
	protected double score;
	
	protected final ScoringParameters params;
	protected Person person;
	
	
	private double lastActivityEndTime = Time.getUndefinedTime();

	public Ctu2020LegScoring(final ScoringParameters params, Person person) {
		this.params = params;
		this.person = person;
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public double getScore() {
		return this.score;
	}
	
//	This is called at each event triggered into the mobility simulation 
	@Override
	public void handleEvent(Event event) {
		
//		By way of example, to handle an event of type ActivityEndEvent an if statement
//		that checks the instance of the object can be used.
		
		if ( event instanceof ActivityEndEvent ) {
			
			this.lastActivityEndTime = event.getTime() ;
		}
		
	}
	
	// This is called to handle events of type 
	@Override
	public void handleLeg(Leg leg) {
		// check if the leg.getDepartureTime() is defined otherwise it throws an exception
		Gbl.assertIf( !Time.isUndefinedTime( leg.getDepartureTime() ) ) ;
		Gbl.assertIf( !Time.isUndefinedTime( leg.getTravelTime() ) );

		//call the method calcLegScoreCtu2020
		double legScore = calcLegScoreCtu2020(leg.getDepartureTime(), leg.getDepartureTime() + leg.getTravelTime(), leg);
		
		this.score += legScore;
		
	}
	
	protected double calcLegScoreCtu2020(final double departureTime, final double arrivalTime, final Leg leg) {
		
		double tempScore = 0.0;
		double travelTime = arrivalTime - departureTime; // travel time in seconds	
		
		//parameters for a specific mode can be retrieved from the object of type 
		//ScoringParameters
		ModeUtilityParameters modeParams = this.params.modeParams.get(leg.getMode());
		
		//check if modeParams is not null. This can happen if the specific mode
		//is not declared in the config file
		//
		//		<parameterset type="modeParams">
		//		   ...
		//		</parameterset>
		
		if (modeParams == null) {
			modeParams = this.params.modeParams.get(TransportMode.walk);
			
			//in case all the modes parameters are required to be specified, it is 
			//possible to run an exception that stops the simulation at run time
			//throw new RuntimeException("just encountered mode for which no scoring parameters are defined: " + leg.getMode()) ;
			
		}
		
		tempScore += travelTime * modeParams.marginalUtilityOfTraveling_s;
		
		// by default all the parameters in an object of type ModeUtilityParameters
		// have value equal to 0.0. This value overwritten with the value in the 
		// config file
		if (modeParams.monetaryDistanceCostRate != 0.0) {
	
			Route route = leg.getRoute();
			double dist = route.getDistance(); // distance in meters
			
			// If a person attribute is necessary to score the leg, it is possible to 
			// retrieve it from the attribute map in the Person
			// (int)person.getAttributes().getAsMap().get("age")
			// these attributes are defined for each person in the Siouxfall_population.xml
			tempScore += (modeParams.monetaryDistanceCostRate * dist ) * (int)person.getAttributes().getAsMap().get("age")*0.0001 + modeParams.dailyUtilityConstant;
		    System.out.println();
		}
		tempScore += modeParams.constant;
		
		tempScore += modeParams.dailyUtilityConstant; 
			
		return tempScore;
	}
}
