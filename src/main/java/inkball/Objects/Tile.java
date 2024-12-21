package inkball.Objects;

import inkball.App;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Tile class for drawing tiles
 */
public class Tile implements Object {
    App parent;
    private int x, y;
    private final int size = 32;
    private final String imagePath = "inkball/tile.png";
    private PImage image;


    /**
     * Constructor for the Tile class
     * @param parent
     * @param x
     * @param y
     */
    public Tile(App parent, int x, int y) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.image = parent.loadImage(imagePath);
    }

    /**
     * Display the tile
     * @param pg
     */
    @Override
    public void display(PGraphics pg) {
        pg.fill(0);
        if(image != null)
            pg.image(image, x, y, size, size);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
