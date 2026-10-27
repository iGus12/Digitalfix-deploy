package com.digitalfix.ms_rabbit_admin.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record QueueRequest(

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]{1,100}$",
        message = "El nombre contiene caracteres no permitidos"
    )
    String name

) {}