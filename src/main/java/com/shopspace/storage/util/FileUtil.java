package com.shopspace.storage.util;

import com.shopspace.storage.exception.FileExtensionException;
import com.shopspace.storage.exception.FileSizeException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class FileUtil {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${files.validations.availableFormats}")
    private Set<String> defaultAvailableFormats;

    /**
     *
     * @param base64 base64 from file
     * @return File, base64 is converting to File
     */
    public File base64ToFile(String base64) {
        return this.base64ToFile(base64, null);
    }

    /**
     *
     * @param base64 base64 from file
     * @param name file name
     * @return File, base64 is converting to File
     */
    public File base64ToFile(String base64, String name) {
        try {
            if (name == null) name = UUID.randomUUID().toString();

            String symbol = Pattern.quote(",");
            String format = "." + this.getFormatFromBase64(base64);
            String path = this.getPublicPathOS() + name + (name.contains(format) ? "" : format);
            String[] formattedBase64 = base64.split(symbol);

            byte[] data = Base64.decodeBase64(formattedBase64[1]);
            try (OutputStream stream = new FileOutputStream(path)) {
                stream.write(data);
            }

            return new File(path);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     *
     * @param base64 string
     * @return file media type (png, jpeg, jpg, webp, etc)
     */
    public String getFormatFromBase64(String base64) {
        try {
            String firstSymbol = Pattern.quote(";");
            String secondSymbol = Pattern.quote(":");
            String thirdSymbol = Pattern.quote("/");

            String[] first = base64.split(firstSymbol);
            String[] second = first[0].split(secondSymbol);
            String[] third = second[1].split(thirdSymbol);

            return third[1];
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     *
     * @return path of tmp folder where be uploading images
     */
    public String getPublicPathOS() {
        try {
            String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

            if (os.equals("linux")) return "/tmp";

            // ClassLoader classLoader = getClass().getClassLoader();
            // String path = classLoader.getResource(".").getFile() + "tmp/";

            String path = new ClassPathResource(".").getFile().getPath() + "tmp/";

            File folder = new File(path);

            if (!folder.exists()) {
                folder.mkdir();
                folder.setReadable(true);
                folder.setWritable(true);
            }

            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param f file to remove from tmp folder
     */
    public void safeCloseAndDelete(File f) {
        try {
            f.delete();
        } catch (Exception e) {
            logger.error("An error occurred while trying to save the file securely");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param f file
     * @return get file size in kilobyte
     */
    public Integer getFileSizeKb(File f) {
        try {
            return (int) (f.length() / 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     *
     * @param f file to validate
     * @param minSize min size for file
     * @param maxSize max size for file
     * @return boolean value if validations are ok
     */
    public boolean validateFile(File f, Integer minSize, Integer maxSize) {
        return this.validateFile(f, minSize, maxSize, null);
    }

    /**
     *
      * @param f file to validate
     * @param minSize min size for file
     * @param maxSize max size for file
     * @param formatsAvailable formats available that can upload
     * @return boolean value if validations are ok
     */
    public boolean validateFile(File f, Integer minSize, Integer maxSize, Set<String> formatsAvailable) {
        String symbol = Pattern.quote(".");

        if (formatsAvailable == null) {
            formatsAvailable = this.defaultAvailableFormats;
        }

        int sizeKb = this.getFileSizeKb(f);
        if (sizeKb < minSize)
            throw new FileSizeException("An error occurred while validating the minimum file size which should be: " + minSize + " kb");


        if (sizeKb > maxSize)
            throw new FileSizeException("An error occurred while validating the maximum file size which should be: " + minSize + " kb");

        String[] fileDataName = f.getName().split(symbol);
        String format = fileDataName[1];
        boolean isValidateFormat = formatsAvailable.contains(format);

        if (!isValidateFormat)
            throw new FileExtensionException("An error occurred validating the file extension which should be: " + formatsAvailable);

        return true;
    }

    /**
     *
     * @param name file name
     * @return file mimetype
     */
    public String mediaTypeFromName(String name) {
        String symbol = Pattern.quote(".");
        String[] nameSplited = name.split(symbol);
        String format = nameSplited[nameSplited.length - 1];

        return this.mediaTypeFromFormat(format);
    }

    /**
     *
     * @param format is file media type (png, jpeg, jpg, webp)
     * @return file mimetype
     */
    public String mediaTypeFromFormat(String format) {
        switch (format) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            case "png":
                return MediaType.IMAGE_PNG_VALUE;
            case "webp":
                return "image/webp";
            default:
                break;
        }

        return null;
    }

    /**
     *
     * @param path path of the file from DB
     * @return file name
     */
    public String getNameFromPath(String path) {
        try {
            if (path.contains("/")) {
                String symbol = Pattern.quote("/");

                String[] pathSplited = path.split(symbol);

                return pathSplited[pathSplited.length - 1];
            }

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param path path of file from db
     * @return folder name where the file is located
     */
    public String getFolderFromPath(String path) {
        String name = this.getNameFromPath(path);

        return path.replace("/" + name, "");
    }
}
