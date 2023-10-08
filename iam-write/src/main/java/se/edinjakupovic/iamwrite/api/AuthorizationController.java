package se.edinjakupovic.iamwrite.api;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.edinjakupovic.iamwrite.service.AuthorizationService;
import se.edinjakupovic.iamwrite.service.requests.Authorize;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    private final AuthorizationService service;

    public AuthorizationController(AuthorizationService service) {
        this.service = service;
    }

    @PostMapping
    boolean isAuthorized(@RequestBody Authorize authorize) {
        return service.isAuthorized(authorize.subjectId(), authorize.resourceId());
    }
}
