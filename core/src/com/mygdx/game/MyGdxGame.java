package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter
{
    public static int wndWidth = 640, wndHeight = 480;
    private SpriteBatch batch;
    private Animator animatorBatman, animatorCoin60;
    private float batmanScale, coin60Scale, zoom;
    private Lable lable;
    private int xStep,yStep;
    private List<Point> coinPoints;

    private TiledMap map;
    private OrthogonalTiledMapRenderer ortoMapRenderer;
    private OrthographicCamera ortoCamera;
    private Dimension batmanDimention;


    @Override
    public void create ()
    {
        xStep = 2;  yStep = 2;
        batch = new SpriteBatch();

        map = new TmxMapLoader().load ("maps/map1.tmx");
        //int tileWidth = (int)map.getProperties().get("tilewidth");
        //int tileHeight = (int)map.getProperties().get("tileheight");
        //int mapWidth = tileWidth * (int)map.getProperties().get("width");
        //int mapHeight = tileHeight * (int)map.getProperties().get("height");

        ortoMapRenderer = new OrthogonalTiledMapRenderer (map);
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();
        ortoCamera = new OrthographicCamera (viewportWidth, viewportHeight); //< Значения можно менять.

        zoom = ortoCamera.zoom = 1.5f;

        animatorBatman = new Animator ("runRight.png", 8, 1, 15, Animation.PlayMode.LOOP,
                                       0, 0, 256, 47, 8);
        batmanScale = 1.0f / zoom;
        xStep = (int)(xStep * batmanScale);
        yStep = (int)(yStep * batmanScale);
        batmanDimention = animatorBatman.getTileDimention();
        lable = new Lable ((int)(32 / zoom));


        //Никакой магии — объект 'точка' является RectangleMapObject с нулевыми размерами.
        MapObjects mapObjects = map.getLayers()
                                   .get ("Слой объектов 1")
                                   .getObjects();
        RectangleMapObject rmo = (RectangleMapObject) mapObjects.get ("Камера");
        ortoCamera.position.x = rmo.getRectangle().x;
        ortoCamera.position.y = rmo.getRectangle().y;
        ortoCamera.update(); //< Вызываем каждый раз после изменения камеры.

/*  На бэтмене не так заметно, а вот с монетками мы помучались…
    В редакторе карт координаты карты отсчитываются от ЛВУгла.
    RectangleMapObject.rectangle преобразует координаты карты так, чтобы они отсчитывались от ЛНУгла.
    На экране они отсчитываются от ЛНУгла.
    Камера.position, судя по всему, указывает на центр вьюпорта.
    … Блядь!!!!
*/
        Point screenOriginOffset = new Point((int)((rmo.getRectangle().x - viewportWidth * zoom / 2.0f)),
                                             (int)((rmo.getRectangle().y - viewportHeight * zoom / 2.0f)));

        animatorCoin60 = new Animator ("coins.png", 6, 1, 10.0f, Animation.PlayMode.LOOP, 194, 24, 62*6, 60, 8);
        coin60Scale = 0.25f / zoom;
    //Расставляем монетки оконным координатам.
        Point coinDrawShift = new Point ((int)(-animatorCoin60.tileWidth/2.0f  * coin60Scale),
                                         (int)(-animatorCoin60.tileHeight/2.0f * coin60Scale));
        coinPoints = new LinkedList<>();
        for (MapObject mo : mapObjects)
        {
            String name = mo.getName();
            if (name != null && name.startsWith("Монетка"))
            {
                Rectangle rm = ((RectangleMapObject) mo).getRectangle();
                coinPoints.add (new Point ((int)((rm.x - screenOriginOffset.x + coinDrawShift.x)),
                                           (int)((rm.y - screenOriginOffset.y + coinDrawShift.y))));
            }
        }
    }

    @Override public void render ()
    {
        ScreenUtils.clear (0.25f, 0.75f, 0.85f, 1);

        if (Gdx.input.isKeyPressed (Input.Keys.ESCAPE))
            Gdx.app.exit();

        float deltaTime = Gdx.graphics.getDeltaTime();
        animatorCoin60.updateTime (deltaTime);

        boolean cameraMoved = cameraMoving (readDirectionsKeys (Gdx.input));
        animatorBatman.updateTime (cameraMoved ? deltaTime : -1.0f);

        if (cameraMoved)
            ortoCamera.update();
        ortoMapRenderer.setView (ortoCamera);
        ortoMapRenderer.render();

        batch.begin();
        drawBatman();
        drawCoins();
        lable.draw (batch, "ЙЦУКЕНГШЩЗХЪЖДЛОРПАВЫФЯЧСМИТЬБЮЁ йцукенгшщзхъждлорпавыфячсмитьбюё.,!:?—-«»()/*\\");
        batch.end();
    }
    
    @Override public void dispose () {
        batch.dispose();
        animatorBatman.dispose();
        animatorCoin60.dispose();
        lable.dispose();
        map.dispose();
        ortoMapRenderer.dispose();
    }

/** @param delta смещение камеры по осям.
    @return TRUE, если камера сместилась, или FALSE, если смещения камеры не было. */
    private boolean cameraMoving (Point delta)
    {
        if (delta.x == 0 && delta.y == 0)
            return false;

        ortoCamera.position.x += delta.x;
        ortoCamera.position.y += delta.y;
        for (Point p : coinPoints) {
            p.x -= delta.x;
            p.y -= delta.y;
        }
        return true;
    }

    private Point readDirectionsKeys(Input input)
    {
        Point delta = new Point(0, 0);
        if (input.isKeyPressed (Input.Keys.D) || input.isKeyPressed (Input.Keys.RIGHT))  delta.x = xStep;
        else
        if (input.isKeyPressed (Input.Keys.A) || input.isKeyPressed (Input.Keys.LEFT))   delta.x = -xStep;

        if (input.isKeyPressed (Input.Keys.W) || input.isKeyPressed (Input.Keys.UP))     delta.y = yStep;
        else
        if (input.isKeyPressed (Input.Keys.S) || input.isKeyPressed (Input.Keys.DOWN))   delta.y = -yStep;
        return delta;
    }

    private void drawBatman () {
        Point center = new Point ((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
        batch.draw (animatorBatman.getTile(),
                center.x/2.0f - batmanDimention.width/2.0f,
                center.y/2.0f - batmanDimention.height/2.0f,
                0, 0, animatorBatman.tileWidth, animatorBatman.tileHeight,
                batmanScale, batmanScale, 0);
    }

    private void drawCoins () {
        for (Point p : coinPoints)
            batch.draw (animatorCoin60.getTile(),
                        (float)p.x / zoom, (float)p.y / zoom,
                        0, 0, animatorCoin60.tileWidth, animatorCoin60.tileHeight,
                        coin60Scale, coin60Scale, 0);
    }
}
