package fpt.ntu.vuatrovn.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();
    
    public String uploadFile(MultipartFile file) throws IOException{
                String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        String uploadUrl = supabaseUrl + "/storage/v1/object/"
                + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> request =
                new HttpEntity<>(file.getBytes(), headers);

        restTemplate.exchange(uploadUrl, HttpMethod.POST, request, String.class);

        return supabaseUrl + "/storage/v1/object/public/"
                + bucket + "/" + fileName;
    }

    // Delete Image
    public void deleteFile(String fileUrl) {

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        String deleteUrl = supabaseUrl + "/storage/v1/object/"
                + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, request, String.class);
    }
}
