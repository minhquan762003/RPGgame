package com.test.minhquan.monster;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.minhquan.player.Player;

public class Monster {
    private Vector2 position;
    private Vector2 velocity;
    private float mapWidth;
    private float mapHeight;
    private float width;
    private float height;

    private TextureAtlas atlas;
    private Animation animation;
    private float elapsedTime;

    private int x = 50;
    private String status = "right";
    private float visionRange;
    private float speed = 50f;
    private Player player;

    private int health;
    private boolean isAlive;
    
    private float damageCooldown;
    private float timeSinceLastHit; 
    public Monster() {
    }

    public Monster(TextureAtlas atlas, Vector2 position, float mapWidth, float mapHeight,
            float elapsedTime, Player player) {
        this.atlas = atlas;
        this.position = position;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.elapsedTime = elapsedTime;
        this.velocity = new Vector2(0, 0);
        this.visionRange = 100f;
        this.player = player;
        this.health = 100;
        this.isAlive = true;
        this.width = 30;
        this.height = 30;
        this.damageCooldown = 1.0f; // Thời gian chờ là 2 giây
        this.timeSinceLastHit = 0f; 
    }

    public void update(float deltaTime, MapObjects objects) {
        timeSinceLastHit += deltaTime;

        Vector2 direction = new Vector2(0, 0);

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

            if(distanceToPlayer >= safeDistance){
                direction = player.getPosition().cpy().sub(position).nor();
                velocity = direction.scl(speed);
                Vector2 newposition = position.cpy().mulAdd(velocity, deltaTime);
                
                if(position.x > newposition.x){
                    setStatus("left");
                } 
                else if(position.x < newposition.x){
                    setStatus("right");
                }
                if(position.y > newposition.y){
                    
                    setStatus("front");
                }else if(position.y < newposition.y){
                    setStatus("behind");
                }
                position.set(newposition);
            }else{
                setVelocity(new Vector2(0,0));
                setStatus("right");
            }


        } else {
            Vector2 newPosition = position.cpy().mulAdd(velocity, deltaTime);

            if (!checkCollision(newPosition, objects)) {
                position.set(newPosition);

            } else if (checkCollision(newPosition, objects)) {
                x = -x;
                if (x < 0) {
                    setStatus("left");
                } else {
                    setStatus("right");
                }
            }
            if (position.x < 0) {
                x = -x;
                setStatus("right");
            }

            if (position.x + width > mapWidth) {
                x = -x;
                setStatus("left");
            }
            setVelocity(new Vector2(x, 0));

        }

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void draw(SpriteBatch sb, float elapsedTime) {
        animation = new Animation<>(0.7f, atlas.findRegions(status), Animation.PlayMode.LOOP);
        TextureRegion currentFrame = (TextureRegion) animation.getKeyFrame(elapsedTime, true);
        sb.draw(currentFrame, position.x, position.y,width,height);
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

    public void takeDamage(int damage){
        health -= damage;
        if(health<= 0){
            System.out.println("monster defeated");
            isAlive = false;
        }
    }

    public Vector2 getPosition(){
        return position;
    }

    public int getHealth(){
        return health;
    }

    public boolean isAlive(){
        return isAlive;
    }
    
    public Rectangle getBoudingRectangle(){
        return new Rectangle(position.x, position.y, width,height);
    }

    public void dispose(){
        atlas.dispose();
    }
}
