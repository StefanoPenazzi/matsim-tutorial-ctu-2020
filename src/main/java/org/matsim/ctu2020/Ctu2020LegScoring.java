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
	
	@Override
	public void handleEvent(Event event) {
		
		if ( event instanceof ActivityEndEvent ) {
			
			this.lastActivityEndTime = event.getTime() ;
		}
		
	}
	
	@Override
	public void handleLeg(Leg leg) {
		Gbl.assertIf( !Time.isUndefinedTime( leg.getDepartureTime() ) ) ;
		Gbl.assertIf( !Time.isUndefinedTime( leg.getTravelTime() ) );

		double legScore = calcLegScoreCtu2020(leg.getDepartureTime(), leg.getDepartureTime() + leg.getTravelTime(), leg);
		
		this.score += legScore;
		
	}
	
	protected double calcLegScoreCtu2020(final double departureTime, final double arrivalTime, final Leg leg) {
		double tempScore = 0.0;
		double travelTime = arrivalTime - departureTime; // travel time in seconds	
		ModeUtilityParameters modeParams = this.params.modeParams.get(leg.getMode());
		if (modeParams == null) {
			modeParams = this.params.modeParams.get(TransportMode.walk);
			//throw new RuntimeException("just encountered mode for which no scoring parameters are defined: " + leg.getMode()) ;
			
		}
		tempScore += travelTime * modeParams.marginalUtilityOfTraveling_s;
		
		if (modeParams.monetaryDistanceCostRate != 0.0) {
			Route route = leg.getRoute();
			double dist = route.getDistance(); // distance in meters
			tempScore += (modeParams.monetaryDistanceCostRate * dist ) * (int)person.getAttributes().getAsMap().get("age")*0.0001 + modeParams.dailyUtilityConstant;
		    System.out.println();
		}
		tempScore += modeParams.constant;
		
		tempScore += modeParams.dailyUtilityConstant; 
			
		return tempScore;
	}
}
