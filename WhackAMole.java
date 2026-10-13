import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Whack-a-Mole Mini-Project Game
 * -------------------------------
 * Single-file Java Swing application.
 *
 * How to run:
 *   javac WhackAMole.java
 *   java WhackAMole
 */
public class WhackAMole extends JFrame implements ActionListener {

    // --- UI Components ---
    private JButton[] holes = new JButton[9]; // 3x3 grid of mole holes
    private JLabel scoreLabel;                // NORTH status: Score
    private JLabel timeLabel;                 // NORTH status: Time Left
    private JButton playAgainButton;          // SOUTH reset control

    // --- Dual-Timer Engine ---
    private Timer countdownTimer; // Session Lifecycle Timer: ticks every 1000ms
    private Timer moleTimer;      // Mole-Movement Timer: ticks every 800ms initially

    // --- Game State ---
    private Random random = new Random();
    private int score = 0;
    private int timeLeft = 30;          // 30-second session
    private int currentMoleIndex = -1;  // -1 = no mole on board
    private int moleDelay = 800;        // initial mole speed (ms)

    // --- Difficulty Progression State ---
    private static final int SPEED_UP_EVERY_POINTS = 50;
    private static final int SPEED_UP_STEP_MS = 100;
    private static final int MIN_MOLE_DELAY_MS = 300;
    private int nextSpeedUpAt = 50; // first threshold

    /**
     * Constructor: builds window architecture and boots the game.
     */
    public WhackAMole() {
        // ----- JFrame setup -----
        super("Whack-a-Mole");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout());

        // ----- NORTH: Top status panel (1x2 GridLayout) -----
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        timeLabel = new JLabel("Time Left: 30s", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(scoreLabel);
        topPanel.add(timeLabel);
        add(topPanel, BorderLayout.NORTH);

        // ----- CENTER: 3x3 grid of holes -----
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < 9; i++) {
            holes[i] = new JButton("");
            holes[i].setBackground(Color.LIGHT_GRAY); // default empty-hole color
            holes[i].setOpaque(true);
            holes[i].setBorderPainted(false); // remove focus/border lines (clean canvas)
            holes[i].setFocusPainted(false);
            holes[i].setFocusable(false);
            holes[i].setFont(new Font("Arial", Font.BOLD, 20));
            holes[i].addActionListener(this); // route clicks to actionPerformed
            gridPanel.add(holes[i]);
        }
        add(gridPanel, BorderLayout.CENTER);

        // ----- SOUTH: Play Again control in FlowLayout panel -----
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playAgainButton = new JButton("Play Again");
        playAgainButton.setFocusable(false);
        playAgainButton.addActionListener(this);
        playAgainButton.setEnabled(false); // disabled while game is active
        bottomPanel.add(playAgainButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ----- Dual-Timer Engine Setup -----
        setupTimers();
    }

    /**
     * Initializes and starts both timers simultaneously (boot cycle).
     * Called once at launch; restart logic reuses these Timer objects.
     */
    private void setupTimers() {
        // Session Countdown Timer: fires every 1000ms on the EDT
        countdownTimer = new Timer(1000, this);

        // Mole-Movement Timer: fires every 800ms initially on the EDT
        moleTimer = new Timer(moleDelay, this);

        // Spin up both loops together
        countdownTimer.start();
        moleTimer.start();

        // Place the first mole immediately so board is not empty
        moveMole();
    }

    /**
     * Mole displacement routine.
     * Clears the current mole, picks a new random index 0-8,
     * sets its text to "(O_O)" and background to Green.
     */
    private void moveMole() {
        // 1. Clear previous mole (if any)
        if (currentMoleIndex >= 0 && currentMoleIndex < 9) {
            holes[currentMoleIndex].setText("");
            holes[currentMoleIndex].setBackground(Color.LIGHT_GRAY);
        }

        // 2. Pick new random button index 0 to 8
        currentMoleIndex = random.nextInt(9);

        // 3. Activate new mole
        holes[currentMoleIndex].setText("(O_O)");
        holes[currentMoleIndex].setBackground(Color.GREEN);
    }

    /**
     * Central event dispatch: handles both Timer ticks and JButton clicks.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // --- Case 1: Session Countdown Timer tick (every 1 second) ---
        if (src == countdownTimer) {
            timeLeft--;
            timeLabel.setText("Time Left: " + timeLeft + "s");

            if (timeLeft <= 0) {
                gameOver(); // trigger terminal sequence
            }
            return;
        }

        // --- Case 2: Mole-Movement Timer tick (every moleDelay ms) ---
        if (src == moleTimer) {
            moveMole();
            return;
        }

        // --- Case 3: Play Again button ---
        if (src == playAgainButton) {
            resetGame();
            return;
        }

        // --- Case 4: Grid hole clicks (indexes 0-8) ---
        for (int i = 0; i < 9; i++) {
            if (src == holes[i]) {
                // Guard: ignore clicks when board is disabled (game over)
                if (!holes[i].isEnabled()) {
                    return;
                }

                // HIT: user clicked the button displaying "(O_O)"
                if (i == currentMoleIndex && holes[i].getText().equals("(O_O)")) {
                    score += 10; // reward
                    scoreLabel.setText("Score: " + score);

                    // Advanced Difficulty Progression:
                    // Every +50 points, speed up mole timer by 100ms, floor 300ms.
                    if (score >= nextSpeedUpAt) {
                        nextSpeedUpAt += SPEED_UP_EVERY_POINTS;
                        moleDelay = Math.max(MIN_MOLE_DELAY_MS, moleDelay - SPEED_UP_STEP_MS);
                        moleTimer.setDelay(moleDelay);
                        moleTimer.setInitialDelay(moleDelay);
                    }

                    // Immediately force mole to jump (reset interval)
                    moveMole();
                    moleTimer.restart(); // restart countdown so full interval applies
                } else {
                    // MISS: empty hole penalty, never below 0
                    score -= 5;
                    if (score < 0) {
                        score = 0;
                    }
                    scoreLabel.setText("Score: " + score);
                }
                break;
            }
        }
    }

    /**
     * Game Over State:
     * Stops both timers, clears grid, restores gray canvas,
     * disables grid buttons, shows summary dialog, enables Play Again.
     */
    private void gameOver() {
        // Stop background loops
        countdownTimer.stop();
        moleTimer.stop();

        // Clear all grid buttons and freeze component state
        for (int i = 0; i < 9; i++) {
            holes[i].setText("");
            holes[i].setBackground(Color.LIGHT_GRAY);
            holes[i].setEnabled(false); // no more clicks register
        }
        currentMoleIndex = -1;
        timeLabel.setText("Time Left: 0s");

        // Enable reset control
        playAgainButton.setEnabled(true);

        // Summary dialog box
        JOptionPane.showMessageDialog(this,
                "Game Over!\nFinal Score: " + score,
                "Whack-a-Mole - Session Terminated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Reset Logic for "Play Again":
     * Resets score, time, speed; re-enables grid; restarts timers.
     */
    private void resetGame() {
        score = 0;
        timeLeft = 30;
        moleDelay = 800;
        nextSpeedUpAt = SPEED_UP_EVERY_POINTS;
        currentMoleIndex = -1;

        scoreLabel.setText("Score: 0");
        timeLabel.setText("Time Left: 30s");

        // Re-enable grid buttons and restore gray canvas
        for (int i = 0; i < 9; i++) {
            holes[i].setText("");
            holes[i].setBackground(Color.LIGHT_GRAY);
            holes[i].setEnabled(true);
        }

        // Restore mole speed and restart both timers
        moleTimer.setDelay(moleDelay);
        moleTimer.setInitialDelay(moleDelay);
        playAgainButton.setEnabled(false); // disable while game is active

        countdownTimer.start();
        moleTimer.start();
        moveMole(); // immediate first mole
    }

    /**
     * Entry point. Launches UI on the Event Dispatch Thread.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WhackAMole game = new WhackAMole();
            game.setVisible(true);
        });
    }
}
