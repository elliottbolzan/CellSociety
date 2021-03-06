package games.segregation;

import java.util.ArrayList;
import java.util.Arrays;

import cellsociety_team18.Game;
import cellsociety_team18.State;
import games.EmptyState;

/**
 * @author Nikita Zemlevskiy This class is the implementation of Schelling's
 *         model of segregation game. It contains setup related to segregation
 *         and method to get a random segregation state.
 */
public class SegregationGame extends Game {

	@Override
	public void setup() {
		setParameters("satisfaction", "percentPopulation1", "percentPopulation2", "population1Color", "population2Color", "emptyColor");
	}
	
	@Override
	public void setStates() {
		getStates().put("Population 1", new PopulationOne(this));
		getStates().put("Population 2", new PopulationTwo(this));
		getStates().put("Empty", new EmptyState(this));
	}
	
	/**
	 * @return a new state probabilistically.
	 */
	@Override
	public State getStateProbabilistically() {
		double rand = Math.random();
		if (rand < getSettings().getDoubleParameter("percentPopulation1"))
			return new PopulationOne(this);
		if (rand >= getSettings().getDoubleParameter("percentPopulation1") && rand < (getSettings().getDoubleParameter("percentPopulation1") + getSettings().getDoubleParameter("percentPopulation2")))
			return new PopulationTwo(this);
		return new EmptyState(this);
	}
	
	/**
	 * @return a new state randomly.
	 */
	@Override
	public State getStateRandomly() {
		return getStateRandomly(new ArrayList<>(Arrays.asList(new EmptyState(this), new PopulationOne(this), new PopulationTwo(this))));
	}

}
