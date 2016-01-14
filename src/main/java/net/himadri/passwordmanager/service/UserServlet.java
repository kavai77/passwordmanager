package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.EncodedUserId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class UserServlet extends HttpServlet {

    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        ofy().save().entity(new AccessLog(user.getUserId(), user.getEmail(), new Date()));
        response.setContentType(MediaType.JSON_UTF_8.type());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", user.getUserId());
        jsonObject.addProperty("nickName", user.getNickname());
        jsonObject.addProperty("logoutURL", userService.createLogoutURL("/index.html"));
        EncodedUserId encodedUserId = ofy().load().type(EncodedUserId.class).id(user.getUserId()).now();
        jsonObject.addProperty("encodedUserId", encodedUserId != null);
        gson.toJson(jsonObject, new JsonWriter(response.getWriter()));
    }

}
