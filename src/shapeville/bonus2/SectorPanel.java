package shapeville.bonus2;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;
import shapeville.utils.WoodenButton;
import shapeville.utils.ColorConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel for Bonus 2: Sector Area & Perimeter Practice in the Shapeville application.
 * <p>
 * This panel allows users to practice calculating the area and perimeter of sectors,
 * provides interactive feedback, tracks progress and attempts, and integrates with the main application.
 * </p>
 */
public class SectorPanel extends JPanel {
    /** Reference to the main application instance */
    private final ShapevilleApp mainApp;
    /** List of sector data for the practice */
    private final List<SectorData> sectors;
    /** Index of the current sector being practiced */
    private int currentIndex = -1;
    /** Number of attempts for the current sector */
    private int attempts = 0;
    /** Number of completed sectors */
    private int completed = 0;
    /** Total number of sectors */
    private static final int TOTAL = 8;
    /** Maximum number of attempts allowed */
    private static final int MAX_ATTEMPTS = ScoreManager.MAX_ATTEMPTS;
    /** Time limit for each sector (in seconds) */
    private static final int TIME_LIMIT = 300;
    /** Countdown timer for the current sector */
    private Timer countdownTimer;
    /** Remaining seconds for the current sector */
    private int secondsRemaining;
    /** Array to track which sectors are completed */
    private boolean[] completedSectors;
    /** Static flag to remember if all sectors are completed */
    private static boolean hasCompletedAllSectors = false;
    /** Progress bar for Bonus 2 */
    private JProgressBar bonus2Progress;

    // UI components
    /** Panel for sector selection */
    private JPanel sectorSelectionPanel;
    /** Panel for practice interface */
    private JPanel practicePanel;
    /** Label for prompts */
    private JLabel promptLabel;
    /** Label for attempts */
    private JLabel attemptsLabel;
    /** Label for progress */
    private JLabel progressLabel;
    /** Label for timer */
    private JLabel timerLabel;
    /** Label for feedback */
    private JLabel feedbackLabel;
    /** Text field for area input */
    private JTextField areaField;
    /** Text field for perimeter input */
    private JTextField perimeterField;
    /** Button to submit answers */
    private WoodenButton submitButton;
    /** Button to return home */
    private WoodenButton homeButton;
    /** Button to proceed to next sector */
    private WoodenButton nextButton;
    /** Main panel for layout */
    private JPanel mainPanel;
    /** Decimal formatter for results */
    private DecimalFormat df = new DecimalFormat("0.00");

    /** Display panel for the sector graphic */
    private SectorDisplayPanel displayPanel;

    /**
     * Constructs a SectorPanel for Bonus 2.
     * Initializes UI components, sector data, and sets up the layout.
     *
     * @param app The main application instance
     */
    public SectorPanel(ShapevilleApp app) {
        this.mainApp = app;
        this.sectors = createSectors();
        this.completedSectors = new boolean[TOTAL];
        if (hasCompletedAllSectors) {
            // 如果之前已经完成了所有扇形，则将所有扇形标记为已完成
            for (int i = 0; i < TOTAL; i++) {
                completedSectors[i] = true;
            }
            completed = TOTAL;
        }
        setLayout(new BorderLayout());
        createAndShowSectorSelection();
    }

    /**
     * Creates the list of sector data for the practice.
     *
     * @return List of SectorData objects
     */
    private List<SectorData> createSectors() {
        List<SectorData> list = new ArrayList<>();
        list.add(new SectorData(8, 90, "cm"));
        list.add(new SectorData(18, 130, "ft"));
        list.add(new SectorData(19, 120, "cm"));
        list.add(new SectorData(22, 110, "ft"));
        list.add(new SectorData(3.5, 100, "m"));
        list.add(new SectorData(8, 270, "in"));
        list.add(new SectorData(12, 280, "yd"));
        list.add(new SectorData(15, 250, "mm"));
        return list;
    }

    /**
     * Creates and displays the sector selection panel.
     */
    private void createAndShowSectorSelection() {
        removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.BONUS_BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel title = new JLabel("Bonus 2: Select a Sector to Practice");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Grid of sectors - changed to 2x4 grid
        sectorSelectionPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        sectorSelectionPanel.setBackground(panel.getBackground());
        sectorSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Check if all sectors are completed
        boolean allCompleted = true;
        for (int i = 0; i < sectors.size(); i++) {
            if (!completedSectors[i]) {
                allCompleted = false;
            }
            JPanel sectorButton = createSectorButton(i);
            sectorSelectionPanel.add(sectorButton);
        }
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        centerPanel.add(sectorSelectionPanel, BorderLayout.CENTER);
        
        // Progress panel
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(panel.getBackground());
        
        // Progress label
        JLabel progressLabel = new JLabel(String.format("Completed: %d/%d", completed, TOTAL));
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        progressLabel.setHorizontalAlignment(JLabel.CENTER);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        
        // Progress bar
        bonus2Progress = new JProgressBar(0, 100);
        bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
        bonus2Progress.setStringPainted(true);
        bonus2Progress.setString(bonus2Progress.getValue() + "%");
        progressPanel.add(bonus2Progress, BorderLayout.CENTER);
        
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // If all sectors are completed, show return to home button
        if (allCompleted) {
            WoodenButton homeButton = new WoodenButton("Return to Home");
            homeButton.addActionListener(e -> returnToHomeWithProgress());
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(panel.getBackground());
            buttonPanel.add(homeButton);
            mainApp.updateProgress(100.0/6);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        add(panel);
        revalidate();
        repaint();
    }

    /**
     * Creates a button panel for a specific sector.
     *
     * @param index The index of the sector
     * @return JPanel representing the sector button
     */
    private JPanel createSectorButton(int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Title label (Sector & Arc N)
        JLabel titleLabel = new JLabel("Sector & Arc " + (index + 1));
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(completedSectors[index] ? Color.GRAY : new Color(101, 67, 33));
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(titleLabel);

        // "Done" label for completed sectors
        JLabel doneLabel = new JLabel("Done");
        doneLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        doneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneLabel.setForeground(Color.GRAY);
        doneLabel.setVisible(completedSectors[index]);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(doneLabel);
        contentPanel.add(Box.createVerticalGlue());

        // Button with wooden style
        JButton button = new WoodenButton("");
        button.setLayout(new BorderLayout());
        button.setBackground(completedSectors[index] ? new Color(220, 220, 220) : new Color(232, 194, 145));
        button.setEnabled(!completedSectors[index]);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.add(contentPanel, BorderLayout.CENTER);
        if (!completedSectors[index]) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startPractice(index);
                }
            });
        }
        
        panel.add(button, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Starts the practice for the selected sector.
     *
     * @param index The index of the sector to practice
     */
    private void startPractice(int index) {
        currentIndex = index;
        removeAll();
        add(createMainPanel());
        revalidate();
        repaint();
        displaySector(currentIndex);
    }

    /**
     * Creates the main practice panel for area and perimeter calculation.
     *
     * @return JPanel containing the practice interface
     */
    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorConstants.BONUS_BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Bonus 2: Sector Area & Perimeter");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setOpaque(true);
        title.setBackground(mainPanel.getBackground());
        mainPanel.add(title, BorderLayout.NORTH);

        // 创建左右分隔的面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        
        // 左侧面板 - 扇形显示
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(mainPanel.getBackground());
        
        displayPanel = new SectorDisplayPanel();
        displayPanel.setPreferredSize(new Dimension(300, 300));
        displayPanel.setMaximumSize(new Dimension(300, 300));
        displayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayPanel.setOpaque(true);
        displayPanel.setBackground(mainPanel.getBackground());
        leftPanel.add(displayPanel);
        
        promptLabel = new JLabel();
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        promptLabel.setOpaque(true);
        promptLabel.setBackground(mainPanel.getBackground());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(promptLabel);
        
        splitPane.setLeftComponent(leftPanel);
        
        // 右侧面板 - 输入和解答
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(mainPanel.getBackground());
        
        // 输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(mainPanel.getBackground());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        areaField = new JTextField(15);
        areaField.setMaximumSize(new Dimension(300, 35));
        areaField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(areaField);
        inputPanel.add(Box.createVerticalStrut(10));

        perimeterField = new JTextField(15);
        perimeterField.setMaximumSize(new Dimension(300, 35));
        perimeterField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(perimeterField);
        inputPanel.add(Box.createVerticalStrut(15));

        submitButton = new WoodenButton("Submit");
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(e -> checkAnswer());
        inputPanel.add(submitButton);
        
        rightPanel.add(inputPanel);
        
        // 解答面板
        JPanel solutionPanel = new JPanel();
        solutionPanel.setLayout(new BoxLayout(solutionPanel, BoxLayout.Y_AXIS));
        solutionPanel.setBackground(mainPanel.getBackground());
        solutionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        feedbackLabel.setOpaque(true);
        feedbackLabel.setBackground(mainPanel.getBackground());
        solutionPanel.add(feedbackLabel);
        
        rightPanel.add(solutionPanel);
        
        splitPane.setRightComponent(rightPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // 底部状态面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(mainPanel.getBackground());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statusPanel.setBackground(mainPanel.getBackground());

        attemptsLabel = new JLabel();
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(attemptsLabel);

        progressLabel = new JLabel();
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(progressLabel);

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(timerLabel);

        bottomPanel.add(statusPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(mainPanel.getBackground());

        nextButton = new WoodenButton("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextSector());
        btnPanel.add(nextButton);

        bottomPanel.add(btnPanel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Displays the sector at the specified index and resets input fields.
     *
     * @param index The index of the sector to display
     */
    private void displaySector(int index) {
        if (index >= TOTAL) {
            showCompletionPanel();
            return;
        }
        SectorData s = sectors.get(index);
        displayPanel.setSector(s.radius, s.angle, s.unit);
        String sectorInfo = String.format("Sector %d/8: radius=%.2f %s, angle=%d°", 
                index + 1, s.radius, s.unit, s.angle);
        promptLabel.setText(sectorInfo);
        areaField.setText("");
        perimeterField.setText("");
        areaField.setBorder(BorderFactory.createTitledBorder(
            String.format("Area (%s²)", s.unit)));
        perimeterField.setBorder(BorderFactory.createTitledBorder(
            String.format("Perimeter (%s)", s.unit)));
        feedbackLabel.setText(" ");
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        startTimer();
    }

    /**
     * Checks the user's answers for area and perimeter, provides feedback, and updates progress.
     */
    private void checkAnswer() {
        String areaStr = areaField.getText().trim();
        String periStr = perimeterField.getText().trim();
        if (areaStr.isEmpty() || periStr.isEmpty()) {
            feedbackLabel.setText("Please enter both area and perimeter!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        double userArea, userPeri;
        try {
            userArea = Double.parseDouble(areaStr);
            userPeri = Double.parseDouble(periStr);
        } catch (Exception e) {
            feedbackLabel.setText("Please enter valid numbers!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        SectorData s = sectors.get(currentIndex);
        double correctArea = s.getArea();
        double correctPeri = s.getPerimeter();
        boolean areaOK = Math.abs(userArea - correctArea) < 0.1;
        boolean periOK = Math.abs(userPeri - correctPeri) < 0.1;
        
        if (areaOK && periOK) {
            if (countdownTimer != null) countdownTimer.stop();
            int score = ScoreManager.calculateScore(true, attempts);
            feedbackLabel.setText("Correct! +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0));
            mainApp.updateScore(score);
            completedSectors[currentIndex] = true;
            completed++;
            if (completed == TOTAL) {
                hasCompletedAllSectors = true; // 标记为全部完成
            }
            progressLabel.setText("Progress: " + completed + "/" + TOTAL);
            // 更新 Bonus2 的进度条
            if (bonus2Progress != null) {
                bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
                bonus2Progress.setString(bonus2Progress.getValue() + "%");
            }
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
            showDetailedSolution(s);
        } else {
            feedbackLabel.setText("Wrong, try again!");
            feedbackLabel.setForeground(Color.RED);
            if (attempts >= MAX_ATTEMPTS) {
                if (countdownTimer != null) countdownTimer.stop();
                completedSectors[currentIndex] = true;
                completed++;
                progressLabel.setText("Progress: " + completed + "/" + TOTAL);
                // 更新 Bonus2 的进度条
                if (bonus2Progress != null) {
                    bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
                    bonus2Progress.setString(bonus2Progress.getValue() + "%");
                }
                showDetailedSolution(s);
                submitButton.setEnabled(false);
                nextButton.setEnabled(true);
            }
        }
    }

    /**
     * Proceeds to the next sector after completion or timeout.
     */
    private void nextSector() {
        if (currentIndex >= 0) {
            completedSectors[currentIndex] = true;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        createAndShowSectorSelection();
    }

    /**
     * Starts the countdown timer for the current sector.
     */
    private void startTimer() {
        secondsRemaining = TIME_LIMIT;
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            secondsRemaining--;
            int min = secondsRemaining / 60;
            int sec = secondsRemaining % 60;
            timerLabel.setText(String.format("Time left: %d:%02d", min, sec));
            if (secondsRemaining <= 0) {
                countdownTimer.stop();
                timeExpired();
            } else if (secondsRemaining <= 30) {
                timerLabel.setForeground(Color.RED);
            } else {
                timerLabel.setForeground(Color.BLACK);
            }
        });
        countdownTimer.start();
        timerLabel.setForeground(Color.BLACK);
    }

    /**
     * Handles the event when time expires for the current sector.
     */
    private void timeExpired() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        completedSectors[currentIndex] = true;
        completed++;
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        // 更新 Bonus2 的进度条
        if (bonus2Progress != null) {
            bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
            bonus2Progress.setString(bonus2Progress.getValue() + "%");
        }
        showDetailedSolution(sectors.get(currentIndex));
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
    }

    /**
     * Shows the completion panel when all sectors are finished.
     */
    private void showCompletionPanel() {
        removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.SUCCESS_BG_COLOR);
        JLabel label = new JLabel("Congratulations! All sectors completed!");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        WoodenButton homeBtn = new WoodenButton("Home");
        homeBtn.addActionListener(e -> returnToHomeWithProgress());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(panel.getBackground());
        btnPanel.add(homeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
        // 新增：全部完成时加主进度
        mainApp.addBonus2Progress();
    }

    /**
     * Shows a detailed solution for the current sector, including calculation steps.
     *
     * @param s The SectorData object for the current sector
     */
    private void showDetailedSolution(SectorData s) {
        JTextArea solutionArea = new JTextArea();
        solutionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solutionArea.setBackground(mainPanel.getBackground());
        solutionArea.setEditable(false);
        solutionArea.setLineWrap(true);
        solutionArea.setWrapStyleWord(true);
        
        // Calculate with PI = 3.14
        double PI = 3.14;
        double areaStep1 = s.angle / 360.0;
        double areaStep2 = areaStep1 * PI * s.radius * s.radius;
        
        double arcLength = s.angle / 360.0 * 2 * PI * s.radius;
        double perimeter = arcLength + 2 * s.radius;
        
        StringBuilder solution = new StringBuilder();
        
        // First show the correctness of user's answer
        String userArea = areaField.getText().trim();
        String userPeri = perimeterField.getText().trim();
        try {
            double userAreaVal = Double.parseDouble(userArea);
            double userPeriVal = Double.parseDouble(userPeri);
            
            solution.append("Your answer:\n");
            solution.append(String.format("Area = %.2f %s² ", userAreaVal, s.unit));
            if (Math.abs(userAreaVal - areaStep2) < 0.1) {
                solution.append("(Correct)\n");
            } else {
                solution.append("(Incorrect)\n");
            }
            
            solution.append(String.format("Perimeter = %.2f %s ", userPeriVal, s.unit));
            if (Math.abs(userPeriVal - perimeter) < 0.1) {
                solution.append("(Correct)\n");
            } else {
                solution.append("(Incorrect)\n");
            }
            solution.append("\n");
        } catch (NumberFormatException e) {
            // If user input was not valid numbers, skip this part
        }
        
        // Show the detailed solution
        solution.append("Area calculation steps:\n");
        solution.append("1. Formula:\n   Area = (central angle/360°) × π × r²\n");
        solution.append(String.format("2. Substitute:\n   (%d°/360°) × 3.14 × %.2f²\n", 
                       s.angle, s.radius));
        solution.append(String.format("3. Calculate:\n   %.3f × 3.14 × %.3f\n", 
                       areaStep1, s.radius * s.radius));
        solution.append(String.format("4. Result:\n   %.2f %s²\n\n", areaStep2, s.unit));
        
        solution.append("Perimeter calculation steps:\n");
        solution.append("1. Formula:\n   Perimeter = arc length + 2r\n");
        solution.append("2. Arc length formula:\n   Arc = (angle/360°) × 2π × r\n");
        solution.append(String.format("3. Substitute:\n   (%d°/360°) × 2 × 3.14 × %.2f + 2 × %.2f\n", 
                       s.angle, s.radius, s.radius));
        solution.append(String.format("4. Arc length:\n   %.3f × 2 × 3.14 × %.2f = %.2f\n", 
                       s.angle/360.0, s.radius, arcLength));
        solution.append(String.format("5. Final result:\n   %.2f + %.2f = %.2f %s\n", 
                       arcLength, 2 * s.radius, perimeter, s.unit));
        
        solutionArea.setText(solution.toString());
        
        // Update the right panel
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JSplitPane) {
                JSplitPane splitPane = (JSplitPane) comp;
                JPanel rightPanel = (JPanel) splitPane.getRightComponent();
                rightPanel.removeAll();
                
                // Re-add input panel
                JPanel inputPanel = new JPanel();
                inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
                inputPanel.setBackground(mainPanel.getBackground());
                inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                
                areaField.setMaximumSize(new Dimension(300, 35));
                perimeterField.setMaximumSize(new Dimension(300, 35));
                
                inputPanel.add(areaField);
                inputPanel.add(Box.createVerticalStrut(10));
                inputPanel.add(perimeterField);
                inputPanel.add(submitButton);
                
                rightPanel.add(inputPanel);
                
                // Add solution panel with scroll capability
                JPanel solutionPanel = new JPanel();
                solutionPanel.setLayout(new BorderLayout());
                solutionPanel.setBackground(mainPanel.getBackground());
                solutionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
                
                JScrollPane scrollPane = new JScrollPane(solutionArea);
                scrollPane.setBorder(null);
                scrollPane.setBackground(mainPanel.getBackground());
                scrollPane.getViewport().setBackground(mainPanel.getBackground());
                scrollPane.setPreferredSize(new Dimension(280, 300));
                
                solutionPanel.add(scrollPane, BorderLayout.CENTER);
                rightPanel.add(solutionPanel);
                
                rightPanel.revalidate();
                rightPanel.repaint();
                break;
            }
        }
    }

    /**
     * Returns to the home screen and updates progress.
     */
    private void returnToHomeWithProgress() {
        mainApp.returnToHome();
    }

    /**
     * Checks if all sectors are completed.
     *
     * @return true if all sectors are completed, false otherwise
     */
    public boolean isAllSectorsCompleted() {
        if (completedSectors == null) return hasCompletedAllSectors;
        for (boolean completed : completedSectors) {
            if (!completed) return false;
        }
        hasCompletedAllSectors = true; // 记住完成状态
        return true;
    }

    /**
     * Data class representing a sector with radius, angle, and unit.
     */
    static class SectorData {
        /** The radius of the sector */
        double radius;
        /** The central angle of the sector in degrees */
        int angle;
        /** The unit of measurement */
        String unit;

        /**
         * Constructs a SectorData object.
         *
         * @param r The radius
         * @param a The angle in degrees
         * @param u The unit
         */
        public SectorData(double r, int a, String u) {
            this.radius = r;
            this.angle = a;
            this.unit = u;
        }

        /**
         * Calculates the area of the sector (π = 3.14).
         *
         * @return The area of the sector
         */
        public double getArea() {
            return angle / 360.0 * 3.14 * radius * radius;
        }

        /**
         * Calculates the perimeter of the sector (π = 3.14).
         *
         * @return The perimeter of the sector
         */
        public double getPerimeter() {
            return angle / 360.0 * 2 * 3.14 * radius + 2 * radius;
        }
    }

    /**
     * Inner class for displaying the sector graphic.
     */
    class SectorDisplayPanel extends JPanel {
        /** The radius of the sector */
        private double radius = 8;
        /** The central angle of the sector in degrees */
        private int angle = 90;
        /** The unit of measurement */
        private String unit = "cm";

        /**
         * Constructs a SectorDisplayPanel with default values.
         */
        public SectorDisplayPanel() {
            setOpaque(true);
        }

        /**
         * Sets the sector parameters and repaints the panel.
         *
         * @param r The radius
         * @param a The angle in degrees
         * @param u The unit
         */
        public void setSector(double r, int a, String u) {
            this.radius = r;
            this.angle = a;
            this.unit = u;
            repaint();
        }

        /**
         * Paints the sector graphic, including radius lines and labels.
         *
         * @param g The Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(255, 250, 240));
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 计算面板尺寸和中心点
            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;
            
            // 计算绘图半径和位置
            int drawRadius = (int) Math.round(Math.min(w, h) * 0.35);
            int x = cx - drawRadius;
            int y = cy - drawRadius;
            int drawSize = drawRadius * 2;
            
            // 扇形填充
            g2.setColor(new Color(255, 192, 203));
            g2.fillArc(x, y, drawSize, drawSize, 0, -angle);
            
            // 扇形边框
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawArc(x, y, drawSize, drawSize, 0, -angle);
            
            // 绘制半径线
            // 第一条半径线 (0度)
            int endX1 = cx + (int) Math.round(drawRadius * Math.cos(0));
            int endY1 = cy - (int) Math.round(drawRadius * Math.sin(0));
            g2.drawLine(cx, cy, endX1, endY1);
            
            // 第二条半径线 (-angle度)
            double angleRad = Math.toRadians(-angle);
            int endX2 = cx + (int) Math.round(drawRadius * Math.cos(angleRad));
            int endY2 = cy - (int) Math.round(drawRadius * Math.sin(angleRad));
            g2.drawLine(cx, cy, endX2, endY2);
            
            // 标注
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.setColor(Color.BLACK);
            
            // 半径标注
            String radiusText = String.format("r=%.2f %s", radius, unit);
            g2.drawString(radiusText, cx + 5, cy - 5);
            
            // 角度标注
            String angleText = String.format("%d°", angle);
            double midAngle = Math.toRadians(-angle / 2.0);
            int labelRadius = (int) Math.round(drawRadius * 0.3);
            int labelX = cx + (int) Math.round(labelRadius * Math.cos(midAngle));
            int labelY = cy - (int) Math.round(labelRadius * Math.sin(midAngle));
            g2.drawString(angleText, labelX, labelY);
        }
    }
} 