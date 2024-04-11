package com.dark.canves;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dark.canves.interfaces.DrawingEvents;
import com.dark.canves.view.DrawingView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity implements DrawingEvents {

    DrawingView drawingView;
    MaterialButton clear;
    MaterialButtonToggleGroup drawState;
    MaterialSwitch ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);
        clear = findViewById(R.id.clear);
        drawState = findViewById(R.id.draw_state);
        ref = findViewById(R.id.refBtn);

        ref.setOnCheckedChangeListener((buttonView, isChecked) -> drawingView.isRef = isChecked);

        drawState.addOnButtonCheckedListener((materialButtonToggleGroup, i, b) -> {
            if (b) {
                if (i == R.id.free) drawingView.DrawFree();

                if (i == R.id.box) drawingView.DrawBox();

                if (i == R.id.line) drawingView.DrawLine();

                if (i == R.id.circle) drawingView.DrawCircle();

                if (i == R.id.frame) drawingView.DrawFrame();

            }
        });

        clear.setOnClickListener(v -> drawingView.clearDrawing());


    }

    @Override
    public void onFrameAdded() {
        drawState.check(R.id.free);
        drawingView.DrawFree();
    }
}