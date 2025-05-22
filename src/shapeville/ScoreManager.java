package shapeville;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to manage scoring and progress tracking across different tasks in the Shapeville application.
 * This class handles score calculation, progress tracking, and completion status for all learning modules.
 *
 * @author Shapeville Team
 * @version 1.0
 */
public class ScoreManager {
    /** Score value for first attempt in basic level */
    public static final int BASIC_FIRST_ATTEMPT = 3;
    
    /** Score value for second attempt in basic level */
    public static final int BASIC_SECOND_ATTEMPT = 2;
    
    /** Score value for third attempt in basic level */
    public static final int BASIC_THIRD_ATTEMPT = 1;
    
    /** Score value for first attempt in advanced level */
    public static final int ADVANCED_FIRST_ATTEMPT = 6;
    
    /** Score value for second attempt in advanced level */
    public static final int ADVANCED_SECOND_ATTEMPT = 4;
    
    /** Score value for third attempt in advanced level */
    public static final int ADVANCED_THIRD_ATTEMPT = 2;
    
    /** Maximum number of attempts allowed per question */
    public static final int MAX_ATTEMPTS = 3;
    
    /** Total score accumulated across all tasks */
    private static int score = 0;
    
    /** Completion status flags for each task module */
    private static boolean task1_2dCompleted = false;
    private static boolean task1_3dCompleted = false;
    private static boolean task2Completed = false;
    private static boolean task3Completed = false;
    private static boolean task4Completed = false;
    private static boolean bonus1Completed = false;
    private static boolean bonus2Completed = false;
    
    /** Individual scores for each task module */
    private static int task1_2dScore = 0;
    private static int task1_3dScore = 0;
    private static int task2Score = 0;
    private static int task3Score = 0;
    private static int task4Score = 0;
    private static int bonus1Score = 0;
    private static int bonus2Score = 0;
    
    /** Sets to track answered questions for each task module */
    private static Set<String> answeredTask1_2dQuestions = new HashSet<>();
    private static Set<String> answeredTask1_3dQuestions = new HashSet<>();
    private static Set<String> answeredTask2Questions = new HashSet<>();
    private static Set<String> answeredTask3Questions = new HashSet<>();
    private static Set<String> answeredTask4Questions = new HashSet<>();
    private static Set<String> answeredBonus1Questions = new HashSet<>();
    private static Set<String> answeredBonus2Questions = new HashSet<>();
    
    /** Progress counters for each task module */
    private static int task1_2dProgress = 0;
    private static int task1_3dProgress = 0;
    private static int task2Progress = 0;
    private static int task3Progress = 0;
    private static int task4Progress = 0;
    private static int bonus1Progress = 0;
    private static int bonus2Progress = 0;
    
    /** Global progress tracking */
    private static int totalProgress = 0;
    
    /** Total number of tasks in the application */
    private static final int TOTAL_TASKS = 6;
    
    /**
     * Calculates the score based on difficulty level and attempt number.
     *
     * @param isAdvanced true if the question is at advanced level, false for basic level
     * @param attemptNumber the current attempt number (1-3)
     * @return the calculated score for the attempt
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
     * Generates a feedback message based on the score achieved.
     *
     * @param score the score received for the attempt
     * @return a motivational feedback message
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
    
    /**
     * Increments the global progress by one task unit.
     * The increment is calculated as 100 divided by the total number of tasks.
     */
    public static void addTaskProgress() {
        int increment = 100 / TOTAL_TASKS;
        totalProgress = Math.min(totalProgress + increment, 100);
    }

    /**
     * Gets the current global progress percentage.
     *
     * @return the global progress as a percentage (0-100)
     */
    public static int getGlobalProgress() {
        return totalProgress;
    }
    
    /**
     * Calculates the progress percentage for a specific task.
     *
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
    
    /**
     * Gets the total score accumulated across all tasks.
     *
     * @return the total score
     */
    public static int getScore() {
        return score;
    }

    /**
     * Adds points to the total score.
     *
     * @param points the points to add
     */
    public static void addScore(int points) {
        score += points;
    }
    
    /**
     * Checks if Task 1's 2D shapes module is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isTask1_2dCompleted() {
        return task1_2dCompleted;
    }
    
    /**
     * Sets the completion status for Task 1's 2D shapes module.
     *
     * @param completed the completion status to set
     */
    public static void setTask1_2dCompleted(boolean completed) {
        task1_2dCompleted = completed;
    }
    
    /**
     * Checks if Task 1's 3D shapes module is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isTask1_3dCompleted() {
        return task1_3dCompleted;
    }
    
    /**
     * Sets the completion status for Task 1's 3D shapes module.
     *
     * @param completed the completion status to set
     */
    public static void setTask1_3dCompleted(boolean completed) {
        task1_3dCompleted = completed;
    }
    
    /**
     * Checks if Task 2 (Angle Types) is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isTask2Completed() {
        return task2Completed;
    }
    
    /**
     * Sets the completion status for Task 2.
     *
     * @param completed the completion status to set
     */
    public static void setTask2Completed(boolean completed) {
        task2Completed = completed;
    }
    
    /**
     * Checks if Task 3 (Area Calculation) is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isTask3Completed() {
        return task3Completed;
    }
    
    /**
     * Sets the completion status for Task 3.
     *
     * @param completed the completion status to set
     */
    public static void setTask3Completed(boolean completed) {
        task3Completed = completed;
    }
    
    /**
     * Checks if Task 4 (Circle Calculations) is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isTask4Completed() {
        return task4Completed;
    }
    
    /**
     * Sets the completion status for Task 4.
     *
     * @param completed the completion status to set
     */
    public static void setTask4Completed(boolean completed) {
        task4Completed = completed;
    }
    
    /**
     * Checks if Bonus Task 1 (Compound Shapes) is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isBonus1Completed() {
        return bonus1Completed;
    }
    
    /**
     * Sets the completion status for Bonus Task 1.
     *
     * @param completed the completion status to set
     */
    public static void setBonus1Completed(boolean completed) {
        bonus1Completed = completed;
    }
    
    /**
     * Checks if Bonus Task 2 (Sector & Arc) is completed.
     *
     * @return true if completed, false otherwise
     */
    public static boolean isBonus2Completed() {
        return bonus2Completed;
    }
    
    /**
     * Sets the completion status for Bonus Task 2.
     *
     * @param completed the completion status to set
     */
    public static void setBonus2Completed(boolean completed) {
        bonus2Completed = completed;
    }
    
    /**
     * Checks if a specific 2D shape question has been answered.
     *
     * @param shapeName the name of the shape
     * @return true if the shape has been answered, false otherwise
     */
    public static boolean isShape2DAnswered(String shapeName) {
        return answeredTask1_2dQuestions.contains(shapeName);
    }
    
    /**
     * Marks a 2D shape question as answered and updates progress.
     *
     * @param shapeName the name of the shape
     */
    public static void markShape2DAnswered(String shapeName) {
        answeredTask1_2dQuestions.add(shapeName);
        task1_2dProgress++;
    }
    
    /**
     * Gets the progress for Task 1's 2D shapes module.
     *
     * @return the number of completed 2D shape questions
     */
    public static int getTask1_2dProgress() {
        return task1_2dProgress;
    }
    
    /**
     * Adds points to Task 1's 2D shapes score.
     *
     * @param points the points to add
     */
    public static void addToTask1_2dScore(int points) {
        task1_2dScore += points;
    }
    
    /**
     * Gets the score for Task 1's 2D shapes module.
     *
     * @return the total score for 2D shapes
     */
    public static int getTask1_2dScore() {
        return task1_2dScore;
    }
    
    /**
     * Checks if a specific 3D shape question has been answered.
     *
     * @param shapeName the name of the shape
     * @return true if the shape has been answered, false otherwise
     */
    public static boolean isShape3DAnswered(String shapeName) {
        return answeredTask1_3dQuestions.contains(shapeName);
    }
    
    /**
     * Marks a 3D shape question as answered and updates progress.
     *
     * @param shapeName the name of the shape
     */
    public static void markShape3DAnswered(String shapeName) {
        answeredTask1_3dQuestions.add(shapeName);
        task1_3dProgress++;
    }
    
    /**
     * Gets the progress for Task 1's 3D shapes module.
     *
     * @return the number of completed 3D shape questions
     */
    public static int getTask1_3dProgress() {
        return task1_3dProgress;
    }
    
    /**
     * Adds points to Task 1's 3D shapes score.
     *
     * @param points the points to add
     */
    public static void addToTask1_3dScore(int points) {
        task1_3dScore += points;
    }
    
    /**
     * Gets the score for Task 1's 3D shapes module.
     *
     * @return the total score for 3D shapes
     */
    public static int getTask1_3dScore() {
        return task1_3dScore;
    }
    
    /**
     * Checks if a specific angle type question has been answered.
     *
     * @param angleType the type of angle
     * @return true if the angle type has been answered, false otherwise
     */
    public static boolean isAngleTypeAnswered(String angleType) {
        return answeredTask2Questions.contains(angleType);
    }
    
    /**
     * Marks an angle type question as answered and updates progress.
     *
     * @param angleType the type of angle
     */
    public static void markAngleTypeAnswered(String angleType) {
        answeredTask2Questions.add(angleType);
        task2Progress++;
    }
    
    /**
     * Gets the progress for Task 2 (Angle Types).
     *
     * @return the number of completed angle type questions
     */
    public static int getTask2Progress() {
        return task2Progress;
    }
    
    /**
     * Adds points to Task 2's score.
     *
     * @param points the points to add
     */
    public static void addToTask2Score(int points) {
        task2Score += points;
    }
    
    /**
     * Gets the score for Task 2.
     *
     * @return the total score for angle types
     */
    public static int getTask2Score() {
        return task2Score;
    }
    
    /**
     * Checks if a specific Task 3 question has been answered.
     *
     * @param questionId the identifier of the question
     * @return true if the question has been answered, false otherwise
     */
    public static boolean isTask3Answered(String questionId) {
        return answeredTask3Questions.contains(questionId);
    }
    
    /**
     * Marks a Task 3 question as answered and updates progress.
     *
     * @param questionId the identifier of the question
     */
    public static void markTask3Answered(String questionId) {
        answeredTask3Questions.add(questionId);
        task3Progress++;
    }
    
    /**
     * Gets the progress for Task 3.
     *
     * @return the number of completed Task 3 questions
     */
    public static int getTask3Progress() {
        return task3Progress;
    }
    
    /**
     * Resets all progress and scores to their initial state.
     * This method is primarily used for testing purposes.
     */
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