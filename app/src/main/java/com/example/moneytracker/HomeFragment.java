package com.example.moneytracker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DBHelper DB;

    private LinearLayout parentLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        DB = new DBHelper(requireContext());
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface openSansBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Open_Sans/static/OpenSans-Bold.ttf");
        Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Open_Sans/static/OpenSans-SemiBold.ttf");
//        Typeface semiPoppinsBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SeBold.ttf");
        Typeface poppinsBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

        TextView recent_trans_label = view.findViewById(R.id.recent_transactions_label);
        TextView currentBalanceLabel = view.findViewById(R.id.current_balance_label);
        TextView crrBalance = view.findViewById(R.id.current_balance);
        TextView crrIncome = view.findViewById(R.id.income_val);
        TextView crrExpense = view.findViewById(R.id.expense_val);
        TextView usernameText = view.findViewById(R.id.usernameText);
        TextView expenseLabel = view.findViewById(R.id.expense_label);
        TextView incomeLabel = view.findViewById(R.id.income_label);


        currentBalanceLabel.setTypeface(customFont);
        recent_trans_label.setTypeface(poppinsBoldFont);
        usernameText.setTypeface(openSansBoldFont);
        crrBalance.setTypeface(poppinsBoldFont);
        crrIncome.setTypeface(semiBoldFont);
        crrExpense.setTypeface(semiBoldFont);
        expenseLabel.setTypeface(customFont);
        incomeLabel.setTypeface(customFont);


        SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        User crrUser = DB.getUserById(userId);

        usernameText.setText(crrUser.getUsername());
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String crrBalanceVal = decimalFormat.format(crrUser.getIncome() - crrUser.getExpense());
        String crrIncomeVal = decimalFormat.format(crrUser.getIncome());
        String crrExpenseVal = decimalFormat.format(crrUser.getExpense());
        crrBalance.setText(crrBalanceVal);
        crrIncome.setText(crrIncomeVal);
        crrExpense.setText(crrExpenseVal);

        parentLayout = view.findViewById(R.id.transaction_container);
        List<Transaction> transactionList = DB.getRecentTransactions(userId);
        for (Transaction transaction : transactionList) {
            View transactionItem = inflater.inflate(R.layout.transaction_item, parentLayout, false);
            Category category = DB.getCategoryById(transaction.getCategoryId(), userId);
            // Find views within the transaction item layout
            TextView nameTextView = transactionItem.findViewById(R.id.transaction_name);
            nameTextView.setTypeface(poppinsBoldFont);
            TextView dateTextView = transactionItem.findViewById(R.id.transaction_date);
            dateTextView.setTypeface(customFont);
            TextView amountTextView = transactionItem.findViewById(R.id.transaction_amount);
            amountTextView.setTypeface(poppinsBoldFont);
            ImageView iconImageView = transactionItem.findViewById(R.id.transaction_icon);
            FrameLayout frameLayout = transactionItem.findViewById(R.id.transaction_framelayout);
            int statusColor;
            int amountColor;
            Drawable drawable;
            // Set values for the views
            nameTextView.setText(category.getName());
            dateTextView.setText(transaction.getDate());
            String amount = "" + decimalFormat.format(transaction.getAmount());
            amountTextView.setText(amount);
            if(category.getType() == 0){
                drawable = getResources().getDrawable(R.drawable.decrease);
                statusColor = Color.parseColor("#E9C4A2");
                amountColor = Color.parseColor("#AD5719");
            }
            else{
                drawable = getResources().getDrawable(R.drawable.increase);
                statusColor = Color.parseColor("#DDF0DB");
                amountColor = Color.parseColor("#129B38");
            }
            iconImageView.setImageDrawable(drawable);
            ColorStateList colorStateList = ColorStateList.valueOf(statusColor);
            frameLayout.setBackgroundTintList(colorStateList);
            amountTextView.setTextColor(amountColor);
            // Add the transaction item to the parent layout
            transactionItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TransactionDetailFragment transactionDetailFragment = new TransactionDetailFragment(transaction);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace the current fragment with the ChangePasswordFragment
                    transaction.replace(R.id.home_fragment, transactionDetailFragment);

                    // Add the transaction to the back stack (optional)
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();


                }
            });
            parentLayout.addView(transactionItem);
        }

        return view;
    }
}