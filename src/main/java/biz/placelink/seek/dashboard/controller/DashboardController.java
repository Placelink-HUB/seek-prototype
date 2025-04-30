package biz.placelink.seek.dashboard.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    @Value("${web.push.vapid.public}")
    private String publicKey;

    @GetMapping(value = "/public/dashboard/integrated")
    protected String integratedDashboard(@PathVariable String siteId, Model model) {
        model.addAttribute("pl_webpush_s2_key_public", publicKey);
        return "dashboard/integrated-dashboard";
    }

    @GetMapping(value = "/public/dashboard/{siteId}")
    protected String detailDashboard(@PathVariable String siteId, Model model) {
        model.addAttribute("pl_webpush_s2_key_public", publicKey);
        return "dashboard/detail-dashboard";
    }

}
