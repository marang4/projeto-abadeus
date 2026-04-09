package br.com.abadeus.domain.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file, String pasta) throws Exception;
    void deleteFile(String url);
}