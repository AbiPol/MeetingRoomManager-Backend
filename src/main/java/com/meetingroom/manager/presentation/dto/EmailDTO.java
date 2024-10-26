package com.meetingroom.manager.presentation.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmailDTO {

    private String toUser;
    private String subject;
    private String message;
}
