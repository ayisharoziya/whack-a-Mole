
import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class Last72Hours extends JFrame {

    private int hours = 0;
    private int health = 100;
    private int hunger = 20;
    private int thirst = 20;
    private int energy = 80;
    private int morale = 75;

    private int food = 8;
    private int water = 10;
    private int medicine = 3;
    private int power = 80;

    private boolean gameOver = false;
    private final Random random = new Random();

    private JLabel clockLabel, statusLabel, messageLabel;
    private JLabel foodLabel, waterLabel, medicineLabel;

    private JProgressBar healthBar, hungerBar, thirstBar;
    private JProgressBar energyBar, moraleBar, powerBar;

    private JPanel mainPanel;

    public Last72Hours() {
        setTitle("LAST 72 HOURS | Survival Protocol");
        setSize(960, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel(new BorderLayout(14, 14));
        mainPanel.setBackground(new Color(15, 20, 28));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        createHeader();
        createDashboard();
        createActions();

        setContentPane(mainPanel);
        refreshUI();
        setVisible(true);
    }

    private JLabel label(String text, int size, Color color) {
        JLabel result = new JLabel(text);
        result.setFont(new Font("SansSerif", Font.BOLD, size));
        result.setForeground(color);
        return result;
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 20, 28));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 5));
        titles.setBackground(new Color(15, 20, 28));

        titles.add(label(
            "LAST 72 HOURS", 28,
            new Color(225, 235, 245)
        ));

        titles.add(label(
            "BUNKER SURVIVAL PROTOCOL", 12,
            new Color(95, 185, 170)
        ));

        clockLabel = label(
            "TIME: 00 / 72 HOURS", 16,
            new Color(245, 190, 90)
        );

        header.add(titles, BorderLayout.WEST);
        header.add(clockLabel, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);
    }

    private JPanel createCard(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(new Color(27, 35, 46));

        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(55, 70, 85)
                ),
                BorderFactory.createEmptyBorder(
                    12, 14, 12, 14
                )
            )
        );

        panel.add(label(
            title, 15, new Color(225, 235, 245)
        ));

        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JProgressBar createBar(
        JPanel panel,
        String title,
        int value,
        Color color
    ) {
        panel.add(label(
            title, 12, new Color(170, 185, 200)
        ));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(value + "%");
        bar.setForeground(color);
        bar.setBackground(new Color(45, 55, 68));
        bar.setBorderPainted(false);

        bar.setPreferredSize(new Dimension(300, 20));
        bar.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 20)
        );

        panel.add(bar);
        panel.add(Box.createVerticalStrut(10));

        return bar;
    }

    private void createDashboard() {
        JPanel dashboard = new JPanel(
            new GridLayout(2, 2, 12, 12)
        );

        dashboard.setBackground(new Color(15, 20, 28));

        JPanel survivor = createCard("SURVIVOR CONDITION");

        healthBar = createBar(
            survivor, "Health", health,
            new Color(220, 75, 75)
        );

        hungerBar = createBar(
            survivor, "Hunger (higher is worse)", hunger,
            new Color(230, 165, 65)
        );

        thirstBar = createBar(
            survivor, "Thirst (higher is worse)", thirst,
            new Color(65, 165, 230)
        );

        energyBar = createBar(
            survivor, "Energy", energy,
            new Color(125, 190, 115)
        );

        moraleBar = createBar(
            survivor, "Morale", morale,
            new Color(180, 130, 225)
        );

        JPanel supplies = createCard("BUNKER INVENTORY");

        foodLabel = label("", 14, Color.WHITE);
        waterLabel = label("", 14, Color.WHITE);
        medicineLabel = label("", 14, Color.WHITE);

        powerBar = createBar(
            supplies, "Battery power", power,
            new Color(95, 185, 170)
        );

        supplies.add(foodLabel);
        supplies.add(Box.createVerticalStrut(12));

        supplies.add(waterLabel);
        supplies.add(Box.createVerticalStrut(12));

        supplies.add(medicineLabel);
        supplies.add(Box.createVerticalStrut(12));

        supplies.add(label(
            "Use MEDICAL KIT to restore health.",
            11, Color.LIGHT_GRAY
        ));

        JPanel mission = createCard("MISSION OBJECTIVE");

        mission.add(label(
            "SURVIVE UNTIL RESCUE", 15,
            new Color(245, 190, 90)
        ));

        mission.add(Box.createVerticalStrut(12));

        mission.add(label(
            "Manage resources carefully.",
            12, Color.LIGHT_GRAY
        ));

        mission.add(Box.createVerticalStrut(8));

        mission.add(label(
            "Every action advances time by 3 hours.",
            12, Color.LIGHT_GRAY
        ));

        mission.add(Box.createVerticalStrut(8));

        mission.add(label(
            "Reach hour 72 alive.",
            12, Color.LIGHT_GRAY
        ));

        mission.add(Box.createVerticalStrut(8));

        mission.add(label(
            "Scavenging can find supplies or cause injury.",
            11, Color.LIGHT_GRAY
        ));

        JPanel intel = createCard("BUNKER RADIO");

        statusLabel = label(
            "SIGNAL: UNSTABLE", 14,
            new Color(95, 185, 170)
        );

        messageLabel = label(
            "The bunker is sealed. Await instructions.",
            12, Color.LIGHT_GRAY
        );

        intel.add(statusLabel);
        intel.add(Box.createVerticalStrut(15));
        intel.add(messageLabel);

        dashboard.add(survivor);
        dashboard.add(supplies);
        dashboard.add(mission);
        dashboard.add(intel);

        mainPanel.add(dashboard, BorderLayout.CENTER);
    }

    private JButton createButton(
        String text,
        Runnable action
    ) {
        JButton button = new JButton(text);

        button.setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );

        button.setForeground(Color.WHITE);
        button.setBackground(new Color(43, 59, 73));
        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(75, 95, 110)
                ),
                BorderFactory.createEmptyBorder(
                    11, 8, 11, 8
                )
            )
        );

        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        button.addActionListener(e -> {
            if (!gameOver) {
                action.run();
            }
        });

        return button;
    }

    private void createActions() {
        JPanel actions = new JPanel(
            new GridLayout(2, 4, 9, 9)
        );

        actions.setBackground(new Color(15, 20, 28));

        actions.setBorder(
            BorderFactory.createEmptyBorder(4, 0, 0, 0)
        );

        actions.add(createButton(
            "EAT FOOD", this::eat
        ));

        actions.add(createButton(
            "DRINK WATER", this::drink
        ));

        actions.add(createButton(
            "REST", this::rest
        ));

        actions.add(createButton(
            "SCAVENGE", this::scavenge
        ));

        actions.add(createButton(
            "REPAIR POWER", this::repair
        ));

        actions.add(createButton(
            "USE MEDICAL KIT", this::useMedicine
        ));

        actions.add(createButton(
            "SEND RADIO SIGNAL", this::radio
        ));

        actions.add(createButton(
            "HOW TO PLAY", this::showInstructions
        ));

        mainPanel.add(actions, BorderLayout.SOUTH);
    }

    private void advanceTime() {
        hours += 3;

        hunger = Math.min(100, hunger + 12);
        thirst = Math.min(100, thirst + 16);
        energy = Math.max(0, energy - 8);
        power = Math.max(0, power - 5);

        if (hunger >= 80) {
            health -= 8;
        }

        if (thirst >= 80) {
            health -= 12;
        }

        if (energy == 0) {
            health -= 5;
        }

        health = Math.max(0, health);

        if (health <= 0) {
            finish(false, "Your health reached zero.");
            return;
        }

        if (hours >= 72) {
            hours = 72;
            finish(
                true,
                "The rescue team has reached your bunker."
            );
            return;
        }

        if (random.nextInt(100) < 30) {
            randomEvent();

            if (gameOver) {
                return;
            }
        }

        refreshUI();
    }

    private void eat() {
        if (food <= 0) {
            setMessage(
                "No food remains. Try scavenging."
            );
            return;
        }

        food--;
        hunger = Math.max(0, hunger - 35);
        energy = Math.min(100, energy + 8);

        setMessage(
            "You ate a ration. Hunger reduced."
        );

        advanceTime();
    }

    private void drink() {
        if (water <= 0) {
            setMessage(
                "No clean water remains. Try scavenging."
            );
            return;
        }

        water--;
        thirst = Math.max(0, thirst - 40);

        setMessage("You drank clean water.");

        advanceTime();
    }

    private void rest() {
        energy = Math.min(100, energy + 35);
        morale = Math.min(100, morale + 5);

        setMessage("You rested in the bunker.");

        advanceTime();
    }

    private void scavenge() {
        if (power < 10) {
            setMessage(
                "Insufficient power for a safe expedition."
            );
            return;
        }

        power -= 10;

        int result = random.nextInt(5);

        switch (result) {
            case 0:
                food += 2;
                setMessage(
                    "Scavenge: found 2 food rations."
                );
                break;

            case 1:
                water += 2;
                setMessage(
                    "Scavenge: found 2 water units."
                );
                break;

            case 2:
                medicine++;
                setMessage(
                    "Scavenge: found a medical kit."
                );
                break;

            case 3:
                health = Math.max(0, health - 12);
                setMessage(
                    "An accident injured you!"
                );
                break;

            default:
                morale = Math.min(100, morale + 8);
                setMessage(
                    "You found a safe room. Morale improved."
                );
                break;
        }

        if (health <= 0) {
            finish(
                false,
                "You were fatally injured while scavenging."
            );
            return;
        }

        advanceTime();
    }

    private void repair() {
        if (power >= 100) {
            setMessage(
                "Battery is already fully charged."
            );
            return;
        }

        power = Math.min(100, power + 25);
        energy = Math.max(0, energy - 8);

        setMessage("You restored the power system.");

        advanceTime();
    }

    private void useMedicine() {
        if (medicine <= 0) {
            setMessage("No medical kits remain.");
            return;
        }

        if (health >= 100) {
            setMessage(
                "You do not need medical treatment right now."
            );
            return;
        }

        medicine--;
        health = Math.min(100, health + 30);

        setMessage(
            "Medical kit used. Health restored."
        );

        advanceTime();
    }

    private void radio() {
        if (power < 10) {
            setMessage(
                "Not enough power to operate the radio."
            );
            return;
        }

        power -= 10;

        if (random.nextInt(100) < 45) {
            morale = Math.min(100, morale + 15);

            statusLabel.setText(
                "SIGNAL: RESPONSE RECEIVED"
            );

            setMessage(
                "A rescue signal answers from outside."
            );
        } else {
            statusLabel.setText(
                "SIGNAL: NO RESPONSE"
            );

            morale = Math.max(0, morale - 5);

            setMessage(
                "Only static. You must keep trying."
            );
        }

        advanceTime();
    }

    private void randomEvent() {
        int event = random.nextInt(5);

        switch (event) {
            case 0:
                power = Math.max(0, power - 10);
                setMessage(
                    "EVENT: Power fluctuation detected!"
                );
                break;

            case 1:
                morale = Math.max(0, morale - 10);
                setMessage(
                    "EVENT: A distant impact shakes the bunker."
                );
                break;

            case 2:
                health = Math.max(0, health - 8);
                setMessage(
                    "EVENT: You suffered a minor injury."
                );
                break;

            case 3:
                food++;
                setMessage(
                    "EVENT: You discovered a hidden food ration."
                );
                break;

            default:
                water++;
                setMessage(
                    "EVENT: A sealed bottle of water was found."
                );
                break;
        }

        if (health <= 0) {
            finish(
                false,
                "You did not survive the incident."
            );
        }
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
        refreshUI();
    }

    private void showInstructions() {
        JOptionPane.showMessageDialog(
            this,
            "OBJECTIVE: Survive for 72 hours until rescue arrives.\n\n" +
            "• Eat food to reduce hunger.\n" +
            "• Drink water to reduce thirst.\n" +
            "• Rest to restore energy.\n" +
            "• Scavenge to find supplies, but risk injury.\n" +
            "• Repair power to restore battery capacity.\n" +
            "• Use a medical kit to restore health.\n" +
            "• Send radio signals to attempt contact.\n\n" +
            "Each action advances time by 3 hours.\n" +
            "Hunger, thirst, and low energy can reduce health.",
            "HOW TO PLAY",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void refreshUI() {
        if (clockLabel != null) {
            clockLabel.setText(
                String.format(
                    "TIME: %02d / 72 HOURS", hours
                )
            );
        }

        setBar(healthBar, health);
        setBar(hungerBar, hunger);
        setBar(thirstBar, thirst);
        setBar(energyBar, energy);
        setBar(moraleBar, morale);
        setBar(powerBar, power);

        if (foodLabel != null) {
            foodLabel.setText("Food rations: " + food);
        }

        if (waterLabel != null) {
            waterLabel.setText("Water units: " + water);
        }

        if (medicineLabel != null) {
            medicineLabel.setText(
                "Medicine kits: " + medicine
            );
        }

        if (mainPanel != null) {
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private void setBar(
        JProgressBar bar,
        int value
    ) {
        if (bar == null) {
            return;
        }

        int safeValue = Math.max(
            0, Math.min(100, value)
        );

        bar.setValue(safeValue);
        bar.setString(safeValue + "%");
    }

    private void finish(
        boolean won,
        String reason
    ) {
        if (gameOver) {
            return;
        }

        gameOver = true;
        refreshUI();

        String title = won
            ? "RESCUE SUCCESSFUL"
            : "MISSION FAILED";

        String message = reason
            + "\n\nSurvival time: " + hours
            + " hours\nHealth remaining: " + health
            + "%\n\nStart a new mission?";

        int choice = JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            won
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();

            SwingUtilities.invokeLater(
                Last72Hours::new
            );
        } else {
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            Last72Hours::new
        );
    }
}
