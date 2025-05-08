package shapeville.task4;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;

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
    private int secondsRemaining;
    
    private final Random random = new Random();
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    // Constants for calculations
    private static final double PI = Math.PI;
    private static final int TOTAL_CALCULATIONS = 8; // 2 types * 2 input methods * 2 times each
    
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
        generateNextCalculation();
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
        panel.setBackground(new Color(240, 255, 240)); // Light green background
        
        JLabel completionLabel = new JLabel("Fantastic! You've completed the Circle Calculations task!");
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
        
        JButton nextTaskButton = new JButton("Go to Bonus 1: Compound Shapes");
        nextTaskButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTaskButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextTaskButton.setForeground(Color.WHITE);
        nextTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.startBonus1();
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
                completedCalculations++;
                generateNextCalculation();
                answerField.setEnabled(true);
                submitButton.setEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void generateNextCalculation() {
        // Reset attempts
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);
        
        // Clear feedback and answer field
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.requestFocus();
        
        // Check if we've gone through all calculations
        if (completedCalculations >= totalCalculations) {
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }
        
        // Generate a random calculation
        String[] calculationTypes = {"Area", "Circumference"};
        String[] inputTypes = {"Radius", "Diameter"};
        
        currentCalculationType = calculationTypes[random.nextInt(calculationTypes.length)];
        currentInputType = inputTypes[random.nextInt(inputTypes.length)];
        
        // Generate a random value between 1 and 10 for radius or diameter
        currentValue = 1 + random.nextInt(10);
        
        // Update the circle display
        circleDisplay.setCircleInfo(currentCalculationType, currentInputType, currentValue);
        
        // Update progress
        progressLabel.setText("Progress: " + completedCalculations + "/" + totalCalculations);
        
        // Update progress bar in main app
        int progress = ScoreManager.calculateProgress(completedCalculations, totalCalculations);
        mainApp.updateProgress(progress);
        
        // Start the timer
        startTimer();
    }
    
    private double calculateCorrectAnswer() {
        if (currentCalculationType.equals("Area")) {
            if (currentInputType.equals("Radius")) {
                return PI * Math.pow(currentValue, 2);
            } else { // Diameter
                double radius = currentValue / 2;
                return PI * Math.pow(radius, 2);
            }
        } else { // Circumference
            if (currentInputType.equals("Radius")) {
                return 2 * PI * currentValue;
            } else { // Diameter
                return PI * currentValue;
            }
        }
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
        
        double correctAnswer = calculateCorrectAnswer();
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
            
            // Show the correct answer and formula
            circleDisplay.setShowSolution(true);
            
            // Move to next calculation
            completedCalculations++;
            
            // Disable input until next calculation
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
            
            // Enable after a short delay
            Timer timer = new Timer(2500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    circleDisplay.setShowSolution(false);
                    generateNextCalculation();
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
            
            // If max attempts reached, show correct answer and move to next calculation
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                if (countdownTimer != null) {
                    countdownTimer.stop();
                }
                
                feedbackLabel.setText("The correct answer is: " + df.format(correctAnswer));
                
                // Show the solution
                circleDisplay.setShowSolution(true);
                
                // Disable input until next calculation
                answerField.setEnabled(false);
                submitButton.setEnabled(false);
                
                // Enable after a short delay
                Timer timer = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        circleDisplay.setShowSolution(false);
                        completedCalculations++;
                        generateNextCalculation();
                        answerField.setEnabled(true);
                        submitButton.setEnabled(true);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
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
        }
        
        public void setCircleInfo(String calculationType, String inputType, double value) {
            this.calculationType = calculationType;
            this.inputType = inputType;
            this.value = value;
            this.showSolution = false;
            repaint();
        }
        
        public void setShowSolution(boolean showSolution) {
            this.showSolution = showSolution;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (calculationType == null || inputType == null) return;
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            // Determine radius for drawing (in pixels)
            int pixelRadius;
            if (inputType.equals("Radius")) {
                pixelRadius = (int) (value * 15); // Scale factor to make it visible
            } else { // Diameter
                pixelRadius = (int) (value * 15 / 2); // Convert diameter to radius and scale
            }
            
            // Make sure circle fits in the panel
            pixelRadius = Math.min(pixelRadius, Math.min(getWidth(), getHeight()) / 2 - 30);
            
            // Draw the circle
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(135, 206, 250)); // Light sky blue
            g2d.fillOval(centerX - pixelRadius, centerY - pixelRadius, pixelRadius * 2, pixelRadius * 2);
            
            g2d.setColor(Color.BLACK);
            g2d.drawOval(centerX - pixelRadius, centerY - pixelRadius, pixelRadius * 2, pixelRadius * 2);
            
            // Draw center point
            g2d.fillOval(centerX - 3, centerY - 3, 6, 6);
            
            // Draw measurement line (radius or diameter)
            if (inputType.equals("Radius")) {
                // Draw radius line
                g2d.setColor(Color.RED);
                g2d.drawLine(centerX, centerY, centerX + pixelRadius, centerY);
                
                // Label the radius
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("r = " + value, centerX + pixelRadius / 2 - 15, centerY - 10);
            } else {
                // Draw diameter line
                g2d.setColor(Color.RED);
                g2d.drawLine(centerX - pixelRadius, centerY, centerX + pixelRadius, centerY);
                
                // Label the diameter
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("d = " + value, centerX - 15, centerY - 10);
            }
            
            // Draw task information
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String taskInfo = "Calculate the " + calculationType + " given the " + inputType + ": " + value;
            g2d.drawString(taskInfo, 20, 30);
            
            // Show solution if required
            if (showSolution) {
                g2d.setColor(new Color(0, 150, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                
                double answer = calculateCorrectAnswer();
                String solution = calculationType + " = " + df.format(answer);
                
                // Draw solution with formula
                String formula = "Formula: ";
                if (calculationType.equals("Area")) {
                    if (inputType.equals("Radius")) {
                        formula += "A = π × r² = π × " + value + "² = " + df.format(answer);
                    } else { // Diameter
                        double radius = value / 2;
                        formula += "A = π × (d/2)² = π × (" + value + "/2)² = π × " + df.format(radius) + "² = " + df.format(answer);
                    }
                } else { // Circumference
                    if (inputType.equals("Radius")) {
                        formula += "C = 2 × π × r = 2 × π × " + value + " = " + df.format(answer);
                    } else { // Diameter
                        formula += "C = π × d = π × " + value + " = " + df.format(answer);
                    }
                }
                
                g2d.drawString(solution, 20, getHeight() - 60);
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString(formula, 20, getHeight() - 30);
            }
        }
    }
} 