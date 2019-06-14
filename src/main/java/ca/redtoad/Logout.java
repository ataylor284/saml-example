package ca.redtoad;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.api.SAML2ProfileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logout extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(Logout.class);
    private final SAML2ClientBuilder saml2ClientBuilder = new SAML2ClientBuilder();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.info("Logout");
            WebContext context = new J2EContext(request, response);
            SAML2Client client = saml2ClientBuilder.build();

            String samlResponse = request.getParameter("SAMLResponse");
            if (samlResponse != null) {
                SAML2ProfileHandler<LogoutRequest> logoutProfileHandler = client.getLogoutProfileHandler();
                log.info("handler = " + logoutProfileHandler);
                try {
                    logoutProfileHandler.receive(client.getContextProvider().buildContext(context));
                } catch (HttpAction e) {
                    if (e.getCode() == 200) {
                        response.sendRedirect("/");
                    }
                }
                throw new RuntimeException("unhandled logout response " + response);
            }
            else {
                ProfileManager<SAML2Profile> profileManager = new ProfileManager<>(context);
                SAML2Profile profile = profileManager.get(true).get();
                log.info("profile = " + profile);
                profileManager.logout();
                RedirectAction action = client.getLogoutAction(context, profile, null);
                action.perform(context);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
