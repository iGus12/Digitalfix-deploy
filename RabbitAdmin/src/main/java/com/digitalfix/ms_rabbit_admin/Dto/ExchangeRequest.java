package com.digitalfix.ms_rabbit_admin.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ExchangeRequest(

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]{1,100}$",
        message = "El nombre contiene caracteres no permitidos"
    )
    String name,

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
        regexp = "^(direct|topic|fanout|headers)$",
        flags = Pattern.Flag.CASE_INSENSITIVE,
        message = "El tipo debe ser direct, topic, fanout o headers"
    )
    String type,

    boolean durable,
    boolean autoDelete

) {}