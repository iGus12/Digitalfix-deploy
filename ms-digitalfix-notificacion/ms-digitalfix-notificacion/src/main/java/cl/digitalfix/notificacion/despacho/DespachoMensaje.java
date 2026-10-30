package cl.digitalfix.notificacion.despacho;

public record DespachoMensaje(
        String tecnicoId,
        String ordenId,
        String direccion,
        String detalle
) {}
