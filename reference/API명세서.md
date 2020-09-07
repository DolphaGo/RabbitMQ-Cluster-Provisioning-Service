## API 명세서

#### Manager Server : http://49.50.163.24:8081

#### Swagger :  http://49.50.163.24:8081/swagger-ui.html



#### 1. 클러스터 생성

| Method |      요청 URL      |                         Description                          |
| :----: | :----------------: | :----------------------------------------------------------: |
|  POST  | /api/createCluster | 입력받은 `nodeCount`만큼의 노드를 가진 RabbitMQ 클러스터를 생성함 |

| 요청변수명 | 타입 | 필수 여부 |             설명              |
| :--------: | :--: | :-------: | :---------------------------: |
| nodeCount  | int  |     Y     | 클러스터를 구성하는 노드의 수 |



##### 응답 예시

```
{
  "createServerInstancesResponse": {
    "returnMessage": "success",
    "totalRows": 3,
    "serverInstanceList": [
      {
        "serverInstanceNo": "5011875",
        "serverName": "rabbit-1032003",
        "serverInstanceStatus": {
          "code": "INIT",
          "codeName": "Server init state"
        },
        "publicIp": "",
        "privateIp": "10.41.3.178",
        "portForwardingPublicIp": "45.119.146.178"
      },
      {
        "serverInstanceNo": "5011872",
        "serverName": "rabbit-1032002",
        "serverInstanceStatus": {
          "code": "INIT",
          "codeName": "Server init state"
        },
        "publicIp": "",
        "privateIp": "10.41.5.208",
        "portForwardingPublicIp": "45.119.146.178"
      },
      {
        "serverInstanceNo": "5011869",
        "serverName": "rabbit-1032001",
        "serverInstanceStatus": {
          "code": "INIT",
          "codeName": "Server init state"
        },
        "publicIp": "",
        "privateIp": "10.41.164.65",
        "portForwardingPublicIp": "45.119.146.178"
      }
    ]
  }
}
```



#### 2. 클러스터 삭제

| Method |       요청 URL        |                       Description                       |
| :----: | :-------------------: | :-----------------------------------------------------: |
|  POST  | /api/terminateCluster | 입력받은 clusterName을 가진 cluster 관련 자원 모두 반납 |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 #1

```
cluster-1029을 삭제하는 요청을 보냈습니다.
```

##### 응답 예시 #2

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```



#### 3. 로드밸런서 생성

| Method |        요청 URL         |                        Description                         |
| :----: | :---------------------: | :--------------------------------------------------------: |
|  PUT   | /api/createLoadBalancer | 입력받은 clusterName에 해당하는 클러스터에 로드밸런서 할당 |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 #1

```
cluster-1032에 loadBalancer 설정 요청을 보냈습니다.
```

##### 응답 예시 #2

```
{
  "status": 400,
  "code": "L001",
  "message": "이미 로드밸런서가 존재합니다."
}
```

##### 응답 예시 #3

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```



#### 4. 로드밸런서 삭제

| Method |        요청 URL         |                        Description                         |
| :----: | :---------------------: | :--------------------------------------------------------: |
| DELETE | /api/deleteLoadBalancer | 입력받은 clusterName에 해당하는 클러스터에 로드밸런서 삭제 |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시  #1

```
{
  "returnCode": "0",
  "returnMessage": "success",
  "totalRows": "1",
  "loadBalancerInstanceList": [
    {
      "httpKeepAlive": false,
      "loadBalancerInstanceNo": "5011929",
      "virtualIp": "10.41.208.34",
      "loadBalancerName": "loadb1743160563e",
      "loadBalancerAlgorithmType": {
        "code": "RR",
        "codeName": "Round Robin"
      },
      "domainName": "",
      "internetLineType": {
        "code": "PUBLC",
        "codeName": "PUBLC"
      },
      "loadBalancerInstanceStatusName": "terminating",
      "loadBalancerInstanceStatus": {
        "code": "USED",
        "codeName": "NET USED state"
      },
      "loadBalancerInstanceOperation": {
        "code": "TERMT",
        "codeName": "NET TERMINATED OP"
      },
      "networkUsageType": {
        "code": "PRVT",
        "codeName": "Private "
      },
      "isHttpKeepAlive": false,
      "connectionTimeout": 60,
      "loadBalancerRuleList": [
        {
          "protocolType": {
            "code": "TCP",
            "codeName": "tcp"
          },
          "loadBalancerPort": 5672,
          "serverPort": 5672,
          "l7HealthCheckPath": "",
          "certificateName": "",
          "proxyProtocolUseYn": "N"
        }
      ],
      "loadBalancedServerInstanceList": [
        {
          "serverInstance": {
            "serverInstanceNo": "5011875",
            "serverName": "rabbit-1032003",
            "serverInstanceStatus": {
              "code": "RUN",
              "codeName": "Server run state"
            },
            "publicIp": "",
            "privateIp": "10.41.3.178",
            "portForwardingPublicIp": "45.119.146.178"
          },
          "serverHealthCheckStatusList": [
            {
              "protocolType": {
                "code": "TCP",
                "codeName": "tcp"
              },
              "loadBalancerPort": 5672,
              "serverPort": 5672,
              "l7HealthCheckPath": "",
              "proxyProtocolUseYn": "N",
              "serverStatus": true
            }
          ]
        },
        {
          "serverInstance": {
            "serverInstanceNo": "5011872",
            "serverName": "rabbit-1032002",
            "serverInstanceStatus": {
              "code": "RUN",
              "codeName": "Server run state"
            },
            "publicIp": "",
            "privateIp": "10.41.5.208",
            "portForwardingPublicIp": "45.119.146.178"
          },
          "serverHealthCheckStatusList": [
            {
              "protocolType": {
                "code": "TCP",
                "codeName": "tcp"
              },
              "loadBalancerPort": 5672,
              "serverPort": 5672,
              "l7HealthCheckPath": "",
              "proxyProtocolUseYn": "N",
              "serverStatus": true
            }
          ]
        },
        {
          "serverInstance": {
            "serverInstanceNo": "5011869",
            "serverName": "rabbit-1032001",
            "serverInstanceStatus": {
              "code": "RUN",
              "codeName": "Server run state"
            },
            "publicIp": "",
            "privateIp": "10.41.164.65",
            "portForwardingPublicIp": "45.119.146.178"
          },
          "serverHealthCheckStatusList": [
            {
              "protocolType": {
                "code": "TCP",
                "codeName": "tcp"
              },
              "loadBalancerPort": 5672,
              "serverPort": 5672,
              "l7HealthCheckPath": "",
              "proxyProtocolUseYn": "N",
              "serverStatus": true
            }
          ]
        }
      ]
    }
  ]
}
```

##### 응답 예시  #2

```
{
  "status": 400,
  "code": "L002",
  "message": "로드 밸런서가 존재하지 않습니다."
}
```

##### 응답 예시  #3

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```



#### 5. 클러스터 상세 정보 조회

| Method |         요청 URL          |                       Description                       |
| :----: | :-----------------------: | :-----------------------------------------------------: |
|  GET   | /api/detail/{clusterName} | 입력받은 clusterName에 해당하는 클러스터 상세 정보 제공 |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 

```
{
  "clusterName": "cluster-1032",
  "loadBalancerIp": "10.41.208.190",
  "loadBalancerPort": "5672",
  "autoScale": true,
  "onloadBalancer": true,
  "nodeListResponseDtos": [
    {
      "serverInstanceNo": "5012002",
      "serverName": "rabbit-1032003",
      "privateIp": "10.41.83.56",
      "userId": "root",
      "password": "Y3fTAH!29b",
      "serverStatusCode": "RUN",
      "portForwardingPublicIp": "45.119.146.178",
      "portForwardingPort": "1030"
    },
    {
      "serverInstanceNo": "5011999",
      "serverName": "rabbit-1032002",
      "privateIp": "10.41.4.188",
      "userId": "root",
      "password": "L2JG?hBPNRBh",
      "serverStatusCode": "RUN",
      "portForwardingPublicIp": "45.119.146.178",
      "portForwardingPort": "1031"
    },
    {
      "serverInstanceNo": "5011996",
      "serverName": "rabbit-1032001",
      "privateIp": "10.41.165.79",
      "userId": "root",
      "password": "A6*iGU*32Nc",
      "serverStatusCode": "RUN",
      "portForwardingPublicIp": "45.119.146.178",
      "portForwardingPort": "1032"
    }
  ]
}
```



#### 6. 가지고 있는 클러스터 목록 조회

| Method |     요청 URL      |                 Description                 |
| :----: | :---------------: | :-----------------------------------------: |
|  GET   | /api/findClusters | 가지고 있는 클러스터의 이름들을 제공합니다. |



##### 응답 예시 

```
{
  "clusterNameList": [
    "cluster-1029",
    "cluster-1032"
  ]
}
```



#### 7. AutoScale 기능 켜기

| Method |     요청 URL     |                         Description                          |
| :----: | :--------------: | :----------------------------------------------------------: |
|  PUT   | /api/onAutoScale | 입력받은 clusterName에 해당하는 클러스터에 AutoScale 기능 활성화 |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 #1

```
AutoScale 기능 활성화 :cluster-1032
```

##### 응답 예시 #2

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```



#### 8. AutoScale 기능 끄기

| Method |     요청 URL      |                         Description                          |
| :----: | :---------------: | :----------------------------------------------------------: |
| DELETE | /api/offAutoScale | 입력받은 clusterName에 해당하는 클러스터에 AutoScale 기능 비활성화 |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 #1

```
AutoScale 기능 비활성화 :cluster-1032
```

##### 응답 예시 #2

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```



#### 9. 수동 ScaleIn 기능

| Method |   요청 URL   |                         Description                          |
| :----: | :----------: | :----------------------------------------------------------: |
|  POST  | /api/scaleIn | 입력받은 clusterName에 해당하는 클러스터를 scaleIn 조치를 취합니다. |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 #1 - 성공시 ScaleIn 처리 되는 노드의 정보를 제공함

```
{
  "createdDate": "2020-08-28T04:40:16.974",
  "modifiedDate": "2020-08-28T04:40:16.974",
  "nodeId": 3,
  "serverInstanceNo": "5011987",
  "serverName": "rabbit-1029001",
  "privateIp": "10.41.164.238",
  "portForwardingPublicIp": "45.119.146.178",
  "portForwardingPort": "1029",
  "password": "T9@AP9%e-ba",
  "portForwardingConfigurationNo": "76013",
  "serverStatusCode": "RUN",
  "serverStatusCodeName": "Server run state"
}
```

##### 응답 예시 #2 - 클러스터를 구성하는 노드의 개수가 2개 이하일 때

```
{
  "status": 400,
  "code": "S001",
  "message": "더 이상 Scale In을 할 수 없습니다. 노드가 최소 2개는 존재해야 합니다."
}
```

##### 응답 예시 #3 - 존재하지 않는 클러스터에 요청을 보냈을 때

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```

##### 응답 예시 #4 - 해당 클러스터에 ScaleOut 작업이 진행 중일 때

```
{
  "status": 400,
  "code": "S004",
  "message": "현재 해당 클러스터에서 ScaleOut 작업이 진행 중입니다."
}
```

##### 응답 예시 #5 - 해당 클러스터에 ScaleIn 작업이 진행 중일 때

```
{
  "status": 400,
  "code": "S003",
  "message": "현재 해당 클러스터에서 ScaleIn 작업이 진행 중입니다."
}
```



#### 10. 수동 ScaleOut 기능

| Method |   요청 URL    |                         Description                          |
| :----: | :-----------: | :----------------------------------------------------------: |
|  POST  | /api/scaleOut | 입력받은 clusterName에 해당하는 클러스터를 scaleOut 조치를 취합니다. |

| 요청변수명  |  타입  | 필수 여부 |      설명       |
| :---------: | :----: | :-------: | :-------------: |
| clusterName | String |     Y     | 클러스터의 이름 |



##### 응답 예시 #1 - 성공시 ScaleOut 처리 되는 노드의 정보를 제공함

```
{
  "returnMessage": "success",
  "totalRows": 1,
  "serverInstanceList": [
    {
      "serverInstanceNo": "5012056",
      "serverName": "rabbit-1029",
      "serverInstanceStatus": {
        "code": "INIT",
        "codeName": "Server init state"
      },
      "publicIp": "",
      "privateIp": "10.41.164.165",
      "portForwardingPublicIp": "45.119.146.178"
    }
  ]
}
```

##### 응답 예시 #2 - ScaleOut 제한. 5개 이상일 때 Lock

```
{
  "status": 400,
  "code": "S002",
  "message": "더 이상 Scale Out을 할 수 없습니다. 노드는 최대 5개까지 가능합니다."
}
```

##### 응답 예시 #3 - 존재하지 않는 클러스터에 요청을 보냈을 때

```
{
  "status": 400,
  "code": "C001",
  "message": "해당 클러스터가 존재하지 않습니다."
}
```

##### 응답 예시 #4 -  해당 클러스터에 ScaleOut 작업이 진행 중일 때

```
{
  "status": 400,
  "code": "S004",
  "message": "현재 해당 클러스터에서 ScaleOut 작업이 진행 중입니다."
}
```

##### 응답 예시 #5 - 해당 클러스터에 ScaleIn 작업이 진행 중 일 때

```
{
  "status": 400,
  "code": "S003",
  "message": "현재 해당 클러스터에서 ScaleIn 작업이 진행 중입니다."
}
```



#### 에러코드

| HTTP 코드 | 에러 코드 |                         에러 메세지                          |
| :-------: | :-------: | :----------------------------------------------------------: |
|    400    |   C001    |              해당 클러스터가 존재하지 않습니다.              |
|    400    |   R001    |                      잘못된 요청입니다.                      |
|    405    |   R002    |                  다른 작업이 진행 중입니다.                  |
|    405    |   E001    |                   존재하지 않는 값입니다.                    |
|    405    |   L001    |                이미 로드밸런서가 존재합니다.                 |
|    400    |   L002    |               로드 밸런서가 존재하지 않습니다.               |
|    405    |   S001    | 더 이상 Scale In을 할 수 없습니다. 노드가 최소 2개는 존재해야 합니다. |
|    405    |   S002    | 더 이상 Scale Out을 할 수 없습니다. 노드는 최대 5개까지 가능합니다. |
|    405    |   S003    |     현재 해당 클러스터에서 ScaleIn 작업이 진행 중입니다.     |
|    405    |   S004    |    현재 해당 클러스터에서 ScaleOut 작업이 진행 중입니다.     |