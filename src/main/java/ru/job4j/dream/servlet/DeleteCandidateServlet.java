package ru.job4j.dream.servlet;

import ru.job4j.dream.store.MemStore;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class DeleteCandidateServlet extends HttpServlet {
    private final Properties cfg = MemStore.instOf().getConfig();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MemStore store = MemStore.instOf();
        int candidateId = Integer.parseInt(req.getParameter("id"));
        store.deleteCandidate(candidateId);
        File file = new File(cfg.getProperty("image.storage") + candidateId + cfg.getProperty("image.extension"));
        if (file.exists()) {
            file.delete();
        }
        req.setAttribute("candidates", MemStore.instOf().findAllCandidates());
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }
}
