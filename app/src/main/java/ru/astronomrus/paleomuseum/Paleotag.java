package ru.astronomrus.paleomuseum;

/**
 * Created by vkoroy on 23.03.18.
 */

public class Paleotag {
    Paleotag()
    {
        name = new String();
        tag= new String();
    }
    public String name;
    public String tag;

    @Override
    public String toString() {
        return  name+"#"+tag;
    }
}
