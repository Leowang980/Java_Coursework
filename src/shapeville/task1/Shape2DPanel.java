package shapeville.task1;
import shapeville.ScoreManager;
import shapeville.ShapevilleApp;
import shapeville.utils.ImageProvider;
import shapeville.utils.WoodenButton;
import shapeville.utils.ColorConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A panel for Task 1: 2D Shape Identification in the Shapeville application.
 * <p>
 * This panel allows users to identify 2D shapes by name, provides interactive feedback,
 * tracks progress and score, and integrates with the main application for navigation and persistence.
 * </p>
 */
public class Shape2DPanel extends JPanel {
    /** Reference to the main application instance */
    private final ShapevilleApp mainApp;
    /** Layout manager for switching between different panels */
    private final CardLayout cardLayout;
    /** Container panel for all content */
    private final JPanel contentPanel;

    /** Label to display the shape image */
    private JLabel shapeImageLabel;
    /** Input field for entering the shape name */
    private JTextField answerField;
    /** Button to submit the answer */
    private WoodenButton submitButton;
    /** Label to show feedback messages */
    private JLabel feedbackLabel;
    /** Label to show number of attempts */
    private JLabel attemptsLabel;
    /** Label to show progress */
    private JLabel progressLabel;
    /** Label to show the module score */
    private JLabel moduleScoreLabel;
    /** Progress bar for module progress */
    private JProgressBar moduleProgressBar;
    /** Current module score */
    private int moduleScore = 0;

    /** List of 2D shapes for the task */
    private List<Shape2D> shapes;
    /** The current shape being displayed */
    private Shape2D currentShape;
    /** Number of attempts for the current shape */
    private int attempts = 0;
    /** Number of shapes completed */
    private int totalCompleted = 0;

    /** Button to proceed to the next shape */
    private WoodenButton nextButton;

    /**
     * Constructs a Shape2DPanel for Task 1.
     * Initializes UI components, loads and shuffles shapes, and sets up the layout.
     *
     * @param mainApp The main application instance
     */
    public Shape2DPanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        // Initialize the shapes
        initializeShapes();

        // Shuffle the shapes to randomize the order
        Collections.shuffle(shapes);
        
        // Load previous progress and score
        moduleScore = ScoreManager.getTask1_2dScore();
        totalCompleted = ScoreManager.getTask1_2dProgress();

        // Set up the layout
        setLayout(new BorderLayout());

        // Create task panel
        JPanel taskPanel = createTaskPanel();
        contentPanel.add(taskPanel, "TASK");

        // Create completion panel
        

        // Add the content panel to this panel
        add(contentPanel, BorderLayout.CENTER);

        // Show the task panel first
        cardLayout.show(contentPanel, "TASK");

        // If all shapes were already answered, show completion panel
        if (totalCompleted >= shapes.size()) {
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }
        
        // Filter out shapes that have already been answered
        filterAnsweredShapes();
        
        // Display the first shape
        displayNextShape();
        
        // Update UI elements with saved progress
        updateProgressUI();
    }

    /**
     * Initializes the list of 2D shapes for the task.
     */
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

    /**
     * Creates the main task panel containing the shape image, input fields, and controls.
     *
     * @return JPanel containing the task interface
     */
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.MAIN_BG_COLOR); // 使用木质风格的主背景色

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorConstants.TITLE_BG_COLOR); // 使用木质风格的标题背景色
        JLabel titleLabel = new JLabel("Task 1: 2D Shape Identification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Center panel with shape image
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Shape image placeholder
        shapeImageLabel = new JLabel();
        shapeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        shapeImageLabel.setPreferredSize(new Dimension(300, 300));
        shapeImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(shapeImageLabel, BorderLayout.CENTER);

        // 新增：模块进度条
        moduleProgressBar = new JProgressBar(0, shapes.size());
        moduleProgressBar.setValue(0);
        moduleProgressBar.setStringPainted(true);
        moduleProgressBar.setString("0/" + shapes.size());
        centerPanel.add(moduleProgressBar, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with input field and submit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panel.getBackground());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(panel.getBackground());

        JLabel promptLabel = new JLabel("Enter the name of this shape:");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        answerField = new JTextField(15);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));

        submitButton = new WoodenButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        submitButton.setForeground(Color.BLACK);
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

        // 新增：模块分数显示
        moduleScoreLabel = new JLabel("Module Score: 0");
        moduleScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        moduleScoreLabel.setForeground(new Color(0, 102, 204));

        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(attemptsLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(progressLabel);
        feedbackPanel.add(Box.createHorizontalStrut(20));
        feedbackPanel.add(moduleScoreLabel);

        bottomPanel.add(feedbackPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(panel.getBackground());

        nextButton = new WoodenButton("Next Shape");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    displayNextShape();
                    answerField.setEnabled(true);
                    submitButton.setEnabled(true);
            }
        });
        nextButton.setEnabled(false);

        buttonPanel.add(nextButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the completion panel shown when all shapes are identified.
     *
     * @return JPanel containing the completion message and navigation buttons
     */
    private JPanel createCompletionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.SUCCESS_BG_COLOR); // 使用木质风格的成功背景色

        // 显示本模块得分
        JLabel completionLabel = new JLabel("<html>Great job! You've completed the 2D Shapes Identification task!<br>Module Score: " + moduleScore + "</html>");
        completionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        completionLabel.setHorizontalAlignment(JLabel.CENTER);

        WoodenButton homeButton = new WoodenButton("Return to Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.returnToHome();
            }
        });

        WoodenButton goTo3DButton = new WoodenButton("Continue to 3D Shapes");
        goTo3DButton.setFont(new Font("Arial", Font.BOLD, 14));
        goTo3DButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.startTask1_3D();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(homeButton);
        buttonPanel.add(goTo3DButton);

        panel.add(completionLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Displays the next shape in the list, updates progress, and resets input fields.
     */
    private void displayNextShape() {
        nextButton.setEnabled(false);
        if (shapes.isEmpty()) {
            // All shapes have been answered, show completion panel
            JPanel completionPanel = createCompletionPanel();
            contentPanel.add(completionPanel, "COMPLETION");
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }

        // Reset attempt count
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);

        // Get the next unanswered shape
        currentShape = shapes.remove(0); // Get and remove first shape from list
        
        // Display the shape
        try {
            ImageIcon icon = ImageProvider.get2DShapeIcon(currentShape.getImagePath());
            if (icon != null) {
                shapeImageLabel.setIcon(icon);
            } else {
                shapeImageLabel.setText("Image not found: " + currentShape.getImagePath());
            }
        } catch (Exception e) {
            shapeImageLabel.setText("Error loading image");
            e.printStackTrace();
        }

        // Clear feedback and input
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.requestFocus();
        answerField.setEnabled(true);
        submitButton.setEnabled(true);

        // Update progress display
        moduleProgressBar.setValue(totalCompleted);
        moduleProgressBar.setString(totalCompleted + "/11");
    }

    /**
     * Checks the user's answer for the current shape and provides feedback.
     * Updates progress and handles correct/incorrect answers.
     */
    private void checkAnswer() {
        String userAnswer = answerField.getText().trim().toLowerCase();
        String correctAnswer = currentShape.getName().toLowerCase();

        if (userAnswer.isEmpty()) {
            feedbackLabel.setText("Please enter an answer!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }

        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);

        if (userAnswer.equals(correctAnswer)) {
            // Correct answer
            int score = ScoreManager.calculateScore(false, attempts); // Basic level
            moduleScore += score; // Add to module score
            ScoreManager.addToTask1_2dScore(score); // Persist module score
            System.out.println("2D Module Score: " + ScoreManager.getTask1_2dScore());
            String feedback = ScoreManager.getFeedbackMessage(score);
            feedbackLabel.setText("Correct! " + feedback + " +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0)); // Dark green

            // Update score in main app
            mainApp.updateScore(score);
            
            // Mark this shape as completed
            ScoreManager.markShape2DAnswered(currentShape.getName());
            totalCompleted = ScoreManager.getTask1_2dProgress();

            // Disable input fields
            nextButton.setEnabled(true);
            answerField.setEnabled(false);
            submitButton.setEnabled(false);

            // Update progress display
            updateProgressUI();

            // Check if all shapes are completed
            if (shapes.isEmpty() || totalCompleted >= 11) { // 11 is the total number of shapes
                // Mark task as completed
                mainApp.addTask1ProgressPart(0.5);
                JPanel completionPanel = createCompletionPanel();
                contentPanel.add(completionPanel, "COMPLETION");
                // Show completion panel
                cardLayout.show(contentPanel, "COMPLETION");
            }
        } else {
            // Wrong answer
            feedbackLabel.setText("That's not correct. Try again!");
            feedbackLabel.setForeground(Color.RED);

            // If max attempts reached, show correct answer
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                feedbackLabel.setText("The correct answer is: " + correctAnswer);
                nextButton.setEnabled(true);
                answerField.setEnabled(false);
                submitButton.setEnabled(false);

                // 新增：答错也记录
                ScoreManager.markShape2DAnswered(currentShape.getName());
                totalCompleted = ScoreManager.getTask1_2dProgress();
                updateProgressUI();

                // 检查是否全部完成
                if (shapes.isEmpty() || totalCompleted >= 11) {
                    mainApp.addTask1ProgressPart(0.5);
                    JPanel completionPanel = createCompletionPanel();
                    contentPanel.add(completionPanel, "COMPLETION");
                    cardLayout.show(contentPanel, "COMPLETION");
                }
            }
        }

        // Update the module score label
        moduleScoreLabel.setText("Module Score: " + moduleScore);
        //nextButton.setEnabled(true);
    }       

    /**
     * Filters out shapes that have already been answered by the user.
     */
    private void filterAnsweredShapes() {
        // Create a temporary list to hold unanswered shapes
        List<Shape2D> unansweredShapes = new ArrayList<>();
        
        for (Shape2D shape : shapes) {
            if (!ScoreManager.isShape2DAnswered(shape.getName())) {
                unansweredShapes.add(shape);
            }
        }
        
        // Replace the original list with the filtered list
        shapes = unansweredShapes;
    }
    
    /**
     * Updates the progress and score displays in the UI.
     */
    private void updateProgressUI() {
        // Update progress and score displays
        progressLabel.setText("Progress: " + totalCompleted + "/" + 11); // Total shapes is 11
        moduleScoreLabel.setText("Module Score: " + moduleScore);
        moduleProgressBar.setValue(totalCompleted);
        moduleProgressBar.setString(totalCompleted + "/11");
    }

    /**
     * Inner class to represent a 2D shape with a name and image path.
     */
    private static class Shape2D {
        /** The name of the 2D shape */
        private final String name;
        /** The image path for the 2D shape */
        private final String imagePath;

        /**
         * Constructs a Shape2D object.
         *
         * @param name      The name of the shape
         * @param imagePath The image path for the shape
         */
        public Shape2D(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }

        /**
         * Gets the name of the shape.
         *
         * @return The shape name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the image path for the shape.
         *
         * @return The image path
         */
        public String getImagePath() {
            return imagePath;
        }
    }
} 