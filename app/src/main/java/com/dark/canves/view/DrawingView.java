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

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private final Paint paint, dottedPaint, grid, text;
    private final List<Path> paths;
    private final List<LineData> lines;
    private final List<LineData> circles;
    private final List<RectF> boxes;
    private boolean drawBox = false;
    private boolean drawFree = true;
    private boolean drawLine = false;
    private boolean drawCircle = false;
    private boolean isDrawing = false;
    private Path currentPath;
    private LineData currentLine, currentCircle;
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

        grid = new Paint();
        grid.setColor(Color.LTGRAY);
        grid.setStrokeWidth(2);
        grid.setStyle(Paint.Style.FILL);
        // grid.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0)); // Set a dash effect

        // Initialize lists to store paths and boxes
        paths = new ArrayList<>();
        boxes = new ArrayList<>();
        lines = new ArrayList<>();
        circles = new ArrayList<>();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int line = 10;

        // Define the number of grid lines you want
        int numHorizontalLines, numVerticalLines;

        if (width < height) {
            numHorizontalLines = (int) (4.5 * line);
            numVerticalLines = 2 * line;
        } else {
            numHorizontalLines = 2 * line;
            numVerticalLines = (int) (4.5 * line);
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


        if (drawBox) {
            if (currentBox != null) {
                canvas.drawRect(currentBox, dottedPaint);
            }
        }

        if (drawFree) {
            if (currentPath != null) {
                canvas.drawPath(currentPath, dottedPaint);
            }
        }

        if (drawLine) {
            if (currentPath != null) {
                canvas.drawPath(currentPath, dottedPaint);
            }
        }

        if (drawCircle) {
            if (currentPath != null) {
                if (isDrawing) {
                    canvas.drawLine(currentLine.sX, currentLine.sY, currentLine.eX, currentLine.eY, paint);
                    canvas.drawText("Radius: " + currentCircle.eX, currentLine.eX, currentLine.eY, text);
                }
                canvas.drawCircle(currentCircle.sX, currentCircle.sY, currentCircle.eX, dottedPaint);
            }
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
                invalidate();
                break;
            default:
                return false;
        }

        return true;
    }


    // Method to clear the drawing
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
