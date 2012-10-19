import GameEngine.GameObject;

import java.awt.geom.Point2D;

class PlayerObject extends GameObject {

    int numberOfDirectionTextures = 72;
    float direction;

    int health;
    float maxSpeed = 3;
    float accelerationScale = 0.2f;
    float slowDown = 0.95f;
    Point2D.Float velocity = new Point2D.Float(0, 0);

    Point2D.Float oldPosition;
    Point2D.Float collision;

    public PlayerObject(float x, float y) {
        super(x, y);
        health = 3;
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

    public void revertPosition() {
        this.setPosition(oldPosition);
    }

    public float getDirection() {
        return direction;
    }

    public void resetCollision() {
        this.collision = null;
    }

    public void setCollisionAt(Point2D.Float location) {
        this.collision = location;
    }

    public void moveInDirection(float direction) {
        oldPosition = this.getPosition();
        incrementPosition((float) Math.sin(Math.toRadians(direction)) * 20, -(float) Math.cos(Math.toRadians(direction)) * 20);
        setDirection(direction);
    }

    public void inputMovement(Point2D.Float acceleration) {
        oldPosition = this.getPosition();
        if (acceleration.x == 0 && acceleration.y == 0) {
            velocity.x *= slowDown;
            velocity.y *= slowDown;
        } else {
            float accelerationAbs = (float) Math.sqrt(acceleration.x * acceleration.x + acceleration.y * acceleration.y);
            velocity.x += acceleration.x * accelerationScale / accelerationAbs;
            velocity.y += acceleration.y * accelerationScale / accelerationAbs;
            float speed = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
            if (speed > maxSpeed) {
                velocity.x = velocity.x * maxSpeed / speed;
                velocity.y = velocity.y * maxSpeed / speed;
            }
        }
        if (collision == null) {
        } else {
            //revert the position and bounce a little bit
            position.x += (position.x - collision.x) / 2;
            position.y += (position.y - collision.y) / 2;
            Point2D.Float proj = new Point2D.Float();
            float collisionDirection = getRadiansTo(collision);
            float movementDirection = (float) Math.atan2(velocity.y, velocity.x);
            float angleBetween = movementDirection - collisionDirection;
            float velocityAbs = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
            float cosAB = (float) Math.cos(angleBetween);  //angle between
            velocity.x -= velocityAbs * cosAB * (float) Math.cos(collisionDirection) * 1.1;
            velocity.y -= velocityAbs * cosAB * (float) Math.sin(collisionDirection) * 1.1;
            resetCollision();
        }
        incrementPosition(velocity.x, velocity.y);
    }

    public void damage(int amount) {
        health -= amount;
    }

    public int getHealth() {
        return health;
    }
}
