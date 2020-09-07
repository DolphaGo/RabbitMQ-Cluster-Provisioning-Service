package com.navercorp.RabbitMQMonitoring.util.jsch;

import java.io.*;
import java.util.Properties;

import com.jcraft.jsch.*;
import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@NoArgsConstructor
public class JSchUtil {

    //세션 생성
    public Session createSession(Node node) throws Exception {
        log.info("start createSession.............:" + node.getServerName());
        JSch jSch = new JSch();
        String user = "root";
        String host = node.getPortForwardingPublicIp();
        int port = Integer.parseInt(node.getPortForwardingPort());
        String password = node.getPassword();

        log.info(user + "@" + host + ":" + port + "에 비밀번호:" + password + "를 이용하여 접속합니다.");

        Session session = jSch.getSession(user, host, port);

        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        log.info("finish createSession.............:" + node.getServerName());
        return session;
    }

    //파일 업로드
    public void upload(ChannelSftp sftp, String dir, File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            sftp.put(in, dir);
        } catch (SftpException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    //파일 다운로드
    public void download(ChannelSftp sftp, String dir, File file) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = sftp.get(dir);
        } catch (SftpException e) {
            log.error(e.getMessage());
        }
        try {
            out = new FileOutputStream(file);
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }



}
