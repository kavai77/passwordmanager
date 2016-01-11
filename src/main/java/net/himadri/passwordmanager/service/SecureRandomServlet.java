package net.himadri.passwordmanager.service;

import com.google.common.net.MediaType;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;

public class SecureRandomServlet extends HttpServlet {

    public static final int COUNT = 20;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(MediaType.PLAIN_TEXT_UTF_8.toString());
        response.getWriter().print(createSecureRandom());
    }

    String createSecureRandom() {
        return RandomStringUtils.random(COUNT, 0, 0, true, true, null, new SecureRandom());
    }

}
