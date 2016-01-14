package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.EncodedUserId;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class EncodedUserIdServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        String encodedUserId = request.getParameter("encodedUserId");
        if (StringUtils.isEmpty(operation) || StringUtils.isEmpty(encodedUserId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        switch (operation) {
            case "store":
                ofy().save().entity(new EncodedUserId(currentUser.getUserId(), encodedUserId, currentUser.getEmail()));
                response.setStatus(HttpServletResponse.SC_CREATED);
                break;
            case "check":
                EncodedUserId userId = ofy().load().type(EncodedUserId.class).id(currentUser.getUserId()).safe();
                response.setStatus(StringUtils.equals(encodedUserId, userId.getEncoded())
                        ? HttpServletResponse.SC_OK : HttpServletResponse.SC_UNAUTHORIZED);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
