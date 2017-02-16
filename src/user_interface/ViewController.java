// This entire file is part of my masterpiece.
// Elliott Bolzan

package user_interface;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import cellsociety_team18.Simulation;
import cellsociety_team18.SimulationController;
import cellsociety_team18.State;
import graphic_elements.GraphicPolygon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;

/**
 * @author Elliott Bolzan This class controls the user interface. Specifically,
 *         it controls the control panel, graphing mechanism, and collection of
 *         SimulationViews. It uses a BorderPane to display the different user
 *         interface components.
 */
public class ViewController {

	private final Dimension DEFAULT_SIZE = new Dimension(900, 500);
	private final int GRID_SIZE = 480;
	private final int CONTROL_PANEL_WIDTH = 400;

	private SimulationController mySimulationController = new SimulationController();
	private ArrayList<SimulationView> mySimulationViews = new ArrayList<SimulationView>();

	private PopulationGraph myGraph;
	private Timeline myAnimation;
	private ResourceBundle myResources = ResourceBundle.getBundle("UIStrings");

	/**
	 * This method is automatically called from Main.
	 * 
	 * @param stage
	 *            the main window.
	 * @return a View Controller.
	 */
	public ViewController(Stage stage) {
		stage.setTitle("CellSociety");
		stage.setResizable(false);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.exit(0);
			}
		});
		setupUI(stage);
	}

	/**
	 * Create and start the animation. This is called when the Start button is
	 * pressed, and gives the illusion of movement.
	 * 
	 * @param delay
	 *            the delay between each time step.
	 */
	public void start(int delay) {
		if (mySimulationViews.size() > 0) {
			KeyFrame frame = new KeyFrame(Duration.millis(delay), e -> step());
			myAnimation = new Timeline();
			myAnimation.setCycleCount(Timeline.INDEFINITE);
			myAnimation.getKeyFrames().add(frame);
			myAnimation.play();
		}
	}

	/**
	 * Pause the animation - if such an animation exists.
	 */
	public void stop() {
		if (myAnimation != null) {
			myAnimation.pause();
		}
	}

	/**
	 * Begin the creation of all the UI components.
	 * 
	 * @param stage
	 *            the window to create the components in.
	 */
	private void setupUI(Stage stage) {
		Scene scene = new Scene(createPane(), DEFAULT_SIZE.width, DEFAULT_SIZE.height, Color.WHITE);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Begin the creation of all the UI components.
	 * 
	 * @return the BorderPane containing the ControlPanel and Graph.
	 */
	private BorderPane createPane() {
		BorderPane borderPane = new BorderPane();
		borderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		ControlPanel controlPanel = new ControlPanel(this, myResources, CONTROL_PANEL_WIDTH);
		borderPane.setLeft(controlPanel);
		myGraph = new PopulationGraph(DEFAULT_SIZE.width - CONTROL_PANEL_WIDTH, DEFAULT_SIZE.height);
		borderPane.setCenter(myGraph);
		return borderPane;
	}

	/**
	 * Updates the simulation on each time step - if there are any simulations
	 * to update. Also updates the display grid and graph.
	 */
	public void step() {
		if (mySimulationViews.size() > 0) {
			mySimulationController.step();
			myGraph.update(mySimulationController.getProportions());
			updateGrids();
		}
	}

	/**
	 * Creates a new simulation, but does not add it to the simulation
	 * controller.
	 * 
	 * @param game
	 *            A String identifying the game.
	 * @param configuration
	 *            A String identifying the configuration file to be used.
	 */
	public Simulation newSimulation(String game, String configuration) {
		return mySimulationController.create(game, configuration);
	}

	/**
	 * Displays the simulation created by the user.
	 * 
	 * @param simulation
	 *            The simulation to be displayed.
	 */
	public void displaySimulation(Simulation simulation) {
		simulation.setup();
		mySimulationController.add(simulation);
		DisplayGrid grid = new DisplayGrid(this, simulation, GRID_SIZE,
				simulation.getSettings().getParameter("cellType"),
				Boolean.parseBoolean(simulation.getSettings().getParameter("outlineGrid")),
				simulation.getSettings().getIntParameter("cellSize"));
		grid.update(simulation.getGrid());
		mySimulationViews.add(createSimulationView(simulation, grid));
	}

	/**
	 * Creates a simulation view – a window containing a DisplayGrid and a
	 * reference to a Simulation. This window has the option to update on each
	 * time step.
	 * 
	 * @param simulation
	 *            The simulation to be displayed.
	 * @param grid
	 *            The display grid to add to the SimulationView.
	 * @return a SimulationView.
	 */
	private SimulationView createSimulationView(Simulation simulation, DisplayGrid grid) {
		SimulationView view = new SimulationView(simulation, grid, GRID_SIZE, mySimulationViews.size() + 1);
		view.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				stop();
				mySimulationController.remove(view.getSimulation());
				mySimulationViews.remove(view);
				myGraph.update(mySimulationController.getProportions());
			}
		});
		return view;
	}

	/**
	 * Shuffles the cells and reloads the current simulation.
	 */
	public void shuffleGrid() {
		stop();
		mySimulationController.shuffle();
		myGraph.clear();
		updateGrids();
	}

	/**
	 * Updates the visualizations.
	 */
	private void updateGrids() {
		for (SimulationView view : mySimulationViews) {
			view.update();
		}
	}

	/**
	 * Handles a click on a cell, if the simulation has been launched and is currently paused.
	 * 
	 * @param graphicCell
	 *            The cell that was clicked.
	 * @param simulation
	 *            The simulation that was clicked.
	 */
	public void cellClicked(GraphicPolygon graphicCell, Simulation simulation) {
		if (myAnimation == null || myAnimation.getStatus() == Animation.Status.PAUSED) {
			Map<String, State> states = simulation.getGame().getStates();
			createStateDialog(states, graphicCell);
		}
	}
	
	/**
	 * Creates a dialog in which the user can choose the state.
	 * Upon the user's response, accordingly updates the state.
	 * 
	 * @param states A map from state names to State objects.
	 * @param graphicCell
	 *            The cell that was clicked.
	 */
	private void createStateDialog(Map<String, State> states, GraphicPolygon graphicCell) {
		ChoiceDialog<String> dialog = new ChoiceDialog<>(GraphicPolygon.getStateName(states, graphicCell),
				states.keySet());
		dialog.setTitle("State");
		dialog.setHeaderText("Each cell can have several states.");
		dialog.setContentText("Choose your cell's state:");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(picked -> {
			graphicCell.update(states.get(picked));
			updateGrids();
		});
	}

}
