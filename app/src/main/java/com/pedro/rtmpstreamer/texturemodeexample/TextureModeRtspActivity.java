package com.pedro.rtmpstreamer.texturemodeexample;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pedro.rtmpstreamer.R;
import com.pedro.rtplibrary.rtsp.RtspCamera2;
import com.pedro.rtplibrary.view.AutoFitTextureView;
import com.pedro.rtsp.utils.ConnectCheckerRtsp;

/**
 * Unstable activity. See builder header.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TextureModeRtspActivity extends AppCompatActivity
    implements ConnectCheckerRtsp, View.OnClickListener {

  private RtspCamera2 rtspCamera2;
  private AutoFitTextureView textureView;
  private Button button;
  private EditText etUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_texture_mode);
    textureView = (AutoFitTextureView) findViewById(R.id.textureView);
    button = (Button) findViewById(R.id.b_start_stop);
    button.setOnClickListener(this);
    etUrl = (EditText) findViewById(R.id.et_rtp_url);
    etUrl.setHint(R.string.hint_rtsp);
    rtspCamera2 = new RtspCamera2(textureView, this);
    textureView.setSurfaceTextureListener(surfaceTextureListener);
  }

  @Override
  protected void onPause() {
    super.onPause();
    rtspCamera2.stopPreview();
  }

  @Override
  public void onConnectionSuccessRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(TextureModeRtspActivity.this, "Connection success", Toast.LENGTH_SHORT)
            .show();
      }
    });
  }

  @Override
  public void onConnectionFailedRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(TextureModeRtspActivity.this, "Connection failed", Toast.LENGTH_SHORT)
            .show();
        rtspCamera2.stopStream();
        button.setText(R.string.start_button);
      }
    });
  }

  @Override
  public void onDisconnectRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(TextureModeRtspActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthErrorRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(TextureModeRtspActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthSuccessRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(TextureModeRtspActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onClick(View view) {
    if (!rtspCamera2.isStreaming()) {
      if (rtspCamera2.prepareAudio() && rtspCamera2.prepareVideo()) {
        button.setText(R.string.stop_button);
        rtspCamera2.startStream(etUrl.getText().toString());
      } else {
        Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT)
            .show();
      }
    } else {
      button.setText(R.string.start_button);
      rtspCamera2.stopStream();
    }
  }

  /**
   * [TextureView.SurfaceTextureListener] handles several lifecycle events on a
   * [TextureView].
   */
  private TextureView.SurfaceTextureListener surfaceTextureListener =
      new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
          textureView.setAspectRatio(480, 640);
          rtspCamera2.startPreview();
          // optionally:
          // rtspCamera2.startPreview(CameraCharacteristics.LENS_FACING_BACK);
          // or
          //rtspCamera2.startPreview(CameraCharacteristics.LENS_FACING_FRONT);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
          rtspCamera2.stopPreview();
          return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
      };
}
