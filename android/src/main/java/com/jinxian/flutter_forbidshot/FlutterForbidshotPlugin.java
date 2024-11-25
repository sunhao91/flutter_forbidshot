package com.jinxian.flutter_forbidshot;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.media.AudioManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterForbidshotPlugin */
public class FlutterForbidshotPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

  private MethodChannel methodCall;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    methodCall = new MethodChannel(binding.getBinaryMessenger(), "flutter_forbidshot");
    methodCall.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    methodCall.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "setOn":
        if (activity != null && activity.getWindow() != null) {
          activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        break;
      case "setOff":
        if (activity != null && activity.getWindow() != null) {
          activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        break;
      case "volume":
        result.success(getVolume());
        break;
      case "setVolume":
        double volume = call.argument("volume");
        setVolume(volume);
        result.success(null);
        break;
    }
  }

  AudioManager audioManager;
  private float getVolume() {
    if (audioManager == null) {
      audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }
    float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    float current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    float target = current / max;

    return target;
  }

  private void setVolume(double volume) {
    int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (max * volume), AudioManager.FLAG_PLAY_SOUND);
  }
}
