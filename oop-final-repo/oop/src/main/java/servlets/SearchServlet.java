package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

import dataBase.*;
import objects.User;
import objects.Quiz;


@WebServlet(name = "SearchServlet", value = "/SearchServlet")
public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = (DbQuizDAO) request.getServletContext().getAttribute("quizDAO");
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        String searchInput = request.getParameter("searchInput");
        String type = request.getParameter("type");

        System.out.println("serach " + searchInput);
        System.out.println("type "+ type);

        if (type.equals("quizName")) {

            List<Quiz> quizzes = quizDAO.getQuizByQuizName(searchInput);
            if (quizzes.size() == 0){
//                rame mesiji
            }else{
                request.getRequestDispatcher("/quizPage.jsp?searchInput="+searchInput).forward(request, response);
            }
        } else if (type.equals("quizTag")){

            List<Quiz> quizzes = quizDAO.getQuizzesByTag(searchInput);
            request.setAttribute("searchResult", quizzes);
            request.getRequestDispatcher("/quizTagSearch.jsp").forward(request, response);
        } else {
            User user = userDAO.getUserByUsername(searchInput);
            request.setAttribute("searchedUser", user);
            String path = "profile.jsp?self=false&&username="+searchInput;
            request.getRequestDispatcher(path).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}