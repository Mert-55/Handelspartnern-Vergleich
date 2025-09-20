package org.iu.handelspartnern.spring.controller;

import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.iu.handelspartnern.common.dto.AddTradingPartnerDto;
import org.iu.handelspartnern.spring.service.TradingPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TradingPartnerController {

    @Autowired
    private TradingPartnerService tradingPartnerService;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) PartnerType type,
            @RequestParam(required = false) PartnerStatus status, @RequestParam(required = false) String search) {

        model.addAttribute("partners", tradingPartnerService.getAllPartners(type, status, search));
        model.addAttribute("partnerTypes", PartnerType.values());
        model.addAttribute("partnerStatuses", PartnerStatus.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchQuery", search);

        return "index";
    }

    @GetMapping("/partners/{id}")
    @ResponseBody
    public Object getPartner(@PathVariable Long id) {
        return tradingPartnerService.getPartnerById(id).orElseThrow(() -> new RuntimeException("Partner not found"));
    }

    @PostMapping("/partners")
    public String createPartner(@ModelAttribute AddTradingPartnerDto dto) {
        tradingPartnerService.createPartner(dto);
        return "redirect:/";
    }

    @DeleteMapping("/partners/{id}")
    @ResponseBody
    public String deletePartner(@PathVariable Long id) {
        tradingPartnerService.deletePartner(id);
        return "{\"status\": \"deleted\"}";
    }
}