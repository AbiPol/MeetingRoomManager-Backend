package com.meetingroom.manager.services.interfaces;

import java.io.File;


import com.meetingroom.manager.persistence.entity.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;

public interface IEmailService {

    public void sendMailConfirmAccount(Session sesion, Usuario user, String siteURL) throws AddressException, MessagingException;

    void sendEmail(String toUser, String subject, String message);

    void sendEmailWithFile(String[] toUser, String subject, String message, File file);

    public Session creaSession();
    //public void sendVerificationEmail(Usuario user, String siteURL);
}
