package ru.job4j.dream.store;

import org.junit.Before;
import org.junit.Test;
import ru.job4j.dream.model.Candidate;
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
        Post post = new Post(0, "Java Job");
        store.save(post);
        Post postInDb = store.findPostById(post.getId());
        assertThat(postInDb.getName(), is(post.getName()));
    }

    @Test
    public void whenCreateCandidate() {
        Store store = DbStore.instOf();
        Candidate candidate = new Candidate(0, "Java Developer");
        store.save(candidate);
        Candidate candidateInDb = store.findCandidateById(candidate.getId());
        assertThat(candidateInDb.getName(), is(candidate.getName()));
    }

    @Test
    public void whenUpdatePost() {
        Store store = DbStore.instOf();
        Post post = new Post(0, "Java Job");
        store.save(post);
        store.save(new Post(post.getId(), "Junior Java Job"));
        Post postInDb = store.findPostById(post.getId());
        assertThat(postInDb.getName(), is("Junior Java Job"));
    }

    @Test
    public void whenUpdateCandidate() {
        Store store = DbStore.instOf();
        Candidate candidate = new Candidate(0, "Java Developer");
        store.save(candidate);
        store.save(new Candidate(candidate.getId(), "Junior Java Developer"));
        Candidate candidateInDb = store.findCandidateById(candidate.getId());
        assertThat(candidateInDb.getName(), is("Junior Java Developer"));
    }

    @Test
    public void whenFindAllPosts() {
        Store store = DbStore.instOf();
        store.save(new Post(0, "Junior Java Job"));
        store.save(new Post(0, "Middle Java Job"));
        store.save(new Post(0, "Senior Java Job"));
        int postsSizeInDb = store.findAllPosts().size();
        assertThat(postsSizeInDb, is(3));
    }

    @Test
    public void whenFindAllCandidates() {
        Store store = DbStore.instOf();
        store.save(new Candidate(0, "Junior Java Developer"));
        store.save(new Candidate(0, "Middle Java Developer"));
        store.save(new Candidate(0, "Senior Java Developer"));
        int candidatesSizeInDb = store.findAllCandidates().size();
        assertThat(candidatesSizeInDb, is(3));
    }

    @Test
    public void whenDeleteCandidate() {
        Store store = DbStore.instOf();
        Candidate candidate = new Candidate(0, "Java Developer");
        store.save(candidate);
        store.deleteCandidate(candidate.getId());
        int candidatesSizeInDb = store.findAllCandidates().size();
        assertThat(candidatesSizeInDb, is(0));
    }
}