package com.github.woonsan.oak.app.simple;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SimpleOakAppApplication {

    private static Logger log = LoggerFactory.getLogger(SimpleOakAppApplication.class);

    @Autowired
    private Repository repository;

    public static void main(String[] args) {
        SpringApplication.run(SimpleOakAppApplication.class, args);
    }

    @RequestMapping("/echo")
    public String getEcho(@RequestParam(name = "message", required = false) String message) {
        return message;
    }

    @RequestMapping(path = { "/greeting" }, method = { RequestMethod.GET })
    public String getGreeting() {
        String message = "";
        Session session = null;

        try {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

            Node root = session.getRootNode();
            Node greeting = null;

            if (root.hasNode("greeting")) {
                greeting = root.getNode("greeting");

                if (greeting.hasProperty("message")) {
                    message = greeting.getProperty("message").getString();
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to read greeting message.", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

        return message;
    }

    @RequestMapping(path = { "/greeting" }, method = { RequestMethod.POST })
    public String createGreeting(@RequestParam(name = "message", required = true) String message) {
        Session session = null;

        try {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

            Node root = session.getRootNode();
            Node greeting = null;

            if (root.hasNode("greeting")) {
                greeting = root.getNode("greeting");
            } else {
                greeting = root.addNode("greeting");
            }

            greeting.setProperty("message", message);
            session.save();
        } catch (RepositoryException e) {
            log.error("Failed to save greeting message.", e);

            try {
                session.refresh(false);
            } catch (RepositoryException re) {
                log.error("Failed to refresh.", re);
            }
        } finally {
            if (session != null) {
                session.logout();
            }
        }

        return message;
    }
}
