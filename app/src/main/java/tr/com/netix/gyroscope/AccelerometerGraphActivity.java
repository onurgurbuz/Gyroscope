package tr.com.netix.gyroscope;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccelerometerGraphActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "AccelerometerGraph";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor sensors;

    private LineChart mChart;
    private Button startButton, stopButton;
    private Thread thread;
    private boolean plotData = true;
    private String mLastY = "";
    private DecimalFormat decimalFormat = new DecimalFormat("##.#####");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.sss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_graph);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 0; i < sensors.size(); i++) {
            Log.d(TAG, "onCreate: Sensor " + i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        mChart = (LineChart) findViewById(R.id.chart1);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        startButton.setOnClickListener(view -> onResume());
        stopButton.setOnClickListener(view -> onPause());

        // enable description text
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Tümsek Grafiği");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend legend = mChart.getLegend();

        // modify the legend ...
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);

        YAxis yLeftAxis = mChart.getAxisLeft();
        yLeftAxis.setTextColor(Color.BLACK);
        yLeftAxis.setDrawGridLines(false);
        yLeftAxis.setAxisMaximum(40f);
        yLeftAxis.setAxisMinimum(-40f);
        yLeftAxis.setDrawGridLines(true);

        YAxis yRightAxis = mChart.getAxisRight();
        yRightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.setDrawBorders(true);

        feedMultiple();
    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), event.values[1]), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(3000);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {
        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(() -> {
            while (true) {
                plotData = true;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (mLastY.equals(decimalFormat.format(event.values[1]))) {
            Log.d("Aynı", "Veri geldi " + event.values[1]);
        }

        mLastY = decimalFormat.format(event.values[1]);
        String currentDateandTime = sdf.format(new Date());
        Log.d("Farklı", "Veri geldi " + event.values[1]);
        Log.e("Current Time", currentDateandTime);
        Log.d(TAG, "-------------------------------------------");
        if (plotData) {
            addEntry(event);
            plotData = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(AccelerometerGraphActivity.this);
        thread.interrupt();
        super.onDestroy();
    }
}