import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FitnessTrackerApp extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton logoutButton;
    private JButton signupButton;
    private JLabel statusLabel;
    private JPanel exercisePanel;
    private JButton trackExerciseButton;
    private JLabel gymExercisesLabel;
    private JLabel yogaExercisesLabel;
    private JLabel pointsLabel;
    private JPanel graphPanel;
    private JTable exerciseTable;
    private DefaultTableModel tableModel;
    private JList<String> rewardList; // Added reward list
    private DefaultListModel<String> rewardListModel; // Model for the reward list
    private Map<String, String> credentials; // To store username-password pairs
    private Map<String, Integer> exerciseMap; // To store exercise counts for each user
    private Map<String, Integer> exerciseGoals; // To store exercise goals for each user
    private int points = 0;
    private String currentUser; // Store the username of the current user

    private boolean loggedIn = false;

    public FitnessTrackerApp() {
        setTitle("Fitness Tracker App");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ImageIcon icon = new ImageIcon("fitness_icon.png");
        setIconImage(icon.getImage());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2));
        loginPanel.setBackground(new Color(240, 248, 255)); // Light Blue
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        logoutButton = new JButton("Logout");
        signupButton = new JButton("Signup");
        statusLabel = new JLabel("Please login");

        loginPanel.add(new JLabel("Username: "));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password: "));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(signupButton);
        loginPanel.add(logoutButton);
        loginPanel.add(statusLabel);

        exercisePanel = new JPanel();
        exercisePanel.setLayout(new GridLayout(2, 2));
        exercisePanel.setBackground(new Color(240, 255, 240)); // Honeydew
        exercisePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        trackExerciseButton = new JButton("Track Exercise");
        gymExercisesLabel = new JLabel("Gym Exercises:");
        yogaExercisesLabel = new JLabel("Yoga Exercises:");
        exercisePanel.add(trackExerciseButton);
        exercisePanel.add(gymExercisesLabel);
        exercisePanel.add(yogaExercisesLabel);
        exercisePanel.setVisible(false);

        JPanel pointsPanel = new JPanel();
        pointsPanel.setLayout(new GridLayout(1, 2));
        pointsPanel.setBackground(new Color(255, 255, 224)); // Light Yellow
        pointsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        pointsLabel = new JLabel("Points: " + points);
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        pointsPanel.add(pointsLabel);
        pointsPanel.add(graphPanel);

        // Create the table
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Exercise");
        tableModel.addColumn("Time");
        exerciseTable = new JTable(tableModel);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 228, 225)); // Misty Rose
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tablePanel.add(new JScrollPane(exerciseTable), BorderLayout.CENTER);

        // Reward panel
        JPanel rewardPanel = new JPanel(new BorderLayout());
        rewardPanel.setBackground(new Color(255, 250, 205)); // Lemon Chiffon
        rewardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rewardListModel = new DefaultListModel<>();
        rewardList = new JList<>(rewardListModel);
        rewardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rewardPanel.add(new JLabel("Rewards Earned:"), BorderLayout.NORTH);
        rewardPanel.add(new JScrollPane(rewardList), BorderLayout.CENTER);

        add(loginPanel, BorderLayout.NORTH);
        add(exercisePanel, BorderLayout.CENTER);
        add(pointsPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.WEST);
        add(rewardPanel, BorderLayout.EAST); // Added reward panel

        credentials = new HashMap<>();
        exerciseMap = new HashMap<>();
        exerciseGoals = new HashMap<>();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Check if the user is already signed up
                if (!credentials.containsKey(username)) {
                    JOptionPane.showMessageDialog(null, "You need to sign up first.");
                    return; // Stop further processing
                }

                if (authenticate(username, password)) {
                    loggedIn = true;
                    statusLabel.setText("Logged in as " + username);
                    loginButton.setEnabled(false);
                    logoutButton.setEnabled(true);
                    exercisePanel.setVisible(true);

                    // Set the current user
                    currentUser = username;

                    // Update points label
                    pointsLabel.setText("Points: " + points);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect username or password. Please try again.");
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loggedIn = false;
                usernameField.setText("");
                passwordField.setText("");
                statusLabel.setText("Please login");
                loginButton.setEnabled(true);
                logoutButton.setEnabled(false);
                exercisePanel.setVisible(false);

                // Reset points to zero
                points = 0;
                pointsLabel.setText("Points: " + points);
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (!username.isEmpty() && !password.isEmpty()) {
                    if (credentials.containsKey(username)) {
                        JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different one.");
                    } else {
                        credentials.put(username, password);
                        JOptionPane.showMessageDialog(null, "Signup successful. Please login with your credentials.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password to signup.");
                }
            }
        });

        trackExerciseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedIn) {
                    Object[] options = {"Yoga", "Gym"};
                    int choice = JOptionPane.showOptionDialog(null, "Select Exercise Type", "Exercise Type", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (choice == 0) {
                        // Display Yoga exercises
                        String[] yogaExercises = {"Sun Salutation", "Warrior Pose", "Downward Facing Dog", "Tree Pose"};
                        String selectedExercise = (String) JOptionPane.showInputDialog(null, "Select Yoga Exercise", "Yoga Exercises", JOptionPane.PLAIN_MESSAGE, null, yogaExercises, yogaExercises[0]);
                        if (selectedExercise != null) {
                            setGoalAndStartTimer(selectedExercise);
                        }
                    } else if (choice == 1) {
                        // Display Gym exercises
                        String[] gymExercises = {"Deadlift", "Squats", "Bench Press", "Pull-ups"};
                        String selectedExercise = (String) JOptionPane.showInputDialog(null, "Select Gym Exercise", "Gym Exercises", JOptionPane.PLAIN_MESSAGE, null, gymExercises, gymExercises[0]);
                        if (selectedExercise != null) {
                            setGoalAndStartTimer(selectedExercise);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please login to track exercise.");
                }
            }
        });

        // Add listener for reward list
        rewardList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = rewardList.getSelectedIndex();
                if (index >= 0 && rewardListModel.getElementAt(index).endsWith("(Unlocked)")) {
                    showReward(index + 1);
                }
            }
        });

        logoutButton.setEnabled(false); // Initially disable logout button

        initializeRewards(); // Initialize rewards
    }

    private void initializeRewards() {
        // Initialize rewards as locked
        for (int i = 1; i <= 10; i++) {
            rewardListModel.addElement("Reward " + i + " (Locked)");
        }
    }

    private void setGoalAndStartTimer(String selectedExercise) {
        // Set goal for the selected exercise
        String goal = JOptionPane.showInputDialog(null, "Enter time you want to " + selectedExercise + ":");
        if (goal != null) {
            exerciseGoals.put(selectedExercise, Integer.parseInt(goal));
            updateExerciseTable(); // Update the exercise table
            setTimer(selectedExercise);
        }
    }

    private void setTimer(String selectedExercise) {
        // Set the timer based on user input
        String timerValue = JOptionPane.showInputDialog(null, "Set timer for " + selectedExercise + " (in minutes):");
        if (timerValue != null && !timerValue.isEmpty()) {
            int minutes = Integer.parseInt(timerValue);
            int totalSeconds = minutes * 60; // Convert minutes to seconds
            displayStopwatch(selectedExercise, totalSeconds);
        }
    }

    private void displayStopwatch(String selectedExercise, int totalSeconds) {
        // Create and display stopwatch
        JFrame stopwatchFrame = new JFrame("Stopwatch");
        stopwatchFrame.setSize(300, 200);
        stopwatchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel stopwatchPanel = new JPanel();
        stopwatchPanel.setLayout(new BorderLayout());

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        stopwatchPanel.add(timeLabel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start");
        stopwatchPanel.add(startButton, BorderLayout.SOUTH);

        stopwatchFrame.add(stopwatchPanel);
        stopwatchFrame.setVisible(true);

        Timer timer = new Timer(1000, new ActionListener() {
            int remainingSeconds = totalSeconds;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    int minutes = remainingSeconds / 60;
                    int seconds = remainingSeconds % 60;
                    DecimalFormat df = new DecimalFormat("00");
                    String time = df.format(minutes) + ":" + df.format(seconds);
                    timeLabel.setText(time);
                } else {
                    ((Timer) e.getSource()).stop();
                    JOptionPane.showMessageDialog(null, "Time's up for " + selectedExercise + "!");
                    int creditedPoints = totalSeconds / 2; // Credit user with half of the time as points
                    points += creditedPoints; // Increase user's points
                    pointsLabel.setText("Points: " + points); // Update points label

                    // Check if user has earned enough points for a reward
                    int rewardsEarned = points / 100;
                    if (rewardsEarned > 0) {
                        for (int i = 1; i <= rewardsEarned; i++) {
                            if (i <= 10 && !rewardListModel.getElementAt(i - 1).endsWith("(Unlocked)")) {
                                rewardListModel.setElementAt("Reward " + i + " (Unlocked)", i - 1);
                            }
                        }
                    }
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.start();
            }
        });
    }

    private void drawGraph(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        int height = graphPanel.getHeight();
        int width = graphPanel.getWidth();
        int barWidth = width / 10;
        int barHeight = (int) ((double) points / 10 * height); // Scale points to fit graph height
        g2d.fillRect(0, height - barHeight, barWidth, barHeight);
    }

    private void updateExerciseTable() {
        tableModel.setRowCount(0); // Clear previous data
        for (String exercise : exerciseGoals.keySet()) {
            int goal = exerciseGoals.get(exercise);
            tableModel.addRow(new Object[]{exercise, goal});
        }
    }

    private boolean authenticate(String username, String password) {
        // Perform authentication here
        return true; // Dummy authentication for demonstration
    }

    private void showReward(int rewardNumber) {
        String rewardContent = "";
        String quote = "";
        switch (rewardNumber) {
            case 1:
                rewardContent = "Exclusive Gym Swag Pack";
                quote = "The only bad workout is the one that didn't happen.";
                break;
            case 2:
                rewardContent = "One-month Gym Membership Coupon";
                quote = "Don't wish for it, work for it.";
                break;
            case 3:
                rewardContent = "Yoga Essentials Kit";
                quote = "Your body can do anything. It's your brain you have to convince.";
                break;
            case 4:
                rewardContent = "Fitness Tracker Watch";
                quote = "The only way to do great work is to love what you do.";
                break;
            case 5:
                rewardContent = "Healthy Recipe Cookbook";
                quote = "Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle.";
                break;
            case 6:
                rewardContent = "Personal Training Session Voucher";
                quote = "Your only limit is you.";
                break;
            case 7:
                rewardContent = "Wellness Retreat Weekend Pass";
                quote = "The pain you feel today will be the strength you feel tomorrow.";
                break;
            case 8:
                rewardContent = "Nutrition Consultation Package";
                quote = "Strive for progress, not perfection.";
                break;
            case 9:
                rewardContent = "Exercise Equipment Set";
                quote = "Don't stop until you're proud.";
                break;
            case 10:
                rewardContent = "Personalized Fitness Plan";
                quote = "Success is not owned, it's rented. And rent is due every day.";
                break;
            default:
                rewardContent = "No reward available";
                quote = "Keep pushing forward!";
        }

        JFrame rewardFrame = new JFrame("Reward " + rewardNumber);
        rewardFrame.setSize(300, 200);
        rewardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rewardFrame.getContentPane().setBackground(new Color(255, 245, 200)); // Set background color

        JPanel rewardPanel = new JPanel();
        rewardPanel.setLayout(new BorderLayout());
        rewardPanel.setBackground(new Color(255, 245, 200)); // Set background color

        JLabel rewardLabel = new JLabel("Congratulations! You have earned " + rewardContent + "!");
        rewardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rewardPanel.add(rewardLabel, BorderLayout.CENTER);

        JLabel quoteLabel = new JLabel("Motivational Quote: " + quote);
        quoteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rewardPanel.add(quoteLabel, BorderLayout.SOUTH);

        rewardFrame.add(rewardPanel);
        rewardFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FitnessTrackerApp().setVisible(true);
            }
        });
    }
}