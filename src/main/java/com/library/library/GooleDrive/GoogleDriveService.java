package com.library.library.GooleDrive;

import com.google.api.services.drive.model.File;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GoogleDriveService {

    File uploadEbook(@NotNull MultipartFile file) throws IOException;

    File uploadCover(@NotNull MultipartFile file) throws IOException;

    byte[] downloadFile(String fileId) throws IOException;

    void deleteFile(String fileId) throws IOException;

    String getFileViewUrl(String fileId) throws IOException;

    String getFileDownloadUrl(String fileId) throws IOException;

    List<File> listProfilePictures() throws IOException;

    List<File> listCovers() throws IOException;

    List<File> listEbooks() throws IOException;

    File uploadProfilePicture(@NotNull MultipartFile file) throws IOException;

}

