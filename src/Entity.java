import processing.core.PImage;

interface Entity
{
    Point getPosition();
    void setPosition(Point p);
    PImage getCurrentImage();
}
