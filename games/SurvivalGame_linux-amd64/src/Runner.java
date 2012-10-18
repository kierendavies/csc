import GameEngine.GameCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Runner {
    static int gameWidth = 1280;
    static int gameHeight = 1024;

    static JTextField widthField;
    static JTextField heightField;

    static JFrame launcher;
    static JFrame game;

    public static void main(String[] args) {
        launcher = new JFrame("Zarmina");
        launcher.setResizable(true);
        launcher.setSize(200, 240);
        launcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcher.setLayout(new BoxLayout(launcher.getContentPane(), BoxLayout.PAGE_AXIS));

        JLabel banner = new JLabel("Zarmina");
        banner.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
        banner.setAlignmentX(Component.CENTER_ALIGNMENT);
        launcher.add(banner);
        launcher.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton newGameButton = new JButton("New game");
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameWidth = Integer.parseInt(widthField.getText());
                gameHeight = Integer.parseInt(heightField.getText());
                Runner.newGame();
            }
        });
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        launcher.add(newGameButton);

        JLabel widthLabel = new JLabel("Game width:");
        widthLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        launcher.add(widthLabel);
        widthField = new JTextField(4);
        widthField.setText(""+gameWidth);
        widthField.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        launcher.add(widthField);

        JLabel heightLabel = new JLabel("Game height:");
        heightLabel.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        launcher.add(heightLabel);
        heightField = new JTextField(4);
        heightField.setText(""+gameHeight);
        heightField.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        launcher.add(heightField);

        launcher.add(Box.createRigidArea(new Dimension(0, 5)));

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        launcher.add(exitButton);

        launcher.setVisible(true);
    }

    private static void newGame() {
        game = new JFrame("Zarmina");
        SurvivalGame ag = new SurvivalGame(100);
        ag.linkToFrame(game);

        GameCanvas glc = new GameCanvas(ag);
        game.add(glc);

        game.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        game.setResizable(false);
        game.setSize(gameWidth, gameHeight);
        game.setVisible(true);

        // need this so that you don't have to click on the window to gain focus ;)
        glc.requestFocusInWindow();
    }

    public static void endGame() {
        game.dispose();
    }
}
