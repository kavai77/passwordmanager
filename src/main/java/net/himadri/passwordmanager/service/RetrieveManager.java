package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.himadri.passwordmanager.entity.DataEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class RetrieveManager extends HttpServlet {

    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        List<DataEntity> dataEntityList = ofy().load().type(DataEntity.class).filter("userId", userId).order("domain").list();
        response.setContentType(MediaType.JSON_UTF_8.toString());
        gson.toJson(dataEntityList, List.class, new JsonWriter(response.getWriter()));
    }
}
