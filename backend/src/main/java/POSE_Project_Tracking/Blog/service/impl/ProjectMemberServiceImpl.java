package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.ProjectMemberReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectMemberRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ProjectMemberMapper;
import POSE_Project_Tracking.Blog.repository.ProjectMemberRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IProjectMemberService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProjectMemberServiceImpl implements IProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public ProjectMemberRes addMember(ProjectMemberReq projectMemberReq) {
        // Lấy project
        Project project = projectRepository.findById(projectMemberReq.getProjectId())
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Lấy user
        User user = userRepository.findById(projectMemberReq.getUserId())
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        // Check if member already exists
        if (projectMemberRepository.findByProjectAndUser(project, user).isPresent()) {
            throw new CustomException(MEMBER_ALREADY_EXISTS);
        }

        // Map request to entity
        ProjectMember member = projectMemberMapper.toEntity(projectMemberReq, project, user);

        // Save
        member = projectMemberRepository.save(member);

        return projectMemberMapper.toResponse(member);
    }

    @Override
    public ProjectMemberRes updateMemberRole(Long id, EUserRole role) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        member.setRole(role);
        member = projectMemberRepository.save(member);

        return projectMemberMapper.toResponse(member);
    }

    @Override
    public ProjectMemberRes getMemberById(Long id) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        return projectMemberMapper.toResponse(member);
    }

    @Override
    public List<ProjectMemberRes> getAllMembers() {
        return projectMemberRepository.findAll().stream()
                .map(projectMemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberRes> getMembersByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return projectMemberRepository.findByProject(project).stream()
                .map(projectMemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberRes> getMembersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return projectMemberRepository.findByUser(user).stream()
                .map(projectMemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberRes> getMembersByRole(EUserRole role) {
        return projectMemberRepository.findByRole(role).stream()
                .map(projectMemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeMember(Long id) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        projectMemberRepository.delete(member);
    }

    @Override
    public List<ProjectMemberRes> getProjectsByUser(Long userId) {
        // Alias for getMembersByUser
        return getMembersByUser(userId);
    }

    @Override
    public ProjectMemberRes updateMember(Long id, ProjectMemberReq projectMemberReq) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // Only update role for now
        if (projectMemberReq.getRole() != null) {
            member.setRole(projectMemberReq.getRole());
        }

        member = projectMemberRepository.save(member);
        return projectMemberMapper.toResponse(member);
    }

    @Override
    public void deactivateMember(Long id) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        member.setIsActive(false);
        projectMemberRepository.save(member);
    }

    @Override
    public void activateMember(Long id) {
        ProjectMember member = projectMemberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        member.setIsActive(true);
        projectMemberRepository.save(member);
    }
}
