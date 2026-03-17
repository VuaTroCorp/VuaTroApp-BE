package fpt.ntu.vuatrovn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile/{user}")
@Tag(name = "Profile API", description = "Manage user accounts and posts.")
@RequiredArgsConstructor
public class ProfileController {
    
}
