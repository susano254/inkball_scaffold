package inkball;

import inkball.Parsers.Level;
import inkball.Parsers.ParseLayout;
import inkball.Screens.TopBar;
import inkball.Objects.*;
import inkball.Objects.Object;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;



/**
 * Main class for the game. This class is responsible for the game loop and the interaction between the player and the game.
 */
public class App extends PApplet {
    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 18;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 60;

    public static final String resourcePath = "inkball/";
    public String configPath;

    public static Random random = new Random();
    /**
     * Game state variables
     */
    boolean isPaused = false;
    boolean isFinished = false;
    int lastSpawn = 0;
    int gameStartTime = 0;
    int score = 0;
    int time = 0;

    /**
     * Level data
     */
    public Level levelData;
    int level = 1;


    /**
     * Game Elements top,Bar, board, animation
     * each represents a layer of drawing and different portion of the screen
     */
    public TopBar topBar;               // top bar for displaying score and time and ballqueue
    public PGraphics board, animation;  // board for displaying the game elements

    /**
     * Game Elements each array list represents a different type of object in the game board
     */
    public Line tempLine = null;
    public ArrayList<Line> lines = new ArrayList<Line>();
    public ArrayList<Ball> balls = new ArrayList<Ball>();
    public ArrayList<Wall> walls = new ArrayList<Wall>();
    public ArrayList<Hole> holes = new ArrayList<Hole>();
    public ArrayList<Spawner> spawners = new ArrayList<Spawner>();
    public ArrayList<Tile> tiles = new ArrayList<Tile>();

	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        lines.clear();
        balls.clear();
        walls.clear();
        holes.clear();
        spawners.clear();
        tiles.clear();

        levelData = new Level(this);
        levelData.parseConfigFile(configPath, level);

        frameRate(FPS);
        ParseLayout pl = new ParseLayout(this, "level" + level + ".txt");
        print("Objects: " + pl.objects.size() + "\n");
        for(int i = 0; i < pl.objects.size(); i++) {
            for(int j = 0; j < pl.objects.get(i).size(); j++) {
                Object obj = (Object) pl.objects.get(i).get(j);
                print(obj + "\n");
                if(obj instanceof Ball) {
                    balls.add((Ball) obj);
                } else if(obj instanceof Wall) {
                    walls.add((Wall) obj);
                } else if(obj instanceof Hole) {
                    holes.add((Hole) obj);
                } else if(obj instanceof Spawner) {
                    spawners.add((Spawner) obj);
                }
                else if (obj instanceof Tile){
                    tiles.add((Tile) obj);
                }
            }
        }
        print("finished parsing\n");

        this.topBar = new TopBar(this);
        board = createGraphics(WIDTH, WIDTH);
        animation = createGraphics(WIDTH, WIDTH);


        if(board != null) {
            board.beginDraw();
            for (Tile t : tiles) {
                t.display(board);
            }
            for (Wall w : walls) {
                w.display(board);
            }
            for (Hole h : holes) {
                h.display(board);
            }
            for (Spawner s : spawners) {
                s.display(board);
            }
            board.endDraw();
        }

        println(levelData);
        lastSpawn = millis();
        gameStartTime = millis();
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        // if the key pressed is 'r', reset the game
        if(event.getKey() == 'r') {
            if(isFinished) {
                level = 1;
                levelData.level = 1;
                isFinished = false;
            }
            setup();
        }
        // if space is pressed pause the game
        if(event.getKey() == ' ') {
            isPaused = !isPaused;
        }
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        if(e.getButton() == LEFT) {
            print("Mouse pressed\n");
            tempLine = new Line(this, mouseX, mouseY - TOPBAR, mouseX, mouseY);
        }
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        if(e.getButton() == LEFT) {
            tempLine.setEnd(mouseX, mouseY - TOPBAR);
            animation.beginDraw();
            tempLine.display(animation);
            animation.endDraw();
        }

		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
        if (e.getButton() == RIGHT) {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).collides(mouseX, mouseY-TOPBAR)) {
                    print("Mouse position collides with line: " + i + "\n");
                    lines.remove(i);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == LEFT) {
            print("Mouse released\n");
            lines.add(tempLine);
            print("Line added\n");
            tempLine = null;
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        if (isPaused || isFinished) {
            return;
        }
        background(255);

        if((millis() - lastSpawn)/1000 >= levelData.spawnInterval) {
            lastSpawn = millis();
            // choose a random spawner
            spawn();
        }

        int timeElapsed = (millis() - gameStartTime)/1000;
        time = levelData.time - timeElapsed;

        animation.beginDraw();
        animation.clear();

        for(Line l : lines) {
            l.display(animation);
        }

        Iterator<Ball> it = balls.iterator();
        while(it.hasNext()) {
            Ball b = it.next();
            if(b.isInHole()) {
                it.remove();
                continue;
            }
            b.checkBoundaryCollision();
            for(Wall w : walls) {
                b.checkCollisionWithWall(w);
            }
            for(Line l : lines) {
                b.checkCollisionWithLine(l);
            }

            for(Hole h : holes) {
                b.checkCollisionWithHole(h);
            }
            b.update();
            b.display(animation);
        }

        for(int i = 0; i < balls.size(); i++) {
            for(int j = 0; j < balls.size(); j++) {
                if(i != j)
                    balls.get(i).checkCollision(balls.get(j));
            }
        }

        animation.endDraw();


        topBar.display(levelData.ballsQueue, score, time);
        image(board, 0, TOPBAR);
        image(animation, 0, TOPBAR);



        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //TODO

        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------
		//display game end message
        if(time <= 0) {
            endGame();
        }
        if(balls.isEmpty() && levelData.ballsQueue.isEmpty()) {
            if(levelData.level < 3) {
                level++;
                levelData.level++;
                setup();
            } else {
                endGame();
            }
        }

    }

    /**
     * Spawn a ball at a random spawner.
     * The ball is chosen from the ballsQueue.
     */
    private void spawn() {
        int spawnerIndex = random.nextInt(spawners.size());
        Spawner spawner = spawners.get(spawnerIndex);
        if(!levelData.ballsQueue.isEmpty()) {
            Ball ball = levelData.ballsQueue.poll();
            ball.position.x = spawner.x;
            ball.position.y = spawner.y;

            ball.velocity.mult(2);
            balls.add(ball);
        }
    }

    /**
     * End the game and display the game over message.
     */
    public void endGame(){
        isFinished = true;
        board.beginDraw();
        board.fill(255, 0, 0);
        board.textSize(32);
        board.text("Game Over", WIDTH/2-64, WIDTH/2);
        board.endDraw();
        image(board, 0, TOPBAR);

    }

    /**
     * Calculate the score of the player based on the ball and hole.
     * @param ball the ball object
     * @param hole the hole object
     */
    public void calculateScore(Ball ball, Hole hole) {
        if(ball.color.value == hole.color.value) {
            int scoreInc = levelData.scoreIncrease.get(hole.color.getString());
            score += (int) (scoreInc * levelData.increaseModifier);
        } else {
            int scoreDec = levelData.scoreDecrease.get(hole.color.getString());
            score -= (int) (scoreDec * levelData.decreaseModifier);
        }

    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
