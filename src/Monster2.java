import java.awt.*;

public class Monster2 {
  static Image MonsterImage;
     double monsterPositionX;
     double monsterPositionY;
     double monsterSize;

    public Monster2() {

    }

    public Monster2(double monsterPositionX, double monsterPositionY,double monsterSize) {
        this.monsterPositionX = monsterPositionX;
        this.monsterPositionY = monsterPositionY;
        this.monsterSize = monsterSize;
    }

    public static Image getMonster() {
        return MonsterImage;
    }

    public static void setMonster(Image monster) {
        MonsterImage = monster;
    }

    public double getMonsterPositionX() {
        return monsterPositionX;
    }

    public void setMonsterPositionX(double monsterPositionX) {
        this.monsterPositionX = monsterPositionX;
    }

    public double getMonsterPositionY() {
        return monsterPositionY;
    }

    public void setMonsterPositionY(double monsterPositionY) {
        this.monsterPositionY = monsterPositionY;
    }
}
