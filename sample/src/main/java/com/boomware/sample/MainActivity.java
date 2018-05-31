package com.boomware.sample;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.boomware.sdk.Boomware;
import com.boomware.sdk.BoomwareCallback;
import com.boomware.sdk.BoomwareException;
import com.boomware.sdk.api.BoomwareRequest;
import com.boomware.sdk.ui.CodeEditText;
import com.boomware.sdk.ui.PhoneEditText;


public class MainActivity extends AppCompatActivity implements BoomwareCallback, PhoneEditText.Listener, CodeEditText.Listener {

    public static final String TAG = "BoomwareSample";


    @BindView(R.id.enter_phone_dialog) View enterPhoneView;
    @BindView(R.id.enter_code_dialog) View enterCodeView;
    @BindView(R.id.phone_number_edit) PhoneEditText phoneEdit;
    @BindView(R.id.code_edit) CodeEditText codeEdit;
    @BindView(R.id.country) TextView country;

    @BindView(R.id.verify_by_call_btn) AppCompatButton callBtn;
    @BindView(R.id.verify_by_sms_btn) AppCompatButton smsBtn;
    @BindView(R.id.verify_by_voice_btn) AppCompatButton voiceBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        phoneEdit.setListener(this);
        codeEdit.setListener(this);

    }

    @OnClick(R.id.verify_by_call_btn) public void onCallClick() {
        Boomware.getInstance().verifyNumber(phoneEdit.getPhoneNumber(), Boomware.Method.call, this);
    }

    @OnClick(R.id.verify_by_sms_btn) public void onSMSClick() {
        Boomware.getInstance().verifyNumber(phoneEdit.getText().toString(), Boomware.Method.sms, this);
    }

    @OnClick(R.id.verify_by_voice_btn) public void onVoiceClick() {
        Boomware.getInstance().verifyNumber(phoneEdit.getText().toString(), Boomware.Method.voice, this);
    }

    @Override public void onCodeSent(BoomwareRequest request) {
        Log.d(TAG, "Code sent, " + request);
        showEnterCodeView();
    }


    @Override public void onVerificationCompleted(BoomwareRequest request, String code) {
        Log.d(TAG, "onVerificationCompleted: " + request.getId());
        codeEdit.setCodeWithoutListener(code);

        Boomware.getInstance().bindNumber(request.getNumber());

        // you should complete registration on your backend side
        // use API method POST /v1/verify/info
        // see https://boomware.com

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.verification_completed, request.getNumber()))
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(dialogInterface -> showEnterPhoneView()).create().show();
    }

    @Override public void onVerificationFailed(BoomwareException e) {
        switch (e.getErrorCode()) {
            case BoomwareException.INVALID_NUMBER:
                Toast.makeText(this, R.string.error_wrong_number, Toast.LENGTH_LONG).show();
                break;
            case BoomwareException.INVALID_CODE:
                Toast.makeText(this, R.string.error_wrong_code, Toast.LENGTH_LONG).show();
                codeEdit.shake();
                break;
            default:
                Log.e(TAG, "Verification failed with error: " + e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        codeEdit.reset();
    }

    @Override public void onConnectionError(IOException e) {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    private void showEnterCodeView() {
        codeEdit.requestFocus();
        codeEdit.reset();
        enterPhoneView.setVisibility(View.GONE);
        enterCodeView.setVisibility(View.VISIBLE);
    }

    private void showEnterPhoneView() {
        enterPhoneView.setVisibility(View.VISIBLE);
        enterCodeView.setVisibility(View.GONE);
    }

    public void onBackPressed() {
        if (enterCodeView.getVisibility() == View.VISIBLE) {
            showEnterPhoneView();
        } else {
            super.onBackPressed();
        }
    }

    @Override public void onPhoneChanged(String number, boolean isValidNumber, String countryIso) {

        country.setText(countryIso != null ? new Locale("", countryIso).getDisplayCountry() : "");

        callBtn.setEnabled(isValidNumber);
        smsBtn.setEnabled(isValidNumber);
        voiceBtn.setEnabled(isValidNumber);
    }

    @Override public void onCodeEntered(String code) {
        Boomware.getInstance().check(code, this);
    }
}
