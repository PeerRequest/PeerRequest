package com.peerrequest.app.api;

import com.peerrequest.app.data.Paged;
import com.peerrequest.app.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing endpoints to retrieve user information.
 */
@RestController
@ApiControllerPrefix
public class UserController extends ServiceBasedController {

    private final int maxPageSize = 100;

    @GetMapping("/users")
    Paged<List<User>> list(@RequestParam Optional<Integer> limit,
                           @RequestParam Optional<Integer> page) {
        int pageSize = limit.orElse(maxPageSize);
        pageSize = Math.min(pageSize, maxPageSize);
        pageSize = Math.max(0, pageSize);

        var responsePage = userService.list(
            Pageable.ofSize(pageSize)
                .withPage(page.orElse(1) - 1)
        );
        return new Paged<>(
            responsePage.getSize(),
            responsePage.getNumber() + 1,
            responsePage.getTotalPages(),
            responsePage.toList()
        );
    }

    @GetMapping("/users/{id}")
    Optional<User> getById(@PathVariable String id) {
        return userService.get(id);
    }
}
