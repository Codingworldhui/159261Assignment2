import java.awt.event.KeyEvent;

public class Ending extends GameEngine{
//    public static void main(String[] args) {
//        createGame(new Ending());
//    }
    boolean isSpace;
    boolean isFirst;
    @Override
    public void init() {
        isSpace = false;
        isFirst = false;
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        setWindowSize(1000,500);
        changeBackgroundColor(black);
        clearBackground(width(),height());
        changeColor(white);
        drawText(5,230,"The world of Jianghu is not just about fighting and killing.");
        drawText(250,280,"Enter ‘space’ to try again.");
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            if(!isFirst) {
                createGame(new Example());
                isFirst = true;
                isSpace = true;
                mFrame.dispose();
            }
        }
    }


    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            isFirst = false;
            isSpace = false;
        }
    }
}
