package com.teamsix.firstteamproject.community.controller;

import com.teamsix.firstteamproject.community.dto.PostDTO;
import com.teamsix.firstteamproject.community.dto.SimplePostDTO;
import com.teamsix.firstteamproject.community.service.PostService;
import com.teamsix.firstteamproject.global.dto.ResultDTO;
import com.teamsix.firstteamproject.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/board")
@RestController
public class PostController {

    public PostController(PostService postService) {
        this.postService = postService;
    }

    private final PostService postService;

    // post 저장
    // 게시글과 사용자 정보를 같이 전송할 수 있는지 프론트엔드에게 질문 or post에 userId포함되게 하기
    @Operation(summary = "커뮤니티 글 저장", description = "요청한 글을 저장한다.")
    @PostMapping("/write")
    public ResultDTO<PostDTO> getSaveRequest(
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @RequestPart(value = "post") PostDTO postDTO
    ){
        return ApiUtils.ok(postService.savePost(postDTO, images));
    }


    //post 페이지 조회(리스트)
    @Operation(summary = "커뮤니티 글 페이지 조회", description = "post들에 대한 페이지를 반환한다.")
    @GetMapping("/lists")
    public ResultDTO<Page<SimplePostDTO>> getSimplePostDTOSRequest(
            Pageable pageable, @RequestParam Long category
    ){
        return ApiUtils.ok(postService.getSimplePostDTOS(pageable, category));
    }

    //post 세부 조회
    @Operation(summary = "커뮤니티 글 세부 조회", description = "특정 id의 post에 대한 세부정보를 반환한다.")
    @GetMapping("/lists/{postId}")
    public ResultDTO<PostDTO> getPostDTORequest(
            @PathVariable Long postId
    ){
        return ApiUtils.ok(postService.getPostDTO(postId));
    }

    //post 삭제
    @Operation(summary = "커뮤니티 글 삭제", description = "특정 id의 post를 삭제한다.")
    @DeleteMapping ("/lists/{postId}")
    public ResultDTO<String> getDeletePostRequest(
            @PathVariable Long postId
    ){
        postService.deletePost(postId);
        return ApiUtils.ok("해당 "+ postId +"의 post가 삭제되었습니다.");
    }

    //post 수정
    @Operation(summary = "커뮤니티 글 수정", description = "특정 id의 post를 수정한다.")
    @PostMapping ("/lists/{postId}")
    public ResultDTO<PostDTO> getUpdatingPostRequest(
            @PathVariable Long postId,
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @RequestPart(value = "post") PostDTO postDTO
    ){
        return ApiUtils.ok(postService.updatePost(images, postDTO));
    }

    // post 좋아요 클릭 (후에 수정)
    @Operation(summary = "커뮤니티 글 좋아요", description = "특정 id의 post 좋아요 수를 올린다. ")
    @GetMapping("/lists/{postId}/likes")
    public ResultDTO<PostDTO> getLikesRequest(
            @PathVariable Long postId,
            @RequestParam Long userId
    ){
        return ApiUtils.ok(postService.addingLikesToPost(postId, userId));
    }

    //해당 카테고리에 해당하는 Post들을 조회한다.


    //포스트에 대한 검색 기능




}
