import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FirstPage extends JFrame {
    private Image backgroundImage;

    public FirstPage() {
        setTitle("Game");

        // 设置窗口大小
        setSize(1000, 500);

        // 设置关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(200,200);

        // 加载背景图像
        backgroundImage = new ImageIcon("src/initbackground.jpg").getImage();

        // 创建自定义面板
        BackgroundPanel panel = new BackgroundPanel();
        setContentPane(panel);

        panel.setLayout(null);

        // 加载按钮背景图像
        ImageIcon buttonIcon = new ImageIcon("src/button.jpg");

        // 创建按钮并设置图标
        JButton button = new JButton(buttonIcon);
        button.setSize(buttonIcon.getIconWidth(), buttonIcon.getIconHeight()); // 设置按钮大小
        button.setLocation((1000 - button.getWidth()) / 2, (500 - button.getHeight()) / 2); // 将按钮居中

        // 设置按钮透明
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        panel.add(button);

        // 添加按钮点击事件监听器
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建并启动 Example 游戏
                Example0 example0Game = new Example0();
                example0Game.startGame();

                // 关闭当前窗口
                dispose();
            }
        });
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // 绘制背景图像
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        // 创建并显示窗口
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FirstPage().setVisible(true);
            }
        });
    }
}