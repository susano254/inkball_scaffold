package inkball.Screens;

import inkball.App;
import inkball.Objects.Ball;
import processing.core.PGraphics;

import java.util.Queue;

/**
 * TopBar class for displaying the top bar
 */
public class TopBar {
    App parent;
    /**
     * PGraphics canvas for drawing the top bar
     */
    PGraphics canvas, ballsQueueView, ScoreAndTimeView;


    /**
     * Constructor for the TopBar class
     * @param parent
     */
    public TopBar(App parent) {
        this.parent = parent;
        this.canvas = parent.createGraphics(App.WIDTH, App.TOPBAR);
        this.ballsQueueView = parent.createGraphics(32*5, 32);
        this.ScoreAndTimeView = parent.createGraphics(32*4, App.TOPBAR);
    }

    /**
     * Display the top bar with the balls queue, score and time
     * @param ballsQueue
     * @param score
     * @param time
     */
    public void display(Queue<Ball> ballsQueue, int score, int time) {
        canvas.beginDraw();
        canvas.background(204);
        ballsQueueView.beginDraw();
        ballsQueueView.background(0);
        int i = 0;
        for (Ball ball : ballsQueue) {
            ballsQueueView.image(ball.image, i*32, 0, 32, 32);
            i++;
        }
        ballsQueueView.endDraw();
        ScoreAndTimeView.beginDraw();
        ScoreAndTimeView.background(204);
        ScoreAndTimeView.fill(0);
        ScoreAndTimeView.textSize(16);
        ScoreAndTimeView.text("Score: " + score, 0, 20);
        ScoreAndTimeView.text("Time: " + time, 0, 52);
        ScoreAndTimeView.endDraw();

        canvas.image(ballsQueueView, 8, 8);
        canvas.image(ScoreAndTimeView, App.WIDTH - (32*4 + 8), 0);
        canvas.endDraw();
        parent.image(canvas, 0, 0);
    }
}
