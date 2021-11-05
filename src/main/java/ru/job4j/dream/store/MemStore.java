package ru.job4j.dream.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStore implements Store {
    private static final Store INST = new MemStore();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private static AtomicInteger postId = new AtomicInteger(4);
    private static AtomicInteger candidateId = new AtomicInteger(4);
    private final Properties cfg = new Properties();
    private static final Logger LOG = LoggerFactory.getLogger(MemStore.class.getName());

    private MemStore() {
        try (InputStream in = MemStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            LOG.error("Exception in MemStore constructor", e);
        }
        posts.put(1, new Post(1, "Junior Java Job", "Description for Junior Java Job"));
        posts.put(2, new Post(2, "Middle Java Job", "Description for Middle Java Job"));
        posts.put(3, new Post(3, "Senior Java Job", "Description for Senior Java Job"));
        candidates.put(1, new Candidate(1, "Junior Java"));
        candidates.put(2, new Candidate(2, "Middle Java"));
        candidates.put(3, new Candidate(3, "Senior Java"));
    }

    public static Store instOf() {
        return INST;
    }

    public Properties getConfig() {
        return cfg;
    }

    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    public void save(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.incrementAndGet());
        }
        posts.put(post.getId(), post);
    }

    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate.setId(candidateId.incrementAndGet());
        }
        candidates.put(candidate.getId(), candidate);
    }

    public Post findPostById(int id) {
        return posts.get(id);
    }

    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }

    public void deleteCandidate(int id) {
        Candidate candidate = findCandidateById(id);
        if (candidate != null) {
            candidates.remove(id);
        }
    }

    @Override
    public void clearTable(String tableName) {
    }
}
