package inkball.Parsers;

import inkball.App;
import inkball.Color;
import inkball.Objects.*;
import inkball.Objects.Object;
import processing.core.PApplet;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * ParseLayout class for parsing the layout of the level
 */
public class ParseLayout {
    App parent;
    public String layoutPath;
    public List<String> layout;
    public char[][] layoutGrid;  // 2D array for storing the level grid
    public List<List<Object>> objects = new ArrayList<>();

    public int rows = 18;
    public int cols = 18;


    /**
     * Constructor for the ParseLayout class
     * @param parent
     * @param layoutPath
     */
    public ParseLayout(App parent, String layoutPath) {
        this.parent = parent;
        this.layoutPath = layoutPath;
        this.layoutGrid = new char[rows][cols];
        PApplet.println("layoutPath: " + layoutPath);

        try {
            this.layout = Files.readAllLines(Paths.get(layoutPath));
            PApplet.println("layout: " + layout);
            parse();

            PApplet.println("layoutGrid: ");
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    PApplet.print(layoutGrid[i][j]);
                }
                PApplet.print("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the layout of the level
     */
    public void parse() {
        for (int i = 0; i < layout.size() && i < rows; i++) {
            String line = layout.get(i);
            for (int j = 0; j < line.length() && j < cols; j++) {
                layoutGrid[i][j] = line.charAt(j);
            }
        }

        // Create objects based on the layout grid each cell is represented by a character and App.CELLSIZExApp.CELLSIZE pixels
        for (int i = 0; i < rows; i++) {
            objects.add(new ArrayList<>());
            ArrayList<Object> row = (ArrayList<Object>) objects.get(i);
            for (int j = 0; j < cols; j++) {
                char cell = layoutGrid[i][j];
                if(cell == 'X' || cell == '1' || cell == '2' || cell == '3' || cell == '4') {
                    Wall wall = new Wall(parent, j * App.CELLSIZE, i * App.CELLSIZE, new Color(cell));
                    row.add(wall);
                } else if (cell == 'B') {
                    j++;
                    char nextCell = layoutGrid[i][j];
                    Ball ball = new Ball(parent, (j-1) * App.CELLSIZE, i * App.CELLSIZE, new Color(nextCell));
                    row.add(ball);
                    row.add(new Tile(parent, j * App.CELLSIZE, i * App.CELLSIZE));
                    row.add(new Tile(parent, (j-1) * App.CELLSIZE, i * App.CELLSIZE));
                } else if (cell == 'S') {
                    Spawner spawner = new Spawner(parent, j * App.CELLSIZE, i * App.CELLSIZE);
                    row.add(spawner);

                } else if (cell == 'H') {
                    j++;
                    char nextCell = layoutGrid[i][j];
                    Hole hole  = new Hole(parent, (j-1) * App.CELLSIZE, i * App.CELLSIZE, new Color(nextCell));
                    row.add(hole);
                }
                else if (cell == ' ') {
                    Tile tile = new Tile(parent, j * App.CELLSIZE, i * App.CELLSIZE);
                    row.add(tile);
                }
            }
        }
    }
}
