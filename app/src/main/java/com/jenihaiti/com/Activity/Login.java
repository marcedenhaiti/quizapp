package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.jenihaiti.com.Model.LoginRegiModel.LoginRegiModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    Map<String, String> map;

    LinearLayout lyGmail, lyFacebook, lyPassVisibility, lyForgotPassword;

    TextView txtSignin, txtRegister, ivPassVisible;
    EditText etEmail, etPassword;

    CallbackManager callbackManager;

    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";

    String strFirstname, strLastname, strEmail, strPassword, strType, strDeviceToken,
            fbName, fbEmail;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    MultipartBody.Part body;
    RequestBody firstName, lastName, email, password, type, mobileNumber, deviceToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Utility.screenCapOff(Login.this);

        prefManager = new PrefManager(Login.this);
        map = new HashMap<>();
        map = Utility.GetMap(Login.this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();
//        getResources().getString(R.string.facebook_app_id)

        init();
        strDeviceToken = Utility.getToken(Login.this);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("loginResult1", "Token::" + loginResult.getAccessToken());
                        Log.e("loginResult", "" + loginResult.getAccessToken().getToken());
                        AccessToken accessToken = loginResult.getAccessToken();
                        Log.e("loginResult3", "" + accessToken);
                        useLoginInformation(accessToken);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("exception", "" + exception.getMessage());
                    }
                });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void init() {
        try {
            lyGmail = findViewById(R.id.lyGmail);
            lyFacebook = findViewById(R.id.lyFacebook);
            lyPassVisibility = findViewById(R.id.lyPassVisibility);
            lyForgotPassword = findViewById(R.id.lyForgotPassword);

            txtSignin = findViewById(R.id.txtSignin);
            txtRegister = findViewById(R.id.txtRegister);

            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);

            ivPassVisible = findViewById(R.id.ivPassVisible);

            lyGmail.setOnClickListener(this);
            lyFacebook.setOnClickListener(this);
            lyPassVisibility.setOnClickListener(this);
            txtSignin.setOnClickListener(this);
            txtRegister.setOnClickListener(this);
            lyForgotPassword.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @SuppressLint("CheckResult")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyPassVisibility:
                if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    Log.e("etPassword ==>", "" + etPassword.getText().toString());
                    Toasty.warning(Login.this, "Enter Password", Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    Log.e("Visible Password ", "" + etPassword.getText().toString());
                    ivPassVisible.setBackground(getResources().getDrawable(R.drawable.ic_pass_invisible));
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    Log.e("Invisible Password", "" + etPassword.getText().toString());
                    ivPassVisible.setBackground(getResources().getDrawable(R.drawable.ic_pass_visible));
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;

            case R.id.lyGmail:
                Log.e("gMail", "perform");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
                break;

            case R.id.lyFacebook:
                mGoogleSignInClient.signOut();
                Log.e("fb", "facebook");
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL, PROFILE));
                break;

            case R.id.txtSignin:
                strEmail = etEmail.getText().toString();
                strPassword = etPassword.getText().toString();

                if (TextUtils.isEmpty(strEmail)) {
                    Toasty.warning(Login.this, "Enter email", Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strPassword)) {
                    Toasty.warning(Login.this, "Enter password", Toasty.LENGTH_SHORT).show();
                    return;
                }

                strType = "1";
                Log.e("==>deviceToken", "" + strDeviceToken);

//                email = RequestBody.create(MediaType.parse("text/plain"), "" + strEmail);
//                password = RequestBody.create(MediaType.parse("text/plain"), "" + strPassword);
//                type = RequestBody.create(MediaType.parse("text/plain"), "1");

                SignIn();

                break;

            case R.id.txtRegister:
                startActivity(new Intent(Login.this, Registration.class));
                break;

            case R.id.lyForgotPassword:
                startActivity(new Intent(Login.this, ForgotActivity.class));
                break;
        }
    }

    //login API
    public void SignIn() {
        if (!((Activity) Login.this).isFinishing()) {
            Utility.ProgressBarShow(Login.this);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<LoginRegiModel> call = bookNPlayAPI.login(strEmail, strPassword, strType, strDeviceToken);
        call.enqueue(new Callback<LoginRegiModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginRegiModel> call, @NonNull Response<LoginRegiModel> response) {
                try {
                    Log.e("Status", "" + response.body().getStatus());

                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("email ==>", "" + response.body().getResult().get(0).getEmail());
                        prefManager.setLoginId("" + response.body().getResult().get(0).getId());
                        prefManager.setValue("Email", "" + response.body().getResult().get(0).getEmail());
                        prefManager.setValue("Phone", "" + response.body().getResult().get(0).getMobileNumber());
                        prefManager.setValue("username", "" + response.body().getResult().get(0).getFirstName());
                        Log.e("LoginId ==>", "" + prefManager.getLoginId());
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Login.this.finish();
                        Toasty.success(Login.this, "" + response.body().getMessage(),
                                Toasty.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(Login.this, R.style.AlertDialogDanger)
                                .setTitle("" + getResources().getString(R.string.app_name))
                                .setMessage("" + response.body().getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                    }
                                }).show();
                    }

                } catch (Exception e) {
                    Log.e("login Exception ==>", "" + e);
                }
                Utility.ProgressbarHide();
            }

            @Override
            public void onFailure(@NonNull Call<LoginRegiModel> call, @NonNull Throwable t) {
                Log.e("Throwable", "" + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    //login API
    public void SignIn_Social() {
        if (!((Activity) Login.this).isFinishing()) {
            Utility.ProgressBarShow(Login.this);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<LoginRegiModel> call = bookNPlayAPI.login(firstName, email, password, type, deviceToken, body);
        call.enqueue(new Callback<LoginRegiModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginRegiModel> call, @NonNull Response<LoginRegiModel> response) {
                try {
                    Log.e("Status", "" + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("email ==>", "" + response.body().getResult().get(0).getEmail());
                        prefManager.setLoginId("" + response.body().getResult().get(0).getId());
                        prefManager.setValue("Email", "" + response.body().getResult().get(0).getEmail());
                        prefManager.setValue("Phone", "" + response.body().getResult().get(0).getMobileNumber());
                        prefManager.setValue("username", "" + response.body().getResult().get(0).getFirstName());
                        Log.e("LoginId ==>", "" + prefManager.getLoginId());
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Login.this.finish();
                        Toasty.success(Login.this, "" + response.body().getMessage(),
                                Toasty.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(Login.this, R.style.AlertDialogDanger)
                                .setTitle("" + getResources().getString(R.string.app_name))
                                .setMessage("" + response.body().getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                    }
                                }).show();
                    }
                } catch (Exception e) {
                    Log.e("login Exception ==>", "" + e);
                }
                Utility.ProgressbarHide();
            }

            @Override
            public void onFailure(@NonNull Call<LoginRegiModel> call, @NonNull Throwable t) {
                Log.e("login Throwable", "" + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    private void useLoginInformation(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    if (object != null) {

                        String f_name = object.optString("firstName");
                        String l_name = object.optString("lastName");
                        fbEmail = object.optString("email");
                        String id = object.optString("id");
                        String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                        Log.e("Firstname", "" + f_name);
                        Log.e("lastName", "" + l_name);
                        Log.e("fbEmail", "" + fbEmail);
                        Log.e("id", "" + id);
                        Log.e("image_url", "" + image_url);

                        fbEmail = object.optString("email");
                        fbName = f_name + l_name;

                        if (fbEmail.length() == 0) {
                            fbEmail = fbName.trim() + "@facebook.com";
                        }
                        Log.e("name", "" + fbName);
                        Log.e("email", "" + fbEmail);

                        firstName = RequestBody.create(MediaType.parse("text/plain"), "" + fbName);
                        lastName = RequestBody.create(MediaType.parse("text/plain"), "" + l_name);
                        email = RequestBody.create(MediaType.parse("text/plain"), "" + fbEmail);
                        type = RequestBody.create(MediaType.parse("text/plain"), "2");
                        mobileNumber = RequestBody.create(MediaType.parse("text/plain"), "");
                        password = RequestBody.create(MediaType.parse("text/plain"), "");
                        deviceToken = RequestBody.create(MediaType.parse("text/plain"), "" + strDeviceToken);

                        if (image_url != null)
                            new DownloadTask().execute("" + image_url);
                        else
                            SignIn_Social();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("facebook Exception", "" + e.getMessage());
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "firstName,lastName,email,id");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.e("getDisplayName", "" + account.getDisplayName());
            Log.e("getDisplayName2", "" + account.getGivenName());
            Log.e("getEmail", "" + account.getEmail());
            Log.e("getIdToken", "" + account.getIdToken());
            Log.e("getPhotoUrl", "" + account.getPhotoUrl());

            strFirstname = "" + account.getDisplayName();

            strLastname = "";
            strEmail = "" + account.getEmail();

            firstName = RequestBody.create(MediaType.parse("text/plain"), "" + strFirstname);
            email = RequestBody.create(MediaType.parse("text/plain"), "" + strEmail);
            password = RequestBody.create(MediaType.parse("text/plain"), "");
            type = RequestBody.create(MediaType.parse("text/plain"), "2");
            deviceToken = RequestBody.create(MediaType.parse("text/plain"), "" + strDeviceToken);

            if (account.getPhotoUrl() != null)
                new DownloadTask().execute("" + account.getPhotoUrl());
            else
                SignIn_Social();

        } catch (ApiException e) {
            Log.e("ApiException", "signInResult:failed code = " + e.getStatusCode());
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, Bitmap> {
        protected void onPreExecute() {
        }

        protected Bitmap doInBackground(String... url) {
            String imageURL = url[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result) {
            // Hide the progress dialog
            Log.e("==bitmap=>", "" + result);
            if (result != null) {
                new fileFromBitmap(result, getApplicationContext()).execute();
            } else {
                // Notify user that an error occurred while downloading image
                Toasty.error(Login.this, "Error", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    public class fileFromBitmap extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;
        String path_external = Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg";
        File file_thumbnail;

        public fileFromBitmap(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            Log.e("ts", "" + ts);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            file_thumbnail = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "" + ts + ".jpg");
            try {
                FileOutputStream fo = new FileOutputStream(file_thumbnail);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("==>fileName", "" + file_thumbnail.getPath());

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file_thumbnail);
            body = MultipartBody.Part.createFormData("profile_img", file_thumbnail.getName(), requestFile);

            Log.e("email", "" + email);

            SignIn_Social();
        }
    }

}