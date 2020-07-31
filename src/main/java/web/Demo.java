package web;
import org.springframework.web.bind.annotation.*;
@RestController
class Service {
    @GetMapping("/test")
    String showTest() {
        return "Welcome to Spring Boot";
    }
}
