package com.meetingroom.manager.services.implementacion;

import java.io.File;
//import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.services.interfaces.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${email.sender}")
    private String emailUser;

    @Autowired
    private JavaMailSender mailSender;

    
    @Override
    @Transactional
    public void sendMailConfirmAccount(Usuario user, String siteURL) {
        MimeMessage mensaje = mailSender.createMimeMessage();
        System.out.println("sendMailConfirmAccount ***SITEURL: " + siteURL);
        try {
            mensaje.setFrom(new InternetAddress(emailUser));
            mensaje.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
            mensaje.setContent(getHtmlConfirm(siteURL,user.getVerificationCode(),user.getEmail()), "text/html; charset=utf-8");
            mensaje.setSubject("Verifica tu registro en Meeting-Room Services");

            mailSender.send(mensaje);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }    
    }
     
    /*
     * Configuramos el correo a enviar con solo texto
     */
    @Override
    @Transactional
    public void sendEmail(String[] toUser, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailUser); //Quien envia el correo
        mailMessage.setTo(toUser);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    /*
     * Metodo para el envio de correos con archivo adjunto
     */
    @Override
    @Transactional
    public void sendEmailWithFile(String[] toUser, String subject, String message, File file) {
         try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(emailUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * No se usa de momento. Se usa el servicio sendMailConfirmAccount
     */
    /* 
    @Override
    @Transactional
    public void sendVerificationEmail(Usuario user, String siteURL)  {
		String toAddress = user.getEmail();
		String fromAddress = emailUser;
		String senderName = "MeetingRoom";
		String subject = "Verifica tu registro en Meeting-Room Services";
		String content = "Hola,<br>"
				//+ "Please click the link below to verify your registration:<br>"
                + "Por favor, haz click en el link de abajo para verificar tu registro:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
				+ "Muchas gracias,<br>"
				+ "Meeting-Room.";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		try {
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            //content = content.replace("[[name]]", user.getFullName());
		    String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
            System.out.println("URL generado: " + verifyURL);
		
            //String verifyURL= "";
		    content = content.replace("[[URL]]", verifyURL);
		
		    helper.setText(content, true);
		
		    mailSender.send(message);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
	}    


    *******************/

    /*
     * ************************************
     * Metodos privados
     * **************************************
     */
    private String getHtmlConfirm(String URL, String codigoVerificacion,String email){
        String htmlEmail = "";

        System.out.println("getHtmlConfirm ***URL: " + URL);
        try {
            Path path = Paths.get("src/main/resources/mails/welcome.html");  
            try (var file = Files.lines(path)) {
                var html = file.collect(Collectors.joining());
                
                String verifyURL = URL + "/verify?code=" + codigoVerificacion;
                htmlEmail = html.replace("{{URL}}", verifyURL).replace("{{fullName}}", email);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return htmlEmail;
    }


   
}
