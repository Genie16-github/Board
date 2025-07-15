package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.entity.User;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 게시글 전체 조회
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 게시글 작성
    @PostMapping
    public Post createPost(@Valid @RequestBody Post post, Principal principal) {
        String username = principal.getName(); // 현재 로그인한 사용자 이름
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        post.setUser(user);
        return postRepository.save(post);
    }


    // 게시글 단일 조회
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @Valid @RequestBody Post postRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        return postRepository.save(post);
    }


    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }
}
