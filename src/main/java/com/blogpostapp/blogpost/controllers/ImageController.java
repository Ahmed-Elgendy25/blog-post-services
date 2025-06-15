// package com.blogpostapp.blogpost.controllers;

// import com.blogpostapp.blogpost.services.SupabaseService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/images")
// public class ImageController {

//     private final SupabaseService supabaseService;

//     @Autowired
//     public ImageController(SupabaseService supabaseService) {
//         this.supabaseService = supabaseService;
//     }

//     @PostMapping("/upload")
//     @PreAuthorize("hasAuthority('author')")
//     public ResponseEntity<?> uploadImage(
//             @RequestParam("file") MultipartFile file,
//             @RequestHeader("Authorization") String authHeader) {
//         try {
//             if (file.isEmpty()) {
//                 return ResponseEntity.badRequest().body("Please select a file to upload");
//             }
            
//             // Extract JWT token from Authorization header
//             String jwtToken = authHeader;
//             if (authHeader.startsWith("Bearer ")) {
//                 jwtToken = authHeader.substring(7);
//             }

//             String imageUrl = supabaseService.uploadImage(file, jwtToken);
            
//             Map<String, String> response = new HashMap<>();
//             response.put("imageUrl", imageUrl);
            
//             return ResponseEntity.status(HttpStatus.CREATED).body(response);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body("Error uploading image: " + e.getMessage());
//         }
//     }
// }
