package com.kevien.damico.auth.model.dto;

public record UserRequest(String firstName, String lastName, String email, String username, String password, String address, String phoneNumber, boolean verified, boolean activated) {

}
