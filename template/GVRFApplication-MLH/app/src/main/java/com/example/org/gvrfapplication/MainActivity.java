package com.example.org.gvrfapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.transition.Scene;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import org.gearvrf.GVRActivity;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCamera;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRHybridObject;
import org.gearvrf.GVRImage;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScreenshot3DCallback;
import org.gearvrf.GVRScreenshotCallback;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRCameraSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.scene_objects.view.GVRTextView;
import org.gearvrf.scene_objects.view.GVRView;
import org.gearvrf.utility.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends GVRActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Set Main Scene
         * It will be displayed when app starts
         */
        setMain(new Main());
    }

    private final class Main extends GVRMain  {
        GVRSceneObject mCube;
        private GVRCameraSceneObject obj;
        private Bitmap sb;
        FileOutputStream out = null;

        GVRScreenshotCallback callback = new GVRScreenshotCallback() {
            @Override
            public void onScreenCaptured(Bitmap bitmap) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();


                String temp = Base64.encodeToString(b, Base64.DEFAULT);
                Log.e("HERE IS A FUCKING BITMAP", temp);
                File f1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Kushang.txt");
                FileWriter stream = null;
                try {
                    stream = new FileWriter(f1);
                    stream.append(temp);
                    stream.flush();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Create a new HttpClient and Post Header

                try {

                    String url = "http://18.219.138.54:1222/people/find/connection";
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    //add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    //Request Parameters you want to send
                    String urlParameters = "id=C02G8416DRJM&image="+temp;

                    // Send post request
                    con.setDoOutput(true);// Should be part of code only for .Net web-services else no need for PHP
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    System.out.println("\nSending 'POST' request to URL : " + url);
                    System.out.println("Post parameters : " + urlParameters);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    Log.e("Server response", response.toString());

                    // Create connection
//                    URL url = new URL("http://18.219.138.54:1222/people/find/connection");
//
//                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                    conn.setReadTimeout(10000);
//                    conn.setConnectTimeout(15000);
//                    conn.setRequestMethod("POST");
//                    conn.setDoInput(true);
//                    conn.setDoOutput(true);
//
//                    Uri.Builder builder = new Uri.Builder()
//                            .appendQueryParameter("id", "1")
//                            .appendQueryParameter("image", temp);
//
//                    String query = builder.build().getEncodedQuery();
//
//                    OutputStream os = conn.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(
//                            new OutputStreamWriter(os, "UTF-8"));
//                    writer.write(query);
//                    writer.flush();
//                    writer.close();
//                    os.close();
//                    conn.connect();
//                    Log.e("Server response", writer.toString());

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }









            }


        };
        private GVRTextViewSceneObject textViewSceneObject;



        @Override
        public void onInit(final GVRContext gvrContext) throws Throwable {

            obj = new GVRCameraSceneObject(gvrContext,5.0f, 5.0f);
            obj.setUpCameraForVrMode(1);
            obj.getTransform().setPosition(0.0f, 0.0f, -4.0f);
            gvrContext.getMainScene().getMainCameraRig().addChildObject(obj);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    gvrContext.captureScreenCenter(callback);
                }
            }, 2000);   //5 seconds


           textViewSceneObject = new GVRTextViewSceneObject(gvrContext);
           textViewSceneObject.setText("HELLO FUCKING WORLD");
           textViewSceneObject.setTextSize(7f);
           textViewSceneObject.setTextColor(Color.RED);
           textViewSceneObject.getTransform().setPosition(1.2f, 1.0f, -4.0f);
           textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
           gvrContext.getMainScene().getMainCameraRig().addChildObject(textViewSceneObject);
           //Toast.makeText(getApplicationContext(), textViewSceneObject.getText(), Toast.LENGTH_LONG).show();

            textViewSceneObject = new GVRTextViewSceneObject(gvrContext);
            textViewSceneObject.setText("This is new text");
            textViewSceneObject.setTextSize(7f);
            textViewSceneObject.setTextColor(Color.RED);
            textViewSceneObject.getTransform().setPosition(1.2f, 0.5f, -4.0f);
            textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
            gvrContext.getMainScene().getMainCameraRig().addChildObject(textViewSceneObject);

            //Toast.makeText(getApplicationContext(), textViewSceneObject.getText(), Toast.LENGTH_LONG).show();





        }



        @Override
        public SplashMode getSplashMode() {
            return SplashMode.NONE;
        }

        @Override
        public void onStep() {
            //Add update logic here
//
//            float x =  textViewSceneObject.getTransform().getPositionX();
//            float y=  textViewSceneObject.getTransform().getPositionY();
//            float x = getGVRContext().getMainScene().
//            Log.e("X value", String.valueOf(x));
//            Log.e("Y value", String.valueOf(y));
//            textViewSceneObject.getTransform().setPosition(0.0f, 0.0f, -4.0f);
//            getGVRContext().getMainScene().addSceneObject(textViewSceneObject);
        }
    }
}
