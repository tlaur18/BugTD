package dk.sdu.mmmi.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import dk.sdu.mmmi.cbse.Game;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.entityparts.AnimationPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.SpritePart;
import dk.sdu.mmmi.cbse.common.events.Event;
import dk.sdu.mmmi.cbse.common.events.GameOverEvent;
import dk.sdu.mmmi.cbse.common.events.GameWonEvent;
import dk.sdu.mmmi.gui.GuiPluginService;
import dk.sdu.mmmi.gui.input.GameInputProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GameScreen implements Screen {

    Stage stage;
    private float elapsedTime = 0f;
    SpriteBatch spriteBatch;
    SpriteBatch animationBatch;
    private static final AssetManager assetManager = new AssetManager();
    private Animation animation;
    private TextureAtlas textureAtlas;

    public GameScreen() {
        stage = new Stage();
        stage = new Stage();
        spriteBatch = new SpriteBatch();
        animationBatch = new SpriteBatch();
        loadAssets();
        loadAnimations();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputProcessor(Game.getInstance().getGameData()));
        Game.getInstance().restart(Arrays.asList(GuiPluginService.getInstance()));
    }

    @Override
    public void render(float v) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        // clear screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        draw();
        Game.getInstance().getGameData().setDelta(Gdx.graphics.getDeltaTime());
        Game.getInstance().getGameData().getKeys().update();
        Game.getInstance().update(); //TODO: Should this be in render? From Thomas: Yes I think it fits here inside the GameScreen's render method
        // Is this the right place to put the game over check? Yes it probably is
        if(!Game.getInstance().getGameData().getEvents(GameWonEvent.class).isEmpty()){
            for (Event gameWonEvent : Game.getInstance().getGameData().getEvents(GameWonEvent.class)) {
                Game.getInstance().getGameData().removeEvent(gameWonEvent);
                
            }
            GuiPluginService.getInstance().setScreen(new MenuScreen());
        }
        if (!Game.getInstance().getGameData().getEvents(GameOverEvent.class).isEmpty()) {
            // TODO: Show some UI that informs the user that the game is over
            for (Event gameOverEvent : Game.getInstance().getGameData().getEvents(GameOverEvent.class)) {
                Game.getInstance().getGameData().removeEvent(gameOverEvent);
            }
            
            GuiPluginService.getInstance().setScreen(new MenuScreen());
        }
    }

    private void draw() {
        ArrayList<Entity> entitiesToDraw = new ArrayList<>();

        // Populate list
        for (Entity entity : Game.getInstance().getWorld().getEntities()) {
            SpritePart spritePart = entity.getPart(SpritePart.class);
            PositionPart positionPart = entity.getPart(PositionPart.class);

            if (spritePart != null && positionPart != null) {
                entitiesToDraw.add(entity);
            }
        }

        // Sort by layer
        entitiesToDraw.sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                SpritePart spritePart1 = e1.getPart(SpritePart.class);
                SpritePart spritePart2 = e2.getPart(SpritePart.class);
                return spritePart1.getLayer() - spritePart2.getLayer();
            }
        });

        // Draw
        for (Entity entity : entitiesToDraw) {
            SpritePart spritePart = entity.getPart(SpritePart.class);
            PositionPart positionPart = entity.getPart(PositionPart.class);

            drawSprite(spritePart, positionPart);
        }

        loadAnimations();
        ArrayList<Entity> entitiesToAnimate = new ArrayList<>();

        // Populate list
        for (Entity entity : Game.getInstance().getWorld().getEntities()) {
            AnimationPart animationPart = entity.getPart(AnimationPart.class);
            PositionPart positionPart = entity.getPart(PositionPart.class);
            // System.out.println(textureAtlas.getRegions());

            if (animationPart != null && positionPart != null) {
                entitiesToAnimate.add(entity);
            }
        }

        // Draw
        for (Entity entity : entitiesToAnimate) {
            AnimationPart animationPart = entity.getPart(AnimationPart.class);
            PositionPart positionPart = entity.getPart(PositionPart.class);

            drawAnimation(animationPart, positionPart);
        }

    }

    private void drawAnimation(AnimationPart anima, PositionPart pos) {
        spriteBatch.begin();
        spriteBatch.draw(animation.getKeyFrame(elapsedTime, true), pos.getX(), pos.getY());
        spriteBatch.end();
    }

    private void drawSprite(SpritePart spritePart, PositionPart positionPart) {
        animationBatch.begin();
        Texture texture = assetManager.get(spritePart.getSpritePath(), Texture.class);
        Sprite sprite = new Sprite(texture);
        sprite.rotate((float) Math.toDegrees(positionPart.getRadians()));
        sprite.setX(positionPart.getX());
        sprite.setY(positionPart.getY());
        sprite.setAlpha(spritePart.getAlpha());
        sprite.setSize(spritePart.getWidth(), spritePart.getHeight());
        sprite.draw(animationBatch);
        animationBatch.end();

    }

    public void loadAnimations() {
        for (Entity entity : Game.getInstance().getWorld().getEntities()) {
            AnimationPart animationPart = entity.getPart(AnimationPart.class);
            if (animationPart != null) {
                textureAtlas = new TextureAtlas(Gdx.files.internal(animationPart.getAtlasPath()));
                animation = new Animation(1f / 15f, textureAtlas.getRegions());
            }
        }
    }

    public void loadAssets() {

        for (Entity entity : Game.getInstance().getWorld().getEntities()) {
            SpritePart spritePart = entity.getPart(SpritePart.class);
            if (spritePart != null) {
                GameScreen.assetManager.load(spritePart.getSpritePath(), Texture.class);
                GameScreen.assetManager.update();
            }
        }
        GameScreen.assetManager.finishLoading();

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        animationBatch.dispose();
        textureAtlas.dispose();
    }
}
