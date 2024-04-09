package com.dark.canves;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dark.canves.view.DrawingView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class MainActivity extends AppCompatActivity {

    DrawingView drawingView;
    MaterialButton clear;
    MaterialButtonToggleGroup drawState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);
        clear = findViewById(R.id.clear);
        drawState = findViewById(R.id.draw_state);

        drawState.addOnButtonCheckedListener((materialButtonToggleGroup, i, b) -> {
            if (b) {
                if (i == R.id.free) {
                    drawingView.DrawFree();
                }

                if (i == R.id.box) {
                    drawingView.DrawBox();
                }

                if (i == R.id.line) {
                    drawingView.DrawLine();
                }

                if (i == R.id.circle) {
                    drawingView.DrawCircle();
                }
            }
        });

        clear.setOnClickListener(v -> drawingView.clearDrawing());


    }
}