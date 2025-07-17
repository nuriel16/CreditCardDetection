package creditcarddetector;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SampleSimpleApplication implements CommandLineRunner {

    @Autowired
    private HelloWorldService helloWorldService;

    public static void main(String[] args) {
        SpringApplication.run(SampleSimpleApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println(helloWorldService.getHelloMessage());
    }
}
