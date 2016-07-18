package fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;
import com.xwray.passwordview.PasswordView;

import activities.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class RegisterFragment extends Fragment {


    private TextView avatarDialogTitleTextView;
    private EditText registerStudentNumberText, registerStudentNicknameText;
    private PasswordView registerPasswordText, registerConfirmPasswordText;
    private Button registerButton, avatarDialogCancelButton, avatarDialogAcceptButton;
    private TextInputLayout registerStudentNumberTextInputLayout, registerPasswordTextInputLayout, registerConfirmPasswordTextInputLayout, registerStudentNicknameTextInputLayout;
    private CheckBox registerPrivateCheckBox;
    private CircleImageView registerProfileCircleImageView, registerProfileIconCircleImageView, avatarDialogCircleImageView;
    private AlertDialog avatarDialog;
    private View avatarView;
    private NetworkManagerSingleton networkManagerSingleton;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private int currentAvatar;
    private boolean validStudentNumber, validNickname, validPassword, validPasswordConfirm;
    private ProgressDialog progressDialog;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignSingletons();
        assignViews(view);
        assignAvatarDialog();
        assignFonts();
        assignListeners();
        assignProgressDialog();
    }

    private void assignProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void assignSingletons() {
        networkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
    }

    private void assignAvatarDialog() {
        gridView = (GridView) avatarView.findViewById(R.id.avatarGridView);
        avatarDialogCancelButton = (Button) avatarView.findViewById(R.id.avatarDialogCancelButton);
        avatarDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarDialog.cancel();
            }
        });
        avatarDialogAcceptButton = (Button) avatarView.findViewById(R.id.avatarDialogAcceptButton);
        avatarDialogAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewID = getResources().getIdentifier("avatar_" + String.valueOf(currentAvatar), "drawable", getActivity().getPackageName());
                registerProfileCircleImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), viewID));
                registerProfileCircleImageView.setTag(currentAvatar);
                avatarDialog.cancel();
            }
        });
        avatarDialogTitleTextView = (TextView) avatarView.findViewById(R.id.avatarDialogTitleTextView);
        imageAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(imageAdapter);
        avatarDialogCircleImageView = (CircleImageView) avatarView.findViewById(R.id.avatarDialogCircleImageView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int viewID = getResources().getIdentifier("avatar_" + String.valueOf(position), "drawable", getActivity().getPackageName());
                avatarDialogCircleImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), viewID));
                currentAvatar = position;
                imageAdapter.setSelectedItem(position);
                imageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void assignViews(View view) {
        registerStudentNumberText = (EditText) view.findViewById(R.id.registerStudentNumberText);
        registerPasswordText = (PasswordView) view.findViewById(R.id.registerPasswordText);
        registerConfirmPasswordText = (PasswordView) view.findViewById(R.id.registerConfirmPasswordText);
        registerStudentNicknameText = (EditText) view.findViewById(R.id.registerStudentNicknameText);
        registerButton = (Button) view.findViewById(R.id.registerButton);
        registerStudentNumberTextInputLayout = (TextInputLayout) view.findViewById(R.id.registerStudentNumberTextInputLayout);
        registerPasswordTextInputLayout = (TextInputLayout) view.findViewById(R.id.registerPasswordTextInputLayout);
        registerConfirmPasswordTextInputLayout = (TextInputLayout) view.findViewById(R.id.registerConfirmPasswordTextInputLayout);
        registerStudentNicknameTextInputLayout = (TextInputLayout) view.findViewById(R.id.registerStudentNicknameTextInputLayout);
        registerProfileCircleImageView = (CircleImageView) view.findViewById(R.id.registerProfileCircleImageView);
        registerProfileIconCircleImageView = (CircleImageView) view.findViewById(R.id.registerProfileIconCircleImageView);
        registerPrivateCheckBox = (CheckBox) view.findViewById(R.id.registerPrivateCheckBox);
        avatarDialog = new AlertDialog.Builder(getActivity()).create();
        avatarView = View.inflate(getActivity(), R.layout.avatar_dialog, null);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        registerStudentNumberText.setTypeface(Roboto_Regular);
        registerPasswordText.setTypeface(Roboto_Regular);
        registerConfirmPasswordText.setTypeface(Roboto_Regular);
        registerStudentNumberTextInputLayout.setTypeface(Roboto_Regular);
        registerPasswordTextInputLayout.setTypeface(Roboto_Regular);
        registerConfirmPasswordTextInputLayout.setTypeface(Roboto_Regular);
        registerStudentNicknameText.setTypeface(Roboto_Regular);
        registerStudentNicknameTextInputLayout.setTypeface(Roboto_Regular);
        registerButton.setTypeface(Roboto_Medium);
        avatarDialogAcceptButton.setTypeface(Roboto_Medium);
        avatarDialogCancelButton.setTypeface(Roboto_Medium);
        avatarDialogTitleTextView.setTypeface(Roboto_Regular);
    }

    private void assignListeners() {
        registerProfileCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarDialog.setView(avatarView);
                avatarDialog.show();
            }
        });
        registerProfileIconCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarDialog.setView(avatarView);
                avatarDialog.show();
            }
        });
        registerConfirmPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doRegister();
                }
                return false;
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
        registerStudentNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    registerStudentNumberTextInputLayout.setErrorEnabled(false);
                    registerStudentNumberTextInputLayout.setError(null);
                    validStudentNumber = true;
                } else {
                    registerStudentNumberTextInputLayout.setErrorEnabled(true);
                    registerStudentNumberTextInputLayout.setError("Student Number is required");
                    validStudentNumber = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerStudentNicknameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    registerStudentNicknameTextInputLayout.setErrorEnabled(false);
                    registerStudentNicknameTextInputLayout.setError(null);
                    validNickname = true;
                } else {
                    registerStudentNicknameTextInputLayout.setErrorEnabled(true);
                    registerStudentNicknameTextInputLayout.setError("Nickname is required");
                    validNickname = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    registerPasswordTextInputLayout.setErrorEnabled(false);
                    registerPasswordTextInputLayout.setError(null);
                    validPassword = true;

                } else {
                    registerPasswordTextInputLayout.setErrorEnabled(true);
                    registerPasswordTextInputLayout.setError("Password is required");
                    validPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerConfirmPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    registerConfirmPasswordTextInputLayout.setErrorEnabled(false);
                    registerConfirmPasswordTextInputLayout.setError(null);
                    validPasswordConfirm = true;
                } else {
                    registerConfirmPasswordTextInputLayout.setErrorEnabled(true);
                    registerConfirmPasswordTextInputLayout.setError("Confirm Password is required");
                    validPasswordConfirm = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doRegister() {
        if (validInput()) {
            progressDialog.show();
            User user = new User();
            user.setUser_student_number(registerStudentNumberText.getText().toString());
            user.setUser_nickname(registerStudentNicknameText.getText().toString());
            user.setUser_password(registerConfirmPasswordText.getText().toString());
            user.setUser_avatar(currentAvatar);
            user.setUser_is_private(registerPrivateCheckBox.isChecked());
            networkManagerSingleton.registerJSONRequest(user, new NetworkManagerSingleton.BooleanResponseListener() {
                @Override
                public void getResult(Boolean response, String message) {
                    if (response) {
                        doLogin();
                    } else {
                        progressDialog.hide();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void doLogin() {
        User user = new User();
        user.setUser_student_number(registerStudentNumberText.getText().toString());
        user.setUser_password(registerConfirmPasswordText.getText().toString());
        networkManagerSingleton.loginJSONRequest(user, new NetworkManagerSingleton.BooleanResponseListener() {
            @Override
            public void getResult(Boolean response, String message) {
                if (response) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    progressDialog.hide();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validInput() {
        if (registerStudentNumberText.getText().length() == 0) {
            registerStudentNumberTextInputLayout.setErrorEnabled(true);
            registerStudentNumberTextInputLayout.setError("Student Number is required");
            validStudentNumber = false;
        }
        if (registerStudentNicknameText.getText().length() == 0) {
            registerStudentNicknameTextInputLayout.setErrorEnabled(true);
            registerStudentNicknameTextInputLayout.setError("Nickname is required");
            validNickname = false;
        }
        if (registerPasswordText.getText().length() == 0) {
            registerPasswordTextInputLayout.setErrorEnabled(true);
            registerPasswordTextInputLayout.setError("Password is required");
            validPassword = false;
        }
        if (registerConfirmPasswordText.getText().length() == 0) {
            registerConfirmPasswordTextInputLayout.setErrorEnabled(true);
            registerConfirmPasswordTextInputLayout.setError("Confirm Password is required");
            validPasswordConfirm = false;
        }
        if (!registerPasswordText.getText().toString().equals(registerConfirmPasswordText.getText().toString())) {
            registerConfirmPasswordTextInputLayout.setErrorEnabled(true);
            registerConfirmPasswordTextInputLayout.setError("Passwords should match");
            validPasswordConfirm = false;
        }
        return validStudentNumber && validNickname && validPassword && validPasswordConfirm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private int selectedItem;

        public ImageAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
        }

        @Override
        public int getCount() {
            return 28;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CircleImageView circleImageView;
            if (convertView == null) {
                circleImageView = new CircleImageView(mContext);
                //circleImageView.setLayoutParams(new GridView.LayoutParams(230, 230));
            } else {
                circleImageView = (CircleImageView) convertView;
            }
            int id = getResources().getIdentifier("avatar_" + String.valueOf(position), "drawable", mContext.getPackageName());
            circleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, id));
            if (position == selectedItem) {
                circleImageView.setBorderWidth(2);
                circleImageView.setBorderColor(ContextCompat.getColor(mContext, R.color.accent));
            } else {
                circleImageView.setBorderWidth(0);
                circleImageView.setBorderColor(ContextCompat.getColor(mContext, R.color.accent));
            }
            return circleImageView;
        }
    }
}
