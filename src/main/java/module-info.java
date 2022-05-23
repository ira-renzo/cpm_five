module com.example.cpm_five {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    opens com.example.cpm_five to javafx.fxml;
    exports com.example.cpm_five;
}