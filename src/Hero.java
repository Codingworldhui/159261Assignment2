import java.awt.*;

public class Hero {
    public Image heroImg;
    public Image[] run;
    public Image[] jump;
    public Image[] attack1;
    public Image[] attack2;
    public Image[] attack3;
    public double HeroPositionX;
    public double HeroPositionY;
    public double HeroVelocityX = 100;
    public double HeroVelocityY = 0;
    public boolean is_moving = false;
    public boolean is_left = false;
    public boolean is_right = false;
    public boolean is_jump = false;


}