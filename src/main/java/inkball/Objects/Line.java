package inkball.Objects;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PApplet.print;

/**
 * Line class for drawing lines and checking for collisions
 */
public class Line implements Object {
    public PApplet parent;
    public float x1, y1, x2, y2;
    /**
     * Normal to the line at the midpoint it's important for the ball to bounce off the line correctly
     */
    public PVector p1Norm = new PVector();
    public PVector p2Norm = new PVector();


    /**
     * Constructor for the Line class
     * @param parent
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Line(PApplet parent, float x1, float y1, float x2, float y2) {
        this.parent = parent;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Display the line
     * @param pg
     */
    @Override
    public void display(PGraphics pg) {
        pg.line(x1, y1, x2, y2);
        //draw its normal too
        PVector normal = new PVector(y2-y1, x1-x2);
        normal.normalize();
        normal.mult(parent.dist(x1, y1, x2, y2)/2);
        p1Norm.set((x1+x2)/2, (y1+y2)/2);
        p2Norm.set(p1Norm.x + normal.x, p1Norm.y + normal.y);
    }

    /**
     * Set the start point of the line
     * @param x
     * @param y
     */
    public void setEnd(float x, float y) {
        x2 = x;
        y2 = y;
    }


    /**
     * Check if a point collides with the line
     * @param x
     * @param y
     * @return
     */
    public boolean collides(float x, float y) {
        float d1 = parent.dist(x, y, x1, y1);
        float d2 = parent.dist(x, y, x2, y2);
        float lineLen = parent.dist(x1, y1, x2, y2);
        float buffer = 0.1f;    // higher buffer value means less accurate

        if(d1+d2 - lineLen <= buffer) {
            return true;
        }
        return false;
    }


    /**
     * Get the distance of a point from the line
     * @param x
     * @param y
     * @return normal distance from the line to the point used for collision detection
     */
    public float distance(float x, float y) {
        float num = parent.abs((y2-y1)*x - (x2-x1)*y + x2*y1 - y2*x1);
        float den = parent.sqrt(PApplet.sq(y2-y1) + PApplet.sq(x2-x1));
        return num/den;
    }

    @Override
    public String toString() {
        return "Line{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}
