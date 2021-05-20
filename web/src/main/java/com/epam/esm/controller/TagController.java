package com.epam.esm.controller;

import com.epam.esm.hateoas.TagResource;
import com.epam.esm.model.GiftTag;
import com.epam.esm.service.GiftTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Rest controller for Tags
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/tags", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TagController {

    private final GiftTagService tagService;
    private final TagResource tagResource;

    @Autowired
    public TagController(GiftTagService tagService,
                         TagResource tagResource) {
        this.tagService = tagService;
        this.tagResource = tagResource;
    }

    /**
     * Create tag
     *
     * @param tag the GiftTag
     * @return the tag id
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftTag> create(@Valid @RequestBody GiftTag tag) {
        return ResponseEntity.ok(tagService.create(tag));
    }

    /**
     * Delete tag
     *
     * @param id the GiftTag id
     * @return the response entity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @Min(value = 1) Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tag by id
     *
     * @param id the GiftTag id
     * @return the tag
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<EntityModel<GiftTag>> getTagById(@PathVariable @Min(value = 1) Long id) {
        return ResponseEntity.ok(tagResource.toModel(tagService.findById(id)));
    }

    /**
     * Get all tags
     *
     * @param pageable the pagination
     * @return List of GiftTags
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<CollectionModel<EntityModel<GiftTag>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(
                tagResource.toCollectionModel(
                        tagService.findAll(pageable)));
    }
}
