package ca.mcgill.ecse321.treeple;

import javax.annotation.PreDestroy;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.*;

import ca.mcgill.ecse321.treeple.controller.configuration.*;
import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

@SpringBootApplication
public class TreePLESpringApplication extends SpringBootServletInitializer {

    @Autowired
    private AndroidProperties androidProperties;

    @Autowired
    private WebFrontendProperties webFrontendProperties;

    private SQLiteJDBC sql;

    public static void main(String[] args) {
        SpringApplication.run(TreePLESpringApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Let the model matcher map corresponding fields by name
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
        modelMapper.getConfiguration().setSourceNamingConvention(NamingConventions.NONE).setDestinationNamingConvention(NamingConventions.NONE);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    // Enable CORS globally
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow web client
                String frontendUrl = "http://" + webFrontendProperties.getIp() + ":" + webFrontendProperties.getPort();
                // Allow android client
                String androidUrl = "http://" + androidProperties.getIp() + ":" + androidProperties.getPort();
                // For debug purposes, allow connecting  from localhost as well
                registry.addMapping("/**").allowedOrigins(frontendUrl, androidUrl, "http://localhost:8087", "http://127.0.0.1:8087");
            }
        };
    }

    @Bean
    @EventListener(ApplicationReadyEvent.class)
    public SQLiteJDBC ModelIdCountInitializer() {
        sql = new SQLiteJDBC();
        sql.connect();
        Tree.setNextTreeId(sql.getMaxTreeId() + 1);
        Location.setNextLocationId(sql.getMaxLocationId() + 1);
        SurveyReport.setNextReportId(sql.getMaxReportId() + 1);
        return sql;
    }

    @PreDestroy
    public void CloseSQLite() {
        if (sql != null)
            sql.closeConnection();
    }
}
