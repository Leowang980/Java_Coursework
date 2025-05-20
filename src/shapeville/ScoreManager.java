package shapeville;

/**
 * Utility class to manage scoring and attempts across different tasks
 */
public class ScoreManager {
    // Score values for basic level
    public static final int BASIC_FIRST_ATTEMPT = 3;
    public static final int BASIC_SECOND_ATTEMPT = 2;
    public static final int BASIC_THIRD_ATTEMPT = 1;
    
    // Score values for advanced level
    public static final int ADVANCED_FIRST_ATTEMPT = 6;
    public static final int ADVANCED_SECOND_ATTEMPT = 4;
    public static final int ADVANCED_THIRD_ATTEMPT = 2;
    
    // Maximum number of attempts allowed
    public static final int MAX_ATTEMPTS = 3;
    private static int score = 0;// 分数存储
    // 新增全局进度存储
    private static int totalProgress = 0;
    private static final int TOTAL_TASKS = 6;//总任务数
    /**
     * Calculate score based on level and number of attempts
     * @param isAdvanced true if advanced level, false if basic level
     * @param attemptNumber the attempt number (1-3)
     * @return calculated score
     */
    public static int calculateScore(boolean isAdvanced, int attemptNumber) {
        if (attemptNumber < 1 || attemptNumber > MAX_ATTEMPTS) {
            return 0;
        }
        
        if (isAdvanced) {
            switch (attemptNumber) {
                case 1: return ADVANCED_FIRST_ATTEMPT;
                case 2: return ADVANCED_SECOND_ATTEMPT;
                case 3: return ADVANCED_THIRD_ATTEMPT;
                default: return 0;
            }
        } else {
            switch (attemptNumber) {
                case 1: return BASIC_FIRST_ATTEMPT;
                case 2: return BASIC_SECOND_ATTEMPT;
                case 3: return BASIC_THIRD_ATTEMPT;
                default: return 0;
            }
        }
    }
    
    /**
     * Get a feedback message based on the score
     * @param score the score received
     * @return appropriate feedback message
     */
    public static String getFeedbackMessage(int score) {
        if (score >= ADVANCED_FIRST_ATTEMPT) {
            return "Outstanding! Perfect on the first try!";
        } else if (score >= BASIC_FIRST_ATTEMPT) {
            return "Great job!";
        } else if (score >= BASIC_THIRD_ATTEMPT) {
            return "Good effort!";
        } else {
            return "Keep practicing!";
        }
    }
    public static void addTaskProgress() {
        int increment = 100 / TOTAL_TASKS;
        totalProgress = Math.min(totalProgress + increment, 100);
    }

    public static int getGlobalProgress() {
        return totalProgress;
    }
    /**
     * Calculate progress percentage based on completed items and total items
     * @param completed number of completed items
     * @param total total number of items
     * @return progress percentage (0-100)
     */
    public static int calculateProgress(int completed, int total) {
        if (total <= 0) {
            return 0;
        }
        return (completed * 100) / total;
    }
    public static int getScore() {
        return score;
    }

    public static void addScore(int points) {
        score += points;
    }
} 