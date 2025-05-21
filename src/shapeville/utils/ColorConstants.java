package shapeville.utils;

import java.awt.Color;

/**
 * 集中管理应用中使用的所有颜色常量
 */
public class ColorConstants {
    // 木质风格颜色（来自WoodenButton）
    public static final Color WOOD_BG_COLOR = new Color(232, 194, 145);      // 浅木色背景
    public static final Color WOOD_BORDER_COLOR = new Color(160, 82, 45);    // 深棕色边框
    public static final Color WOOD_TEXT_COLOR = new Color(101, 67, 33);      // 深棕色文本
    
    // 主背景颜色 - 轻木色调
    public static final Color MAIN_BG_COLOR = new Color(255, 248, 225);      // 米黄色，与木质相配
    
    // 标题栏背景
    public static final Color TITLE_BG_COLOR = new Color(176, 115, 66);      // 中棕色，比边框浅一点
    
    // 导航栏背景
    public static final Color NAV_BG_COLOR = new Color(245, 230, 200);       // 浅黄色，与木质协调
    
    // 成功/完成页面背景色
    public static final Color SUCCESS_BG_COLOR = new Color(235, 245, 215);   // 浅绿黄色，显示完成感
    
    // Bonus任务面板背景色
    public static final Color BONUS_BG_COLOR = new Color(245, 235, 225);     // 浅粉木色
    
    // 特殊面板背景色
    public static final Color PANEL_HIGHLIGHT_1 = new Color(240, 230, 205);  // 浅棕黄色
    public static final Color PANEL_HIGHLIGHT_2 = new Color(230, 220, 195);  // 稍深棕黄色
    
    // 按钮颜色（提交等功能按钮）
    public static final Color FUNC_BUTTON_BG = new Color(180, 155, 115);     // 中木色，与边框协调
    
    // 任务完成指示颜色
    public static final Color TASK_COMPLETED_COLOR = new Color(144, 238, 144); // 浅绿色，表示任务完成
} 