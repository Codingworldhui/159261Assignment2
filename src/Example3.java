import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Example3 extends GameEngine{
    public static void main(String[] args) {
        createGame(new Example3());
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
    int currentFrame_Be_attacked;
    int currentFrame_die;
    double animTime;
    Hero littlehero = new Hero();
    double gravity = 980;
    boolean gameover;

    Monster boss = new Monster();

    public void initMonster() {
            boss.monsterPositionX = 500 ;// 设置怪物的初始位置
            boss.monsterPositionY = 120;
            // 加载怪物图像
            boss.monsterImage = loadImage("images/sotrak_rewop_0.png");
    }

    public void updateMonster(double dt) {
        if (!boss.is_left && boss.monsterPositionX < 800) {
            // 怪物向右移动
            boss.monsterPositionX += 100 * dt;
            if (boss.monsterPositionX >= 800) {
                boss.is_left = true;
            }
        } else if (boss.is_left && boss.monsterPositionX > 300) {
            // 怪物向左移动
            boss.monsterPositionX -= 100 * dt;
            if (boss.monsterPositionX <= 300) {
                boss.is_left = false;
            }
        }
    }

    public void drawMonster() {
        drawImage(boss.monsterImage, boss.monsterPositionX, boss.monsterPositionY);
    }



    public void init(){
        gameover = false;
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
        littlehero.die = new Image[7];
        littlehero.Be_attacked = new Image[4];
        littlehero.heroImg = loadImage("src/adventurer.png");
        background = loadImage("src/background3.png");

        initHealth();

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
        for (int k = 0; k < 4;k++){
            littlehero.Be_attacked[k] = subImage(littlehero.heroImg,32*k,32*6,32,32);
        }
        for (int k = 0; k < 7;k++){
            littlehero.die[k] = subImage(littlehero.heroImg,32*k,32*7,32,32);
        }

        initMonster();

    }




    String direction;
    private double idleTime = 3.0; // Set the wait time to 5 seconds
    private double timeSinceStop = 0.0; // It is used to track the time elapsed after the stop
    int rest;//When the character is stationary for more than 5 seconds, the rest value is 1
    @Override
    public void update(double dt) {
        if(!gameover){
            animTime += dt;

            if (littlehero.is_moving) {
                if (littlehero.is_left) {
                    littlehero.HeroPositionX -= littlehero.HeroVelocityX * dt;
                } else {
                    littlehero.HeroPositionX += littlehero.HeroVelocityX * dt;
                }
                currentFrame_Run = getFrame(1.0, 8);
                timeSinceStop = 0.0;
            } else {
                timeSinceStop += dt;
                if (timeSinceStop >= idleTime) {
                    rest = 1;
                    currentFrame_rest = getFrame(2.0, 13);
                }
            }
            if (littlehero.is_jump) {
                littlehero.HeroPositionY += littlehero.HeroVelocityY * dt;
                littlehero.HeroVelocityY += gravity * dt;
                currentFrame_jump = getFrame(0.3, 6);
                if (littlehero.HeroPositionY >= 300) {
                    littlehero.HeroPositionY = 300;
                    littlehero.is_jump = false;
                }
            }
            if(attackMode != -1) {
                currentFrame_attack = getFrame(1.3, 10);
                if (currentFrame_attack >= 9) {
                    attackMode = -1;
                }
            }


            if (littlehero.be_attacked){
                currentFrame_Be_attacked = getFrame(0.75,4);
                if(currentFrame_Be_attacked >= 3){
                    littlehero.be_attacked = false;
                }
//            System.out.println(currentFrame_Be_attacked);
            }
            if (!littlehero.is_alive){
                currentFrame_die = getFrame(1,7);
                if(currentFrame_die >= 6){
                    gameover = true;
                }
            }

        }
        if(healthCount < 0){
            littlehero.is_alive = false;
        }

        updateMonster(dt);

    }

    public  int getFrame(double d,int num_frames){return (int)Math.floor(((animTime % d) / d) * num_frames);}
    @Override
    public void paintComponent() {
        if(!gameover){
            drawImage(background,0,0,1000,500);
            //   drawRectangle(littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
            changeColor(red);
            drawHealth();

            Running();
            StateMotionless();
            Jumping();
            Attack();
            Die();
            Be_attack();

        }else {
            changeColor(white);
            drawText(300,220,"GAME OVER!","Arial",70);
        }

        // drawLine(littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3 ,littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3,5);

        drawMonster();

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
    public void Be_attack(){
        if (littlehero.be_attacked){
            // drawImage(background,0,0,1000,500);
            drawImage(littlehero.Be_attacked[currentFrame_Be_attacked],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);

        }
    }
    public void Die(){
        if (!littlehero.is_alive){
            //drawImage(background,0,0,1000,500);
            drawImage(littlehero.die[currentFrame_die],littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
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
        if(!littlehero.is_moving && !littlehero.is_jump && attackMode == -1 && !littlehero.be_attacked && littlehero.is_alive){

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
        if (e.getKeyCode()==KeyEvent.VK_O){
            littlehero.be_attacked=true;
        }
        if (e.getKeyCode()==KeyEvent.VK_U){
            littlehero.is_alive=false;
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