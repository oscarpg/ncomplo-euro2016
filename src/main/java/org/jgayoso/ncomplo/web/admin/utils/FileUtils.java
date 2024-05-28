package org.jgayoso.ncomplo.web.admin.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static File convert(final String useCase, final MultipartFile file, final String login) throws IOException {
        final File convFile = new File(useCase+'#'+login);

        try (final FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
