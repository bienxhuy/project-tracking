package OOSE_Final_Project.Blog.service;

import OOSE_Final_Project.Blog.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface ITagService {

    Tag createTag(Tag tag);

    List<Tag> getAllTags();

    Optional<Tag> getTagById(Long id);

    Optional<Tag> getTagByName(String name);

    Tag updateTag(Long id, Tag tagDetails);

    void deleteTag(Long id);

}
