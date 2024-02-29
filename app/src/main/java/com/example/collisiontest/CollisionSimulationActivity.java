package com.example.collisiontest;

import android.app.Activity;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CollisionSimulationActivity extends Activity {

    private CollisionHandler collisionHandler;
    private SimulationView simulationView;
    private ImageView imageView;
    private AnimationDrawable dogAnimation;
    private GestureDetector gestureDetector;
    private boolean isJumping = false;


    /*
    to implement gesture movement i think i need to make another method in the CollisionHandler class
    to make the rectangles follow the movement of the imageView, feeling ko kasi hindi susunod yung
    rectangle if we try to just pure copy-paste the code that LK made for the swiping gestures
    shit but i'll try anyway
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the content view to your activity_main.xml layout

        getWindow().setBackgroundDrawableResource(R.drawable.road);

        collisionHandler = new CollisionHandler();

        ConstraintLayout container = findViewById(R.id.container);

        simulationView = new SimulationView(this);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );

        container.addView(simulationView, layoutParams);


        imageView = findViewById(R.id.image); // Make sure to reference the correct ImageView
        imageView.setBackgroundResource(R.drawable.animation);
        dogAnimation = (AnimationDrawable) imageView.getBackground();

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateGame();
                simulationView.invalidate(); // Force a redraw
                handler.postDelayed(this, 10); // Decrease the delay for smoother animation
                dogAnimation.start();
            }
        });

        gestureDetector = new GestureDetector(this, new MyGestureListener());
    }

    private void updateGame() {
        collisionHandler.updatePositions();

        // Check for collisions
        if (collisionHandler.checkCollision(isJumping)) {
            collisionHandler.handleCollision();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe right
                        moveAnimationRight();
                    } else {
                        // Swipe left
                        moveAnimationLeft();
                    }
                    return true;
                }
            } else {
                if (diffY < 0) {
                    // Swipe up
                    jumpAnimation();
                    return true;
                }
            }

            return false;
        }
    }

    // Move the animation left
    private void moveAnimationLeft() {
        imageView.animate().translationXBy(-285).setDuration(75).start();
        collisionHandler.moveLeft(); // Update collision rectangles position for left movement
    }

    private void moveAnimationRight() {
        imageView.animate().translationXBy(285).setDuration(75).start();
        collisionHandler.moveRight(); // Update collision rectangles position for right movement
    }

    private void jumpAnimation() {
        final int jumpDistance = 200; // Adjust jump distance as needed
        final int jumpDuration = 50; // Adjust jump duration as needed

        isJumping = true; // Set the jumping flag to true

        imageView.animate().translationYBy(-jumpDistance).setDuration(jumpDuration).withEndAction(new Runnable() {
            @Override
            public void run() {
                imageView.animate().translationYBy(jumpDistance).setDuration(jumpDuration);
                // Reset the jumping flag when the jump animation finishes
                isJumping = false;
            }
        }).start();

    }

    private class SimulationView extends View {

        private final Paint paint;

        public SimulationView(Context context) {
            super(context);
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw the hitboxes with transparency
            paint.setColor(Color.TRANSPARENT); // set color to transparent to make rectangle hitboxes semi-transparent
            Rect image1Rect = collisionHandler.getImage1();
            canvas.drawRect(image1Rect, paint);

            paint.setColor(Color.argb(100, 0, 0, 255));
            Rect image2Rect = collisionHandler.getImage2();
            canvas.drawRect(image2Rect, paint);

            // Get the current translationY of the imageView (dog)
            int translationY = (int) imageView.getTranslationY();

            // Draw the dog animation inside the hitbox
            RectF dogRectF = new RectF(image1Rect);
            int left = (int) dogRectF.left;
            int top = (int) dogRectF.top + translationY; // Adjust for the jump animation
            int right = (int) dogRectF.right;
            int bottom = (int) dogRectF.bottom + translationY; // Adjust for the jump animation

            dogAnimation.setBounds(left, top, right, bottom);
            dogAnimation.draw(canvas);
        }
    }
}


