package fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.progamer.R;
import com.xwray.passwordview.PasswordView;
import activities.MainActivity;
import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class LoginFragment extends Fragment {

    private EditText loginStudentNumberText;
    private PasswordView loginPasswordText;
    private ProgressDialog progressDialog;
    private Button loginButton;
    private NetworkManagerSingleton networkManagerSingleton;
    private DatabaseHandlerSingleton databaseHandlerSingleton;
    private TextInputLayout loginStudentNumberTextInputLayout, loginPasswordTextInputLayout;
    private boolean validStudentNumber, validPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignSingletons();
        assignViews(view);
        assignFonts();
        assignListeners();
        assignProgressDialog();
    }

    public void assignSingletons() {
        networkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
        databaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void assignProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void assignViews(View view) {
        loginStudentNumberText = (EditText) view.findViewById(R.id.loginStudentNumberText);
        loginPasswordText = (PasswordView) view.findViewById(R.id.loginPasswordText);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginStudentNumberTextInputLayout = (TextInputLayout) view.findViewById(R.id.loginStudentNumberTextInputLayout);
        loginPasswordTextInputLayout = (TextInputLayout) view.findViewById(R.id.loginPasswordTextInputLayout);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        loginStudentNumberText.setTypeface(Roboto_Regular);
        loginPasswordText.setTypeface(Roboto_Regular);
        loginStudentNumberTextInputLayout.setTypeface(Roboto_Regular);
        loginPasswordTextInputLayout.setTypeface(Roboto_Regular);
        loginButton.setTypeface(Roboto_Medium);
    }

    private void assignListeners() {
        loginPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                }
                return false;
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        loginStudentNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    loginStudentNumberTextInputLayout.setErrorEnabled(false);
                    loginStudentNumberTextInputLayout.setError(null);
                    validStudentNumber = true;
                } else {
                    loginStudentNumberTextInputLayout.setErrorEnabled(true);
                    loginStudentNumberTextInputLayout.setError("Student Number is required");
                    validStudentNumber = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    loginPasswordTextInputLayout.setErrorEnabled(false);
                    loginPasswordTextInputLayout.setError(null);
                    validPassword = true;
                } else {
                    loginPasswordTextInputLayout.setErrorEnabled(true);
                    loginPasswordTextInputLayout.setError("Password is required");
                    validPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void doLogin() {
        if (validInput()) {
            progressDialog.show();
            User user = new User();
            user.setUser_student_number_id(loginStudentNumberText.getText().toString());
            user.setUser_password(loginPasswordText.getText().toString());
            networkManagerSingleton.getLoginUserJsonRequest(user, new NetworkManagerSingleton.BooleanResponseListener() {
                @Override
                public void getResult(Boolean response, String message) {
                    if (response) {
                        if (databaseHandlerSingleton.loginUser(loginStudentNumberText.getText().toString())) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            progressDialog.hide();
                            Toast.makeText(getContext(), "Failed to log in the user in the local database", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        progressDialog.hide();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validInput() {
        if (loginStudentNumberText.getText().length() == 0) {
            loginStudentNumberTextInputLayout.setErrorEnabled(true);
            loginStudentNumberTextInputLayout.setError("Student Number is required");
            validStudentNumber = false;
        }
        if (loginPasswordText.getText().length() == 0) {
            loginPasswordTextInputLayout.setErrorEnabled(true);
            loginPasswordTextInputLayout.setError("Password is required");
            validPassword = false;
        }
        return validStudentNumber && validPassword;
    }
}
