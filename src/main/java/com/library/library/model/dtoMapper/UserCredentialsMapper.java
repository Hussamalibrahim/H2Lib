package com.library.library.model.dtoMapper;

import com.library.library.model.dto.UserCredentialsDto;
import com.library.library.model.UserCredentials;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class UserCredentialsMapper {

    public static UserCredentials convertToEntity(UserCredentialsDto userCredentialsDto) {
        UserCredentials userCredentials = new UserCredentials();


        userCredentials.setId(userCredentialsDto.getId());
        userCredentials.setUser(userCredentialsDto.getUser());
        userCredentials.setEmail(userCredentialsDto.getEmail());
        // NEVER include password
        userCredentials.setEnabled(userCredentialsDto.getEnabled());
        userCredentials.setEmailVerified(userCredentialsDto.getEmailVerified());
        userCredentials.setLocked(userCredentialsDto.getLocked());
        userCredentials.setLockedAt(userCredentialsDto.getLockedAt());
        userCredentials.setPasswordChangedAt(userCredentialsDto.getPasswordChangedAt());
        userCredentials.setProviders(userCredentialsDto.getProviders());
        userCredentials.setRole(userCredentialsDto.getRole());


        return userCredentials;
    }

    public static UserCredentialsDto convertToDto(UserCredentials userCredentials) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto();

        userCredentialsDto.setId(userCredentials.getId());
        userCredentialsDto.setUser(userCredentials.getUser());
        userCredentialsDto.setEmail(userCredentials.getEmail());
        // NEVER include password
        userCredentialsDto.setEnabled(userCredentials.getEnabled());
        userCredentialsDto.setEmailVerified(userCredentials.getEmailVerified());
        userCredentialsDto.setLocked(userCredentials.getLocked());
        userCredentialsDto.setLockedAt(userCredentials.getLockedAt());
        userCredentialsDto.setPasswordChangedAt(userCredentials.getPasswordChangedAt());
        userCredentialsDto.setProviders(userCredentials.getProviders());
        userCredentialsDto.setRole(userCredentials.getRole());

        return userCredentialsDto;
    }
}
