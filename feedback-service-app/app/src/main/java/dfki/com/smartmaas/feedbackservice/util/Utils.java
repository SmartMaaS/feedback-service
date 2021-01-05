package dfki.com.smartmaas.feedbackservice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.activity.MainActivity;
import dfki.com.smartmaas.feedbackservice.exception.InvalidLocationNameException;


public class Utils {
    private static final String TAG = Utils.class.getName();
    private static LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            1.0f
    );
    private static LinearLayout.LayoutParams buttonLayoutPrms = new LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);

    public static void setBlinking(View view) {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(350);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }

    public static LinearLayout.LayoutParams getConsLayParams() {
        return layoutParams;
    }

    /*
    @throws IOException if input latitude and longitude cannot be converted to a valid address.
     */
    public static String convertLatLongToAddress(double lat, double lng, MainActivity mainActivity) throws IOException {
        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
        List<Address> addresses;
        addresses = geocoder.getFromLocation(lat, lng, 1);
        return addresses.get(0).getAddressLine(0);
    }

    /*
    @throws IOException if input address name cannot be converted to latitude and longitude.
    @throws InvalidLocationNameException  if input address name is not a valid address.
     */
    public static HashMap<String, Double> convertAddressToLatLng(String addressName, MainActivity mainActivity) throws IOException, InvalidLocationNameException {
        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocationName(addressName, 1);
        if (addresses.size() == 0) {
            throw new InvalidLocationNameException("Location is invalid!");
        }
        HashMap<String, Double> location = new HashMap<>();
        location.put("latitude", addresses.get(0).getLatitude());
        location.put("longitude", addresses.get(0).getLongitude());

        return location;
    }

    /*
    @throws JsonProcessingException if input object is not a valid json. There might be other causes as well.
     */
    public static String convertToJson(Object object) throws JsonProcessingException {
        if (object == null)
            return "";
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(object);
    }

    public static String serializeToXml(Object object) throws Exception {
        if (object == null)
            return "";
        Serializer serializer = new Persister();
        StringWriter stringWriter = new StringWriter();
        serializer.write(object, stringWriter);

        return stringWriter.toString();
    }

    public static Object deserializeToObject(String xml, Object object) throws Exception {
        Serializer serializer = new Persister();
        object = serializer.read(object.getClass(), xml);
        return object;
    }

    public static void sendRequestToFeedbackWebService(Context context, String data, String webServiceUrl, String contentType,
                                                       HashMap<String, String> moreHeaders,
                                                       String successMsg, String errorMsg) throws IOException {
        HttpsURLConnection httpsURLConnection;

        URL request_url;
        try {
            request_url = new URL(webServiceUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String erMsg = "URL(" + webServiceUrl + ") cannot be parsed.";
//            Log.e(TAG, erMsg);
            throw new MalformedURLException(erMsg);
        }

        try {
            httpsURLConnection = (HttpsURLConnection) request_url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            String erMsg = "URL connection couldn't be opened for the following adress(" + request_url + ").";
            throw new IOException(erMsg);
        }
        try {
            httpsURLConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
            String erMsg = "Cannot set POST as request method to url connection";
            throw new ProtocolException(erMsg);

        }
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return context.getResources().getString(R.string.feedback_web_service_url).contains(hostname);
            }
        });
        httpsURLConnection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
        try {
            httpsURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            String erMsg = "URL connection couldn't be connected for the following adress(" + httpsURLConnection.getURL() + ").";
            throw new IOException(erMsg);
        }

    }

    public static void postToFeedbWS(Context context, String data, String webServiceUrl, String contentType,
                                     HashMap<String, String> moreHeaders,
                                     String successMsg, String errorMsg) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, webServiceUrl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        makeShortToast(context, successMsg);
                        Log.i(TAG, "\n" + context.getResources()
                                .getString(R.string.fbs_response_message) + "\n" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeShortToast(context, errorMsg);
                Log.e(TAG, context.getResources().getString(R.string.fbs_error_message)
                        + error.getMessage());
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                return data.getBytes(Charset.defaultCharset());
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(context.getResources()
                        .getString(R.string.headers_content_type_message), contentType);
                for (String key : moreHeaders.keySet()) {
                    String value = moreHeaders.get(key);
                    headers.put(key, value);
                }
                return headers;
            }


        };


        Volley.newRequestQueue(context).add(stringRequest);

//        Volley.newRequestQueue(context, new HurlStack(null, getSocketFactory(context))).add(stringRequest);
    }

    public static void postToGTFSLD(Context context, String data, String webServiceUrl, String contentType,
                                    HashMap<String, String> moreHeaders, Map<String, String> params,
                                    String successMsg, String errorMsg) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, webServiceUrl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        makeShortToast(context, successMsg);
                        Log.i(TAG, "\n" + context.getResources()
                                .getString(R.string.fbs_response_message) + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeShortToast(context, errorMsg);
                Log.e(TAG, context.getResources().getString(R.string.fbs_error_message)
                        + error.getMessage());
            }
        }) {

//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return data.getBytes(Charset.defaultCharset());
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put(context.getResources()
//                        .getString(R.string.headers_content_type_message), contentType);
//                for (String key : moreHeaders.keySet()) {
//                    String value = moreHeaders.get(key);
//                    headers.put(key, value);
//                }
//                return headers;
//            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };


        Volley.newRequestQueue(context).add(stringRequest);

//        Volley.newRequestQueue(context, new HurlStack(null, getSocketFactory(context))).add(stringRequest);
    }

    public static SSLSocketFactory getSocketFactory(Context appContext) {

        CertificateFactory cf = null;
        try {

            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = appContext.getResources().openRawResource(R.raw.feedservcertificate);
            Certificate ca;
            try {

                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }


            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    Log.e("CipherUsed", session.getCipherSuite());
                    String serverURL = appContext.getResources().getString(R.string.feedback_web_service_url);
                    return serverURL.contains(hostname);
//                    return hostname.compareTo()==0; //The Hostname of your server.

                }
            };


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext context = null;
            context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            SSLSocketFactory sf = context.getSocketFactory();


            return sf;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String removeAllSpaces(String trimMe) {
        StringBuilder trimmed = new StringBuilder();
        for (String s : trimMe.split(" ")) {
            trimmed.append(s);
        }
        return trimmed.toString();

    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void requestFocusAndShowKeyboard(Context context, View view) {
        view.requestFocus();
        showKeyboard(context, view);
    }

    public static void hideNavigationBottomView(ConstraintLayout constraintLayout) {
        LinearLayout.LayoutParams params = Utils.getConsLayParams();
        params.weight = 0;
        constraintLayout.setLayoutParams(params);
    }

    public static void showNavigationBottomView(ConstraintLayout constraintLayout) {
        LinearLayout.LayoutParams params = Utils.getConsLayParams();
        params.weight = 1;
        constraintLayout.setLayoutParams(params);
    }

    public static void saveStringToPreferences(Context context, String key, String value) {
        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences_name), Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(key, value);
        sharedPreferences.apply();
    }

    public static String fetchStringFromPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);

        return sharedPreferences.getString(key, context.getResources().
                getString(R.string.no_data_found_shrd_prfs));
    }

    public static void cleanSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

    }

    public static void makeShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        return dateFormat.format(currentTime);
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }

}
