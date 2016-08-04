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
import android.util.Log;
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

    private EditText mEditStudentNumber;
    private PasswordView mPasswordView;
    private ProgressDialog mProgressDialog;
    private Button mButtonLogin;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private TextInputLayout mTextInputStudentNumber, mTextInputPassword;
    private boolean mIsValidStudentNumber, mIsValidPassword;
    private String mClassName = getClass().toString();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }

    private void assignSingletons() {
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void assignProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void assignViews(View view) {
        mEditStudentNumber = (EditText) view.findViewById(R.id.edit_student_number);
        mPasswordView = (PasswordView) view.findViewById(R.id.password_view);
        mButtonLogin = (Button) view.findViewById(R.id.button_login);
        mTextInputStudentNumber = (TextInputLayout) view.findViewById(R.id.text_input_student_number);
        mTextInputPassword = (TextInputLayout) view.findViewById(R.id.text_input_password);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        mEditStudentNumber.setTypeface(Roboto_Regular);
        mPasswordView.setTypeface(Roboto_Regular);
        mTextInputStudentNumber.setTypeface(Roboto_Regular);
        mTextInputPassword.setTypeface(Roboto_Regular);
        mButtonLogin.setTypeface(Roboto_Medium);
    }

    private void assignListeners() {
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                }
                return false;
            }
        });
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        mEditStudentNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mTextInputStudentNumber.setErrorEnabled(false);
                    mTextInputStudentNumber.setError(null);
                    mIsValidStudentNumber = true;
                } else {
                    mTextInputPassword.setErrorEnabled(true);
                    mTextInputPassword.setError("Student Number is required");
                    mIsValidStudentNumber = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mTextInputPassword.setErrorEnabled(false);
                    mTextInputPassword.setError(null);
                    mIsValidPassword = true;
                } else {
                    mTextInputPassword.setErrorEnabled(true);
                    mTextInputPassword.setError("Password is required");
                    mIsValidPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doLogin() {
        if (validInput()) {
            mProgressDialog.show();
            User user = new User();
            user.setUser_student_number_id(mEditStudentNumber.getText().toString());
            user.setUser_password(mPasswordView.getText().toString());
            mNetworkManagerSingleton.getLoginUserJsonRequest(user, new NetworkManagerSingleton.BooleanResponseListener() {
                @Override
                public void getResult(Boolean response, String message) {
                    if (response) {
                        if (mDatabaseHandlerSingleton.loginUser(mEditStudentNumber.getText().toString())) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            mProgressDialog.hide();
                            Log.e(mClassName, "Failed to log in the user in the local database");
                            Toast.makeText(getContext(), "Failed to log in the user in the local database", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mProgressDialog.hide();
                        Log.e(mClassName, message);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validInput() {
        if (mEditStudentNumber.getText().length() == 0) {
            mTextInputStudentNumber.setErrorEnabled(true);
            mTextInputStudentNumber.setError("Student Number is required");
            mIsValidStudentNumber = false;
        }
        if (mPasswordView.getText().length() == 0) {
            mTextInputPassword.setErrorEnabled(true);
            mTextInputPassword.setError("Password is required");
            mIsValidPassword = false;
        }
        return mIsValidStudentNumber && mIsValidPassword;
    }
}
