package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.himadri.passwordmanager.entity.DataEntity;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class StoreManager extends HttpServlet {

    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String domain = request.getParameter("domain");
        String hex = request.getParameter("hex");
        if(StringUtils.isEmpty(domain) || StringUtils.isEmpty(hex)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        DataEntity dataEntity = new DataEntity(userId, domain, hex);
        ofy().save().entities(dataEntity).now();
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType(MediaType.JSON_UTF_8.toString());
        gson.toJson(dataEntity, DataEntity.class, new JsonWriter(response.getWriter()));
    }
}
