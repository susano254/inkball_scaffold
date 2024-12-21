package inkball.Objects;

import inkball.App;
import inkball.Color;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Wall class for drawing walls
 */
public class Wall implements Object {
    public PApplet parent;
    public PVector position;
    public PVector velocity;
    public float width, height;
    public Color color;
    public PImage image;
    public final String className = "wall";

    /**
     * Constructor for the Wall class
     * @param parent
     * @param x
     * @param y
     * @param color
     */
    public Wall(App parent, float x, float y, Color color) {
        position = new PVector(x, y);
        velocity = new PVector(0, 0);
        this.parent = parent;
        this.width = 32;
        this.height = 32;
        this.color = color;
        this.image = parent.loadImage(App.resourcePath + className + color.getChar() + ".png");
    }

    /**
     * Display the wall
     * @param pg
     */
    @Override
    public void display(PGraphics pg) {
        pg.fill(204);
        pg.image(image , position.x, position.y, width, height);
    }

    @Override
    public String toString() {
        return "Wall{" +
                "position=" + position +
                ", velocity=" + velocity +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
