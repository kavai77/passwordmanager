package net.himadri.passwordmanager.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger LOG = Logger.getLogger(AdminController.class.getName());

    @RequestMapping("/refactor")
    public void refactor() {

    }
}
