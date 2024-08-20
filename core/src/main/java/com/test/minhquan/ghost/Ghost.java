package com.test.minhquan.ghost;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.minhquan.player.Player;

public class Ghost {
    private Vector2 position;
    private Vector2 velocity;

    private float mapWidth;
    private float mapHeight;
    private float width;
    private float height;
    private TextureAtlas atlas;
    private Animation animation;
    private float elapsedTime;

    private float x = 50;
    private float visionRange;
    private float speed;
    private Player player;

    private int health;
    private boolean isAlive;
    private float damageCooldown;
    private float timeSinceLastHit;

    public Ghost() {
    }

    public Ghost(TextureAtlas atlas, Vector2 position, float mapWidth, float mapHeight, float elapsedTime,
            Player player) {
        this.atlas = atlas;
        this.position = position;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.elapsedTime = elapsedTime;
        this.velocity = new Vector2(0, 0);
        this.player = player;
        this.speed = 50f;
        this.visionRange = 100f;
        this.health = 100;
        this.width = 30;
        this.height = 30;
        this.isAlive = true;
        this.damageCooldown = 1.0f;
        this.timeSinceLastHit = 0f;
    }

    public void update(float deltaTime, MapObjects objects) {

        Vector2 direction = new Vector2(0, 0);
        timeSinceLastHit += deltaTime;
        if(isAlive() && player.getBoundingRectangle().overlaps(getBoudingRectangle())){
            if(timeSinceLastHit >= damageCooldown){
                player.takeDamage(10);
                timeSinceLastHit = 0f;
                System.out.println("Player:" + player.getHealth());
            }
        }

        if (player.getPosition().dst(position) <= visionRange) {
            float distanceToPlayer = player.getPosition().dst(position);
            float safeDistance = 30.0f;

            if (distanceToPlayer >= safeDistance) {
                direction = player.getPosition().cpy().sub(position).nor();
                velocity = direction.scl(speed);
                position.mulAdd(velocity, deltaTime);
            } else {
                setVelocity(new Vector2(0, 0));

            }
        } else {
            Vector2 newPosition = position.cpy().mulAdd(velocity, deltaTime);
            if (!checkCollision(newPosition, objects)) {
                position.set(newPosition);

            } else if (checkCollision(newPosition, objects)) {
                x = -x;
            }
            if (position.x < 0) {
                x = -x;
            }

            if (position.x + width > mapWidth) {
                x = -x;
            }

            setVelocity(new Vector2(x, 0));

        }

    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void draw(SpriteBatch sb, float elapsedTime) {
        animation = new Animation<>(0.7f, atlas.findRegions("ghost"), Animation.PlayMode.LOOP);
        TextureRegion currentFrame = (TextureRegion) animation.getKeyFrame(elapsedTime, true);
        sb.draw(currentFrame, position.x, position.y, width,height);
    }

    public boolean checkCollision(Vector2 newPosition, MapObjects objects) {
        Rectangle newRect = new Rectangle(newPosition.x, newPosition.y, width, height);
        for (RectangleMapObject rectObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rect = rectObject.getRectangle();
            if (newRect.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getBoudingRectangle() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            System.out.println("ghost defeated");
            isAlive = false;
        }
    }
    public boolean isAlive(){
        return isAlive;
    }

    public int getHealth() {
        return health;
    }
    public void dispose(){
        atlas.dispose();
    }
}
