package shapeville.task1;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;
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
 * A panel for Task 1: 3D Shape Identification in the Shapeville application.
 * <p>
 * This panel allows users to identify 3D shapes by name, provides interactive feedback,
 * tracks progress and score, and integrates with the main application for navigation and persistence.
 * </p>
 */
public class Shape3DPanel extends JPanel {
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

    /** List of 3D shapes for the task */
    private List<Shape3D> shapes;
    /** The current shape being displayed */
    private Shape3D currentShape;
    /** Index of the current shape in the list */
    private int currentShapeIndex = 0;
    /** Number of attempts for the current shape */
    private int attempts = 0;
    /** Number of shapes completed */
    private int totalCompleted = 0;
    /** Button to proceed to the next shape */
    private WoodenButton nextButton;

    public Shape3DPanel(ShapevilleApp mainApp) {
        this.mainApp = mainApp;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        // Initialize the shapes
        initializeShapes();

        // Shuffle the shapes to randomize the order
        Collections.shuffle(shapes);

        // 过滤已答过的题
        filterAnsweredShapes();

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

        // Display the first shape
        displayNextShape();
    }

    private void initializeShapes() {
        shapes = new ArrayList<>();

        // Add the 3D shapes from Figure 2
        shapes.add(new Shape3D("cube", "/shapeville/images/3d/cube.png"));
        shapes.add(new Shape3D("cuboid", "/shapeville/images/3d/cuboid.png"));
        shapes.add(new Shape3D("cylinder", "/shapeville/images/3d/cylinder.png"));
        shapes.add(new Shape3D("sphere", "/shapeville/images/3d/sphere.png"));
        shapes.add(new Shape3D("triangular prism", "/shapeville/images/3d/triangular_prism.png"));
        shapes.add(new Shape3D("square-based pyramid", "/shapeville/images/3d/square_pyramid.png"));
        shapes.add(new Shape3D("cone", "/shapeville/images/3d/cone.png"));
        shapes.add(new Shape3D("tetrahedron", "/shapeville/images/3d/tetrahedron.png"));
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.MAIN_BG_COLOR);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorConstants.TITLE_BG_COLOR);
        JLabel titleLabel = new JLabel("Task 1: 3D Shape Identification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Center panel with shape image
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());

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

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(panel.getBackground());

        JLabel promptLabel = new JLabel("Enter the name of this 3D shape:");
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(panel.getBackground());

        nextButton = new WoodenButton("Next Shape");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentShapeIndex < shapes.size()) {
                    currentShapeIndex++;
                    totalCompleted++;
                    displayNextShape();
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
        panel.setBackground(ColorConstants.SUCCESS_BG_COLOR);
        
        // 新增：显示2D、3D和总分
        int score2d = ScoreManager.getTask1_2dScore();
        System.out.println("2D Module Score: " + score2d);
        int score3d = ScoreManager.getTask1_3dScore();
        System.out.println("3D Module Score: " + score3d);
        int total = score2d + score3d;
        String html = "<html>Congratulations! You've completed the 3D Shapes Identification task!<br>" +
                "2D Module Score: <b>" + score2d + "</b><br>" +
                "3D Module Score: <b>" + score3d + "</b><br>" +
                "<span style='font-size:16pt'>Total for Task 1: <b>" + total + "</b></span></html>";
        JLabel completionLabel = new JLabel(html);
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

        WoodenButton nextTaskButton = new WoodenButton("Go to Task 2: Angle Types");
        nextTaskButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.startTask2();
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

    private void displayNextShape() {
        // Reset attempts
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + ScoreManager.MAX_ATTEMPTS);

        // Clear feedback and answer field
        feedbackLabel.setText(" ");
        answerField.setText("");
        answerField.requestFocus();
        nextButton.setEnabled(false);
        // Check if we've gone through all shapes
        if (currentShapeIndex >= shapes.size()) {
            // 只在全部完成时加分
            mainApp.addTask1Progress3DPart();
            JPanel completionPanel = createCompletionPanel();
            contentPanel.add(completionPanel, "COMPLETION");
            cardLayout.show(contentPanel, "COMPLETION");
            return;
        }

        // Get the current shape
        currentShape = shapes.get(currentShapeIndex);

        // Display the shape image
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(currentShape.getImagePath()));
            // Resize the image to fit the label nicely
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            shapeImageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Could not load image: " + currentShape.getImagePath());
            e.printStackTrace();
            // Use a placeholder if the image couldn't be loaded
            shapeImageLabel.setIcon(null);
            shapeImageLabel.setText("Shape: " + currentShape.getName());
        }

        // Update progress
        progressLabel.setText("Progress: " + totalCompleted + "/" + shapes.size());
        // 更新模块进度条
        moduleProgressBar.setValue(totalCompleted);
        moduleProgressBar.setString(totalCompleted + "/" + shapes.size());
        // 更新模块分数显示
        moduleScoreLabel.setText("Module Score: " + moduleScore);
        // Enable input fields
        answerField.setEnabled(true);
        submitButton.setEnabled(true);
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

        boolean correct = userAnswer.equals(currentShape.getName());
        if (correct) {
            // Correct answer
            int score = ScoreManager.calculateScore(true, attempts); // Advanced level
            String feedback = ScoreManager.getFeedbackMessage(score);

            feedbackLabel.setText("Correct! " + feedback + " +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0)); // Dark green

            // Update score in main app
            mainApp.updateScore(score);
            // 新增：更新模块分数
            moduleScore += score;
            moduleScoreLabel.setText("Module Score: " + moduleScore);
            // 持久化3D分数
            ScoreManager.addToTask1_3dScore(score);
            System.out.println("3D Module Score: " + ScoreManager.getTask1_3dScore());
            // Disable input fields
            answerField.setEnabled(false);
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
        } else {
            // Wrong answer
            feedbackLabel.setText("That's not correct. Try again!");
            feedbackLabel.setForeground(Color.RED);

            // If max attempts reached, show correct answer
            if (attempts >= ScoreManager.MAX_ATTEMPTS) {
                feedbackLabel.setText("The correct answer is: " + currentShape.getName());
                // Disable input fields
                answerField.setEnabled(false);
                submitButton.setEnabled(false);
                nextButton.setEnabled(true);
            }
        }
        // 无论对错都记录已答题
        if (!ScoreManager.isShape3DAnswered(currentShape.getName())) {
            ScoreManager.markShape3DAnswered(currentShape.getName());
            //totalCompleted = ScoreManager.getTask1_3dProgress();
        }
    }

    // Inner class to represent a 3D shape
    private static class Shape3D {
        private final String name;
        private final String imagePath;

        public Shape3D(String name, String imagePath) {
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

    private void filterAnsweredShapes() {
        List<Shape3D> unanswered = new ArrayList<>();
        for (Shape3D shape : shapes) {
            if (!ScoreManager.isShape3DAnswered(shape.getName())) {
                unanswered.add(shape);
            }
        }
        shapes = unanswered;
    }
}