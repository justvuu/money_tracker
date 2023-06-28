package com.example.moneytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout horizontalLayout;
    private LinearLayout contentLayout;
    private TextView incomeVal;
    private TextView expenseVal;

    private Typeface boldFont;
    private Typeface customFont;
    private Typeface semiBoldFont;

    private DBHelper DB;

    private double totalIncome;
    private double totalExpense;

    public TransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(String param1, String param2) {
        TransactionFragment fragment = new TransactionFragment();
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
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        DB = new DBHelper(requireContext());
        customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

        TextView title = view.findViewById(R.id.transaction_fragment_title);
        TextView overallLabel = view.findViewById(R.id.overall_label);
        TextView monthLabel = view.findViewById(R.id.month_label);
        TextView detailLabel = view.findViewById(R.id.detail_label);
        title.setTypeface(boldFont);
        overallLabel.setTypeface(semiBoldFont);
        monthLabel.setTypeface(semiBoldFont);
        detailLabel.setTypeface(semiBoldFont);
        horizontalLayout = view.findViewById(R.id.horizontal_layout);
        contentLayout = view.findViewById(R.id.transaction_content_layout);
        incomeVal = view.findViewById(R.id.trans_fragment_income);
        expenseVal = view.findViewById(R.id.trans_fragment_expense);
        totalExpense = 0;
        totalIncome = 0;

        for (int i = 0; i < 12; i++) {
            final int index = i;
            TextView textView = (TextView) horizontalLayout.getChildAt(i);
            textView.setTextColor(Color.GRAY);
            textView.setTypeface(customFont);
            int selectedI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Update the content of the LinearLayout based on the clicked item
                    int textColor = Color.BLACK;
                    textView.setTextColor(textColor);
                    textView.setTypeface(customFont, Typeface.BOLD);
                    for (int j = 0; j < 12; j++){
                        if(j == selectedI) continue;
                        TextView textView = (TextView) horizontalLayout.getChildAt(j);
                        textView.setTextColor(Color.GRAY);
                        textView.setTypeface(customFont);
                    }
                    updateContentLayout(index, textView.getText().toString(), inflater);
                }
            });
        }
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        TextView textView = (TextView) horizontalLayout.getChildAt(currentMonth);
        textView.performClick();

        return view;
    }
    private void updateContentLayout(int index, String content, LayoutInflater inflater) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        // Clear the existing content
        contentLayout.removeAllViews();
        totalExpense = 0;
        totalIncome = 0;
        String recentDate = "";

        SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        // Query the database for transactions of the selected month
        List<Transaction> transactions = DB.getTransactionsByMonthAndUserId("" + (index+1), userId);

        // Display the transactions in the content layout
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate().toString();
            if(!recentDate.equals(transDate)){
                recentDate = transDate;
                contentLayout.addView(renderNewTextView(recentDate));
            }
            View transactionItem = inflater.inflate(R.layout.transaction_item, contentLayout, false);
            Category category = DB.getCategoryById(transaction.getCategoryId(), userId);
            // Find views within the transaction item layout
            TextView nameTextView = transactionItem.findViewById(R.id.transaction_name);
            nameTextView.setTypeface(semiBoldFont);
            TextView dateTextView = transactionItem.findViewById(R.id.transaction_date);
            TextView amountTextView = transactionItem.findViewById(R.id.transaction_amount);
            ImageView iconImageView = transactionItem.findViewById(R.id.transaction_icon);
            FrameLayout frameLayout = transactionItem.findViewById(R.id.transaction_framelayout);
            int statusColor;
            int amountColor;
            Drawable drawable;
            // Set values for the views
            nameTextView.setText(category.getName());
            dateTextView.setText(transaction.getDate());
            String amount = "" + decimalFormat.format(transaction.getAmount()) ;
            amountTextView.setText(amount);
            if(category.getType() == 0){
                drawable = getResources().getDrawable(R.drawable.decrease);
                statusColor = Color.parseColor("#E9C4A2");
                amountColor = Color.parseColor("#AD5719");
                totalExpense += transaction.getAmount();
            }
            else{
                drawable = getResources().getDrawable(R.drawable.increase);
                statusColor = Color.parseColor("#DDF0DB");
                amountColor = Color.parseColor("#129B38");
                totalIncome += transaction.getAmount();
            }
            iconImageView.setImageDrawable(drawable);
            ColorStateList colorStateList = ColorStateList.valueOf(statusColor);
            frameLayout.setBackgroundTintList(colorStateList);
            amountTextView.setTextColor(amountColor);
            transactionItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TransactionDetailFragment transactionDetailFragment = new TransactionDetailFragment(transaction);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace the current fragment with the ChangePasswordFragment
                    transaction.replace(R.id.transaction_fragment, transactionDetailFragment);

                    // Add the transaction to the back stack (optional)
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();


                }
            });
            contentLayout.addView(transactionItem);
        }
        incomeVal.setText("" +  decimalFormat.format(totalIncome));
        expenseVal.setText("" + decimalFormat.format(totalExpense));
    }

    private TextView renderNewTextView(String text){
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 15);
        textView.setLayoutParams(layoutParams);
        return textView;
    }
}