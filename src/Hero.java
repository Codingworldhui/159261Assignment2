import java.awt.*;

public class Hero {
    public Image heroImg;
    public Image[] run;
    public Image[] jump;
    public Image[] attack1;
    public Image[] attack2;
    public Image[] attack3;
    public Image[] Be_attacked;
    public Image[] die;
    public double HeroPositionX;
    public double HeroPositionY;
    public double HeroVelocityX = 200;
    public double HeroVelocityY = 0;
    public boolean is_moving = false;
    public boolean is_left = false;
    public boolean is_right = false;
    public boolean is_jump = false;
    public boolean be_attacked = false;
    public boolean is_alive = true;

    public boolean fire_available = false;

    public boolean wave_available = false;

    public boolean is_attacking = false;


}