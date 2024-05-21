import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

public class Example extends GameEngine{
    public static void main(String[] args) {
        createGame(new Example());
    }

    Image background;
    Image portalImage;

    Image portalDown[];
    Image[] motionless;
    Image[] health;

    int currentFrame_Run;
    int currentFrame_rest;
    int currentFrame_jump;
    int currentFrame_attack;
    int attackMode;
    double animTime;
    Hero littlehero = new Hero();
    double gravity = 980;
    public void init(){
        littlehero.HeroPositionX = 100;
        littlehero.HeroPositionY = 300;
        rest = 0;
        attackMode = -1;
        direction = "right";
        motionless = new Image[13];
        littlehero.run = new Image[8];
        littlehero.jump = new Image[6];
        littlehero.attack1 = new Image[10];
        littlehero.attack2 = new Image[10];
        littlehero.attack3 = new Image[10];
        littlehero.heroImg = loadImage("src/adventurer.png");
        background = loadImage("src/background1.png");
        initPortal();

        initHealth();

        initSun();

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
        //attack1,2,3
        for (int l = 0;l < 10;l++){
            littlehero.attack1[l] = subImage(littlehero.heroImg,32*l,32*2,32,32);
            littlehero.attack2[l] = subImage(littlehero.heroImg,32*l,32*3,32,32);
            littlehero.attack3[l] = subImage(littlehero.heroImg,32*l,32*4,32,32);
        }
    }




    String direction;
    private double idleTime = 3.0; // Set the wait time to 5 seconds
    private double timeSinceStop = 0.0; // It is used to track the time elapsed after the stop
    int rest;//When the character is stationary for more than 5 seconds, the rest value is 1
    @Override
    public void update(double dt) {
        updatePortal(dt);
        updateSun(dt);
        animTime += dt;
        if(littlehero.is_moving){
            if(littlehero.is_left){
                littlehero.HeroPositionX -= littlehero.HeroVelocityX * dt;
            }
            else
            {
                littlehero.HeroPositionX += littlehero.HeroVelocityX *dt;
            }
            currentFrame_Run = getFrame(0.3,8);
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
        if(attackMode != -1){
            currentFrame_attack = getFrame(1.3,10);
            if(currentFrame_attack >= 9){
                attackMode = -1;
            }
        }
    }

    public  int getFrame(double d,int num_frames){return (int)Math.floor(((animTime % d) / d) * num_frames);}
    @Override
    public void paintComponent() {
        drawImage(background,0,0,1000,500);
     //   drawRectangle(littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
        changeColor(red);
        drawPortal();
        drawHealth();

        drawSun();
        Running();
        StateMotionless();
        Jumping();
        Attack();
       // drawLine(littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3 ,littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3,5);


    }
    public void initPortal(){
        portalImage = loadImage("src/teleport.png");

        portalDown = new Image[5];
        for (int i = 0; i < 5; i++) {
            portalDown[i] = subImage(portalImage,0,256*i,512,256);
        }
    }
    int portalFrame;
    public void updatePortal(double dt){
        portalFrame = getFrame(2.0,4);
    }
    public void drawPortal(){

        drawImage(portalDown[portalFrame],800,340,128*1.5,64*1.5);
    }

    //health
    int healthCount;
    public void initHealth(){
        health = new Image[10];
        healthCount = 9;

        for (int i = 1; i < 11; i++) {
            String imageName = "Health/Health" + i + ".png";
            // 加载图片并存储到数组中
            Image image = loadImage(imageName);
            health[i-1] = image;
        }
    }
    public void drawHealth(){
        drawImage(health[healthCount],littlehero.HeroPositionX + 5,littlehero.HeroPositionY - 10,378*0.2,38*0.2d);
    }
    //Falling objects
    int maxSuns;
    double sunX[],  sunY[];
    double sunVX[], sunVY[];
    double sunAngle[];
    Image sun;
    Image sunImage[];
    public void initSun(){
        sun = loadImage("src/sun.png");
        maxSuns = 5;
        sunX = new double[maxSuns];
        sunY = new double[maxSuns];
        sunVX = new double[maxSuns];
        sunVY = new double[maxSuns];
        sunAngle = new double[maxSuns];
        sunImage = new Image[3];
        for (int i = 0; i < 3; i++) {
            sunImage[i] = subImage(sun,50*i,0,50,50);
        }
        for (int i = 0; i < maxSuns; i++) {
            newSun(i);
        }
    }
    public void newSun(int i){
        sunX[i] = rand(1000);
        sunY[i] = 0;



        // Random Target Position
        double tx = rand(1000);
        double ty = 500;

        // Calculate velocity
        sunVX[i] = tx - sunX[i];
        sunVY[i] = ty - sunY[i];

        // Calculate angle
        sunAngle[i] = atan2(sunVX[i], -sunVY[i]) - 90;

        // Rescale velocity
        double l = length(sunVX[i], sunVY[i]);
        sunVX[i] *= 80 / l;
        sunVY[i] *= 80 / l;
    }
    int sunFrame;
    public void updateSun(double dt){
        sunFrame = getFrame(0.8,3);
        for(int i = 0; i < maxSuns; i++) {
            // Move Missile down
            sunX[i] += sunVX[i]*dt;
            sunY[i] += sunVY[i]*dt;

            // Check if missile has reached the bottom of the screen
            if(sunY[i] >= 500) {
                newSun(i);
            }
           // drawLine(littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3 ,littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3,5);
           // littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3   这是小英雄的圆心
            if(distance(littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3 ,sunX[i],sunY[i]) < 25 + 32*0.5){
                newSun(i);
                healthCount--;
            }
        }
    }
    public void drawSun(){
        for(int i = 0; i < maxSuns; i++) {

            // Save Transform
            saveCurrentTransform();


            translate(sunX[i], sunY[i]);

            // Rotate by sun Angle
            rotate(sunAngle[i]);

            // Draw Missile Image
            drawImage(sunImage[sunFrame], -50, -50, 50, 50);


            // Reset Transform
            restoreLastTransform();
        }
    }

    Image missileImage;
    public void Attack(){
       // clearBackground(1000,500);

        if(direction.equals("right")){
            switch (attackMode){
                case 1:
                    drawImage(littlehero.attack1[currentFrame_attack],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
                    break;
                case 2:
                    drawImage(littlehero.attack2[currentFrame_attack],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
                    break;
                case 3:
                    drawImage(littlehero.attack3[currentFrame_attack],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
                    break;

            }
       }else {
            switch (attackMode){
                case 1:
                    drawImage(littlehero.attack1[currentFrame_attack],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
                    break;
                case 2:
                    drawImage(littlehero.attack2[currentFrame_attack],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
                    break;
                case 3:
                    drawImage(littlehero.attack3[currentFrame_attack],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
                    break;

            }
        }

    }
    public void Jumping(){
        if(littlehero.is_jump){
//            changeBackgroundColor(white);
//            clearBackground(500,500);

            if(direction.equals("right")){
                drawImage(littlehero.jump[currentFrame_jump],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
            }else {
                drawImage(littlehero.jump[currentFrame_jump],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);

            }
        }
    }
    public void Running(){
        if(littlehero.is_moving && attackMode == -1){
            if(littlehero.is_left){

                drawImage(littlehero.run[currentFrame_Run],littlehero.HeroPositionX+ 32*3,littlehero.HeroPositionY,-32*3,32*3);

            }else if(littlehero.is_right){

                drawImage(littlehero.run[currentFrame_Run],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);

            }
        }

    }

    public void StateMotionless(){
        if(!littlehero.is_moving && !littlehero.is_jump && attackMode == -1){

            if(direction.equals("right")){
                drawImage(motionless[0],littlehero.HeroPositionX,littlehero.HeroPositionY ,32*3,32*3);

            }else {
                drawImage(motionless[0],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
            }
//            if(rest == 1){
//
//                drawImage(motionless[currentFrame_rest],littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,-32*3,32*3);
//            }
        }
    }
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_D){
            littlehero.is_moving = true;
            littlehero.is_left = false;
            littlehero.is_right = true;

            direction = "right";
            rest = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_A){
            littlehero.is_left = true;
            littlehero.is_right = false;
            littlehero.is_moving = true;
            direction = "left";
            rest = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if(littlehero.HeroPositionY == 300){
                littlehero.HeroVelocityY = -500;
                littlehero.is_jump = true;
                littlehero.is_moving = false;
            }
            rest = 0;
//           一瞬间扭头
           // System.out.println(rest);
        }
        if(e.getKeyCode() == KeyEvent.VK_J){
            attackMode = 1;
            rest = 0;
         //   littlehero.is_moving = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_K){
            attackMode = 2;
            rest = 0;
           // littlehero.is_moving = true;
        } if(e.getKeyCode() == KeyEvent.VK_L){
            attackMode = 3;
            rest = 0;
           // littlehero.is_moving = true;
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