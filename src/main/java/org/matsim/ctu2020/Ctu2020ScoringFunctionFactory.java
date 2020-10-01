/**
 * 
 */
package org.matsim.ctu2020;

import javax.inject.Inject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelMoneyScoring;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;
import org.matsim.core.scoring.functions.SubpopulationScoringParameters;

/**
 * @author stefanopenazzi
 *
 */
public class Ctu2020ScoringFunctionFactory implements ScoringFunctionFactory {

	
	private final Config config;
	private Network network;

	private final ScoringParametersForPerson params;

	public Ctu2020ScoringFunctionFactory( final Scenario sc ) {
		this( sc.getConfig(), new SubpopulationScoringParameters( sc ) , sc.getNetwork() );
	}

	@Inject
	Ctu2020ScoringFunctionFactory(Config config, ScoringParametersForPerson params, Network network) {
		this.config = config;
		this.params = params;
		this.network = network;
	}
	
	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		final ScoringParameters parameters = params.getScoringParameters( person );

		SumScoringFunction sumScoringFunction = new SumScoringFunction();
		sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring( parameters ));
		//sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring( parameters , this.network, config.transit().getTransitModes() ));
		sumScoringFunction.addScoringFunction(new Ctu2020LegScoring( parameters , person));
		sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring( parameters ));
		sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring( parameters ));
		return sumScoringFunction;
	}

}
