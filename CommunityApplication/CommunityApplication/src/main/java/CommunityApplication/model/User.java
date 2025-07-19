package CommunityApplication.model;

import CommunityApplication.DTO.UploadPostResponseDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String phoneNumber;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String gender;
    private String maritalStatus;
    private LocalDate dob;
    private float height;
    private Integer weight;
    private String bloodGroup;
    private String fatherName;
    private String address;
    private Long emergencyContact;




}
