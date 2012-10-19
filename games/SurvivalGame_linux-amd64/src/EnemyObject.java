import GameEngine.GameObject;

import java.awt.geom.Point2D;

public class EnemyObject extends GameObject {
    int numberOfDirectionTextures = 72;
    float direction;
    Point2D.Float oldPosition;
    Grid grid;

    float speed = 2;
    int damage = 1;

    public EnemyObject(float x, float y, Grid grid) {
        super(x, y);
        this.grid = grid;
    }

    public void setDirection(float direction) {
        while (direction < 0.0) direction += 360.0;
        while (direction >= 360.0) direction -= 360.0;

        this.direction = direction;

        // setting the correct texture
        float offsetDirection = direction + (360.0f / numberOfDirectionTextures) / 2.0f;
        while (offsetDirection >= 360.0) offsetDirection -= 360.0;

        setActiveTexture((int) ((float) (((offsetDirection) / 360.0)) * numberOfDirectionTextures));
    }

    public void moveTowards(Point2D.Float location) {
        direction = getRadiansTo(location) + (float) Math.PI / 2;
        //setDirection((float) Math.toDegrees(direction));
        grid.remove(this);
        incrementPosition((float) Math.sin(direction) * speed, -(float) Math.cos(direction) * speed);
        grid.add(this);
    }

    public void doTimeStep() {
        Point2D.Float playerPosition = SurvivalGame.getPlayerPosition();
        float dx = position.x - playerPosition.x;
        float dy = position.y - playerPosition.y;
        if (Math.sqrt(dx * dx + dy * dy) < 9600)
        moveTowards(playerPosition);
    }

    private void findPath(Point2D.Float location) {

    }

    public int getDamage() {
        return damage;
    }
}
