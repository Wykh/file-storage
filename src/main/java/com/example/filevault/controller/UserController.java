package com.example.filevault.controller;

import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.UserDto;
import com.example.filevault.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Tag(name = "User controller")
    @Operation(summary = "Register user")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class)
            )
    )
    @PostMapping
    public ResponseEntity<UserDto> registerOne(@RequestParam String username,
                                               @RequestParam String password) {

        return ResponseEntity.ok(userService.registerOne(username, password));
    }

    @Tag(name = "User controller")
    @Operation(summary = "Update user")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class)
            )
    )
    @PutMapping("/{username}")
    ResponseEntity<UserDto> updateOne(@PathVariable String username,
                                      @RequestParam(required = false) String role,
                                      @RequestParam(required = false) Boolean isBlocked) {
        return ResponseEntity.ok(userService.updateOne(username, role, isBlocked));
    }

}
