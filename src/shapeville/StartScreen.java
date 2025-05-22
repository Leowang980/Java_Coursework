package shapeville;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

/**
 * The start screen of the Shapeville application.
 * This class creates an interactive start screen with animated elements,
 * including a character that can be controlled, clouds, stars, and interactive buttons.
 * The screen provides navigation to different stages of the application.
 *
 * @author Shapeville Team
 * @version 1.0
 */
public class StartScreen extends JPanel implements KeyListener {
    /** The main application instance */
    private ShapevilleApp app;
    
    /** Image resources */
    private Image littlePersonImage;
    private Image cloudImage;
    private Image lockImage;
    private Image bonusImage;
    private Image doorImage;
    private Image doorOpenImage;
    
    /** Door animation state management */
    private boolean isDoorOpening = false;
    private long doorOpenTime = 0;
    private final int DOOR_OPEN_DELAY = 1000; // 1 second delay
    
    /** Key Stage 1 navigation delay management */
    private boolean keyStage1NavigationPending = false;
    private long keyStage1NavigationTime = 0;
    private final int KEY_STAGE1_NAVIGATION_DELAY = 5000; // 5 seconds delay
    
    /** Navigation control flag */
    private boolean navigationTriggered = false;
    
    /** Character properties and physics */
    private int personX = 180;
    private int personY = 620;
    private int personWidth = 50;
    private int personHeight = 50;
    private boolean isJumping = false;
    private boolean isFalling = false;
    private int jumpCount = 0;
    private int maxJumpCount = 2;
    private int jumpVelocity = -15;
    private int fallVelocity = 0;
    private int gravity = 1;
    private int groundLevel = 620;
    
    /** Button interaction states */
    private boolean standingOnStage1 = false;
    private boolean standingOnStage2 = false;
    
    /** Interactive areas on screen */
    private Rectangle titleArea = new Rectangle(350, 320, 500, 120);
    private Rectangle keyStage2Area = new Rectangle(350, 500, 500, 70);
    private Rectangle keyStage1Area = new Rectangle(350, 600, 500, 70);
    private Rectangle bonusButtonArea;
    private Rectangle bonusCaveArea;
    
    /** Hover states for interactive elements */
    private boolean keyStage1Hover = false;
    private boolean keyStage2Hover = false;
    private boolean bonusButtonHover = false;
    
    /** Animated elements */
    private ArrayList<FancyCloud> clouds = new ArrayList<>();
    private ArrayList<Star> stars = new ArrayList<>();
    private Timer animationTimer;
    
    /** Title animation parameters */
    private float titlePulse = 0f;
    private float titlePulseDirection = 0.01f;

    /**
     * Constructs a new StartScreen with the specified application instance.
     *
     * @param app The main Shapeville application instance
     */
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
            doorImage = new ImageIcon(getClass().getResource("/shapeville/images/start_backgound/Door.png")).getImage();
            doorOpenImage = new ImageIcon(getClass().getResource("/shapeville/images/start_backgound/Door_open.png")).getImage();
            System.out.println("Bonus image loaded: " + (bonusImage != null)); // Debug
            System.out.println("Door image loaded: " + (doorImage != null)); // Debug
            System.out.println("Door open image loaded: " + (doorOpenImage != null)); // Debug
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
                    // If clicked on the door, start the door opening sequence
                    if (!isDoorOpening && !navigationTriggered) {
                        isDoorOpening = true;
                        doorOpenTime = System.currentTimeMillis();
                    }
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
                    
                    // Set size and position for the door - aligned with bottom
                    // Adjust size to maintain door proportions (taller than wide)
                    bonusCaveArea.width = 140;
                    bonusCaveArea.height = 200;
                    bonusCaveArea.x = getWidth() - bonusCaveArea.width - rightMargin;
                    bonusCaveArea.y = getHeight() - bonusCaveArea.height; // Align with bottom
                    
                    // Position the button ABOVE the door
                    bonusButtonArea.width = 120;
                    bonusButtonArea.height = 35;
                    bonusButtonArea.x = bonusCaveArea.x + (bonusCaveArea.width - bonusButtonArea.width) / 2;
                    bonusButtonArea.y = bonusCaveArea.y - bonusButtonArea.height - 5; // Above the door
                    
                    // Check if person is near the door to trigger door opening
                    if (!isDoorOpening && !navigationTriggered && !isJumping && !isFalling && 
                        personX + personWidth > bonusCaveArea.x && 
                        personX < bonusCaveArea.x + bonusCaveArea.width &&
                        personY + personHeight >= bonusCaveArea.y + bonusCaveArea.height - 30) {
                        // Start door opening sequence
                        isDoorOpening = true;
                        doorOpenTime = System.currentTimeMillis();
                    }
                    
                    // Check if it's time to navigate after door has been open for the delay period
                    if (isDoorOpening && !navigationTriggered && 
                        System.currentTimeMillis() - doorOpenTime >= DOOR_OPEN_DELAY) {
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
    
    /**
     * Updates all animations and game states.
     * This includes cloud movement, star twinkling, and character physics.
     */
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
        
        // Reset button standing states
        boolean wasStandingOnStage1 = standingOnStage1;
        standingOnStage1 = false;
        standingOnStage2 = false;
        
        // Update little person physics if needed
        if (isJumping || isFalling) {
            // Apply gravity
            fallVelocity += gravity;
            personY += fallVelocity;
            
            // Check if we've landed on the ground or on a button
            if (personY >= groundLevel) {
                // Landed on ground
                personY = groundLevel;
                isJumping = false;
                isFalling = false;
                fallVelocity = 0;
                jumpCount = 0;
                
                // Cancel any pending navigation since we're on the ground
                keyStage1NavigationPending = false;
            } else {
                // Check if landed on Key Stage 1 button
                if (fallVelocity > 0 &&
                    personX + personWidth/2 >= keyStage1Area.x &&
                    personX + personWidth/2 <= keyStage1Area.x + keyStage1Area.width &&
                    personY + personHeight <= keyStage1Area.y + 5 &&
                    personY + personHeight + fallVelocity >= keyStage1Area.y) {
                    
                    // Land on Key Stage 1 button
                    personY = keyStage1Area.y - personHeight;
                    isJumping = false;
                    isFalling = false;
                    fallVelocity = 0;
                    jumpCount = 0;
                    standingOnStage1 = true;
                    
                    // Start Key Stage 1 navigation timer if not already pending
                    if (!keyStage1NavigationPending && !navigationTriggered) {
                        keyStage1NavigationPending = true;
                        keyStage1NavigationTime = System.currentTimeMillis();
                    }
                }
                
                // Check if landed on Key Stage 2 button
                else if (fallVelocity > 0 &&
                    personX + personWidth/2 >= keyStage2Area.x &&
                    personX + personWidth/2 <= keyStage2Area.x + keyStage2Area.width &&
                    personY + personHeight <= keyStage2Area.y + 5 &&
                    personY + personHeight + fallVelocity >= keyStage2Area.y) {
                    
                    // Land on Key Stage 2 button
                    personY = keyStage2Area.y - personHeight;
                    isJumping = false;
                    isFalling = false;
                    fallVelocity = 0;
                    jumpCount = 0;
                    standingOnStage2 = true;
                    
                    // Cancel any pending Key Stage 1 navigation when landing on Key Stage 2
                    keyStage1NavigationPending = false;
                }
            }
        } else {
            // Check if we're standing on a button
            if (personY + personHeight == keyStage1Area.y &&
                personX + personWidth/2 >= keyStage1Area.x &&
                personX + personWidth/2 <= keyStage1Area.x + keyStage1Area.width) {
                standingOnStage1 = true;
                
                // Start Key Stage 1 navigation timer if not already pending
                if (!keyStage1NavigationPending && !navigationTriggered) {
                    keyStage1NavigationPending = true;
                    keyStage1NavigationTime = System.currentTimeMillis();
                }
            } else if (personY + personHeight == keyStage2Area.y &&
                       personX + personWidth/2 >= keyStage2Area.x &&
                       personX + personWidth/2 <= keyStage2Area.x + keyStage2Area.width) {
                standingOnStage2 = true;
                
                // Cancel any pending Key Stage 1 navigation when standing on Key Stage 2
                keyStage1NavigationPending = false;
            } else if (personY < groundLevel && !isJumping) {
                // If we're not on ground and not on a button, start falling
                isFalling = true;
                
                // Cancel any pending navigation if we're not on Key Stage 1 button anymore
                if (!standingOnStage1) {
                    keyStage1NavigationPending = false;
                }
            }
        }
        
        // If we were standing on stage 1 but now we're not, cancel navigation
        if (wasStandingOnStage1 && !standingOnStage1) {
            keyStage1NavigationPending = false;
        }
        
        // Check if Key Stage 1 navigation delay has passed
        if (keyStage1NavigationPending && !navigationTriggered && 
            System.currentTimeMillis() - keyStage1NavigationTime >= KEY_STAGE1_NAVIGATION_DELAY) {
            // Set flag to prevent repeated navigation
            keyStage1NavigationPending = false;
            navigationTriggered = true;
            // Navigate to Key Stage 1
            navigateToMainScreen(1);
        }
    }
    
    /**
     * Navigates to the main screen with the specified access level.
     *
     * @param accessLevel The access level to set (1 for Key Stage 1, 2 for Key Stage 2, 3 for Bonus)
     */
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
        drawKeyStageSign(g2d, keyStage2Area, "Key Stage 2", keyStage2Hover || standingOnStage2);
        drawKeyStageSign(g2d, keyStage1Area, "Key Stage 1", keyStage1Hover || standingOnStage1);
        
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
    
    /**
     * Draws the mountain background with gradient effects.
     *
     * @param g2d The graphics context to draw with
     */
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
    
    /**
     * Draws the SHAPEVILLE title with decorative elements.
     *
     * @param g2d The graphics context to draw with
     */
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
    
    /**
     * Draws a decorative leaf with rotation.
     *
     * @param g2d The graphics context to draw with
     * @param x The x-coordinate of the leaf
     * @param y The y-coordinate of the leaf
     * @param width The width of the leaf
     * @param height The height of the leaf
     * @param color The color of the leaf
     * @param angle The rotation angle in degrees
     */
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
    
    /**
     * Draws a Key Stage sign with hover effects.
     *
     * @param g2d The graphics context to draw with
     * @param area The area to draw the sign in
     * @param text The text to display on the sign
     * @param isHighlighted Whether the sign should be highlighted
     */
    private void drawKeyStageSign(Graphics2D g2d, Rectangle area, String text, boolean isHighlighted) {
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
        
        // Add highlight effect if hovered or character is standing on it
        if (isHighlighted) {
            g2d.setColor(new Color(255, 255, 150, 80)); // Brighter highlight for standing
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
    
    /**
     * Draws a wooden sign with text and hover effects.
     *
     * @param g2d The graphics context to draw with
     * @param x The x-coordinate of the sign
     * @param y The y-coordinate of the sign
     * @param width The width of the sign
     * @param height The height of the sign
     * @param text The text to display
     * @param fontSize The size of the font
     * @param isHovered Whether the sign is being hovered over
     */
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
    
    /**
     * Draws a diamond shape.
     *
     * @param g2d The graphics context to draw with
     * @param x The x-coordinate of the diamond
     * @param y The y-coordinate of the diamond
     * @param size The size of the diamond
     * @param color The color of the diamond
     */
    private void drawDiamond(Graphics2D g2d, int x, int y, int size, Color color) {
        // Draw solid diamond with exact color from reference
        g2d.setColor(color);
        
        int[] xPoints = {x + size/2, x + size, x + size/2, x};
        int[] yPoints = {y, y + size/2, y + size, y + size/2};
        g2d.fillPolygon(xPoints, yPoints, 4);
    }
    
    /**
     * Draws decorative blue triangles.
     *
     * @param g2d The graphics context to draw with
     * @param x The x-coordinate of the triangle row
     * @param y The y-coordinate of the triangle row
     * @param width The width of the triangle row
     * @param pointInward Whether the triangles should point inward
     */
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
    
    /**
     * Draws the bonus button and door with animation effects.
     *
     * @param g2d The graphics context to draw with
     */
    private void drawBonusButton(Graphics2D g2d) {
        // Draw the door image - either closed or open depending on state
        if (isDoorOpening && doorOpenImage != null) {
            // Draw the open door when opening animation is active
            g2d.drawImage(doorOpenImage, bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, this);
        } else if (doorImage != null) {
            // Draw the closed door normally
            g2d.drawImage(doorImage, bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, this);
        } else {
            // Fallback to black rectangle if images fail to load
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, 15, 15);
        }
        
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
            // Also highlight the door area with a subtle glow
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.fillRoundRect(bonusCaveArea.x, bonusCaveArea.y, bonusCaveArea.width, bonusCaveArea.height, 15, 15);
            g2d.setComposite(AlphaComposite.SrcOver);
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
                
                // If standing on Key Stage 2 button, navigate immediately
                // For Key Stage 1, the delay is handled in updateAnimations
                if (standingOnStage2) {
                    navigateToMainScreen(2); // Key Stage 2
                }
            } else if (jumpCount < maxJumpCount) {
                // Multi-jump
                fallVelocity = jumpVelocity;
                jumpCount++;
            }
        } else if (key == KeyEvent.VK_ENTER) {
            // Enter key can navigate if standing on a button
            // For Key Stage 1, the delay is handled in updateAnimations
            if (standingOnStage2) {
                navigateToMainScreen(2); // Key Stage 2
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
    
    /**
     * Inner class representing an animated cloud.
     */
    private class FancyCloud {
        /** Cloud position and size */
        int x, y, width, height;
        /** Cloud movement speed */
        float speed;
        /** Cloud transparency */
        float alpha = 0.95f;
        
        /**
         * Constructs a new FancyCloud.
         *
         * @param x The initial x-coordinate
         * @param y The initial y-coordinate
         * @param width The width of the cloud
         * @param height The height of the cloud
         * @param speed The movement speed of the cloud
         */
        public FancyCloud(int x, int y, int width, int height, float speed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
        }
        
        /**
         * Updates the cloud's position.
         */
        public void move() {
            x -= speed;
            if (x < -width) {
                x = getWidth() + new Random().nextInt(300);
                y = new Random().nextInt(200);
            }
        }
        
        /**
         * Draws the cloud.
         *
         * @param g2d The graphics context to draw with
         */
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
    
    /**
     * Inner class representing a twinkling star.
     */
    private class Star {
        /** Star position and size */
        int x, y, size;
        /** Star transparency */
        int alpha = 255;
        /** Star fade speed */
        int fadeSpeed;
        /** Whether the star is currently fading */
        boolean fading = true;
        
        /**
         * Constructs a new Star.
         *
         * @param x The x-coordinate of the star
         * @param y The y-coordinate of the star
         * @param size The size of the star
         * @param fadeSpeed The speed at which the star fades in and out
         */
        public Star(int x, int y, int size, int fadeSpeed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.fadeSpeed = fadeSpeed;
        }
        
        /**
         * Updates the star's twinkling effect.
         */
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
     * Resets the navigation state and character position when returning to the start screen.
     */
    public void resetNavigation() {
        // Reset navigation flag
        navigationTriggered = false;
        isDoorOpening = false;
        keyStage1NavigationPending = false;
        standingOnStage1 = false;
        standingOnStage2 = false;
        
        // Reset character position to starting point
        personX = 180;
        personY = groundLevel;
        isJumping = false;
        isFalling = false;
        fallVelocity = 0;
        jumpCount = 0;
    }
} 