package com.kevien.damico.auth.model.dto;

public record UserDto(String firstName, String lastName, String email, String username, String address, String phoneNumber, boolean verified, boolean activated) {

}
