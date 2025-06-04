package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.SizeDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.SizeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/sizes")
@PreAuthorize("hasRole('ADMIN')")
public class SizeController {
    @Autowired
    private SizeService sizeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseObject<SizeDTO>> createSize(@Valid @RequestBody SizeDTO sizeDTO) {
        SizeDTO createdSize = sizeService.createSize(sizeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject<>(201, "Size created successfully", createdSize)
        );
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<SizeDTO>>> getAllSizes() {
        List<SizeDTO> sizes = sizeService.getAllSizes();
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Get all sizes successfully", sizes)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<SizeDTO>> getSizeById(@PathVariable Long id) {
        Optional<SizeDTO> sizeDTO = sizeService.getSizeById(id);
        return sizeDTO.map(size -> ResponseEntity.ok(
                        new ResponseObject<>(200, "Size found", size)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject<>(404, "Size not found", null)
                ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<SizeDTO>> updateSize(@PathVariable Long id,@Valid @RequestBody SizeDTO sizeDTO) {
        SizeDTO updatedSize = sizeService.updateSize(id, sizeDTO);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Size updated successfully", updatedSize)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Size deleted successfully", null)
        );
    }
}
