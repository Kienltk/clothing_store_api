package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.DashboardDTO;
import com.clothingstore.clothing_store_api.dto.InfoUserDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @PostMapping("/upload")
    public ResponseEntity<ResponseObject<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        ResponseObject<String> response;
        try {
            adminService.uploadFiles(files);
            response = new ResponseObject<>(
                    HttpStatus.OK.value(),
                    "Files uploaded successfully",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response = new ResponseObject<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            response = new ResponseObject<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to upload files: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
