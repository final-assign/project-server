package org.example.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private UserType type;
    private String schoolId;
    private String password;
    private String name;
    private int balance;
}
