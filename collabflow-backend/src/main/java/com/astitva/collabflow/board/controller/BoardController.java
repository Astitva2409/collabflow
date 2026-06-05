package com.astitva.collabflow.board.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.board.dto.BoardResponse;
import com.astitva.collabflow.board.service.BoardService;
import com.astitva.collabflow.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/**
 * REST controller for board APIs.
 */
@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * Fetches board and its columns for a project.
     */
    @GetMapping
    public ApiResponse<BoardResponse> getBoard(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        BoardResponse response = boardService.getBoard(
                workspaceId,
                projectId,
                currentUser.getId()
        );

        return ApiResponse.success("Board fetched successfully", response);
    }
}