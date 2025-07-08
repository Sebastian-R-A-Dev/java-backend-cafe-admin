package com.inn.cafe.utils;

import com.inn.cafe.constants.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender emailSender;

    public static final String ACCOUNT_APPROVED = "Cuenta Aprobada.";
    public static final String ACCOUNT_DISABLED = "Cuenta Desabilitada.";
    public static final String FORGOT_PASSWORD = "Solicitud de nueva contraseña.";


    public void sendSimpleMessage(String to, String subject, String text, List<String> additionalEmails) {
        //SimpleMailMessage message = new SimpleMailMessage();
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(Constants.EMAIL_SENDER);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setTo(to);
            if (additionalEmails != null && !additionalEmails.isEmpty())
                mimeMessageHelper.setCc(this.getCcArray(additionalEmails));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        /*message.setFrom(Constants.EMAIL_SENDER);
        message.setTo(to);
        message.setSubject(subject);
        message.setText();*/

        emailSender.send(mimeMessage);
    }

    private String[] getCcArray(List<String> ccList) {
        String[] cc = new String[ccList.size()];
        for (int i = 0; i < ccList.size(); i++) {
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public String forgotPasswordBody(String userName, String temporalPassword, String currentDate) {
        //HTML template
        return """
        <!DOCTYPE html>
        <html>
        <body style="font-family: Arial, sans-serif; text-align: center; background-color: #f5f5dc; padding: 20px;">
            <center><div style="height: 100vh;background-color: #f5f5dc; padding: 20px; border-radius: 10px; display: inline-block; text-align: center; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);">
                <center><h1 style="color: #8B4513;">Café Admin</h1></center>
                <div style="padding-left: 35px;padding-right: 35px;">
                <p style="margin: 10px 0; font-size:25px; font-weight: bold;text-align: left;">Hola <b>%s</b> tenemos nuevas noticias.</p>
                <p style="margin-top: 50px; font-size:20px;text-align: left;">Una nueva contraseña temporal ah sido generada: <b>%s</b>, en la fecha <b>%s</b></p><br/>
                 <p style="font-size:20px;text-align: left;">Recuerde cambiar esta contraseña para evitar tener problemas en su proximo inicio de sesión.</p>
                <p style="margin-top: 200px; font-size:20px;text-align: left;">Si usted no ah solicitado una nueva contraseña y cree que es un error, porfavor comuniquese con admministración.</p>
                </div>
            </div></center>
        </body>
        </html>
        """.formatted(userName, temporalPassword,currentDate);
    }

    public String enableOrDisableUserBody(String emailToProcess, String currentAdminEmail, String currentDate, boolean wasDisable) {
        String enableOrDisableMsj = wasDisable ? "desabilitado" : "habilitado";
        //HTML template
        return """
        <!DOCTYPE html>
        <html>
        <body style="font-family: Arial, sans-serif; text-align: center; background-color: #f5f5dc; padding: 20px;">
            <center><div style="height: 100vh;background-color: #f5f5dc; padding: 20px; border-radius: 10px; display: inline-block; text-align: center; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);">
                <center><h1 style="color: #8B4513;">Café Admin</h1></center>
                <div style="padding-left: 35px;padding-right: 35px;">
                <p style="margin: 10px 0; font-size:25px; font-weight: bold;text-align: left;">Aviso a todos los administradores!</p>
                <p style="margin-top: 50px; font-size:20px;text-align: left;">El usuario <b>%s</b> fue <b>%s</b> por el administrador <b>%s</b> en la fecha <b>%s</b>.</p>
                <p style="margin-top: 200px; font-size:20px;text-align: left;">Buen día para todos los administradores, por favor comunicarse con <b>%s</b> si esto se trata de un error.</p>
                </div>
            </div></center>
        </body>
        </html>
        """.formatted(emailToProcess, enableOrDisableMsj, currentAdminEmail, currentDate, currentAdminEmail);
    }
}
