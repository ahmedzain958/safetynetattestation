package com.codinginflow.architectureexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.javapoet.MethodSpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* new DeviceUtils().isDeviceRooted(getApplicationContext())*/
        tv = findViewById(R.id.tv);
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS) {
            // The SafetyNet Attestation API is available.

            SafetyNet.getClient(this).attest(hexStringToByteArray("zain"), "AIzaSyDs6zfJNwxH8OUu97aJh28fm6dZzuir3zE")
                    .addOnSuccessListener(this,
                            new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                                @Override
                                public void onSuccess(final SafetyNetApi.AttestationResponse response) {
                                    // Indicates communication with the service was successful.
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    tv.setText(initialDataExtraction(response.getJwsResult()));

                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            OnlineVerify.process(response.getJwsResult());
                                        }
                                    };
                                    runnable.run();
                                }
                            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // An error occurred while communicating with the service.
                            if (e instanceof ApiException) {
                                // An error with the Google Play services API contains some
                                // additional details.
                                ApiException apiException = (ApiException) e;
                                // You can retrieve the status code using the
                                // apiException.getStatusCode() method.
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            } else {
                                // A different, unknown type of error occurred.
                                Log.d("TAG", "Error: " + e.getMessage());
                                Toast.makeText(MainActivity.this, "unknown exception", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }


    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String initialDataExtraction(final String jwsResult) {

        final String[] jwsResultParts = jwsResult.split("[.]");
        if (jwsResultParts.length == 3) {
            final byte[] header = Base64.decode(jwsResultParts[0], Base64.NO_WRAP);
            final byte[] data = Base64.decode(jwsResultParts[1], Base64.CRLF);
            final byte[] signature = Base64.decode(jwsResultParts[2], Base64.NO_WRAP);

            /*Log.d(TAG, "initialDataExtraction: header = " + new String(header, UTF_8));
            Log.d(TAG, "initialDataExtraction: data = " + new String(data, UTF_8));
            Log.d(TAG, "initialDataExtraction: signature = " + new String(signature, UTF_8));
*/
            return new String(data, UTF_8);
        } else {
//            Log.e(TAG, "initialDataExtraction: Failure: Illegal JWS signature format. The JWS consists of " + jwsResultParts.length + " parts instead of 3.");
            return null;
        }
    }

    public static class myClass {
        public static void main(String[] args) {
            MethodSpec sumOfTen = MethodSpec
                    .methodBuilder("sumOfTen")
                    .addStatement("int sum = 0")
                    .beginControlFlow("for (int i = 0; i <= 10; i++)")
                    .addStatement("System.out.println(i.toString())")
                    .addStatement("sum += i")
                    .endControlFlow()
                    .build();
            System.out.println("Hello Attia");
        }
    }


}

