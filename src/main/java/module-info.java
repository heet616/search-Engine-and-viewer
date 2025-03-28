module heet.researchSearchEngine {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;

    opens heet.researchSearchEngine to javafx.fxml;
    exports heet.researchSearchEngine;
    exports heet.researchSearchEngine.Models;
    opens heet.researchSearchEngine.Models to javafx.fxml;
    exports heet.researchSearchEngine.Repository;
    opens heet.researchSearchEngine.Repository to javafx.fxml;
    exports heet.researchSearchEngine.Utils;
    opens heet.researchSearchEngine.Utils to javafx.fxml;
    exports heet.researchSearchEngine.Controller;
    opens heet.researchSearchEngine.Controller to javafx.fxml;
    exports heet.researchSearchEngine.View;
    opens heet.researchSearchEngine.View to javafx.fxml;
}