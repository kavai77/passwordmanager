package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Password {
    @Id
    private Long id;
    @Index
    private String userId;
    @Index
    private String domain;
    private String userName;
    private String hex;
    private String iv;
    private Date created;
    private Date modified;
}
