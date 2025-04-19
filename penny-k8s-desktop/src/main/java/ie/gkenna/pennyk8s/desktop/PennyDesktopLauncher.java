package ie.gkenna.pennyk8s.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import ie.gkenna.pennyk8s.backend.PennyK8sApplication;

/**
 * Desktop launcher that embeds spring backend
 * and displays the Vue frontend in a JavaFX WebView.
 */
public class PennyDesktopLauncher extends Application {

    private ConfigurableApplicationContext springContext;

    /**
     * Called before {@link #start(Stage)}.
     * spins up the spring context
     */
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder()
                .sources(PennyK8sApplication.class)  //
                .run();
    }

    /**
     * Start the JavaFX UI.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Penny K8s Dashboard");

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.load("http://localhost:8080/");

        BorderPane root = new BorderPane(webView);
        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setScene(scene);
        primaryStage.show();

        // when the window closes, shut everything down:
        primaryStage.setOnCloseRequest(evt -> {
            // first close JavaFX
            Platform.exit();
            // then close Spring
            if (springContext != null) {
                springContext.close();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
