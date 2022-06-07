package fr.sae.terraria.modele.entities.player;

import fr.sae.terraria.modele.Environment;
import fr.sae.terraria.modele.entities.entity.*;
import fr.sae.terraria.modele.entities.player.inventory.Inventory;
import fr.sae.terraria.vue.View;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;


public class Player extends Entity implements CollideObjectType, MovableObjectType, CollapsibleObjectType, SpawnableObjectType
{
    public static final int BREAK_BLOCK_DISTANCE = 1;

    private final EnumMap<KeyCode, Boolean> keysInput;
    private final EnumMap<MouseButton, Boolean> mouseInput;

    private final ObjectProperty objectWasPickup;

    private StowableObjectType itemSelected;

    private final Environment environment;

    private final Inventory inventory;


    public Player(final Environment environment)
    {
        super();
        this.environment = environment;
        this.inventory = new Inventory(this);

        this.animation = new Animation();
        this.keysInput = new EnumMap<>(KeyCode.class);
        this.mouseInput = new EnumMap<>(MouseButton.class);

        this.objectWasPickup = new SimpleObjectProperty(null);
        this.objectWasPickup.addListener((obs, oldObject, newObject) -> this.inventory.put((StowableObjectType) newObject));
    }

    @Override public void updates()
    {
        // Applique les déplacements selon les valeurs de l'offset
        // this.setX(this.x.get() + this.offset[0] * this.velocity);

        if (this.offset[1] == Entity.IDLE && !air) {
            this.gravity.xInit = this.x.get();
            this.gravity.yInit = this.y.get();
            this.gravity.vInit = this.velocity;
            this.gravity.degInit =  -90;

            this.gravity.timer = .0;
        }

        this.worldLimit();
        this.move();

        if (!Objects.isNull(this.rect))
            this.rect.updates(x.get(), y.get());
        this.animation.loop();
    }

    @Override public void move() { this.setX(this.getX() + this.offset[0] * this.getVelocity()); }

    @Override public void collide()
    {
        Map<String, Boolean> whereCollide = super.collide(this.environment);

        if (!whereCollide.isEmpty()) {
            if (whereCollide.get("left").equals(Boolean.TRUE) || whereCollide.get("right").equals(Boolean.TRUE))
                this.offset[0] = Entity.IDLE;
        }
    }

    @Override public void hit() { }

    @Override public void spawn(int x, int y)
    {
        this.setX(x);
        this.setY(y);

        Image image = View.loadAnImage("sprites/player/player_idle.png", environment.scaleMultiplicatorWidth, environment.scaleMultiplicatorHeight);
        this.setRect((int) image.getWidth(), (int) image.getHeight());
        image.cancel();

        this.getGravity().setXInit(x);
        this.getGravity().setYInit(y);
    }

    @Override public void moveRight() { super.moveRight(); }
    @Override public void moveLeft() { super.moveLeft(); }
    @Override public void jump() { super.jump(); }
    @Override public void fall() { super.fall(); }

    @Override public void worldLimit()
    {
        if (super.worldLimit(this.environment))
            this.offset[0] = Entity.IDLE;
    }

    /** Lie les inputs au clavier à une ou des actions. */
    public void eventInput()
    {
        this.inventory.eventInput();

        this.keysInput.forEach((key, value) -> {
            if (Boolean.TRUE.equals(value)) {
                if (key == KeyCode.Z || key == KeyCode.SPACE)
                    if (this.offset[1] != Entity.IS_FALLING) this.jump();

                if (key == KeyCode.D)
                    this.moveRight();
                else if (key == KeyCode.Q)
                    this.moveLeft();
            }
        });
    }

    public void pickup(StowableObjectType pickupObject) { this.objectWasPickup.set(pickupObject); }


    public Map<MouseButton, Boolean> getMouseInput() { return this.mouseInput; }
    public Map<KeyCode, Boolean> getKeysInput() { return this.keysInput; }
    public StowableObjectType getItemSelected() { return this.itemSelected; }
    public Inventory getInventory() { return this.inventory; }

    public void setItemSelected(StowableObjectType itemSelected) { this.itemSelected = itemSelected; }
}
