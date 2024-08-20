package com.test.minhquan;



import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.test.minhquan.ghost.Ghost;
import com.test.minhquan.monster.Monster;
import com.test.minhquan.player.Player;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Test extends ApplicationAdapter implements InputProcessor {

    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float pwidth = 30;
    private float pheight = 30;
    private float mapWidth = 800;
    private float mapHeight = 600;
    private MapLayer collisionLayer;
    private MapObjects objects;

    private SpriteBatch sb;
    private TextureAtlas atlas;
    private float elapsedTime = 0f;

    private String defaultAnim = "hiepSiAnimation/defaultAnim_remake.txt";
    private String swordAnim = "hiepSiAnimation/swordAnim.txt";

    private Player player;
    private Ghost ghost;
    private TextureAtlas ghostAtlas;

    private Monster monster;
    private TextureAtlas monsterAtlas;

    private String currentStatus = "idle_front";

    private ShapeRenderer shapeRenderer;

    private int x, y;
    private Array<Monster> monsters;
    public Test() {
    }

    @Override
    public void create() {
        sb = new SpriteBatch();

        tiledMap = new TmxMapLoader().load("800x600Map.tmx");

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        camera = new OrthographicCamera();

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        atlas = new TextureAtlas(Gdx.files.internal(defaultAnim));
        player = new Player(atlas, new Vector2(0, 0), pwidth, pheight, mapWidth, mapHeight, elapsedTime, currentStatus);

        Gdx.input.setInputProcessor(this);

        monsterAtlas = new TextureAtlas(Gdx.files.internal("monsterAnimation/monsterAnimation.txt"));
        monster = new Monster(monsterAtlas, new Vector2(200, 200), mapWidth, mapHeight, elapsedTime, player);
        
        ghostAtlas = new TextureAtlas(Gdx.files.internal("ghostAnimation/ghostAnim.txt"));
        ghost = new Ghost(ghostAtlas, new Vector2(100, 100), mapWidth, mapHeight, elapsedTime, player);
        
        collisionLayer = tiledMap.getLayers().get("vaCham");
        objects = collisionLayer.getObjects();
        
        shapeRenderer = new ShapeRenderer();

        player.setMonster(monster);
        player.setGhost(ghost);

        monsters = new Array<Monster>();
        monsters.add(new Monster(monsterAtlas, new Vector2(300,200), mapWidth, mapHeight, elapsedTime, player));
        monsters.add(new Monster(monsterAtlas, new Vector2(400,200), mapWidth, mapHeight, elapsedTime, player));
        monsters.add(new Monster(monsterAtlas, new Vector2(500,200), mapWidth, mapHeight, elapsedTime, player));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        player.update(Gdx.graphics.getDeltaTime(), objects);

        // camera.position.set(player.getPosition().x, player.getPosition().y,0);

        camera.update();

        mapRenderer.setView(camera);

        mapRenderer.render();

        sb.begin();
        for (Monster monster : monsters){
            if(monster.isAlive()){
                monster.update(Gdx.graphics.getDeltaTime(), objects);
                monster.draw(sb, elapsedTime);
            }
        }

        
        if(monster.isAlive()){
            monster.update(Gdx.graphics.getDeltaTime(), objects);
            monster.draw(sb, elapsedTime);
        }
        if(ghost.isAlive()){
            ghost.update(Gdx.graphics.getDeltaTime(), objects);
            ghost.draw(sb, elapsedTime);
        }

    
        elapsedTime += Gdx.graphics.getDeltaTime();
        //game over
        if(player.isAlive()){
            player.draw(sb, elapsedTime);
        }

        sb.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Rectangle playerRect = player.getBoundingRectangle();
        shapeRenderer.setColor(0, 1, 0, 1);  // Màu xanh lá cây cho hitbox của Player
        shapeRenderer.rect(playerRect.x +x, playerRect.y + y, playerRect.width, playerRect.height);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        sb.dispose();
        tiledMap.dispose();
        player.dispose();
        mapRenderer.dispose();
        monster.dispose();
        ghost.dispose();
    
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.LEFT) {
            player.setStatus("left");
            player.setVelocity(new Vector2(-100, 0));
            x = -30;
            y = 0;
        } else if (keycode == Input.Keys.RIGHT) {
            player.setStatus("right");
            player.setVelocity(new Vector2(100, 0));
            x = 30;
            y = 0;
        } else if (keycode == Input.Keys.UP) {
            player.setStatus("behind");
            player.setVelocity(new Vector2(0, 100));
            x = 0;
            y = 30;
        } else if (keycode == Input.Keys.DOWN) {
            player.setStatus("front");
            player.setVelocity(new Vector2(0, -100));
            x = 0;
            y = -30;
        }
        else if (keycode == Input.Keys.E) {
            if (player.checkSword()) {
                atlas = new TextureAtlas(Gdx.files.internal(swordAnim));
                player.setAtlas(atlas);
            }
        }else if(keycode == Input.Keys.SPACE){
            player.createAttackHitbox(x, y);
        }
        return true;
    }


    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT ) {
            player.setVelocity(new Vector2(0, 0));
            player.setStatus("idle_left");
        }else if(keycode == Input.Keys.RIGHT){
            player.setVelocity(new Vector2(0, 0));
            player.setStatus("idle_right");
        }else if(keycode == Input.Keys.UP){
            player.setVelocity(new Vector2(0, 0));
            player.setStatus("idle_behind");
        }else if(keycode == Input.Keys.DOWN){
            player.setVelocity(new Vector2(0, 0));
            player.setStatus("idle_front");
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return true;
    }

}
