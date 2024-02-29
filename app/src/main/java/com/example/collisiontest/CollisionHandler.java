package com.example.collisiontest;

import android.graphics.Rect;

public class CollisionHandler {

    private Rect image1;
    private Rect image2;
    private boolean isJumping = false;

    public CollisionHandler() {
        // rectangle initialization
        image1 = new Rect(515, 1805, 625, 2020);
        image2 = new Rect(460, 180, 680, 400);
    }

    // Update positions of the images
    public void updatePositions() {
        image2.offset(0, 10); // Update the position of image2 here or by any other means
    }

    // Check for collisions
    public boolean checkCollision(boolean isJumping) {
        if (isJumping) { // Skip collision detection when jumping
            return false;
        }
        // Use Rect.intersect() to check for collision
        return Rect.intersects(image1, image2);
    }

    public void handleCollision() {
        // Handle collision as needed
        image2.offset(0, -10); // Example action to handle collision
    }

    public Rect getImage1() {
        return image1;
    }

    public Rect getImage2() {
        return image2;
    }

    // Move the main object left
    public void moveLeft() {
        image1.offset(-70, 0); // Adjust the offset according to your needs
    }

    // Move the main object right
    public void moveRight() {
        image1.offset(70, 0); // Adjust the offset according to your needs
    }

    // Jump the main object
    public void jump() {
        isJumping = true; // Set jumping flag to true
        image1.offset(0, -50); // Move the main object up (adjust the value according to your needs)
    }

    // Method to reset the jumping flag when the jump animation finishes
    public void finishJump() {
        isJumping = false;
    }
}