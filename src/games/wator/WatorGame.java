package games.wator;

import cellsociety_team18.Game;
import cellsociety_team18.State;
import games.EmptyState;

/**
 * @author Nikita Zemlevskiy This class is the implementation of the Wator game.
 *         It contains setup related to Wator and method to get a random Wator
 *         state.
 */
public class WatorGame extends Game {

	@Override
	public void setup() {
		setParameters("percentFish", "percentSharks", "sharkReprodTime", "sharkStartEnergy", "sharkEnergyPerFish", "fishReprodTime", "fishColor", "sharkColor", "emptyColor");
	}
	
	@Override
	public void setStates() {
		getStates().put("Fish", new FishState(this));
		getStates().put("Shark", new SharkState(this));
		getStates().put("Empty", new EmptyState(this));
	}
	
	/**
	 * Get a random Wator state.
	 * @return new random state.
	 */
	@Override
	public State getRandomState() {
		double rand = Math.random();
		if (rand < getDoubleParameter("percentFish"))
			return new FishState(this);
		else if (rand >= getDoubleParameter("percentFish") && rand < (getDoubleParameter("percentFish") + getDoubleParameter("percentSharks")))
			return new SharkState(this);
		return new EmptyState(this);
	}

}