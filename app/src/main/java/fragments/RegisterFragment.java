package fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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


    private TextView mTextAvatarDialogTitle;
    private EditText mEditStudentNumber, mEditNickname;
    private PasswordView mPasswordView1, mPasswordView2;
    private Button mButtonRegister, mButtonAvatarDialogCancel, mButtonAvatarDialogAccept;
    private TextInputLayout mTextInputStudentNumber, mTextInputPassword1, mTextInputPassword2, mTextInputNickname;
    private CheckBox mCheckBoxPrivate;
    private CircleImageView mCircleImageAvatar, mCircleImageAvatarEdit, mCircleImageAvatarDialog;
    private AlertDialog mAlertDialogAvatar;
    private View mViewAvatar;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private GridView mGridView;
    private ImageAdapter mImageAdapter;
    private int mCurrentAvatarId;
    private boolean mIsValidStudentNumber, mIsValidNickname, mIsValidPassword1, mIsValidPassword2;
    private ProgressDialog mProgressDialog;

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
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void assignSingletons() {
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void assignAvatarDialog() {
        mGridView = (GridView) mViewAvatar.findViewById(R.id.grid_view);
        mButtonAvatarDialogCancel = (Button) mViewAvatar.findViewById(R.id.button_avatar_dialog_cancel);
        mButtonAvatarDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogAvatar.cancel();
            }
        });
        mButtonAvatarDialogAccept = (Button) mViewAvatar.findViewById(R.id.avatarDialogAcceptButton);
        mButtonAvatarDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewID = getResources().getIdentifier("avatar_" + String.valueOf(mCurrentAvatarId), "drawable", getActivity().getPackageName());
                mCircleImageAvatar.setImageDrawable(ContextCompat.getDrawable(getActivity(), viewID));
                mCircleImageAvatar.setTag(mCurrentAvatarId);
                mAlertDialogAvatar.cancel();
            }
        });
        mTextAvatarDialogTitle = (TextView) mViewAvatar.findViewById(R.id.text_avatar_dialog_title);
        mImageAdapter = new ImageAdapter(getActivity());
        mGridView.setAdapter(mImageAdapter);
        mCircleImageAvatarDialog = (CircleImageView) mViewAvatar.findViewById(R.id.circle_image_avatar_dialog);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int viewID = getResources().getIdentifier("avatar_" + String.valueOf(position), "drawable", getActivity().getPackageName());
                mCircleImageAvatarDialog.setImageDrawable(ContextCompat.getDrawable(getActivity(), viewID));
                mCurrentAvatarId = position;
                mImageAdapter.setSelectedItem(position);
                mImageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void assignViews(View view) {
        mEditStudentNumber = (EditText) view.findViewById(R.id.edit_student_number);
        mEditNickname = (EditText) view.findViewById(R.id.edit_nickname);
        mPasswordView1 = (PasswordView) view.findViewById(R.id.password_view_1);
        mPasswordView2 = (PasswordView) view.findViewById(R.id.password_view_2);
        mButtonRegister = (Button) view.findViewById(R.id.button_register);
        mTextInputStudentNumber = (TextInputLayout) view.findViewById(R.id.text_input_student_number);
        mTextInputPassword1 = (TextInputLayout) view.findViewById(R.id.text_input_password_1);
        mTextInputPassword2 = (TextInputLayout) view.findViewById(R.id.text_input_password_2);
        mTextInputNickname = (TextInputLayout) view.findViewById(R.id.text_input_nickname);

        mCircleImageAvatar = (CircleImageView) view.findViewById(R.id.registerProfileCircleImageView);
        mCircleImageAvatarEdit = (CircleImageView) view.findViewById(R.id.registerProfileIconCircleImageView);
        mCheckBoxPrivate = (CheckBox) view.findViewById(R.id.registerPrivateCheckBox);
        mAlertDialogAvatar = new AlertDialog.Builder(getActivity()).create();
        mViewAvatar = View.inflate(getActivity(), R.layout.dialog_change_avatar, null);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        mEditStudentNumber.setTypeface(Roboto_Regular);
        mEditNickname.setTypeface(Roboto_Regular);
        mPasswordView1.setTypeface(Roboto_Regular);
        mPasswordView2.setTypeface(Roboto_Regular);
        mButtonRegister.setTypeface(Roboto_Medium);
        mTextInputStudentNumber.setTypeface(Roboto_Regular);
        mTextInputPassword1.setTypeface(Roboto_Regular);
        mTextInputPassword2.setTypeface(Roboto_Regular);
        mTextInputNickname.setTypeface(Roboto_Regular);
        mButtonAvatarDialogAccept.setTypeface(Roboto_Medium);
        mButtonAvatarDialogCancel.setTypeface(Roboto_Medium);
        mTextAvatarDialogTitle.setTypeface(Roboto_Regular);
    }

    private void assignListeners() {
        mCircleImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogAvatar.setView(mViewAvatar);
                mAlertDialogAvatar.show();
            }
        });
        mCircleImageAvatarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogAvatar.setView(mViewAvatar);
                mAlertDialogAvatar.show();
            }
        });
        mPasswordView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doRegister();
                }
                return false;
            }
        });
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
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
                    mTextInputStudentNumber.setErrorEnabled(true);
                    mTextInputStudentNumber.setError("Student Number is required");
                    mIsValidStudentNumber = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mTextInputNickname.setErrorEnabled(false);
                    mTextInputNickname.setError(null);
                    mIsValidNickname = true;
                } else {
                    mTextInputNickname.setErrorEnabled(true);
                    mTextInputNickname.setError("Nickname is required");
                    mIsValidNickname = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mTextInputPassword1.setErrorEnabled(false);
                    mTextInputPassword1.setError(null);
                    mIsValidPassword1 = true;

                } else {
                    mTextInputPassword1.setErrorEnabled(true);
                    mTextInputPassword1.setError("Password is required");
                    mIsValidPassword1 = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mTextInputPassword2.setErrorEnabled(false);
                    mTextInputPassword2.setError(null);
                    mIsValidPassword2 = true;
                } else {
                    mTextInputPassword2.setErrorEnabled(true);
                    mTextInputPassword2.setError("Confirm Password is required");
                    mIsValidPassword2 = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doRegister() {
        if (validInput()) {
            mProgressDialog.show();
            User user = new User();
            user.setUser_student_number_id(mEditStudentNumber.getText().toString());
            user.setUser_nickname(mEditNickname.getText().toString());
            user.setUser_password(mPasswordView2.getText().toString());
            user.setUser_avatar(mCurrentAvatarId);
            user.setUser_is_private(mCheckBoxPrivate.isChecked());
            mNetworkManagerSingleton.postRegisterUserJsonRequest(user, new NetworkManagerSingleton.BooleanResponseListener() {
                @Override
                public void getResult(Boolean response, String message) {
                    if (response) {
                        doLogin();
                    } else {
                        mProgressDialog.hide();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void doLogin() {
        User user = new User();
        user.setUser_student_number_id(mEditStudentNumber.getText().toString());
        user.setUser_password(mPasswordView2.getText().toString());
        mNetworkManagerSingleton.getLoginUserJsonRequest(user, new NetworkManagerSingleton.BooleanResponseListener() {
            @Override
            public void getResult(Boolean response, String message) {
                if (response) {
                    if (mDatabaseHandlerSingleton.loginUser(mEditStudentNumber.getText().toString())) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        mProgressDialog.hide();
                        Toast.makeText(getContext(), "Failed to log in the user in the local database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validInput() {
        if (mEditStudentNumber.getText().length() == 0) {
            mTextInputStudentNumber.setErrorEnabled(true);
            mTextInputStudentNumber.setError("Student Number is required");
            mIsValidStudentNumber = false;
        }
        if (mEditNickname.getText().length() == 0) {
            mTextInputNickname.setErrorEnabled(true);
            mTextInputNickname.setError("Nickname is required");
            mIsValidNickname = false;
        }
        if (mPasswordView1.getText().length() == 0) {
            mTextInputPassword1.setErrorEnabled(true);
            mTextInputPassword1.setError("Password is required");
            mIsValidPassword1 = false;
        }
        if (mPasswordView2.getText().length() == 0) {
            mTextInputPassword2.setErrorEnabled(true);
            mTextInputPassword2.setError("Confirm Password is required");
            mIsValidPassword2 = false;
        }
        if (!mPasswordView1.getText().toString().equals(mPasswordView2.getText().toString())) {
            mTextInputPassword2.setErrorEnabled(true);
            mTextInputPassword2.setError("Passwords should match");
            mIsValidPassword2 = false;
        }
        return mIsValidStudentNumber && mIsValidNickname && mIsValidPassword1 && mIsValidPassword2;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
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
