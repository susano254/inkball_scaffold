package inkball.Parsers;

import inkball.App;
import inkball.Color;
import inkball.Objects.Ball;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.print;
import static processing.core.PApplet.println;

/**
 * Level class for parsing the level data
 */
public class Level {
    App parent;
    public int level = 1;
    public int time = 0;
    public int spawnInterval = 10;
    public float increaseModifier = 1.0f;
    public float decreaseModifier = 1.0f;
    public Queue<Ball> ballsQueue = new LinkedList<>();
    public HashMap<String, Integer> scoreIncrease = new HashMap<>();
    public HashMap<String, Integer> scoreDecrease = new HashMap<>();

    /**
     * Constructor for the Level class
     * @param parent
     */
    public Level(App parent) {
        this.parent = parent;
    }

    /**
     * Parse the config json file and extract the level data
     * @param path
     * @param level
     */
    public void parseConfigFile(String path, int level) {
        JSONObject file = parent.loadJSONObject("config.json");
        println(file);

        JSONArray levels = file.getJSONArray("levels");
        println(levels);

        JSONObject levelData = levels.getJSONObject(level - 1);
        println(levelData);

        JSONObject scoreInc = file.getJSONObject("score_increase_from_hole_capture");
        println(scoreInc);

        for(java.lang.Object key : scoreInc.keys()){
            scoreIncrease.put(key.toString(), scoreInc.getInt(key.toString()));
        }


        JSONObject scoreDec = file.getJSONObject("score_decrease_from_wrong_hole");
        println(scoreDec);

        for(java.lang.Object key : scoreDec.keys()){
            scoreDecrease.put(key.toString(), scoreDec.getInt(key.toString()));
        }

        this.level = level;
        this.time = levelData.getInt("time");
        this.spawnInterval = levelData.getInt("spawn_interval");
        this.increaseModifier = levelData.getFloat("score_increase_from_hole_capture_modifier");
        this.decreaseModifier = levelData.getFloat("score_decrease_from_wrong_hole_modifier");
        JSONArray balls = levelData.getJSONArray("balls");
        println(balls);

        for (int i = 0; i < balls.size(); i++) {
            String ballColor = balls.getString(i);
            println(ballColor);
            Color color = new Color(ballColor);
            println("color: " + color);
            Ball b = new Ball(parent, 0, 0, color);
            ballsQueue.add(b);
        }


    }

    @Override
    public String toString() {
        ArrayList<Color> balls = new ArrayList<>();
        for(Ball b : ballsQueue){
            balls.add(b.color);
        }
        return "Level{" +
                "level=" + level +
                ", time=" + time +
                ", spawnInterval=" + spawnInterval +
                ", increaseModifier=" + increaseModifier +
                ", decreaseModifier=" + decreaseModifier +
                ", ballsQueue=" + balls +
                ", scoreIncrease=" + scoreIncrease +
                ", scoreDecrease=" + scoreDecrease +
                '}';
    }
}
