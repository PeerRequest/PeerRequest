package com.peerrequest.app.api;

import com.peerrequest.app.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
abstract class ServiceBasedController {
    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected EntryService entryService;

    @Autowired
    protected ReviewService reviewService;

    @Autowired
    protected DirectRequestService directRequestService;

    @Autowired
    protected DirectRequestProcessService directRequestProcessService;

    @Autowired
    protected DocumentService documentService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected NotificationService notificationService;
}
