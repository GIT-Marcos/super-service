module navegador2.SPRService {
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;
    requires jbcrypt;
    requires org.postgresql.jdbc;
    requires org.apache.poi.ooxml;
    requires com.github.librepdf.openpdf;
    requires org.jfree.jfreechart;
    requires com.google.guice;
    requires com.google.guice.extensions.persist;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.desktop;

    // Abre paquetes a FXML/FX Base para reflexión
    opens SPRService.SPRService to javafx.fxml;
    opens SPRService.SPRService.controllers to javafx.fxml, com.google.guice;
    opens SPRService.SPRService.components to javafx.fxml;
    opens SPRService.SPRService.entities to org.hibernate.orm.core;
    opens SPRService.SPRService.DTOs to javafx.base;
    opens SPRService.SPRService.viewModels to javafx.base;
    opens SPRService.SPRService.DAOs.impl to com.google.guice;

    // Exporta lo que deba ser visible para otros módulos (incluido Scene Builder)
    exports SPRService.SPRService;
    exports SPRService.SPRService.DTOs;
    exports SPRService.SPRService.exceptions;
    exports SPRService.SPRService.entities;
    exports SPRService.SPRService.services;
    exports SPRService.SPRService.enums;
    exports SPRService.SPRService.navigation;
    exports SPRService.SPRService.controllers;
    exports SPRService.SPRService.components;
    exports SPRService.SPRService.util;
    exports SPRService.SPRService.viewModels;
    exports SPRService.SPRService.services.impl;
    exports SPRService.SPRService.DAOs;
    exports SPRService.SPRService.DAOs.impl;
    exports SPRService.SPRService.util.persistence;
    exports SPRService.SPRService.viewModels.tablas;
    opens SPRService.SPRService.viewModels.tablas to javafx.base;
    exports SPRService.SPRService.DTOs.filtros;
    opens SPRService.SPRService.DTOs.filtros to javafx.base;
    exports SPRService.SPRService.viewModels.celdas;
    opens SPRService.SPRService.viewModels.celdas to javafx.base;
    exports SPRService.SPRService.controllers.charts;
    opens SPRService.SPRService.controllers.charts to com.google.guice, javafx.fxml;

}