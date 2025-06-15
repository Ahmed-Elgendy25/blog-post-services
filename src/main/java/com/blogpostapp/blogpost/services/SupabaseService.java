// package com.blogpostapp.blogpost.services;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.util.Map;
// import java.util.UUID;

// @Service
// public class SupabaseService {

//     @Value("${supabase.url:https://wyhuqismvawczoawlkhd.supabase.co}")
//     private String supabaseUrl;

//     @Value("${supabase.anon.key}")
//     private String supabaseAnonKey;

//     @Value("${supabase.storage.bucket:post-imgs}")
//     private String storageBucket;

//     private final RestTemplate restTemplate;

//     @Autowired
//     public SupabaseService() {
//         this.restTemplate = new RestTemplate();
//     }

//     /**
//      * Uploads an image to Supabase Storage with JWT authentication
//      * @param file The image file to upload
//      * @param jwtToken The JWT token for authentication
//      * @return The URL of the uploaded image
//      * @throws IOException If there's an error reading the file
//      */
//     public String uploadImage(MultipartFile file, String jwtToken) throws IOException {
//         // Create unique file name
//         String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
//         // Prepare headers with authentication
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "Bearer " + jwtToken);
//         headers.set("apikey", supabaseAnonKey);
//         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        
//         // Create the request entity with file data
//         HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        
//         // Construct the upload URL
//         String uploadUrl = supabaseUrl + "/storage/v1/object/" + storageBucket + "/" + fileName;
        
//         // Make the request to Supabase Storage
//         ResponseEntity<Map> response = restTemplate.exchange(
//             uploadUrl, 
//             HttpMethod.POST, 
//             requestEntity, 
//             Map.class
//         );
        
//         // Return the public URL of the uploaded image
//         return supabaseUrl + "/storage/v1/object/public/" + storageBucket + "/" + fileName;
//     }
// }
