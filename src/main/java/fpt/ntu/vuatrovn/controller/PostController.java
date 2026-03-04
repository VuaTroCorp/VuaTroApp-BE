package fpt.ntu.vuatrovn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fpt.ntu.vuatrovn.service.SupabaseStorageService;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("api/test")
public class PostController {
    private final SupabaseStorageService storageService;

    public PostController(SupabaseStorageService storageService){
        this.storageService = storageService;
    }

    @GetMapping("/test-auth")
    public String testAuth(Authentication authentication) {
        return "Current user: " + authentication.getName();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        return storageService.uploadFile(file);
    }
    
}
