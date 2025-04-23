package de.instinct.eqlibgdxutils.rendering.ui.component.passive.description.model;

import com.badlogic.gdx.graphics.Color;

import lombok.Data;

@Data
public class Segment {

    private final String replacement;
    private final Color color;
    private final int startIndex;
    private final int endIndex;

}
