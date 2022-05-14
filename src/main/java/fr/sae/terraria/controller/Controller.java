package fr.sae.terraria.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.util.ResourceBundle;

import java.net.URL;

import fr.sae.terraria.vue.TileMapsView;
import fr.sae.terraria.modele.Environment;
import fr.sae.terraria.modele.TileMaps;


public class Controller implements Initializable
{
    // Constantes
    private static final int displayRenderingWidth = 465;
    private static final int displayRenderingHeight = 256;

    // FXML objects
    @FXML
    private BorderPane root;
    @FXML
    private HBox title;
    @FXML
    private Pane display;

    // Property variables
    private final IntegerProperty tileWidth;
    private final IntegerProperty tileHeight;

    // Local Object variable
    private Environment environment;
    private TileMaps tileMaps;


    public Controller(Stage primaryStage)
    {
        this.tileWidth = new SimpleIntegerProperty();
        this.tileHeight = new SimpleIntegerProperty();
        this.tileMaps = new TileMaps();

        this.addKeysEventListener(primaryStage);

        // Listener pour sync la taille des tiles pour scale les tiles
        primaryStage.heightProperty().addListener((obs, oldV, newV) -> tileHeight.setValue((int) (TileMaps.TILE_DEFAULT_SIZE *((newV.intValue()-title.getPrefHeight()) / displayRenderingHeight))));
        primaryStage.widthProperty().addListener((obs, oldV, newV) -> tileWidth.setValue((int) (TileMaps.TILE_DEFAULT_SIZE *((newV.intValue() / displayRenderingWidth)))));
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        this.tileMaps.load("src/main/resources/fr/sae/terraria/maps/map_0.json");

        // La taille des tiles apres le scaling
        this.tileHeight.setValue((int) (TileMaps.TILE_DEFAULT_SIZE *((root.getPrefHeight()-title.getPrefHeight()) / 256)));
        this.tileWidth.setValue((int) (TileMaps.TILE_DEFAULT_SIZE *((root.getPrefWidth() / 465))));

        int widthGame = this.tileMaps.getWidth()*tileWidth.get();
        int heightGame = this.tileMaps.getHeight()*tileHeight.get();
        this.environment = new Environment(tileMaps, widthGame, heightGame, this.tileWidth.get(), this.tileHeight.get());

        TileMapsView tileMapsView = new TileMapsView(environment, display, tileWidth, tileHeight);
        tileMapsView.displayMaps(tileMaps);
        tileMapsView.displayPlayer();
    }

    /**
     * Ajoute la fonctionnalité des listeners des event du clavier
     * @param stage Specifie l'application
     */
    private void addKeysEventListener(Stage stage)
    {
        stage.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            this.environment.getKeysInput().put(key.getCode(), true);
            key.consume();
        });

        stage.addEventFilter(KeyEvent.KEY_RELEASED, key -> {
            this.environment.getKeysInput().put(key.getCode(), false);
            key.consume();
        });
    }
}