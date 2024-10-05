package servlets;

import dataBase.UserDAO;
import objects.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
@WebServlet(name = "RegisterServlet", value = "/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        UserDAO userDAO2 = (UserDAO) request.getSession().getAttribute("userDAO");

        System.out.println(userDAO2);

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            //.........................mesigi unda gamovitanot ro daregistrirebulia........
            request.getSession().setAttribute("currUser", user);
            request.setAttribute("text", "User already exists, try different username.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else {
            BCrypt.Hasher hasher = BCrypt.withDefaults();
            char[] chars = new char[password.length()];
            for(int i = 0; i < password.length(); i++ ){
                chars[i] = password.charAt(i);
            }
            String passHash = hasher.hashToString(10,chars);
            User newUser = new User(username,email,passHash,false);
            userDAO.addUser(newUser);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
