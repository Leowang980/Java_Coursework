package shapeville.task1;
import shapeville.ScoreManager;
import shapeville.ShapevilleApp;
import shapeville.utils.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shape2DPanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private JLabel shapeImageLabel;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel attemptsLabel;
    private JLabel progressLabel;

    private List<Shape2D> shapes;
    private Shape2D currentShape;
    private int currentShapeIndex = 0;
    private int attempts = 0;
    private int totalCompleted = 0;

    public Shape2DPanel(ShapevilleApp mainApp) {
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

        // Add the 2D shapes from Figure 1
        shapes.add(new Shape2D("circle", "circle"));
        shapes.add(new Shape2D("rectangle", "rectangle"));
        shapes.add(new Shape2D("triangle", "triangle"));
        shapes.add(new Shape2D("oval", "oval"));
        shapes.add(new Shape2D("octagon", "octagon"));
        shapes.add(new Shape2D("square", "square"));
        shapes.add(new Shape2D("heptagon", "heptagon"));
        shapes.add(new Shape2D("rhombus", "rhombus"));
        shapes.add(new Shape2D("pentagon", "pentagon"));
        shapes.add(new Shape2D("hexagon", "hexagon"));
        shapes.add(new Shape2D("kite", "kite"));
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // Light blue background

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        JLabel titleLabel = new JLabel("Task 1: 2D Shape Identification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Center panel with the shape image
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());

        // Shape image placeholder
        shapeImageLabel = new JLabel();
        shapeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        shapeImageLabel.setPreferredSize(new Dimension(300, 300));
        shapeImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(shapeImageLabel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with input field and submit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panel.getBackground());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(panel.getBackground());

        JLabel promptLabel = new JLabel("Enter the name of this shape:");
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

        progressLabel = new JLabel("Progress: 0/" + shapes.size());
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(attemptsLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(progressLabel);

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
                if (currentShapeIndex < shapes.size()) {
                    currentShapeIndex++;
                    totalCompleted++;
                    displayNextShape();
                    answerField.setEnabled(true);
                    submitButton.setEnabled(true);
                }
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

        JLabel completionLabel = new JLabel("Great job! You've completed the 2D Shapes Identification task!");
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

        JButton goTo3DButton = new JButton("Continue to 3D Shapes");
        goTo3DButton.setFont(new Font("Arial", Font.BOLD, 14));
        goTo3DButton.setBackground(new Color(50, 205, 50)); // Lime green
        goTo3DButton.setForeground(Color.WHITE);
        goTo3DButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.startTask1_3D();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(homeButton);
        buttonPanel.add(goTo3DButton);

        panel.add(completionLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }

        // Get the current shape
        currentShape = shapes.get(currentShapeIndex);

        // Display the shape image using ImageProvider
        try {
            ImageIcon icon = ImageProvider.get2DShapeIcon(currentShape.getImagePath());
            shapeImageLabel.setIcon(icon);
        } catch (Exception e) {
            System.err.println("Could not load image for shape: " + currentShape.getName());
            e.printStackTrace();
            // Use a placeholder if the image couldn't be loaded
            shapeImageLabel.setIcon(null);
            shapeImageLabel.setText("Shape: " + currentShape.getName());
        }

        // Update progress
        progressLabel.setText("Progress: " + totalCompleted + "/" + shapes.size());

        // Update progress bar in main app
        int progress = ScoreManager.calculateProgress(totalCompleted, shapes.size());
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

        if (userAnswer.equals(currentShape.getName())) {
            // Correct answer
            int score = ScoreManager.calculateScore(false, attempts); // Basic level
            String feedback = ScoreManager.getFeedbackMessage(score);

            feedbackLabel.setText("Correct! " + feedback + " +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0)); // Dark green

            // Update score in main app
            mainApp.updateScore(score);

            // Disable input until next shape
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
        } else {
            // Wrong answer
            feedbackLabel.setText("That's not correct. Try again!");
            feedbackLabel.setForeground(Color.RED);

            // If max attempts reached, show correct answer
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                feedbackLabel.setText("The correct answer is: " + currentShape.getName());

                // Disable input until next shape
                answerField.setEnabled(false);
                submitButton.setEnabled(false);
            }
        }
    }

    // Inner class to represent a 2D shape
    private static class Shape2D {
        private final String name;
        private final String imagePath;

        public Shape2D(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public String getImagePath() {
            return imagePath;
        }
    }
} 