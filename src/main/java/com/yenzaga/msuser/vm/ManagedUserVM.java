package com.yenzaga.msuser.vm;


import com.yenzaga.msuser.validator.ValidPassword;

public class ManagedUserVM extends UserDTO {
    @ValidPassword
    private String password;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" +
                "} " + super.toString();
    }
}
