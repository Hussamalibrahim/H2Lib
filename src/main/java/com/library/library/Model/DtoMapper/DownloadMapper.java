package com.library.library.Model.DtoMapper;


import com.library.library.Model.Dto.DownloadsDto;
import com.library.library.Model.Downloads;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DownloadMapper {


    public static Downloads ConvertToEntity(DownloadsDto downloadsDto){
        Downloads downloads = new Downloads();

        downloads.setId(downloadsDto.getId());
        downloads.setDateOfDownload(downloadsDto.getDateOfDownload());

        return downloads;
    }

    public static DownloadsDto ConvertToEntity(Downloads downloads){
        DownloadsDto downloadsDto = new DownloadsDto();

        downloadsDto.setId(downloads.getId());
        downloadsDto.setDateOfDownload(downloads.getDateOfDownload());
        return downloadsDto;
    }
}
