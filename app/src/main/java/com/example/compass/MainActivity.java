package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mMagnetometar;
    private Sensor mAccelerometar;
    private ImageView mImageViewCompass;
    private float[] mGravityValues = new float[3];
    private float[] mAccelerationValues = new float[3];
    private float[] mRotationMatrix = new float[9];
    private float mLastDirectionDegrees = 0f;

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            calculateCompassDirection( event );
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
    }

    private void calculateCompassDirection(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerationValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGravityValues = event.values.clone();
                break;
        }

        boolean success = SensorManager.getRotationMatrix( mRotationMatrix, null, mAccelerationValues, mGravityValues );

        if (success) {
            float[] orientationValues = new float[3];
            SensorManager.getOrientation( mRotationMatrix, orientationValues );
            float azimuth = (float) Math.toDegrees( -orientationValues[0] );
            RotateAnimation rotateAnimation = new RotateAnimation( mLastDirectionDegrees, azimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f );
            rotateAnimation.setDuration( 50 );
            rotateAnimation.setFillAfter( true );
            mImageViewCompass.startAnimation( rotateAnimation );
            mLastDirectionDegrees = azimuth;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener( mSensorListener, mMagnetometar, SensorManager.SENSOR_DELAY_FASTEST );
        mSensorManager.registerListener( mSensorListener, mAccelerometar, SensorManager.SENSOR_DELAY_FASTEST );

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener( mSensorListener );
    }
}
