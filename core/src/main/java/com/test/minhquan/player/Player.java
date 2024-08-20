package com.test.minhquan.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.minhquan.ghost.Ghost;
import com.test.minhquan.monster.Monster;

public class Player {
    private Vector2 position;
    private Vector2 velocity;

    
    private float width;
    private float height;
    private float mapWidth;
    private float mapHeight;

    
    private TextureAtlas atlas;
    private Animation anim;
    private float elapsedTime;
    private String status;

    private boolean sword = false;

    private int health;
    private boolean isAlive;

    private Monster monster;
    private Ghost ghost;
    public Player() {
    }

    public Player(TextureAtlas atlas, Vector2 position, float width, float height, float mapWidth, float mapHeight,
            float elapsedTime, String status) {
        this.atlas = atlas;
        this.position = position;
        this.velocity = new Vector2(0, 0);
        this.width = width;
        this.height = height;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.elapsedTime = elapsedTime;
        this.status = status;
        this.health = 100;
        this.isAlive = true;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void update(float deltaTime, MapObjects objects) {
        Vector2 newPosition = position.cpy().mulAdd(velocity, deltaTime);
        if(!checkCollision(newPosition, objects)){
            position.set(newPosition);
        }
        if (position.x < 0) {
            position.x = 0;
        }
        if (position.y < 0) {
            position.y = 0;
        }
        if (position.x + width > mapWidth) {
            position.x = mapWidth - width;
        }
        if (position.y + height > mapHeight) {
            position.y = mapHeight - height;
        }
    }

    public void draw(SpriteBatch sb, float elapsedTime) {
        anim = new Animation<>(0.7f, atlas.findRegions(status), Animation.PlayMode.LOOP);
        TextureRegion currentFrame = (TextureRegion) anim.getKeyFrame(elapsedTime, true);
        sb.draw(currentFrame, position.x, position.y);
    }
    public void setAtlas(TextureAtlas newAtlas){
        this.atlas = newAtlas;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getPosition() {
        return position; 
    }

    public void dispose() {

    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    private boolean checkCollision(Vector2 newPosition, MapObjects objects) {
        Rectangle newRect = new Rectangle(newPosition.x, newPosition.y, width, height);

        for (RectangleMapObject rectObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rect = rectObject.getRectangle();
            Integer objectID = rectObject.getProperties().get("id", Integer.class);
            if (newRect.overlaps(rect)) {
                if(objectID == 93){
                    sword = true;
                }
                return true;
            }
        }
        return false;
    }
    public boolean checkSword(){
        return sword;
    }
    public void takeDamage(int damage){
        health -= damage;
        if(health <=0){
            isAlive = false;
        }
    }

    public boolean isAlive(){
        return isAlive;
    }

    public void setMonster(Monster monster){
        this.monster = monster;
    }

    public void createAttackHitbox(int x, int y){
        Rectangle attackHitBox = new Rectangle(getPosition().x + x, getPosition().y + y,50,50);
        if(attackHitBox.overlaps(monster.getBoudingRectangle())){
            monster.takeDamage(10);
            System.out.println("monster health: " + monster.getHealth());
        }else if (attackHitBox.overlaps(ghost.getBoudingRectangle())){
            ghost.takeDamage(10);
            System.out.println("ghost health: " + ghost.getHealth());
        }

    }
    public int getHealth(){
        return health;
    }
    public void setGhost(Ghost ghost){
        this.ghost = ghost;
    }
}
