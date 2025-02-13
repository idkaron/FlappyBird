import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class FlappyBird implements ActionListener, KeyListener {
    private JFrame frame;
    private JPanel panel;
    private Timer gameTimer;
    private Timer startTimer;
    private int birdY;
    private int birdVelocity;
    private int gravity = 2;
    private ArrayList<Rectangle> pipes;
    private int pipeSpeed = 5;
    private int score;
    private boolean gameOver;
    private boolean gameStarted;
    private int countdown = 3;

    public FlappyBird() {
        frame = new JFrame("Flappy Bird");
        panel = new GamePanel();
        gameTimer = new Timer(20, this);
        startTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdown == 0) {
                    gameStarted = true;
                    startTimer.stop();
                    gameTimer.start();
                } else {
                    countdown--;
                    panel.repaint();
                }
            }
        });

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.addKeyListener(this);
        frame.setVisible(true);

        resetGame();
        startTimer.start();
    }

    public static void main(String[] args) {
        new FlappyBird();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && gameStarted) {
            birdY += birdVelocity;
            birdVelocity += gravity;

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= pipeSpeed;

                // Check if bird has successfully passed through the pipes
                if (pipe.x + pipe.width == 100) {
                    score++;
                }

                if (pipe.x + pipe.width < 0) {
                    pipes.remove(pipe);
                    if (i % 2 == 0) { // Only add new pipes when the upper pipe is removed
                        addPipe();
                    }
                }
            }

            if (birdY > 600 || birdY < 0) {
                gameOver = true;
            }

            checkCollision();
            panel.repaint();
        }
    }

    private void checkCollision() {
        for (Rectangle pipe : pipes) {
            if (pipe.intersects(new Rectangle(100, birdY, 50, 50))) {
                gameOver = true;
                gameTimer.stop();
            }
        }
    }

    private void addPipe() {
        int pipeHeight = (int) (Math.random() * 200) + 150; // Adjusted for wider gap
        int gap = 200; // Gap wide enough for the bird to pass through

        pipes.add(new Rectangle(800, 0, 100, pipeHeight));
        pipes.add(new Rectangle(800, pipeHeight + gap, 100, 600 - pipeHeight - gap));
    }

    private void resetGame() {
        birdY = 250;
        birdVelocity = 0;
        pipes = new ArrayList<>();
        score = 0;
        gameOver = false;
        gameStarted = false;
        countdown = 3;
        addPipe();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();
                startTimer.start();
            } else if (gameStarted) {
                birdVelocity = -20;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Background
            g.setColor(Color.cyan);
            g.fillRect(0, 0, 800, 600);

            // Bird
            g.setColor(Color.yellow);
            g.fillRect(100, birdY, 50, 50);

            // Pipes
            g.setColor(Color.green);
            for (Rectangle pipe : pipes) {
                g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
            }

            // Score
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Score: " + score, 600, 50);

            // Countdown
            if (!gameStarted && countdown > 0) {
                g.drawString("Starting in: " + countdown, 300, 300);
            }

            // Game Over
            if (gameOver) {
                g.drawString("Game Over! Press Space to Restart", 200, 300);
            }
        }
    }
}
