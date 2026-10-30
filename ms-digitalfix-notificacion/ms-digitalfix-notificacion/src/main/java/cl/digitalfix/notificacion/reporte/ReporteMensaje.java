package cl.digitalfix.notificacion.reporte;

public record ReporteMensaje(
        String ordenId,
        String tipoReporte,
        String correoCliente
) {}
