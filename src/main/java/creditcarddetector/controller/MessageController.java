package creditcarddetector.controller;

import creditcarddetector.model.DetectionEvent;
import creditcarddetector.model.MessageDTO;
import creditcarddetector.repository.DetectionEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MessageController {

    @Autowired
    private DetectionEventRepository repository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping("/message")
    public ResponseEntity<?> receiveMessage(@RequestBody MessageDTO message) {
        amqpTemplate.convertAndSend("creditcard.exchange", "creditcard.routingkey", message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/detections")
    public List<Map<String, Object>> getDetections(
            @RequestParam long timeFrom,
            @RequestParam long timeTo) throws Exception {
        if (timeTo < timeFrom) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "timeFrom can't be after timeTo.");
        }
        List<DetectionEvent> events = repository.findByTimestampBetween(timeFrom, timeTo);

        Map<String, Long> stats = events.stream()
                .collect(Collectors.groupingBy(DetectionEvent::getSender, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        stats.forEach((sender, count) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("sender", sender);
            entry.put("detectionsCount", count);
            result.add(entry);
        });
        return result;
    }
}
