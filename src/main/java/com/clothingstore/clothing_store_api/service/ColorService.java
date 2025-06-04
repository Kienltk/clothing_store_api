package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.ColorDTO;
import com.clothingstore.clothing_store_api.entity.Color;
import com.clothingstore.clothing_store_api.repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ColorService {
    @Autowired
    private ColorRepository colorRepository;

    private ColorDTO convertToDTO(Color color) {
        ColorDTO colorDTO = new ColorDTO();
        colorDTO.setId(color.getId());
        colorDTO.setColor(color.getColor());
        return colorDTO;
    }

    private Color convertToEntity(ColorDTO colorDTO) {
        Color color = new Color();
        color.setId(colorDTO.getId());
        color.setColor(colorDTO.getColor());
        return color;
    }

    public ColorDTO createColor(ColorDTO colorDTO) {
        Color color = convertToEntity(colorDTO);
        Color savedColor = colorRepository.save(color);
        return convertToDTO(savedColor);
    }

    public List<ColorDTO> getAllColors() {
        return colorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ColorDTO> getColorById(Long id) {
        return colorRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ColorDTO updateColor(Long id, ColorDTO colorDTO) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with id: " + id));
        color.setColor(colorDTO.getColor());
        Color updatedColor = colorRepository.save(color);
        return convertToDTO(updatedColor);
    }

    public boolean deleteColor(Long id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with id: " + id));
        colorRepository.delete(color);
        return false;
    }
}
