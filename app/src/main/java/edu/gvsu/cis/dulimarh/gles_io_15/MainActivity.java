package edu.gvsu.cis.dulimarh.gles_io_15;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends ActionBarActivity implements SensorEventListener{

    private MyGLView glView;
    private TextView txt;
    private SensorManager sm;
    private Sensor motionSensor;
    private float[] rotationMat;
    private StringBuilder debugMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glView = (MyGLView) findViewById(R.id.glview);
        txt = (TextView) findViewById(R.id.txt);
        rotationMat = new float[16];
        Matrix.setIdentityM(rotationMat, 0);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        motionSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        debugMsg = new StringBuilder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, motionSensor, SensorManager
                .SENSOR_DELAY_NORMAL);
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
        debugMsg.setLength(0);
        debugMsg.append("onSensorChanged: " + ev.timestamp / 1000000);
        if (ev.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            for (int k = 0; k < ev.values.length; k++)
            debugMsg.append(String.format("%7.2f ", ev.values[k]));
        }
        SensorManager.getRotationMatrixFromVector(rotationMat, ev.values);
        txt.setText(debugMsg.toString());
        glView.onRotationChanged(rotationMat);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
