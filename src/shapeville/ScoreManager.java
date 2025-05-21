package shapeville;

import java.util.HashSet;
import java.util.Set;

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
    
    // Persistent score and progress storage
    private static int score = 0;// 分数存储
    
    // Persistent progress tracking for all tasks
    private static boolean task1_2dCompleted = false;
    private static boolean task1_3dCompleted = false;
    private static boolean task2Completed = false;
    private static boolean task3Completed = false;
    private static boolean task4Completed = false;
    private static boolean bonus1Completed = false;
    private static boolean bonus2Completed = false;
    
    // Task module scores
    private static int task1_2dScore = 0;
    private static int task1_3dScore = 0;
    private static int task2Score = 0;
    private static int task3Score = 0;
    private static int task4Score = 0;
    private static int bonus1Score = 0;
    private static int bonus2Score = 0;
    
    // Sets to track answered questions by name or ID
    private static Set<String> answeredTask1_2dQuestions = new HashSet<>();
    private static Set<String> answeredTask1_3dQuestions = new HashSet<>();
    private static Set<String> answeredTask2Questions = new HashSet<>();
    private static Set<String> answeredTask3Questions = new HashSet<>();
    private static Set<String> answeredTask4Questions = new HashSet<>();
    private static Set<String> answeredBonus1Questions = new HashSet<>();
    private static Set<String> answeredBonus2Questions = new HashSet<>();
    
    // Progress trackers for each task
    private static int task1_2dProgress = 0;
    private static int task1_3dProgress = 0;
    private static int task2Progress = 0;
    private static int task3Progress = 0;
    private static int task4Progress = 0;
    private static int bonus1Progress = 0;
    private static int bonus2Progress = 0;
    
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
    
    // Task completion status getters and setters
    public static boolean isTask1_2dCompleted() {
        return task1_2dCompleted;
    }
    
    public static void setTask1_2dCompleted(boolean completed) {
        task1_2dCompleted = completed;
    }
    
    public static boolean isTask1_3dCompleted() {
        return task1_3dCompleted;
    }
    
    public static void setTask1_3dCompleted(boolean completed) {
        task1_3dCompleted = completed;
    }
    
    public static boolean isTask2Completed() {
        return task2Completed;
    }
    
    public static void setTask2Completed(boolean completed) {
        task2Completed = completed;
    }
    
    public static boolean isTask3Completed() {
        return task3Completed;
    }
    
    public static void setTask3Completed(boolean completed) {
        task3Completed = completed;
    }
    
    public static boolean isTask4Completed() {
        return task4Completed;
    }
    
    public static void setTask4Completed(boolean completed) {
        task4Completed = completed;
    }
    
    public static boolean isBonus1Completed() {
        return bonus1Completed;
    }
    
    public static void setBonus1Completed(boolean completed) {
        bonus1Completed = completed;
    }
    
    public static boolean isBonus2Completed() {
        return bonus2Completed;
    }
    
    public static void setBonus2Completed(boolean completed) {
        bonus2Completed = completed;
    }
    
    // Question-level tracking methods
    
    // Task 1 2D Shape methods
    public static boolean isShape2DAnswered(String shapeName) {
        return answeredTask1_2dQuestions.contains(shapeName);
    }
    
    public static void markShape2DAnswered(String shapeName) {
        answeredTask1_2dQuestions.add(shapeName);
        task1_2dProgress++;
    }
    
    public static int getTask1_2dProgress() {
        return task1_2dProgress;
    }
    
    public static void addToTask1_2dScore(int points) {
        task1_2dScore += points;
    }
    
    public static int getTask1_2dScore() {
        return task1_2dScore;
    }
    
    // Task 1 3D Shape methods
    public static boolean isShape3DAnswered(String shapeName) {
        return answeredTask1_3dQuestions.contains(shapeName);
    }
    
    public static void markShape3DAnswered(String shapeName) {
        answeredTask1_3dQuestions.add(shapeName);
        task1_3dProgress++;
    }
    
    public static int getTask1_3dProgress() {
        return task1_3dProgress;
    }
    
    public static void addToTask1_3dScore(int points) {
        task1_3dScore += points;
    }
    
    public static int getTask1_3dScore() {
        return task1_3dScore;
    }
    
    // Task 2 Angle methods
    public static boolean isAngleTypeAnswered(String angleType) {
        return answeredTask2Questions.contains(angleType);
    }
    
    public static void markAngleTypeAnswered(String angleType) {
        answeredTask2Questions.add(angleType);
        task2Progress++;
    }
    
    public static int getTask2Progress() {
        return task2Progress;
    }
    
    public static void addToTask2Score(int points) {
        task2Score += points;
    }
    
    public static int getTask2Score() {
        return task2Score;
    }
    
    // Task 3 Area methods
    public static boolean isTask3Answered(String questionId) {
        return answeredTask3Questions.contains(questionId);
    }
    public static void markTask3Answered(String questionId) {
        answeredTask3Questions.add(questionId);
        task3Progress++;
    }
    public static int getTask3Progress() {
        return task3Progress;
    }
    
    // Clear methods for testing
    public static void resetAllProgress() {
        score = 0;
        task1_2dCompleted = false;
        task1_3dCompleted = false;
        task2Completed = false;
        task3Completed = false;
        task4Completed = false;
        bonus1Completed = false;
        bonus2Completed = false;
        
        task1_2dScore = 0;
        task1_3dScore = 0;
        task2Score = 0;
        task3Score = 0;
        task4Score = 0;
        bonus1Score = 0;
        bonus2Score = 0;
        
        answeredTask1_2dQuestions.clear();
        answeredTask1_3dQuestions.clear();
        answeredTask2Questions.clear();
        answeredTask3Questions.clear();
        answeredTask4Questions.clear();
        answeredBonus1Questions.clear();
        answeredBonus2Questions.clear();
        
        task1_2dProgress = 0;
        task1_3dProgress = 0;
        task2Progress = 0;
        task3Progress = 0;
        task4Progress = 0;
        bonus1Progress = 0;
        bonus2Progress = 0;
        
        totalProgress = 0;
    }
} 