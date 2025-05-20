package shapeville.bonus2;

import shapeville.ScoreManager;
import shapeville.ShapevilleApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Bonus2 扇形面积与周长练习面板
 * Sector Area & Perimeter Practice Panel
 */
public class SectorPanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final List<SectorData> sectors; // 8个扇形数据
    private int currentIndex = -1; // Changed to -1 to indicate no sector selected
    private int attempts = 0;
    private int completed = 0;
    private static final int TOTAL = 8;
    private static final int MAX_ATTEMPTS = ScoreManager.MAX_ATTEMPTS;
    private static final int TIME_LIMIT = 300; // 5分钟，单位秒
    private Timer countdownTimer;
    private int secondsRemaining;
    private boolean[] completedSectors; // Track which sectors are completed
    private static boolean hasCompletedAllSectors = false; // 添加静态变量来记住完成状态
    private JProgressBar bonus2Progress; // 添加 Bonus2 的进度条

    // UI组件
    private JPanel sectorSelectionPanel; // New panel for sector selection
    private JPanel practicePanel; // Panel for practice interface
    private JLabel promptLabel, attemptsLabel, progressLabel, timerLabel, feedbackLabel;
    private JTextField areaField, perimeterField;
    private JButton submitButton, homeButton, nextButton;
    private JPanel mainPanel;
    private DecimalFormat df = new DecimalFormat("0.00");

    private SectorDisplayPanel displayPanel; // 扇形图形显示面板

    public SectorPanel(ShapevilleApp app) {
        this.mainApp = app;
        this.sectors = createSectors();
        this.completedSectors = new boolean[TOTAL];
        if (hasCompletedAllSectors) {
            // 如果之前已经完成了所有扇形，则将所有扇形标记为已完成
            for (int i = 0; i < TOTAL; i++) {
                completedSectors[i] = true;
            }
            completed = TOTAL;
        }
        setLayout(new BorderLayout());
        createAndShowSectorSelection();
    }

    // 创建8个扇形数据
    private List<SectorData> createSectors() {
        List<SectorData> list = new ArrayList<>();
        list.add(new SectorData(8, 90, "cm"));
        list.add(new SectorData(18, 130, "ft"));
        list.add(new SectorData(19, 120, "cm"));
        list.add(new SectorData(22, 110, "ft"));
        list.add(new SectorData(3.5, 100, "m"));
        list.add(new SectorData(8, 270, "in"));
        list.add(new SectorData(12, 280, "yd"));
        list.add(new SectorData(15, 250, "mm"));
        return list;
    }

    private void createAndShowSectorSelection() {
        removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        
        // Title
        JLabel title = new JLabel("Bonus 2: Select a Sector to Practice");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Grid of sectors
        sectorSelectionPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        sectorSelectionPanel.setBackground(panel.getBackground());
        sectorSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Check if all sectors are completed
        boolean allCompleted = true;
        for (int i = 0; i < sectors.size(); i++) {
            if (!completedSectors[i]) {
                allCompleted = false;
            }
            SectorData sector = sectors.get(i);
            JPanel sectorButton = createSectorButton(sector, i);
            sectorSelectionPanel.add(sectorButton);
        }
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        centerPanel.add(sectorSelectionPanel, BorderLayout.CENTER);
        
        // Progress panel
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(panel.getBackground());
        
        // Progress label
        JLabel progressLabel = new JLabel(String.format("Completed: %d/%d", completed, TOTAL));
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        progressLabel.setHorizontalAlignment(JLabel.CENTER);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        
        // Progress bar
        bonus2Progress = new JProgressBar(0, 100);
        bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
        bonus2Progress.setStringPainted(true);
        bonus2Progress.setString(bonus2Progress.getValue() + "%");
        progressPanel.add(bonus2Progress, BorderLayout.CENTER);
        
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // If all sectors are completed, show return to home button
        if (allCompleted) {
            JButton homeButton = new JButton("Return to Home");
            homeButton.addActionListener(e -> returnToHomeWithProgress());
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(panel.getBackground());
            buttonPanel.add(homeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        add(panel);
        revalidate();
        repaint();
    }

    private JPanel createSectorButton(SectorData sector, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Create a small version of sector display
        JPanel miniSectorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 计算面板尺寸和中心点
                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;
                int cy = h / 2;
                
                // 计算绘图尺寸和位置
                int size = Math.min(w, h);
                int x = (w - size) / 2;
                int y = (h - size) / 2;
                
                // 设置颜色
                if (completedSectors[index]) {
                    g2.setColor(new Color(200, 200, 200)); // Gray for completed
                } else {
                    g2.setColor(new Color(255, 192, 203)); // Pink for uncompleted
                }
                
                // 绘制扇形
                g2.fillArc(x, y, size, size, 0, -sector.angle);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(x, y, size, size, 0, -sector.angle);
                
                // 绘制半径线
                int radius = size / 2;
                
                // 第一条半径线 (0度)
                int endX1 = cx + (int) Math.round(radius * Math.cos(0));
                int endY1 = cy - (int) Math.round(radius * Math.sin(0));
                g2.drawLine(cx, cy, endX1, endY1);
                
                // 第二条半径线 (-angle度)
                double angleRad = Math.toRadians(-sector.angle);
                int endX2 = cx + (int) Math.round(radius * Math.cos(angleRad));
                int endY2 = cy - (int) Math.round(radius * Math.sin(angleRad));
                g2.drawLine(cx, cy, endX2, endY2);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }
        };
        miniSectorPanel.setOpaque(false);
        
        // Make the panel clickable
        if (!completedSectors[index]) {
            miniSectorPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            miniSectorPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    startPractice(index);
                }
            });
        }
        
        panel.add(miniSectorPanel, BorderLayout.CENTER);
        
        // Add sector info
        JLabel infoLabel = new JLabel(String.format("<html>r=%.2f %s<br>angle=%d°</html>", 
                                    sector.radius, sector.unit, sector.angle));
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void startPractice(int index) {
        currentIndex = index;
        removeAll();
        add(createMainPanel());
        revalidate();
        repaint();
        displaySector(currentIndex);
    }

    // 创建主界面
    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 240));

        JLabel title = new JLabel("Bonus 2: Sector Area & Perimeter");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setOpaque(true);
        title.setBackground(mainPanel.getBackground());
        mainPanel.add(title, BorderLayout.NORTH);

        // 创建左右分隔的面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        
        // 左侧面板 - 扇形显示
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(mainPanel.getBackground());
        
        displayPanel = new SectorDisplayPanel();
        displayPanel.setPreferredSize(new Dimension(300, 300));
        displayPanel.setMaximumSize(new Dimension(300, 300));
        displayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayPanel.setOpaque(true);
        displayPanel.setBackground(mainPanel.getBackground());
        leftPanel.add(displayPanel);
        
        promptLabel = new JLabel();
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        promptLabel.setOpaque(true);
        promptLabel.setBackground(mainPanel.getBackground());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(promptLabel);
        
        splitPane.setLeftComponent(leftPanel);
        
        // 右侧面板 - 输入和解答
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(mainPanel.getBackground());
        
        // 输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(mainPanel.getBackground());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        areaField = new JTextField(15);
        areaField.setMaximumSize(new Dimension(300, 35));
        areaField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(areaField);
        inputPanel.add(Box.createVerticalStrut(10));

        perimeterField = new JTextField(15);
        perimeterField.setMaximumSize(new Dimension(300, 35));
        perimeterField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(perimeterField);
        inputPanel.add(Box.createVerticalStrut(15));

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(255, 0, 0)); // Red background
        submitButton.setForeground(Color.WHITE); // White text
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        submitButton.setOpaque(true); // 确保背景色可见
        submitButton.setBorderPainted(false); // 移除边框
        submitButton.addActionListener(e -> checkAnswer());
        inputPanel.add(submitButton);
        
        rightPanel.add(inputPanel);
        
        // 解答面板
        JPanel solutionPanel = new JPanel();
        solutionPanel.setLayout(new BoxLayout(solutionPanel, BoxLayout.Y_AXIS));
        solutionPanel.setBackground(mainPanel.getBackground());
        solutionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        feedbackLabel.setOpaque(true);
        feedbackLabel.setBackground(mainPanel.getBackground());
        solutionPanel.add(feedbackLabel);
        
        rightPanel.add(solutionPanel);
        
        splitPane.setRightComponent(rightPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // 底部状态面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(mainPanel.getBackground());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statusPanel.setBackground(mainPanel.getBackground());

        attemptsLabel = new JLabel();
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(attemptsLabel);

        progressLabel = new JLabel();
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(progressLabel);

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(timerLabel);

        bottomPanel.add(statusPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(mainPanel.getBackground());

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(50, 205, 50));
        nextButton.setForeground(Color.BLACK);
        nextButton.setFocusPainted(false);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextSector());
        btnPanel.add(nextButton);

        bottomPanel.add(btnPanel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // 显示第index个扇形
    private void displaySector(int index) {
        if (index >= TOTAL) {
            showCompletionPanel();
            return;
        }
        SectorData s = sectors.get(index);
        displayPanel.setSector(s.radius, s.angle, s.unit);
        String sectorInfo = String.format("Sector %d/8: radius=%.2f %s, angle=%d°", 
                index + 1, s.radius, s.unit, s.angle);
        promptLabel.setText(sectorInfo);
        areaField.setText("");
        perimeterField.setText("");
        areaField.setBorder(BorderFactory.createTitledBorder(
            String.format("Area (%s²)", s.unit)));
        perimeterField.setBorder(BorderFactory.createTitledBorder(
            String.format("Perimeter (%s)", s.unit)));
        feedbackLabel.setText(" ");
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        startTimer();
    }

    // 检查答案
    private void checkAnswer() {
        String areaStr = areaField.getText().trim();
        String periStr = perimeterField.getText().trim();
        if (areaStr.isEmpty() || periStr.isEmpty()) {
            feedbackLabel.setText("Please enter both area and perimeter!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        double userArea, userPeri;
        try {
            userArea = Double.parseDouble(areaStr);
            userPeri = Double.parseDouble(periStr);
        } catch (Exception e) {
            feedbackLabel.setText("Please enter valid numbers!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        SectorData s = sectors.get(currentIndex);
        double correctArea = s.getArea();
        double correctPeri = s.getPerimeter();
        boolean areaOK = Math.abs(userArea - correctArea) < 0.1;
        boolean periOK = Math.abs(userPeri - correctPeri) < 0.1;
        
        if (areaOK && periOK) {
            if (countdownTimer != null) countdownTimer.stop();
            int score = ScoreManager.calculateScore(true, attempts);
            feedbackLabel.setText("Correct! +" + score + " points");
            feedbackLabel.setForeground(new Color(0, 128, 0));
            mainApp.updateScore(score);
            completedSectors[currentIndex] = true;
            completed++;
            if (completed == TOTAL) {
                hasCompletedAllSectors = true; // 标记为全部完成
            }
            progressLabel.setText("Progress: " + completed + "/" + TOTAL);
            // 更新 Bonus2 的进度条
            if (bonus2Progress != null) {
                bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
                bonus2Progress.setString(bonus2Progress.getValue() + "%");
            }
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
            showDetailedSolution(s);
        } else {
            feedbackLabel.setText("Wrong, try again!");
            feedbackLabel.setForeground(Color.RED);
            if (attempts >= MAX_ATTEMPTS) {
                if (countdownTimer != null) countdownTimer.stop();
                completedSectors[currentIndex] = true;
                completed++;
                progressLabel.setText("Progress: " + completed + "/" + TOTAL);
                // 更新 Bonus2 的进度条
                if (bonus2Progress != null) {
                    bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
                    bonus2Progress.setString(bonus2Progress.getValue() + "%");
                }
                showDetailedSolution(s);
                submitButton.setEnabled(false);
                nextButton.setEnabled(true);
            }
        }
    }

    // 下一个扇形
    private void nextSector() {
        if (currentIndex >= 0) {
            completedSectors[currentIndex] = true;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        createAndShowSectorSelection();
    }

    // 计时器
    private void startTimer() {
        secondsRemaining = TIME_LIMIT;
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            secondsRemaining--;
            int min = secondsRemaining / 60;
            int sec = secondsRemaining % 60;
            timerLabel.setText(String.format("Time left: %d:%02d", min, sec));
            if (secondsRemaining <= 0) {
                countdownTimer.stop();
                timeExpired();
            } else if (secondsRemaining <= 30) {
                timerLabel.setForeground(Color.RED);
            } else {
                timerLabel.setForeground(Color.BLACK);
            }
        });
        countdownTimer.start();
        timerLabel.setForeground(Color.BLACK);
    }

    // 时间到
    private void timeExpired() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        completedSectors[currentIndex] = true;
        completed++;
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        // 更新 Bonus2 的进度条
        if (bonus2Progress != null) {
            bonus2Progress.setValue((int)((completed * 100.0) / TOTAL));
            bonus2Progress.setString(bonus2Progress.getValue() + "%");
        }
        showDetailedSolution(sectors.get(currentIndex));
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
    }

    // 完成界面
    private void showCompletionPanel() {
        removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 255, 240));
        JLabel label = new JLabel("Congratulations! All sectors completed!");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        JButton homeBtn = new JButton("Home");
        homeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        homeBtn.setBackground(new Color(70, 130, 180));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setFocusPainted(false);
        homeBtn.addActionListener(e -> returnToHomeWithProgress());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(panel.getBackground());
        btnPanel.add(homeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
        // 新增：全部完成时加主进度
        mainApp.addBonus2Progress();
    }

    private void showDetailedSolution(SectorData s) {
        JTextArea solutionArea = new JTextArea();
        solutionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solutionArea.setBackground(mainPanel.getBackground());
        solutionArea.setEditable(false);
        solutionArea.setLineWrap(true);
        solutionArea.setWrapStyleWord(true);
        
        // Calculate with PI = 3.14
        double PI = 3.14;
        double areaStep1 = s.angle / 360.0;
        double areaStep2 = areaStep1 * PI * s.radius * s.radius;
        
        double arcLength = s.angle / 360.0 * 2 * PI * s.radius;
        double perimeter = arcLength + 2 * s.radius;
        
        StringBuilder solution = new StringBuilder();
        
        // First show the correctness of user's answer
        String userArea = areaField.getText().trim();
        String userPeri = perimeterField.getText().trim();
        try {
            double userAreaVal = Double.parseDouble(userArea);
            double userPeriVal = Double.parseDouble(userPeri);
            
            solution.append("Your answer:\n");
            solution.append(String.format("Area = %.2f %s² ", userAreaVal, s.unit));
            if (Math.abs(userAreaVal - areaStep2) < 0.1) {
                solution.append("(Correct)\n");
            } else {
                solution.append("(Incorrect)\n");
            }
            
            solution.append(String.format("Perimeter = %.2f %s ", userPeriVal, s.unit));
            if (Math.abs(userPeriVal - perimeter) < 0.1) {
                solution.append("(Correct)\n");
            } else {
                solution.append("(Incorrect)\n");
            }
            solution.append("\n");
        } catch (NumberFormatException e) {
            // If user input was not valid numbers, skip this part
        }
        
        // Show the detailed solution
        solution.append("Area calculation steps:\n");
        solution.append("1. Formula:\n   Area = (central angle/360°) × π × r²\n");
        solution.append(String.format("2. Substitute:\n   (%d°/360°) × 3.14 × %.2f²\n", 
                       s.angle, s.radius));
        solution.append(String.format("3. Calculate:\n   %.3f × 3.14 × %.3f\n", 
                       areaStep1, s.radius * s.radius));
        solution.append(String.format("4. Result:\n   %.2f %s²\n\n", areaStep2, s.unit));
        
        solution.append("Perimeter calculation steps:\n");
        solution.append("1. Formula:\n   Perimeter = arc length + 2r\n");
        solution.append("2. Arc length formula:\n   Arc = (angle/360°) × 2π × r\n");
        solution.append(String.format("3. Substitute:\n   (%d°/360°) × 2 × 3.14 × %.2f + 2 × %.2f\n", 
                       s.angle, s.radius, s.radius));
        solution.append(String.format("4. Arc length:\n   %.3f × 2 × 3.14 × %.2f = %.2f\n", 
                       s.angle/360.0, s.radius, arcLength));
        solution.append(String.format("5. Final result:\n   %.2f + %.2f = %.2f %s\n", 
                       arcLength, 2 * s.radius, perimeter, s.unit));
        
        solutionArea.setText(solution.toString());
        
        // Update the right panel
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JSplitPane) {
                JSplitPane splitPane = (JSplitPane) comp;
                JPanel rightPanel = (JPanel) splitPane.getRightComponent();
                rightPanel.removeAll();
                
                // Re-add input panel
                JPanel inputPanel = new JPanel();
                inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
                inputPanel.setBackground(mainPanel.getBackground());
                inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                
                areaField.setMaximumSize(new Dimension(300, 35));
                perimeterField.setMaximumSize(new Dimension(300, 35));
                
                inputPanel.add(areaField);
                inputPanel.add(Box.createVerticalStrut(10));
                inputPanel.add(perimeterField);
                inputPanel.add(Box.createVerticalStrut(15));
                inputPanel.add(submitButton);
                
                rightPanel.add(inputPanel);
                
                // Add solution panel with scroll capability
                JPanel solutionPanel = new JPanel();
                solutionPanel.setLayout(new BorderLayout());
                solutionPanel.setBackground(mainPanel.getBackground());
                solutionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
                
                JScrollPane scrollPane = new JScrollPane(solutionArea);
                scrollPane.setBorder(null);
                scrollPane.setBackground(mainPanel.getBackground());
                scrollPane.getViewport().setBackground(mainPanel.getBackground());
                scrollPane.setPreferredSize(new Dimension(280, 300));
                
                solutionPanel.add(scrollPane, BorderLayout.CENTER);
                rightPanel.add(solutionPanel);
                
                rightPanel.revalidate();
                rightPanel.repaint();
                break;
            }
        }
    }

    // 扇形数据类
    static class SectorData {
        double radius;
        int angle;
        String unit;
        public SectorData(double r, int a, String u) {
            this.radius = r;
            this.angle = a;
            this.unit = u;
        }
        // Area calculation with PI = 3.14
        public double getArea() {
            return angle / 360.0 * 3.14 * radius * radius;
        }
        // Perimeter calculation with PI = 3.14
        public double getPerimeter() {
            return angle / 360.0 * 2 * 3.14 * radius + 2 * radius;
        }
    }

    /**
     * 内部类：用于绘制当前扇形图形
     */
    class SectorDisplayPanel extends JPanel {
        private double radius = 8;
        private int angle = 90;
        private String unit = "cm";
        
        public SectorDisplayPanel() {
            setOpaque(true);
        }

        public void setSector(double r, int a, String u) {
            this.radius = r;
            this.angle = a;
            this.unit = u;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(255, 250, 240));
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 计算面板尺寸和中心点
            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;
            
            // 计算绘图半径和位置
            int drawRadius = (int) Math.round(Math.min(w, h) * 0.35);
            int x = cx - drawRadius;
            int y = cy - drawRadius;
            int drawSize = drawRadius * 2;
            
            // 扇形填充
            g2.setColor(new Color(255, 192, 203));
            g2.fillArc(x, y, drawSize, drawSize, 0, -angle);
            
            // 扇形边框
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawArc(x, y, drawSize, drawSize, 0, -angle);
            
            // 绘制半径线
            // 第一条半径线 (0度)
            int endX1 = cx + (int) Math.round(drawRadius * Math.cos(0));
            int endY1 = cy - (int) Math.round(drawRadius * Math.sin(0));
            g2.drawLine(cx, cy, endX1, endY1);
            
            // 第二条半径线 (-angle度)
            double angleRad = Math.toRadians(-angle);
            int endX2 = cx + (int) Math.round(drawRadius * Math.cos(angleRad));
            int endY2 = cy - (int) Math.round(drawRadius * Math.sin(angleRad));
            g2.drawLine(cx, cy, endX2, endY2);
            
            // 标注
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.setColor(Color.BLACK);
            
            // 半径标注
            String radiusText = String.format("r=%.2f %s", radius, unit);
            g2.drawString(radiusText, cx + 5, cy - 5);
            
            // 角度标注
            String angleText = String.format("%d°", angle);
            double midAngle = Math.toRadians(-angle / 2.0);
            int labelRadius = (int) Math.round(drawRadius * 0.3);
            int labelX = cx + (int) Math.round(labelRadius * Math.cos(midAngle));
            int labelY = cy - (int) Math.round(labelRadius * Math.sin(midAngle));
            g2.drawString(angleText, labelX, labelY);
        }
    }

    // 添加一个新方法来处理返回主页
    private void returnToHomeWithProgress() {
        if (isAllSectorsCompleted()) {
            mainApp.updateProgress(100.0/6); // Bonus2 完成时更新主进度条
        }
        mainApp.returnToHome();
    }

    // 修改 isAllSectorsCompleted 方法
    public boolean isAllSectorsCompleted() {
        if (completedSectors == null) return hasCompletedAllSectors;
        for (boolean completed : completedSectors) {
            if (!completed) return false;
        }
        hasCompletedAllSectors = true; // 记住完成状态
        return true;
    }

    // 添加一个公共方法供外部调用来更新进度
    public void updateMainProgressIfCompleted() {
        if (isAllSectorsCompleted()) {
            mainApp.updateProgress(100.0/6);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // 当面板被移除时（比如切换到其他面板时），检查并更新进度
        updateMainProgressIfCompleted();
    }
} 