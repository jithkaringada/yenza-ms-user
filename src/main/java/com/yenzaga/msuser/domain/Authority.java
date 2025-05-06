package com.yenzaga.msuser.domain;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/*
 * An authority (a security role) used by Spring Security
 */
public class Authority implements GrantedAuthority {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 50)
    @Id
    private String authority;

    public Authority() {}

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() { return authority; }

    public void setName(String name) { this.authority = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Authority authority = (Authority) o;
        return Objects.equals(authority, authority.authority);
    }

    @Override
    public int hashCode() {
        return authority != null ? authority.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Authority{" + "authority='" + authority + '\'' + '}';
    }
}
