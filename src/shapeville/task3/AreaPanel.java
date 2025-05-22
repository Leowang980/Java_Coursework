package shapeville.task3;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;
import shapeville.utils.WoodenButton;
import shapeville.utils.ColorConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A panel that handles the area calculation task in Shapeville.
 * This panel provides an interactive interface for users to practice calculating
 * areas of different geometric shapes including rectangles, parallelograms,
 * triangles, and trapeziums.
 *
 * @author Shapeville Team
 * @version 1.0
 */
public class AreaPanel extends JPanel {
    /** The main application instance */
    private final ShapevilleApp mainApp;
    
    /** Layout manager for switching between different views */
    private final CardLayout cardLayout;
    
    /** Main content panel that holds all views */
    private final JPanel contentPanel;

    /** Display component for geometric shapes */
    private ShapeAreaDisplay shapeDisplay;
    
    /** Input field for user's answer */
    private JTextField answerField;
    
    /** Button to submit the answer */
    private WoodenButton submitButton;
    
    /** Label to show feedback messages */
    private JLabel feedbackLabel;
    
    /** Label to show number of attempts */
    private JLabel attemptsLabel;
    
    /** Label to show remaining time */
    private JLabel timerLabel;
    
    /** Progress bar showing completion status */
    private JProgressBar progressBar;

    /** List of available geometric shapes */
    private List<GeometricShape> shapes;
    
    /** Currently selected shape for practice */
    private GeometricShape currentShape;
    
    /** Number of attempts made for current shape */
    private int attempts = 0;
    
    /** Total number of completed shapes */
    private int totalCompleted = 0;
    
    /** Timer for countdown */
    private Timer countdownTimer;
    
    /** Remaining time in seconds */
    private int secondsRemaining;
    
    /** Flag to track if progress has been added */
    private boolean haveAddedProgress = true;
    
    /** Random number generator for shape parameters */
    private final Random random = new Random();
    
    /** Decimal formatter for area calculations */
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    /** Panel for shape selection */
    private JPanel selectionPanel;
    
    /** Array to track completion status of each shape */
    private boolean[] shapeCompleted = new boolean[4];
    
    /** Array to store shape selection panels */
    private JPanel[] shapePanels = new JPanel[4];
    
    /** Timer for updating progress display */
    private Timer updateTimer;

    /**
     * Constructs a new AreaPanel with the specified main application.
     *
     * @param mainApp The main Shapeville application instance
     */
    public AreaPanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        initializeShapes();
        // 过滤已答过的题目
        filterAnsweredShapes();
        setLayout(new BorderLayout());

        // 添加选择界面
        selectionPanel = createSelectionPanel();
        contentPanel
                .add(selectionPanel, "SELECTION");

        JPanel taskPanel = createTaskPanel();
        contentPanel
                .add(taskPanel, "TASK");

        JPanel completionPanel = createCompletionPanel();
        contentPanel
                .add(completionPanel, "COMPLETION");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout
                .show(contentPanel, "SELECTION"); // 默认显示选择界面
    }

    /**
     * Creates the shape selection panel with buttons for each geometric shape.
     *
     * @return JPanel containing the shape selection interface
     */
    private JPanel createSelectionPanel() {
        removeAll();
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ColorConstants.MAIN_BG_COLOR);

        // 标题标签
        JLabel selectionTitleLabel = new JLabel("Select a Shape to Practice");
        selectionTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        selectionTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        selectionTitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(selectionTitleLabel, BorderLayout.NORTH);

        // 进度条面板
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(panel.getBackground());
        
        // Progress label
        JLabel progressLabel = new JLabel(String.format("Completed: %d/4", totalCompleted));
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        progressLabel.setHorizontalAlignment(JLabel.CENTER);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int)((totalCompleted * 100.0) / 4));
        progressBar.setStringPainted(true);
        progressBar.setString(progressBar.getValue() + "%");
        progressPanel.add(progressBar, BorderLayout.CENTER);

        // 添加更新进度的监听器
        if (updateTimer != null) {
            updateTimer.stop();
        }
        updateTimer = new Timer(100, e -> {
            progressLabel.setText(String.format("Completed: %d/4", totalCompleted));
            progressBar.setValue((int)((totalCompleted * 100.0) / 4));
            progressBar.setString(progressBar.getValue() + "%");
        });
        updateTimer.start();

        // 添加面板销毁时的清理
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!panel.isDisplayable() && updateTimer != null) {
                    updateTimer.stop();
                }
            }
        });

        // 形状按钮面板（2x2网格布局）
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(panel.getBackground());

        // 定义四个形状名称
        String[] shapeNames = {"Rectangle", "Parallelogram", "Triangle", "Trapezium"};
        for (int i = 0; i < shapeNames.length; i++) {
            final int index = i;
            
            // Use similar panel approach as in Bonus 1
            JPanel shapePanel = new JPanel(new BorderLayout());
            shapePanel.setBackground(new Color(255, 250, 240));
            shapePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            // Content panel with box layout
            JPanel contentPanel = new JPanel();
            contentPanel.setOpaque(false);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            
            // Shape title with nice font
            JLabel shapeTitleLabel = new JLabel(shapeNames[i]);
            shapeTitleLabel.setFont(new Font("Serif", Font.BOLD, 24));
            shapeTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            shapeTitleLabel.setForeground(shapeCompleted[i] ? Color.GRAY : new Color(101, 67, 33));
            contentPanel.add(Box.createVerticalGlue());
            contentPanel.add(shapeTitleLabel);
            
            // Add "Done" label for completed shapes
            JLabel doneLabel = new JLabel("✓ Done");
            doneLabel.setFont(new Font("Serif", Font.BOLD, 16));
            doneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            doneLabel.setForeground(Color.GRAY);
            doneLabel.setVisible(shapeCompleted[i]);
            contentPanel.add(Box.createVerticalStrut(8));
            contentPanel.add(doneLabel);
            contentPanel.add(Box.createVerticalGlue());
            
            // Create the wooden button to hold the content
            JButton button = new WoodenButton("");
            button.setLayout(new BorderLayout());
            button.setBackground(shapeCompleted[i] ? new Color(220, 220, 220) : new Color(232, 194, 145));
            button.setEnabled(!shapeCompleted[i]);
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.add(contentPanel, BorderLayout.CENTER);
            
            if (!shapeCompleted[i]) {
                button.addActionListener(e -> {
                    startSelectedShape(shapeNames[index].toLowerCase());
                });
            }
            
            shapePanel.add(button, BorderLayout.CENTER);
            buttonPanel.add(shapePanel);
            shapePanels[i] = shapePanel; // Store panel reference
        }

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Starts the practice session for the selected shape.
     *
     * @param shapeName The name of the selected shape
     */
    private void startSelectedShape(String shapeName) {
        // 根据形状名称找到对应的 GeometricShape 对象
        currentShape = shapes.stream()
                .filter(shape -> shape.getName().equals(shapeName))
                .findFirst()
                .orElse(null);

        if (currentShape != null) {
            // 生成随机参数
            generateShapeParameters();

            // 更新图形显示
            shapeDisplay.setShape(currentShape);

            // 切换到任务界面
            cardLayout.show(contentPanel, "TASK");

            // 启动计时器
            startTimer();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid shape selected!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initializes the list of available geometric shapes with their area calculation formulas.
     */
    private void initializeShapes() {
        shapes = new ArrayList<>();

        // Rectangle shape
        shapes.add(new GeometricShape("rectangle",
                (parameters) -> parameters[0] * parameters[1], // Area formula: length * width
                new String[]{"length", "width"}));

        // Parallelogram shape
        shapes.add(new GeometricShape("parallelogram",
                (parameters) -> parameters[0] * parameters[1], // Area formula: base * height
                new String[]{"base", "height"}));

        // Triangle shape
        shapes.add(new GeometricShape("triangle",
                (parameters) -> 0.5 * parameters[0] * parameters[1], // Area formula: 0.5 * base * height
                new String[]{"base", "height"}));

        // Trapezium shape
        shapes.add(new GeometricShape("trapezium",
                (parameters) -> 0.5 * (parameters[0] + parameters[1]) * parameters[2], // Area formula: 0.5 * (a + b) * height
                new String[]{"a", "b", "height"}));
    }

    /**
     * Creates the task panel where users can practice area calculations.
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
        JLabel titleLabel = new JLabel("Task 3: Area Calculation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Center panel with the shape display
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());

        // Shape display panel
        shapeDisplay = new ShapeAreaDisplay();
        shapeDisplay.setPreferredSize(new Dimension(400, 400));
        shapeDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(shapeDisplay, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with input field and submit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panel.getBackground());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(panel.getBackground());

        JLabel promptLabel = new JLabel("Calculate the area and enter your answer:");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        answerField = new JTextField(15);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));

        submitButton = new WoodenButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setForeground(Color.BLACK);
        submitButton.addActionListener(e -> checkAnswer());

        // Add enter key functionality to the text field
        answerField.addActionListener(e -> checkAnswer());

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

        timerLabel = new JLabel("Time: 3:00");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(attemptsLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(timerLabel);

        bottomPanel.add(feedbackPanel, BorderLayout.CENTER);

        // "Back to Selection" button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panel.getBackground());

        WoodenButton backButton = new WoodenButton("Back to Selection");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            System.out.println("totalCompleted: " + totalCompleted);
            if (totalCompleted == 4) {
                mainApp.updateProgress(100.0/6);
                cardLayout.show(contentPanel, "COMPLETION");
            } else {
                shapeDisplay.setShowSolution(false);
                displayNextShape();
                answerField.setEnabled(true);
                submitButton.setEnabled(true);
                cardLayout.show(contentPanel, "SELECTION"); // Return to selection screen
            }       
        });

        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the completion panel shown when all shapes are completed.
     *
     * @return JPanel containing the completion message and navigation buttons
     */
    private JPanel createCompletionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ColorConstants.SUCCESS_BG_COLOR); // 使用木质风格的成功背景色

        JLabel completionLabel = new JLabel("Excellent! You've completed the Area Calculation task!");
        completionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        completionLabel.setHorizontalAlignment(JLabel.CENTER);

        WoodenButton homeButton = new WoodenButton("Return to Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setForeground(Color.BLACK);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (haveAddedProgress) {
                    haveAddedProgress = false;
                    //mainApp.updateProgress(100.0/6);
                }
                mainApp.returnToHome();
            }
        });

        WoodenButton nextTaskButton = new WoodenButton("Go to Task 4: Circle Calculations");
        nextTaskButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTaskButton.setForeground(Color.BLACK);
        nextTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (haveAddedProgress) {
                    haveAddedProgress = false;
                    //mainApp.updateProgress(100.0/6);
                }
                mainApp.startTask4();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(homeButton);
        buttonPanel.add(nextTaskButton);

        panel.add(completionLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 新增：全部完成时加主进度
        //mainApp.addTask3Progress();
        return panel;
    }

    /**
     * Starts the countdown timer for the current task.
     */
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

    /**
     * Handles the expiration of the countdown timer.
     */
    private void timeExpired() {
        feedbackLabel.setText("Time's up! The correct answer is: " + df.format(currentShape.calculateArea()));
        feedbackLabel.setForeground(Color.RED);

        // Disable input
        answerField.setEnabled(false);
        submitButton.setEnabled(false);
        totalCompleted++;
        // Enable after a short delay and move to next shape
//        Timer timer = new Timer(2000, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                currentShapeIndex++;
//                totalCompleted++;
//                displayNextShape();
//                answerField.setEnabled(true);
//                submitButton.setEnabled(true);
//            }
//        });
//        timer.setRepeats(false);
//        timer.start();
    }

    /**
     * Displays the next shape for practice.
     */
    private void displayNextShape() {
        // 重置尝试次数
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);

        // 清空反馈信息和输入框
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.requestFocus(); // 聚焦输入框

        // 停止当前计时器
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        // 直接返回形状选择界面
        cardLayout.show(contentPanel, "SELECTION");

        // 更新全局进度条（如果需要）
        //mainApp.updateProgress(0); // 重置为0或保持当前进度
    }

    /**
     * Generates random parameters for the current shape.
     */
    private void generateShapeParameters() {
        double[] parameters = new double[currentShape.getParameterNames().length];

        // Generate random values between 1 and 20 for each parameter
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = 1 + random.nextInt(20);
        }

        currentShape.setParameters(parameters);
    }

    /**
     * Checks the user's answer against the correct area calculation.
     */
    private void checkAnswer() {
        String userAnswer = answerField.getText().trim();

        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }

        double userValue;
        try {
            userValue = Double.parseDouble(userAnswer);
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid number!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }

        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);

        double correctAnswer = currentShape.calculateArea();
        double tolerance = 0.1; // Allow for small rounding differences

        if (Math.abs(userValue - correctAnswer) <= tolerance) {
            // Correct answer
            if (countdownTimer != null) {
                countdownTimer.stop();
            }

            int score = ScoreManager.calculateScore(false, attempts); // Basic level
            String feedback = ScoreManager.getFeedbackMessage(score);

            feedbackLabel.setText("Correct! " + feedback + " +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0)); // Dark green

            // Update score in main app
            mainApp.updateScore(score);

            // 记录已答
            ScoreManager.markTask3Answered(currentShape.getName());
            //totalCompleted = ScoreManager.getTask3Progress();

            // 更新完成状态
            int shapeIndex = getShapeIndex(currentShape.getName());
            if (shapeIndex != -1) {
                shapeCompleted[shapeIndex] = true;
                totalCompleted++;
                
                // 更新进度条
                progressBar.setValue((int)((totalCompleted * 100.0) / 4));
                progressBar.setString(progressBar.getValue() + "%");
                
                // 更新按钮状态
                JPanel shapePanel = shapePanels[shapeIndex];
                JButton button = (JButton) shapePanel.getComponent(0);
                button.setEnabled(false);
                button.setBackground(new Color(220, 220, 220));
                
                // 显示完成标签
                JPanel contentPanel = (JPanel) button.getComponent(0);
                JLabel shapeTitleLabel = (JLabel) contentPanel.getComponent(1);
                shapeTitleLabel.setForeground(Color.GRAY);
                
                // 强制重绘面板
                shapePanel.revalidate();
                shapePanel.repaint();
            }

            // Disable input until next shape
            System.out.println("disable input");
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
            ScoreManager.markTask3Answered(currentShape.getName());
            // 返回选择界面
            answerField.setEnabled(false);
        } else {
            // Wrong answer
            feedbackLabel.setText("That's not correct. Try again!");
            feedbackLabel.setForeground(Color.RED);

            // If max attempts reached, show correct answer and move to next shape
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                if (countdownTimer != null) {
                    countdownTimer.stop();
                }

                feedbackLabel.setText("The correct answer is: " + df.format(correctAnswer));

                // Show the solution
                shapeDisplay.setShowSolution(true);

                // Disable input until next shape
                answerField.setEnabled(false);
                submitButton.setEnabled(false);

                // 记录已答
                ScoreManager.markTask3Answered(currentShape.getName());
                //totalCompleted = ScoreManager.getTask3Progress();

                // 更新完成状态
                int shapeIndex = getShapeIndex(currentShape.getName());
                if (shapeIndex != -1) {
                    shapeCompleted[shapeIndex] = true;
                    totalCompleted++;
                    
                    // 更新进度条
                    progressBar.setValue((int)((totalCompleted * 100.0) / 4));
                    progressBar.setString(progressBar.getValue() + "%");
                    
                    // 更新按钮状态
                    JPanel shapePanel = shapePanels[shapeIndex];
                    JButton button = (JButton) shapePanel.getComponent(0);
                    button.setEnabled(false);
                    button.setBackground(new Color(220, 220, 220));
                    
                    
                    // 显示完成标签
                    JPanel contentPanel = (JPanel) button.getComponent(0);
                    JLabel shapeTitleLabel = (JLabel) contentPanel.getComponent(1);
                    shapeTitleLabel.setForeground(Color.GRAY);
                    
                    JLabel doneLabel = (JLabel) contentPanel.getComponent(2);
                    doneLabel.setVisible(true);
                    doneLabel.setForeground(Color.GRAY);
                    doneLabel.setText("Done");
                    doneLabel.setFont(new Font("Serif", Font.BOLD, 16));
                }
                shapeDisplay.setShowSolution(false);
                answerField.setEnabled(true);
                ScoreManager.markTask3Answered(currentShape.getName());

            }
        }
    }

    /**
     * Gets the index of a shape in the shapes array.
     *
     * @param shapeName The name of the shape to find
     * @return The index of the shape, or -1 if not found
     */
    private int getShapeIndex(String shapeName) {
        String[] shapeNames = {"rectangle", "parallelogram", "triangle", "trapezium"};
        for (int i = 0; i < shapeNames.length; i++) {
            if (shapeNames[i].equals(shapeName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Filters out shapes that have already been answered.
     */
    private void filterAnsweredShapes() {
        List<GeometricShape> unanswered = new ArrayList<>();
        for (GeometricShape shape : shapes) {
            if (!ScoreManager.isTask3Answered(shape.getName())) {
                unanswered.add(shape);
            }
        }
        shapes = unanswered;
    }

    /**
     * Inner class for displaying geometric shapes with their parameters.
     */
    private class ShapeAreaDisplay extends JPanel {
        /** The current shape to display */
        private GeometricShape shape;
        
        /** Flag to control solution display */
        private boolean showSolution = false;

        /**
         * Constructs a new ShapeAreaDisplay.
         */
        public ShapeAreaDisplay() {
            setBackground(Color.WHITE);
        }

        /**
         * Sets the shape to display.
         *
         * @param shape The geometric shape to display
         */
        public void setShape(GeometricShape shape) {
            this.shape = shape;
            repaint();
        }

        /**
         * Sets whether to show the solution.
         *
         * @param showSolution True to show the solution, false otherwise
         */
        public void setShowSolution(boolean showSolution) {
            this.showSolution = showSolution;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (shape == null) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Shape: " + shape.getName(), 20, 30);

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            double[] parameters = shape.getParameters();
            String[] paramNames = shape.getParameterNames();

            for (int i = 0; i < parameters.length; i++) {
                g2d.drawString(paramNames[i] + " = " + parameters[i], 20, 60 + i * 20);
            }

            g2d.setStroke(new BasicStroke(2));

            // Draw based on the shape type
            switch (shape.getName()) {
                case "rectangle":
                    drawRectangle(g2d, centerX, centerY, parameters);
                    break;
                case "parallelogram":
                    drawParallelogram(g2d, centerX, centerY, parameters);
                    break;
                case "triangle":
                    drawTriangle(g2d, centerX, centerY, parameters);
                    break;
                case "trapezium":
                    drawTrapezium(g2d, centerX, centerY, parameters);
                    break;
            }

            // Show solution if required
            if (showSolution) {
                g2d.setColor(new Color(0, 150, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String solution = "Area = " + df.format(shape.calculateArea());

                // Draw solution with formula
                StringBuilder formula = new StringBuilder("Formula: ");
                switch (shape.getName()) {
                    case "rectangle":
                        formula.append("length × width");
                        break;
                    case "parallelogram":
                        formula.append("base × height");
                        break;
                    case "triangle":
                        formula.append("(1/2) × base × height");
                        break;
                    case "trapezium":
                        formula.append("(1/2) × (a + b) × height");
                        break;
                }

                g2d.drawString(solution, getWidth() - 200, getHeight() - 60);
                g2d.drawString(formula.toString(), getWidth() - 250, getHeight() - 30);
            }
        }

        /**
         * Draws a rectangle with its dimensions.
         *
         * @param g2d The graphics context
         * @param centerX The x-coordinate of the center
         * @param centerY The y-coordinate of the center
         * @param parameters The rectangle's parameters
         */
        private void drawRectangle(Graphics2D g2d, int centerX, int centerY, double[] parameters) {
            int length = (int) (parameters[0] * 10);
            int width = (int) (parameters[1] * 10);

            int x = centerX - length / 2;
            int y = centerY - width / 2;

            g2d.setColor(new Color(135, 206, 250)); // Light sky blue
            g2d.fillRect(x, y, length, width);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, length, width);

            // Draw dimension lines and labels
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            // Length
            g2d.drawLine(x, y - 15, x + length, y - 15);
            g2d.drawLine(x, y - 10, x, y - 20);
            g2d.drawLine(x + length, y - 10, x + length, y - 20);
            g2d.drawString("Length = " + parameters[0], x + length / 3, y - 20);

            // Width
            g2d.drawLine(x - 15, y, x - 15, y + width);
            g2d.drawLine(x - 10, y, x - 20, y);
            g2d.drawLine(x - 10, y + width, x - 20, y + width);
            g2d.drawString("Width = " + parameters[1], x - 50, y + width / 2);
        }

        /**
         * Draws a parallelogram with its dimensions.
         *
         * @param g2d The graphics context
         * @param centerX The x-coordinate of the center
         * @param centerY The y-coordinate of the center
         * @param parameters The parallelogram's parameters
         */
        private void drawParallelogram(Graphics2D g2d, int centerX, int centerY, double[] parameters) {
            int base = (int) (parameters[0] * 10);
            int height = (int) (parameters[1] * 10);
            int skew = 30; // Skew amount for parallelogram

            int x = centerX - base / 2 - skew / 2;
            int y = centerY - height / 2;

            int[] xPoints = {x, x + base, x + base + skew, x + skew};
            int[] yPoints = {y + height, y + height, y, y};

            g2d.setColor(new Color(221, 160, 221)); // Plum
            g2d.fillPolygon(xPoints, yPoints, 4);

            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, 4);

            // Draw dimension lines and labels
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            // Base
            g2d.drawLine(x, y + height + 15, x + base, y + height + 15);
            g2d.drawLine(x, y + height + 10, x, y + height + 20);
            g2d.drawLine(x + base, y + height + 10, x + base, y + height + 20);
            g2d.drawString("Base = " + parameters[0], x + base / 3, y + height + 30);

            // Height
            g2d.drawLine(x + base + skew + 15, y, x + base + skew + 15, y + height);
            g2d.drawLine(x + base + skew + 10, y, x + base + skew + 20, y);
            g2d.drawLine(x + base + skew + 10, y + height, x + base + skew + 20, y + height);
            g2d.drawString("Height = " + parameters[1], x + base + skew + 20, y + height / 2);

            // Draw a dashed height line
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3}, 0));
            g2d.drawLine(x + base, y + height, x + base, y);
            g2d.setStroke(new BasicStroke(2));
        }

        /**
         * Draws a triangle with its dimensions.
         *
         * @param g2d The graphics context
         * @param centerX The x-coordinate of the center
         * @param centerY The y-coordinate of the center
         * @param parameters The triangle's parameters
         */
        private void drawTriangle(Graphics2D g2d, int centerX, int centerY, double[] parameters) {
            int base = (int) (parameters[0] * 10);
            int height = (int) (parameters[1] * 10);

            int x = centerX - base / 2;
            int y = centerY + height / 2;

            int[] xPoints = {x, x + base, centerX};
            int[] yPoints = {y, y, y - height};

            g2d.setColor(new Color(144, 238, 144)); // Light green
            g2d.fillPolygon(xPoints, yPoints, 3);

            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, 3);

            // Draw dimension lines and labels
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            // Base
            g2d.drawLine(x, y + 15, x + base, y + 15);
            g2d.drawLine(x, y + 10, x, y + 20);
            g2d.drawLine(x + base, y + 10, x + base, y + 20);
            g2d.drawString("Base = " + parameters[0], x + base / 3, y + 30);

            // Height
            g2d.drawLine(centerX, y, centerX, y - height);
            g2d.drawString("Height = " + parameters[1], centerX + 5, y - height / 2);

            // Draw a dot at the apex
            g2d.fillOval(centerX - 3, y - height - 3, 6, 6);
        }

        /**
         * Draws a trapezium with its dimensions.
         *
         * @param g2d The graphics context
         * @param centerX The x-coordinate of the center
         * @param centerY The y-coordinate of the center
         * @param parameters The trapezium's parameters
         */
        private void drawTrapezium(Graphics2D g2d, int centerX, int centerY, double[] parameters) {
            int a = (int) (parameters[0] * 10); // Top side
            int b = (int) (parameters[1] * 10); // Bottom side
            int height = (int) (parameters[2] * 10);

            int x = centerX - b / 2;
            int y = centerY + height / 2;

            int topLeftX = centerX - a / 2;
            int topLeftY = y - height;

            int[] xPoints = {x, x + b, topLeftX + a, topLeftX};
            int[] yPoints = {y, y, topLeftY, topLeftY};

            g2d.setColor(new Color(255, 182, 193)); // Light pink
            g2d.fillPolygon(xPoints, yPoints, 4);

            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, 4);

            // Draw dimension lines and labels
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            // Bottom side (b)
            g2d.drawLine(x, y + 15, x + b, y + 15);
            g2d.drawLine(x, y + 10, x, y + 20);
            g2d.drawLine(x + b, y + 10, x + b, y + 20);
            g2d.drawString("b = " + parameters[1], x + b / 3, y + 30);

            // Top side (a)
            g2d.drawLine(topLeftX, topLeftY - 15, topLeftX + a, topLeftY - 15);
            g2d.drawLine(topLeftX, topLeftY - 10, topLeftX, topLeftY - 20);
            g2d.drawLine(topLeftX + a, topLeftY - 10, topLeftX + a, topLeftY - 20);
            g2d.drawString("a = " + parameters[0], topLeftX + a / 3, topLeftY - 20);

            // Height
            g2d.drawLine(x - 15, y, x - 15, topLeftY);
            g2d.drawLine(x - 10, y, x - 20, y);
            g2d.drawLine(x - 10, topLeftY, x - 20, topLeftY);
            g2d.drawString("Height = " + parameters[2], x - 70, centerY);
        }
    }

    /**
     * Represents a geometric shape with its area calculation formula.
     */
    public static class GeometricShape {
        /** The name of the shape */
        private final String name;
        
        /** The calculator for the shape's area */
        private AreaCalculator calculator;
        
        /** The names of the shape's parameters */
        private final String[] parameterNames;
        
        /** The current values of the shape's parameters */
        private double[] parameters;

        /**
         * Constructs a new GeometricShape.
         *
         * @param name The name of the shape
         * @param calculator The area calculator for the shape
         * @param parameterNames The names of the shape's parameters
         */
        public GeometricShape(String name, AreaCalculator calculator, String[] parameterNames) {
            this.name = name;
            this.calculator = calculator;
            this.parameterNames = parameterNames;
            this.parameters = new double[parameterNames.length];
        }

        /**
         * Gets the name of the shape.
         *
         * @return The shape's name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the current parameter values.
         *
         * @return Array of parameter values
         */
        public double[] getParameters() {
            return parameters;
        }

        /**
         * Sets the parameter values.
         *
         * @param parameters The new parameter values
         */
        public void setParameters(double[] parameters) {
            if (parameters.length == this.parameterNames.length) {
                this.parameters = parameters;
            }
        }

        /**
         * Gets the names of the shape's parameters.
         *
         * @return Array of parameter names
         */
        public String[] getParameterNames() {
            return parameterNames;
        }

        /**
         * Calculates the area of the shape.
         *
         * @return The calculated area
         */
        public double calculateArea() {
            return calculator.calculate(parameters);
        }
    }

    /**
     * Functional interface for calculating the area of a shape.
     */
    @FunctionalInterface
    public interface AreaCalculator {
        /**
         * Calculates the area of a shape.
         *
         * @param parameters The parameters needed for the calculation
         * @return The calculated area
         */
        double calculate(double[] parameters);
    }
}