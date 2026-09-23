package com.careerpath.career.api;

import com.careerpath.career.domain.CareerPathRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/career-paths")
public class CareerPathController {
    private final CareerPathRepository repository;
    public CareerPathController(CareerPathRepository repository) { this.repository = repository; }

    @GetMapping
    List<CareerPathResponse> all() {
        return repository.findAll().stream().map(p -> new CareerPathResponse(p.getId(), p.getName(), p.getDescription())).toList();
    }

    @GetMapping("/{id}")
    CareerPathResponse one(@PathVariable UUID id) {
        return repository.findById(id).map(p -> new CareerPathResponse(p.getId(), p.getName(), p.getDescription()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public record CareerPathResponse(UUID id, String name, String description) {}
}
