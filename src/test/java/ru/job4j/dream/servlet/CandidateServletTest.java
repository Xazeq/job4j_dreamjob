package ru.job4j.dream.servlet;

import org.junit.Before;
import org.junit.Test;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.notNullValue;

public class CandidateServletTest {
    @Before
    public void clearTable() {
        DbStore.instOf().clearTables();
    }

    @Test
    public void whenCreateCandidate() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("0");
        when(req.getParameter("name")).thenReturn("candidate name");
        when(req.getParameter("cities")).thenReturn("1");
        new CandidateServlet().doPost(req, resp);
        Candidate candidate = DbStore.instOf().findAllCandidates().stream().findFirst().get();
        assertThat(candidate, notNullValue());
        assertThat(candidate.getName(), is("candidate name"));
    }

    @Test
    public void whenUpdateCandidate() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("0");
        when(req.getParameter("name")).thenReturn("candidate name");
        when(req.getParameter("cities")).thenReturn("1");
        CandidateServlet servlet = new CandidateServlet();
        servlet.doPost(req, resp);
        Candidate oldCandidate = DbStore.instOf().findAllCandidates().stream().findFirst().get();
        when(req.getParameter("id")).thenReturn(String.valueOf(oldCandidate.getId()));
        when(req.getParameter("name")).thenReturn("new candidate name");
        when(req.getParameter("cities")).thenReturn("1");
        servlet.doPost(req, resp);
        Candidate newCandidate = DbStore.instOf().findAllCandidates().stream().findFirst().get();
        assertThat(newCandidate, notNullValue());
        assertThat(newCandidate.getName(), is("new candidate name"));
    }
}