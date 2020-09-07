package com.navercorp.rabbit.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.navercorp.rabbit.util.jsch.JSchUtil;
import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.Node.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class JSchService {
    private final JSchUtil jschUtil;

    /**
     * 클러스터링 설정을 위한 hosts파일 초기 설정
     * vi ../etc/hosts 파일 편집과 같은 기능
     *
     * 자기 자신을 제외한 나머지 노드들의 호스트 정보 입력
     * ex) 현재 노드가 rabbit-88001이라면
     *
     * 127.0.0.1       rabbit-88001 // 은 적혀있다.
     * 10.41.1.151     rabbit-53  // 여기서부터 써주는 것
     *
     * => SFTP의 Download & Upload로 해결
     */
    public void initCluster(List<Node> nodes) throws Exception {
        log.info("init Cluster.............");
        String dir = "/etc/hosts";
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            //i번째 노드 host init
            Session session = jschUtil.createSession(node);
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.connect();

            // hosts 다운로드
            File file = new File("hosts");
            jschUtil.download(sftp, dir, file);

            // hosts 파일 수정
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            for (int j = 0; j < nodes.size(); j++) {
                Node onode = nodes.get(j);
                String name = onode.getServerName();
                String privateIp = onode.getPrivateIp();
                pw.write('\n' + privateIp + '\t' + name);
            }
            pw.flush();
            pw.close();

            // hosts 업로드
            jschUtil.upload(sftp, dir, file);

            sftp.disconnect();
            session.disconnect();
        }
        log.info("finish Cluster.............");
    }


    public void joinCluster(List<Node> nodes) throws Exception {
        log.info("joinCluster Start...............");

        String command = null;
        String targetHostname = "rabbit@"+nodes.get(0).getServerName();

        for (int i = 1; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            command = "rabbitmqctl stop_app; "
                    + "rabbitmqctl join_cluster " + targetHostname + "; "
                    + "rabbitmqctl start_app; ";

            log.info(node.getServerName() + "'s command={}", command);

            Session session = jschUtil.createSession(node);
            Channel channel = session.openChannel("exec");  //채널접속
            ChannelExec channelExec = (ChannelExec) channel; //명령 전송 채널사용
            channelExec.setPty(true);
            channelExec.setCommand(command);

            //콜백(디버깅용)
            StringBuilder outputBuffer = new StringBuilder();
            InputStream in = channel.getInputStream();
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect(); //실행

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int inread = in.read(tmp, 0, 1024);
                    outputBuffer.append(new String(tmp, 0, inread));
                    if (inread < 0) break;
                }
                if (channel.isClosed()) {
                    log.info(outputBuffer.toString());
                    channel.disconnect();
                    break;
                }
            }
        }
        log.info("joinCluster Finish...............");
    }


    /**
     * # ha-all은 새로 추가되는 정책의 이름, ^ha\.은 regexp로 표현된 미러링할 queue 이름, 뒤의 json은 정책 세부 사항,
     * rabbitmqctl set_policy ha-all "^ha\." '{"ha-mode":"all"}'
     *
     * # myQueue Virtual Host 속한 이름에 .ha 가 포함된 Queue를 모두 미러링 하는 정책
     * rabbitmqctl set_policy -p myQueue ha-all "^.*\.ha.*" '{"ha-mode":"all"}'
     *
     * rabbitmqctl list_policies 로 정책 확인 가능
     */
    public void mirroring(Node node) throws Exception {
        log.info("mirroring Start...............");
        //rabbitmqctl set_policy ha-all "^ha\." '{"ha-mode":"all"}'

        String command = "rabbitmqctl set_policy ha-all \"^ha\\.\" '{\"ha-mode\":\"all\"}'";
        log.info(node.getServerName() + "'s command={}", command);
        Session session = jschUtil.createSession(node);
        Channel channel = session.openChannel("exec");  //채널접속
        ChannelExec channelExec = (ChannelExec) channel; //명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand(command);

        //콜백(디버깅용)
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err);

        channel.connect(); //실행

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int inread = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, inread));
                if (inread < 0) break;
            }
            if (channel.isClosed()) {
                log.info(outputBuffer.toString());
                channel.disconnect();
                break;
            }
        }
        log.info("mirroring Finish...............");
    }


    /**
     *  scale in 을 위한 메서드
     *  현재 node가 속한 Cluster에서 빠져나오게 하고, 현재 node는 stop&terminate
     */
    @Transactional
    public void leaveCluster(Node node) throws Exception{
        String command = "rabbitmqctl stop_app; "+ "rabbitmqctl reset;";

        Session session = jschUtil.createSession(node);
        Channel channel = session.openChannel("exec");  //채널접속
        ChannelExec channelExec = (ChannelExec) channel; //명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand(command);

        //콜백(디버깅용)
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err);

        channel.connect(); //실행

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int inread = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, inread));
                if (inread < 0) break;
            }
            if (channel.isClosed()) {
                log.info(outputBuffer.toString());
                channel.disconnect();
                break;
            }
        }
    }

    /**
     *  scale out을 위한 메서드
     *  현재 node를 cluster에 이어붙이고, HA mode=all 정책을 연결 시점 이후부터 메세지를 복사하게 한다.
     */
    public void joinCluster(Node newNode, Node targetNode, List<Node> nodes) throws Exception{
        initHosts(newNode,nodes);
        String targetHostname="rabbit@"+targetNode.getServerName();

        log.info("current Host={}",newNode.getServerName());
        log.info("targetHostname={}",targetHostname);

        String command = "rabbitmqctl stop_app; "
                + "rabbitmqctl join_cluster " + targetHostname + "; "
                + "rabbitmqctl start_app; ";

        log.info("Scale Out Command={}",command);

        Session session = jschUtil.createSession(newNode);
        Channel channel = session.openChannel("exec");  //채널접속
        ChannelExec channelExec = (ChannelExec) channel; //명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand(command);

        //콜백(디버깅용)
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err);

        channel.connect(); //실행

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int inread = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, inread));
                if (inread < 0) break;
            }
            if (channel.isClosed()) {
                log.info(outputBuffer.toString());
                channel.disconnect();
                break;
            }
        }
    }

    /**
     *  새로 추가된 Node를 Cluster에 이어 붙이기 위한 사전 작업(hosts 파일 수정)
     */
    private void initHosts(Node newNode,List<Node> nodes) throws Exception{
        //새로운 노드의 hosts file에 기존 Node들의 정보를 추가함.
        List<Node> targetNodes=new ArrayList<>();
        for(int i=0;i<nodes.size();i++){
            Node node=nodes.get(i);
            targetNodes.add(node);
        }

        editHosts(newNode,targetNodes);

        //기존 Node들의 hosts file에 새로운 노드 정보를 추가함.
        targetNodes.clear();
        targetNodes.add(newNode);

        for(int i=0;i<nodes.size();i++){
            Node node=nodes.get(i);
            if(node.getServerInstanceNo().equals(newNode.getServerInstanceNo())) continue;

            editHosts(node,targetNodes);
        }
    }

    /**
     * node의 hosts file에 targetNode들의 정보를 추가함
     */
    public void editHosts(Node curNode,List<Node> targetNodes) throws Exception{
        String dir = "/etc/hosts";

        Session session = jschUtil.createSession(curNode);
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftp = (ChannelSftp) channel;
        sftp.connect();

        // hosts 다운로드
        File file = new File("hosts");
        jschUtil.download(sftp, dir, file);

        // hosts 파일 수정
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

        for (int j = 0; j < targetNodes.size(); j++) {
            Node node = targetNodes.get(j);
            String name = node.getServerName();
            String privateIp = node.getPrivateIp();
            pw.write('\n' + privateIp + '\t' + name);
        }
        pw.flush();
        pw.close();

        // hosts 업로드
        jschUtil.upload(sftp, dir, file);

        sftp.disconnect();
        session.disconnect();
    }
}
