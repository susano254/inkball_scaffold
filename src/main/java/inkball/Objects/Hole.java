package inkball.Objects;

import inkball.App;
import inkball.Color;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Hole class for drawing holes
 */
public class Hole implements Object {
    PApplet parent;
    public PVector position;
    public final float size = 64;
    public Color color;
    public PImage image;
    public final String className = "hole";

    /**
     * Constructor for the Hole class
     * @param parent
     * @param x
     * @param y
     * @param color
     */
    public Hole(App parent, float x, float y, Color color) {
        position = new PVector(x, y);
        this.parent = parent;
        this.color = color;
        this.image = parent.loadImage(App.resourcePath + className + color.getChar() + ".png");
    }

    /**
     * Display the hole
     * @param pg
     */
    @Override
    public void display(PGraphics pg) {
        pg.fill(0);
        pg.image(image, position.x, position.y, size, size);
    }

    @Override
    public String toString() {
        return "Hole{" +
                "x=" + position.x +
                ", y=" + position.y +
                '}';
    }
}
