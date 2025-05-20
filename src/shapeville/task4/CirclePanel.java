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
import java.awt.event.KeyEvent; // 新增此行

import java.util.List;
import java.util.ArrayList;

public class CirclePanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private CircleDisplay circleDisplay;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel attemptsLabel;
    private JLabel progressLabel;
    private JLabel timerLabel;

    private String currentCalculationType; // "Area" or "Circumference"
    private String currentInputType; // "Radius" or "Diameter"
    private double currentValue; // The radius or diameter value
    private int attempts = 0;
    private int totalCalculations = 0;
    private int completedCalculations = 0;
    private Timer countdownTimer;
    private Timer nextStepTimer; // 统一管理所有定时器
    private int secondsRemaining;
    private int totalTimeUsed = 0; // 新增成员变量

    private final Random random = new Random();
    private final DecimalFormat df = new DecimalFormat("#.#");

    // Constants for calculations
    private static final double PI = 3.14;
    private static final int TOTAL_CALCULATIONS = 2; // 2 types *// 2 input methods * 2 times each

    //    private List<String> requiredInputTypes = new ArrayList<>();
    private Set<String> completedTypes = new HashSet<>();
    // 新增组件
    private JPanel selectionPanel;
    private ButtonGroup calculationTypeGroup;
    private JRadioButton areaRadioButton;
    private JRadioButton circumferenceRadioButton;

    private boolean lastAnswerCorrect = false;
    private String nextCalculationType;// 控制下一个计算类型
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

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // Light blue background

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
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

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        submitButton.setForeground(Color.WHITE);
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

        JButton nextButton = new JButton("Next Calculation");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextButton.setForeground(Color.WHITE);
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

    private JPanel createCompletionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 255, 240)); // 浅绿色背景

        // 完成提示标签
        JLabel completionLabel = new JLabel("<html><center>Fantastic! You've completed all<br>" + currentCalculationType + " calculations!</center></html>");
        completionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        completionLabel.setHorizontalAlignment(JLabel.CENTER);
        completionLabel.setForeground(new Color(0, 100, 0)); // 深绿色文字

        // 统计信息面板
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statsPanel.setBackground(panel.getBackground());

        JLabel scoreLabel = new JLabel("Total Score: " + mainApp.getCurrentScore());
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel timeLabel = new JLabel("Average Time: " + calculateAverageTime() + "s per question");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setHorizontalAlignment(JLabel.CENTER);

        statsPanel.add(scoreLabel);
        statsPanel.add(timeLabel);

        // 返回主页按钮
        JButton homeButton = new JButton("Return to Home (Alt+H)");
        homeButton.setMnemonic(KeyEvent.VK_H); // 无障碍键盘快捷键
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setBackground(new Color(70, 130, 180)); // 钢蓝色
        homeButton.setForeground(Color.WHITE);
        homeButton.addActionListener(e -> {
            if (countdownTimer != null) countdownTimer.stop(); // 停止计时器
//            requiredInputTypes.clear(); // 清空待处理输入类型
            mainApp.returnToHome();
        });

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(homeButton);

        // 布局组装
        panel.add(completionLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // 新增辅助方法 - 计算平均用时
    private String calculateAverageTime() {
        if (completedCalculations == 0) return "0.0"; // 改为一位小数
        return df.format((double) totalTimeUsed / completedCalculations); // 直接使用 df 格式化
    }

    private void updateProgress() {
        int progress = completedTypes.size() * 50;
        progressLabel.setText("Progress: " + progress + "%");
        mainApp.updateProgress(progress); // 更新主界面进度条
    }

    private void resetStates() {
        completedTypes.clear();
//        requiredInputTypes.clear();
        progressLabel.setText("Progress: 0%");
        mainApp.updateProgress(0);
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
            int progress = 100/6;
            mainApp.updateProgress(progress);
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
        int progress = ScoreManager.calculateProgress(completedCalculations, totalCalculations);
        mainApp.updateProgress(progress);

        // 启动计时器
        startTimer();
    }

    private void createSelectionPanel() {
        selectionPanel = new JPanel(new BorderLayout(10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        selectionPanel.setBackground(new Color(240, 248, 255));

        // 标题
        JLabel titleLabel = new JLabel("Choose Calculation Type");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        selectionPanel.add(titleLabel, BorderLayout.NORTH);

        // 单选按钮面板
        JPanel radioPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        radioPanel.setBackground(selectionPanel.getBackground());

        calculationTypeGroup = new ButtonGroup();

        areaRadioButton = new JRadioButton("Area");
        areaRadioButton.setFont(new Font("Arial", Font.PLAIN, 16));
        areaRadioButton.setBackground(selectionPanel.getBackground());

        circumferenceRadioButton = new JRadioButton("Circumference");
        circumferenceRadioButton.setFont(new Font("Arial", Font.PLAIN, 16));
        circumferenceRadioButton.setBackground(selectionPanel.getBackground());

        calculationTypeGroup.add(areaRadioButton);
        calculationTypeGroup.add(circumferenceRadioButton);

        // 添加无障碍支持
        areaRadioButton.setMnemonic(KeyEvent.VK_A);
        circumferenceRadioButton.setMnemonic(KeyEvent.VK_C);

        radioPanel.add(areaRadioButton);
        radioPanel.add(circumferenceRadioButton);

        // 确认按钮
        JButton confirmButton = new JButton("Start Calculations");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        // 在 createSelectionPanel() 方法中修改确认按钮的逻辑
        confirmButton.addActionListener(e -> {
            if (!areaRadioButton.isSelected() && !circumferenceRadioButton.isSelected()) {
                JOptionPane.showMessageDialog(this, "Please select a calculation type!");
                return;
            }

            // 设置新的计算类型
            currentCalculationType = areaRadioButton.isSelected() ? "Area" : "Circumference";

            // 初始化输入类型列表（每次选择后重新加载）
//            requiredInputTypes.clear();
//            requiredInputTypes.add("Radius");
//            requiredInputTypes.add("Diameter");
            // 新增：强制要求两种计算类型
            completedTypes.clear();
            totalCalculations = 2; // 强制总任务数为2
            progressLabel.setText("Progress: 0/" + totalCalculations);

            // 跳转到任务界面
            cardLayout.show(contentPanel, "TASK");
            nextCalculationType = currentCalculationType; // 初始为用户选择的类型
            // 生成第一题（类型为用户选择的初始类型）
            generateNextCalculation();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(selectionPanel.getBackground());
        buttonPanel.add(confirmButton);

        selectionPanel.add(radioPanel, BorderLayout.CENTER);
        selectionPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

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
            mainApp.updateScore(score);
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

    // Inner class for drawing the circle and displaying calculation information
    private class CircleDisplay extends JPanel {
        private String calculationType;
        private String inputType;
        private double value;
        private boolean showSolution = false;

        public CircleDisplay() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(400, 300)); // 明确设置尺寸
        }

        public void setCircleInfo(String calculationType, String inputType, double value) {
            this.calculationType = calculationType;
            this.inputType = inputType;
            this.value = value;
            this.showSolution = false;
            repaint(); // 触发重绘
        }

        public void setShowSolution(boolean showSolution) {
            this.showSolution = showSolution;
            repaint();
        }

        public String getFormulaText() {
            if (calculationType == null || inputType == null) {
                return "";
            }
            if (calculationType.equals("Area")) {
                return inputType.equals("Radius") ?
                        "A = π × r²" :
                        "A = π × (d/2)²";
            } else {
                return inputType.equals("Radius") ?
                        "C = 2 × π × r" :
                        "C = π × d";
            }
        }

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