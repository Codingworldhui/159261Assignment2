import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

public class Example extends GameEngine{
    public static void main(String[] args) {
        createGame(new Example());
    }
    Image hero;
    Image background;
    Image[] motionless;
    Image[] run;
    Image[] jump;
    int currentFrame_Run;
    int currentFrame_rest;
    int currentFrame_jump;
    double animTime;
    double HeroPositionX;
    double HeroPositionY;
    double HeroVelocityX = 100;
    double HeroVelocityY = 0;
    double gravity = 980;
    public void init(){
        HeroPositionX = 100;
        HeroPositionY = 300;
        rest = 0;
        direction = "right";
        motionless = new Image[13];
        run = new Image[8];
        jump = new Image[6];
        hero = loadImage("src/adventurer.png");
        background = loadImage("src/background1.png");
        for (int i = 0; i < 13; i++) {
            motionless[i] = subImage(hero,32*i,0,32,32);
        }
        for(int j = 0;j < 8;j++){
            run[j] = subImage(hero,32*j,32,32,32);
        }
        //jump
        for (int k = 0; k < 6;k++){
            jump[k] = subImage(hero,32*k,32*5,32,32);
        }
        pos.setLocation(100,80);
    }


    Point2D pos = new Point2D.Double();
    boolean is_moving = false;
    boolean is_left = false;
    boolean is_jump = false;
    String direction;
    private double idleTime = 5.0; // Set the wait time to 5 seconds
    private double timeSinceStop = 0.0; // It is used to track the time elapsed after the stop
    int rest;//When the character is stationary for more than 5 seconds, the rest value is 1
    @Override
    public void update(double dt) {
        animTime += dt;
        if(is_moving){
            if(is_left){
              HeroPositionX -= HeroVelocityX * dt;
            }
            else
            {
                HeroPositionX += HeroVelocityX *dt;
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
        if(is_jump){
            HeroVelocityY += gravity *dt;
            HeroPositionY += HeroVelocityY *dt;
            currentFrame_jump = getFrame(0.3,6);
            if(HeroPositionY >= 300){
                HeroPositionY = 300;
                is_jump = false;
            }
        }

        //        }else if(is_jump){
//            currentFrame_jump = getFrame(0.3,6);
//            System.out.println(currentFrame_jump);
//            if(currentFrame_jump >= 5){
//               // is_jump = false;
//            }else if(currentFrame_jump % 2 == 0){
//                pos.setLocation(pos.getX(),pos.getY() - 5);
//            }else{
//                pos.setLocation(pos.getX(),pos.getY() + 5);
//
//            }
      //  System.out.println(currentFrame);
    }

    public  int getFrame(double d,int num_frames){return (int)Math.floor(((animTime % d) / d) * num_frames);}
    @Override
    public void paintComponent() {
        changeBackgroundColor(white);
        clearBackground(500,500);
//        drawImage(background,0,0,1000,500);
//        drawImage(run[currentFrame_Run],positionX,positionY,32*3,32*3);

       Running();
        StateMotionless();
        if(is_jump){
            changeBackgroundColor(white);
            clearBackground(500,500);
            drawImage(background,0,0,1000,500);
            if(direction.equals("right")){
                drawImage(jump[currentFrame_jump],HeroPositionX,HeroPositionY,32*3,32*3);
            }else {
                drawImage(jump[currentFrame_jump],HeroPositionX + 32*3,HeroPositionY,-32*3,32*3);

            }
        }
    }
    public void Running(){

        if(is_left){
            drawImage(background,0,0,1000,500);
            drawImage(run[currentFrame_Run],HeroPositionX+ 32*3,HeroPositionY,-32*3,32*3);

        }else{
            drawImage(background,0,0,1000,500);
            drawImage(run[currentFrame_Run],HeroPositionX,HeroPositionY,32*3,32*3);

        }
    }

    public void StateMotionless(){
        if(!is_moving){
            changeBackgroundColor(white);
            clearBackground(500,500);
            drawImage(background,0,0,1000,500);
            if(direction.equals("right")){
                drawImage(motionless[0],HeroPositionX,HeroPositionY ,32*3,32*3);

            }else {
                drawImage(motionless[0],HeroPositionX + 32*3,HeroPositionY,-32*3,32*3);
            }
            if(rest == 1){
                changeBackgroundColor(white);
                clearBackground(500,500);
                drawImage(background,0,0,1000,500);
                drawImage(motionless[currentFrame_rest],HeroPositionX + 32*3,HeroPositionY,-32*3,32*3);
            }
        }
    }
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_D){
            is_moving = true;
            is_left = false;
            direction = "right";
            rest = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_A){
            is_left = true;
            is_moving = true;
            direction = "left";
            rest = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
           if(HeroPositionY == 300){
               HeroVelocityY = -500;
               is_jump = true;
           }
            rest = 0;
        }
    }


    public void keyReleased(KeyEvent e){
        is_moving = false;
//        if(e.getKeyCode() == KeyEvent.VK_SPACE){
//           // is_jump = false;
//        }
    }
}
