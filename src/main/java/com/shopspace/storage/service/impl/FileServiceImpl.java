package com.shopspace.storage.service.impl;

import com.google.cloud.storage.Blob;
import com.shopspace.storage.dto.FileDTO;
import com.shopspace.storage.exception.ResourceNotFoundException;
import com.shopspace.storage.service.FileService;
import com.shopspace.storage.util.FileUtil;
import com.shopspace.storage.util.FirebaseStorageUtil;
import com.shopspace.storage.util.ShopSpaceStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    FirebaseStorageUtil firebaseStorageUtil;
    @Autowired
    FileUtil fileUtil;

    @Override
    public String upload(FileDTO request) {
        String identifier = null;

        if (StringUtils.hasText(request.getBase64()) && StringUtils.hasText(request.getFolder()))
            identifier = firebaseStorageUtil.upload(request.getBase64(), request.getFolder());

        return identifier;
    }

    @Override
    public boolean delete(String path) {
        return  firebaseStorageUtil.deleteWithPath(path);
    }

    @Override
    public ResponseEntity<byte[]> download(String path) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        path = ShopSpaceStorageUtil.decode(path);
        String name = fileUtil.getNameFromPath(path);
        String mimetype = fileUtil.mediaTypeFromName(name);

        Blob blob = firebaseStorageUtil.download(path);

        if (blob == null) throw new ResourceNotFoundException("The file doesn't exist");

        byte[] content = blob.getContent();

        headers.add("Content-Description", "File Transfer");
        headers.add("Content-Type", mimetype);
        headers.add("Content-Disposition", "attachment; filename=" + name);
        headers.add("Content-Transfer-Encoding", "binary");
        headers.add("Connection", "Keep-Alive");
        headers.add("Expires", "0");
        headers.add("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        headers.add("Pragma", "public");
        headers.add("Content-Length", String.valueOf(content.length));

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
