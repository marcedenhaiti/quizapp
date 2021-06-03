package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.jenihaiti.com.Model.QuestionModel.QuestionModel;
import com.jenihaiti.com.Model.QuestionModel.Result;
import com.jenihaiti.com.Model.SuccessModel.SuccessModel;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.PrefManager;
import com.jenihaiti.com.Util.Utility;
import com.jenihaiti.com.Webservice.AppAPI;
import com.jenihaiti.com.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.iwgang.countdownview.CountdownView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionAnswer extends AppCompatActivity implements View.OnClickListener,
        CountdownView.OnCountdownEndListener, CountdownView.OnCountdownIntervalListener {

    private PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;

    ImageView ivQuestion;
    TextView txtToolbarTitle, txtQuestion, txtLevelNumber, txtQueNumber, txtTotalQue,
            txtRightAnswers, txtWrongAnswers, txtA, txtB, txtC, txtD, txtOptionA, txtOptionB,
            txtOptionC, txtOptionD, txtTickA, txtTickB, txtTickC, txtTickD,
            txtAnswerit, txtNext, txtAnswerStatus;

    CountdownView countdownTimer;

    List<Result> questionList;

    LinearLayout lyToolbar, lyBack, lyBottom, lyOptionA, lyOptionB, lyOptionC, lyOptionD;

    String levelID, catID, strAnswer = "", TotalLevel, currentLevel;
    int QueNo = 0, rightAnswers = 0, wrongAnswers = 0, attendedQue = 0;

    MediaPlayer mpWrong, mpCorrect;
    Vibrator vibe;

    boolean isAnswered = false, isSelected = false;

    public static Handler handler;
    public static Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);
        vibe = (Vibrator) QuestionAnswer.this.getSystemService(Context.VIBRATOR_SERVICE);

        init();
        GetQuestionByLevel();

        txtToolbarTitle.setText("Questions");

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionAnswer.this.finish();
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                isAnswered = true;
                if (isAnswered && isSelected) {
                    attendedQue = attendedQue + 1;
                    isSelected = false;
                }
                updateUI();

                if (txtNext.getText().toString().equalsIgnoreCase(getString(R.string.finish))) {
                    Log.e("lavel_id ==>", "" + levelID);
                    Log.e("questions_attended ==>", "" + attendedQue);
                    Log.e("total_questions ==>", "" + questionList.size());
                    Log.e("correct_answers ==>", "" + rightAnswers);
                    Log.e("user_id ==>", "" + prefManager.getLoginId());
                    Save_Question_Report();

                } else {
                    Log.e("QueNo ==>", "" + QueNo);
                    if (questionList.size() > 1) {
                        if (QueNo == (questionList.size() - 2)) {
                            txtNext.setText(getResources().getString(R.string.finish));
                        }

                        QueNo = QueNo + 1;

                        if (!questionList.get(QueNo).getImage().equalsIgnoreCase("")) {
                            ivQuestion.setVisibility(View.VISIBLE);
                            Picasso.get().load(questionList.get(QueNo).getImage()).into(ivQuestion);
                        } else {
                            ivQuestion.setVisibility(View.GONE);
                        }
                        txtQuestion.setText("" + questionList.get(QueNo).getQuestion());
                        txtOptionA.setText("" + questionList.get(QueNo).getOptionA());
                        txtOptionB.setText("" + questionList.get(QueNo).getOptionB());
                        if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                            lyOptionC.setVisibility(View.INVISIBLE);
                        } else {
                            lyOptionC.setVisibility(View.VISIBLE);
                            txtOptionC.setText("" + questionList.get(QueNo).getOptionC());
                        }
                        if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                            lyOptionD.setVisibility(View.INVISIBLE);
                        } else {
                            lyOptionD.setVisibility(View.VISIBLE);
                            txtOptionD.setText("" + questionList.get(QueNo).getOptionD());
                        }
                        txtLevelNumber.setText("" + currentLevel);
                        txtQueNumber.setText("" + (QueNo + 1));
                        txtTotalQue.setText(" / " + questionList.size());
                        countdownTimer.start(16000);
                    } else {
                        txtNext.setText(getResources().getString(R.string.finish));
                    }
                }
            }
        };

    }

    private void init() {
        try {
            prefManager = new PrefManager(QuestionAnswer.this);
            map = new HashMap<>();
            map = Utility.GetMap(QuestionAnswer.this);

            Intent intent = getIntent();
            if (intent.hasExtra("catID") && intent.hasExtra("levelID")) {
                catID = intent.getStringExtra("catID");
                levelID = intent.getStringExtra("levelID");
                TotalLevel = intent.getStringExtra("TotalLevel");
                currentLevel = intent.getStringExtra("currentLevel");
                Log.e("catID ==>", "" + catID);
                Log.e("levelID ==>", "" + levelID);
                Log.e("TotalLevel ==>", "" + TotalLevel);
                Log.e("currentLevel ==>", "" + currentLevel);
            }

            shimmer = findViewById(R.id.shimmer);
            lyBottom = findViewById(R.id.lyBottom);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            countdownTimer = findViewById(R.id.countdown_timer);
            countdownTimer.setOnCountdownEndListener(this);
            countdownTimer.setOnCountdownIntervalListener(10000, this);

            ivQuestion = findViewById(R.id.ivQuestion);
            txtLevelNumber = findViewById(R.id.txtLevelNumber);
            txtQueNumber = findViewById(R.id.txtQueNumber);
            txtTotalQue = findViewById(R.id.txtTotalQue);
            txtQuestion = findViewById(R.id.txtQuestion);
            txtRightAnswers = findViewById(R.id.txtRightAnswers);
            txtWrongAnswers = findViewById(R.id.txtWrongAnswers);
            txtA = findViewById(R.id.txtA);
            txtB = findViewById(R.id.txtB);
            txtC = findViewById(R.id.txtC);
            txtD = findViewById(R.id.txtD);
            txtTickA = findViewById(R.id.txtTickA);
            txtTickB = findViewById(R.id.txtTickB);
            txtTickC = findViewById(R.id.txtTickC);
            txtTickD = findViewById(R.id.txtTickD);
            txtOptionA = findViewById(R.id.txtOptionA);
            txtOptionB = findViewById(R.id.txtOptionB);
            txtOptionC = findViewById(R.id.txtOptionC);
            txtOptionD = findViewById(R.id.txtOptionD);
            txtAnswerStatus = findViewById(R.id.txtAnswerStatus);
            txtAnswerit = findViewById(R.id.txtAnswerit);
            txtNext = findViewById(R.id.txtNext);

            lyOptionA = findViewById(R.id.lyOptionA);
            lyOptionB = findViewById(R.id.lyOptionB);
            lyOptionC = findViewById(R.id.lyOptionC);
            lyOptionD = findViewById(R.id.lyOptionD);

            lyBack.setOnClickListener(this);
            txtAnswerit.setOnClickListener(this);
            txtNext.setOnClickListener(this);
            lyOptionA.setOnClickListener(this);
            lyOptionB.setOnClickListener(this);
            lyOptionC.setOnClickListener(this);
            lyOptionD.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                QuestionAnswer.this.finish();
                break;

            case R.id.txtAnswerit:
                Log.e("countdownTimer ==>", "" + countdownTimer.getSecond());
                Log.e("QueNo ==>", "" + QueNo);
                Log.e("strAnswer ==>", "" + strAnswer);
                Log.e("Answer ==>", "" + questionList.get(QueNo).getAnswer());
                onAnswerIt();
                break;

            case R.id.txtNext:
                onNextFinish();
                break;

            case R.id.lyOptionA:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "1";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickA.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtA.setTextColor(getResources().getColor(R.color.white));
                    txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionA.setTextColor(getResources().getColor(R.color.white));
                    txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;

            case R.id.lyOptionB:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "2";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickB.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtB.setTextColor(getResources().getColor(R.color.white));
                    txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionB.setTextColor(getResources().getColor(R.color.white));
                    txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;

            case R.id.lyOptionC:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "3";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickC.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtC.setTextColor(getResources().getColor(R.color.white));
                    txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.white));
                    txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;

            case R.id.lyOptionD:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "4";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickD.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtD.setTextColor(getResources().getColor(R.color.white));
                    txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.white));
                    txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;
        }
    }

    //get_lavel API call
    private void GetQuestionByLevel() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<QuestionModel> call = bookNPlayAPI.get_question_by_lavel("" + catID, "" + levelID);
        call.enqueue(new Callback<QuestionModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<QuestionModel> call, @NonNull Response<QuestionModel> response) {
                try {
                    Log.e("get_question_by_lavel status =>", "" + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            questionList = new ArrayList<>();
                            questionList = response.body().getResult();
                            Log.e("questionList size", "" + questionList.size());

                            if (!questionList.get(0).getImage().equalsIgnoreCase("")) {
                                ivQuestion.setVisibility(View.VISIBLE);
                                Picasso.get().load(questionList.get(0).getImage()).into(ivQuestion);
                            } else {
                                ivQuestion.setVisibility(View.GONE);
                            }
                            txtQuestion.setText("" + questionList.get(0).getQuestion());
                            txtOptionA.setText("" + questionList.get(0).getOptionA());
                            txtOptionB.setText("" + questionList.get(0).getOptionB());
                            if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                                lyOptionC.setVisibility(View.INVISIBLE);
                            } else {
                                lyOptionC.setVisibility(View.VISIBLE);
                                txtOptionC.setText("" + questionList.get(0).getOptionC());
                            }
                            if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                                lyOptionD.setVisibility(View.INVISIBLE);
                            } else {
                                lyOptionD.setVisibility(View.VISIBLE);
                                txtOptionD.setText("" + questionList.get(0).getOptionD());
                            }

                            txtLevelNumber.setText("" + currentLevel);
                            txtQueNumber.setText("" + (QueNo + 1));
                            txtRightAnswers.setText("Right " + rightAnswers);
                            txtWrongAnswers.setText("Wrong " + wrongAnswers);

                            txtTotalQue.setText(" / " + questionList.size());

                            if(questionList.size()==1){
                                txtNext.setText(getResources().getString(R.string.finish));
                            }
                            countdownTimer.start(16000);
                        }

                    } else {
                        Log.e("get_question_by_lavel massage =>", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_question_by_lavel API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<QuestionModel> call, @NonNull Throwable t) {
                Log.e("get_question_by_lavel Failure", "That didn't work!!!" + t.getMessage());
                Utility.shimmerHide(shimmer);
            }
        });
    }

    @Override
    public void onEnd(CountdownView cv) {
        Log.e("cv ==>", "" + cv.getSecond());
        Log.e("onEnd QueNo ==>", "" + QueNo);
        Log.e("onEnd strAnswer ==>", "" + strAnswer);
        Log.e("onEnd rightAnswers ==>", "" + rightAnswers);
        Log.e("onEnd wrongAnswers ==>", "" + wrongAnswers);

        isAnswered = true;
        if (isAnswered && isSelected) {
            attendedQue = attendedQue + 1;
            isSelected = false;
        }

        updateUI();

        if (txtNext.getText().toString().equalsIgnoreCase(getString(R.string.finish))) {
            Log.e("lavel_id ==>", "" + levelID);
            Log.e("questions_attended ==>", "" + attendedQue);
            Log.e("total_questions ==>", "" + questionList.size());
            Log.e("correct_answers ==>", "" + rightAnswers);
            Log.e("user_id ==>", "" + prefManager.getLoginId());
            Save_Question_Report();
        }

        if (QueNo != (questionList.size() - 1)) {

            if (questionList.size() > 1) {
                Log.e("QueNo ==>", "" + QueNo);
                if (QueNo == (questionList.size() - 2)) {
                    txtNext.setText(getResources().getString(R.string.finish));
                }

                QueNo = QueNo + 1;

                if (!questionList.get(QueNo).getImage().equalsIgnoreCase("")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Picasso.get().load(questionList.get(QueNo).getImage()).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
                txtQuestion.setText("" + questionList.get(QueNo).getQuestion());
                txtOptionA.setText("" + questionList.get(QueNo).getOptionA());
                txtOptionB.setText("" + questionList.get(QueNo).getOptionB());
                if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                    lyOptionC.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionC.setVisibility(View.VISIBLE);
                    txtOptionC.setText("" + questionList.get(QueNo).getOptionC());
                }
                if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                    lyOptionD.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionD.setVisibility(View.VISIBLE);
                    txtOptionD.setText("" + questionList.get(QueNo).getOptionD());
                }
                txtLevelNumber.setText("" + currentLevel);
                txtQueNumber.setText("" + (QueNo + 1));
                txtTotalQue.setText(" / " + questionList.size());
                countdownTimer.start(16000);
            } else {
                txtNext.setText(getResources().getString(R.string.finish));
            }

        } else {
            if (cv.getSecond() == 0) {
                isAnswered = true;
                txtAnswerStatus.setVisibility(View.VISIBLE);
                txtAnswerStatus.setTextColor(getResources().getColor(R.color.Red));
                txtAnswerStatus.setText(getResources().getString(R.string.time_over_status));
            }
        }
    }

    @Override
    public void onInterval(CountdownView cv, long remainTime) {
        Log.e("==>remainTime", "" + remainTime / 1000);
    }

    private void onNextFinish() {
        handler.removeCallbacks(runnable);
        isAnswered = true;
        if (isAnswered && isSelected) {
            attendedQue = attendedQue + 1;
            isSelected = false;
        }
        updateUI();

        if (txtNext.getText().toString().equalsIgnoreCase(getString(R.string.finish))) {
            Log.e("lavel_id ==>", "" + levelID);
            Log.e("questions_attended ==>", "" + attendedQue);
            Log.e("total_questions ==>", "" + questionList.size());
            Log.e("correct_answers ==>", "" + rightAnswers);
            Log.e("user_id ==>", "" + prefManager.getLoginId());
            Save_Question_Report();

        } else {
            Log.e("QueNo ==>", "" + QueNo);
            if (questionList.size() > 1) {

                if (QueNo == (questionList.size() - 2)) {
                    txtNext.setText(getResources().getString(R.string.finish));
                }
                QueNo = QueNo + 1;

                if (!questionList.get(QueNo).getImage().equalsIgnoreCase("")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Picasso.get().load(questionList.get(QueNo).getImage()).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
                txtQuestion.setText("" + questionList.get(QueNo).getQuestion());
                txtOptionA.setText("" + questionList.get(QueNo).getOptionA());
                txtOptionB.setText("" + questionList.get(QueNo).getOptionB());
                if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                    lyOptionC.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionC.setVisibility(View.VISIBLE);
                    txtOptionC.setText("" + questionList.get(QueNo).getOptionC());
                }
                if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                    lyOptionD.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionD.setVisibility(View.VISIBLE);
                    txtOptionD.setText("" + questionList.get(QueNo).getOptionD());
                }
                txtLevelNumber.setText("" + currentLevel);
                txtQueNumber.setText("" + (QueNo + 1));
                txtTotalQue.setText(" / " + questionList.size());
                countdownTimer.start(16000);

            } else {
                txtNext.setText(getResources().getString(R.string.finish));
            }
        }
    }

    private void onAnswerIt() {
        if (countdownTimer.getSecond() != 0) {
            if (!strAnswer.equalsIgnoreCase("")) {
                if (!isAnswered) {
                    isAnswered = true;
                    txtAnswerStatus.setVisibility(View.VISIBLE);
                    Log.e("answer==>", "" + countdownTimer.getSecond());
                    if (strAnswer.equalsIgnoreCase(questionList.get(QueNo).getAnswer())) {
                        mpCorrect = MediaPlayer.create(QuestionAnswer.this, R.raw.correct_ans_sound);
                        if (prefManager.getBool("SOUND") == true) {
                            mpCorrect.start();
                        }
                        if (prefManager.getBool("VIBRATION") == true) {
                            vibe.vibrate(100);
                        }

                        txtAnswerStatus.setTextColor(getResources().getColor(R.color.text_blue));
                        txtAnswerStatus.setText(getResources().getString(R.string.correct_answer_status));
                    } else {
                        mpWrong = MediaPlayer.create(QuestionAnswer.this, R.raw.wrong_ans_sound);
                        if (prefManager.getBool("SOUND") == true) {
                            mpWrong.start();
                        }
                        if (prefManager.getBool("VIBRATION") == true) {
                            vibe.vibrate(500);
                        }

                        txtAnswerStatus.setTextColor(getResources().getColor(R.color.Red));
                        txtAnswerStatus.setText(getResources().getString(R.string.wrong_answer_status));
                    }

                    CorrectUI();
                    handler.postDelayed(runnable, 3000);
                }
            } else {
                Toasty.info(QuestionAnswer.this, "Please, select any option.",
                        Toasty.LENGTH_SHORT).show();
            }
        } else {
            txtAnswerStatus.setVisibility(View.VISIBLE);
            txtAnswerStatus.setTextColor(getResources().getColor(R.color.Red));
            txtAnswerStatus.setText(getResources().getString(R.string.time_over_status));
        }
    }

    private void updateUI() {
        txtAnswerStatus.setVisibility(View.GONE);
        lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

        txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

        txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));

        if (!strAnswer.equalsIgnoreCase("")) {
            if (strAnswer.equalsIgnoreCase(questionList.get(QueNo).getAnswer())) {
                rightAnswers = rightAnswers + 1;
            } else {
                wrongAnswers = wrongAnswers + 1;
            }
            Log.e("UpdateUI right ==>", "" + rightAnswers);
            Log.e("UpdateUI wrong ==>", "" + wrongAnswers);

            txtRightAnswers.setText("Right " + rightAnswers);
            txtWrongAnswers.setText("Wrong " + wrongAnswers);
            strAnswer = "";
        }
        isAnswered = false;
    }

    private void CorrectUI() {
        if (!questionList.get(QueNo).getAnswer().equalsIgnoreCase("")) {
            if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("1")) {
                lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickA.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtA.setTextColor(getResources().getColor(R.color.white));
                txtOptionA.setTextColor(getResources().getColor(R.color.white));
            } else if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("2")) {
                lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickB.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtB.setTextColor(getResources().getColor(R.color.white));
                txtOptionB.setTextColor(getResources().getColor(R.color.white));
            } else if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("3")) {
                lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickC.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtC.setTextColor(getResources().getColor(R.color.white));
                txtOptionC.setTextColor(getResources().getColor(R.color.white));
            } else if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("4")) {
                lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickD.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtD.setTextColor(getResources().getColor(R.color.white));
                txtOptionD.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.shimmerHide(shimmer);
        countdownTimer.stop();
        handler.removeCallbacks(runnable);
    }

    //save_question_report API call
    private void Save_Question_Report() {
        Utility.ProgressBarShow(QuestionAnswer.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.save_question_report(catID,"" + levelID,
                "" + attendedQue, "" + questionList.size(),
                "" + rightAnswers, "" + prefManager.getLoginId());
        call.enqueue(new Callback<SuccessModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("save_question_report massage =>", "" + response.body().getMessage());
                        Log.e("==>levelID", "" + levelID);
                        Intent intent = new Intent(QuestionAnswer.this, LevelResult.class);
                        intent.putExtra("levelID", "" + levelID);
                        intent.putExtra("currentLevel", "" + currentLevel);
                        intent.putExtra("TotalLevel", "" + TotalLevel);
                        startActivity(intent);
                        finish();
                        handler.removeCallbacks(runnable);
                    } else {
                        Log.e("save_question_report massage =>", "" + response.body().getMessage());
                    }
                    countdownTimer.allShowZero();
                    countdownTimer.stop();
                } catch (Exception e) {
                    Log.e("save_question_report API error==>", "" + e);
                }
                Utility.ProgressbarHide();
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                Log.e("save_question_report Failure", "That didn't work!!!" + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.shimmerHide(shimmer);
        countdownTimer.stop();
        handler.removeCallbacks(runnable);
    }

}