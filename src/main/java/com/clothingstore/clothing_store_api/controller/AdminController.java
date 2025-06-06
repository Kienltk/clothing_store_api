package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.DashboardDTO;
import com.clothingstore.clothing_store_api.dto.InfoUserDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ResponseObject<DashboardDTO>> getDashboardData() {
        DashboardDTO dashboardData = adminService.getDashboardData();
        ResponseObject<DashboardDTO> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Get Info User success",
                dashboardData
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
