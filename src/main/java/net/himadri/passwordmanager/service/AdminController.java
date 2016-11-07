package net.himadri.passwordmanager.service;

import com.googlecode.objectify.Objectify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger LOG = Logger.getLogger(AdminController.class.getName());

    @Autowired
    Objectify ofy;

    @RequestMapping("/refactor")
    public void refactor() {

    }
}
