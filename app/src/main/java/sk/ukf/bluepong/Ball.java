package sk.ukf.bluepong;

public class Ball {

    private int x;
    private int y;
    private int width;
    private int height;
    private int speedX;
    private int speedY;
    private boolean directionRight;
    private boolean directionDown;

    public Ball(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        speedX = Game.getW() / 50;
        speedY = Game.getH() / 100;
        directionRight = true;
        directionDown = true;
    }

    public boolean move() {
        if (directionRight) x += speedX;
        else x -= speedX;
        if (directionDown) y += speedY;
        else y -= speedY;

        return true;
    }

    public boolean checkPaddleCollision(Paddle paddle) {
        if (paddle.getX() - paddle.getWidth() < x + width &&
                paddle.getX() + paddle.getWidth() > x - width &&
                paddle.getY() - paddle.getHeight() < y + height &&
                paddle.getY() + paddle.getHeight() > y - height) {
            directionDown = !directionDown;
            return true;
        }
        return false;
    }

    public boolean checkSideWallCollision() {
        if (x + width >= Game.getW() || x - width <= 0) {
            directionRight = !directionRight;
            return true;
        }
        return false;
    }

    public boolean checkBallScored() {
        return y + width >= Game.getH() || y - width <= 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpeedX() { return speedX; }
    public int getSpeedY() { return speedY; }
    public boolean getDirectionRight() { return directionRight; }
    public boolean getDirectionDown() { return directionDown; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setSpeedX(int speedX) { this.speedX = speedX; }
    public void setSpeedY(int speedY) { this.speedY = speedY; }
    public void setDirectionRight(boolean directionRight) { this.directionRight = directionRight; }
    public void setDirectionDown(boolean directionDown) { this.directionDown = directionDown; }
}
