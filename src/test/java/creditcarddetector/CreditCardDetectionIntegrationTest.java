package creditcarddetector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class CreditCardDetectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitListenerEndpointRegistry rabbitRegistry;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void configureMongo(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        System.out.println("clean after test.");
        rabbitRegistry.stop();
        ((CachingConnectionFactory) rabbitTemplate.getConnectionFactory()).destroy();
    }

    void printDbInfo() {
        String host = mongoDBContainer.getHost();
        int port = mongoDBContainer.getMappedPort(27017);
        System.out.println("Mongo is at: " + host + ":" + port);
        System.out.println("Mongo db name: " + mongoTemplate.getDb().getName());
    }

    @Test
    void testDetectionAndAggregation() throws Exception {
//        printDbInfo();// just for debugging
        String messageJson = """
        {
          "id": "msg1",
          "sender": "sender@coro.net",
          "recipients": ["rec1@coro.net"],
          "subject": "Test 4111111111111111",
          "body": "Card: 5425-2334-3010-9903 and 4917484589897107",
          "sentTime": 1642426114024
        }
        """;

        // post message
        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageJson))
                .andExpect(status().isOk());
        Thread.sleep(2000); // let rabbit some time to save the message.
        // get detections
        long detectionTime = 1642426114024l;
        RequestBuilder builder = MockMvcRequestBuilders.get("/detections")
                .param("timeFrom", String.valueOf(detectionTime - 100000))
                .param("timeTo", String.valueOf(detectionTime + 100000));
        ResultActions result = mockMvc.perform(builder);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sender").value("sender@coro.net"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].detectionsCount").value(1));
    }

    @Test
    void testDetectionAggregationEmptyResult() throws Exception {
        long timeFrom = 1000000000000L;
        long timeTo = 1000000005000L;
        mockMvc.perform(MockMvcRequestBuilders.get("/detections")
                .param("timeFrom", String.valueOf(timeFrom))
                .param("timeTo", String.valueOf(timeTo)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void testDetectionsInvalidTimeRange() throws Exception {
        // timeFrom > timeTo => Bad Request
        long timeFrom = 2000L;
        long timeTo = 1000L;
        mockMvc.perform(MockMvcRequestBuilders.get("/detections")
                .param("timeFrom", String.valueOf(timeFrom))
                .param("timeTo", String.valueOf(timeTo)))
                .andExpect(status().isBadRequest());
    }
}
