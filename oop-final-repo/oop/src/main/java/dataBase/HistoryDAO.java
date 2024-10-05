package dataBase;

import objects.History;

import java.util.List;

public interface HistoryDAO {
    int ERROR = -1;
    int SUCCESS = 0;

    /**
     * Inserts a history entry into the database
     * @param history The history object to be inserted
     * @return SUCCESS if successful, ERROR otherwise
     */
    int insertHistory(History history);

    /**
     * Retrieves a history entry by its ID from the database
     * @param id The ID of the history entry
     * @return The retrieved history object, or null if not found
     */
    History getHistoryById(int id);

    /**
     * Retrieves all history entries for a specific user from the database
     * @param userId The ID of the user
     * @return A list of history objects for the user
     */
    List<History> getHistoryByUserId(int userId);

    /**
     * Retrieves all history entries for a specific quiz from the database
     * @param quizId The ID of the quiz
     * @return A list of history objects for the quiz
     */
    List<History> getHistoryByQuizId(int quizId);
    List<History> UserRecentHistory(int quizId, int userId, int limit);
    List<History> sortedHistory(int quizId, String orderType, int limit);
}
