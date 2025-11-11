package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.entity.Tag;
import POSE_Project_Tracking.Blog.repository.TagRepository;
import POSE_Project_Tracking.Blog.service.ITagService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
public class TagServiceImpl implements ITagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag createTag(Tag tag) {
        if (tagRepository.findByName(tag.getName())
                         .isPresent()) {
            throw new IllegalArgumentException("Tag with name " + tag.getName() + " already exists");
        }
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public Tag updateTag(Long id, Tag tagDetails) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + id));

        tag.setName(tagDetails.getName());
        tag.setDescription(tagDetails.getDescription());

        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}
