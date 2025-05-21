package shapeville.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate shape images dynamically instead of loading them from files.
 * This avoids the need to include actual image files with the application.
 */
public class ImageProvider {
    private static final int IMAGE_SIZE = 201;
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    
    /**
     * Get the image icon for a 2D shape
     * @param shapeName the name of the shape
     * @return the ImageIcon for the shape
     */
    public static ImageIcon get2DShapeIcon(String shapeName) {
        String key = "2d_" + shapeName;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }
        
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set up anti-aliasing for smoother drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        
        // Shape outline style
        g2d.setStroke(new BasicStroke(3));
        
        // Draw the shape based on its e
        drawShape2D(g2d, shapeName);
        
        g2d.dispose();
        
        // Create and cache the icon
        ImageIcon icon = new ImageIcon(image);
        imageCache.put(key, icon);
        
        return icon;
    }
    
    /**
     * Get the image icon for a 3D shape
     * @param shapeName the name of the shape
     * @return the ImageIcon for the shape
     */
    public static ImageIcon get3DShapeIcon(String shapeName) {
        String key = "3d_" + shapeName;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }
        
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set up anti-aliasing for smoother drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        
        // Shape outline style
        g2d.setStroke(new BasicStroke(3));
        
        // Draw the shape based on its name
        drawShape3D(g2d, shapeName);
        
        g2d.dispose();
        
        // Create and cache the icon
        ImageIcon icon = new ImageIcon(image);
        imageCache.put(key, icon);
        
        return icon;
    }
    
    /**
     * Draw a 2D shape
     * @param g2d the Graphics2D object to draw on
     * @param shapeName the name of the shape
     */
    private static void drawShape2D(Graphics2D g2d, String shapeName) {
        Color color;
        Shape shape;
        
        // Center of the image
        int centerX = IMAGE_SIZE / 2;
        int centerY = IMAGE_SIZE / 2;
        
        // Size of the shape (slightly smaller than the image)
        int size = (int)(IMAGE_SIZE * 0.7);
        
        switch (shapeName.toLowerCase()) {
            case "circle":
                color = new Color(255, 100, 100); // Red
                shape = new Ellipse2D.Double(centerX - size/2, centerY - size/2, size, size);
                break;
                
            case "rectangle":
                color = new Color(100, 100, 255); // Blue
                shape = new Rectangle2D.Double(centerX - size/2, centerY - size/3, size, size*2/3);
                break;
                
            case "triangle":
                color = new Color(100, 255, 100); // Green
                int[] xPoints = {centerX, centerX - size/2, centerX + size/2};
                int[] yPoints = {centerY - size/2, centerY + size/2, centerY + size/2};
                shape = new Polygon(xPoints, yPoints, 3);
                break;
                
            case "oval":
                color = new Color(186, 85, 211); // Medium Orchid
                shape = new Ellipse2D.Double(centerX - size/2, centerY - size/3, size, size*2/3);
                break;
                
            case "square":
                color = new Color(30, 144, 255); // Dodger Blue
                shape = new Rectangle2D.Double(centerX - size/2, centerY - size/2, size, size);
                break;
                
            case "octagon":
                color = new Color(255, 165, 0); // Orange
                shape = createRegularPolygon(centerX, centerY, size/2, 8);
                break;
                
            case "heptagon":
                color = new Color(255, 105, 180); // Hot Pink
                shape = createRegularPolygon(centerX, centerY, size/2, 7);
                break;
                
            case "hexagon":
                color = new Color(128, 128, 128); // Gray
                shape = createRegularPolygon(centerX, centerY, size/2, 6);
                break;
                
            case "pentagon":
                color = new Color(139, 69, 19); // Saddle Brown
                shape = createRegularPolygon(centerX, centerY, size/2, 5);
                break;

            case "kite":
                color = new Color(255, 105, 180);
                int[] kiteX = {centerX, centerX + size / 3, centerX, centerX - size / 3};
                int[] kiteY = {centerY - size * 2 / 3, centerY, centerY + size / 3, centerY};
                shape = new Polygon(kiteX, kiteY, 4);
                break;
            case "rhombus":
                color = new Color(32, 178, 170); // Pink
                int[] rhombusX = {centerX, centerX + size/3, centerX, centerX - size/3};
                int[] rhombusY = {centerY - size/2, centerY, centerY + size/2, centerY};
                shape = new Polygon(rhombusX, rhombusY, 4);
                break;
                
            default:
                // Default to a circle if shape name is not recognized
                color = Color.GRAY;
                shape = new Ellipse2D.Double(centerX - size/2, centerY - size/2, size, size);
        }
        
        // Fill the shape
        g2d.setColor(color);
        g2d.fill(shape);
        
        // Draw the outline
        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
    }
    
    /**
     * Draw a 3D shape
     * @param g2d the Graphics2D object to draw on
     * @param shapeName the name of the shape
     */
    private static void drawShape3D(Graphics2D g2d, String shapeName) {
        // Center of the image
        int centerX = IMAGE_SIZE / 2;
        int centerY = IMAGE_SIZE / 2;
        
        // Size of the shape (slightly smaller than the image)
        int size = (int)(IMAGE_SIZE * 0.6);
        
        switch (shapeName.toLowerCase()) {
            case "cube":
                drawCube(g2d, centerX, centerY, size);
                break;
                
            case "cuboid":
                drawCuboid(g2d, centerX, centerY, size, size, size/2);
                break;
                
            case "cylinder":
                drawCylinder(g2d, centerX, centerY, size/2, size);
                break;
                
            case "sphere":
                drawSphere(g2d, centerX, centerY, size/2);
                break;
                
            case "triangular prism":
            case "triangular_prism":
                drawTriangularPrism(g2d, centerX, centerY, size);
                break;
                
            case "square-based pyramid":
            case "square_pyramid":
                drawSquarePyramid(g2d, centerX, centerY, size);
                break;
                
            case "cone":
                drawCone(g2d, centerX, centerY, size/2, size);
                break;
                
            case "tetrahedron":
                drawTetrahedron(g2d, centerX, centerY, size);
                break;
                
            default:
                // Default to a cube if shape name is not recognized
                drawCube(g2d, centerX, centerY, size);
        }
    }
    
    /**
     * Create a regular polygon with the given number of sides
     * @param centerX center X coordinate
     * @param centerY center Y coordinate
     * @param radius radius of the polygon
     * @param sides number of sides
     * @return a GeneralPath representing the polygon
     */
    private static Shape createRegularPolygon(int centerX, int centerY, int radius, int sides) {
        GeneralPath polygon = new GeneralPath();
        
        double angle = 2 * Math.PI / sides;
        
        // Start from the top
        double startAngle = -Math.PI / 2;
        
        // First point
        polygon.moveTo(
            centerX + radius * Math.cos(startAngle),
            centerY + radius * Math.sin(startAngle)
        );
        
        // Remaining points
        for (int i = 1; i < sides; i++) {
            double theta = startAngle + angle * i;
            polygon.lineTo(
                centerX + radius * Math.cos(theta),
                centerY + radius * Math.sin(theta)
            );
        }
        
        // Close the path
        polygon.closePath();
        
        return polygon;
    }
    
    // Drawing methods for 3D shapes
    
    private static void drawCube(Graphics2D g2d, int centerX, int centerY, int size) {
        int half = size / 2;
        int offset = size / 4; // 3D perspective offset
        
        // Front face
        int[] xPoints = {centerX - half, centerX + half, centerX + half, centerX - half};
        int[] yPoints = {centerY - half, centerY - half, centerY + half, centerY + half};
        Polygon frontFace = new Polygon(xPoints, yPoints, 4);
        
        // Back face
        int[] xPointsBack = {centerX - half + offset, centerX + half + offset, centerX + half + offset, centerX - half + offset};
        int[] yPointsBack = {centerY - half - offset, centerY - half - offset, centerY + half - offset, centerY + half - offset};
        Polygon backFace = new Polygon(xPointsBack, yPointsBack, 4);
        
        // Fill the back face
        g2d.setColor(new Color(144, 238, 144)); // Light Green
        g2d.fill(backFace);
        
        // Connect front and back faces with lines
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPointsBack[i], yPointsBack[i]);
        }
        
        // Fill the front face
        g2d.setColor(new Color(50, 205, 50)); // Lime Green
        g2d.fill(frontFace);
        
        // Draw the outlines
        g2d.setColor(Color.BLACK);
        g2d.draw(frontFace);
        g2d.draw(backFace);
    }
    
    private static void drawCuboid(Graphics2D g2d, int centerX, int centerY, int width, int height, int depth) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int offsetX = depth / 2; // 3D perspective offset X
        int offsetY = depth / 2; // 3D perspective offset Y
        
        // Front face
        int[] xPoints = {centerX - halfWidth, centerX + halfWidth, centerX + halfWidth, centerX - halfWidth};
        int[] yPoints = {centerY - halfHeight, centerY - halfHeight, centerY + halfHeight, centerY + halfHeight};
        Polygon frontFace = new Polygon(xPoints, yPoints, 4);
        
        // Back face
        int[] xPointsBack = {centerX - halfWidth + offsetX, centerX + halfWidth + offsetX, centerX + halfWidth + offsetX, centerX - halfWidth + offsetX};
        int[] yPointsBack = {centerY - halfHeight - offsetY, centerY - halfHeight - offsetY, centerY + halfHeight - offsetY, centerY + halfHeight - offsetY};
        Polygon backFace = new Polygon(xPointsBack, yPointsBack, 4);
        
        // Fill the back face
        g2d.setColor(new Color(173, 216, 230)); // Light Blue
        g2d.fill(backFace);
        
        // Connect front and back faces with lines
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPointsBack[i], yPointsBack[i]);
        }
        
        // Fill the front face
        g2d.setColor(new Color(30, 144, 255)); // Dodger Blue
        g2d.fill(frontFace);
        
        // Draw the outlines
        g2d.setColor(Color.BLACK);
        g2d.draw(frontFace);
        g2d.draw(backFace);
    }
    
    private static void drawCylinder(Graphics2D g2d, int centerX, int centerY, int radius, int height) {
        int halfHeight = height / 2;
        
        // Top ellipse
        Ellipse2D topCircle = new Ellipse2D.Double(centerX - radius, centerY - halfHeight, radius * 2, radius / 2);
        
        // Bottom ellipse
        Ellipse2D bottomCircle = new Ellipse2D.Double(centerX - radius, centerY + halfHeight - radius / 4, radius * 2, radius / 2);
        
        // Side lines
        g2d.setColor(Color.BLACK);
        g2d.drawLine(centerX - radius, centerY - halfHeight + radius / 4, centerX - radius, centerY + halfHeight);
        g2d.drawLine(centerX + radius, centerY - halfHeight + radius / 4, centerX + radius, centerY + halfHeight);
        
        // Fill the cylinder
        g2d.setColor(new Color(220, 20, 60)); // Crimson
        g2d.fill(new Rectangle2D.Double(centerX - radius, centerY - halfHeight + radius / 4, radius * 2, height - radius / 4));
        
        // Fill the top circle
        g2d.setColor(new Color(250, 128, 114)); // Salmon
        g2d.fill(topCircle);
        
        // Fill the bottom circle (only if visible)
        g2d.setColor(new Color(178, 34, 34)); // Firebrick
        g2d.fill(bottomCircle);
        
        // Draw outlines
        g2d.setColor(Color.BLACK);
        g2d.draw(topCircle);
        g2d.draw(bottomCircle);
        g2d.drawLine(centerX - radius, centerY - halfHeight + radius / 4, centerX - radius, centerY + halfHeight);
        g2d.drawLine(centerX + radius, centerY - halfHeight + radius / 4, centerX + radius, centerY + halfHeight);
    }
    
    private static void drawSphere(Graphics2D g2d, int centerX, int centerY, int radius) {
        // Base circle
        Ellipse2D circle = new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        // Create gradient for 3D effect
        Paint oldPaint = g2d.getPaint();
        RadialGradientPaint gradient = new RadialGradientPaint(
            new Point2D.Double(centerX - radius/3, centerY - radius/3),
            radius * 2,
            new float[] {0.0f, 1.0f},
            new Color[] {new Color(255, 165, 0), new Color(255, 140, 0)}
        );
        g2d.setPaint(gradient);
        
        // Fill the sphere
        g2d.fill(circle);
        
        // Reset paint and draw outline
        g2d.setPaint(oldPaint);
        g2d.setColor(Color.BLACK);
        g2d.draw(circle);
        
        // Add highlight for 3D effect
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(centerX - radius/3, centerY - radius/3, radius/2, radius/2);
    }
    
    private static void drawTriangularPrism(Graphics2D g2d, int centerX, int centerY, int size) {
        int half = size / 2;
        int offset = size / 4; // 3D perspective offset
        
        // Front face (triangle)
        int[] xPointsFront = {centerX, centerX - half, centerX + half};
        int[] yPointsFront = {centerY - half, centerY + half, centerY + half};
        Polygon frontFace = new Polygon(xPointsFront, yPointsFront, 3);
        
        // Back face (triangle)
        int[] xPointsBack = {centerX + offset, centerX - half + offset, centerX + half + offset};
        int[] yPointsBack = {centerY - half - offset, centerY + half - offset, centerY + half - offset};
        Polygon backFace = new Polygon(xPointsBack, yPointsBack, 3);
        
        // Fill the back face
        g2d.setColor(new Color(255, 222, 173)); // Navajo White
        g2d.fill(backFace);
        
        // Connect front and back faces with lines
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(xPointsFront[i], yPointsFront[i], xPointsBack[i], yPointsBack[i]);
        }
        
        // Fill the front face
        g2d.setColor(new Color(255, 160, 122)); // Light Salmon
        g2d.fill(frontFace);
        
        // Draw the outlines
        g2d.setColor(Color.BLACK);
        g2d.draw(frontFace);
        g2d.draw(backFace);
    }
    
    private static void drawSquarePyramid(Graphics2D g2d, int centerX, int centerY, int size) {
        int half = size / 2;
        int offsetX = size / 4; // 3D perspective offset X
        int offsetY = size / 4; // 3D perspective offset Y
        
        // Base (square)
        int[] xPointsBase = {centerX - half, centerX + half, centerX + half + offsetX, centerX - half + offsetX};
        int[] yPointsBase = {centerY + half, centerY + half, centerY + half - offsetY, centerY + half - offsetY};
        Polygon base = new Polygon(xPointsBase, yPointsBase, 4);
        
        // Apex (top point)
        int apexX = centerX + offsetX/2;
        int apexY = centerY - half - offsetY/2;
        
        // Fill the base
        g2d.setColor(new Color(219, 112, 147)); // Pale Violet Red
        g2d.fill(base);
        
        // Draw triangular faces
        int[][] faceX = {
            {centerX - half, centerX + half, apexX}, // Front face
            {centerX + half, centerX + half + offsetX, apexX}, // Right face
            {centerX + half + offsetX, centerX - half + offsetX, apexX}, // Back face
            {centerX - half + offsetX, centerX - half, apexX} // Left face
        };
        
        int[][] faceY = {
            {centerY + half, centerY + half, apexY}, // Front face
            {centerY + half, centerY + half - offsetY, apexY}, // Right face
            {centerY + half - offsetY, centerY + half - offsetY, apexY}, // Back face
            {centerY + half - offsetY, centerY + half, apexY} // Left face
        };
        
        Color[] faceColors = {
            new Color(199, 21, 133), // Medium Violet Red (front)
            new Color(219, 112, 147), // Pale Violet Red (right)
            new Color(255, 182, 193), // Light Pink (back)
            new Color(255, 105, 180)  // Hot Pink (left)
        };
        
        for (int i = 0; i < 4; i++) {
            Polygon face = new Polygon(faceX[i], faceY[i], 3);
            g2d.setColor(faceColors[i]);
            g2d.fill(face);
            g2d.setColor(Color.BLACK);
            g2d.draw(face);
        }
        
        // Draw the base outline
        g2d.setColor(Color.BLACK);
        g2d.draw(base);
    }
    
    private static void drawCone(Graphics2D g2d, int centerX, int centerY, int radius, int height) {
        int halfHeight = height / 2;
        
        // Base ellipse
        Ellipse2D baseEllipse = new Ellipse2D.Double(centerX - radius, centerY + halfHeight - radius / 4, radius * 2, radius / 2);
        
        // Apex (top point)
        int apexX = centerX;
        int apexY = centerY - halfHeight;
        
        // Fill the base
        g2d.setColor(new Color(100, 149, 237)); // Cornflower Blue
        g2d.fill(baseEllipse);
        
        // Draw the cone sides
        g2d.setColor(new Color(65, 105, 225)); // Royal Blue
        int[] xPoints = {centerX - radius, centerX + radius, apexX};
        int[] yPoints = {centerY + halfHeight, centerY + halfHeight, apexY};
        Polygon side = new Polygon(xPoints, yPoints, 3);
        g2d.fill(side);
        
        // Draw outlines
        g2d.setColor(Color.BLACK);
        g2d.draw(baseEllipse);
        g2d.drawLine(centerX - radius, centerY + halfHeight, apexX, apexY);
        g2d.drawLine(centerX + radius, centerY + halfHeight, apexX, apexY);
    }
    
    private static void drawTetrahedron(Graphics2D g2d, int centerX, int centerY, int size) {
        int half = size / 2;
        
        // Base points (triangle)
        int[] xPointsBase = {centerX - half, centerX + half, centerX};
        int[] yPointsBase = {centerY + half/2, centerY + half/2, centerY - half/2};
        
        // Apex (top point)
        int apexX = centerX;
        int apexY = centerY - half;
        
        // Draw the faces
        Polygon[] faces = new Polygon[4];
        
        // Base face
        faces[0] = new Polygon(xPointsBase, yPointsBase, 3);
        
        // Side faces
        faces[1] = new Polygon(
            new int[]{xPointsBase[0], xPointsBase[1], apexX},
            new int[]{yPointsBase[0], yPointsBase[1], apexY},
            3
        );
        
        faces[2] = new Polygon(
            new int[]{xPointsBase[1], xPointsBase[2], apexX},
            new int[]{yPointsBase[1], yPointsBase[2], apexY},
            3
        );
        
        faces[3] = new Polygon(
            new int[]{xPointsBase[2], xPointsBase[0], apexX},
            new int[]{yPointsBase[2], yPointsBase[0], apexY},
            3
        );
        
        // Colors for the faces
        Color[] colors = {
            new Color(152, 251, 152), // Pale Green (base)
            new Color(144, 238, 144), // Light Green (side 1)
            new Color(124, 252, 0),   // Lawn Green (side 2)
            new Color(50, 205, 50)    // Lime Green (side 3)
        };
        
        // Draw the faces with different colors
        for (int i = 0; i < 4; i++) {
            g2d.setColor(colors[i]);
            g2d.fill(faces[i]);
            g2d.setColor(Color.BLACK);
            g2d.draw(faces[i]);
        }
    }
} 