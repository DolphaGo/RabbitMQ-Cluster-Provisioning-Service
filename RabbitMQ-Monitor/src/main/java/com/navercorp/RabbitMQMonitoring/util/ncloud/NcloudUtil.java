package com.navercorp.RabbitMQMonitoring.util.ncloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@Component
@RequiredArgsConstructor
@Slf4j
public class NcloudUtil {
    public String makeSignature(String method, String url, String timestamp, String accessKey, String secretKey) {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            String encodeBase64String = Base64.encodeBase64String(rawHmac);
            return encodeBase64String;
        } catch (Exception e) {
            log.error("makeSignature error... : " + e);
            return null;
        }
    }

    public HttpEntity<String> getHttpEntity(String method, String url, String accessKey, String secretKey) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = makeSignature(method, url, timestamp, accessKey, secretKey);

        HttpHeaders header = new HttpHeaders();
        header.add("x-ncp-apigw-timestamp", timestamp);
        header.add("x-ncp-iam-access-key", accessKey);
        header.add("x-ncp-apigw-signature-v2", signature);

        return new HttpEntity<>(header);
    }

    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout(10000);//읽기 시간 초과, ms
        httpRequestFactory.setConnectTimeout(10000);//연결 시간 초과, ms
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100) //connection pool 적용 : 최대 오픈되는 커넥션 수를 제한한다.
                .setMaxConnPerRoute(5) //connection pool 적용 : IP,포트 1쌍에 대해 수행 할 연결 수를 제한한다.
                .build();
        httpRequestFactory.setHttpClient(httpClient); //동기실행에 사용될 HttpClient 세팅
        return new RestTemplate(httpRequestFactory);
    }
}
