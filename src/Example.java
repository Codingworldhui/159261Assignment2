import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

public class Example extends GameEngine{
    public static void main(String[] args) {
        createGame(new Example());
    }

    Image background;
    Image[] motionless;

    int currentFrame_Run;
    int currentFrame_rest;
    int currentFrame_jump;
    double animTime;
    Hero littlehero = new Hero();
    double gravity = 980;
    public void init(){
        littlehero.HeroPositionX = 100;
        littlehero.HeroPositionY = 300;
        rest = 0;
        direction = "right";
        motionless = new Image[13];
        littlehero.run = new Image[8];
        littlehero.jump = new Image[6];
        littlehero.heroImg = loadImage("src/adventurer.png");
        background = loadImage("src/background1.png");
        for (int i = 0; i < 13; i++) {
            motionless[i] = subImage(littlehero.heroImg,32*i,0,32,32);
        }
        for(int j = 0;j < 8;j++){
            littlehero.run[j] = subImage(littlehero.heroImg,32*j,32,32,32);
        }
        //jump
        for (int k = 0; k < 6;k++){
            littlehero.jump[k] = subImage(littlehero.heroImg,32*k,32*5,32,32);
        }
    }




    String direction;
    private double idleTime = 3.0; // Set the wait time to 5 seconds
    private double timeSinceStop = 0.0; // It is used to track the time elapsed after the stop
    int rest;//When the character is stationary for more than 5 seconds, the rest value is 1
    @Override
    public void update(double dt) {
        animTime += dt;
        if(littlehero.is_moving){
            if(littlehero.is_left){
                littlehero.HeroPositionX -= littlehero.HeroVelocityX * dt;
            }
            else
            {
                littlehero.HeroPositionX += littlehero.HeroVelocityX *dt;
            }
            currentFrame_Run = getFrame(1.0,8);
            timeSinceStop = 0.0;
        } else{
            timeSinceStop += dt;
            if(timeSinceStop >= idleTime){
                rest = 1;
                currentFrame_rest = getFrame(2.0,13);
            }
        }
        if(littlehero.is_jump){
            littlehero.HeroPositionY += littlehero.HeroVelocityY *dt;
            littlehero.HeroVelocityY += gravity *dt;
// 1/2 gt2
            currentFrame_jump = getFrame(0.3,6);
            if(littlehero.HeroPositionY >= 300){
                littlehero.HeroPositionY = 300;
                littlehero.is_jump = false;
            }
        }
    }

    public  int getFrame(double d,int num_frames){return (int)Math.floor(((animTime % d) / d) * num_frames);}
    @Override
    public void paintComponent() {

        Running();
        StateMotionless();
        if(littlehero.is_jump){
//            changeBackgroundColor(white);
//            clearBackground(500,500);
            drawImage(background,0,0,1000,500);
            if(direction.equals("right")){
                drawImage(littlehero.jump[currentFrame_jump],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
            }else {
                drawImage(littlehero.jump[currentFrame_jump],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);

            }
        }
    }
    public void Running(){

        if(littlehero.is_left){
            drawImage(background,0,0,1000,500);
            drawImage(littlehero.run[currentFrame_Run],littlehero.HeroPositionX+ 32*3,littlehero.HeroPositionY,-32*3,32*3);

        }else{
            drawImage(background,0,0,1000,500);
            drawImage(littlehero.run[currentFrame_Run],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);

        }
    }

    public void StateMotionless(){
        if(!littlehero.is_moving){
            changeBackgroundColor(white);
            clearBackground(500,500);
            drawImage(background,0,0,1000,500);
            if(direction.equals("right")){
                drawImage(motionless[0],littlehero.HeroPositionX,littlehero.HeroPositionY ,32*3,32*3);

            }else {
                drawImage(motionless[0],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
            }
            if(rest == 1){
                changeBackgroundColor(white);
                clearBackground(500,500);
                drawImage(background,0,0,1000,500);
                drawImage(motionless[currentFrame_rest],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
            }
        }
    }
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_D){
            littlehero.is_moving = true;
            littlehero.is_left = false;
            direction = "right";
            rest = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_A){
            littlehero.is_left = true;
            littlehero.is_moving = true;
            direction = "left";
            rest = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if(littlehero.HeroPositionY == 300){
                littlehero.HeroVelocityY = -500;
                littlehero.is_jump = true;
            }
            rest = 0;
//           一瞬间扭头
            System.out.println(rest);
        }
    }

    // 往右走 和 空格冲突
    public void keyReleased(KeyEvent e){
        littlehero.is_moving = false;
//        if(e.getKeyCode() == KeyEvent.VK_SPACE){
//           // is_jump = false;
//        }
    }
}