package com.navercorp.RabbitMQMonitoring.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CLUSTER_NOT_EXIST(400,"C001","해당 클러스터가 존재하지 않습니다."),
    CANNOT_ALLOW_REQUEST(400,"R001","잘못된 요청입니다."),
    CANNOT_ALLOW_METHOD(405,"R002","다른 작업이 진행 중입니다."),
    VALUE_NOT_EXIST(400,"E001","존재하지 않는 값입니다."),
    CANNOT_CREATE_LOADBALANCER(405,"L001","이미 로드밸런서가 존재합니다."),
    CANNOT_DELETE_LOADBALANCER(405,"L002","로드 밸런서가 존재하지 않습니다."),
    CANNOT_DO_SCALEIN(405,"S001","더 이상 Scale In을 할 수 없습니다. 노드가 최소 2개는 존재해야 합니다."),
    CANNOT_DO_SCALEOUT(405,"S002","더 이상 Scale Out을 할 수 없습니다. 노드는 최대 5개까지 가능합니다."),
    SCALEIN_IS_RUNNING(405,"S003","현재 해당 클러스터에서 ScaleIn 작업이 진행 중입니다."),
    SCALEOUT_IS_RUNNING(405,"S004","현재 해당 클러스터에서 ScaleOut 작업이 진행 중입니다."),
    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}