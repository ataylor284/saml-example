package ca.redtoad;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.saml.profile.SAML2Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Secure extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(Secure.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebContext context = new J2EContext(request, response);
        ProfileManager<SAML2Profile> profileManager = new ProfileManager<>(context);

        if (!profileManager.isAuthenticated()) {
            HttpSession session = request.getSession();
            session.setAttribute("redirecturl", request.getRequestURL().toString());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/login");
            dispatcher.forward(request, response);
        } else {
            SAML2Profile profile = profileManager.get(true).get();
            log.info("profile = " + profile);
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().printf("<html>Hello %s from secure! <a href='/logout'>Logout</a></html>", profile.getId());
        }
    }
}
