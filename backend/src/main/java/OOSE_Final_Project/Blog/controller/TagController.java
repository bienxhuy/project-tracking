package OOSE_Final_Project.Blog.controller;

import OOSE_Final_Project.Blog.dto.res.ApiResponse;
import OOSE_Final_Project.Blog.entity.Category;
import OOSE_Final_Project.Blog.entity.Tag;
import OOSE_Final_Project.Blog.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/tags")
@RestController
public class TagController {


    @Autowired
    ITagService tagService;

    @GetMapping("")
    public ApiResponse<List<Tag>> getAllCategories() {
        var categories = tagService.getAllTags();
        return new ApiResponse<>(HttpStatus.OK, "Get all tag", categories, null);
    }

    @PostMapping("")
    public ApiResponse<Tag> createCategory(@RequestBody Tag tag) {
        Tag result = tagService.createTag(tag);
        return new ApiResponse<>(HttpStatus.OK, "Create Tag", result, null);
    }

    @PutMapping("/{tagId}")
    public ApiResponse<Tag> updateTag(@PathVariable Long tagId, @RequestBody Tag tag) {
        Tag result = tagService.updateTag(tagId, tag);
        return new ApiResponse<>(HttpStatus.OK, "Update tag", result, null);
    }

    @DeleteMapping("{tagId}")
    public ApiResponse<Category> deleteCategory(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return new ApiResponse<>(HttpStatus.OK, "Delete tag", null, null);
    }
}
