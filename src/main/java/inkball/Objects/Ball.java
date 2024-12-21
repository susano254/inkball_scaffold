package inkball.Objects;

import inkball.App;
import inkball.Color;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import static processing.core.PApplet.*;


/**
 * Ball class for drawing balls and checking for collisions with walls, lines and holes
 */
public class Ball implements Object {
    public App parent;
    public PVector position;
    public PVector velocity;
    public Color color;

    /**
     * Visibility of the ball
     */
    public boolean Visible = true;
    /**
     * If the ball has fallen in a hole
     */
    public boolean fellInHole = false;
    public PImage image;

    public float radius = 16;

    /**
     * Mass of the ball for collision calculations
     */
    public float  m;

    public final String className = "ball";


    /**
     * Speed multiplier for the ball
     */
    public float speedMultiplier = 2;
    public int horizontalLineBounces = 0;
    public int verticalLineBounces = 0;

    /**
     * Constructor for the Ball class
     * @param parent
     * @param x
     * @param y
     * @param color
     */
    public Ball(App parent, float x, float y, Color color) {
        position = new PVector(x+radius, y+radius);
        velocity = PVector.random2D();
        velocity.mult(speedMultiplier);
        m = radius*.1f;

        this.parent = parent;
        this.color = color;
        this.image = parent.loadImage(App.resourcePath + className + color.getChar() + ".png");
    }

    /**
     * Update the position of the ball
     */
    public void update() {
        position.add(velocity);
    }

    /**
     * Check if the ball has collided with the boundaries of the screen and bounce it off
     */
    public void checkBoundaryCollision() {
        if (position.x > App.WIDTH-radius) {
            position.x = App.WIDTH-radius;
            velocity.x *= -1;
        } else if (position.x < radius) {
            position.x = radius;
            velocity.x *= -1;
        } else if (position.y > App.WIDTH-radius) {
            position.y = App.WIDTH-radius;
            velocity.y *= -1;
        } else if (position.y < radius) {
            position.y = radius;
            velocity.y *= -1;
        }
    }

    /**
     * Check if the ball has collided with a wall and bounce it off
     * @param wall
     */
    public void checkCollisionWithWall(Wall wall){
        // wall is rectangle and ball is circle
        // make simple collision detection
        if(position.x + radius + velocity.x > wall.position.x &&
                position.x - radius + velocity.x < wall.position.x + wall.width &&
                position.y + radius > wall.position.y &&
                position.y - radius < wall.position.y + wall.height) {
            // reset the position to avoid the ball getting stuck in the wall
            if(velocity.x > 0){
                position.x = wall.position.x - radius - 2;
            } else {
                position.x = wall.position.x + wall.width + radius + 2;
            }

            velocity.x *= -1;
        }

        if(position.y + radius + velocity.y > wall.position.y &&
                position.y - radius + velocity.y < wall.position.y + wall.height &&
                position.x + radius > wall.position.x &&
                position.x - radius < wall.position.x + wall.width) {

            // reset the position to avoid the ball getting stuck in the wall
            if(velocity.y > 0){
                position.y = wall.position.y - radius - 2;
            } else {
                position.y = wall.position.y + wall.height + radius + 2;
            }
            velocity.y *= -1;
        }
    }

    /**
     * Check if the ball has collided with a line
     * @param line
     */
    public void checkCollisionWithLine(Line line) {
        float distance = line.distance(position.x, position.y);
        PVector leftPoint = new PVector(min(line.x1, line.x2), min(line.y1, line.y2));
        PVector rightPoint = new PVector(max(line.x1, line.x2), max(line.y1, line.y2));


        if(distance < radius) {
            if(position.x > leftPoint.x && position.x < rightPoint.x){
                if(position.y > leftPoint.y && position.y < rightPoint.y){
                    bounceLine(line);
                }
            }

        }
    }

    /**
     * Bounce the ball off a line
     * @param line
     */
    void bounceLine(Line line){
        print(verticalLineBounces + ": Bounce Line \n");
        PVector p1 = new PVector(line.x1, line.y1);
        PVector p2 = new PVector(line.x2, line.y2);

        //get direction of the normal to the line
        PVector normal = PVector.sub(line.p2Norm, line.p1Norm);
        normal.normalize();
        print(normal.x + " " + normal.y + "\n");

        // if ball is moving against the normal print true else false
        print(PVector.dot(velocity, normal) < 0 ? "true\n" : "false\n");
        if(!(PVector.dot(velocity, normal) < 0)){
            // swipe p1 and p2
            PVector temp = p1;
            p1 = p2;
            p2 = temp;
        }



        // get the angle between the line and the horizontal axis
        float angle = atan2(p2.y - p1.y, p2.x - p1.x);

        PVector relativePosition = PVector.sub(position, p2);


        PVector rotatedPosition = new PVector();
        float cosAngle = cos(angle);
        float sinAngle = sin(angle);

        rotatedPosition.x = cosAngle * relativePosition.x + sinAngle * relativePosition.y;
        rotatedPosition.y = cosAngle * relativePosition.y - sinAngle * relativePosition.x;

        PVector rotatedVelocity = new PVector();
        rotatedVelocity.x = cosAngle * velocity.x + sinAngle * velocity.y;
        rotatedVelocity.y = cosAngle * velocity.y - sinAngle * velocity.x;


        // do the bounce if the ball is in the right position
        if(rotatedPosition.y + radius/2 >=0 && rotatedVelocity.y > 0 && rotatedPosition.y - radius/2 <= 0){
            rotatedPosition.y = -radius/2;
            rotatedVelocity.y *= -1;

            // rotate the ball back
            PVector newPosition = new PVector();
            newPosition.x = cosAngle * rotatedPosition.x - sinAngle * rotatedPosition.y;
            newPosition.y = cosAngle * rotatedPosition.y + sinAngle * rotatedPosition.x;
            position = PVector.add(newPosition,  p2);

            PVector newVelocity = new PVector();
            newVelocity.x = cosAngle * rotatedVelocity.x - sinAngle * rotatedVelocity.y;
            newVelocity.y = cosAngle * rotatedVelocity.y + sinAngle * rotatedVelocity.x;
            velocity = newVelocity;
            verticalLineBounces++;
        }

    }

    /**
     * Check if the ball has collided with a hole and if it has fallen in it calculate the score and do the animations
     * @param hole
     */
    public void checkCollisionWithHole(Hole hole) {

        // wall is rectangle and ball is circle
        // Calculate direction vector from ball to hole
        PVector center = new PVector(hole.position.x + hole.size/2, hole.position.y + hole.size/2);
        PVector direction = PVector.sub(center, position);
//        Line line = new Line(parent, position.x, position.y, hole.position.x + hole.size/2, hole.position.y + hole.size/2);
//        line.display(parent.animation);

        // Calculate the distance between the ball and the hole
        float distance = direction.mag();

        if(distance < radius + hole.size/4) {
            print("Ball with Color " + color + " has Collided with hole with Color " + hole.color + "\n");
            float force = 0.4f;
            direction.normalize();
            direction.mult(force);
            velocity = direction;
            radius = 0.5f*distance;
            if(radius < 4){
                fellInHole = true;
                Visible = false;
                // calculate scoe based on the color of the hole
                parent.calculateScore(this, hole);
            }

        }

    }

    /**
     * Check if the ball is not visible
     * @return
     */
    public boolean isNotVisible() {
        return !Visible;
    }

    /**
     * Check if the ball has fallen in a hole
     * @return
     */
    public boolean isInHole() {
        return fellInHole;
    }

    /**
     * Check if the ball has collided with another ball and bounce them off
     * @param other
     */
    public void checkCollision(Ball other) {
        // Get distances between the balls components
        PVector distanceVect = PVector.sub(other.position, position);

        // Calculate magnitude of the vector separating the balls
        float distanceVectMag = distanceVect.mag();

        // Minimum distance before they are touching
        float minDistance = radius + other.radius;

        if (distanceVectMag < minDistance) {
            float distanceCorrection = (minDistance-distanceVectMag)/2.0f;
            PVector d = distanceVect.copy();
            PVector correctionVector = d.normalize().mult(distanceCorrection);
            other.position.add(correctionVector);
            position.sub(correctionVector);

            // get angle of distanceVect
            float theta  = distanceVect.heading();
            // precalculate trig values
            float sine = sin(theta);
            float cosine = cos(theta);

            PVector[] positionTemp = {
                    new PVector(), new PVector()
            };

            positionTemp[1].x  = cosine * distanceVect.x + sine * distanceVect.y;
            positionTemp[1].y  = cosine * distanceVect.y - sine * distanceVect.x;

            // rotate Temporary velocities
            PVector[] velocityTemp = {
                    new PVector(), new PVector()
            };

            velocityTemp[0].x  = cosine * velocity.x + sine * velocity.y;
            velocityTemp[0].y  = cosine * velocity.y - sine * velocity.x;
            velocityTemp[1].x  = cosine * other.velocity.x + sine * other.velocity.y;
            velocityTemp[1].y  = cosine * other.velocity.y - sine * other.velocity.x;

            PVector[] velocityFinal = {
                    new PVector(), new PVector()
            };

            // final rotated velocity for b[0]
            velocityFinal[0].x = ((m - other.m) * velocityTemp[0].x + 2 * other.m * velocityTemp[1].x) / (m + other.m);
            velocityFinal[0].y = velocityTemp[0].y;

            // final rotated velocity for b[0]
            velocityFinal[1].x = ((other.m - m) * velocityTemp[1].x + 2 * m * velocityTemp[0].x) / (m + other.m);
            velocityFinal[1].y = velocityTemp[1].y;

            // hack to avoid clumping
            positionTemp[0].x += velocityFinal[0].x;
            positionTemp[1].x += velocityFinal[1].x;

            // rotate balls
            PVector[] positionFinal = {
                    new PVector(), new PVector()
            };

            positionFinal[0].x = cosine * positionTemp[0].x - sine * positionTemp[0].y;
            positionFinal[0].y = cosine * positionTemp[0].y + sine * positionTemp[0].x;
            positionFinal[1].x = cosine * positionTemp[1].x - sine * positionTemp[1].y;
            positionFinal[1].y = cosine * positionTemp[1].y + sine * positionTemp[1].x;

            // update balls to screen position
            other.position.x = position.x + positionFinal[1].x;
            other.position.y = position.y + positionFinal[1].y;

            position.add(positionFinal[0]);

            // update velocities
            velocity.x = cosine * velocityFinal[0].x - sine * velocityFinal[0].y;
            velocity.y = cosine * velocityFinal[0].y + sine * velocityFinal[0].x;
            other.velocity.x = cosine * velocityFinal[1].x - sine * velocityFinal[1].y;
            other.velocity.y = cosine * velocityFinal[1].y + sine * velocityFinal[1].x;
        }
    }

    /**
     * Display the ball
     * @param pg
     */
    @Override
    public void display(PGraphics pg) {
        pg.fill(204);
        pg.image(image, position.x - radius, position.y - radius, radius*2, radius*2);
    }


    @Override
    public String toString() {
        return "Ball{" +
                "position=" + position +
                ", velocity=" + velocity +
                ", color='" + color + '\'' +
                ", radius=" + radius +
                ", m=" + m +
                ", horizontalLineBounces=" + horizontalLineBounces +
                ", verticalLineBounces=" + verticalLineBounces +
                '}';
    }
}
