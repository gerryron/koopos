package com.gerryron.kooposservice.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String firstName;
    private String lastName;
    @Column(nullable = false)
    private String phoneNumber;
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
