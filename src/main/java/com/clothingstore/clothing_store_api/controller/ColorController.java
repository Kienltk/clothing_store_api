package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.ColorDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.ColorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/colors")
@PreAuthorize("hasRole('ADMIN')")
public class ColorController {
    @Autowired
    private ColorService colorService;

    @PostMapping
    public ResponseEntity<ResponseObject<ColorDTO>> createColor(
           @Valid @RequestBody ColorDTO colorDTO) {
        ColorDTO createdColor = colorService.createColor(colorDTO);
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.CREATED.value(), "Color created successfully", createdColor),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<ColorDTO>>> getAllColors() {
        List<ColorDTO> colors = colorService.getAllColors();
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Colors retrieved successfully", colors),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<ColorDTO>> getColorById(
            @PathVariable Long id) {
        Optional<ColorDTO> colorDTO = colorService.getColorById(id);
        return colorDTO.map(dto -> new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Color retrieved successfully", dto),
                HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Color not found", null),
                HttpStatus.NOT_FOUND
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<ColorDTO>> updateColor(
            @PathVariable Long id,
           @Valid @RequestBody ColorDTO colorDTO) {
        ColorDTO updatedColor = colorService.updateColor(id, colorDTO);
        if (updatedColor == null) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Color not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Color updated successfully", updatedColor),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<String>> deleteColor(
            @PathVariable Long id) {
        boolean deleted = colorService.deleteColor(id);
        if (!deleted) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Color not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Color deleted successfully", "Deleted color with id = " + id),
                HttpStatus.OK
        );
    }

}
