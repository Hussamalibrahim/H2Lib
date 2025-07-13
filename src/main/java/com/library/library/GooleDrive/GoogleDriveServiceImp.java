package com.library.library.GooleDrive;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.library.library.Exception.infrastructure.DriveException;
import com.library.library.Utils.TypeFiles;
import com.library.library.Exception.Validation.FileValidationException;
import com.library.library.Exception.Validation.InvalidFileExtensionException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class GoogleDriveServiceImp implements GoogleDriveService {
//webContentLink to access to file directly without ui for files
    //webViewLink

    @Autowired
    private Drive drive;

    @Autowired
    private GoogleDriveProperties properties;

    // Folder management
    public String ensureFolderStructure(List<String> folderPath) throws IOException {
        if (folderPath.size() != 2 ||
                !folderPath.get(0).equals(properties.getRootFolder()) ||
                !properties.getAllowedFolders().contains(folderPath.get(1))) {
            throw new IllegalArgumentException("Invalid folder path specified");
        }

        String parentId = findFolder(properties.getRootFolder());
        if (parentId == null) {
            throw new IllegalStateException("Root folder not found");
        }

        String childFolder = folderPath.get(1);
        String folderId = findChildFolder(parentId, childFolder);

        if (folderId == null) {
            throw new IllegalStateException(
                    String.format("Folder '%s' does not exist in '%s'", childFolder, properties.getRootFolder()));
        }

        return folderId;
    }

    private String findFolder(String folderName) throws IOException {
        FileList result = drive.files().list()
                .setQ(String.format("name='%s' and mimeType='application/vnd.google-apps.folder'", folderName))
                .setSpaces("drive")
                .setFields("files(id)")
                .execute();

        return result.getFiles().isEmpty() ? null : result.getFiles().getFirst().getId();
    }

    private String findChildFolder(String parentId, String folderName) throws IOException {
        FileList result = drive.files().list()
                .setQ(String.format("'%s' in parents and name='%s' and mimeType='application/vnd.google-apps.folder'",
                        parentId, folderName))
                .setSpaces("drive")
                .setFields("files(id)")
                .execute();

        return result.getFiles().isEmpty() ? null : result.getFiles().getFirst().getId();
    }

    // File upload methods
    public File uploadEbook(@NotNull MultipartFile file) throws IOException {
        validateFile(file, properties.getMaxEbookSize(), properties.getAllowedEbookFormats());
        String folderId = ensureFolderStructure(List.of(properties.getRootFolder(), "ebooks"));
        return uploadFile(file, TypeFiles.getMimeType(file.getOriginalFilename()), folderId);
    }

    public File uploadCover(@NotNull MultipartFile file) throws IOException {
        validateFile(file, properties.getMaxCoverSize(), properties.getAllowedCoverFormats());
        String folderId = ensureFolderStructure(List.of(properties.getRootFolder(), "covers"));
        return uploadFile(file, TypeFiles.getMimeType(file.getOriginalFilename()), folderId);
    }

    public File uploadProfilePicture(@NotNull MultipartFile file) throws IOException {
        validateFile(file, properties.getMaxProfilePictureSize(), properties.getAllowedProfilePictureFormats());
        String folderId = ensureFolderStructure(List.of(properties.getRootFolder(), "profile-pictures"));
        return uploadFile(file, TypeFiles.getMimeType(file.getOriginalFilename()), folderId);
    }

    private void validateFile(MultipartFile file, DataSize maxSize, List<String> allowedFormats) {
        if (file.getSize() > maxSize.toBytes()) {
            throw new FileValidationException(
                    String.format("File size exceeds maximum allowed size of '%s' your file size '%s'",maxSize,file.getSize()));
        }

        String fileExtension = TypeFiles.getFileExtension(file.getOriginalFilename()).toUpperCase();
        if (!allowedFormats.contains(fileExtension.toUpperCase())) {
            throw new InvalidFileExtensionException(
                    String.format("File extension '%s' not allowed. Allowed: %s",
                            fileExtension,
                            String.join(", ", allowedFormats)
                    )
            );
        }
    }



    // Core operations
    private File uploadFile(MultipartFile file, String mimeType, String folderId) throws IOException {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList(folderId));

            ByteArrayContent mediaContent = new ByteArrayContent(
                    mimeType,
                    file.getBytes()
            );
            return drive.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, webViewLink, webContentLink, parents, mimeType")
                    .execute();
        } catch (DriveException driveOperationException) {
            throw new DriveException("Failed to upload file to Google Drive", driveOperationException);
        }
    }

    public byte[] downloadFile(String fileId) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            return outputStream.toByteArray();
        }
    }

    public void deleteFile(String fileId) throws IOException {
        drive.files().delete(fileId).execute();
    }

    // Listing methods
    public List<File> listEbooks() throws IOException {
        String folderId = ensureFolderStructure(List.of(properties.getRootFolder(), "ebooks"));
        return listFolderContents(folderId);
    }

    public List<File> listCovers() throws IOException {
        String folderId = ensureFolderStructure(List.of(properties.getRootFolder(), "covers"));
        return listFolderContents(folderId);
    }

    public List<File> listProfilePictures() throws IOException {
        String folderId = ensureFolderStructure(List.of(properties.getRootFolder(), "profile-pictures"));
        return listFolderContents(folderId);
    }

    private List<File> listFolderContents(String folderId) throws IOException {
        FileList result = drive.files().list()
                .setQ(String.format("'%s' in parents", folderId))
                .setSpaces("drive")
                .setFields("files(id, name, mimeType, webViewLink, webContentLink, parents, createdTime, modifiedTime)")
                .execute();

        return Optional.ofNullable(result.getFiles()).orElse(Collections.emptyList());
    }
    // Direct URL access
    public String getFileViewUrl(String fileId) throws IOException {
        File file = drive.files().get(fileId).setFields("webViewLink").execute();
        return file.getWebViewLink();
    }
    // Direct URL access
    public String getFileDownloadUrl(String fileId) throws IOException {
        File file = drive.files().get(fileId).setFields("webContentLink").execute();
        return file.getWebContentLink();
    }
}