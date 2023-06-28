package com.example.moneytracker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button logoutBtn;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

        TextView title = view.findViewById(R.id.settings_title);
        TextView usernameLabel = view.findViewById(R.id.settings_username);
        TextView generalLabel = view.findViewById(R.id.settings_general_label);
        TextView aboutLabel = view.findViewById(R.id.settings_about_label);
        TextView categoryLabel = view.findViewById(R.id.settings_category_label);
        TextView changePassLabel = view.findViewById(R.id.settings_change_password_label);
        TextView aboutUsLabel = view.findViewById(R.id.settings_aboutus_label);

        title.setTypeface(boldFont);
        usernameLabel.setTypeface(semiBoldFont);
        generalLabel.setTypeface(semiBoldFont);
        aboutLabel.setTypeface(semiBoldFont);
        categoryLabel.setTypeface(semiBoldFont);
        changePassLabel.setTypeface(semiBoldFont);
        aboutUsLabel.setTypeface(semiBoldFont);

        SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String userName = preferences.getString("username", "None");
        usernameLabel.setText(userName);

        LinearLayout changepasswordBtn = view.findViewById(R.id.changepassword_nav_btn);
        LinearLayout categoryBtn = view.findViewById(R.id.settings_category_list);
        LinearLayout aboutUsBtn = view.findViewById(R.id.about_us_btn);

        logoutBtn = view.findViewById(R.id.logout_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Perform any additional logout-related actions if needed

                // Redirect the user to the login screen or another appropriate screen
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryFragment categoryFragment = new CategoryFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace the current fragment with the ChangePasswordFragment
                transaction.replace(R.id.setting_fragment, categoryFragment);

                // Add the transaction to the back stack (optional)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace the current fragment with the ChangePasswordFragment
                transaction.replace(R.id.setting_fragment, aboutUsFragment);

                // Add the transaction to the back stack (optional)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        changepasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();

                // Start a fragment transaction
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace the current fragment with the ChangePasswordFragment
                transaction.replace(R.id.setting_fragment, changePasswordFragment);

                // Add the transaction to the back stack (optional)
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });



        return view;
    }
}