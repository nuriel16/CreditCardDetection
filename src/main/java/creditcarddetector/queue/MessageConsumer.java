package creditcarddetector.queue;

import creditcarddetector.model.MessageDTO;
import creditcarddetector.service.CreditCardDetectorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @Autowired
    private CreditCardDetectorService detectionService; // שירות שמזהה ומאכסן

    @RabbitListener(queues = "creditcard.queue")
    public void processMessage(MessageDTO message) {
        detectionService.scanAndSave(message);
//        System.out.println("creditcard.queue - message saved");
    }
}

