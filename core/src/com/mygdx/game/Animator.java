package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.awt.Dimension;

public class Animator
{
    private final Texture texture;
    private final Animation<TextureRegion> animation;
    private       float time;
    public  final int tileWidth, tileHeight;

    public Animator(String fileName, int cols, int rows, float fps, Animation.PlayMode playMode,
                    int x, int y, int width, int height, int tilesNumber)
    {
        texture = new Texture (fileName);
        TextureRegion region = new TextureRegion (
                texture,
                x,y,    //< ЛВ-угол области тестуры.
                width,height);  //< ширина и высота области текстуры.

        tileWidth  = region.getRegionWidth()/cols;
        tileHeight = region.getRegionHeight()/rows;
        TextureRegion[][] matrixOfRegions = region.split (tileWidth, tileHeight);

        int bandSize = Math.min (tilesNumber, cols * rows);
        TextureRegion[] bandOfRegions = new TextureRegion [bandSize];

        for (int i=0, c=0;  i< matrixOfRegions.length;  i++)
        {
            for (int j=0;  j< matrixOfRegions[i].length;  j++, c++)
                if (c < bandSize)
                    bandOfRegions[c] = matrixOfRegions[i][j];
        }
        animation = new Animation<>(1.0f/fps, bandOfRegions);
        animation.setPlayMode (playMode);
    }

    public void updateTime (float delta) {
        if (delta > 0.0f)
            time += delta;
        else
            time = 0.0f;
    }

    public TextureRegion getTile () {    return animation.getKeyFrame (time);    }

    public void dispose () {    texture.dispose();    }

    public boolean isFinished () {
        // Показывает, является ли последним тайл, соответствующий текущему времени.
        return animation.isAnimationFinished (time);
    }

    public void setPlayMode (Animation.PlayMode mode) {
        animation.setPlayMode (mode);
    }

    public Dimension getTileDimention () {    return new Dimension (tileWidth, tileHeight);    }
}
