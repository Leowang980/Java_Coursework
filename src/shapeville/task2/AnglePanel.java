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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnglePanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private AngleDisplay angleDisplay;
    private JTextField angleInputField;
    private JButton submitAngleButton;
    private JTextField answerField;
    private JButton submitAnswerButton;
    private JLabel feedbackLabel;
    private JLabel attemptsLabel;
    private JLabel progressLabel;
    private JLabel currentAngleLabel;

    private Set<String> identifiedAngleTypes;
    private int currentAngleDegrees;
    private String currentAngleType;
    private int attempts = 0;

    public AnglePanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        identifiedAngleTypes = new HashSet<>();

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

        // Top panel with angle input field and submit button
        JPanel topInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topInputPanel.setBackground(panel.getBackground());

        JLabel anglePromptLabel = new JLabel("Enter an angle between 0 and 360 degrees (multiple of 10):");
        anglePromptLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        angleInputField = new JTextField(10);
        angleInputField.setFont(new Font("Arial", Font.PLAIN, 14));

        submitAngleButton = new JButton("Submit Angle");
        submitAngleButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitAngleButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        submitAngleButton.setForeground(Color.BLACK);
        submitAngleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAngleInput();
            }
        });

        // Add enter key functionality to the text field
        angleInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAngleInput();
            }
        });

        topInputPanel.add(anglePromptLabel);
        topInputPanel.add(angleInputField);
        topInputPanel.add(submitAngleButton);

        panel.add(topInputPanel, BorderLayout.PAGE_START);

        // Bottom panel with input field and submit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panel.getBackground());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(panel.getBackground());

        JLabel promptLabel = new JLabel("Enter the type of this angle (acute, right, obtuse, reflex):");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        answerField = new JTextField(15);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));
        answerField.setEnabled(false);

        submitAnswerButton = new JButton("Submit");
        submitAnswerButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitAnswerButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        submitAnswerButton.setForeground(Color.BLACK);
        submitAnswerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });
        submitAnswerButton.setEnabled(false);

        // Add enter key functionality to the text field
        answerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        inputPanel.add(promptLabel);
        inputPanel.add(answerField);
        inputPanel.add(submitAnswerButton);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        // Feedback and attempts panel
        JPanel feedbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        feedbackPanel.setBackground(panel.getBackground());

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 14));

        attemptsLabel = new JLabel("Attempts: 0/" + ScoreManager.MAX_ATTEMPTS);
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        progressLabel = new JLabel("Identified Types: 0/4");
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(attemptsLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(progressLabel);

        bottomPanel.add(feedbackPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panel.getBackground());

        JButton homeButton = new JButton("Return to Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setBackground(new Color(70, 130, 180)); // Steel blue
        homeButton.setForeground(Color.BLACK);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.returnToHome();
            }
        });

        buttonPanel.add(homeButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAngleInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
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

        JLabel completionLabel = new JLabel("Excellent! You've identified all 4 angle types!");
        completionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        completionLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton homeButton = new JButton("Return to Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setBackground(new Color(70, 130, 180)); // Steel blue
        homeButton.setForeground(Color.BLACK);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.returnToHome();
            }
        });

        JButton nextTaskButton = new JButton("Go to Task 3: Area Calculation");
        nextTaskButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTaskButton.setBackground(new Color(50, 205, 50)); // Lime green
        nextTaskButton.setForeground(Color.BLACK);
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

        //mainApp.addTask2Progress();
        return panel;
    }

    private void handleAngleInput() {
        String input = angleInputField.getText().trim();
        try {
            int degrees = Integer.parseInt(input);
            if (degrees >= 0 && degrees <= 360 && degrees % 10 == 0) {
                currentAngleDegrees = degrees;
                currentAngleType = getAngleType(degrees);
                angleDisplay.setAngle(degrees);
                currentAngleLabel.setText("Current Angle: " + degrees + "°");
                answerField.setEnabled(true);
                submitAnswerButton.setEnabled(true);
                attempts = 0;
                attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);
                feedbackLabel.setText(" ");
                answerField.setText("");
                answerField.requestFocus();

                // Disable angle input field and submit button
                angleInputField.setEnabled(false);
                submitAngleButton.setEnabled(false);
            } else {
                feedbackLabel.setText("Please enter an angle between 0 and 360 degrees (multiple of 10)!");
                feedbackLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid integer!");
            feedbackLabel.setForeground(Color.RED);
        }
    }

    private String getAngleType(int degrees) {
        if (degrees < 90) {
            return "acute";
        } else if (degrees == 90) {
            return "right";
        } else if (degrees < 180) {
            return "obtuse";
        } else {
            return "reflex";
        }
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

        if (userAnswer.equals(currentAngleType)) {
            // Correct answer
            int score = ScoreManager.calculateScore(false, attempts); // Basic level
            String feedback = ScoreManager.getFeedbackMessage(score);

            feedbackLabel.setText("Correct! " + feedback + " +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0)); // Dark green

            // Update score in main app
            mainApp.updateScore(score);

            identifiedAngleTypes.add(currentAngleType);
            progressLabel.setText("Identified Types: " + identifiedAngleTypes.size() + "/4");

            if (identifiedAngleTypes.size() == 4) {
                cardLayout.show(contentPanel, "COMPLETION");
                mainApp.addTask2Progress();
                return;
            }

            // Enable angle input field and submit button
            angleInputField.setEnabled(true);
            submitAngleButton.setEnabled(true);
            angleInputField.setText("");
            angleInputField.requestFocus();

            // Disable input until next angle
            answerField.setEnabled(false);
            submitAnswerButton.setEnabled(false);
        } else {
            // Wrong answer
            feedbackLabel.setText("That's not correct. Try again!");
            feedbackLabel.setForeground(Color.RED);

            // If max attempts reached, show correct answer and move to next angle
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                feedbackLabel.setText("The correct answer is: " + currentAngleType);

                // Enable angle input field and submit button
                angleInputField.setEnabled(true);
                submitAngleButton.setEnabled(true);
                angleInputField.setText("");
                angleInputField.requestFocus();

                // Disable input until next angle
                answerField.setEnabled(false);
                submitAnswerButton.setEnabled(false);
            }
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
                        0, angleDegrees);
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