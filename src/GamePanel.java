import java.awt.Point;
import java.util.Stack;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;

    final Stack<Point> snakeBody = new Stack<>();
    final LinkedList<Level> levels = new LinkedList<>();
    int applesEaten = 0;
    int appleX;
    int appleY;
    int buffX;
    int buffY;
    boolean hasBuff = false;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    Level currentLevel;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        setupLevels();
        startGame();
    }

    private void setupLevels() {
        // Level 1 - No barriers
        levels.add(new Level(1));

        // Level 2 - Barriers
        Level level2 = new Level(2);
        level2.addBarrier(new Point(300, 300));
        level2.addBarrier(new Point(300, 325));
        level2.addBarrier(new Point(300, 350));
        levels.add(level2);

        // Set the current level to the first level
        currentLevel = levels.removeFirst();
    }

    public void startGame() {
        newApple();
        running = true;
        snakeBody.clear();
        for (int i = 0; i < 6; i++) {
            snakeBody.push(new Point(UNIT_SIZE * i, 0));
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            draw(g);
        } else {
            if (currentLevel.levelNumber == 2 && applesEaten == 20) {
                youWin(g);
            } else {
                gameOver(g);
            }
        }
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (Point part : snakeBody) {
                g.setColor(snakeBody.indexOf(part) == 0 ? Color.GREEN : new Color(random.nextInt(1, 255), random.nextInt(1, 255), random.nextInt(1,255)));
                g.fillRect(part.x, part.y, UNIT_SIZE, UNIT_SIZE);
            }

            if (hasBuff) {
                g.setColor(Color.BLUE);
                g.fillOval(buffX, buffY, UNIT_SIZE, UNIT_SIZE);
            }

            // Draw barriers
            for (Point barrier : currentLevel.barriers) {
                g.setColor(Color.YELLOW);
                g.fillRect(barrier.x, barrier.y, UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Times New Roman", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
            g.drawString("Level: " + currentLevel.levelNumber, (SCREEN_WIDTH - metrics.stringWidth("Level: " + currentLevel.levelNumber)) / 2, g.getFont().getSize() + 40);
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        boolean validPosition;
        do {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPosition = true;
            for (Point part : snakeBody) {
                if (part.equals(new Point(appleX, appleY))) {
                    validPosition = false;
                    break;
                }
            }
            for (Point barrier : currentLevel.barriers) {
                if (barrier.equals(new Point(appleX, appleY))) {
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition);
    }

    public void newBuff() {
        boolean validPosition;
        do {
            buffX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            buffY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPosition = true;
            for (Point part : snakeBody) {
                if (part.equals(new Point(buffX, buffY)) || (buffX == appleX && buffY == appleY)) {
                    validPosition = false;
                    break;
                }
            }
            for (Point barrier : currentLevel.barriers) {
                if (barrier.equals(new Point(buffX, buffY))) {
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition);
        hasBuff = true;
    }

    public void checkBuff() {
        if (snakeBody.peek().equals(new Point(buffX, buffY))) {
            shortenSnake();
            hasBuff = false;
        }
    }

    public void shortenSnake() {
        int shortenLength = 3; // Length to shorten the snake by
        for (int i = 0; i < shortenLength && !snakeBody.isEmpty(); i++) {
            snakeBody.removeFirst(); // Remove tail elements
        }
    }

    public void move() {
        Point newHead = new Point(snakeBody.peek());
        switch (direction) {
            case 'U': newHead.y -= UNIT_SIZE; break;
            case 'D': newHead.y += UNIT_SIZE; break;
            case 'L': newHead.x -= UNIT_SIZE; break;
            case 'R': newHead.x += UNIT_SIZE; break;
        }
        snakeBody.push(newHead);
        if (!checkApple()) {
            snakeBody.removeFirst(); // Remove tail
        }
    }

    public boolean checkApple() {
        if (snakeBody.peek().equals(new Point(appleX, appleY))) {
            applesEaten++;
            newApple();
            if((random.nextInt(100) <= 5) && !hasBuff) {  //There is 5% to spawn the buff
                newBuff();
            }
            if (applesEaten % 3 == 0 && currentLevel.levelNumber == 1) {
                nextLevel();
            }

            return true;
        }
        return false;
    }

    public void nextLevel() {
        if (!levels.isEmpty()) {
            currentLevel = levels.remove(); // Remove and get the next level
            setupLevel(); // Set up the new level
        } else {
            // Handle game completion scenario if there are no more levels
            running = false;
        }
    }

    private void setupLevel() {
        snakeBody.clear();
        for (int i = 0; i < 6; i++) {
            snakeBody.push(new Point(UNIT_SIZE * i, 0));
        }
        direction = 'R';
        newApple();
    }

    public void checkCollisions() {
        Point head = snakeBody.peek();
        for (int i = 0; i < snakeBody.size() - 1; i++) {
            if (head.equals(snakeBody.get(i))) {
                running = false;
            }
        }

        if (head.x < 0 || head.x >= SCREEN_WIDTH || head.y < 0 || head.y >= SCREEN_HEIGHT) {
            running = false;
        }

        for (Point barrier : currentLevel.barriers) {
            if (head.equals(barrier)) {
                running = false;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        hasBuff = false;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        g.setColor(Color.RED);
        g.setFont(new Font("Times New Roman", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    public void youWin(Graphics g) {
        hasBuff = false;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        g.setColor(Color.GREEN);
        g.setFont(new Font("Times New Roman", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("You win!", (SCREEN_WIDTH - metrics2.stringWidth("You win!")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
            checkBuff();

            // Check if score is 15 and current level is 2 to trigger 'youWin'
            if (currentLevel.levelNumber == 2 && applesEaten == 20) {
                running = false;
                timer.stop();
            }
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    public class Level {
        int levelNumber;
        List<Point> barriers;

        Level(int levelNumber) {
            this.levelNumber = levelNumber;
            this.barriers = new LinkedList<>();
        }

        void addBarrier(Point point) {
            barriers.add(point);
        }
    }
}
