package dfki.com.smartmaas.feedbackservice.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private final static String TAG = BaseActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            Log.e(TAG, "Class: " + paramThrowable.getClass() + " --- Cause: " + paramThrowable.getLocalizedMessage());
//                Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
        });
    }
}
