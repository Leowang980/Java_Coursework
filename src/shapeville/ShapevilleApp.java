package shapeville;

import shapeville.task1.Shape2DPanel;
import shapeville.task1.Shape3DPanel;
import shapeville.task2.AnglePanel;
import shapeville.task3.AreaPanel;
import shapeville.task4.CirclePanel;
import shapeville.bonus1.CompoundPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ShapevilleApp extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JProgressBar progressBar;
    private JLabel scoreLabel;
    private int totalScore = 0;
    
    // Constants for the different screens
    public static final String START_SCREEN = "START";
    public static final String HOME_SCREEN = "HOME";
    public static final String SHAPE_2D_SCREEN = "SHAPE_2D";
    public static final String SHAPE_3D_SCREEN = "SHAPE_3D";
    public static final String ANGLE_SCREEN = "ANGLE";
    public static final String AREA_SCREEN = "AREA";
    public static final String CIRCLE_SCREEN = "CIRCLE";
    public static final String COMPOUND_SCREEN = "COMPOUND";
    public static final String SECTOR_SCREEN = "SECTOR";
    
    // Task panels
    private StartScreen startScreen;
    private Shape2DPanel shape2DPanel;
    private Shape3DPanel shape3DPanel;
    private AnglePanel anglePanel;
    private AreaPanel areaPanel;
    private CirclePanel circlePanel;
    private JPanel sectorPanel; // Bonus2 扇形练习面板
    private JPanel compoundPanel; // Bonus1 扇形练习面板
    private double progress_total = 0.0;
    // Task 1 2D/3D完成标志
    private boolean task1_2dCompleted = false;
    private boolean task1_3dCompleted = false;
    // 新增：Task2、3、4、Bonus1、Bonus2完成标志
    private boolean task2Completed = false;
    private boolean task3Completed = false;
    private boolean task4Completed = false;
    private boolean bonus1Completed = false;
    private boolean bonus2Completed = false;
    private int totalProgress = 0; // 记录总进度百分比
    private int accessLevel = 0; // 0=none, 1=Key Stage 1, 2=Key Stage 2, 3=All
    
    public ShapevilleApp() {
        setTitle("Shapeville - Learning Geometry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize the layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create start screen
        startScreen = new StartScreen(this);
        
        // Create panels for different screens
        JPanel homePanel = createHomePanel();
        
        // Create task panels (we will initialize them lazily to improve startup time)
        
        // Add panels to the card layout
        contentPanel.add(startScreen, START_SCREEN);
        contentPanel.add(homePanel, HOME_SCREEN);
        
        // Add the main content panel
        add(contentPanel, BorderLayout.CENTER);
        
        // Create and add the navigation panel at the bottom
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);
        
        // Show start screen by default
        cardLayout.show(contentPanel, START_SCREEN);
        
        // Hide the progress bar initially (will show only in home screen)
        navPanel.setVisible(false);
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        JLabel titleLabel = new JLabel("Welcome to Shapeville!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Center panel with task buttons
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        centerPanel.setBackground(new Color(240, 248, 255));
        
        // Add task buttons with lock status based on access level
        addTaskButton(centerPanel, "Task 1: Shape Identification", "Learn to identify 2D and 3D shapes", e -> startTask1(), 1);
        addTaskButton(centerPanel, "Task 2: Angle Types", "Learn to identify different types of angles", e -> startTask2(), 1);
        addTaskButton(centerPanel, "Task 3: Area Calculation", "Calculate areas of different shapes", e -> startTask3(), 2);
        addTaskButton(centerPanel, "Task 4: Circle Calculations", "Calculate area and circumference", e -> startTask4(), 2);
        addTaskButton(centerPanel, "Bonus 1: Compound Shapes", "Calculate areas of compound shapes", e -> startBonus1(), 3);
        addTaskButton(centerPanel, "Bonus 2: Sector & Arc", "Calculate sector area and arc length", e -> startBonus2(), 3);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Game info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel infoLabel = new JLabel("<html><h3>Game Levels and Scoring:</h3>" +
                "Basic level: 3 points (1st attempt), 2 points (2nd attempt), 1 point (3rd attempt)<br>" +
                "Advanced level: 6 points (1st attempt), 4 points (2nd attempt), 2 points (3rd attempt)</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addTaskButton(JPanel panel, String title, String description, ActionListener action, int requiredLevel) {
        JButton button = new JButton("<html><center>" + title + "<br><font size='-1'>" + description + "</font></center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(135, 206, 250)); // Light sky blue
        button.setForeground(new Color(25, 25, 112)); // Midnight blue
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Add button with action if accessible based on access level
        if (accessLevel >= requiredLevel) {
            button.addActionListener(action);
        } else {
            // Add lock icon and disable button if not accessible
            try {
                ImageIcon lockIcon = new ImageIcon(getClass().getResource("/shapeville/images/enter_page/lock.png"));
                // Scale the lock icon to an appropriate size
                Image scaledImage = lockIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                lockIcon = new ImageIcon(scaledImage);
                
                button.setText("<html><center>" + title + "<br><font size='-1'>" + description + 
                        "</font></center></html>");
                button.setIcon(lockIcon);
                button.setEnabled(false);
                button.setDisabledIcon(lockIcon);
            } catch (Exception e) {
                // If lock icon cannot be loaded, just use text
                button.setText("<html><center>" + title + "<br><font size='-1'>" + description + 
                        "</font><br><font color='red'>(🔒 Locked)</font></center></html>");
                button.setEnabled(false);
            }
        }
        
        panel.add(button);
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(new Color(230, 230, 250)); // Lavender
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(new Color(50, 205, 50)); // Lime green
        
        // Score label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(panel.getBackground());
        leftPanel.add(new JLabel("Progress: "));
        leftPanel.add(progressBar);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(panel.getBackground());
        rightPanel.add(scoreLabel);
        
        JButton backButton = new JButton("Back to Start");
        backButton.addActionListener(e -> returnToStartScreen());
        
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> returnToHome());
        
        JButton endButton = new JButton("End Session");
        endButton.addActionListener(e -> endSession());
        
        rightPanel.add(backButton);
        rightPanel.add(homeButton);
        rightPanel.add(endButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    // Methods to navigate between screens
    public void startHomeScreen() {
        // Remove existing home panel if it exists
        for (Component comp : contentPanel.getComponents()) {
            if (comp != startScreen) {
                contentPanel.remove(comp);
            }
        }
        
        // Create a new home panel with updated access level
        JPanel homePanel = createHomePanel();
        contentPanel.add(homePanel, HOME_SCREEN);
        
        // Show the updated home panel
        cardLayout.show(contentPanel, HOME_SCREEN);
        
        // Show navigation panel with progress bar
        getContentPane().getComponent(1).setVisible(true);
        
        // Add key listener to handle Escape key
        homePanel.setFocusable(true);
        homePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    returnToStartScreen(); // Go back to start screen
                }
            }
        });
        homePanel.requestFocusInWindow();
    }
    
    private Component findPanelByName(String name) {
        // This method had issues - we don't need it anymore
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                return comp;
            }
        }
        return null;
    }
    
    public void returnToHome() {
        startHomeScreen();
    }
    
    public void returnToStartScreen() {
        // Hide navigation panel
        getContentPane().getComponent(1).setVisible(false);
        
        // Show start screen
        cardLayout.show(contentPanel, START_SCREEN);
        
        // Request focus for the start screen to capture key events
        startScreen.requestFocusInWindow();
    }
    
    private void endSession() {
        JOptionPane.showMessageDialog(this, 
                "You have achieved " + totalScore + " points in this session. Goodbye!", 
                "Session Ended", 
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
    // Methods to start different tasks
    
    // Task 1: Shape Identification
    public void startTask1() {
        // First part of Task 1 is 2D shape identification
        startTask1_2D();
    }
    
    public void startTask1_2D() {
        // Initialize the 2D shapes panel if not already done
        if (shape2DPanel == null) {
            shape2DPanel = new Shape2DPanel(this);
            contentPanel.add(shape2DPanel, SHAPE_2D_SCREEN);
        }
        // Show the 2D shapes screen
        cardLayout.show(contentPanel, SHAPE_2D_SCREEN);
    }
    
    public void startTask1_3D() {
        // Initialize the 3D shapes panel if not already done
        if (shape3DPanel == null) {
            shape3DPanel = new Shape3DPanel(this);
            contentPanel.add(shape3DPanel, SHAPE_3D_SCREEN);
        }
        // Show the 3D shapes screen
        cardLayout.show(contentPanel, SHAPE_3D_SCREEN);
    }
    
    // Task 2: Angle Type Identification
    public void startTask2() {
        // Initialize the angle panel if not already done
        if (anglePanel == null) {
            anglePanel = new AnglePanel(this);
            contentPanel.add(anglePanel, ANGLE_SCREEN);
        }
        
        // Show the angle screen
        cardLayout.show(contentPanel, ANGLE_SCREEN);
    }
    
    // Task 3: Area Calculation
    public void startTask3() {
        // Initialize the area panel if not already done
        if (areaPanel == null) {
            areaPanel = new AreaPanel(this);
            contentPanel.add(areaPanel, AREA_SCREEN);
        }
        
        // Show the area screen
        cardLayout.show(contentPanel, AREA_SCREEN);
    }
    
    // Task 4: Circle Calculations
    public void startTask4() {
        // Initialize the circle panel if not already done
        if (circlePanel == null) {
            circlePanel = new CirclePanel(this);
            contentPanel.add(circlePanel, CIRCLE_SCREEN);
        }
        
        // Show the circle screen
        cardLayout.show(contentPanel, CIRCLE_SCREEN);
    }

    // Bonus 1: Compound Shapes Area Calculation
    public void startBonus1() {
        // 初始化CompoundPanel，如果未创建则新建
        if (compoundPanel == null) {
            compoundPanel = new shapeville.bonus1.CompoundPanel(this);
            contentPanel.add(compoundPanel, "COMPOUND_SCREEN");
        }
        // 显示CompoundPanel
        cardLayout.show(contentPanel, "COMPOUND_SCREEN");
    }
    
    // Bonus 2: Sector Area and Arc Length Calculation
    public void startBonus2() {
        // 初始化SectorPanel，如果未创建则新建
        if (sectorPanel == null) {
            sectorPanel = new shapeville.bonus2.SectorPanel(this);
            contentPanel.add(sectorPanel, "SECTOR_SCREEN");
        }
        // 显示SectorPanel
        cardLayout.show(contentPanel, "SECTOR_SCREEN");
    }
    
    // Methods to update UI elements
    public void updateScore(int points) {
        totalScore += points;
        scoreLabel.setText("Score: " + totalScore);
    }
    
    public void updateProgress(double progress) {
        progress_total += progress;
        System.out.println("progress_total: " + progress_total);
        progressBar.setValue((int)Math.round(progress_total));
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ShapevilleApp app = new ShapevilleApp();
            app.setVisible(true);
        });
    }
    public int getCurrentScore() {
        return ScoreManager.getScore();
    }
    
    /**
     * Task1子模块完成时调用，part=0.5表示2D或3D各占一半
     */
    public void addTask1ProgressPart(double part) {
        double increment = 100.0 / 6 * part; // 1/12=8.33...%
        if (part == 0.5) {
            if (!task1_2dCompleted) {
                totalProgress += (int)Math.round(increment);
                task1_2dCompleted = true;
                progress_total += increment;
                System.out.println("progress_total: " + progress_total);
                progressBar.setValue((int)Math.round(progress_total));
            }
        } else if (part == 1.5) { // 备用：全部完成
            if (!task1_2dCompleted || !task1_3dCompleted) {
                totalProgress += increment * 2;
                task1_2dCompleted = true;
                task1_3dCompleted = true;
                progress_total += increment * 2;
                progressBar.setValue((int)Math.round(progress_total));
            }
        }
    }
    public void addTask1Progress3DPart() {
        double increment = 100.0 / 6 * 0.5;
        if (!task1_3dCompleted) {
            totalProgress += (int)Math.round(increment);
            task1_3dCompleted = true;
            progress_total += increment;
            System.out.println("progress_total: " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
        }
    }
    public boolean isTask1_2dCompleted() { return task1_2dCompleted; }
    public boolean isTask1_3dCompleted() { return task1_3dCompleted; }
    
    // 统一进度加分方法
    public void addTask2Progress() {
        double increment = 100.0 / 6;
        if (!task2Completed) {
            totalProgress += (int)Math.round(increment);
            task2Completed = true;
            progress_total += increment;
            System.out.println("progress_total: " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
        }
    }
    public void addTask3Progress() {
        int increment = 100 / 6;
        if (!task3Completed) {
            totalProgress += increment;
            task3Completed = true;
            progressBar.setValue(progressBar.getValue()+increment);
        }
    }
    public void addTask4Progress() {
        double increment = 100.0 / 6;
        if (!task4Completed) {
            totalProgress += (int)Math.round(increment);
            task4Completed = true;
            progress_total += increment;
            System.out.println("progress_total: " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
        }
    }
    public void addBonus1Progress() {
        int increment = 100 / 6;
        if (!bonus1Completed) {
            totalProgress += increment;
            bonus1Completed = true;
            progressBar.setValue(progressBar.getValue()+increment);
        }
    }
    public void addBonus2Progress() {
        int increment = 100 / 6;
        if (!bonus2Completed) {
            totalProgress += increment;
            bonus2Completed = true;
            progressBar.setValue(progressBar.getValue()+increment);
        }
    }
    public boolean isTask2Completed() { return task2Completed; }
    public boolean isTask3Completed() { return task3Completed; }
    public boolean isTask4Completed() { return task4Completed; }
    public boolean isBonus1Completed() { return bonus1Completed; }
    public boolean isBonus2Completed() { return bonus2Completed; }
    
    // Set access level based on start screen selection
    public void setAccessLevel(int level) {
        this.accessLevel = level;
        System.out.println("Access level set to: " + level);
    }
} 