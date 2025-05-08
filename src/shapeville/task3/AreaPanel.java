package shapeville.task3;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AreaPanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    private ShapeAreaDisplay shapeDisplay;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel attemptsLabel;
    private JLabel progressLabel;
    private JLabel timerLabel;
    
    private List<GeometricShape> shapes;
    private GeometricShape currentShape;
    private int currentShapeIndex = 0;
    private int attempts = 0;
    private int totalCompleted = 0;
    private Timer countdownTimer;
    private int secondsRemaining;
    
    private Random random = new Random();
    private DecimalFormat df = new DecimalFormat("#.##");
    
    public AreaPanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        
        // Initialize the shapes
        initializeShapes();
        
        // Shuffle the shapes to randomize the order
        Collections.shuffle(shapes);
        
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
        
        // Display the first shape
        displayNextShape();
    }
    
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
    
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
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
        
        progressLabel = new JLabel("Progress: 0/" + shapes.size());
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
        
        JButton nextButton = new JButton("Next Shape");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdownTimer != null) {
                    countdownTimer.stop();
                }
                displayNextShape();
            }
        });
        
        buttonPanel.add(nextButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCompletionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 255, 240)); // Light green background
        
        JLabel completionLabel = new JLabel("Excellent! You've completed the Area Calculation task!");
        completionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        completionLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton homeButton = new JButton("Return to Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setBackground(new Color(70, 130, 180)); // Steel blue
        homeButton.setForeground(Color.WHITE);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.returnToHome();
            }
        });
        
        JButton nextTaskButton = new JButton("Go to Task 4: Circle Calculations");
        nextTaskButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTaskButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextTaskButton.setForeground(Color.WHITE);
        nextTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.startTask4();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(homeButton);
        buttonPanel.add(nextTaskButton);
        
        panel.add(completionLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
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
        feedbackLabel.setText("Time's up! The correct answer is: " + df.format(currentShape.calculateArea()));
        feedbackLabel.setForeground(Color.RED);
        
        // Disable input
        answerField.setEnabled(false);
        submitButton.setEnabled(false);
        
        // Enable after a short delay and move to next shape
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentShapeIndex++;
                totalCompleted++;
                displayNextShape();
                answerField.setEnabled(true);
                submitButton.setEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void displayNextShape() {
        // Reset attempts
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);
        
        // Clear feedback and answer field
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.requestFocus();
        
        // Check if we've gone through all shapes
        if (currentShapeIndex >= shapes.size()) {
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }
        
        // Get the current shape
        currentShape = shapes.get(currentShapeIndex);
        
        // Generate random parameters for the shape
        generateShapeParameters();
        
        // Update shape display
        shapeDisplay.setShape(currentShape);
        
        // Update progress
        progressLabel.setText("Progress: " + totalCompleted + "/" + shapes.size());
        
        // Update progress bar in main app
        int progress = ScoreManager.calculateProgress(totalCompleted, shapes.size());
        mainApp.updateProgress(progress);
        
        // Start the timer
        startTimer();
    }
    
    private void generateShapeParameters() {
        double[] parameters = new double[currentShape.getParameterNames().length];
        
        // Generate random values between 1 and 20 for each parameter
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = 1 + random.nextInt(20);
        }
        
        currentShape.setParameters(parameters);
    }
    
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
            
            // Move to next shape
            currentShapeIndex++;
            totalCompleted++;
            
            // Disable input until next shape
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
            
            // Enable after a short delay
            Timer timer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayNextShape();
                    answerField.setEnabled(true);
                    submitButton.setEnabled(true);
                }
            });
            timer.setRepeats(false);
            timer.start();
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
                
                // Enable after a short delay
                Timer timer = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        shapeDisplay.setShowSolution(false);
                        currentShapeIndex++;
                        totalCompleted++;
                        displayNextShape();
                        answerField.setEnabled(true);
                        submitButton.setEnabled(true);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    // Inner class to display geometric shapes with their parameters
    private class ShapeAreaDisplay extends JPanel {
        private GeometricShape shape;
        private boolean showSolution = false;
        
        public ShapeAreaDisplay() {
            setBackground(Color.WHITE);
        }
        
        public void setShape(GeometricShape shape) {
            this.shape = shape;
            repaint();
        }
        
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
    
    // Inner class to represent a geometric shape
    public static class GeometricShape {
        private final String name;
        private AreaCalculator calculator;
        private final String[] parameterNames;
        private double[] parameters;
        
        public GeometricShape(String name, AreaCalculator calculator, String[] parameterNames) {
            this.name = name;
            this.calculator = calculator;
            this.parameterNames = parameterNames;
            this.parameters = new double[parameterNames.length];
        }
        
        public String getName() {
            return name;
        }
        
        public double[] getParameters() {
            return parameters;
        }
        
        public void setParameters(double[] parameters) {
            if (parameters.length == this.parameterNames.length) {
                this.parameters = parameters;
            }
        }
        
        public String[] getParameterNames() {
            return parameterNames;
        }
        
        public double calculateArea() {
            return calculator.calculate(parameters);
        }
    }
    
    // Functional interface for area calculation
    @FunctionalInterface
    public interface AreaCalculator {
        double calculate(double[] parameters);
    }
} 