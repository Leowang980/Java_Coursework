package shapeville.task4;

import java.util.Set;
import java.util.HashSet;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;
import java.awt.event.KeyEvent;

import java.util.List;
import java.util.ArrayList;

import shapeville.utils.WoodenButton;
import shapeville.utils.ColorConstants;

/**
 * A panel that implements the circle calculation task in Shapeville.
 * This panel allows users to practice calculating circle area and circumference
 * using either radius or diameter as input. It includes features such as:
 * - Interactive circle visualization
 * - Timer for each calculation
 * - Progress tracking
 * - Multiple calculation types (Area and Circumference)
 * - Input validation and feedback
 */
public class CirclePanel extends JPanel {
    /** Reference to the main application instance */
    private final ShapevilleApp mainApp;
    /** Layout manager for switching between different panels */
    private final CardLayout cardLayout;
    /** Container panel for all content */
    private final JPanel contentPanel;

    /** Display component for the circle visualization */
    private CircleDisplay circleDisplay;
    /** Input field for user's answer */
    private JTextField answerField;
    /** Button to submit the answer */
    private WoodenButton submitButton;
    /** Label to show feedback messages */
    private JLabel feedbackLabel;
    /** Label to show number of attempts */
    private JLabel attemptsLabel;
    /** Label to show progress */
    private JLabel progressLabel;
    /** Label to show remaining time */
    private JLabel timerLabel;

    /** Current calculation type: "Area" or "Circumference" */
    private String currentCalculationType;
    /** Current input type: "Radius" or "Diameter" */
    private String currentInputType;
    /** Current value (radius or diameter) */
    private double currentValue;
    /** Number of attempts for current calculation */
    private int attempts = 0;
    /** Total number of calculations required */
    private int totalCalculations = 0;
    /** Number of completed calculations */
    private int completedCalculations = 0;
    /** Timer for countdown */
    private Timer countdownTimer;
    /** Timer for managing next steps */
    private Timer nextStepTimer;
    /** Remaining seconds in current calculation */
    private int secondsRemaining;
    /** Total time used across all calculations */
    private int totalTimeUsed = 0;

    /** Random number generator for creating calculations */
    private final Random random = new Random();
    /** Formatter for decimal numbers */
    private final DecimalFormat df = new DecimalFormat("#.#");

    /** Value of π used in calculations */
    private static final double PI = 3.14;
    /** Total number of calculations required */
    private static final int TOTAL_CALCULATIONS = 2;

    /** Set to track completed calculation types */
    private Set<String> completedTypes = new HashSet<>();
    /** Panel for calculation type selection */
    private JPanel selectionPanel;
    /** Group for calculation type radio buttons */
    private ButtonGroup calculationTypeGroup;
    /** Radio button for area calculations */
    private JRadioButton areaRadioButton;
    /** Radio button for circumference calculations */
    private JRadioButton circumferenceRadioButton;

    /** Flag indicating if the last answer was correct */
    private boolean lastAnswerCorrect = false;
    /** Next calculation type to be displayed */
    private String nextCalculationType;

    /**
     * Constructs a new CirclePanel with the specified main application reference.
     * Initializes the UI components and sets up the initial state.
     *
     * @param mainApp The main application instance
     */
    public CirclePanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        totalCalculations = TOTAL_CALCULATIONS;

        // Set up the layout
        setLayout(new BorderLayout());

        // Create task panel
        JPanel taskPanel = createTaskPanel();
        contentPanel.add(taskPanel, "TASK");

        // Create completion panel
        JPanel completionPanel = createCompletionPanel();
        contentPanel.add(completionPanel, "COMPLETION");

        // Add the content panel to this panel
        add(contentPanel, BorderLayout.CENTER);

        // Show the task panel first
        cardLayout.show(contentPanel, "TASK");

        // Display the first calculation
        //generateNextCalculation();
        // 创建选择面板
        createSelectionPanel();
        contentPanel.add(selectionPanel, "SELECTION");

        // 初始显示选择面板
        cardLayout.show(contentPanel, "SELECTION");
        // 初始化为空，后续根据用户选择填充
//        requiredInputTypes = new ArrayList<>();
    }

    /**
     * Creates the main task panel containing the circle display and input controls.
     * This panel includes:
     * - Title section
     * - Circle visualization
     * - Formula reference
     * - Input field and submit button
     * - Feedback and progress display
     *
     * @return JPanel containing the task interface
     */
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ColorConstants.MAIN_BG_COLOR); // 使用木质风格的主背景色

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorConstants.TITLE_BG_COLOR); // 使用木质风格的标题背景色
        JLabel titleLabel = new JLabel("Task 4: Circle Calculations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Center panel with the circle display
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());

        // Formula reference panel
        JPanel formulaPanel = createFormulaPanel();
        centerPanel.add(formulaPanel, BorderLayout.NORTH);

        // Circle display panel
        circleDisplay = new CircleDisplay();
        circleDisplay.setPreferredSize(new Dimension(400, 300));
        circleDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(circleDisplay, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with input field and submit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panel.getBackground());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(panel.getBackground());

        JLabel promptLabel = new JLabel("Calculate and enter your answer:");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        answerField = new JTextField(15);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));

        submitButton = new WoodenButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        // Add enter key functionality to the text field
        answerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        inputPanel.add(promptLabel);
        inputPanel.add(answerField);
        inputPanel.add(submitButton);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        // Feedback, attempts and timer panel
        JPanel feedbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        feedbackPanel.setBackground(panel.getBackground());

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 14));

        attemptsLabel = new JLabel("Attempts: 0/" + ScoreManager.MAX_ATTEMPTS);
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        progressLabel = new JLabel("Progress: 0/" + totalCalculations);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        timerLabel = new JLabel("Time: 3:00");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(attemptsLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(progressLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(timerLabel);

        bottomPanel.add(feedbackPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panel.getBackground());

        WoodenButton nextButton = new WoodenButton("Next Calculation");
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdownTimer != null) {
                    countdownTimer.stop();
                }
                generateNextCalculation();
            }
        });

        buttonPanel.add(nextButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the formula reference panel showing area and circumference formulas.
     * Displays both radius-based and diameter-based formulas for each calculation type.
     *
     * @return JPanel containing the formula reference
     */
    private JPanel createFormulaPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Circle Formulas"));
        panel.setBackground(new Color(240, 248, 255));

        // Area formulas
        JPanel areaPanel = new JPanel(new GridLayout(2, 1));
        areaPanel.setBackground(new Color(220, 240, 255));
        areaPanel.setBorder(BorderFactory.createTitledBorder("Area"));

        JLabel areaRadiusFormula = new JLabel("From radius (r): A = π × r²", JLabel.CENTER);
        areaRadiusFormula.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel areaDiameterFormula = new JLabel("From diameter (d): A = π × (d/2)²", JLabel.CENTER);
        areaDiameterFormula.setFont(new Font("Arial", Font.PLAIN, 14));

        areaPanel.add(areaRadiusFormula);
        areaPanel.add(areaDiameterFormula);

        // Circumference formulas
        JPanel circumferencePanel = new JPanel(new GridLayout(2, 1));
        circumferencePanel.setBackground(new Color(255, 240, 220));
        circumferencePanel.setBorder(BorderFactory.createTitledBorder("Circumference"));

        JLabel circumferenceRadiusFormula = new JLabel("From radius (r): C = 2 × π × r", JLabel.CENTER);
        circumferenceRadiusFormula.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel circumferenceDiameterFormula = new JLabel("From diameter (d): C = π × d", JLabel.CENTER);
        circumferenceDiameterFormula.setFont(new Font("Arial", Font.PLAIN, 14));

        circumferencePanel.add(circumferenceRadiusFormula);
        circumferencePanel.add(circumferenceDiameterFormula);

        panel.add(areaPanel);
        panel.add(circumferencePanel);

        return panel;
    }

    /**
     * Creates the completion panel shown when all calculations are finished.
     * Includes a success message and a button to return to the home screen.
     *
     * @return JPanel containing the completion message and return button
     */
    private JPanel createCompletionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ColorConstants.SUCCESS_BG_COLOR); // 使用木质风格的成功背景色

        JLabel completionLabel = new JLabel("Great job! You've completed the Circle Calculations task!");
        completionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        completionLabel.setHorizontalAlignment(JLabel.CENTER);

        WoodenButton homeButton = new WoodenButton("Return to Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //mainApp.addTask4Progress();
                mainApp.returnToHome();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(homeButton);

        panel.add(completionLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 新增：全部完成时加主进度
        //mainApp.addTask4Progress();
        return panel;
    }

    // 新增辅助方法 - 计算平均用时
    private String calculateAverageTime() {
        if (completedCalculations == 0) return "0.0"; // 改为一位小数
        return df.format((double) totalTimeUsed / completedCalculations); // 直接使用 df 格式化
    }

    private void updateProgress() {
        // 删除所有 mainApp.updateProgress(...) 相关调用，只允许 mainApp.addTask4Progress() 在 createCompletionPanel() 里调用
        // 1. updateProgress() 方法体全部注释
        // 2. generateNextCalculation() 方法体内所有 mainApp.updateProgress(...) 注释
        // 3. 其它任何 mainApp.updateProgress(...) 也全部注释
    }

    private void resetStates() {
        completedTypes.clear();
//        requiredInputTypes.clear();
        progressLabel.setText("Progress: 0%");
    }

    private void startTimer() {
        secondsRemaining = 180; // 3 minutes

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsRemaining--;
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));

                if (secondsRemaining <= 0) {
                    countdownTimer.stop();
                    timeExpired();
                } else if (secondsRemaining <= 30) {
                    timerLabel.setForeground(Color.RED);
                }
            }
        });

        countdownTimer.start();
        timerLabel.setForeground(Color.BLACK);
    }

    private void timeExpired() {
        double correctAnswer = calculateCorrectAnswer();
        feedbackLabel.setText("Time's up! The correct answer is: " + df.format(correctAnswer));
        feedbackLabel.setForeground(Color.RED);

        // Show the solution
        circleDisplay.setShowSolution(true);

        // Disable input
        answerField.setEnabled(false);
        submitButton.setEnabled(false);

        // Enable after a short delay and move to next calculation
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                circleDisplay.setShowSolution(false);
                //completedCalculations++;
                generateNextCalculation();
                answerField.setEnabled(true);
                submitButton.setEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Generates the next calculation and updates the display.
     * Handles:
     * - Random selection of input type (radius/diameter)
     * - Value generation
     * - Display updates
     * - Timer management
     * - Progress tracking
     */
    private void generateNextCalculation() {
        // 在生成新题目前重置界面
        circleDisplay.setShowSolution(false);
        answerField.setText("");
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.setEnabled(true);
        submitButton.setEnabled(true);
        attempts = 0;
        attemptsLabel.setText("Attempts: " + ScoreManager.MAX_ATTEMPTS);

        // 检查是否完成所有题目
        if (completedCalculations >= totalCalculations) {
            if (countdownTimer != null) {
                countdownTimer.stop();
            }

            mainApp.updateProgress(100.0/6);
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }
        currentCalculationType = nextCalculationType;// 动态切换计算类型
        // ==== 保持随机生成逻辑 ====
        String[] inputTypes = {"Radius", "Diameter"};
        currentInputType = inputTypes[random.nextInt(2)];
        // 生成随机值并更新界面
        currentValue = 1 + random.nextInt(10);
        circleDisplay.setCircleInfo(currentCalculationType, currentInputType, currentValue);

        // 更新进度
        progressLabel.setText("Progress: " + completedCalculations + "/" + totalCalculations);
        //int progress = ScoreManager.calculateProgress(completedCalculations, totalCalculations);
        //mainApp.updateProgress(progress);

        // 启动计时器
        startTimer();
    }

    /**
     * Creates the selection panel for choosing calculation type.
     * Provides buttons for:
     * - Area calculations
     * - Circumference calculations
     * Each option includes visual styling and formula preview.
     */
    private void createSelectionPanel() {
        selectionPanel = new JPanel(new BorderLayout(10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        selectionPanel.setBackground(ColorConstants.MAIN_BG_COLOR);

        // 标题
        JLabel selectionTitleLabel = new JLabel("Task 4: Choose Calculation Type");
        selectionTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        selectionTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        selectionTitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        selectionPanel.add(selectionTitleLabel, BorderLayout.NORTH);

        // 创建按钮面板 - 使用2行1列的网格，按钮垂直排列
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 30));
        buttonPanel.setBackground(selectionPanel.getBackground());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        // Create Area button with Bonus 1 style
        JPanel areaPanel = new JPanel(new BorderLayout());
        areaPanel.setBackground(new Color(255, 250, 240));
        areaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JPanel areaContentPanel = new JPanel();
        areaContentPanel.setOpaque(false);
        areaContentPanel.setLayout(new BoxLayout(areaContentPanel, BoxLayout.Y_AXIS));
        
        JLabel areaTitleLabel = new JLabel("Area");
        areaTitleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        areaTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        areaTitleLabel.setForeground(new Color(101, 67, 33));
        areaContentPanel.add(Box.createVerticalGlue());
        areaContentPanel.add(areaTitleLabel);
        
        JLabel areaFormulaLabel = new JLabel("π × r²");
        areaFormulaLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        areaFormulaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        areaFormulaLabel.setForeground(new Color(101, 67, 33));
        areaContentPanel.add(Box.createVerticalStrut(10));
        areaContentPanel.add(areaFormulaLabel);
        areaContentPanel.add(Box.createVerticalGlue());
        
        JButton areaButton = new WoodenButton("");
        areaButton.setLayout(new BorderLayout());
        areaButton.setBackground(new Color(232, 194, 145));
        areaButton.setFocusPainted(false);
        areaButton.setContentAreaFilled(true);
        areaButton.setBorderPainted(false);
        areaButton.add(areaContentPanel, BorderLayout.CENTER);
        
        areaButton.addActionListener(e -> {
            currentCalculationType = "Area";
            completedTypes.clear();
            totalCalculations = 2;
            progressLabel.setText("Progress: 0/" + totalCalculations);
            cardLayout.show(contentPanel, "TASK");
            nextCalculationType = currentCalculationType;
            generateNextCalculation();
        });
        
        areaPanel.add(areaButton, BorderLayout.CENTER);

        // Create Circumference button with Bonus 1 style
        JPanel circumferencePanel = new JPanel(new BorderLayout());
        circumferencePanel.setBackground(new Color(255, 250, 240));
        circumferencePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JPanel circumferenceContentPanel = new JPanel();
        circumferenceContentPanel.setOpaque(false);
        circumferenceContentPanel.setLayout(new BoxLayout(circumferenceContentPanel, BoxLayout.Y_AXIS));
        
        JLabel circumferenceTitleLabel = new JLabel("Circumference");
        circumferenceTitleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        circumferenceTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        circumferenceTitleLabel.setForeground(new Color(101, 67, 33));
        circumferenceContentPanel.add(Box.createVerticalGlue());
        circumferenceContentPanel.add(circumferenceTitleLabel);
        
        JLabel circumferenceFormulaLabel = new JLabel("2π × r");
        circumferenceFormulaLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        circumferenceFormulaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        circumferenceFormulaLabel.setForeground(new Color(101, 67, 33));
        circumferenceContentPanel.add(Box.createVerticalStrut(10));
        circumferenceContentPanel.add(circumferenceFormulaLabel);
        circumferenceContentPanel.add(Box.createVerticalGlue());
        
        JButton circumferenceButton = new WoodenButton("");
        circumferenceButton.setLayout(new BorderLayout());
        circumferenceButton.setBackground(new Color(232, 194, 145));
        circumferenceButton.setFocusPainted(false);
        circumferenceButton.setContentAreaFilled(true);
        circumferenceButton.setBorderPainted(false);
        circumferenceButton.add(circumferenceContentPanel, BorderLayout.CENTER);
        
        circumferenceButton.addActionListener(e -> {
            currentCalculationType = "Circumference";
            completedTypes.clear();
            totalCalculations = 2;
            progressLabel.setText("Progress: 0/" + totalCalculations);
            cardLayout.show(contentPanel, "TASK");
            nextCalculationType = currentCalculationType;
            generateNextCalculation();
        });

        circumferencePanel.add(circumferenceButton, BorderLayout.CENTER);

        // 将按钮添加到按钮面板
        buttonPanel.add(areaPanel);
        buttonPanel.add(circumferencePanel);
        
        // 添加进度面板
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(selectionPanel.getBackground());
        
        JLabel progressCompletedLabel = new JLabel("Complete both calculation types to advance");
        progressCompletedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        progressCompletedLabel.setHorizontalAlignment(JLabel.CENTER);
        progressCompletedLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        progressPanel.add(progressCompletedLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(selectionPanel.getBackground());
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        
        selectionPanel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Calculates the correct answer for the current calculation.
     * Handles both area and circumference calculations using either radius or diameter.
     *
     * @return double The correct answer
     */
    private double calculateCorrectAnswer() {
        if (currentCalculationType.equals("Area")) {
            if (currentInputType.equals("Radius")) {
                return PI* Math.pow(currentValue, 2);
            } else { // Diameter
                double radius = currentValue / 2;
                return PI * Math.pow(radius, 2);
            }
        } else { // Circumference
            if (currentInputType.equals("Radius")) {
                return 2 * PI * currentValue;
            } else { // Diameter
                return Math.round( PI * currentValue * 10) / 10.0;
            }
        }
    }

    /**
     * Checks the user's answer against the correct answer.
     * Provides feedback and updates the progress accordingly.
     * Handles both correct and incorrect answers, including:
     * - Input validation
     * - Attempt counting
     * - Score calculation
     * - Progress updates
     */
    private void checkAnswer() {
        // 输入验证（保留原有逻辑）
        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }

        // 数字格式验证（保留原有逻辑）
        double userValue;
        try {
            userValue = Double.parseDouble(userAnswer);
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Invalid input! Numbers only.");
            feedbackLabel.setForeground(Color.RED);
            return;
        }

        // 更新尝试次数（保留原有逻辑）
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);

        // 计算正确答案（保留原有逻辑）
        double correctAnswer = calculateCorrectAnswer();
        double tolerance = 0.1;

        // 答案正确处理（修改部分开始）
        if (Math.abs(userValue - correctAnswer) <= tolerance) {
            // 停止计时器
            if (countdownTimer != null) {
                countdownTimer.stop();
            }

            // 更新得分和进度
            int score = ScoreManager.calculateScore(false, attempts);
            //mainApp.updateScore(score);
            completedCalculations++; // 关键：正确回答后递增完成数

            // 显示反馈
            feedbackLabel.setText("Correct! +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0));

            // 仅显示答案，不自动切换
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
            circleDisplay.setShowSolution(true);
//            Timer timer = new Timer(2500, e -> {
//                circleDisplay.setShowSolution(false);
//                generateNextCalculation();
//                answerField.setEnabled(true);
//                submitButton.setEnabled(true);
//            });

            // 答对切换类型
            nextCalculationType = currentCalculationType.equals("Area")
                    ? "Circumference"
                    : "Area";
            lastAnswerCorrect = true;
//            timer.setRepeats(false);//?
//            timer.start();
        } else {
            // 错误处理（保留原有逻辑，仅添加注释）
            feedbackLabel.setText("Try again! " + getHint(correctAnswer));
            feedbackLabel.setForeground(Color.RED);
            // 答错保持类型
            nextCalculationType = currentCalculationType;
            lastAnswerCorrect = false;
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                // 停止计时器
                if (countdownTimer != null) {
                    countdownTimer.stop();
                }

                // 显示正确答案
                feedbackLabel.setText("Correct answer: " + df.format(correctAnswer));
                circleDisplay.setShowSolution(true);

                // 禁用输入并延迟生成新题目
                answerField.setEnabled(false);
                submitButton.setEnabled(false);

//                Timer timer = new Timer(3000, e -> {
//                    circleDisplay.setShowSolution(false);
//
//                    generateNextCalculation();
//                    answerField.setEnabled(true);
//                    submitButton.setEnabled(true);
//                });
//                timer.setRepeats(false);
//                timer.start();
            }
        }
        //?
//        nextStepTimer = new Timer(2500, e -> {
//            requiredInputTypes.clear();
//            requiredInputTypes.add("Radius");
//            requiredInputTypes.add("Diameter");
//            generateNextCalculation();
//        });
//        nextStepTimer.setRepeats(false); // 关键设置
//        nextStepTimer.start();

    }

    // 新增辅助方法
    private void stopAllTimers() {
        if (countdownTimer != null) countdownTimer.stop();
        if (nextStepTimer != null && nextStepTimer.isRunning()) {
            nextStepTimer.stop();
        }
    }

    private void scheduleNextStep(int delay, Runnable action) {
        stopAllTimers();
        nextStepTimer = new Timer(delay, e -> action.run());
        nextStepTimer.setRepeats(false);
        nextStepTimer.start();
    }

    private String getNextCalculationType() {
        return currentCalculationType.equals("Area") ? "Circumference" : "Area";
    }
    // 新增辅助方法 - 获取动态提示
    private String getHint(double correctAnswer) {
        double difference = Math.abs(correctAnswer - Double.parseDouble(answerField.getText()));

        if (difference > 50) {
            return "(Way too " + (correctAnswer > difference ? "low" : "high") + ")";
        } else if (difference > 10) {
            return "(Quite " + (correctAnswer > difference ? "low" : "high") + ")";
        } else {
            return "(Very close!)";
        }
    }

    /**
     * Inner class for displaying the circle and calculation information.
     * Handles the visual representation of the circle and related measurements.
     */
    private class CircleDisplay extends JPanel {
        /** Current calculation type */
        private String calculationType;
        /** Current input type */
        private String inputType;
        /** Current value */
        private double value;
        /** Flag indicating whether to show the solution */
        private boolean showSolution = false;

        /**
         * Constructs a new CircleDisplay panel.
         * Initializes the display with default settings.
         */
        public CircleDisplay() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(400, 300)); // 明确设置尺寸
        }

        /**
         * Sets the circle information for display.
         *
         * @param calculationType The type of calculation (Area/Circumference)
         * @param inputType The type of input (Radius/Diameter)
         * @param value The input value
         */
        public void setCircleInfo(String calculationType, String inputType, double value) {
            this.calculationType = calculationType;
            this.inputType = inputType;
            this.value = value;
            this.showSolution = false;
            repaint(); // 触发重绘
        }

        /**
         * Sets whether to show the solution on the circle display.
         * @param showSolution true to show the solution, false to hide it
         */
        public void setShowSolution(boolean showSolution) {
            this.showSolution = showSolution;
            repaint();
        }

        /**
         * Paints the circle and related information.
         * Includes:
         * - Circle drawing
         * - Measurement lines
         * - Labels
         * - Formula display
         * - Solution display (when enabled)
         *
         * @param g The Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // ==== 绘制右侧提示信息 ====
            g2d.setColor(new Color(139, 0, 0)); // 深红色文字
            g2d.setFont(new Font("Arial", Font.BOLD, 10));

            // 计算提示信息位置（右侧边距20像素）
            int rightX = getWidth() - 200; // 右侧区域宽度200px
            int infoY = 50; // 起始Y坐标

            // 绘制第一条提示
            String tip1 = "Results retained to one decimal place";
            g2d.drawString(tip1, rightX, infoY);

            // 绘制第二条提示
            String tip2 = "π=3.14";
            g2d.drawString(tip2, rightX, infoY + 30);
            // 绘制第三条提示
            String tip3 = "Allow 0.1 error";
            g2d.drawString(tip3, rightX, infoY + 60);

            // ==== 空值检查：显示友好提示 ====
            if (calculationType == null || inputType == null || value <= 0) {
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 16));
                g2d.drawString("Please select a calculation type to begin...", 50, 50);
                return;
            }

            // ==== 正常绘图逻辑 ====
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // 计算绘制半径（像素）
            int pixelRadius;
            if (inputType.equals("Radius")) {
                pixelRadius = (int) (value * 15); // 半径缩放系数
            } else {
                pixelRadius = (int) (value * 15 / 2); // 直径转半径
            }

            // 确保圆形在面板范围内
            pixelRadius = Math.min(pixelRadius, Math.min(getWidth(), getHeight()) / 2 - 30);

            // 绘制圆形
            g2d.setColor(new Color(135, 206, 250)); // 浅蓝色填充
            g2d.fillOval(centerX - pixelRadius, centerY - pixelRadius, pixelRadius * 2, pixelRadius * 2);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - pixelRadius, centerY - pixelRadius, pixelRadius * 2, pixelRadius * 2);

            // 绘制中心点
            g2d.fillOval(centerX - 3, centerY - 3, 6, 6);

            // 绘制测量线（半径/直径）
            g2d.setColor(Color.RED);
            if (inputType.equals("Radius")) {
                g2d.drawLine(centerX, centerY, centerX + pixelRadius, centerY);
                // 标注半径
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("r = " + value, centerX + pixelRadius / 2 - 15, centerY - 10);
            } else {
                g2d.drawLine(centerX - pixelRadius, centerY, centerX + pixelRadius, centerY);
                // 标注直径
                g2d.drawString("d = " + value, centerX - 15, centerY - 10);
            }

            // 显示题目信息
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String taskInfo = "Calculate the " + calculationType + " given the " + inputType + ": " + value;
            g2d.drawString(taskInfo, 20, 30);

            // ==== 显示公式解析 ====
            if (showSolution) {
                g2d.setColor(new Color(0, 150, 0)); // 深绿色
                String formula = generateFormulaText();
                g2d.drawString(formula, 20, getHeight() - 30);
            }
        }

        // 生成公式文本
        private String generateFormulaText() {
            double answer = calculateCorrectAnswer();
            if (calculationType.equals("Area")) {
                if (inputType.equals("Radius")) {
                    return String.format("A = π × r² = π × %.1f² = %.1f", value, answer);
                } else {
                    double radius = value / 2;
                    return String.format("A = π × (d/2)² = π × (%.1f/2)² = π × %.1f² = %.1f",
                            value, radius, answer);
                }
            } else {
                if (inputType.equals("Radius")) {
                    return String.format("C = 2 × π × r = 2 × π × %.1f = %.1f", value, answer);
                } else {
                    return String.format("C = π × d = π × %.1f = %.1f", value, answer);
                }
            }
        }
    }
}