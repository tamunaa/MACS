import dataBase.questionsDAOs.QuestionsDAO;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.sql.Connection;
import java.sql.SQLException;
import objects.*;
import dataBase.*;
import dataBase.questionsDAOs.*;
@WebListener
public class ContextListener implements ServletContextListener, HttpSessionListener  {
    ConnectionPool pool;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        pool = new ConnectionPool(10);

        ServletContext context = sce.getServletContext();
        UserDAO userDAO = new UserDAO(pool);
        FriendsDAO friendsDAO = new FriendsDAO(pool);
        MessageDAO messageDAO = new MessageDAO(pool);
        HistoryDAO historyDAO = new HistoryDAOSQL(pool);
        DbQuizDAO quizDAO = new DbQuizDAO(pool);
        AchievementDAO achievementDAO = new DbAchievementDAO(pool);
        QuestionsDAO questionsDAO = new QuestionsDAO(pool);
        AnnouncementDAO announcementDAO = new AnnouncementDAOSQL(pool);
        ResponseDAO responseDAO = new ResponseDAO(pool);
        ChallengeDAO challengeDAO = new DbChallengeDAO(pool);
        GradeDAO gradeDAO;
        try {
            gradeDAO = new GradeDAO(pool);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        context.setAttribute("userDAO", userDAO);
        context.setAttribute("friendsDAO", friendsDAO);
        context.setAttribute("messageDAO", messageDAO);
        context.setAttribute("historyDAO", historyDAO);
        context.setAttribute("quizDAO", quizDAO);
        context.setAttribute("achievementDAO", achievementDAO);
        context.setAttribute("questionsDAO", questionsDAO);
        context.setAttribute("announcementDAO", announcementDAO);
        context.setAttribute("responseDAO", responseDAO);
        context.setAttribute("challengeDAO", challengeDAO);
        context.setAttribute("gradeDAO", gradeDAO);

        FriendshipService service = new FriendshipService();
        context.setAttribute("friendshipService", service);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("userDAO");
        sce.getServletContext().removeAttribute("friendsDAO");
        sce.getServletContext().removeAttribute("messageDAO");
        sce.getServletContext().removeAttribute("historyDAO");
        sce.getServletContext().removeAttribute("friendshipService");
        sce.getServletContext().removeAttribute("questionsDAO");
        sce.getServletContext().removeAttribute("achievementDAO");
        sce.getServletContext().removeAttribute("challengeDAO");
        sce.getServletContext().removeAttribute("responseDAO");
        sce.getServletContext().removeAttribute("announcementDAO");
        try {
            pool.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        httpSessionEvent.getSession().setAttribute("currUser", null);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

    }
}
