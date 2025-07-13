package com.library.library.GooleDrive;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import java.util.List;

@Setter
@Getter
@Component
public class GoogleDriveProperties {

    @NotBlank
    @Value("${google.drive.root-folder}")
    private String rootFolder;

    @NotEmpty
    @Value("#{'${google.drive.allowed-folders}'.split(',')}")
    private List<String> allowedFolders;

    @NotEmpty
    @Value("#{'${google.drive.allowed-ebook-formats}'.split(',')}")
    private List<String> allowedEbookFormats;

    @NotBlank
    @Value("${google.drive.max-ebook-size}")
    private DataSize maxEbookSize;

    @NotEmpty
    @Value("#{'${google.drive.allowed-cover-formats}'.split(',')}")
    private List<String> allowedCoverFormats;

    @NotBlank
    @Value("${google.drive.max-cover-size}")
    private DataSize maxCoverSize;

    @NotEmpty
    @Value("#{'${google.drive.allowed-profile-picture-formats}'.split(',')}")
    private List<String> allowedProfilePictureFormats;

    @NotBlank
    @Value("${google.drive.max-profile-picture-size}")
    private DataSize maxProfilePictureSize;
}