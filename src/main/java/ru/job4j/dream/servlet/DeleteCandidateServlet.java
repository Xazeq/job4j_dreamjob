package ru.job4j.dream.servlet;

import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteCandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Store store = Store.instOf();
        int candidateId = Integer.parseInt(req.getParameter("id"));
        store.deleteCandidate(candidateId);
        File file = new File("C:\\images\\" + candidateId + ".JPG");
        if (file.exists()) {
            file.delete();
        }
        req.setAttribute("candidates", Store.instOf().findAllCandidates());
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }
}
