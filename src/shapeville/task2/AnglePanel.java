package shapeville.task2;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnglePanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    private AngleDisplay angleDisplay;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel attemptsLabel;
    private JLabel progressLabel;
    private JLabel currentAngleLabel;
    
    private List<Angle> angles;
    private Angle currentAngle;
    private int currentAngleIndex = 0;
    private int attempts = 0;
    private int totalCompleted = 0;
    
    public AnglePanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        
        // Initialize angles
        initializeAngles();
        
        // Shuffle the angles to randomize the order
        Collections.shuffle(angles);
        
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
        
        // Display the first angle
        displayNextAngle();
    }
    
    private void initializeAngles() {
        angles = new ArrayList<>();
        
        // Add various angles (in degrees) with their types
        // Acute angles: less than 90°
        angles.add(new Angle(30, "acute"));
        angles.add(new Angle(45, "acute"));
        angles.add(new Angle(60, "acute"));
        
        // Right angle: equal to 90°
        angles.add(new Angle(90, "right"));
        
        // Obtuse angles: greater than 90° and less than 180°
        angles.add(new Angle(120, "obtuse"));
        angles.add(new Angle(150, "obtuse"));
        
        // Straight angle: equal to 180°
        angles.add(new Angle(180, "straight"));
        
        // Reflex angles: greater than 180° and less than 360°
        angles.add(new Angle(210, "reflex"));
        angles.add(new Angle(270, "reflex"));
        angles.add(new Angle(300, "reflex"));
    }
    
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        JLabel titleLabel = new JLabel("Task 2: Angle Type Identification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Center panel with the angle display
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        
        // Create a panel to display information about angle types
        JPanel infoPanel = createAngleInfoPanel();
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Angle display canvas
        angleDisplay = new AngleDisplay();
        angleDisplay.setPreferredSize(new Dimension(300, 300));
        angleDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(angleDisplay, BorderLayout.CENTER);
        
        // Current angle label
        currentAngleLabel = new JLabel("Current Angle: 0°");
        currentAngleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentAngleLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(currentAngleLabel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with input field and submit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panel.getBackground());
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(panel.getBackground());
        
        JLabel promptLabel = new JLabel("Enter the type of this angle (acute, right, obtuse, straight, reflex):");
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
        
        // Feedback and attempts panel
        JPanel feedbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        feedbackPanel.setBackground(panel.getBackground());
        
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        attemptsLabel = new JLabel("Attempts: 0/" + ScoreManager.MAX_ATTEMPTS);
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        progressLabel = new JLabel("Progress: 0/" + angles.size());
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(attemptsLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(progressLabel);
        
        bottomPanel.add(feedbackPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panel.getBackground());
        
        JButton nextButton = new JButton("Next Angle");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayNextAngle();
            }
        });
        
        buttonPanel.add(nextButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAngleInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Angle Types"));
        panel.setBackground(new Color(240, 248, 255));
        
        // Acute angle info
        JPanel acutePanel = createAngleTypeInfoPanel("Acute", "< 90°", new Color(173, 216, 230));
        panel.add(acutePanel);
        
        // Right angle info
        JPanel rightPanel = createAngleTypeInfoPanel("Right", "= 90°", new Color(144, 238, 144));
        panel.add(rightPanel);
        
        // Obtuse angle info
        JPanel obtusePanel = createAngleTypeInfoPanel("Obtuse", "90° to 180°", new Color(255, 182, 193));
        panel.add(obtusePanel);
        
        // Straight angle info
        JPanel straightPanel = createAngleTypeInfoPanel("Straight", "= 180°", new Color(255, 222, 173));
        panel.add(straightPanel);
        
        // Reflex angle info
        JPanel reflexPanel = createAngleTypeInfoPanel("Reflex", "> 180°", new Color(221, 160, 221));
        panel.add(reflexPanel);
        
        return panel;
    }
    
    private JPanel createAngleTypeInfoPanel(String type, String range, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        typeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel rangeLabel = new JLabel(range);
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        rangeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(typeLabel, BorderLayout.CENTER);
        panel.add(rangeLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCompletionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 255, 240)); // Light green background
        
        JLabel completionLabel = new JLabel("Excellent! You've completed the Angle Types Identification task!");
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
        
        JButton nextTaskButton = new JButton("Go to Task 3: Area Calculation");
        nextTaskButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTaskButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextTaskButton.setForeground(Color.WHITE);
        nextTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.startTask3();
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
    
    private void displayNextAngle() {
        // Reset attempts
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);
        
        // Clear feedback and answer field
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.requestFocus();
        
        // Check if we've gone through all angles
        if (currentAngleIndex >= angles.size()) {
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }
        
        // Get the current angle
        currentAngle = angles.get(currentAngleIndex);
        
        // Update the angle display
        angleDisplay.setAngle(currentAngle.getDegrees());
        currentAngleLabel.setText("Current Angle: " + currentAngle.getDegrees() + "°");
        
        // Update progress
        progressLabel.setText("Progress: " + totalCompleted + "/" + angles.size());
        
        // Update progress bar in main app
        int progress = ScoreManager.calculateProgress(totalCompleted, angles.size());
        mainApp.updateProgress(progress);
    }
    
    private void checkAnswer() {
        String userAnswer = answerField.getText().trim().toLowerCase();
        
        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);
        
        if (userAnswer.equals(currentAngle.getType())) {
            // Correct answer
            int score = ScoreManager.calculateScore(false, attempts); // Basic level
            String feedback = ScoreManager.getFeedbackMessage(score);
            
            feedbackLabel.setText("Correct! " + feedback + " +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0)); // Dark green
            
            // Update score in main app
            mainApp.updateScore(score);
            
            // Move to next angle
            currentAngleIndex++;
            totalCompleted++;
            
            // Disable input until next angle
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
            
            // Enable after a short delay
            Timer timer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayNextAngle();
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
            
            // If max attempts reached, show correct answer and move to next angle
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                feedbackLabel.setText("The correct answer is: " + currentAngle.getType());
                
                // Disable input until next angle
                answerField.setEnabled(false);
                submitButton.setEnabled(false);
                
                // Enable after a short delay
                Timer timer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        currentAngleIndex++;
                        totalCompleted++;
                        displayNextAngle();
                        answerField.setEnabled(true);
                        submitButton.setEnabled(true);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    // Inner class to represent an angle
    private static class Angle {
        private final int degrees;
        private final String type;
        
        public Angle(int degrees, String type) {
            this.degrees = degrees;
            this.type = type;
        }
        
        public int getDegrees() {
            return degrees;
        }
        
        public String getType() {
            return type;
        }
    }
    
    // Inner class to draw the angle visually
    private static class AngleDisplay extends JPanel {
        private int angleDegrees = 0;
        
        public AngleDisplay() {
            setBackground(Color.WHITE);
        }
        
        public void setAngle(int degrees) {
            this.angleDegrees = degrees;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable anti-aliasing for smoother lines
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Get the center of the component
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            // Size of the angle arms
            int armLength = Math.min(getWidth(), getHeight()) / 2 - 30;
            
            // Draw the reference horizontal line (first arm)
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLUE);
            g2d.draw(new Line2D.Double(centerX, centerY, centerX + armLength, centerY));
            
            // Draw the second arm based on the angle
            g2d.setColor(Color.RED);
            
            // Convert degrees to radians for the rotation
            double radians = Math.toRadians(angleDegrees);
            
            // Calculate the end point of the second arm
            double endX = centerX + armLength * Math.cos(radians);
            double endY = centerY - armLength * Math.sin(radians);
            
            g2d.draw(new Line2D.Double(centerX, centerY, endX, endY));
            
            // Draw the angle arc
            int arcRadius = 30;
            g2d.setColor(Color.GREEN);
            
            // For full circle or special cases
            if (angleDegrees == 360) {
                g2d.drawOval(centerX - arcRadius, centerY - arcRadius, arcRadius * 2, arcRadius * 2);
            } else {
                // Draw the arc - we need to adjust the angle for Java's Arc2D which uses
                // different angle conventions
                g2d.drawArc(centerX - arcRadius, centerY - arcRadius, 
                            arcRadius * 2, arcRadius * 2, 
                            0, -angleDegrees);
            }
            
            // Draw a small circle at the vertex
            g2d.setColor(Color.BLACK);
            g2d.fillOval(centerX - 3, centerY - 3, 6, 6);
            
            // Draw angle markings for special angles
            if (angleDegrees == 90) {
                // Right angle symbol (square)
                g2d.setColor(Color.GREEN);
                int squareSize = 15;
                g2d.drawRect(centerX, centerY - squareSize, squareSize, squareSize);
            }
        }
    }
} 