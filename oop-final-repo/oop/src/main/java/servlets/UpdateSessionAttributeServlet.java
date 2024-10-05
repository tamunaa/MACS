package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.QuestionResponsePair;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/UpdateSessionAttributeServlet")
public class UpdateSessionAttributeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonString = request.getParameter("data");

        // Convert the JSON string back to a List<QuestionResponsePair>
        Gson gson = new Gson();
        List<QuestionResponsePair> updatedPairs = gson.fromJson(jsonString, new TypeToken<List<QuestionResponsePair>>() {}.getType());

        // Update the session attribute with the new data
        request.getSession().setAttribute("responses", updatedPairs);

        response.getWriter().write("Session attribute updated successfully");
    }
}
