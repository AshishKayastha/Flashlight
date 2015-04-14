package com.ashish.flashlight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;


@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    private Camera mCamera;
    private boolean isFlashOn;
    private Camera.Parameters mCameraParams;
    private ImageButton mFlashlightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make this Activity Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Flashlight switch on/off button
        mFlashlightButton = (ImageButton) findViewById(R.id.flashlightBtn);

        // Check if device has Camera flash or not
        final boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            /*
            * If Device doesn't have flash then
            * Show alert message & close the application
            */
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(android.R.string.dialog_alert_title)
                    .setMessage(getString(R.string.no_camera_flash_dialog))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
            return;
        }

        getCamera();

        // Display ImageButton image according to Flash on/off state
        toggleButtonImage();

        /*
         * Switch ImageButton click event to toggle flash on/off
		 */
        mFlashlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    turnOffFlash();
                } else
                    turnOnFlash();
            }
        });
    }

    /*
     * Get the Camera so that we can use Camera flash
     */
    private void getCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCameraParams = mCamera.getParameters();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Turning Off flash
     */
    private void turnOffFlash() {
        if (isFlashOn) {

            // If Camera is not initialized then return without doing anything
            if (mCamera == null || mCameraParams == null)
                return;

            mCameraParams = mCamera.getParameters();
            mCameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mCameraParams);
            mCamera.stopPreview();
            isFlashOn = false;
            toggleButtonImage();
        }
    }

    /*
     * Turning On flash
     */
    private void turnOnFlash() {
        if (!isFlashOn) {

            // If Camera is not initialized then return without doing anything
            if (mCamera == null || mCameraParams == null)
                return;

            // Get  Torch Flash Parameter from Camera hardware
            mCameraParams = mCamera.getParameters();
            mCameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mCameraParams);
            mCamera.startPreview();
            isFlashOn = true;
            toggleButtonImage();
        }
    }

    /*
     * Set ImageButton background according to flash on/off state
     */
    private void toggleButtonImage() {
        if (isFlashOn) {
            mFlashlightButton.setBackgroundResource(R.drawable.flashlight_on);
        } else
            mFlashlightButton.setBackgroundResource(R.drawable.flashlight_off);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Release Camera if the app is stopped
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}