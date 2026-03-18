package fpt.ntu.vuatrovn.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.service.RentalRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rental-procedures")
@Tag(name = "RENTAL PROCEDURE API", description = "API for registering and scheduling room viewing appointments.")
@RequiredArgsConstructor
public class RentalRegistrationController {

    private final RentalRegistrationService registrationService;

    // ==========================================
    // 1. RENTAL REGISTRATION API
    // ========================================== 
    @PostMapping("/register-view/{id}")
    @Operation(summary = "Rental registration", description = "Tenant choose a post to register to view the room")
    public ResponseEntity<?> registerViewRoom(
            @PathVariable Long id,
            Authentication authentication
    ) {

        if(authentication == null){

            Map<String,Object> response = new HashMap<>();
            response.put("status",401);
            response.put("message","You are not logged in to your account. Please log in to your account.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        String email = authentication.getName();

       registrationService.registerViewRoom(id,email);

        Map<String,Object> response = new HashMap<>();
        response.put("status",200);
        response.put("message",
                "Your request has been sent. Please wait for landlord confirmation.");

        return ResponseEntity.ok(response);
    }
}
