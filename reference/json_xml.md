> getServerInstanceList

Json

```

{
  "getServerInstanceListResponse": {
    "requestId": "f1a16490-cbbe-442b-a239-99511b14af98",
    "returnCode": "0",
    "returnMessage": "success",
    "totalRows": 2,
    "serverInstanceList": [
      {
        "serverInstanceNo": "4881898",
        "serverName": "rabbit-817",
        "serverDescription": "",
        "cpuCount": 2,
        "memorySize": 4294967296,
        "baseBlockStorageSize": 53687091200,
        "platformType": {
          "code": "UBS64",
          "codeName": "Ubuntu Server 64 Bit"
        },
        "loginKeyName": "NS_secret",
        "isFeeChargingMonitoring": false,
        "publicIp": "",
        "privateIp": "10.41.2.75",
        "serverImageName": "rabbitmq(3.7.0)-ubuntu-16.04-64-server",
        "serverInstanceStatus": {
          "code": "RUN",
          "codeName": "Server run state"
        },
        "serverInstanceOperation": {
          "code": "NULL",
          "codeName": "Server NULL OP"
        },
        "serverInstanceStatusName": "running",
        "createDate": "2020-08-05T11:08:01+0900",
        "uptime": "2020-08-05T11:12:57+0900",
        "serverImageProductCode": "SPSW0LINUX000075",
        "serverProductCode": "SPSVRSSD00000003",
        "isProtectServerTermination": false,
        "portForwardingPublicIp": "106.10.34.243",
        "portForwardingExternalPort": 1028,
        "portForwardingInternalPort": 22,
        "zone": {
          "zoneNo": "3",
          "zoneName": "KR-2",
          "zoneCode": "KR-2",
          "zoneDescription": "평촌 zone",
          "regionNo": "1"
        },
        "region": {
          "regionNo": "1",
          "regionCode": "KR",
          "regionName": "Korea"
        },
        "baseBlockStorageDiskType": {
          "code": "NET",
          "codeName": "Network Storage"
        },
        "baseBlockStorageDiskDetailType": {
          "code": "SSD",
          "codeName": "SSD"
        },
        "internetLineType": {
          "code": "PUBLC",
          "codeName": "PUBLC"
        },
        "serverInstanceType": {
          "code": "STAND",
          "codeName": "Standard"
        },
        "userData": "",
        "initScriptNo": "",
        "accessControlGroupList": [
          {
            "accessControlGroupConfigurationNo": "214344",
            "accessControlGroupName": "rabbitmq-acg",
            "accessControlGroupDescription": "",
            "isDefault": false,
            "createDate": "2020-07-30T13:25:39+0900"
          }
        ],
        "instanceTagList": [
          
        ]
      },
      {
        "serverInstanceNo": "4884061",
        "serverName": "rabbit-573",
        "serverDescription": "",
        "cpuCount": 2,
        "memorySize": 4294967296,
        "baseBlockStorageSize": 53687091200,
        "platformType": {
          "code": "UBS64",
          "codeName": "Ubuntu Server 64 Bit"
        },
        "loginKeyName": "NS_secret",
        "isFeeChargingMonitoring": false,
        "publicIp": "",
        "privateIp": "10.41.167.171",
        "serverImageName": "rabbitmq(3.7.0)-ubuntu-16.04-64-server",
        "serverInstanceStatus": {
          "code": "RUN",
          "codeName": "Server run state"
        },
        "serverInstanceOperation": {
          "code": "NULL",
          "codeName": "Server NULL OP"
        },
        "serverInstanceStatusName": "running",
        "createDate": "2020-08-05T14:52:13+0900",
        "uptime": "2020-08-05T14:58:54+0900",
        "serverImageProductCode": "SPSW0LINUX000075",
        "serverProductCode": "SPSVRSSD00000003",
        "isProtectServerTermination": false,
        "portForwardingPublicIp": "106.10.34.243",
        "portForwardingExternalPort": 1027,
        "portForwardingInternalPort": 22,
        "zone": {
          "zoneNo": "3",
          "zoneName": "KR-2",
          "zoneCode": "KR-2",
          "zoneDescription": "평촌 zone",
          "regionNo": "1"
        },
        "region": {
          "regionNo": "1",
          "regionCode": "KR",
          "regionName": "Korea"
        },
        "baseBlockStorageDiskType": {
          "code": "NET",
          "codeName": "Network Storage"
        },
        "baseBlockStorageDiskDetailType": {
          "code": "SSD",
          "codeName": "SSD"
        },
        "internetLineType": {
          "code": "PUBLC",
          "codeName": "PUBLC"
        },
        "serverInstanceType": {
          "code": "STAND",
          "codeName": "Standard"
        },
        "userData": "",
        "initScriptNo": "",
        "accessControlGroupList": [
          {
            "accessControlGroupConfigurationNo": "214344",
            "accessControlGroupName": "rabbitmq-acg",
            "accessControlGroupDescription": "",
            "isDefault": false,
            "createDate": "2020-07-30T13:25:39+0900"
          }
        ],
        "instanceTagList": [
          
        ]
      }
    ]
  }
}
```



XML

```
<getServerInstanceListResponse>
  <requestId>0cf1cb98-6fdb-4064-804d-c1a14f903d9b</requestId>
  <returnCode>0</returnCode>
  <returnMessage>success</returnMessage>
  <totalRows>1</totalRows>
  <serverInstanceList>
    <serverInstance>
      <serverInstanceNo>1081368</serverInstanceNo>
      <serverName>s1673fa87a9b</serverName>
      <serverDescription></serverDescription>
      <cpuCount>1</cpuCount>
      <memorySize>1073741824</memorySize>
      <baseBlockStorageSize>53687091200</baseBlockStorageSize>
      <platformType>
        <code>LNX32</code>
        <codeName>Linux 32 Bit</codeName>
      </platformType>
      <loginKeyName>juhoon-kim</loginKeyName>
      <isFeeChargingMonitoring>false</isFeeChargingMonitoring>
      <publicIp></publicIp>
      <privateIp>10.41.19.248</privateIp>
      <serverImageName>centos-6.3-32</serverImageName>
      <serverInstanceStatus>
        <code>NSTOP</code>
        <codeName>Server normal stopped state</codeName>
      </serverInstanceStatus>
      <serverInstanceOperation>
        <code>NULL</code>
        <codeName>Server NULL OP</codeName>
      </serverInstanceOperation>
      <serverInstanceStatusName>stopped</serverInstanceStatusName>
      <createDate>2018-11-23T17:21:05+0900</createDate>
      <uptime>2018-11-23T17:36:40+0900</uptime>
      <serverImageProductCode>SPSW0LINUX000032</serverImageProductCode>
      <serverProductCode>SPSVRSTAND000056</serverProductCode>
      <isProtectServerTermination>false</isProtectServerTermination>
      <portForwardingPublicIp>106.10.51.90</portForwardingPublicIp>
      <zone>
        <zoneNo>3</zoneNo>
        <zoneName>KR-2</zoneName>
        <zoneCode>KR-2</zoneCode>
        <zoneDescription>평촌 zone</zoneDescription>
        <regionNo>1</regionNo>
      </zone>
      <region>
        <regionNo>1</regionNo>
        <regionCode>KR</regionCode>
        <regionName>Korea</regionName>
      </region>
      <baseBlockStorageDiskType>
        <code>NET</code>
        <codeName>Network Storage</codeName>
      </baseBlockStorageDiskType>
      <baseBlockStorageDiskDetailType>
        <code>HDD</code>
        <codeName>HDD</codeName>
      </baseBlockStorageDiskDetailType>
      <internetLineType>
        <code>PUBLC</code>
        <codeName>PUBLC</codeName>
      </internetLineType>
      <serverInstanceType>
        <code>MICRO</code>
        <codeName>Micro Server</codeName>
      </serverInstanceType>
      <userData></userData>
      <initScriptNo></initScriptNo>
      <accessControlGroupList>
        <accessControlGroup>
          <accessControlGroupConfigurationNo>39995</accessControlGroupConfigurationNo>
          <accessControlGroupName>ncloud-default-acg</accessControlGroupName>
          <accessControlGroupDescription>Default AccessControlGroup</accessControlGroupDescription>
          <isDefault>true</isDefault>
          <createDate>2018-04-09T11:12:54+0900</createDate>
        </accessControlGroup>
      </accessControlGroupList>
      <instanceTagList/>
    </serverInstance>
  </serverInstanceList>
</getServerInstanceListResponse>

```

