package com.peerrequest.app.api;

import com.peerrequest.app.services.CategoryService;
import com.peerrequest.app.services.EntryService;
import com.peerrequest.app.services.ReviewService;
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
}
