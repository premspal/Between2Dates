package com.example.daysbetweendateswithcalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;

    public void askRatings() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.rate_i:
                askRatings();
                return true;
            case R.id.share_i:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;
            case R.id.remove_ad_i:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
                Toast.makeText(MainActivity.this, "ad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdClicked();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        Button START = findViewById(R.id.start_btn_i);
        EditText START_TEXT = findViewById(R.id.start_text_i);

        Button END = findViewById(R.id.end_btn_i);
        EditText END_TEXT = findViewById(R.id.end_text_i);

        Button TODAYS = findViewById(R.id.start_today_btn_i);
        Button TODAYE = findViewById(R.id.end_today_btn_i);

        TextView RESULT_TEXT = findViewById(R.id.result_text_i);

        FloatingActionButton DONE = findViewById(R.id.done_btn_i);
        FloatingActionButton DELETEbtn = findViewById(R.id.delete_btn_i);
        CheckBox CB = findViewById(R.id.checkbox_i);


        SelectStartDate(START, START_TEXT);
        SelectEndDate(END, END_TEXT);

        TodayStartDate(TODAYS, START_TEXT);
        TodayEndDate(TODAYE, END_TEXT);


        TableLayout table = findViewById(R.id.tb_i);


        RESULT(DONE, START_TEXT, END_TEXT, RESULT_TEXT, CB, table);
        DELETE(START_TEXT, END_TEXT, DELETEbtn, RESULT_TEXT, table);

        new DateInputMask(START_TEXT);
        new DateInputMask(END_TEXT);


    }

    private void SelectStartDate(Button START, EditText START_TEXT) {
        START.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {


                String start = "DD/MM/YYYY";
                Calendar calendar = null;
                int year = 0;
                int month = 0;
                int dayOfMonth = 0;
                LocalDate date1 = null;

                String inputString1 = START_TEXT.getText().toString();

                if (start.equals(inputString1)) {
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[dd/MM/yyyy]" + "[d/M/yyyy]" + "[dd/M/yyyy]" + "[d/MM/yyyy]");
                    try {
                        date1 = LocalDate.parse(inputString1, dtf);
                        year = date1.getYear();
                        month = (date1.getMonthValue()) - 1;
                        dayOfMonth = date1.getDayOfMonth();
                    } catch (Exception e) {
                        //java.text.ParseException: Unparseable date: Geting error
                        System.out.println("Excep" + e);
                    }

                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                                if (month < 10 || dayOfMonth < 10) {
                                    if (month < 10 && dayOfMonth < 10) {
                                        START_TEXT.setText("0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                                    } else if (month < 10) {
                                        START_TEXT.setText(dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                                    } else {
                                        START_TEXT.setText("0" + dayOfMonth + "/" + (month + 1) + "/" + year);
                                    }
                                } else {
                                    START_TEXT.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                }


                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

    }

    private void SelectEndDate(Button END, EditText END_TEXT) {
        END.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String start = "DD/MM/YYYY";
                Calendar calendar = null;
                int year = 0;
                int month = 0;
                int dayOfMonth = 0;
                LocalDate date1 = null;

                String inputString1 = END_TEXT.getText().toString();

                if (start.equals(inputString1)) {
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[dd/MM/yyyy]" + "[d/M/yyyy]" + "[dd/M/yyyy]" + "[d/MM/yyyy]");
                    try {
                        date1 = LocalDate.parse(inputString1, dtf);
                        year = date1.getYear();
                        month = (date1.getMonthValue()) - 1;
                        dayOfMonth = date1.getDayOfMonth();
                    } catch (Exception e) {
                        //java.text.ParseException: Unparseable date: Geting error
                        System.out.println("Excep" + e);
                    }
                /*    LocalDate date1 = LocalDate.parse(inputString1, dtf);
                    year = date1.getYear();
                    month = (date1.getMonthValue()) - 1;
                    dayOfMonth = date1.getDayOfMonth();*/
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                                if (month < 10 || dayOfMonth < 10) {
                                    if (month < 10 && dayOfMonth < 10) {
                                        END_TEXT.setText("0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                                    } else if (month < 10) {
                                        END_TEXT.setText(dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                                    } else {
                                        END_TEXT.setText("0" + dayOfMonth + "/" + (month + 1) + "/" + year);
                                    }
                                } else {
                                    END_TEXT.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                }


                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
    }


    private void TodayStartDate(Button TODAYS, EditText START_TEXT) {
        TODAYS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (month < 10 || dayOfMonth < 10) {
                    if (month < 10 && dayOfMonth < 10) {
                        START_TEXT.setText("0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                    } else if (month < 10) {
                        START_TEXT.setText(dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                    } else {
                        START_TEXT.setText("0" + dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                } else {
                    START_TEXT.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }


            }
        });
    }

    private void TodayEndDate(Button TODAYE, EditText END_TEXT) {
        TODAYE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (month < 10 || dayOfMonth < 10) {
                    if (month < 10 && dayOfMonth < 10) {
                        END_TEXT.setText("0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                    } else if (month < 10) {
                        END_TEXT.setText(dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                    } else {
                        END_TEXT.setText("0" + dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                } else {
                    END_TEXT.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }


            }
        });
    }

    public void RESULT(FloatingActionButton DONE, EditText START_TEXT, EditText END_TEXT, TextView RESULT_TEXT, CheckBox CB, TableLayout table) {
        DONE.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                TextView cent = findViewById(R.id.cen_unit_text_i);
                TextView deca = findViewById(R.id.dec_unit_text_i);
                TextView year = findViewById(R.id.yea_unit_text_i);
                TextView mont = findViewById(R.id.mon_unit_text_i);
                TextView week = findViewById(R.id.wee_unit_text_i);
                TextView days = findViewById(R.id.day_unit_text_i);
                TextView hours = findViewById(R.id.hour_unit_text_i);
                TextView minu = findViewById(R.id.min_unit_text_i);
                TextView seco = findViewById(R.id.sec_unit_text_i);

                String inputString1 = START_TEXT.getText().toString();
                String inputString2 = END_TEXT.getText().toString();

                LocalDate date1 = null;
                LocalDate date2 = null;
                long sign = 0;
                long daysBetween = 0;
                Period ymd = null;
                long weeks = 0;
                long rd = 0;
                long cen = 0;
                long decade = 0;
                long years = 0;
                long months = 0;
                long hr = 0;
                long min = 0;
                long sec = 0;

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[dd/MM/yyyy]" + "[d/M/yyyy]" + "[dd/M/yyyy]" + "[d/MM/yyyy]");

                try {
                    date1 = LocalDate.parse(inputString1, dtf);
                    date2 = LocalDate.parse(inputString2, dtf);
                    sign = ChronoUnit.DAYS.between(date1, date2);
                    daysBetween = Math.abs(ChronoUnit.DAYS.between(date1, date2));

                    ymd = Period.between(date1, date2);
                    weeks = daysBetween / 7;
                    rd = daysBetween % 7;

                    cen = Math.abs(ChronoUnit.CENTURIES.between(date1, date2));
                    decade = Math.abs(ChronoUnit.DECADES.between(date1, date2));
                    years = Math.abs(ChronoUnit.YEARS.between(date1, date2));
                    months = Math.abs(ChronoUnit.MONTHS.between(date1, date2));
                    hr = daysBetween * 24;
                    min = hr * 60;
                    sec = min * 60;


                    if (CB.isChecked()) {
                        daysBetween = daysBetween + 1;
                        ymd = Period.between(date1, date2).plusDays(1);
                        //value of year and month will be changed because this is belong to ymd
                        weeks = daysBetween / 7;
                        rd = daysBetween % 7;
                        hr = daysBetween * 24;
                        min = hr * 60;
                        sec = min * 60;
                    }
                    if (ymd.getYears() > 0) {
                        //sho all three format
                        if (sign < 0) {
                            RESULT_TEXT.setText(Html.fromHtml("<b>" + "(-)" + "<br />" + "<big>" + "<big>" + "<font color=red size=+1>" + daysBetween + " DAYS " + "</font>" + "</big>" + "</big>" + "</b>" + "<br />" + " or " + "<br />" + weeks + " WEEKS and " + rd + " DAYS " + "<br />" + " or " + "<br />" + ymd.getYears() + " YEARS, " + ymd.getMonths() + " MONTHS and " + ymd.getDays() + " DAYS "));

                        } else {
                            RESULT_TEXT.setText(Html.fromHtml("<b>" + "<big>" + "<big>" + "<font color=red size=+1>" + daysBetween + " DAYS " + "</font>" + "</big>" + "</big>" + "</b>" + "<br />" + " or " + "<br />" + weeks + " WEEKS and " + rd + " DAYS " + "<br />" + " or " + "<br />" + ymd.getYears() + " YEARS, " + ymd.getMonths() + " MONTHS and " + ymd.getDays() + " DAYS "));
                        }
//                    RESULT_TEXT.setText(daysBetween + "DAYS" + System.getProperty("line.separator")+" or "+ System.getProperty("line.separator")+ weeks + " WEEKS and " + rd + " DAYS " + System.getProperty("line.separator")+" or "+ System.getProperty("line.separator")+ ymd.getYears() + " YEARS, " + ymd.getMonths() + " MONTHS and " + ymd.getDays() + " DAYS ");


                    } else if (weeks > 0) {
                        // show both format
                        if (sign < 0) {
                            RESULT_TEXT.setText(Html.fromHtml("<b>" + "(-)" + "<br />" + "<big>" + "<big>" + "<big>" + "<font color=red size=+1>" + daysBetween + " DAYS " + "</font>" + "</big>" + "</big>" + "<big>" + "</b>" + "<br />" + " or " + "<br />" + weeks + " WEEKS and " + rd + " DAYS "));

                        } else {
                            RESULT_TEXT.setText(Html.fromHtml("<b>" + "<big>" + "<big>" + "<big>" + "<font color=red size=+1>" + daysBetween + " DAYS " + "</font>" + "</big>" + "</big>" + "<big>" + "</b>" + "<br />" + " or " + "<br />" + weeks + " WEEKS and " + rd + " DAYS "));
                        }

//                    RESULT_TEXT.setText(daysBetween + " DAYS " + System.getProperty("line.separator")+" or " +System.getProperty("line.separator")+ weeks + " WEEKS and " + rd + " DAYS ");

                    } else {
                        //show one format
                        if (sign < 0) {
                            RESULT_TEXT.setText(Html.fromHtml("<b>" + "(-)" + "<br />" + "<big>" + "<big>" + "<big>" + "<font color=red size=+1>" + daysBetween + " DAYS " + "</font>" + "</big>" + "</big>" + "<big>" + "</b>"));

                        } else {
                            RESULT_TEXT.setText(Html.fromHtml("<b>" + "<big>" + "<big>" + "<big>" + "<font color=red size=+1>" + daysBetween + " DAYS " + "</font>" + "</big>" + "</big>" + "<big>" + "</b>"));
                        }
                    }

                    cent.setText(cen + "");
                    deca.setText(decade + "");
                    year.setText(years + "");
                    mont.setText(months + "");
                    week.setText(weeks + "");
                    days.setText(daysBetween + "");
                    hours.setText(hr + "");
                    minu.setText(min + "");
                    seco.setText(sec + "");

                    table.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    //java.text.ParseException: Unparseable date: Geting error
                    System.out.println("Excep" + e);
                }

            }
        });

    }

    private void DELETE(EditText START_TEXT, EditText END_TEXT, FloatingActionButton DELETEbtn, TextView RESULT_TEXT, TableLayout table) {
        DELETEbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                table.setVisibility(View.INVISIBLE);
                START_TEXT.setText("DD/MM/YYYY");
                END_TEXT.setText("DD/MM/YYYY");
                RESULT_TEXT.setText("");

            }
        });
    }
}