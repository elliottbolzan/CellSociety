package user_interface;

import java.util.HashMap;

import cellsociety_team18.Point;
import graphic_elements.GraphicHexagon;
import graphic_elements.GraphicPolygon;
import graphic_elements.GraphicSquare;
import graphic_elements.GraphicTriangle;
import grids.Grid;
import cellsociety_team18.Simulation;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

/**
 * @author elliott This class represents a visual grid.
 */
public class DisplayGrid extends ScrollPane {

	private int sizeInPixels;
	private int sizeInCells;
	private int cellSize;
	private boolean responsive = false;
	private boolean outlined;

	private ViewController viewController;
	private Simulation simulation;
	private HashMap<Point, GraphicPolygon> cells = new HashMap<Point, GraphicPolygon>();
	private Group root = new Group();

	public DisplayGrid(ViewController viewController, Simulation simulation, int sizeInPixels) {
		this.viewController = viewController;
		this.simulation = simulation;
		this.sizeInPixels = sizeInPixels;
		this.sizeInCells = simulation.getGrid().getSize();
		this.outlined = Boolean.parseBoolean(simulation.getSettings().getParameter("outlineGrid"));
		this.cellSize = simulation.getSettings().getIntParameter("cellSize");
		setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		setContent(root);
		createCells(simulation.getSettings().getParameter("cellType"));
	}

	public int getSizeInCells() {
		return sizeInCells;
	}

	/**
	 * Originally create the graphic cells.
	 */
	private void createCells(String gridType) {
		for (int i = 0; i < sizeInCells; i++) {
			for (int j = 0; j < sizeInCells; j++) {
				GraphicPolygon cell = getNewCell(gridType, new Point(i, j));
				cell.setOnMouseClicked((event) -> {
					if (responsive) {
						viewController.cellClicked(cell, simulation);
					}
				});
				cells.put(new Point(i, j), cell);
				root.getChildren().add(cell);
			}
		}
	}

	private GraphicPolygon getNewCell(String gridType, Point center) {
		if (gridType.equals("Square"))
			return new GraphicSquare(Color.WHITE, sizeInPixels, sizeInCells, center, outlined, cellSize);
		if (gridType.equals("Hexagon"))
			return new GraphicHexagon(Color.WHITE, sizeInPixels, sizeInCells, center, outlined, cellSize);
		return new GraphicTriangle(Color.WHITE, sizeInPixels, sizeInCells, center, outlined, cellSize);
	}

	/**
	 * Update the colors of the visual grid.
	 */
	public void update() {
		Grid grid = simulation.getGrid();
		responsive = true;
		for (int i = 0; i < sizeInCells; i++) {
			for (int j = 0; j < sizeInCells; j++) {
				Point point = new Point(i, j);
				GraphicPolygon cell = cells.get(point);
				cell.setData(grid.getCell(point));
				cells.put(point, cell);
			}
		}
	}

}
