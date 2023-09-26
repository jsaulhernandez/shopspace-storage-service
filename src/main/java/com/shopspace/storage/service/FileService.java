package com.shopspace.storage.service;

import com.shopspace.storage.dto.FileDTO;
import org.springframework.http.ResponseEntity;

public interface FileService {
    String upload(FileDTO request);

    boolean delete(String path);

    ResponseEntity<byte[]> download(String path);
}
