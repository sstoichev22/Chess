import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        Input input = new Input();
        GamePanel gamePanel = new GamePanel(input, 1000, 800, 800, 800);
        input.setGamePanel(gamePanel);
        frame.add(gamePanel);
        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gamePanel.startGameThread();
    }
}
