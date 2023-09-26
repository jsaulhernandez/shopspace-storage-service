package com.shopspace.storage.controller;

import com.shopspace.storage.dto.FileDTO;
import com.shopspace.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody FileDTO request) {
        return new ResponseEntity<>(fileService.upload(request), HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam String path) {
        return new ResponseEntity<>(fileService.delete(path), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String path) {
        return fileService.download(path);
    }
}
