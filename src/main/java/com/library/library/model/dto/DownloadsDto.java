package com.library.library.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class DownloadsDto {
    private Long id;

    private Date dateOfDownload;

}
