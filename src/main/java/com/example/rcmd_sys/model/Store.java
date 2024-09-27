package com.example.rcmd_sys.model;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;

@Entity
@Table(name = "stores") // stores 테이블과 매핑
@NoArgsConstructor(access= AccessLevel.PROTECTED) //기본생성자
@Getter
@AllArgsConstructor // 모든 필드를 초기화하는 생성자
@Builder // 빌더 패턴
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId", updatable = false, unique = true)
    private Long storeId;

    @Column(name = "name",updatable = false)
    String name;

    //이미지 소스
    @Column(name = "imgSrc",updatable = false,nullable = true, columnDefinition = "TEXT")
    String imgSrc;

    @Column(name = "address",updatable = false,nullable = true)
    String address;


    @Column(name = "mapX",updatable = false,nullable = true)
    double mapX;

    @Column(name = "mapY",updatable = false,nullable = true)
    double mapY;

    //유모차 대여여부
    @Column(name = "stroller",updatable = false,nullable = true)
    Boolean stroller;

    //유모차 편의성
    @Column(name = "strollerVal",updatable = false,nullable = true)
    Boolean strollerVal;

    //아이 스페어 체어
    @Column(name = "babySpareChair",updatable = false,nullable = true)
    Boolean babySpareChair;

    //아이 놀이방
    @Column(name = "playground",updatable = false,nullable = true)
    Boolean playground;

    @Column(name = "noKidsZone",updatable = false,nullable = true)
    String noKidsZone;

    @Column(name = "nokidsdetail",updatable = false,nullable = true)
    String nokidsdetail;

    @Column(name = "categoryId",updatable = false,nullable = true)
    Integer categoryId;

    @Column(name = "operationTime",updatable = false,nullable = true , length = 1024)
    String operationTime;

    @Column(name = "tel",updatable = false,nullable = true)
    String tel;

    @Column(name = "pet",updatable = false,nullable = true)
    Boolean pet;

    @Column(name = "parking",updatable = false,nullable = true)
    Boolean parking;

    @Column(name = "checkin",updatable = false,nullable = true)
    String checkin;

    @Column(name = "checkout",updatable = false,nullable = true)
    String checkout;

    @Column(name = "bookmarks")
    int noBmk;

    @Column
    int rcmdType;

    @Column(name = "rcmd")
    boolean rcmd;

}