package com.example.listadetarefas;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerAudioDeviceCallback(new AudioManager.AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                if (MainActivity.this.checkAudioOutput(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) {
                }
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                if (!MainActivity.this.checkAudioOutput(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)) {
                }
            }
        }, new Handler(Looper.getMainLooper()));

        button.setOnClickListener(v -> {
            boolean isSpeakerAvailable = checkAudioOutput(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
            boolean isBluetoothHeadsetConnected = checkAudioOutput(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);

            if (!isBluetoothHeadsetConnected) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("EXTRA_CONNECTION_ONLY", true);
                intent.putExtra("EXTRA_CLOSE_ON_CONNECT", true);
                intent.putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1);
                startActivity(intent);
            }
        });
    }

    boolean checkAudioOutput(int type) {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            return false;
        }
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device : devices) {
            if (device.getType() == type) {
                return true;
            }
        }
        return false;
    }
}