package inkball.Objects;

import inkball.App;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Spawner class for drawing spawners
 */
public class Spawner implements Object {
    public PApplet parent;
    public float x, y;
    public final int size = 32;
    public final String imagePath = "inkball/entrypoint.png";

    /**
     * Constructor for the Spawner class
     * @param parent
     * @param x
     * @param y
     */
    public Spawner(App parent, float x, float y) {
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    /**
     * Display the spawner
     * @param pg
     */
    @Override
    public void display(PGraphics pg) {
        pg.fill(0);
        pg.image(parent.loadImage(imagePath), x, y, size, size);
    }

    @Override
    public String toString() {
        return "Spawner{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
