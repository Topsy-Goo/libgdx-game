package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Lable
{
    private final BitmapFont bitmapFont;

    public Lable(int siz) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Gabriola.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = siz;
        fontParameter.characters = "ЙЦУКЕНГШЩЗХЪЖДЛОРПАВЫФЯЧСМИТЬБЮЁ йцукенгшщзхъждлорпавыфячсмитьбюё.,!:?—-«»()/*\\";
        bitmapFont = fontGenerator.generateFont(fontParameter);
        bitmapFont.setColor (0.7f, 1.0f, 0.9f, 1);
    }

    public void draw (SpriteBatch batch, String text) {
        bitmapFont.draw (batch, text, 0, bitmapFont.getLineHeight());
    }

    public void dispose() {    bitmapFont.dispose();    }
}
