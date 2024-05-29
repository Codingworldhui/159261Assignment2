import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Example3 extends GameEngine {
//    public static void main(String[] args) {
//        createGame(new Example3());
//    }

    int score;
    Image background;
    Image portalImage;
    Image portalDown[];
    Image[] motionless;
    Image[] health;
    Image laserImage; // 激光图片
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
    private final ParticleEmitter fire = new FireParticleEmitter();
    boolean fire_available;
    private float fps;
    double gravity = 980;
    boolean gameover;
    boolean gameWin = false;
    boolean wave_available;

    Monster boss = new Monster();
    double bossHealth = 2000;
    double maxBossHealth = 2000;

    boolean bossAttacking = false;
    int bossAttackMode = 0;
    double bossAttackTimer = 0;
    double bossAttackCooldown = 5.0; // 改成5秒
    AudioClip backgroundMusic;
    AudioClip heroAttackSound;
    AudioClip heroHurtSound;
    boolean ending = false;
    boolean fireAudioPlayed;
    boolean waveAudioPlayed;

    AudioClip fireAudio = loadAudio("audio/fire.wav");
    AudioClip waveAudio = loadAudio("audio/wave.wav");

    ArrayList<Rectangle> bossAttackZones = new ArrayList<>();
    ArrayList<Coin> coinList = new ArrayList<>();
    Random rand = new Random();
    // 金币数量
    int NUM_COINS = 3;

    public void initAudio() {
        backgroundMusic = loadAudio("audio/Hitman.wav");
        heroAttackSound = loadAudio("audio/melee-sound.wav");
        heroHurtSound = loadAudio("audio/hurt.wav");

        // 播放背景音乐
        startAudioLoop(backgroundMusic);
    }

    public void initMonster() {
        boss.monsterPositionX = 500; // 设置怪物的初始位置
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

        // BOSS攻击逻辑
        bossAttackTimer += dt;
        if (bossAttackTimer >= bossAttackCooldown) {
            bossAttacking = true;
            bossAttackMode = (bossAttackMode + 1) % 3;
            bossAttackTimer = 0;
            bossAttackZones.clear();
        }

        if (bossAttacking) {
            switch (bossAttackMode) {
                case 0:
                    // 第一种攻击方式：屏幕出现两条黄色提示线，将在1秒后变成激光
                    if (bossAttackTimer < 1.0) {
                        bossAttackZones.add(new Rectangle(0, 0, 1000, 20));
                        bossAttackZones.add(new Rectangle(0, 300, 1000, 20));
                    } else {
                        for (Rectangle zone : bossAttackZones) {
                            // drawImage(laserImage, zone.x, zone.y, zone.width, zone.height);
                            if (zone.intersects(littlehero.getBounds())) {
                                littlehero.be_attacked = true;
                            }
                        }
                        bossAttacking = false;
                    }
                    break;
                case 2:
                    // 第二种攻击方式：屏幕出现竖屏的6条从下而上的绿色提示线，1秒后出现激光
                    if (bossAttackTimer < 1.0) {
                        for (int i = 0; i < 12; i++) { // 增加到12条竖线
                            bossAttackZones.add(new Rectangle(50 + i * 75, 0, 10, 500));
                        }
                    } else {
                        for (Rectangle zone : bossAttackZones) {
                            // drawImage(laserImage, zone.x, zone.y, zone.width, zone.height);
                            if (zone.intersects(littlehero.getBounds())) {
                                littlehero.be_attacked = true;
                            }
                        }
                        bossAttacking = false;
                    }
                    break;
            }
        }
        if (bossHealth <= 1500 && bossHealth >= 1400) {
            dropCoins();
        }
    }

    void dropCoins() {
        for (int i = 0;i < NUM_COINS;i++) {
            int coinX = rand.nextInt(800);
            int coinY = 300;
            coinList.add(new Coin(coinX,coinY));
        }
    }

    void drawCoins() {
        for (Coin coin :coinList) {
            drawImage(coin.getImage(),coin.getCoinX(),coin.getCoinY(),30,30);
        }
    }

    public void drawMonster() {
        drawImage(boss.monsterImage, boss.monsterPositionX, boss.monsterPositionY);

        // 绘制BOSS血条
        drawRectangle(boss.monsterPositionX + 60, boss.monsterPositionY - 20, 100, 10, 1);
        double healthBarWidth = (bossHealth / maxBossHealth) * 100;
        drawSolidRectangle(boss.monsterPositionX + 60, boss.monsterPositionY - 20, healthBarWidth, 10);

        // 绘制攻击提示区域
        if (bossAttacking) {
            if (bossAttackMode == 0 || bossAttackMode == 2) {
                changeColor(yellow);
            }
            for (Rectangle zone : bossAttackZones) {
                drawSolidRectangle(zone.x, zone.y, zone.width, zone.height);
            }
        }
    }

    public void init() {
        score = 0;
        fireAudioPlayed = false;
        waveAudioPlayed = false;
        initAudio();
        gameover = false;
        gameWin = false;
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
        initWave();

        for (int i = 0; i < 13; i++) {
            motionless[i] = subImage(littlehero.heroImg, 32 * i, 0, 32, 32);
        }
        for (int j = 0; j < 8; j++) {
            littlehero.run[j] = subImage(littlehero.heroImg, 32 * j, 32, 32, 32);
        }
        // jump
        for (int k = 0; k < 6; k++) {
            littlehero.jump[k] = subImage(littlehero.heroImg, 32 * k, 32 * 5, 32, 32);
        }
        // attack1,2,3
        for (int l = 0; l < 10; l++) {
            littlehero.attack1[l] = subImage(littlehero.heroImg, 32 * l, 32 * 2, 32, 32);
            littlehero.attack2[l] = subImage(littlehero.heroImg, 32 * l, 32 * 3, 32, 32);
            littlehero.attack3[l] = subImage(littlehero.heroImg, 32 * l, 32 * 4, 32, 32);
        }
        for (int k = 0; k < 4; k++) {
            littlehero.Be_attacked[k] = subImage(littlehero.heroImg, 32 * k, 32 * 6, 32, 32);
        }
        for (int k = 0; k < 7; k++) {
            littlehero.die[k] = subImage(littlehero.heroImg, 32 * k, 32 * 7, 32, 32);
        }

        initMonster();
    }

    String direction;
    private double idleTime = 3.0; // Set the wait time to 5 seconds
    private double timeSinceStop = 0.0; // It is used to track the time elapsed after the stop
    int rest; // When the character is stationary for more than 5 seconds, the rest value is 1
    //
    public void updateFire(double dt) {
        fire.update((float) dt);
        fps = 1.0f / (float) dt;
    }

    public void drawFire() {
        if (fire_available) {
            saveCurrentTransform();
            //  rotate(90);
            if (littlehero.is_left) {
                fire.emitterangle = -90;
            } else if (littlehero.is_right) {
                fire.emitterangle = 90;
            }
            fire.move((float) littlehero.HeroPositionX + 50, (float) littlehero.HeroPositionY + 55);
            //  translate(littlehero.HeroPositionX,littlehero.HeroPositionY);
            fire.draw(this);
            restoreLastTransform();
        }
    }

    @Override
    public void update(double dt) {
        if (!gameover && !gameWin) {
            animTime += dt;
            updateFire(dt);
            updateWave(dt);

            if (littlehero.is_moving || littlehero.is_jump) {
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
            if (attackMode != -1) {
                currentFrame_attack = getFrame(1.3, 10);
                if (currentFrame_attack >= 9) {
                    attackMode = -1;
                }
            }

            if (littlehero.be_attacked) {
                currentFrame_Be_attacked = getFrame(0.75, 4);
                if (currentFrame_Be_attacked >= 3) {
                    littlehero.be_attacked = false;
                }
            }
            if (!littlehero.is_alive) {
                currentFrame_die = getFrame(1, 7);
                if (currentFrame_die >= 6) {
                    gameover = true;
                }
            }

            updateMonster(dt);
        }
        if (healthCount < 0) {
            healthCount = 0;
            littlehero.is_alive = false;
        }
        if (bossHealth <= 0) {
            gameWin = true;
        }

        collectCoins();
    }

    void collectCoins() {
        for (Coin coin :new ArrayList<>(coinList)) {
            if (distance(littlehero.HeroPositionX, littlehero.HeroPositionY, coin.coinX, coin.coinY) < 20) {
                // 角色和金币相交，获取分数并移除金币
                coinList.remove(coin);
                score++;
            }
        }
    }

    public int getFrame(double d, int num_frames) {
        return (int) Math.floor(((animTime % d) / d) * num_frames);
    }

    @Override
    public void paintComponent() {
        setWindowSize(1000, 500);
        if (!gameover && !gameWin) {
            drawImage(background, 0, 0, 1000, 500);
            changeColor(red);
            drawHealth();
            Running();
            StateMotionless();
            Jumping();
            Attack();
            Die();
            Be_attack();
            drawWave();
            drawFire();
            drawMonster();
        } else if (gameover) {
            changeColor(white);
            drawText(300, 220, "GAME OVER!", "Arial", 70);
            if(!ending) {
                createGame(new Ending());
                ending = true;
                mFrame.dispose();
            }
        } else if (gameWin) {
            changeColor(white);
            drawText(300, 220, "YOU WIN!", "Arial", 70);
            if(!ending) {
                createGame(new Ending());
                ending = true;
                mFrame.dispose();
            }
        }
        drawCoins();
        changeColor(white);
        drawText(10, 30, "Score: " + score, "Arial", 20);
    }

    // wave
    public void initWave() {
        wave = loadImage("src/wave.png");
        WaveVelocityX = 300;
        waveDirection = direction;
    }

    public void createWave() {
        wavePositionX = littlehero.HeroPositionX;
        wavePositionY = littlehero.HeroPositionY - 100;
        wave_available = true;
        waveDirection = direction;
    }

    public void updateWave(double dt) {
        if (waveDirection.equals("left")) {
            wavePositionX -= WaveVelocityX * dt;
        } else if (waveDirection.equals("right")) {
            wavePositionX += WaveVelocityX * dt;
        }
        if (wavePositionX <= -300 || wavePositionX >= 1000) {
            wave_available = false;
        }
    }

    public void drawWave() {
        if (wave_available) {
            saveCurrentTransform();
            translate(wavePositionX, wavePositionY);
            drawImage(wave, 0, 0, 650 * 0.5, 441 * 0.5);
            //   drawSolidCircle(wavePositionX,wavePositionY,5);

            restoreLastTransform();
        }
    }

    // health
    int healthCount;

    public void initHealth() {
        health = new Image[10];
        healthCount = 9;

        for (int i = 1; i < 11; i++) {
            String imageName = "Health/Health" + i + ".png";
            // 加载图片并存储到数组中
            Image image = loadImage(imageName);
            health[i - 1] = image;
        }
    }

    public void drawHealth() {
        drawImage(health[healthCount], littlehero.HeroPositionX - 25, littlehero.HeroPositionY - 10, 378 * 0.2, 38 * 0.2d);
    }

    public void Attack() {
        if (wave_available) {
            if (distance(wavePositionX, wavePositionY, boss.monsterPositionX, boss.monsterPositionY) < 100) {
                bossHealth -= 20;
            }
        } else if (fire_available) {
            if (distance(littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, boss.monsterPositionX, boss.monsterPositionY) < 150) {
                bossHealth -= 10;
            }
        }
        if (direction.equals("right")) {
            switch (attackMode) {
                case 1:
                    drawImage(littlehero.attack1[currentFrame_attack], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
                    if (boss.getBounds().intersects(littlehero.getAttackBounds())) {
                        bossHealth -= 10; // 减少BOSS血量
                    }
                    break;
                case 2:
                    drawImage(littlehero.attack2[currentFrame_attack], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
                    if (boss.getBounds().intersects(littlehero.getAttackBounds())) {
                        bossHealth -= 15; // 减少BOSS血量
                    }
                    break;
                case 3:
                    drawImage(littlehero.attack3[currentFrame_attack], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
                    if (boss.getBounds().intersects(littlehero.getAttackBounds())) {
                        bossHealth -= 20; // 减少BOSS血量
                    }
                    break;
            }
        } else {
            switch (attackMode) {
                case 1:
                    drawImage(littlehero.attack1[currentFrame_attack], littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, -32 * 3, 32 * 3);
                    if (boss.getBounds().intersects(littlehero.getAttackBounds())) {
                        bossHealth -= 10; // 减少BOSS血量
                    }
                    break;
                case 2:
                    drawImage(littlehero.attack2[currentFrame_attack], littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, -32 * 3, 32 * 3);
                    if (boss.getBounds().intersects(littlehero.getAttackBounds())) {
                        bossHealth -= 15; // 减少BOSS血量
                    }
                    break;
                case 3:
                    drawImage(littlehero.attack3[currentFrame_attack], littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, -32 * 3, 32 * 3);
                    if (boss.getBounds().intersects(littlehero.getAttackBounds())) {
                        bossHealth -= 20; // 减少BOSS血量
                    }
                    break;
            }
        }
    }

    public void Be_attack() {
        if (littlehero.be_attacked) {
            healthCount--;
            drawImage(littlehero.Be_attacked[currentFrame_Be_attacked], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
        }
    }

    public void Die() {
        if (!littlehero.is_alive) {
            drawImage(littlehero.die[currentFrame_die], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
        }
    }

    public void Jumping() {
        if (littlehero.is_jump) {
            if (direction.equals("right")) {
                drawImage(littlehero.jump[currentFrame_jump], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
            } else {
                drawImage(littlehero.jump[currentFrame_jump], littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, -32 * 3, 32 * 3);
            }
        }
    }

    public void Running() {
        if (littlehero.is_moving && attackMode == -1) {
            if (littlehero.is_left) {
                drawImage(littlehero.run[currentFrame_Run], littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, -32 * 3, 32 * 3);
            } else if (littlehero.is_right) {
                drawImage(littlehero.run[currentFrame_Run], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
            }
        }
    }

    public void StateMotionless() {
        if (!littlehero.is_moving && !littlehero.is_jump && attackMode == -1 && !littlehero.be_attacked && littlehero.is_alive) {
            if (direction.equals("right")) {
                drawImage(motionless[0], littlehero.HeroPositionX, littlehero.HeroPositionY, 32 * 3, 32 * 3);
            } else {
                drawImage(motionless[0], littlehero.HeroPositionX + 32 * 3, littlehero.HeroPositionY, -32 * 3, 32 * 3);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            littlehero.is_moving = true;
            littlehero.is_left = false;
            littlehero.is_right = true;
            direction = "right";
            rest = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            littlehero.is_left = true;
            littlehero.is_right = false;
            littlehero.is_moving = true;
            direction = "left";
            rest = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (littlehero.HeroPositionY == 300) {
                littlehero.HeroVelocityY = -500;
                littlehero.is_jump = true;
                littlehero.is_moving = false;
            }
            rest = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_J) {
            attackMode = 1;
            rest = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_K) {
            attackMode = 2;
            rest = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            attackMode = 3;
            rest = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_I && !fireAudioPlayed) {
            fire_available = true;
            playAudio(fireAudio);
            fireAudioPlayed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_O && !waveAudioPlayed) {
            if (!wave_available) {
                attackMode = 3;
                createWave();
                playAudio(waveAudio);
                waveAudioPlayed = true;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_A) {
            littlehero.is_moving = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_I) {
            fire_available = false;
            fireAudioPlayed = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_O) {
            waveAudioPlayed = false;
        }
    }

    // Hero class
    public class Hero {
        double HeroPositionX, HeroPositionY;
        double HeroVelocityX = 200;
        double HeroVelocityY;
        boolean is_moving, is_jump, is_left, is_right, be_attacked, is_alive = true;
        Image heroImg;
        Image[] run, jump, attack1, attack2, attack3, die, Be_attacked;

        public Rectangle getBounds() {
            return new Rectangle((int) HeroPositionX, (int) HeroPositionY, 32 * 3, 32 * 3);
        }

        public Rectangle getAttackBounds() {
            return new Rectangle((int) HeroPositionX, (int) HeroPositionY, 32 * 3, 32 * 3);
        }
    }

    // Monster class
    public class Monster {
        double monsterPositionX, monsterPositionY;
        boolean is_left;
        Image monsterImage;

        public Rectangle getBounds() {
            return new Rectangle((int) monsterPositionX, (int) monsterPositionY, 150, 150); // 增大怪物的体积
        }
    }

    class Coin {
        private int coinX;
        private int coinY;
        private Image coinImage;

        public Coin(int coinX,int coinY) {
            this.coinX = coinX;
            this.coinY = coinY;
            this.coinImage = loadImage("src/coin.png");
        }

        public int getCoinX() {
            return coinX;
        }

        public int getCoinY() {
            return coinY;
        }

        public Image getImage() {
            return coinImage;
        }
    }
}
