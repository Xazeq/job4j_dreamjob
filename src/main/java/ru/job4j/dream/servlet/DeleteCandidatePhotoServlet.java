package ru.job4j.dream.servlet;

import ru.job4j.dream.store.DbStore;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class DeleteCandidatePhotoServlet extends HttpServlet {
    private final Properties cfg = DbStore.instOf().getConfig();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("id");
        File file = new File(cfg.getProperty("image.storage") + name + cfg.getProperty("image.extension"));
        if (file.exists()) {
            file.delete();
        }
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
