package shapeville;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

public class StartScreen extends JPanel implements KeyListener {
    private ShapevilleApp app;
    private Image littlePersonImage;
    private Image cloudImage;
    private Image lockImage;
    private Image bonusImage;
    
    // Add navigation control flag
    private boolean navigationTriggered = false;
    
    // Little person properties - start on the ground
    private int personX = 180;
    private int personY = 620;
    private int personWidth = 50;
    private int personHeight = 50;
    private boolean isJumping = false;
    private boolean isFalling = false;
    private int jumpCount = 0;
    private int maxJumpCount = 2; // 允许二段跳
    private int jumpVelocity = -15;
    private int fallVelocity = 0;
    private int gravity = 1;
    private int groundLevel = 620; // Starting ground level
    
    // Game areas
    private Rectangle titleArea = new Rectangle(350, 320, 500, 120);
    private Rectangle keyStage2Area = new Rectangle(350, 500, 500, 70);
    private Rectangle keyStage1Area = new Rectangle(350, 600, 500, 70);
    
    // Bonus areas will be calculated in the constructor
    private Rectangle bonusButtonArea;
    private Rectangle bonusCaveArea;
    
    // Hover states
    private boolean keyStage1Hover = false;
    private boolean keyStage2Hover = false;
    private boolean bonusButtonHover = false;
    
    // Clouds
    private ArrayList<FancyCloud> clouds = new ArrayList<>();
    
    // Stars
    private ArrayList<Star> stars = new ArrayList<>();
    
    // Animation timer
    private Timer animationTimer;
    
    // Animation parameters
    private float titlePulse = 0f;
    private float titlePulseDirection = 0.01f;
    
    public StartScreen(ShapevilleApp app) {
        this.app = app;
        setFocusable(true);
        addKeyListener(this);
        
        // Initialize bonus button position - in bottom right corner
        // We'll set proper values after the component is shown
        bonusButtonArea = new Rectangle(1000, 700, 120, 40);
        bonusCaveArea = new Rectangle(1000, 750, 150, 150); // Larger cave area
        
        // Load images
        try {
            // Load images
            littlePersonImage = new ImageIcon(getClass().getResource("/shapeville/images/start_backgound/little_person.png")).getImage();
            cloudImage = new ImageIcon(getClass().getResource("/shapeville/images/start_backgound/cloud_example.png")).getImage();
            lockImage = new ImageIcon(getClass().getResource("/shapeville/images/enter_page/lock.png")).getImage();
            bonusImage = new ImageIcon(getClass().getResource("/shapeville/images/start_backgound/bonus.png")).getImage();
            System.out.println("Bonus image loaded: " + (bonusImage != null)); // Debug
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize clouds
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            FancyCloud cloud = new FancyCloud(
                random.nextInt(1200), 
                random.nextInt(200), 
                60 + random.nextInt(80),
                40 + random.nextInt(30),
                0.7f + random.nextFloat() * 1.3f
            );
            clouds.add(cloud);
        }
        
        // Initialize stars
        for (int i = 0; i < 25; i++) {
            stars.add(new Star(
                random.nextInt(1200),
                random.nextInt(300),
                2 + random.nextInt(3),
                random.nextInt(60) + 20
            ));
        }
        
        // Mouse listener for hover effects and clicks
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                keyStage1Hover = keyStage1Area.contains(e.getPoint());
                keyStage2Hover = keyStage2Area.contains(e.getPoint());
                bonusButtonHover = bonusButtonArea.contains(e.getPoint()) || bonusCaveArea.contains(e.getPoint());
                repaint();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (keyStage1Area.contains(e.getPoint())) {
                    navigateToMainScreen(1); // Key Stage 1
                } else if (keyStage2Area.contains(e.getPoint())) {
                    navigateToMainScreen(2); // Key Stage 2
                } else if (bonusButtonArea.contains(e.getPoint()) || bonusCaveArea.contains(e.getPoint())) {
                    navigateToMainScreen(3); // Bonus Task
                }
            }
        });
        
        // Start animation timer
        animationTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnimations();
                
                // Update bonus button location if needed
                if (getWidth() > 0 && getHeight() > 0) {
                    // Set the ground level to the bottom of the screen
                    groundLevel = getHeight() - personHeight - 10;
                    
                    int rightMargin = 30;
                    
                    // Set size and position for the cave/black square - aligned with bottom
                    bonusCaveArea.width = 180; 
                    bonusCaveArea.height = 180;
                    bonusCaveArea.x = getWidth() - bonusCaveArea.width - rightMargin;
                    bonusCaveArea.y = getHeight() - bonusCaveArea.height; // Align with bottom
                    
                    // Position the button ABOVE the cave
                    bonusButtonArea.width = 120;
                    bonusButtonArea.height = 35;
                    bonusButtonArea.x = bonusCaveArea.x + (bonusCaveArea.width - bonusButtonArea.width) / 2;
                    bonusButtonArea.y = bonusCaveArea.y - bonusButtonArea.height - 5; // Above the cave
                    
                    // If person hasn't fallen yet, adjust to ground level
                    if (!isJumping && !isFalling && personY != groundLevel) {
                        personY = groundLevel;
                    }
                    
                    // Check if person is inside the bonus cave to trigger navigation
                    if (!navigationTriggered && !isJumping && !isFalling && 
                        personX + personWidth > bonusCaveArea.x && 
                        personX < bonusCaveArea.x + bonusCaveArea.width &&
                        personY + personHeight > bonusCaveArea.y) {
                        // Set flag to prevent repeated navigation
                        navigationTriggered = true;
                        // Navigate to Bonus Tasks (level 3)
                        navigateToMainScreen(3);
                    }
                }
                
                repaint();
            }
        });
        animationTimer.start();
    }
    
    private void updateAnimations() {
        // Update clouds
        for (FancyCloud cloud : clouds) {
            cloud.move();
        }
        
        // Update stars
        for (Star star : stars) {
            star.update();
        }
        
        // Update title pulse effect
        titlePulse += titlePulseDirection;
        if (titlePulse > 1.0f || titlePulse < 0.0f) {
            titlePulseDirection *= -1;
        }
        
        // Update little person physics if needed
        if (isJumping || isFalling) {
            // Apply gravity
            fallVelocity += gravity;
            personY += fallVelocity;
            
            // Check if we've landed on the ground
            if (personY >= groundLevel) {
                personY = groundLevel;
                isJumping = false;
                isFalling = false;
                fallVelocity = 0;
                jumpCount = 0;
            }
        }
    }
    
    private void navigateToMainScreen(int accessLevel) {
        // Set the appropriate access level and navigate to main screen
        app.setAccessLevel(accessLevel);
        app.startHomeScreen();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw sky background - gradient from top to bottom
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(86, 180, 255),
            0, getHeight(), new Color(77, 166, 255)
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw stars
        for (Star star : stars) {
            g2d.setColor(new Color(255, 255, 255, star.alpha));
            g2d.fillOval(star.x, star.y, star.size, star.size);
            
            // Add a subtle glow around stars
            AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f * star.alpha / 255f);
            g2d.setComposite(alphaComposite);
            g2d.fillOval(star.x - 2, star.y - 2, star.size + 4, star.size + 4);
            g2d.setComposite(AlphaComposite.SrcOver);
        }
        
        // Draw clouds
        for (FancyCloud cloud : clouds) {
            cloud.draw(g2d);
        }
        
        // Draw mountains (two mountains at the bottom)
        drawMountains(g2d);
        
        // Draw SHAPEVILLE title with decorations
        drawShapevilleTitle(g2d);
        
        // Draw Key Stage signs
        drawKeyStageSign(g2d, keyStage2Area, "Key Stage 2", keyStage2Hover);
        drawKeyStageSign(g2d, keyStage1Area, "Key Stage 1", keyStage1Hover);
        
        // Draw Bonus Tasks button in the bottom right corner
        drawBonusButton(g2d);
        
        // Draw little person character
        if (littlePersonImage != null) {
            g2d.drawImage(littlePersonImage, personX, personY, personWidth, personHeight, null);
        } else {
            // Fallback if image not loaded
            g2d.setColor(Color.RED);
            g2d.fillRect(personX, personY, personWidth, personHeight);
        }
    }
    
    private void drawMountains(Graphics2D g2d) {
        // Create smoother mountains with gradients
        Color darkGreen = new Color(34, 139, 34);
        Color lightGreen = new Color(85, 180, 85);
        
        // First mountain (left)
        GradientPaint gradient1 = new GradientPaint(
            0, getHeight(), darkGreen,
            400, getHeight() - 200, lightGreen
        );
        g2d.setPaint(gradient1);
        
        Path2D.Double mountain1 = new Path2D.Double();
        mountain1.moveTo(0, getHeight());
        mountain1.curveTo(200, getHeight() - 100, 400, getHeight() - 150, 600, getHeight());
        mountain1.lineTo(0, getHeight());
        mountain1.closePath();
        g2d.fill(mountain1);
        
        // Second mountain (right side)
        GradientPaint gradient2 = new GradientPaint(
            600, getHeight(), darkGreen,
            900, getHeight() - 200, lightGreen
        );
        g2d.setPaint(gradient2);
        
        Path2D.Double mountain2 = new Path2D.Double();
        mountain2.moveTo(400, getHeight());
        mountain2.curveTo(600, getHeight() - 80, 900, getHeight() - 130, 1200, getHeight());
        mountain2.lineTo(400, getHeight());
        mountain2.closePath();
        g2d.fill(mountain2);
    }
    
    private void drawShapevilleTitle(Graphics2D g2d) {
        // Draw title box matching exactly the reference image
        int x = titleArea.x;
        int y = titleArea.y;
        int width = titleArea.width;
        int height = titleArea.height;
        
        // Draw shadow effect
        g2d.setColor(new Color(139, 69, 19, 70));
        g2d.fillRoundRect(x + 5, y + 5, width, height, 25, 25);
        
        // Draw wooden sign background - exact color from reference
        g2d.setColor(new Color(222, 184, 135)); 
        g2d.fillRoundRect(x, y, width, height, 25, 25);
        
        // Draw wood grain texture - horizontal planks
        g2d.setColor(new Color(205, 170, 125, 80));
        g2d.setStroke(new BasicStroke(2));
        
        int plankCount = 5;
        int plankHeight = height / plankCount;
        for (int i = 1; i < plankCount; i++) {
            g2d.drawLine(x + 10, y + i * plankHeight, x + width - 10, y + i * plankHeight);
        }
        
        // Draw border matching reference
        g2d.setColor(new Color(160, 82, 45));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect(x, y, width, height, 25, 25);
        
        // Draw brown circular decorations at corners
        int circleSize = 30;
        Color cornerBrown = new Color(139, 69, 19);
        
        // Top left
        g2d.setColor(cornerBrown);
        g2d.fillOval(x - circleSize/2, y - circleSize/2, circleSize, circleSize);
        
        // Top right
        g2d.fillOval(x + width - circleSize/2, y - circleSize/2, circleSize, circleSize);
        
        // Bottom left
        g2d.fillOval(x - circleSize/2, y + height - circleSize/2, circleSize, circleSize);
        
        // Bottom right
        g2d.fillOval(x + width - circleSize/2, y + height - circleSize/2, circleSize, circleSize);
        
        // Draw green diamond decorations at corners
        Color leafGreen = new Color(50, 205, 50);
        int diamondSize = 28;
        
        // Draw diamonds at corners
        drawDiamond(g2d, x - diamondSize/2, y - diamondSize/2, diamondSize, leafGreen);
        drawDiamond(g2d, x + width - diamondSize/2, y - diamondSize/2, diamondSize, leafGreen);
        drawDiamond(g2d, x - diamondSize/2, y + height - diamondSize/2, diamondSize, leafGreen);
        drawDiamond(g2d, x + width - diamondSize/2, y + height - diamondSize/2, diamondSize, leafGreen);
        
        // Add leaf decorations with varying angles
        drawLeaf(g2d, x + width/4, y - 15, 24, 30, leafGreen, 15);
        drawLeaf(g2d, x + width*3/4, y - 15, 24, 30, leafGreen, -15);
        drawLeaf(g2d, x + width/4, y + height - 15, 24, 30, leafGreen, 200);
        drawLeaf(g2d, x + width*3/4, y + height - 15, 24, 30, leafGreen, 160);
        
        // Draw blue triangle decorations along top edge - pointing inward
        drawBlueTriangles(g2d, x, y, width, true);
        
        // Draw title text matching the reference
        String title = "SHAPEVILLE";
        g2d.setFont(new Font("Serif", Font.BOLD, 72));
        
        // Draw text with exact brown color from reference
        g2d.setColor(new Color(101, 67, 33));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(title)) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(title, textX, textY);
    }
    
    // Draw a leaf with rotation
    private void drawLeaf(Graphics2D g2d, int x, int y, int width, int height, Color color, int angle) {
        // Save the current transform
        AffineTransform oldTransform = g2d.getTransform();
        
        // Create a new transform for the leaf
        AffineTransform leafTransform = new AffineTransform();
        leafTransform.rotate(Math.toRadians(angle), x, y);
        g2d.transform(leafTransform);
        
        // Draw the leaf
        g2d.setColor(color);
        g2d.fillOval(x - width/2, y - height/2, width, height);
        
        // Draw stem
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(x, y, x, y + height/2);
        
        // Restore the original transform
        g2d.setTransform(oldTransform);
    }
    
    private void drawKeyStageSign(Graphics2D g2d, Rectangle area, String text, boolean isHovered) {
        int x = area.x;
        int y = area.y;
        int width = area.width;
        int height = area.height;
        
        // Draw subtle shadow
        g2d.setColor(new Color(139, 69, 19, 70));
        g2d.fillRoundRect(x + 5, y + 5, width, height, 15, 15);
        
        // Draw wooden sign background matching reference color
        g2d.setColor(new Color(222, 184, 135));
        g2d.fillRoundRect(x, y, width, height, 15, 15);
        
        // Draw wood grain texture as horizontal planks
        g2d.setColor(new Color(205, 170, 125, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        
        int plankCount = 3;
        int plankHeight = height / plankCount;
        for (int i = 1; i < plankCount; i++) {
            g2d.drawLine(x + 10, y + i * plankHeight, x + width - 10, y + i * plankHeight);
        }
        
        // Draw border matching reference
        g2d.setColor(new Color(160, 82, 45));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(x, y, width, height, 15, 15);
        
        // Draw blue triangle decorations along the top edge - pointing inward
        drawBlueTriangles(g2d, x, y, width, true);
        
        // Add highlight effect if hovered
        if (isHovered) {
            g2d.setColor(new Color(255, 255, 150, 40));
            g2d.fillRoundRect(x, y, width, height, 15, 15);
        }
        
        // Draw text matching reference
        g2d.setColor(new Color(101, 67, 33));
        g2d.setFont(new Font("Serif", Font.BOLD, 30));
        FontMetrics metrics = g2d.getFontMetrics();
        
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);
    }
    
    private void drawWoodenSign(Graphics2D g2d, int x, int y, int width, int height, String text, int fontSize, boolean isHovered) {
        // Special styling for the Bonus Tasks sign in bottom right
        boolean isBonus = text.equals("Bonus Tasks") && x > 1000; // Position check to ensure it's the bonus in corner
        
        // Draw wooden sign shadow
        g2d.setColor(new Color(139, 69, 19, 70));
        g2d.fillRoundRect(x + 5, y + 5, width, height, 15, 15);
        
        // Draw wooden sign background - lighter color for Bonus Tasks to match image
        if (isBonus) {
            g2d.setColor(new Color(232, 194, 145)); // Lighter tan for bonus
        } else {
            g2d.setColor(new Color(222, 184, 135)); // Regular wooden color
        }
        g2d.fillRoundRect(x, y, width, height, 15, 15);
        
        // Draw wood grain texture
        g2d.setColor(new Color(205, 170, 125, 80));
        g2d.setStroke(new BasicStroke(1));
        
        // Horizontal planks
        int plankHeight = height / 3;
        for (int i = 1; i < 3; i++) {
            g2d.drawLine(x + 5, y + i * plankHeight, x + width - 5, y + i * plankHeight);
        }
        
        // Draw border matching reference
        g2d.setColor(new Color(160, 82, 45));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, width, height, 15, 15);
        
        // Draw blue triangle decorations on top - only for non-bonus signs
        if (!isBonus) {
            drawBlueTriangles(g2d, x, y, width, true);
        }
        
        // Add highlight effect if hovered
        if (isHovered) {
            g2d.setColor(new Color(255, 255, 150, 40));
            g2d.fillRoundRect(x, y, width, height, 15, 15);
        }
        
        // Draw text matching reference - smaller and centered for Bonus Tasks
        g2d.setColor(new Color(101, 67, 33));
        
        if (isBonus) {
            g2d.setFont(new Font("Serif", Font.BOLD, 14));
        } else {
            g2d.setFont(new Font("Serif", Font.BOLD, fontSize));
        }
        
        // Center text
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2d.drawString(text, textX, textY);
    }
    
    private void drawDiamond(Graphics2D g2d, int x, int y, int size, Color color) {
        // Draw solid diamond with exact color from reference
        g2d.setColor(color);
        
        int[] xPoints = {x + size/2, x + size, x + size/2, x};
        int[] yPoints = {y, y + size/2, y + size, y + size/2};
        g2d.fillPolygon(xPoints, yPoints, 4);
    }
    
    private void drawBlueTriangles(Graphics2D g2d, int x, int y, int width, boolean pointInward) {
        // Draw blue triangles matching reference
        g2d.setColor(new Color(100, 200, 255));
        int triangleSize = 10;
        int spacing = width / 7;
        
        for (int i = 1; i <= 6; i++) {
            int triX = x + i * spacing - triangleSize/2;
            int[] xPoints = {triX, triX + triangleSize/2, triX + triangleSize};
            
            // If pointing inward (downward for top edge), flip the Y coordinates
            int[] yPoints;
            if (pointInward) {
                yPoints = new int[]{y, y + triangleSize, y};
            } else {
                yPoints = new int[]{y, y - triangleSize, y};
            }
            
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }
    
    private void drawBonusButton(Graphics2D g2d) {
        // First draw the black square with orange border (cave area)
        // Draw the black background square
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, 15, 15);
        
        // Draw the orange border
        g2d.setColor(new Color(210, 105, 30)); // Dark orange-brown
        g2d.setStroke(new BasicStroke(8));
        g2d.drawRoundRect(bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, 15, 15);
        
        // Draw the wooden sign for Bonus Tasks ABOVE the cave
        g2d.setColor(new Color(232, 194, 145)); // Light wooden color
        g2d.fillRoundRect(bonusButtonArea.x, bonusButtonArea.y, bonusButtonArea.width, bonusButtonArea.height, 10, 10);
        
        // Draw sign border
        g2d.setColor(new Color(160, 82, 45)); // Brown color
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(bonusButtonArea.x, bonusButtonArea.y, bonusButtonArea.width, bonusButtonArea.height, 10, 10);
        
        // Add highlight effect if hovered
        if (bonusButtonHover) {
            g2d.setColor(new Color(255, 255, 150, 40));
            g2d.fillRoundRect(bonusButtonArea.x, bonusButtonArea.y, bonusButtonArea.width, bonusButtonArea.height, 10, 10);
            // Also highlight the cave area
            g2d.fillRoundRect(bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, 15, 15);
        }
        
        // Draw text
        g2d.setColor(new Color(101, 67, 33)); // Dark brown for text
        g2d.setFont(new Font("Serif", Font.BOLD, 14));
        FontMetrics metrics = g2d.getFontMetrics();
        String text = "Bonus Tasks";
        int textX = bonusButtonArea.x + (bonusButtonArea.width - metrics.stringWidth(text)) / 2;
        int textY = bonusButtonArea.y + ((bonusButtonArea.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);
    }
    
    // Key listener methods
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_A) {
            // Move left
            personX -= 10;
            if (personX < 0) personX = 0;
        } else if (key == KeyEvent.VK_D) {
            // Move right
            personX += 10;
            if (personX > getWidth() - personWidth) 
                personX = getWidth() - personWidth;
        } else if (key == KeyEvent.VK_K || key == KeyEvent.VK_SPACE) {
            // Jump
            if (!isJumping && !isFalling) {
                isJumping = true;
                fallVelocity = jumpVelocity;
                jumpCount = 1;
            } else if (jumpCount < maxJumpCount) {
                // Multi-jump
                fallVelocity = jumpVelocity;
                jumpCount++;
            }
        }
        
        repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for this implementation
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this implementation
    }
    
    // Inner class for clouds
    private class FancyCloud {
        int x, y, width, height;
        float speed;
        float alpha = 0.95f;
        
        public FancyCloud(int x, int y, int width, int height, float speed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
        }
        
        public void move() {
            x -= speed;
            if (x < -width) {
                x = getWidth() + new Random().nextInt(300);
                y = new Random().nextInt(200);
            }
        }
        
        public void draw(Graphics2D g2d) {
            // Simple cloud shape matching reference
            g2d.setColor(Color.WHITE);
            
            // Drawing a simple cloud shape
            int puffRadius = height/2;
            g2d.fillOval(x, y, puffRadius, puffRadius);
            g2d.fillOval(x + width/4, y, puffRadius, puffRadius);
            g2d.fillOval(x + width/2, y, puffRadius, puffRadius);
            g2d.fillOval(x + width/4, y - height/4, puffRadius, puffRadius);
            g2d.fillRoundRect(x, y + height/4, width, height/2, height/2, height/2);
        }
    }
    
    // Inner class for twinkling stars
    private class Star {
        int x, y, size;
        int alpha = 255;
        int fadeSpeed;
        boolean fading = true;
        
        public Star(int x, int y, int size, int fadeSpeed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.fadeSpeed = fadeSpeed;
        }
        
        public void update() {
            if (fading) {
                alpha -= fadeSpeed;
                if (alpha <= 50) {
                    fading = false;
                }
            } else {
                alpha += fadeSpeed;
                if (alpha >= 255) {
                    fading = true;
                }
            }
            
            // Ensure alpha stays within bounds
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;
        }
    }
    
    /**
     * Reset navigation control when returning to start screen
     */
    public void resetNavigation() {
        // Reset navigation flag
        navigationTriggered = false;
        
        // Reset character position to starting point
        personX = 180;
        personY = groundLevel;
        isJumping = false;
        isFalling = false;
        fallVelocity = 0;
        jumpCount = 0;
    }
} 