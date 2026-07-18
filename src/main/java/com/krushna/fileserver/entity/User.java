package com.krushna.fileserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "tbl_user")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    private String role;

    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FileData> files;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.isBlank()) {
            return List.of();
        }

        String r = role.trim();
        if (r.regionMatches(true, 0, "ROLE_", 0, 5)) {
            r = r.substring(5);
        }
        String normalized = "ROLE_" + r.toUpperCase();
        return List.of(new SimpleGrantedAuthority(normalized));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
