package user_interface;

import cellsociety_team18.Simulation;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author elliott
 * This class is a window that displays a simulation grid.
 */
public class SimulationView extends Stage {
	
	private DisplayGrid grid;
	private Simulation simulation;

	/**
	 * @param simulation The simulation in question.
	 * @param grid The grid to display the visualization on.
	 * @param size The size of the grid.
	 * @param number The identifier of the simulation.
	 * @return A simulation view.
	 */
	public SimulationView(Simulation simulation, DisplayGrid grid, int size, int number) {
		this.grid = grid;
		this.simulation = simulation;
        setResizable(false);
        setTitle("Simulation " + Integer.toString(number) + " - " + simulation.getGame().getTitle());
        setScene(new Scene(grid, size, size));
        show();
	}
	
	public Simulation getSimulation() {
		return simulation;
	}
	
	/**
	 * Updates the visualization.
	 */
	public void update() {
		grid.update();
	}
	
}
