package hexlet.code.controller.api;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    LabelService labelService;

    @GetMapping("/labels")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public List<LabelDTO> index() {
        var label = labelRepository.findAll();
        return label.stream()
                .map(x -> labelMapper.map(x))
                .toList();
    }

    @GetMapping("/labels/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public LabelDTO show(@PathVariable Long id) {
        var labels = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        var labelDto = labelMapper.map(labels);
        return labelDto;
    }

    @PostMapping("/labels")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public LabelDTO create(@RequestBody @Valid LabelCreateDTO labelCreateDTO) {
        var label = labelMapper.map(labelCreateDTO);
        labelRepository.save(label);
        var labelDto = labelMapper.map(label);
        return labelDto;
    }

    @PutMapping("/labels/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public LabelDTO update(@PathVariable Long id, @RequestBody LabelUpdateDTO labelUpdateDTO) {
        return labelService.update(id, labelUpdateDTO);
    }

    @DeleteMapping("//labels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelRepository.deleteById(id);
    }
}
