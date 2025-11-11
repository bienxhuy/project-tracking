package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.entity.Tag;

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
