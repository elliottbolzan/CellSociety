package wator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cellsociety_team18.Cell;
import cellsociety_team18.State;

/**
 * @author elliott This class represents an Agent in the Wator game.
 */
public abstract class AgentState extends State {

	private int reproductionTime;
	private int survivalTime = 0;

	/**
	 * @param cell The cell that owns this state.
	 * @param reproductionTime The time elapsed before reproduction takes place.
	 */
	public AgentState(Cell cell, int reproductionTime) {
		super(cell);
		this.reproductionTime = reproductionTime;
	}
	
	public void setSurvivalTime(int survivalTime) {
		this.survivalTime = survivalTime;
	}
		
	/**
	 * @return A random cell that the agent can move to.
	 */
	private Cell getPossibleMove() {
		List<Cell> options = getCell().getNeighbors();
		Iterator<Cell> i = options.iterator();
		while (i.hasNext()) {
			Cell cell = i.next();
			if (this instanceof FishState && !(cell.getNextState() instanceof EmptyState) 
					|| this instanceof SharkState && cell.getState() instanceof SharkState) {
				i.remove();
			}
		}
		Collections.shuffle(options);
		if (options.size() == 0) 
			return null;
		return options.get(0);
	}
	
	/**
	 * @return The state that the agent replaced.
	 */
	public State moveTo(AgentState state) {
		survivalTime++;
		Cell destination = getPossibleMove();
		if (destination != null) {
			if (survivalTime >= reproductionTime) {
				survivalTime = 0;
				return replace(destination, state);
			}
			getCell().setNextState(new EmptyState(getCell()));
			return replace(destination, state);
		}
		return null;
	}
	
	/**
	 * Where the replacing is actually done.
	 * @return The replaced state.
	 */
	private State replace(Cell neighbor, AgentState state) {
		state.setCell(neighbor);
		state.setSurvivalTime(survivalTime);
		State replaced = neighbor.getNextState();
		neighbor.setNextState(state);
		return replaced;
	}

	@Override
	public abstract void chooseState();

}