package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.ColorDTO;
import com.clothingstore.clothing_store_api.dto.SizeDTO;
import com.clothingstore.clothing_store_api.entity.Color;
import com.clothingstore.clothing_store_api.entity.Size;
import com.clothingstore.clothing_store_api.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    private SizeDTO convertToDTO(Size size) {
        SizeDTO sizeDTO = new SizeDTO();
        sizeDTO.setId(size.getId());
        sizeDTO.setSize(size.getSize());
        return sizeDTO;
    }

    private Size convertToEntity(SizeDTO sizeDTO) {
        Size size = new Size();
        size.setId(sizeDTO.getId());
        size.setSize(sizeDTO.getSize());
        return size;
    }

    public SizeDTO createSize(SizeDTO sizeDTO) {
        Size size = convertToEntity(sizeDTO);
        Size savedSize = sizeRepository.save(size);
        return convertToDTO(savedSize);
    }

    public List<SizeDTO> getAllSizes() {
        return sizeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<SizeDTO> getSizeById(Long id) {
        return sizeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public SizeDTO updateSize(Long id, SizeDTO sizeDTO) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        size.setSize(sizeDTO.getSize());
        Size updatedSize= sizeRepository.save(size);
        return convertToDTO(updatedSize);
    }

    public void deleteSize(Long id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        sizeRepository.delete(size);
    }
}
