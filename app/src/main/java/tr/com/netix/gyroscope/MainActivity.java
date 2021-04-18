package tr.com.netix.gyroscope;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    private Sensor accelerometer, mGyro, mMagno, mLight, mPressure, mTemp, mHumi;

    TextView xValue, yValue, zValue, xGValue, yGValue, zGValue, xMValue, yMValue, zMValue, light, pressure, temp, humi;

    private double oldX, oldY, oldZ;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, AccelerometerGraphActivity.class);
        startActivity(intent);

        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);

        xGValue = (TextView) findViewById(R.id.xGValue);
        yGValue = (TextView) findViewById(R.id.yGValue);
        zGValue = (TextView) findViewById(R.id.zGValue);

        xMValue = (TextView) findViewById(R.id.xMValue);
        yMValue = (TextView) findViewById(R.id.yMValue);
        zMValue = (TextView) findViewById(R.id.zMValue);

        light = (TextView) findViewById(R.id.light);
        pressure = (TextView) findViewById(R.id.pressure);
        temp = (TextView) findViewById(R.id.temp);
        humi = (TextView) findViewById(R.id.humi);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        /*accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Accelerometer listener");
        } else {
            xValue.setText("Accelerometer Not Supported");
            yValue.setText("Accelerometer Not Supported");
            zValue.setText("Accelerometer Not Supported");
        }

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyro != null) {
            sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Gyro listener");
        } else {
            xGValue.setText("Gyro Not Supported");
            yGValue.setText("Gyro Not Supported");
            zGValue.setText("Gyro Not Supported");
        }

        mMagno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mMagno, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnometer listener");
        } else {
            xMValue.setText("Magno Not Supported");
            yMValue.setText("Magno Not Supported");
            zMValue.setText("Magno Not Supported");
        }

        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLight != null) {
            sensorManager.registerListener(MainActivity.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Light listener");
        } else {
            light.setText("Light Not Supported");
        }

        mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (mPressure != null) {
            sensorManager.registerListener(MainActivity.this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Pressure listener");
        } else {
            pressure.setText("Pressure Not Supported");
        }

        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (mTemp != null) {
            sensorManager.registerListener(MainActivity.this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Temp listener");
        } else {
            temp.setText("Temp Not Supported");
        }

        mHumi = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (mHumi != null) {
            sensorManager.registerListener(MainActivity.this, mHumi, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Humi listener");
        } else {
            humi.setText("Humi Not Supported");
        }*/
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            if (!mInitialized) {
                mLastX = x;
                mLastY = y;
                mLastZ = z;

                xValue.setText("xValue: \n0.0");
                yValue.setText("yValue: \n0.0");
                zValue.setText("zValue: \n0.0");
                mInitialized = true;
            } else {
                float deltaX = Math.abs(mLastX - x);
                float deltaY = Math.abs(mLastY - y);
                float deltaZ = Math.abs(mLastZ - z);
                if (deltaX < NOISE) deltaX = (float) 0.0;
                if (deltaY < NOISE) deltaY = (float) 0.0;
                if (deltaZ < NOISE) deltaZ = (float) 0.0;
                mLastX = x;
                mLastY = y;
                mLastZ = z;

                xValue.setText("xValue: \n" + Float.toString(deltaX));
                yValue.setText("yValue: \n" + Float.toString(deltaY));
                zValue.setText("zValue: \n" + Float.toString(deltaZ));

            }
            /*boolean valuChanged = false;

            if (oldX != sensorEvent.values[0]){
                oldX = sensorEvent.values[0];
                valuChanged = true;
            }

            if (oldY != sensorEvent.values[1]){
                oldY = sensorEvent.values[1];
                valuChanged = true;
            }

            if (oldZ != sensorEvent.values[2]){
                oldZ = sensorEvent.values[2];
                valuChanged = true;
            }

            if (valuChanged) {
                double x = (sensorEvent.values[0] + 360.0) % 360.0;
                double y = (sensorEvent.values[1]+ 360.0) % 360.0;
                double z = (sensorEvent.values[2] + 360.0) % 360.0;

                Log.d(TAG, "onSensorChanged Accelerometer X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
                Log.d(TAG, "onSensorChanged2 Accelerometer X: " + x + "Y: " + y + "Z: " + z);

                xValue.setText("xValue: \n" + sensorEvent.values[0]);
                yValue.setText("yValue: \n" + sensorEvent.values[1]);
                zValue.setText("zValue: \n" + sensorEvent.values[2]);
            }*/
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.d(TAG, "onSensorChanged Gyro X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);

            xGValue.setText("xGValue: \n" + sensorEvent.values[0]);
            yGValue.setText("yGValue: \n" + sensorEvent.values[1]);
            zGValue.setText("zGValue: \n" + sensorEvent.values[2]);
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.d(TAG, "onSensorChanged Magno X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);

            xMValue.setText("xMValue: \n" + sensorEvent.values[0]);
            yMValue.setText("yMValue: \n" + sensorEvent.values[1]);
            zMValue.setText("zMValue: \n" + sensorEvent.values[2]);
        } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
            light.setText("Light: " + sensorEvent.values[0]);
        } else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressure.setText("Pressure: " + sensorEvent.values[0]);
        } else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temp.setText("Temp: " + sensorEvent.values[0]);
        } else if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            humi.setText("Humidity: " + sensorEvent.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
