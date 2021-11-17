package ru.job4j.dream.store;

import org.junit.Before;
import org.junit.Test;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DbStoreTest {
    @Before
    public void clearTable() {
        DbStore.instOf().clearTables();
    }

    @Test
    public void whenCreatePost() {
        Store store = DbStore.instOf();
        Post post = new Post(0, "Java Job", "Job description");
        store.save(post);
        Post postInDb = store.findPostById(post.getId());
        assertThat(postInDb.getName(), is(post.getName()));
    }

    @Test
    public void whenCreateCandidate() {
        Store store = DbStore.instOf();
        Candidate candidate = new Candidate(0, "Java Developer", new City(1, "Moscow"));
        store.save(candidate);
        Candidate candidateInDb = store.findCandidateById(candidate.getId());
        assertThat(candidateInDb.getName(), is(candidate.getName()));
    }

    @Test
    public void whenUpdatePost() {
        Store store = DbStore.instOf();
        Post post = new Post(0, "Java Job", "Job description");
        store.save(post);
        store.save(new Post(post.getId(), "Junior Java Job", "Job description"));
        Post postInDb = store.findPostById(post.getId());
        assertThat(postInDb.getName(), is("Junior Java Job"));
    }

    @Test
    public void whenUpdateCandidate() {
        Store store = DbStore.instOf();
        Candidate candidate = new Candidate(0, "Java Developer", new City(1, "Moscow"));
        store.save(candidate);
        store.save(new Candidate(candidate.getId(), "Junior Java Developer", new City(1, "Moscow")));
        Candidate candidateInDb = store.findCandidateById(candidate.getId());
        assertThat(candidateInDb.getName(), is("Junior Java Developer"));
    }

    @Test
    public void whenFindAllPosts() {
        Store store = DbStore.instOf();
        store.save(new Post(0, "Junior Java Job", "Job description"));
        store.save(new Post(0, "Middle Java Job", "Job description"));
        store.save(new Post(0, "Senior Java Job", "Job description"));
        int postsSizeInDb = store.findAllPosts().size();
        assertThat(postsSizeInDb, is(3));
    }

    @Test
    public void whenFindAllCandidates() {
        Store store = DbStore.instOf();
        store.save(new Candidate(0, "Junior Java Developer", new City(1, "Moscow")));
        store.save(new Candidate(0, "Middle Java Developer", new City(1, "Moscow")));
        store.save(new Candidate(0, "Senior Java Developer", new City(1, "Moscow")));
        int candidatesSizeInDb = store.findAllCandidates().size();
        assertThat(candidatesSizeInDb, is(3));
    }

    @Test
    public void whenDeleteCandidate() {
        Store store = DbStore.instOf();
        Candidate candidate = new Candidate(0, "Java Developer", new City(1, "Moscow"));
        store.save(candidate);
        store.deleteCandidate(candidate.getId());
        int candidatesSizeInDb = store.findAllCandidates().size();
        assertThat(candidatesSizeInDb, is(0));
    }
}