package OOSE_Final_Project.Blog.controller;

import OOSE_Final_Project.Blog.dto.res.ApiResponse;
import OOSE_Final_Project.Blog.entity.Category;
import OOSE_Final_Project.Blog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {


    @Autowired
    ICategoryService categoryService;

    @GetMapping("")
    public ApiResponse<List<Category>> getAllCategories() {
        var categories = categoryService.getAllCategories();
        return new ApiResponse<>(HttpStatus.OK, "Get all category", categories, null);
    }

    @PostMapping("")
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category result = categoryService.createCategory(category);
        return new ApiResponse<>(HttpStatus.OK, "Create category", result, null);
    }

    @PutMapping("/{catId}")
    public ApiResponse<Category> updateCategory(@PathVariable Long catId, @RequestBody Category category) {
        Category result = categoryService.updateCategory(catId, category);
        return new ApiResponse<>(HttpStatus.OK, "Update category", result, null);
    }

    @DeleteMapping("{catId}")
    public ApiResponse<Boolean> deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
        return new ApiResponse<>(HttpStatus.OK, "Delete category", Boolean.TRUE, null);
    }

}
