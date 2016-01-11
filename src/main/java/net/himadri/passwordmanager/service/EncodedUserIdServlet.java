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
        String encodedUserId = request.getParameter("encodedUserId");
        if (StringUtils.isEmpty(encodedUserId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        ofy().save().entity(new EncodedUserId(currentUser.getUserId(), encodedUserId, currentUser.getEmail()));
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
