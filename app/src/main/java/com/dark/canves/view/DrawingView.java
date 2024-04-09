package com.dark.canves.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dark.canves.R;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private final Paint paint, dottedPaint, grid, text, refPaint;
    private final List<Path> paths;
    private final List<LineData> lines;
    private final List<LineData> circles;
    private final List<RectF> boxes;
    public boolean isRef = true;
    private boolean drawBox = false;
    private boolean drawFree = true;
    private boolean drawLine = false;
    private boolean drawCircle = false;
    private boolean isDrawing = false;
    private Path currentPath;
    private LineData currentLine, currentCircle, refLine, refLine2;
    private RectF currentBox;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize paint settings
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        text = new Paint();
        text.setColor(Color.BLACK);
        text.setStyle(Paint.Style.FILL);
        text.setTextSize(28);

        dottedPaint = new Paint();
        dottedPaint.setColor(Color.BLACK);
        dottedPaint.setStrokeWidth(5);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0)); // Set a dash effect

        refPaint = new Paint();
        refPaint.setColor(ContextCompat.getColor(getContext(), R.color.refColor));
        refPaint.setStrokeWidth(5);
        refPaint.setStyle(Paint.Style.STROKE);
        refPaint.setPathEffect(new DashPathEffect(new float[]{15, 15}, 0)); // Set a dash effect

        grid = new Paint();
        grid.setColor(Color.LTGRAY);
        grid.setStrokeWidth(2);
        grid.setStyle(Paint.Style.FILL);


        paths = new ArrayList<>();
        boxes = new ArrayList<>();
        lines = new ArrayList<>();
        circles = new ArrayList<>();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawGrids(canvas);

        for (RectF box : boxes) {
            canvas.drawRect(box, paint);
        }

        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }

        for (LineData path : lines) {
            canvas.drawLine(path.sX, path.sY, path.eX, path.eY, paint);
        }

        for (LineData path : circles) {
            canvas.drawCircle(path.sX, path.sY, path.eX, paint);
        }


        if (currentBox != null && drawBox) {
            canvas.drawRect(currentBox, dottedPaint);
        }

        if (currentPath != null && drawFree) {
            canvas.drawPath(currentPath, dottedPaint);
        }

        if (currentPath != null && drawLine) {
            canvas.drawPath(currentPath, dottedPaint);
        }

        if (currentPath != null && drawCircle) {
            if (isDrawing) {
                canvas.drawLine(currentLine.sX, currentLine.sY, currentLine.eX, currentLine.eY, paint);
                canvas.drawText("Radius: " + currentCircle.eX, currentLine.eX, currentLine.eY, text);
            }
            canvas.drawCircle(currentCircle.sX, currentCircle.sY, currentCircle.eX, dottedPaint);
        }

        if (isDrawing && isRef) {
            canvas.drawLine(refLine.sX, refLine.sY, refLine.eX, refLine.eY, refPaint); //Vertical Reference Line
            canvas.drawLine(refLine2.sX, refLine2.sY, refLine2.eX, refLine2.eY, refPaint); //Horizontal Reference Line
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start a new path or box
                isDrawing = true;
                currentPath = new Path();
                currentLine = new LineData(x, y, x, y);
                currentCircle = new LineData(x, y, 0, 0);
                refLine = new LineData(x, 0, x, y); // Vertical Reference Line
                refLine2 = new LineData(0, y, x, y); // Horizontal Reference Line
                currentBox = new RectF(x, y, x, y);
                currentPath.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                // Update the path or box
                currentPath.lineTo(x, y);
                isDrawing = true;
                currentBox.right = x;
                currentBox.bottom = y;
                currentLine.eX = x;
                currentLine.eY = y;

                refLine.sX = x;
                refLine.eX = x;
                refLine.eY = y;

                refLine2.sY = y;
                refLine2.eX = x;
                refLine2.eY = y;

                // Calculate the radius for the circle
                float dx = currentCircle.sX - x;
                float dy = currentCircle.sY - y;
                double sqrt = Math.sqrt(dx * dx + dy * dy);
                currentCircle.eX = (float) sqrt; // Radius
                currentCircle.eY = (float) sqrt; // Radius

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // Save the drawn path or box
                isDrawing = false;
                if (drawFree) paths.add(currentPath);
                if (drawBox) boxes.add(currentBox);
                if (drawLine) lines.add(currentLine);
                if (drawCircle) circles.add(currentCircle);
                currentPath = null;
                currentBox = null;
                currentCircle = null;
                refLine = null;
                invalidate();
                break;
            default:
                return false;
        }

        return true;
    }

    private void drawGrids(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // Define the number of grid lines you want
        int numHorizontalLines, numVerticalLines;

        if (width < height) {
            numHorizontalLines = (int) (4.5 * 10);
            numVerticalLines = 2 * 10;
        } else {
            numHorizontalLines = 2 * 10;
            numVerticalLines = (int) (4.5 * 10);
        }


        // Calculate the spacing between grid lines
        float horizontalSpacing = (float) height / (numHorizontalLines + 1);
        float verticalSpacing = (float) width / (numVerticalLines + 1);

        // Draw horizontal grid lines
        float y = horizontalSpacing;
        for (int i = 0; i < numHorizontalLines; i++) {
            canvas.drawLine(0, y, width, y, grid);
            y += horizontalSpacing;
        }

        // Draw vertical grid lines
        float x = verticalSpacing;
        for (int i = 0; i < numVerticalLines; i++) {
            canvas.drawLine(x, 0, x, height, grid);
            x += verticalSpacing;
        }
    }

    public void clearDrawing() {
        paths.clear();
        boxes.clear();
        lines.clear();
        circles.clear();
        invalidate();
    }

    public void DrawBox() {
        this.drawBox = true;
        this.drawFree = false;
        this.drawLine = false;
        this.drawCircle = false;
    }

    public void DrawFree() {
        this.drawBox = false;
        this.drawFree = true;
        this.drawLine = false;
        this.drawCircle = false;
    }

    public void DrawLine() {
        this.drawBox = false;
        this.drawFree = false;
        this.drawLine = true;
        this.drawCircle = false;
    }

    public void DrawCircle() {
        this.drawBox = false;
        this.drawFree = false;
        this.drawLine = false;
        this.drawCircle = true;
    }
}

class LineData {

    public float sX, sY, eX, eY;

    public LineData(float sX, float sY, float eX, float eY) {
        this.eX = eX;
        this.eY = eY;
        this.sX = sX;
        this.sY = sY;
    }
}
