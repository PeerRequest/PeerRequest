package com.peerrequest.app.api;

import com.peerrequest.app.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
abstract class ServiceBasedController {
    @Autowired
    protected CategoryService categoryService;
}
