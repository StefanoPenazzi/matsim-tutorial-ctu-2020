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

	/*
	 * The annotation @Inject over the constructor means that this object can be
	 * created by using Guice. This means that all the parameters used by the
	 * constructor are injected themselves letting Guice searches for the bind in
	 * which is specified the link between the interface and its implementation.
	 */
	
	@Inject
	Ctu2020ScoringFunctionFactory(Config config, ScoringParametersForPerson params, Network network) {
		this.config = config;
		this.params = params;
		this.network = network;
	}
	
	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		
		/*
		 * The parameters necessary to score the plan of a certain type of person are
		 * saved in the object params of type ScoringParametersForPerson. This object
		 * has been passed as a parameter in the constructor, this means that comes from
		 * an injection. The object contains a method that interacts with a map from
		 * which these parameters can be retrieved params.getScoringParameters.
		 */
		
		final ScoringParameters personParameters = params.getScoringParameters( person );

		/*
		 * SumScoringFunction is a class that can host different components of the utility
		 * function. The SumScoringFunction method addScoringFunction allows to add new components.
		 * Each component is a class that implements the interface 
		 * 
		 *   org.matsim.core.scoring.SumScoringFunction.ActivityScoring 
		 * 
		 * If this component scores an activity
		 * or the class has to implement the interfaces 
		 * 
		 *   org.matsim.core.scoring.SumScoringFunction.LegScoring
		 *   org.matsim.core.scoring.SumScoringFunction.ArbitraryEventScoring
		 * 
		 * If this component scores a leg
		 *(Always check the default CharyparNagelActivityScoring or CharyparNagelLegScoring
		 *to be sure what interfaces the class has to implements)
		 * 
		 * In this example CharyparNagelLegScoring has been replaced with Ctu2020LegScoring
		 * All the others components are the same used in the default version
		 */
		
		
		
		SumScoringFunction sumScoringFunction = new SumScoringFunction();
		sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring( personParameters ));
		//sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring( parameters , this.network, config.transit().getTransitModes() ));
		sumScoringFunction.addScoringFunction(new Ctu2020LegScoring( personParameters , person));
		sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring( personParameters ));
		sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring( personParameters ));
		return sumScoringFunction;
	}

}
