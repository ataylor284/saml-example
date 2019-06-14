package ca.redtoad;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Login.class);
    private final SAML2ClientBuilder saml2ClientBuilder = new SAML2ClientBuilder();

    // serves GET /login
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            WebContext context = new J2EContext(request, response);
            SAML2Client client = saml2ClientBuilder.build();
            client.redirect(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // serves POST /login/finish
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            WebContext context = new J2EContext(request, response);
            SAML2Client client = saml2ClientBuilder.build();

            SAML2Credentials credentials = client.getCredentials(context);
            SAML2Profile profile = client.getUserProfile(credentials, context);

            log.info("received done message");
            log.info("credentials = " + credentials);
            log.info("profile = " + profile);

            ProfileManager<SAML2Profile> profileManager = new ProfileManager<>(context);
            profileManager.save(true, profile, false);

            HttpSession session = request.getSession();
            response.sendRedirect((String) session.getAttribute("redirecturl"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
