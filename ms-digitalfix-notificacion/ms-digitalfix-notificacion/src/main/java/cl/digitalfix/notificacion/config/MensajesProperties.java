
package cl.digitalfix.notificacion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "digitalfix.mensajes")
public class MensajesProperties {

    private Exchanges exchanges = new Exchanges();
    private Colas colas = new Colas();
    private RoutingKeys routingKeys = new RoutingKeys();

    public Exchanges getExchanges() {
        return exchanges;
    }

    public void setExchanges(Exchanges exchanges) {
        this.exchanges = exchanges;
    }

    public Colas getColas() {
        return colas;
    }

    public void setColas(Colas colas) {
        this.colas = colas;
    }

    public RoutingKeys getRoutingKeys() {
        return routingKeys;
    }

    public void setRoutingKeys(RoutingKeys routingKeys) {
        this.routingKeys = routingKeys;
    }

    // Exchanges de RabbitMQ
    public static class Exchanges {

        private String directo;
        private String tema;
        private String deadLetter;

        public String getDirecto() {
            return directo;
        }

        public void setDirecto(String directo) {
            this.directo = directo;
        }

        public String getTema() {
            return tema;
        }

        public void setTema(String tema) {
            this.tema = tema;
        }

        public String getDeadLetter() {
            return deadLetter;
        }

        public void setDeadLetter(String deadLetter) {
            this.deadLetter = deadLetter;
        }
    }

    // Colas principales y colas de mensajes fallidos
    public static class Colas {

        private String gmail;
        private String gmailDlq;

        private String despacho;
        private String despachoDlq;

        private String reporte;
        private String reporteDlq;

        private String interna;
        private String internaDlq;

        public String getGmail() {
            return gmail;
        }

        public void setGmail(String gmail) {
            this.gmail = gmail;
        }

        public String getGmailDlq() {
            return gmailDlq;
        }

        public void setGmailDlq(String gmailDlq) {
            this.gmailDlq = gmailDlq;
        }

        public String getDespacho() {
            return despacho;
        }

        public void setDespacho(String despacho) {
            this.despacho = despacho;
        }

        public String getDespachoDlq() {
            return despachoDlq;
        }

        public void setDespachoDlq(String despachoDlq) {
            this.despachoDlq = despachoDlq;
        }

        public String getReporte() {
            return reporte;
        }

        public void setReporte(String reporte) {
            this.reporte = reporte;
        }

        public String getReporteDlq() {
            return reporteDlq;
        }

        public void setReporteDlq(String reporteDlq) {
            this.reporteDlq = reporteDlq;
        }

        public String getInterna() {
            return interna;
        }

        public void setInterna(String interna) {
            this.interna = interna;
        }

        // Cola de mensajes fallidos de notificaciones internas
        public String getInternaDlq() {
            return internaDlq;
        }

        public void setInternaDlq(String internaDlq) {
            this.internaDlq = internaDlq;
        }
    }

    // Claves de enrutamiento de RabbitMQ
    public static class RoutingKeys {

        private String gmailDirecta;
        private String gmailTema;
        private String gmailDlq;

        private String despachoDirecta;
        private String despachoTema;
        private String despachoDlq;

        private String reporteDirecta;
        private String reporteTema;
        private String reporteDlq;

        private String internaDirecta;
        private String internaDlq;

        public String getGmailDirecta() {
            return gmailDirecta;
        }

        public void setGmailDirecta(String gmailDirecta) {
            this.gmailDirecta = gmailDirecta;
        }

        public String getGmailTema() {
            return gmailTema;
        }

        public void setGmailTema(String gmailTema) {
            this.gmailTema = gmailTema;
        }

        public String getGmailDlq() {
            return gmailDlq;
        }

        public void setGmailDlq(String gmailDlq) {
            this.gmailDlq = gmailDlq;
        }

        public String getDespachoDirecta() {
            return despachoDirecta;
        }

        public void setDespachoDirecta(String despachoDirecta) {
            this.despachoDirecta = despachoDirecta;
        }

        public String getDespachoTema() {
            return despachoTema;
        }

        public void setDespachoTema(String despachoTema) {
            this.despachoTema = despachoTema;
        }

        public String getDespachoDlq() {
            return despachoDlq;
        }

        public void setDespachoDlq(String despachoDlq) {
            this.despachoDlq = despachoDlq;
        }

        public String getReporteDirecta() {
            return reporteDirecta;
        }

        public void setReporteDirecta(String reporteDirecta) {
            this.reporteDirecta = reporteDirecta;
        }

        public String getReporteTema() {
            return reporteTema;
        }

        public void setReporteTema(String reporteTema) {
            this.reporteTema = reporteTema;
        }

        public String getReporteDlq() {
            return reporteDlq;
        }

        public void setReporteDlq(String reporteDlq) {
            this.reporteDlq = reporteDlq;
        }

        public String getInternaDirecta() {
            return internaDirecta;
        }

        public void setInternaDirecta(String internaDirecta) {
            this.internaDirecta = internaDirecta;
        }

        // Routing key para mensajes fallidos internos
        public String getInternaDlq() {
            return internaDlq;
        }

        public void setInternaDlq(String internaDlq) {
            this.internaDlq = internaDlq;
        }
    }
}
