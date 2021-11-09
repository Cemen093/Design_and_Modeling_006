import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Properties;

class ServerClient extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private InetAddress inetAddress;
    private int countConnections;

    public ServerClient(Socket socket) throws IOException {
        this.socket = socket;
        this.inetAddress = socket.getInetAddress();
        this.countConnections = 1;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void reStart(Socket socket) throws IOException {
        this.socket = socket;
        this.inetAddress = socket.getInetAddress();
        this.countConnections = countConnections + 1;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private String readLine(){
        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getCountConnections() {
        return countConnections;
    }

    @Override
    public void run() {
        String line;
        send("you have been logged in");

        User user = new User();
        send("Registration");

        send("please input you email");
        user.email = readLine();
        send("please input you login");
        user.login = readLine();
        send("please input you password");
        user.password = readLine();
        send("please input you password again");
        user.repeatPassword = readLine();

        if (user.password.equals(user.repeatPassword)){
            sendMail(user);
        } else {
            send("registration fail, password dont match.");
        }
        send("stop");
        close();
    }

    private class User{
        String email;
        String login;
        String password;
        String repeatPassword;
    }

    private void sendMail(User user){
        String serverEmail = "Semyonp93@gmail.com";
        final String serverUsername = "Semyonp93";
        final String serverPassword = "***";
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return;
        }
        sf.setTrustAllHosts(true);


        Properties props = (Properties) System.getProperties().clone();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.ssl.enable", false);
        props.put("mail.smtp.starttls.enable", true);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(serverUsername, serverPassword);
            }
        });

        Message message;
        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(serverEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.email));
            message.setSubject("Registration");
            message.setText("Hello "+user.login+", you have been successfully registered in my application.");
            Transport transport = session.getTransport("smtp");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void close(){
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}