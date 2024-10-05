package servlets;

import dataBase.UserDAOInterface;
import objects.Review;
import objects.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.System.out;
//review and rating
@WebServlet("/review")
public class reviewServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Review review = new Review(((User) request.getSession().getAttribute("currUser")).getId(),
                Integer.parseInt(request.getParameter("quizId")), request.getParameter("review"));
        UserDAOInterface userDAO = (UserDAOInterface) request.getServletContext().getAttribute("userDAO");
        userDAO.addReview(review);

        userDAO.setRating(((User)request.getSession().getAttribute("currUser")).getId(),
                Integer.parseInt(request.getParameter("quizId")), Double.parseDouble(request.getParameter("rating")));
        request.getRequestDispatcher("profile.jsp?self=true").forward(request, response);
    }
}
