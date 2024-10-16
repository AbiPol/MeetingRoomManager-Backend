package com.meetingroom.manager.presentation.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmailFileDTO {

    private String[] toUser;
    private String subject;
    private String message;
    private MultipartFile file;
}
