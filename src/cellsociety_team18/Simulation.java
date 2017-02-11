package cellsociety_team18;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game_of_life.GameOfLifeGame;
import grids.Grid;
import grids.HexagonGrid;
import grids.RectGrid;
import grids.TriangleGrid;
import segregation.SegregationGame;
import slime.SlimeGame;
import sugarscape.SugarScapeGame;
import wator.WatorGame;
import wildfire.WildfireGame;

/**
 * @author Nikita Zemlevskiy This class controls the current simulation being
 *         held. Instance variables of the current game, the current grid, and
 *         all possible games and grids are maintained.
 */
public class Simulation {
	private Map<String, Game> games = new HashMap<String, Game>() {
		{
			put("Wator", new WatorGame());
			put("Segregation", new SegregationGame());
			put("Wildfire", new WildfireGame());
			put("GameOfLife", new GameOfLifeGame());
			put("SugarScape", new SugarScapeGame());
			put("Slime", new SlimeGame());
		}
	};
	private Map<String, Grid> grids = new HashMap<String, Grid>() {
		{
			put("Square", new RectGrid());
			put("Triangle", new TriangleGrid());
			put("Hexagon", new HexagonGrid());
		}
	};
	private Grid grid;
	private Game game;
	private ArrayList<Map<String, Double>> proportions = new ArrayList<Map<String, Double>>();

	/**
	 * Initialize a new simulation.
	 * 
	 * @param gameName
	 *            The name of the game being played
	 * @param size
	 *            the size in cells across that the simulation should be.
	 * @return new simulation with the specified parameters
	 */
	public Simulation(String gameName) {
		game = games.get(gameName);
		game.setup();
	}
	
	public void setupGrid(int size, String gridType, int numNeighbors) {
		grid = grids.get(gridType);
		grid.setup(game, size, numNeighbors);
	}

	/**
	 * Step the simulation.
	 */
	public void step() {
		List<Cell> cells = grid.getCellsAsList();
		Collections.shuffle(cells);
		for (Cell cell : cells) {
			cell.chooseState();
		}
		for (Cell cell : cells) {
			cell.updateState();
		}
		recordProportions();
	}
	
	private void recordProportions() {
		proportions.add(grid.getProportions(game.getStates()));
	}

	/**
	 * Randomize the order of cells stored in the grid.
	 */
	public void shuffle() {
		grid.shuffle(game);
	}

	public Grid getGrid() {
		return grid;
	}

	public Game getGame() {
		return game;
	}
	
	public ArrayList<Map<String, Double>> getProportions() {
		return proportions;
	}
	
}
