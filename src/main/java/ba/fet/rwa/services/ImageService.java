package ba.fet.rwa.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class ImageService {
    private static final String UPLOAD_DIR_NAME = "images";
    private File uploadDir;

    public ImageService() {
        uploadDir = new File(UPLOAD_DIR_NAME);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    public String getImageDirName() {
        return UPLOAD_DIR_NAME;
    }

    // create image from stream
    public String createImage(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        String fileName = UUID.randomUUID().toString();
        Files.copy(inputStream, Paths.get(uploadDir.getPath(), fileName));

        return fileName;
    }
}
