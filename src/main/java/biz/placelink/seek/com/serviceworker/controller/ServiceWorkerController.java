package biz.placelink.seek.com.serviceworker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import biz.placelink.seek.com.serviceworker.service.ServiceWorkerService;
import biz.placelink.seek.com.serviceworker.vo.SubscriptionVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Controller
public class ServiceWorkerController {

    private final ServiceWorkerService serviceWorkerService;

    public ServiceWorkerController(ServiceWorkerService serviceWorkerService) {
        this.serviceWorkerService = serviceWorkerService;
    }

    @PostMapping("/system/serviceworker/subscribe.ar") //
    public String subscribe(@RequestBody SubscriptionVO subscriptionVO, ModelMap model) {
        serviceWorkerService.subscribe(subscriptionVO);
        return "jsonView";
    }

    @PostMapping("/system/serviceworker/unsubscribe.ar") //
    public String unsubscribe(@RequestBody String endpoint, ModelMap model) {
        // serviceWorkerService.unsubscribe(SessionHelper.getUserMno());
        // model.addAttribute(Constants.RESULT_CODE, 1);
        return "jsonView";
    }

}
