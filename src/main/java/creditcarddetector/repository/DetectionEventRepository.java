package creditcarddetector.repository;

import creditcarddetector.model.DetectionEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetectionEventRepository extends MongoRepository<DetectionEvent, String> {
    List<DetectionEvent> findByTimestampBetween(long from, long to);
}
