import java.awt.*;
import java.util.ArrayList;

public class Monster {
    public Image monsterImage;
    public double monsterPositionX;
    public double monsterPositionY;
    public double monsterVelocityX = 50;
    public double monsterVelocityY = 0;
    public boolean is_left = false;
    public boolean is_appear = false;
    public boolean isDefeated;

    ArrayList<FireBall> fireballs;
    long lastFireTime;
    //每3s发生一次
    long fireInterval = 3000;

    public Monster() {
        fireballs = new ArrayList<>();
        lastFireTime = System.currentTimeMillis();
        isDefeated = false;
    }

    public void updateFireballs(double dt) {
        if (!isDefeated) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFireTime >= fireInterval) {
                fire();
                lastFireTime = currentTime;
            }

            for (FireBall fireball :fireballs) {
                fireball.update(dt);
            }
        }
    }

    public void drawFireballs(Graphics2D g) {
        if (!isDefeated) {
            for (FireBall fireball :fireballs) {
                fireball.draw(g);
            }
        }
    }

    private void fire() {
        // Create and add a new fireball
        FireBall fireball = new FireBall(monsterPositionX, monsterPositionY + 20, 60, 10);
        fireballs.add(fireball);
    }

    public void setDefeated(boolean defeated) {
        isDefeated = defeated;
    }

    public ArrayList<FireBall> getFireballs() {
        return fireballs;
    }

    public void removeFireball() {
        for (int i = 0; i < fireballs.size(); i++) {
            if(fireballs.get(i).getExpired()) {
                fireballs.remove(i);
            }
        }
    }

}
