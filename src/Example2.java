import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Example2 extends GameEngine {

//    public static void main(String[] args) {
//        createGame(new Example2());
//    }

    Image background;
    Image portalImage;

    Image portalDown[];
    Image[] motionless;
    Image[] health;
    Image wave;
    double wavePositionX;
    double wavePositionY;
    double WaveVelocityX;
    String waveDirection;

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
    int score;
    private final ParticleEmitter fire =new FireParticleEmitter();;
    private float fps;

    GameEngine.AudioClip audioClip;
    boolean gameover;

    boolean example2Created;
    private List<Monster2> Monster = new CopyOnWriteArrayList<>();

    public void initScore() {
        score = 0;
    }

    public void initAudio() {
        audioClip = loadAudio("audio/pop.wav");
    }

    AudioClip fireAudio;
    AudioClip waveAudio;
    boolean fireCreated;
    public void initFireAudio() {
        fireAudio = loadAudio("audio/fire.wav");
    }
    public void initWaveAudio() {
        waveAudio = loadAudio("audio/wave.wav");
    }



    public void init(){
        example2Created = false;
        fireCreated = false;
        initFireAudio();
        initWaveAudio();

        gameover = false;
        littlehero.HeroPositionX = 40;
        littlehero.HeroPositionY = 280;
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
        background = loadImage("src/background2.jpg");
        initPortal();

        initHealth();
        initCherry();

        initScore();
        initAudio();
        initMonster();
        initWave();

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

    }




    String direction;
    private double idleTime = 3.0; // Set the wait time to 5 seconds
    private double timeSinceStop = 0.0; // It is used to track the time elapsed after the stop
    int rest;//When the character is stationary for more than 5 seconds, the rest value is 1
    private double MonsteridleTime = 5.0;
    private double MonstertimeSinceStop = 0.0;
    @Override
    public void update(double dt) {
        if(!gameover){
            updateFire(dt);
            updatePortal(dt);
            updateCherry(dt);
            animTime += dt;
            updateWave(dt);
            updateMonster(dt);
            if(Monster.isEmpty()){
                MonstertimeSinceStop+=dt;
                if(MonstertimeSinceStop >= MonsteridleTime){
                    MonstertimeSinceStop = 0;
                    Monster2 newMonster = new Monster2(500,200,200);
                    Monster.add(newMonster);
                }
            }
            if (littlehero.is_moving) {
                if (littlehero.is_left) {
                    littlehero.HeroPositionX -= littlehero.HeroVelocityX * dt;
                    if(littlehero.HeroPositionX <=330 && littlehero.HeroPositionX >= 160){
                        littlehero.HeroPositionY -= 1.5;
                    }
                    if(littlehero.HeroPositionX <= 680 && littlehero.HeroPositionX >= 600){
                        littlehero.HeroPositionY += 2.5;
                    }
                } else {
                    littlehero.HeroPositionX += littlehero.HeroVelocityX * dt;
                    if(littlehero.HeroPositionX <= 330 && littlehero.HeroPositionX >= 160){
                        littlehero.HeroPositionY += 1.5;
                    }
                    if(littlehero.HeroPositionX <= 680 && littlehero.HeroPositionX >= 600){
                        littlehero.HeroPositionY -= 2.5;
                    }
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
            healthCount = 0;
            littlehero.is_alive = false;
        }

        if(distance(littlehero.HeroPositionX,littlehero.HeroPositionY,800,340 )< 100 && !example2Created) {
            createGame(new Example3());
            example2Created = true;
            mFrame.dispose();
        }


    }

    public  int getFrame(double d,int num_frames){return (int)Math.floor(((animTime % d) / d) * num_frames);}
    @Override
    public void paintComponent() {
        setWindowSize(1000,500);

        if(!gameover){
            drawImage(background,0,0,1000,500);
            // drawSolidCircle(littlehero.HeroPositionX,littlehero.HeroPositionY,20);
            drawFire();
            //   drawRectangle(littlehero.HeroPositionX,littlehero.HeroPositionY,32*3,32*3);
            changeColor(red);
            drawPortal();
            drawHealth();
            drawCherry();
            Running();
            StateMotionless();
            Jumping();
            Attack();
            Die();
            Be_attack();
            drawWave();
            drawMonster();

            changeColor(white);
            drawText(50, 50, "Score: "+score);


        }else {
            changeColor(white);
            drawText(300,220,"GAME OVER!","Arial",70);
        }

        // drawLine(littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3 ,littlehero.HeroPositionX + 0.5 * 32*3,littlehero.HeroPositionY + 0.7 * 32*3,5);


    }
    //
    public void updateFire(double dt){
        fire.update((float) dt);
        fps=1.0f/(float)dt;
    }
    public void drawFire(){
        if(littlehero.fire_available){
            saveCurrentTransform();
            //  rotate(90);
            if(littlehero.is_left){
                fire.emitterangle = -90;
            }else if(littlehero.is_right) {
                fire.emitterangle = 90;
            }
            fire.move((float) littlehero.HeroPositionX + 50, (float) littlehero.HeroPositionY + 55);
            //  translate(littlehero.HeroPositionX,littlehero.HeroPositionY);
            fire.draw(this);
            restoreLastTransform();
        }

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
    //Monster
    public void initMonster(){
        Monster2.MonsterImage = loadImage("images/monster2.png");
        Monster2 newMonster = new Monster2(500,200,200);
        Monster.add(newMonster);
    }
    private double FireidleTime = 1.5;
    private double FiretimeSinceStop = 0.0;
    public void updateMonster(double dt){
        if(!Monster.isEmpty()){
            //如果被打到了
            for (Monster2 monster2 : Monster){
                if(distance(wavePositionX,wavePositionY,monster2.monsterPositionX,monster2.monsterPositionY) < 110){
                    Monster.remove(monster2);
                    if(monster2.monsterSize > 100){
                        Monster2 newmonster1 = new Monster2(monster2.monsterPositionX - 50,monster2.monsterPositionY + 100,monster2.monsterSize/2);
                        Monster2 newmonster2 = new Monster2(monster2.monsterPositionX + 50,monster2.monsterPositionY + 100,monster2.monsterSize/2);
                        Monster.add(newmonster1);
                        Monster.add(newmonster2);

                    }
                    break;
                }else if(littlehero.fire_available && distance(littlehero.HeroPositionX+32*3,littlehero.HeroPositionY,monster2.monsterPositionX,monster2.monsterPositionY) < 150){
                    FiretimeSinceStop+=dt;
                    fireCreated = true;
                    if(FiretimeSinceStop >= FireidleTime){
                        fireCreated = false;
                        FiretimeSinceStop = 0.0;
                        Monster.remove(monster2);
                        if(monster2.monsterSize > 100){
                            Monster2 newmonster1 = new Monster2(monster2.monsterPositionX - 50,monster2.monsterPositionY + 100,monster2.monsterSize/2);
                            Monster2 newmonster2 = new Monster2(monster2.monsterPositionX + 50,monster2.monsterPositionY + 100,monster2.monsterSize/2);
                            Monster.add(newmonster1);
                            Monster.add(newmonster2);

                        }
                        break;
                    }
                }

            }

        }

    }

    public void drawMonster(){
        if(!Monster.isEmpty()){
            for (Monster2 monster:Monster){
                drawImage(monster.MonsterImage,monster.monsterPositionX  + monster.monsterSize,monster.monsterPositionY,-monster.monsterSize,monster.monsterSize);
                drawSolidCircle(littlehero.HeroPositionX + 32*3,littlehero.HeroPositionY,5);
            }

        }

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
    //wave
    public void initWave(){
        wave = loadImage("src/wave.png");

        WaveVelocityX = 300;
        waveDirection = direction;
    }
    public void createWave( ){
        wavePositionX = littlehero.HeroPositionX;
        wavePositionY = littlehero.HeroPositionY - 100;
        littlehero.wave_available = true;
        waveDirection = direction;
    }

    public void updateWave(double dt){
        if(waveDirection.equals("left")){
            wavePositionX -= WaveVelocityX * dt;
        }else if(waveDirection.equals("right")){
            wavePositionX += WaveVelocityX * dt;
        }
        if(wavePositionX <= -300 || wavePositionX >= 1000){
            littlehero.wave_available = false;
        }
    }
    public void drawWave(){
        if(littlehero.wave_available){
            saveCurrentTransform();
            translate(wavePositionX,wavePositionY);
            drawImage(wave,0,0,650 * 0.5,441 * 0.5);
            //   drawSolidCircle(wavePositionX,wavePositionY,5);

            restoreLastTransform();
        }

    }
    //Falling objects
    int maxCherry;
    double cherryX[],  cherryY[];
    double cherryVX[], cherryVY[];
    double cherryAngle[];
    Image cherry;
    Image cherryImage[];
    public void initCherry(){
        cherry = loadImage("src/cherry.png");
        maxCherry = 10;
        cherryX = new double[maxCherry];
        cherryY = new double[maxCherry];
        cherryVX = new double[maxCherry];
        cherryVY = new double[maxCherry];
        cherryAngle = new double[maxCherry];
        cherryImage = new Image[3];
        for (int i = 0; i < 3; i++) {
            cherryImage[i] = subImage(cherry,297*i,0,297,312);
        }
        for (int i = 0; i < maxCherry; i++) {
            newCherry(i);
        }
    }
    public void newCherry(int i){
        cherryX[i] = rand(1000);
        cherryY[i] = 0;



        // Random Target Position
        double tx = rand(1000);
        double ty = 500;

        // Calculate velocity
        cherryVX[i] = tx - cherryX[i];
        cherryVY[i] = ty - cherryY[i];

        // Calculate angle
        cherryAngle[i] = atan2(cherryVX[i], -cherryVY[i]) - 90;

        // Rescale velocity
        double l = length(cherryVX[i], cherryVY[i]);
        cherryVX[i] *= 80 / l;
        cherryVY[i] *= 80 / l;
    }
    int sunFrame;
    public void updateCherry(double dt){
        sunFrame = getFrame(0.8,3);
        for(int i = 0; i < maxCherry; i++) {
            // Move Missile down
            cherryX[i] += cherryVX[i]*dt;
            cherryY[i] += cherryVY[i]*dt;

            // Check if missile has reached the bottom of the screen
            if(cherryY[i] >= 500) {
                newCherry(i);
            }

        }
    }
    public void drawCherry(){
        for(int i = 0; i < maxCherry; i++) {

            // Save Transform
            saveCurrentTransform();


            translate(cherryX[i], cherryY[i]);

            // Rotate by sun Angle
            rotate(cherryAngle[i]);

            // Draw Missile Image
            drawImage(cherryImage[sunFrame], -50, -50, 50, 50);


            // Reset Transform
            restoreLastTransform();
        }
    }




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
            System.out.println("?");
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
        if (e.getKeyCode()==KeyEvent.VK_I){
            littlehero.fire_available = true;
            playAudio(fireAudio);
        }
        if (e.getKeyCode()==KeyEvent.VK_O){
            if(!littlehero.wave_available){
                attackMode = 3;
                createWave();
            }
            if(littlehero.wave_available) {
                playAudio(waveAudio);
            }
        }
        if (e.getKeyCode()==KeyEvent.VK_U){

        }
        if(e.getKeyCode() == KeyEvent.VK_4){
            init();
        }
    }

    // 往右走 和 空格冲突
    public void keyReleased(KeyEvent e){
        littlehero.is_moving = false;
        if (e.getKeyCode()==KeyEvent.VK_I){
            littlehero.fire_available = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_O) {

        }
    }
}
