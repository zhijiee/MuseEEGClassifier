package controllers.MuseControllers;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileFactory;
import com.choosemuse.libmuse.MuseFileWriter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import controllers.SVMController.SVM_Helper;


public class MuseConnectionHelper {

    static boolean MUSE_CONNECTED = false;
    int reconnectCount = 0;

    private final AtomicReference<Handler> fileHandler = new AtomicReference<>();
    private final AtomicReference<MuseFileWriter> fileWriter = new AtomicReference<>();
    private final Handler handler = new Handler();
    public final double[] eegBuffer = new double[6];
    public final double[] alphaBuffer = new double[6];
    public final double[] accelBuffer = new double[3];
    public final double[] hsiBuffer = new double[4];
    String TAG = "MUSE_HELPER";
    Muse muse;
    Context context;
    private TextView tv_eeg_1;
    private TextView tv_eeg_2;
    private TextView tv_eeg_3;
    private TextView tv_eeg_4;

    private TextView tv_hsi_1;
    private TextView tv_hsi_2;
    private TextView tv_hsi_3;
    private TextView tv_hsi_4;

    public String rawMedBuffer = "nothing";

    private TextView rawMed;
    public Runnable updateGUI = new Runnable() {
        @Override
        public void run() {
            tv_eeg_1.setText("" + eegBuffer[0]);
            tv_eeg_2.setText("" + eegBuffer[1]);
            tv_eeg_3.setText("" + eegBuffer[2]);
            tv_eeg_4.setText("" + eegBuffer[3]);

            tv_hsi_1.setText("" + hsiBuffer[0]);
            tv_hsi_2.setText("" + hsiBuffer[1]);
            tv_hsi_3.setText("" + hsiBuffer[2]);
            tv_hsi_4.setText("" + hsiBuffer[3]);

            handler.postDelayed(updateGUI, 500);
        }
    };
    private TextView tv_muse_status;
    private DataListener dataListener; // Receive packets from connected band
    private ConnectionListener connectionListener; //Headband connection Status
    private String name= "hello";
    private String muse_status;
    private SVM_Helper sh;

    public MuseConnectionHelper(SVM_Helper sh) {

        this.sh = sh;
        //Setup Callback
        WeakReference<MuseConnectionHelper> weakActivity =
                new WeakReference<MuseConnectionHelper>(this);
        connectionListener = new ConnectionListener(weakActivity); //Status of Muse Headband
        dataListener = new DataListener(weakActivity); //Get data from EEG

    }

    public void setTv_muse_status(TextView tv_muse_status) {
        this.tv_muse_status = tv_muse_status;
    }

    public void setHSITextView(TextView hsi1, TextView hsi2, TextView hsi3, TextView hsi4) {
        this.tv_hsi_1 = hsi1;
        this.tv_hsi_2 = hsi2;
        this.tv_hsi_3 = hsi3;
        this.tv_hsi_4 = hsi4;

    }

    public void setEEGTextView(TextView eeg1, TextView eeg2, TextView eeg3, TextView eeg4) {
        this.tv_eeg_1 = eeg1;
        this.tv_eeg_2 = eeg2;
        this.tv_eeg_3 = eeg3;
        this.tv_eeg_4 = eeg4;

    }


    public void setRawMedBuffer(String rawMedBuffer) {
        this.rawMedBuffer = rawMedBuffer;
    }

    public double[] getEegBuffer() {
        return eegBuffer;
    }

    public double[] getHsiBuffer() {
        return hsiBuffer;
    }


    public void setMuse(Muse muse) {
        this.muse = muse;
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public final void connect_to_muse() {
        muse.unregisterAllListeners();
        muse.registerConnectionListener(connectionListener);
        muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
        muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
        muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
        muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
        muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
        muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);
        muse.registerDataListener(dataListener, MuseDataPacketType.HSI_PRECISION);
        muse.registerDataListener(dataListener, MuseDataPacketType.IS_GOOD);

        // Initiate a connection to the headband and stream the data asynchronously.
        muse.runAsynchronously();

    }

    /*
     * -------------------- Begin File I/O --------------------------
     */
    private void initFileWriter() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = name + "_" + formatter.format(now) + ".muse";

        fileHandler.set(new Handler());
        final File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        final File file = new File(dir, fileName);
        // MuseFileWriter will append to an existing file.
        // In this case, we want to start fresh so the file
        // if it exists.
        if (file.exists()) {
            file.delete();
        }
        Log.i(TAG, "Writing data to: " + file.getAbsolutePath());
        fileWriter.set(MuseFileFactory.getMuseFileWriter(file));
    }

    /**
     * Writes the provided MuseDataPacket to the file.  MuseFileWriter knows
     * how to write all packet types generated from LibMuse.
     *
     * @param p The data packet to write.
     */
    private void writeDataPacketToFile(final MuseDataPacket p) {
        Handler h = fileHandler.get();
        if (h != null) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    fileWriter.get().addDataPacket(0, p);
                }
            });
        }
    }


    private void saveFile() {
        Handler h = fileHandler.get();
        if (h != null) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    MuseFileWriter w = fileWriter.get();
                    // Annotation strings can be added to the file to
                    // give context as to what is happening at that point in
                    // time.  An annotation can be an arbitrary string or
                    // may include additional AnnotationData.
//                    w.addConfiguration(0, muse.getMuseConfiguration());
                    w.addAnnotationString(0, "Disconnected");
                    w.flush();
                    w.close();
                    Toast.makeText(context, "File Saved", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
     * ----------------------- Begin Callback methods ---------------
     */

    /**
     * You will receive a callback to this method each time there is a change to the
     * connection state of one of the headbands.
     *
     * @param p    A packet containing the current and prior connection states
     * @param muse The headband whose state changed.
     */
    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();

        // Format a message to show the change of connection state in the UI.
        muse_status = current.toString();
        Log.i(TAG, "Muse Connection Status: " + muse_status);

        handler.post(new Runnable() {
            @Override
            public void run() {
                // Update the UI with the change in connection state.
//                final TextView statusText = findViewById(R.id.con_status);
                if(tv_muse_status != null)
                    tv_muse_status.setText(muse_status);

            }
        });

        if (current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            MUSE_CONNECTED = false;

            //Retry connection of muse for 5 times
            if (muse != null && reconnectCount < 5) {
                reconnectCount++;
//                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (muse != null)
                            connect_to_muse();
                    }
                }, 500);
            }
//            }


        } else if (current == ConnectionState.CONNECTED) {
            reconnectCount = 0; //reset the count
            MUSE_CONNECTED = true;

        }
    }


    /**
     * You will receive a callback to this method each time the headband sends a MuseDataPacket
     * that you have registered.  You can use different listeners for different packet types or
     * a single listener for all packet types as we have done here.
     *
     * @param p    The data packet containing the data from the headband (eg. EEG data)
     * @param muse The headband that sent the information.
     */
    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {

        // Write to file when is_recording started
//        if (is_recording) {
//            writeDataPacketToFile(p);
//        }
        // valuesSize returns the number of data values contained in the packet.
        final long n = p.valuesSize();
        switch (p.packetType()) {
            case EEG:
                assert (eegBuffer.length >= n);
                getEegChannelValues(eegBuffer, p);
                sh.receiveEEGPacket(eegBuffer);
                break;
            case ACCELEROMETER:
                assert (accelBuffer.length >= n);
                getAccelValues(p);
                break;
            case ALPHA_RELATIVE:
                assert (alphaBuffer.length >= n);
                getEegChannelValues(alphaBuffer, p);
                break;
            case HSI_PRECISION:
                assert (alphaBuffer.length >= n);
                getHSIPrecision(hsiBuffer, p);
                break;
            case BATTERY:
            case DRL_REF:
            case QUANTIZATION:

            default:
                break;
        }
    }

    private void getHSIPrecision(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
    }

    private void getEegChannelValues(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT);
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT);
    }

    private void getAccelValues(MuseDataPacket p) {
        accelBuffer[0] = p.getAccelerometerValue(Accelerometer.X);
        accelBuffer[1] = p.getAccelerometerValue(Accelerometer.Y);
        accelBuffer[2] = p.getAccelerometerValue(Accelerometer.Z);
    }




    /*
     *  ------------- Begin Setup classes for callback ---------------
     */
    class ConnectionListener extends MuseConnectionListener {
        final WeakReference<MuseConnectionHelper> activityRef;

        ConnectionListener(final WeakReference<MuseConnectionHelper> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            activityRef.get().receiveMuseConnectionPacket(p, muse);
        }
    }

    class DataListener extends MuseDataListener {
        final WeakReference<MuseConnectionHelper> activityRef;

        DataListener(final WeakReference<MuseConnectionHelper> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            activityRef.get().receiveMuseDataPacket(p, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            // Not going to use Muse default Artifact removal
            //activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }
}
