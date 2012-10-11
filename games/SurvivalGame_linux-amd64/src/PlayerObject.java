import GameEngine.GameObject;

import java.awt.geom.Point2D;

class PlayerObject extends GameObject {

    int numberOfDirectionTextures = 72;
    float direction;

    float maxSpeed = 2;
    float acceleration = 1;

    Point2D.Float oldPosition;

    //==================================================================================================
    public PlayerObject(float x, float y) {
        super(x, y);
    }

    //==================================================================================================

    public void setDirection(float direction) {
        while (direction < 0.0) direction += 360.0;
        while (direction >= 360.0) direction -= 360.0;

        this.direction = direction;

        // settign the correct texture
        float offsetDirection = direction + (360.0f / numberOfDirectionTextures) / 2.0f;
        while (offsetDirection >= 360.0) offsetDirection -= 360.0;

        setActiveTexture((int) ((float) (((offsetDirection) / 360.0)) * numberOfDirectionTextures));
    }

    public void revertPosition() {
        this.setPosition(oldPosition);
    }

    public float getDirection() {
        return direction;
    }


    public void moveInDirection(float direction) {
        oldPosition = this.getPosition();
        incrementPosition((float) Math.sin(Math.toRadians(direction)) * 2, -(float) Math.cos(Math.toRadians(direction)) * 2);
        setDirection(direction);
    }

    public void inputMovement(float x, float y) {

    }
}
