package com.careerpath.opportunity.service;

import java.util.*;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class JobSkillAnalyzer {
    private final Map<String, List<String>> skills = new LinkedHashMap<>();
    public JobSkillAnalyzer() {
        skills.put("Java", List.of("java")); skills.put("Spring Boot", List.of("spring boot")); skills.put("SQL", List.of("sql")); skills.put("PostgreSQL", List.of("postgresql")); skills.put("Oracle", List.of("oracle"));
        skills.put("JPA", List.of("jpa")); skills.put("Hibernate", List.of("hibernate")); skills.put("REST APIs", List.of("rest", "restful", "rest api")); skills.put("SOAP", List.of("soap")); skills.put("Git", List.of("git", "github")); skills.put("SVN", List.of("svn"));
        skills.put("Python", List.of("python")); skills.put("Machine Learning", List.of("machine learning", "ml algorithms")); skills.put("Deep Learning", List.of("deep learning")); skills.put("TensorFlow", List.of("tensorflow")); skills.put("PyTorch", List.of("pytorch"));
        skills.put("CI/CD", List.of("ci/cd", "continuous integration", "continuous delivery")); skills.put("AWS", List.of("aws", "amazon web services")); skills.put("Kubernetes", List.of("kubernetes", "k8s")); skills.put("Docker", List.of("docker", "containerized"));
        skills.put("JavaScript", List.of("javascript")); skills.put("TypeScript", List.of("typescript")); skills.put("Angular", List.of("angular")); skills.put("React", List.of("react")); skills.put("Linux", List.of("linux"));
    }
    public List<String> extract(String text) { List<String> found=skills.entrySet().stream().filter(entry->entry.getValue().stream().anyMatch(term->hasTerm(text,term))).map(Map.Entry::getKey).toList(); return found.isEmpty()?Arrays.stream((text==null?"":text).split(",")).map(String::trim).filter(value->!value.isBlank()).distinct().toList():found; }
    public boolean matches(String evidence, String skill) { return skills.getOrDefault(skill,List.of(skill)).stream().anyMatch(term->hasTerm(evidence,term)); }
    private boolean hasTerm(String text,String term) { return Pattern.compile("(?i)(?<![a-z0-9])"+Pattern.quote(term)+"(?![a-z0-9])").matcher(text==null?"":text).find(); }
}
