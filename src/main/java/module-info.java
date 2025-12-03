module org.clinicapaciente {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.naming;
    requires jakarta.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;

    opens org.clinicapaciente to javafx.fxml;
    opens org.clinicapaciente.controller to javafx.fxml;
    opens org.clinicapaciente.model to org.hibernate.orm.core, javafx.fxml; // importante abrir para Hibernate
    opens org.clinicapaciente.utils to org.hibernate.orm.core;

    exports org.clinicapaciente;
}
