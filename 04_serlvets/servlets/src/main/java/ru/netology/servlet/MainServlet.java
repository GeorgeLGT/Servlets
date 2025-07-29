package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.AppConfig;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {

    private static final String PATH_POSTS = "/api/posts";
    private static final String PATH_POSTS_ID_PREFIX = "/api/posts/";

    private PostController controller;

    @Override
    public void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals("GET") && path.equals(PATH_POSTS)) {
                controller.all(resp);
                return;
            }
            if (method.equals("GET") && path.startsWith(PATH_POSTS_ID_PREFIX)) {
                final var idStr = path.substring(PATH_POSTS_ID_PREFIX.length());
                long id = Long.parseLong(idStr);
                controller.getById(id, resp);
                return;
            }
            if (method.equals("POST") && path.equals(PATH_POSTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.startsWith(PATH_POSTS_ID_PREFIX)) {
                final var idStr = path.substring(PATH_POSTS_ID_PREFIX.length());
                long id = Long.parseLong(idStr);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}