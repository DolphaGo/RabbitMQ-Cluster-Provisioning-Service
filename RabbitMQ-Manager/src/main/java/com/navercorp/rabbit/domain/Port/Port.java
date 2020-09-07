package com.navercorp.rabbit.domain.Port;

import com.navercorp.rabbit.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Port extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portId;

    private String portNum;

    private boolean isAllocated;

    @Builder
    public Port(String portNum, boolean isAllocated) {
        this.portNum = portNum;
        this.isAllocated = isAllocated;
    }
}
