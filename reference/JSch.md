JSch

```
// JSch SFTP를 이용한 host 파일 수정
public void edit() throws Exception {

    Session session = createSession(node);
    Channel channel = session.openChannel("sftp");
    ChannelSftp sftp = (ChannelSftp) channel;
    sftp.connect();
  
    InputStream input = sftp.get("/etc/hosts");
    File file = new File("hosts");
    FileUtils.copyInputStreamToFile(input, file);

    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

    for(int i=0; i<instanceName.size(); i++) {
        String name = instanceName.get(i);
        String ip = privateIps.get(i);
        pw.write('\n' + ip + '\t' + name);
    }
    pw.flush();
    pw.close();

    input = new FileInputStream(file);
    sftp.put(input, "/etc/hosts");

    input.close();
    sftp.disconnect();
    session.disconnect();
}
```





 (참고 : https://lts0606.tistory.com/6)

