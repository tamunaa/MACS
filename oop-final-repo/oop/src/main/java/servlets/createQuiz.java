package servlets;

import dataBase.AchievementDAO;
import dataBase.DbQuizDAO;
import dataBase.QuizDAO;
import dataBase.UserDAO;
import objects.Quiz;
import objects.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;


@WebServlet("/createQuiz")
public class createQuiz extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = (DbQuizDAO) request.getServletContext().getAttribute("quizDAO");
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        AchievementDAO achievementDAO = (AchievementDAO) request.getServletContext().getAttribute("achievementDAO");


        int author = ((User) request.getSession().getAttribute("currUser")).getId();
        String quizName = request.getParameter("quizName");
        int timer = Integer.parseInt(request.getParameter("timer"));

        boolean practiceMode = request.getParameter("practice") != null;
        String questionDisplayMode = request.getParameter("questionDisplay");
        boolean immediateCorrection = request.getParameter("correction") != null;
        String quizDescription = request.getParameter("quizDescription");
        boolean randomOrder = request.getParameter("randomOrder") != null;
        String category = request.getParameter("category");
        if (category == null){
            category = "other";
        }

        Quiz quiz = new Quiz(author, quizName, quizDescription, timer, category);
        quiz.setPractice(practiceMode);
        quiz.setCorrectImmediately(immediateCorrection);
        quiz.setOnOnePage(questionDisplayMode.equals("singlePage"));
        quiz.setRandom(randomOrder);
        int quizId = quizDAO.addQuiz(quiz);

        String tagsInput = request.getParameter("tags");
        String[] tagsArray = tagsInput.split(",");

        int quizzesCreated = quizDAO.getQuizzesByAuthor(author).size();
        if(quizzesCreated == 1){
            achievementDAO.addUserAchievement(author, 1);
        }else if(quizzesCreated == 5){
            achievementDAO.addUserAchievement(author, 5);
        }else if(quizzesCreated == 10) {
            achievementDAO.addUserAchievement(author, 4);
        }

        for (String s : tagsArray) {
            s = s.trim();
            if (s.isEmpty())continue;
            quizDAO.addTag(quiz.getID(), s);
        }

        response.sendRedirect("editQuiz?quizId=" + quizId);
    }
}
