package shapeville.bonus1;

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
public class CompoundPanel extends JPanel {
    private final ShapevilleApp mainApp;
    private final List<CompoundData> compounds; // 8个扇形数据
    private int currentIndex = -1; // Changed to -1 to indicate no sector selected
    private int attempts = 0;
    private int completed = 0;
    private static final int TOTAL = 6;
    private static final int MAX_ATTEMPTS = ScoreManager.MAX_ATTEMPTS;
    private static final int TIME_LIMIT = 300; // 5分钟，单位秒
    private Timer countdownTimer;
    private int secondsRemaining;
    private boolean[] completedSectors; // Track which sectors are completed
    private static boolean hasCompletedAllSectors = false; // 添加静态变量来记住完成状态
    private JProgressBar bonus1Progress; // 添加 Bonus2 的进度条

    // UI组件
    private JPanel compoundSelectionPanel; // New panel for sector selection
    //private JPanel practicePanel; // Panel for practice interface
    private JLabel promptLabel, attemptsLabel, progressLabel, timerLabel, feedbackLabel;
    private JTextField areaField, perimeterField;
    private JButton submitButton, homeButton, nextButton;
    private JPanel mainPanel;
    private DecimalFormat df = new DecimalFormat("0.00");

    private CompoundDisplayPanel displayPanel; // 扇形图形显示面板

    public CompoundPanel(ShapevilleApp app) {
        this.mainApp = app;
        this.compounds = createCompounds();
        this.completedSectors = new boolean[TOTAL];
        if (hasCompletedAllSectors) {
            // 如果之前已经完成了所有扇形，则将所有扇形标记为已完成
            for (int i = 0; i < TOTAL; i++) {
                completedSectors[i] = true;
            }
            completed = TOTAL;
        }
        setLayout(new BorderLayout());
        createAndShowCompoundSelection();
    }

    // 创建8个扇形数据
    private List<CompoundData> createCompounds() {
        List<CompoundData> list = new ArrayList<>();
        list.add(new CompoundData("./sourses/Compound1.jpg", "cm", 310.0));
        list.add(new CompoundData("./sourses/Compound2.jpg", "cm", 598.0));
        list.add(new CompoundData("./sourses/Compound3.jpg", "m", 288.0));
        list.add(new CompoundData("./sourses/Compound4.jpg", "m", 18.0));
        list.add(new CompoundData("./sourses/Compound5.jpg", "m", 3456.0));
        list.add(new CompoundData("./sourses/Compound6.jpg", "m", 174.0));
        return list;
    }

    private void createAndShowCompoundSelection() {
        removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        
        // Title
        JLabel title = new JLabel("Bonus 1: Select a Compound to Practice");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Grid of sectors
        compoundSelectionPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        compoundSelectionPanel.setBackground(panel.getBackground());
        compoundSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Check if all sectors are completed
        boolean allCompleted = true;
        for (int i = 0; i < compounds.size(); i++) {
            if (!completedSectors[i]) {
                allCompleted = false;
            }
            CompoundData compound = compounds.get(i);
            JPanel compoundButton = createCompoundButton(compound, i);
            compoundSelectionPanel.add(compoundButton);
        }
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(panel.getBackground());
        centerPanel.add(compoundSelectionPanel, BorderLayout.CENTER);
        
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
        bonus1Progress = new JProgressBar(0, 100);
        bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
        bonus1Progress.setStringPainted(true);
        bonus1Progress.setString(bonus1Progress.getValue() + "%");
        progressPanel.add(bonus1Progress, BorderLayout.CENTER);
        
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

    private JPanel createCompoundButton(CompoundData compound, int index) {
        ImageIcon icon;
        if(completedSectors[index]){
            icon = new ImageIcon(compound.img_path.replace(".jpg", "_gray.jpg"));
        }else{
            icon = new ImageIcon(compound.img_path);
        }
        
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Failed to load image: " + compound.img_path);
        }
        Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(image);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JButton button = new JButton(scaledIcon);
        if (!completedSectors[index]) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startPractice(index);
                }
            });
        }
        panel.add(button, BorderLayout.CENTER);
        
        return panel;
    }

    private void startPractice(int index) {
        currentIndex = index;
        removeAll();
        add(createMainPanel());
        revalidate();
        repaint();
        displayCompound(currentIndex);
    }

    // 创建主界面
    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 240));

        JLabel title = new JLabel("Bonus 1: Compound Area & Perimeter");
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
        
        displayPanel = new CompoundDisplayPanel();
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
    private void displayCompound(int index) {
        if (index >= TOTAL) {
            showCompletionPanel();
            return;
        }
        CompoundData c = compounds.get(index);
        displayPanel.setCompound(c.img_path);
        String compoundInfo = String.format("Compound %d/6", 
                index + 1);
        promptLabel.setText(compoundInfo);
        areaField.setText("");
        areaField.setBorder(BorderFactory.createTitledBorder(
            String.format("Area (%s²)", c.unit)));
        feedbackLabel.setText(" ");
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        progressLabel.setText("Progress: " + completed + "/" + TOTAL);
        startTimer();
    }

    // 检查答案
    private void checkAnswer() {
        String areaStr = areaField.getText().trim();

        if (areaStr.isEmpty()) {
            feedbackLabel.setText("Please enter the area!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        double userArea;
        try {
            userArea = Double.parseDouble(areaStr);
        } catch (Exception e) {
            feedbackLabel.setText("Please enter valid number!");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + "/" + MAX_ATTEMPTS);
        CompoundData c = compounds.get(currentIndex);
        double correctArea = c.area;
        boolean areaOK = Math.abs(userArea - correctArea) < 0.1;

        
        if (areaOK) {
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
            if (bonus1Progress != null) {
                bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
                bonus1Progress.setString(bonus1Progress.getValue() + "%");
            }
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
            showDetailedSolution(c);
        } else {
            feedbackLabel.setText("Wrong, try again!");
            feedbackLabel.setForeground(Color.RED);
            if (attempts >= MAX_ATTEMPTS) {
                if (countdownTimer != null) countdownTimer.stop();
                completedSectors[currentIndex] = true;
                completed++;
                progressLabel.setText("Progress: " + completed + "/" + TOTAL);
                // 更新 Bonus2 的进度条
                if (bonus1Progress != null) {
                    bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
                    bonus1Progress.setString(bonus1Progress.getValue() + "%");
                }
                showDetailedSolution(c);
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
        createAndShowCompoundSelection();
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
        if (bonus1Progress != null) {
            bonus1Progress.setValue((int)((completed * 100.0) / TOTAL));
            bonus1Progress.setString(bonus1Progress.getValue() + "%");
        }
        showDetailedSolution(compounds.get(currentIndex));
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
    }

    // 完成界面
    private void showCompletionPanel() {
        removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 255, 240));
        JLabel label = new JLabel("Congratulations! All compounds completed!");
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
    }

    private void showDetailedSolution(CompoundData c) {
        JTextArea solutionArea = new JTextArea();
        solutionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solutionArea.setBackground(mainPanel.getBackground());
        solutionArea.setEditable(false);
        solutionArea.setLineWrap(true);
        solutionArea.setWrapStyleWord(true);

        StringBuilder solution = new StringBuilder();
        
        // First show the correctness of user's answer
        String userArea = areaField.getText().trim();

        try {
            double userAreaVal = Double.parseDouble(userArea);
            
            solution.append("Your answer:\n");
            solution.append(String.format("Area = %.2f %s² ", userAreaVal, c.unit));
            if (Math.abs(userAreaVal - c.area) < 0.1) {
                solution.append("(Correct)\n");
            } else {
                solution.append("(Incorrect)\n");
            }
            
            solution.append("\n");
            solution.append(c.answer);
        } catch (NumberFormatException e) {
            // If user input was not valid numbers, skip this part
        }
        
        // Show the detailed solution
        
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
    static class CompoundData {
        String img_path;
        String unit;
        double area;
        String answer;
        public CompoundData(String img_path, String unit, double area) {
            this.img_path = img_path;
            this.unit = unit;
            this.area = area;
            if (img_path.contains("Compound1")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper left rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 10 × 11 = 110 cm²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 10 × 20 = 200 cm²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 110 + 200 = 310 cm²";
            }
            else if (img_path.contains("Compound2")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper left rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 3 × 18 = 54 cm²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 16 × 34 = 544 cm²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 54 + 544 = 598 cm²";
            }
            else if (img_path.contains("Compound3")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 12 × 12 = 144 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 6 × 24 = 144 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 144 + 144 = 288 m²\n";
            }
            else if (img_path.contains("Compound4")){
                this.answer = "1.This compound shape can be divided into a upper triangle and a lower rectangle.\n2.The area of the upper triangle can be derived from the formula of a triangle (base × height / 2).\n3.S1 = 3 × 4 / 2 = 6 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 3 × 4 = 12 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 6 + 12 = 18 m²\n";
            }
            else if (img_path.contains("Compound5")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 36 × 36 = 1296 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 36 × 60 = 2160 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 1296 + 2160 = 3456 m²\n";
            }
            else if (img_path.contains("Compound6")){
                this.answer = "1.This compound shape can be divided into two rectangles.\n2.The area of the upper rectangle can be derived from the formula of a rectangle (length × width).\n3.S1 = 3 × 10 = 30 m²\n4.The area of the lower rectangle can be derived from the formula of a rectangle (length × width).\n5.S2 = 8 × 18 = 144 m²\n6.The total area of the compound shape is the sum of the areas of the two rectangles.\n7.S = S1 + S2 = 30 + 144 = 174 m²\n";
            }
        }
    }

    /**
     * 内部类：用于绘制当前扇形图形
     */
    class CompoundDisplayPanel extends JPanel {
        private String img_path;
        private Image image;
        
        public CompoundDisplayPanel() {
            setOpaque(true);
        }

        public void setCompound(String img_path) {
            this.img_path = img_path;
            // 加载图片
            ImageIcon icon = new ImageIcon(img_path);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                image = icon.getImage();
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(255, 250, 240));
            
            if (image != null) {
                // 计算图片的显示尺寸，保持宽高比
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = image.getWidth(this);
                int imgHeight = image.getHeight(this);
                
                // 计算缩放比例
                double scale = Math.min(
                    (double) panelWidth / imgWidth,
                    (double) panelHeight / imgHeight
                );
                
                // 计算缩放后的尺寸
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);
                
                // 计算居中位置
                int x = (panelWidth - scaledWidth) / 2;
                int y = (panelHeight - scaledHeight) / 2;
                
                // 绘制图片
                g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
            }
        }
    }

    // 添加一个新方法来处理返回主页
    private void returnToHomeWithProgress() {
        if (isAllCompoundsCompleted()) {
            mainApp.updateProgress(100/6); // Bonus2 完成时更新主进度条
        }
        mainApp.returnToHome();
    }

    // 修改 isAllSectorsCompleted 方法
    public boolean isAllCompoundsCompleted() {
        if (completedSectors == null) return hasCompletedAllSectors;
        for (boolean completed : completedSectors) {
            if (!completed) return false;
        }
        hasCompletedAllSectors = true; // 记住完成状态
        return true;
    }

    // 添加一个公共方法供外部调用来更新进度
    public void updateMainProgressIfCompleted() {
        if (isAllCompoundsCompleted()) {
            mainApp.updateProgress(100/6);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // 当面板被移除时（比如切换到其他面板时），检查并更新进度
        updateMainProgressIfCompleted();
    }
} 