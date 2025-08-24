package com.bartoszkorec.warehouse_management.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Embeddable
@Data
@NoArgsConstructor
public class Authority implements GrantedAuthority {

    private String authority;
}
