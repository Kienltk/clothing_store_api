package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.DashboardDTO;
import com.clothingstore.clothing_store_api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> getDashboardData() {
        DashboardDTO dashboardData = adminService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}
