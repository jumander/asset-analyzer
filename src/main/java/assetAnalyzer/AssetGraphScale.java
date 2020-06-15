package assetAnalyzer;

import financial.Time;
import graphics.Color;
import graphics.Renderer;
import text.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by johannes on 17/05/07.
 */
public class AssetGraphScale {


    private float minValue, maxValue;
    private long minTime, maxTime;

    private Renderer render;
    private int font;

    public AssetGraphScale(Renderer render)
    {
        minValue = 0;
        maxValue = 100;

        Calendar time = Calendar.getInstance();
        time.set(Calendar.MONTH, time.get(Calendar.MONTH) + 1);
        maxTime = time.getTimeInMillis()/1000;

        time.set(Calendar.MONTH, time.get(Calendar.MONTH) - 10);
        minTime = time.getTimeInMillis()/1000;

        this.render = render;
        font = render.loadFont("small_arial.fnt");
    }

    public void setVertical(float minValue, float maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void setHorizontal(long minTime, long maxTime)
    {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public long getMinTime()
    {
        return minTime;
    }

    public long getMaxTime()
    {
        return maxTime;
    }

    public void drawCurrentTime(float startX, float startY, float width, float height) {

        float z = 0.75f;

        Calendar now = Calendar.getInstance();
        long time = now.getTimeInMillis();
        long left = minTime * 1000;
        long right = maxTime * 1000;
        if(time < left || time > right)
            return;

        float x = (time - left) / (float)(right-left);

        float r = 0.3f;
        float g = 0.5f;
        float b = 0.2f;

        glUseProgram(0);
        glBegin(GL_LINES);
        glColor3f(r, g, b);
        glVertex3f(2 * (startX + width * x) - 1, - 2 *(startY) + 1, z);
        glColor3f(r, g, b);
        glVertex3f(2 * (startX + width * x) - 1, - 2 *(startY + height - height / 25.0f) + 1, z);
        glEnd();
    }

    public void drawVertical(float startX, float startY, float width, float height) {
        int level = 1; // divide by 10 for realNum
        int start;
        int imax = (int)Math.floor(maxValue*10);
        int imin = (int)Math.floor(minValue*10);

        float z = 1.0f; // background

        do {
            //System.out.println(level);
            start = imax - (imax % level);

            int numLines = (imax-imin)/level + 1;

            if(start > imin && numLines < 100) {

                float col = 0.4f;
                if(numLines > 10) {
                    col = (float) (col / Math.log10(numLines));
                    //col *= col;
                }

                for(int i = 0; start - i * level > imin; i++) {
                    float y = 1-((start - i*level)/10f-minValue)/(maxValue-minValue);
                    //System.out.println(y);
                    glUseProgram(0);
                    glBegin(GL_LINES);
                    glColor4f(0.75f, 0.75f, 0.75f, col);
                    glVertex3f(startX*2-1, -2*(startY + height * y) + 1, z);
                    glColor4f(0.75f, 0.75f, 0.75f, col);
                    glVertex3f(((startX+width)*2-1), -2*(startY + height * y) + 1, z);
                    glEnd();
                }

            }

            level *= 10;
        }while(start > 0 && start > minValue);

    }

    public void drawHorizontal(float startX, float startY, float width, float height) {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis((long)((long)maxTime)*1000);

        //MINUTE
        time.set(Calendar.SECOND, 0);
        drawDates(startX, startY, width, height, time, Calendar.MINUTE, new SimpleDateFormat("HH:mm"));

        //HOUR
        time.set(Calendar.MINUTE, 0);
        drawDates(startX, startY, width, height, time, Calendar.HOUR_OF_DAY, new SimpleDateFormat("HH:mm"));

        //DAY
        time.set(Calendar.HOUR_OF_DAY, 0);
        drawDates(startX, startY, width, height, time, Calendar.DAY_OF_MONTH, new SimpleDateFormat("MMM d"));

        //MONTH
        time.set(Calendar.DAY_OF_MONTH, 1);
        drawDates(startX, startY, width, height, time, Calendar.MONTH, new SimpleDateFormat("MMMM"));

        //YEAR
        time.set(Calendar.DAY_OF_YEAR, 1);
        drawDates(startX, startY, width, height, time, Calendar.YEAR, new SimpleDateFormat("yyyy"));
    }

    private void drawDates(float startX, float startY, float width, float height, Calendar time, int type, SimpleDateFormat format) {

        int length = 1;
        switch(type) {
            case Calendar.YEAR:
                length = 365 * 24 * 60 * 60;
                break;
            case Calendar.MONTH:
                length *= 30;
            case Calendar.DAY_OF_MONTH:
                length *= 24;
            case Calendar.HOUR_OF_DAY:
                length *= 60;
            case Calendar.MINUTE:
                length *= 60;
                break;
            default:
                return;
        }

        int start = (int)(time.getTimeInMillis()/1000);
        int numLines = numHorizontalLines(start, length);
        if(numLines == 0)
            return;

        float col = 0.75f;
        if(numLines > 10 && type != Calendar.YEAR) {
            col = (float) (col / Math.log10(numLines));

            col *= col;
        }

        Calendar tmp = Calendar.getInstance();
        tmp.setTimeInMillis(time.getTimeInMillis());

        while((int)(tmp.getTimeInMillis()/1000) > minTime) {

            int at = (int)(tmp.getTimeInMillis()/1000);
            float x = (at-minTime)/(float)(maxTime-minTime);

            if(numLines <= 1) {
                //fontS.draw(format.format(tmp.getTime()), x, 0.95f, Text.Align.DOWN);
            }
            else if (numLines < 15 || (tmp.get(type) % 5 == 0 && numLines < 50)) {
                render.drawText(font, format.format(tmp.getTime()), startX + x*width, startY+height- width / 20.0f, Renderer.Level.FOREGROUND, new Color(col, col, col), Text.Align.DOWN);
            }


            float z = 1.0f; // background

            glUseProgram(0);
            glBegin(GL_LINES);
            glColor4f(0.75f, 0.75f, 0.75f, col);
            glVertex3f(2*(startX + width * x) - 1, -2*(startY) + 1, z);
            glColor4f(0.75f, 0.75f, 0.75f, col);
            glVertex3f(2*(startX + width * x) - 1, -2*(startY + height - height / 20.0f) + 1, z);
            glEnd();

            tmp.add(type, -1);
        }

    }

    private int numHorizontalLines(int start, int length) {
        if(start < minTime)
            return 0;

        int numLines = (int)((start-minTime) / length + 1);

        return numLines < 1000 ? numLines : 0;
    }
}
