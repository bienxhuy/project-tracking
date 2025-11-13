package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.entity.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    Category createCategory(Category category);

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Long id);

    Optional<Category> getCategoryByName(String name);

    Category updateCategory(Long id, Category categoryDetails);

    void deleteCategory(Long id);

}
