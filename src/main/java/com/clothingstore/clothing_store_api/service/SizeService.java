package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.SizeDTO;
import com.clothingstore.clothing_store_api.entity.Size;
import com.clothingstore.clothing_store_api.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = "sizes", allEntries = true)
    public SizeDTO createSize(SizeDTO sizeDTO) {
        Size size = convertToEntity(sizeDTO);
        Size savedSize = sizeRepository.save(size);
        return convertToDTO(savedSize);
    }

    @Cacheable(value = "sizes", key = "'all'")
    public List<SizeDTO> getAllSizes() {
        return sizeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "sizes", key = "#id")
    public Optional<SizeDTO> getSizeById(Long id) {
        return sizeRepository.findById(id)
                .map(this::convertToDTO);
    }

    @CachePut(value = "sizes", key = "#id")
    public SizeDTO updateSize(Long id, SizeDTO sizeDTO) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        size.setSize(sizeDTO.getSize());
        Size updatedSize = sizeRepository.save(size);
        return convertToDTO(updatedSize);
    }

    @CacheEvict(value = "sizes", allEntries = true)
    public void deleteSize(Long id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        sizeRepository.delete(size);
    }
}
