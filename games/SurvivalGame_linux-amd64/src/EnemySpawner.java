import GameEngine.GameTexture;

import java.util.Vector;

public class EnemySpawner {
    float x;
    float y;
    int interval;
    int timer;
    Grid grid;
    Vector<GameTexture> textures;

    public EnemySpawner(float x, float y, int interval, int initialDelay, Grid grid, Vector<GameTexture> textures) {
        this.x = x;
        this.y = y;
        this.interval = interval;
        this.timer = initialDelay;
        this.grid = grid;
        this.textures = textures;
    }

    private void spawn() {
        EnemyObject enemy = new EnemyObject(x, y, grid);
        for (GameTexture texture : textures) {
            enemy.addTexture(texture);
        }
        SurvivalGame.registerObject(enemy);
    }

    public void doTimeStep() {
        if (timer <= 0) {
            spawn();
            timer = interval;
        }
        timer--;
    }
}
