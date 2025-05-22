package shapeville.bonus1;

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
 * A panel for Bonus 1: Compound Shape Area Practice in the Shapeville application.
 * <p>
 * This panel allows users to practice calculating the area of compound shapes,
 * provides interactive feedback, tracks progress and attempts, and integrates with the main application.
 * </p>
 */
public class CompoundPanel extends JPanel {
    /** Reference to the main application instance */
    private final ShapevilleApp mainApp;
    /** List of compound shape data for the practice */
    private final List<CompoundData> compounds;
    /** Index of the current compound shape being practiced */
    private int currentIndex = -1;
    /** Number of attempts for the current compound shape */
    private int attempts = 0;
    /** Number of completed compound shapes */
    private int completed = 0;
    /** Total number of compound shapes */
    private static final int TOTAL = 6;
    /** Maximum number of attempts allowed */
    private static final int MAX_ATTEMPTS = ScoreManager.MAX_ATTEMPTS;
    /** Time limit for each compound shape (in seconds) */
    private static final int TIME_LIMIT = 300;
    /** Countdown timer for the current compound shape */
    private Timer countdownTimer;
    /** Remaining seconds for the current compound shape */
    private int secondsRemaining;
    /** Array to track which compound shapes are completed */
    private boolean[] completedSectors;
    /** Static flag to remember if all compound shapes are completed */
    private static boolean hasCompletedAllSectors = false;
    /** Progress bar for Bonus 1 */
    private JProgressBar bonus1Progress;

    // UI components
    /** Panel for compound shape selection */
    private JPanel compoundSelectionPanel;
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
    /** Button to proceed to next compound shape */
    private WoodenButton nextButton;
    /** Main panel for layout */
    private JPanel mainPanel;
    /** Decimal formatter for results */
    private DecimalFormat df = new DecimalFormat("0.00");

    /** Display panel for the compound shape graphic */
    private CompoundDisplayPanel displayPanel;

    /**
     * Constructs a CompoundPanel for Bonus 1.
     * Initializes UI components, compound shape data, and sets up the layout.
     *
     * @param app The main application instance
     */
    public CompoundPanel(ShapevilleApp app) {
        this.mainApp = app;
        this.compounds = createCompounds();
        this.completedSectors = new boolean[TOTAL];
        if (hasCompletedAllSectors) {
            // 如果之前已经完成了所有扇形，则将所有扇形标记为已完成
            for (int i = 0; i < TOTAL; i++) {
                completedSectors[i] = true;
            }
            completed = TOTAL;
        }
        setLayout(new BorderLayout());
        createAndShowCompoundSelection();
    }

    /**
     * Creates the list of compound shape data for the practice.
     *
     * @return List of CompoundData objects
     */
    private List<CompoundData> createCompounds() {
        List<CompoundData> list = new ArrayList<>();
        list.add(new CompoundData("shapeville/images/bonus1/Compound1.jpg", "cm", 310.0));
        list.add(new CompoundData("shapeville/images/bonus1/Compound2.jpg", "cm", 598.0));
        list.add(new CompoundData("shapeville/images/bonus1/Compound3.jpg", "m", 288.0));
        list.add(new CompoundData("shapeville/images/bonus1/Compound4.jpg", "m", 18.0));
        list.add(new CompoundData("shapeville/images/bonus1/Compound5.jpg", "m", 3456.0));
        list.add(new CompoundData("shapeville/images/bonus1/Compound6.jpg", "m", 174.0));
        return list;
    }

    /**
     * Creates and displays the compound shape selection panel.
     */
    private void createAndShowCompoundSelection() {
        removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.BONUS_BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel title = new JLabel("Bonus 1: Select a Compound to Practice");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Grid of sectors
        compoundSelectionPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        compoundSelectionPanel.setBackground(panel.getBackground());
        compoundSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Check if all sectors are completed
        boolean allCompleted = true;
        for (int i = 0; i < compounds.size(); i++) {
            if (!completedSectors[i]) {
                allCompleted = false;
            }
            CompoundData compound = compounds.get(i);
            JPanel compoundButton = createCompoundButton(compound, i);
            compoundSelectionPanel.add(compoundButton);
        }
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        centerPanel.add(compoundSelectionPanel, BorderLayout.CENTER);
        
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
        bonus1Progress = new JProgressBar(0, 100);
        bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
        bonus1Progress.setStringPainted(true);
        bonus1Progress.setString(bonus1Progress.getValue() + "%");
        progressPanel.add(bonus1Progress, BorderLayout.CENTER);
        
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // If all sectors are completed, show return to home button
        if (allCompleted) {
            homeButton = new WoodenButton("Return to Home");
            homeButton.addActionListener(e -> mainApp.returnToHome());
            //mainApp.updateProgress(100.0/6);
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(panel.getBackground());
            buttonPanel.add(homeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        add(panel);
        revalidate();
        repaint();
    }

    /**
     * Creates a button panel for a specific compound shape.
     *
     * @param compound The compound shape data
     * @param index The index of the compound shape
     * @return JPanel representing the compound shape button
     */
    private JPanel createCompoundButton(CompoundData compound, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // 内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Compound Shape " + (index + 1));
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(completedSectors[index] ? Color.GRAY : new Color(101, 67, 33));
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(titleLabel);

        JLabel doneLabel = new JLabel("Done");
        doneLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        doneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneLabel.setForeground(Color.GRAY);
        doneLabel.setVisible(completedSectors[index]);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(doneLabel);
        contentPanel.add(Box.createVerticalGlue());

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
     * Starts the practice for the selected compound shape.
     *
     * @param index The index of the compound shape to practice
     */
    private void startPractice(int index) {
        currentIndex = index;
        removeAll();
        add(createMainPanel());
        revalidate();
        repaint();
        displayCompound(currentIndex);
    }

    /**
     * Creates the main practice panel for area calculation.
     *
     * @return JPanel containing the practice interface
     */
    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ColorConstants.BONUS_BG_COLOR);

        JLabel title = new JLabel("Bonus 1: Compound Area & Perimeter");
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
        
        displayPanel = new CompoundDisplayPanel();
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
     * Displays the compound shape at the specified index and resets input fields.
     *
     * @param index The index of the compound shape to display
     */
    private void displayCompound(int index) {
        if (index >= TOTAL) {
            showCompletionPanel();
            return;
        }
        CompoundData c = compounds.get(index);
        displayPanel.setCompound(c.img_path);
        String compoundInfo = String.format("Compound %d/6", 
                index + 1);
        promptLabel.setText(compoundInfo);
        areaField.setText("");
        areaField.setBorder(BorderFactory.createTitledBorder(
            String.format("Area (%s²)", c.unit)));
        feedbackLabel.setText(" ");
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        startTimer();
    }

    /**
     * Checks the user's answer for area, provides feedback, and updates progress.
     */
    private void checkAnswer() {
        String areaStr = areaField.getText().trim();

        if (areaStr.isEmpty()) {
            feedbackLabel.setText("Please enter the area!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        double userArea;
        try {
            userArea = Double.parseDouble(areaStr);
        } catch (Exception e) {
            feedbackLabel.setText("Please enter valid number!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        CompoundData c = compounds.get(currentIndex);
        double correctArea = c.area;
        boolean areaOK = Math.abs(userArea - correctArea) < 0.1;

        
        if (areaOK) {
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
            if (bonus1Progress != null) {
                bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
                bonus1Progress.setString(bonus1Progress.getValue() + "%");
            }
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
            showDetailedSolution(c);
            if (isAllCompoundsCompleted()) {
                mainApp.updateProgress(100.0/6);
            }
        } else {
            feedbackLabel.setText("Wrong, try again!");
            feedbackLabel.setForeground(Color.RED);
            if (attempts >= MAX_ATTEMPTS) {
                if (countdownTimer != null) countdownTimer.stop();
                completedSectors[currentIndex] = true;
                completed++;
                progressLabel.setText("Progress: " + completed + "/" + TOTAL);
                // 更新 Bonus2 的进度条
                if (bonus1Progress != null) {
                    bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
                    bonus1Progress.setString(bonus1Progress.getValue() + "%");
                }
                showDetailedSolution(c);
                submitButton.setEnabled(false);
                nextButton.setEnabled(true);
            }
            if (isAllCompoundsCompleted()) {
                mainApp.updateProgress(100.0/6);
            }
        }
    }

    /**
     * Proceeds to the next compound shape after completion or timeout.
     */
    private void nextSector() {
        if (currentIndex >= 0) {
            completedSectors[currentIndex] = true;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        createAndShowCompoundSelection();
    }

    /**
     * Starts the countdown timer for the current compound shape.
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
     * Handles the event when time expires for the current compound shape.
     */
    private void timeExpired() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        completedSectors[currentIndex] = true;
        completed++;
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        // 更新 Bonus2 的进度条
        if (bonus1Progress != null) {
            bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
            bonus1Progress.setString(bonus1Progress.getValue() + "%");
        }
        showDetailedSolution(compounds.get(currentIndex));
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
    }

    /**
     * Shows the completion panel when all compound shapes are finished.
     */
    private void showCompletionPanel() {
        removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ColorConstants.SUCCESS_BG_COLOR);
        JLabel label = new JLabel("Congratulations! All compounds completed!");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        WoodenButton homeBtn = new WoodenButton("Home");
        homeBtn.setBackground(new Color(70, 130, 180));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setFocusPainted(false);
        homeBtn.addActionListener(e -> returnToHomeWithProgress());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(panel.getBackground());
        btnPanel.add(homeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
        // 新增：全部完成时加主进度
        mainApp.addBonus1Progress();
    }

    /**
     * Shows a detailed solution for the current compound shape, including calculation steps.
     *
     * @param c The CompoundData object for the current compound shape
     */
    private void showDetailedSolution(CompoundData c) {
        JTextArea solutionArea = new JTextArea();
        solutionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solutionArea.setBackground(mainPanel.getBackground());
        solutionArea.setEditable(false);
        solutionArea.setLineWrap(true);
        solutionArea.setWrapStyleWord(true);

        StringBuilder solution = new StringBuilder();
        
        // First show the correctness of user's answer
        String userArea = areaField.getText().trim();

        try {
            double userAreaVal = Double.parseDouble(userArea);
            
            solution.append("Your answer:\n");
            solution.append(String.format("Area = %.2f %s² ", userAreaVal, c.unit));
            if (Math.abs(userAreaVal - c.area) < 0.1) {
                solution.append("(Correct)\n");
            } else {
                solution.append("(Incorrect)\n");
            }
            
            solution.append("\n");
            solution.append(c.answer);
        } catch (NumberFormatException e) {
            // If user input was not valid numbers, skip this part
        }
        
        // Show the detailed solution
        
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
                inputPanel.add(Box.createVerticalStrut(15));
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
     * Checks if all compound shapes are completed.
     *
     * @return true if all compound shapes are completed, false otherwise
     */
    public boolean isAllCompoundsCompleted() {
        if (completedSectors == null) return hasCompletedAllSectors;
        for (boolean completed : completedSectors) {
            if (!completed) return false;
        }
        hasCompletedAllSectors = true; // 记住完成状态
        System.out.println("isAllCompoundsCompleted: " + hasCompletedAllSectors);
        return true;
    }

    /**
     * Data class representing a compound shape with image path, unit, and area.
     */
    static class CompoundData {
        /** The path to the compound shape image */
        String img_path;
        /** The unit of measurement */
        String unit;
        /** The area of the compound shape */
        double area;
        /** The detailed solution steps */
        String answer;

        /**
         * Constructs a CompoundData object.
         *
         * @param img_path The path to the compound shape image
         * @param unit The unit of measurement
         * @param area The area of the compound shape
         */
        public CompoundData(String img_path, String unit, double area) {
            this.img_path = img_path;
            this.unit = unit;
            this.area = area;
            if (img_path.contains("Compound1")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper left rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 10 × 11 = 110 cm²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 10 × 20 = 200 cm²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 110 + 200 = 310 cm²";
            }
            else if (img_path.contains("Compound2")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper left rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 3 × 18 = 54 cm²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 16 × 34 = 544 cm²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 54 + 544 = 598 cm²";
            }
            else if (img_path.contains("Compound3")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 12 × 12 = 144 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 6 × 24 = 144 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 144 + 144 = 288 m²\n";
            }
            else if (img_path.contains("Compound4")){
                this.answer = "1.This compound shape can be divided into a upper triangle and a lower rectangle.\n2.The area of the upper triangle can be derived from the formula of a triangle (base × height / 2).\n3.S1 = 3 × 4 / 2 = 6 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 3 × 4 = 12 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 6 + 12 = 18 m²\n";
            }
            else if (img_path.contains("Compound5")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 36 × 36 = 1296 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 36 × 60 = 2160 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 1296 + 2160 = 3456 m²\n";
            }
            else if (img_path.contains("Compound6")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 3 × 10 = 30 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 8 × 18 = 144 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 30 + 144 = 174 m²\n";
            }
        }
    }

    /**
     * Inner class for displaying the compound shape graphic.
     */
    class CompoundDisplayPanel extends JPanel {
        /** The path to the compound shape image */
        private String img_path;
        /** The loaded image */
        private Image image;
        
        /**
         * Constructs a CompoundDisplayPanel.
         */
        public CompoundDisplayPanel() {
            setOpaque(true);
        }

        /**
         * Sets the compound shape image and repaints the panel.
         *
         * @param img_path The path to the compound shape image
         */
        public void setCompound(String img_path) {
            this.img_path = img_path;
            // 使用 ClassLoader 加载图片资源
            try {
                java.net.URL imageUrl = getClass().getClassLoader().getResource(img_path);
                if (imageUrl != null) {
                    ImageIcon icon = new ImageIcon(imageUrl);
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        image = icon.getImage();
                    } else {
                        System.err.println("Failed to load image: " + img_path);
                    }
                } else {
                    System.err.println("Image not found: " + img_path);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + img_path);
                e.printStackTrace();
            }
            repaint();
        }

        /**
         * Paints the compound shape image, maintaining aspect ratio.
         *
         * @param g The Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(255, 250, 240));
            
            if (image != null) {
                // 计算图片的显示尺寸，保持宽高比
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = image.getWidth(this);
                int imgHeight = image.getHeight(this);
                
                // 计算缩放比例
                double scale = Math.min(
                    (double) panelWidth / imgWidth,
                    (double) panelHeight / imgHeight
                );
                
                // 计算缩放后的尺寸
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);
                
                // 计算居中位置
                int x = (panelWidth - scaledWidth) / 2;
                int y = (panelHeight - scaledHeight) / 2;
                
                // 绘制图片
                g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
            }
        }
    }
} 