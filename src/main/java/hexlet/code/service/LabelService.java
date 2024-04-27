package hexlet.code.service;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {

    private LabelMapper labelMapper;

    private LabelRepository labelRepository;

    public List<LabelDTO> getAll() {
        var label = labelRepository.findAll();
        return label.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with id " + id + " not found")));
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelCreate) {
        var label = labelMapper.map(labelCreate);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with id " + id + " not found")));
        labelMapper.update(labelUpdateDTO, label);
        labelRepository.save(label);
        var labelDto = labelMapper.map(label);
        return labelDto;
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
