package com.example.moneytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DBHelper DB;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_change_password, container, false);
        DB = new DBHelper(requireContext());
        ImageView moveBackIcon = view.findViewById(R.id.changepassword_move_back);
        Button saveBtn = view.findViewById(R.id.changePasswordButton);

        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

        TextView title = view.findViewById(R.id.change_password_title);
        EditText oldPasswordEditText = view.findViewById(R.id.oldPassword);
        EditText newPasswordEditText = view.findViewById(R.id.newPassword);
        EditText retypeNewPasswordEditText = view.findViewById(R.id.retypePassword);

        title.setTypeface(boldFont);
        oldPasswordEditText.setTypeface(customFont);
        newPasswordEditText.setTypeface(customFont);
        retypeNewPasswordEditText.setTypeface(customFont);




        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = newPasswordEditText.getText().toString();
                String retypeNewPassword = retypeNewPasswordEditText.getText().toString();
                String oldPassword = oldPasswordEditText.getText().toString();
                SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", -1);
                if(TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(retypeNewPassword) || TextUtils.isEmpty(oldPassword) ){
                    Toast.makeText(saveBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                }
                else if(!newPassword.equals(retypeNewPassword)){
                    Toast.makeText(saveBtn.getContext(), "The passwords doesn't match", Toast.LENGTH_SHORT).show();
                }
                else{
                    boolean check = DB.updatePassword(userId, oldPassword, newPassword);
                    if(check){
                        Toast.makeText(saveBtn.getContext(), "Change password successfully", Toast.LENGTH_SHORT).show();
                        oldPasswordEditText.setText("");
                        newPasswordEditText.setText("");
                        retypeNewPasswordEditText.setText("");
                    }
                    else{
                        Toast.makeText(saveBtn.getContext(), "Fail to change password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        moveBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
}