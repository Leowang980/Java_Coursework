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
import shapeville.utils.WoodenButton;
import shapeville.utils.ColorConstants;

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

    // 只声明，不初始化
    
    public ShapevilleApp() {
        setTitle("Shapeville - Learning Geometry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize from persisted state in ScoreManager
        totalScore = ScoreManager.getScore();
        task1_2dCompleted = ScoreManager.isTask1_2dCompleted();
        task1_3dCompleted = ScoreManager.isTask1_3dCompleted();
        task2Completed = ScoreManager.isTask2Completed();
        task3Completed = ScoreManager.isTask3Completed();
        task4Completed = ScoreManager.isTask4Completed();
        bonus1Completed = ScoreManager.isBonus1Completed();
        bonus2Completed = ScoreManager.isBonus2Completed();
        
        // 修复计算总进度的逻辑，确保总共6个模块
        double completedTasks = 0.0;
        // Task1包括2D和3D两部分，各占0.5
        if (task1_2dCompleted) completedTasks += 0.5;
        if (task1_3dCompleted) completedTasks += 0.5;
        // 其他任务各占1
        if (task2Completed) completedTasks += 1.0;
        if (task3Completed) completedTasks += 1.0;
        if (task4Completed) completedTasks += 1.0;
        if (bonus1Completed) completedTasks += 1.0;
        if (bonus2Completed) completedTasks += 1.0;
        
        // 确保总共6个模块的进度
        progress_total = (completedTasks / 6.0) * 100.0;
        totalProgress = (int)Math.round(progress_total);
        
        // Initialize the layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create start screen
        startScreen = new StartScreen(this);
        
        // 初始化homePanel
        
        // Add panels to the card layout
        contentPanel.add(startScreen, START_SCREEN);
        //contentPanel.add(homePanel, HOME_SCREEN);
        
        // Add the main content panel
        add(contentPanel, BorderLayout.CENTER);
        
        // Create and add the navigation panel at the bottom
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);
        
        // 确保进度条显示正确的值
        progressBar.setValue((int)Math.round(progress_total));
        progressBar.setString((int)Math.round(progress_total) + "%");
        
        // Show start screen by default
        cardLayout.show(contentPanel, START_SCREEN);
        
        // Hide the progress bar initially (will show only in home screen)
        navPanel.setVisible(false);
        
        // Update score display
        scoreLabel.setText("Score: " + totalScore);
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorConstants.MAIN_BG_COLOR); // 使用木质风格的主背景色
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorConstants.TITLE_BG_COLOR); // 使用木质风格的标题背景色
        JLabel titleLabel = new JLabel("Welcome to Shapeville!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // 增大标题字体
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Center panel with task buttons
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 30, 30)); // 增加网格间距
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // 增加内边距
        centerPanel.setBackground(ColorConstants.MAIN_BG_COLOR);
        
        // Add task buttons with lock status based on access level
        addTaskButton(centerPanel, "Task 1: Shape Identification", "Learn to identify 2D and 3D shapes", e -> startTask1(), 1);
        addTaskButton(centerPanel, "Task 2: Angle Types", "Learn to identify different types of angles", e -> startTask2(), 1);
        addTaskButton(centerPanel, "Task 3: Area Calculation", "Calculate areas of different shapes", e -> startTask3(), 2);
        addTaskButton(centerPanel, "Task 4: Circle Calculations", "Calculate area and circumference", e -> startTask4(), 2);
        addTaskButton(centerPanel, "Bonus 1: Compound Shapes", "Calculate areas of compound shapes", e -> startBonus1(), 3);
        addTaskButton(centerPanel, "Bonus 2: Sector & Arc", "Calculate sector area and arc length", e -> startBonus2(), 3);
        shape2DPanel = new Shape2DPanel(this);
        contentPanel.add(shape2DPanel, SHAPE_2D_SCREEN);
        shape3DPanel = new Shape3DPanel(this);
        contentPanel.add(shape3DPanel, SHAPE_3D_SCREEN);
        anglePanel = new AnglePanel(this);
        contentPanel.add(anglePanel, ANGLE_SCREEN);
        areaPanel = new AreaPanel(this);
        contentPanel.add(areaPanel, AREA_SCREEN);
        circlePanel = new CirclePanel(this);
        contentPanel.add(circlePanel, CIRCLE_SCREEN);
        compoundPanel = new shapeville.bonus1.CompoundPanel(this);
        contentPanel.add(compoundPanel, "COMPOUND_SCREEN");
        sectorPanel = new shapeville.bonus2.SectorPanel(this);
        contentPanel.add(sectorPanel, "SECTOR_SCREEN");
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Game info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(ColorConstants.MAIN_BG_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); // 增加内边距
        
        // 使用普通文本而不是HTML
        JLabel infoLabel = new JLabel("Game Levels and Scoring:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 增大字体
        infoPanel.add(infoLabel, BorderLayout.NORTH);
        
        // 添加详细信息
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 0, 5)); // 增加行间距
        detailsPanel.setBackground(infoPanel.getBackground());
        
        JLabel basicLabel = new JLabel("Basic level: 3 points (1st attempt), 2 points (2nd attempt), 1 point (3rd attempt)");
        basicLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // 增大字体
        
        JLabel advancedLabel = new JLabel("Advanced level: 6 points (1st attempt), 4 points (2nd attempt), 2 points (3rd attempt)");
        advancedLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // 增大字体
        
        detailsPanel.add(basicLabel);
        detailsPanel.add(advancedLabel);
        infoPanel.add(detailsPanel, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addTaskButton(JPanel panel, String title, String description, ActionListener action, int requiredLevel) {
        // 使用新的WoodenButton构造函数，分别传递标题和描述
        JButton button = new WoodenButton(title, description);
        
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
                
                button.setIcon(lockIcon);
                button.setEnabled(true);
                button.setDisabledIcon(lockIcon);
            } catch (Exception e) {
                // If lock icon cannot be loaded, just use text
                ((WoodenButton)button).setDescription(description + " (🔒 Locked)");
                button.setEnabled(true);
            }
        }
        
        panel.add(button);
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(ColorConstants.NAV_BG_COLOR); // 使用木质风格的导航背景色
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int)Math.round(progress_total));
        progressBar.setStringPainted(true);
        progressBar.setString((int)Math.round(progress_total) + "%");
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(ColorConstants.WOOD_BORDER_COLOR); // 使用边框颜色作为进度条颜色
        
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
        
        JButton backButton = new WoodenButton("Back to Start");
        backButton.addActionListener(e -> returnToStartScreen());
        
        JButton homeButton = new WoodenButton("Home");
        homeButton.addActionListener(e -> returnToHome());
        
        JButton endButton = new WoodenButton("End Session");
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
        // 直接显示home界面，不重新创建
        cardLayout.show(contentPanel, HOME_SCREEN);
        
        // 显示导航面板
        getContentPane().getComponent(1).setVisible(true);
        
        // 添加键盘监听器
        Component homePanel = contentPanel.getComponent(1);
        homePanel.setFocusable(true);
        homePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    returnToStartScreen();
                }
            }
        });
        homePanel.requestFocusInWindow();
    }
    
    public void returnToStartScreen() {
        // 不再清除面板缓存
        /*
        shape2DPanel = null;
        shape3DPanel = null;
        anglePanel = null;
        areaPanel = null;
        circlePanel = null;
        sectorPanel = null;
        compoundPanel = null;
        */
        
        // 隐藏导航面板
        getContentPane().getComponent(1).setVisible(false);
        
        // 显示开始界面
        cardLayout.show(contentPanel, START_SCREEN);
        
        // 重置开始界面的导航控制
        startScreen.resetNavigation();
        
        // 请求开始界面的焦点以捕获键盘事件
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
        // Show the 2D shapes screen
        cardLayout.show(contentPanel, SHAPE_2D_SCREEN);
    }
    
    public void startTask1_3D() {
        // Initialize the 3D shapes panel if not already done
        // Show the 3D shapes screen
        cardLayout.show(contentPanel, SHAPE_3D_SCREEN);
    }
    
    // Task 2: Angle Type Identification
    public void startTask2() {
        // Initialize the angle panel if not already done 
        // Show the angle screen
        cardLayout.show(contentPanel, ANGLE_SCREEN);
    }
    
    // Task 3: Area Calculation
    public void startTask3() {
        // Initialize the area panel if not already done
        
        // Show the area screen
        cardLayout.show(contentPanel, AREA_SCREEN);
    }
    
    // Task 4: Circle Calculations
    public void startTask4() {
        // Initialize the circle panel if not already don 
        // Show the circle screen
        cardLayout.show(contentPanel, CIRCLE_SCREEN);
    }

    // Bonus 1: Compound Shapes Area Calculation
    public void startBonus1() {
        // 初始化CompoundPanel，如果未创建则新建

        // 显示CompoundPanel
        cardLayout.show(contentPanel, "COMPOUND_SCREEN");
    }
    
    // Bonus 2: Sector Area and Arc Length Calculation
    public void startBonus2() {
        // 初始化SectorPanel，如果未创建则新建
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
        int progressValue = (int)Math.round(progress_total);
        progressBar.setValue(progressValue);
        progressBar.setString(progressValue + "%");
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
        double increment = 100.0 / 6.0 * part; // 每个模块占16.67%
        if (part == 0.5) { // 2D或3D部分完成
            if (!task1_2dCompleted && part == 0.5) {
                task1_2dCompleted = true;
                ScoreManager.setTask1_2dCompleted(true);
                progress_total += increment;
                System.out.println("Task1 2D完成: progress_total = " + progress_total);
                progressBar.setValue((int)Math.round(progress_total));
                progressBar.setString((int)Math.round(progress_total) + "%");
            }
        }
    }
    
    public void addTask1Progress3DPart() {
        double increment = 100.0 / 6.0 * 0.5; // 3D部分占总进度的8.33%
        if (!task1_3dCompleted) {
            task1_3dCompleted = true;
            ScoreManager.setTask1_3dCompleted(true);
            progress_total += increment;
            System.out.println("Task1 3D完成: progress_total = " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
            progressBar.setString((int)Math.round(progress_total) + "%");
        }
    }
    
    public boolean isTask1_2dCompleted() { 
        return task1_2dCompleted || ScoreManager.isTask1_2dCompleted(); 
    }
    
    public boolean isTask1_3dCompleted() { 
        return task1_3dCompleted || ScoreManager.isTask1_3dCompleted();
    }
    
    // 统一进度加分方法
    public void addTask2Progress() {
        double increment = 100.0 / 6.0; // 每个模块占16.67%
        if (!task2Completed) {
            task2Completed = true;
            ScoreManager.setTask2Completed(true);
            progress_total += increment;
            System.out.println("Task2完成: progress_total = " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
            progressBar.setString((int)Math.round(progress_total) + "%");
        }
    }
    
    public void addTask3Progress() {
        double increment = 100.0 / 6.0;
        if (!task3Completed) {
            task3Completed = true;
            ScoreManager.setTask3Completed(true);
            progress_total += increment;
            System.out.println("Task3完成: progress_total = " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
            progressBar.setString((int)Math.round(progress_total) + "%");
        }
    }
    
    public void addTask4Progress() {
        double increment = 100.0 / 6.0;
        if (!task4Completed) {
            task4Completed = true;
            ScoreManager.setTask4Completed(true);
            progress_total += increment;
            System.out.println("Task4完成: progress_total = " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
            progressBar.setString((int)Math.round(progress_total) + "%");
        }
    }
    
    public void addBonus1Progress() {
        double increment = 100.0 / 6.0;
        if (!bonus1Completed) {
            bonus1Completed = true;
            ScoreManager.setBonus1Completed(true);
            progress_total += increment;
            System.out.println("Bonus1完成: progress_total = " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
            progressBar.setString((int)Math.round(progress_total) + "%");
        }
    }
    
    public void addBonus2Progress() {
        double increment = 100.0 / 6.0;
        if (!bonus2Completed) {
            bonus2Completed = true;
            ScoreManager.setBonus2Completed(true);
            progress_total += increment;
            System.out.println("Bonus2完成: progress_total = " + progress_total);
            progressBar.setValue((int)Math.round(progress_total));
            progressBar.setString((int)Math.round(progress_total) + "%");
        }
    }
    
    public boolean isTask2Completed() { 
        return task2Completed || ScoreManager.isTask2Completed(); 
    }
    
    public boolean isTask3Completed() { 
        return task3Completed || ScoreManager.isTask3Completed(); 
    }
    
    public boolean isTask4Completed() { 
        return task4Completed || ScoreManager.isTask4Completed(); 
    }
    
    public boolean isBonus1Completed() { 
        return bonus1Completed || ScoreManager.isBonus1Completed(); 
    }
    
    public boolean isBonus2Completed() { 
        return bonus2Completed || ScoreManager.isBonus2Completed(); 
    }
    
    // Set access level based on start screen selection
    public void setAccessLevel(int level) {
        this.accessLevel = level;
        System.out.println("Access level set to: " + level);
    }
} 