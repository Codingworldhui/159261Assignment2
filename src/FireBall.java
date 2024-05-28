import java.awt.*;

public class FireBall {
    double positionX,positionY;
    double velocityX,velocityY;
    //火球已移动的总距离
    double totalDistanceMoved;

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
    public boolean getExpired() {
        return isExpired;
    }

    //火球是否已消失
    boolean isExpired;
    //Image fireballImage;

    public FireBall(double x, double y, double vx, double vy) {
        positionX = x;
        positionY = y;
        velocityX = vx;
        velocityY = vy;
        totalDistanceMoved = 0;
        isExpired = false;
        //this.fireballImage = fireballImage;
    }

    public void update(double dt) {
        if (!isExpired) {
            positionX -= velocityX * dt;
            //positionY += velocityY * dt;
            double distance = Math.abs(velocityX * dt);
            totalDistanceMoved += distance;
            if (totalDistanceMoved >= 150) {
                isExpired = true;
            }
        }
    }

    public void draw(Graphics2D g) {
        if (!isExpired) {
            //g.drawImage(fireballImage,(int) positionX,(int) positionY,null);
            g.setColor(Color.YELLOW);
            g.fillOval((int) positionX,(int) positionY,20,20);
            g.setColor(Color.RED);
            g.drawOval((int) positionX,(int) positionY,20,20);
        }
    }


}