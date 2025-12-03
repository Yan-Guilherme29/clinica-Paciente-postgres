module org.clinicapaciente {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;


    opens org.clinicapaciente.controller to javafx.fxml;
    opens org.clinicapaciente.model to javafx.base;
    opens org.clinicapaciente.dao to javafx.fxml;
    opens org.clinicapaciente to javafx.fxml;
    exports org.clinicapaciente;
    exports org.clinicapaciente.dao to javafx.fxml;
    exports org.clinicapaciente.controller to javafx.fxml;
}