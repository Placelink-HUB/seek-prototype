package biz.placelink.seek.dashboard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping(value = "/public/dashboard/{siteId}")
    protected String dashboard(@PathVariable String siteId) {
        return "dashboard/detail-dashboard";
    }

}
