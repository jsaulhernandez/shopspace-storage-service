package com.shopspace.storage.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FirebaseStorageUtil {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    FirebaseProperties firebaseProperties;
    @Autowired
    private FileUtil fileUtil;
    @Value("${files.validations.fileSize.min}")
    private Integer minFileSize;
    @Value("${files.validations.fileSize.max}")
    private Integer maxFileSize;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        // initialize Firebase
        try {
            ClassPathResource serviceAccount = new ClassPathResource("firebase.json");

            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream())).setStorageBucket(firebaseProperties.getBucketName()).build();
            FirebaseApp.initializeApp(options);

            logger.info("Is Firebase Started: " + FirebaseApp.getInstance().getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @return instance of bucket
     */
    private Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    /**
     *
     * @param base64 file in base64
     * @param folder folder in firebase where file will be added
     * @return file name uploaded
     */
    public String upload(String base64, String folder) {
        File file = null;
        Blob blob;

        try {
            if (StringUtils.isEmpty(base64) || (base64.trim().isEmpty()) || (StringUtils.isEmpty(folder) || (folder.trim().isEmpty())))
                return null;

            file = fileUtil.base64ToFile(base64);
            blob = this.upload(file, folder);

            return blob.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
           fileUtil.safeCloseAndDelete(file);
        }
    }

    /**
     *
     * @param file file to upload
     * @param folder place where file will be located
     * @return Blob
     * @throws IOException throw exception if file is not converted to bytes
     */
    public Blob upload(File file, String folder) throws IOException {
        boolean validate = fileUtil.validateFile(file, minFileSize, maxFileSize);
        if (!validate) return null;

        byte[] fileData = FileUtils.readFileToByteArray(file);

        String path = folder + "/" + file.getName();
        String mimeType = fileUtil.mediaTypeFromName(file.getName());
        Bucket bucket = this.getBucket();

        return bucket.create(path, fileData, mimeType);
    }

    /**
     *
     * @param path include the folder and file name
     * @return boolean if the file is deleted
     */
    public boolean deleteWithPath(String path) {
        String name = fileUtil.getNameFromPath(path);
        String folder = fileUtil.getFolderFromPath(path);

        return this.delete(name, folder);
    }

    /**
     *
     * @param name file name
     * @param folder place where is the file
     * @return boolean if the file is deleted
     */
    public boolean delete(String name, String folder) {
        try {
            Blob exists = this.getBlobFile(name, folder);

            if (exists == null) return false;

            return exists.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param name file name
     * @param folder folder where be the file
     * @return Blob
     */
    public Blob getBlobFile(String name, String folder) {
        String path = folder + "/" + name;
        return this.download(path);
    }

    /**
     *
     * @param path folder and file name join
     * @return Blob
     */
    public Blob download(String path) {
        try {
            Bucket bucket = this.getBucket();
            logger.info("instantiating the bucket: {}", bucket.getName());
            logger.info("path of firebase: {}", path);

            return bucket.get(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
