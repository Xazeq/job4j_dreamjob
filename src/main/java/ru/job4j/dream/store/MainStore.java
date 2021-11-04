package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

public class MainStore {
    public static void main(String[] args) {
        Store store = DbStore.instOf();
        store.save(new Post(0, "Junior Java Job"));
        store.save(new Post(0, "Middle Java Job"));
        store.save(new Post(0, "Senior Java Job"));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }
        store.save(new Post(2, "Middle+ Java Job"));
        System.out.println(store.findPostById(2).getName());
        System.out.println("---------------------------");
        store.save(new Candidate(0, "Junior Java Developer"));
        store.save(new Candidate(0, "Middle Java Developer"));
        store.save(new Candidate(0, "Senior Java Developer"));
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }
        store.save(new Candidate(1, "Junior+ Java Developer"));
        store.deleteCandidate(3);
        System.out.println("Candidates after update and delete:");
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }
    }
}
