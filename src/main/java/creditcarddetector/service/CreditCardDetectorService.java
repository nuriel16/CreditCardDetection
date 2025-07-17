/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package creditcarddetector.service;

import creditcarddetector.model.DetectionEvent;
import creditcarddetector.model.MessageDTO;
import creditcarddetector.repository.DetectionEventRepository;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CreditCardDetectorService {

    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:\\d[ -]*?){13,16}\\b");

    @Autowired
    private DetectionEventRepository repository;

    public List<String> detectCreditCards(String text) {
        Set<String> detectedCards = new HashSet<>();
        Matcher matcher = CREDIT_CARD_PATTERN.matcher(text);

        while (matcher.find()) {
            String rawCard = matcher.group();
            String normalizedCard = rawCard.replaceAll("[ -]", "");
            if (isValidCreditCard(normalizedCard)) {
                detectedCards.add(normalizedCard);
            }
        }
        List<String> result = new ArrayList<>();
        result.addAll(detectedCards);
        return result;
    }

    private boolean isValidCreditCard(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public void scanAndSave(MessageDTO message) {
        List<String> detectedCards = detectCreditCards(
                message.getSubject() + " " + message.getBody()
        );
        if (!detectedCards.isEmpty()) {
            DetectionEvent event = new DetectionEvent();
            event.setMessageId(message.getId());
            event.setSender(message.getSender());
            event.setDetectedCardNumbers(detectedCards);
            event.setTimestamp(message.getSentTime());
            repository.save(event);
        }
    }
}
