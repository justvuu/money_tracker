package com.example.moneytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner monthSpinner;

    private  double expenseAmount;
    private double incomeAmount;


    private View expenseColumn;
    private View incomeColumn;

    private Typeface boldFont;
    private Typeface customFont;
    private Typeface semiBoldFont;

    private DBHelper DB;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
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
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        DB = new DBHelper(requireContext());
        LinearLayout containerLayout = view.findViewById(R.id.statistic_scroll_layout);

        customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

        TextView monthLabel = view.findViewById(R.id.statistic_month_label);
        TextView chartLabel = view.findViewById(R.id.statistic_chart_label);
        TextView title = view.findViewById(R.id.statistic_title);

        monthLabel.setTypeface(semiBoldFont);
        chartLabel.setTypeface(semiBoldFont);
        title.setTypeface(boldFont);


        expenseAmount = 0;
        incomeAmount = 0;
        expenseColumn = view.findViewById(R.id.expense_column);
        incomeColumn = view.findViewById(R.id.income_column);

        monthSpinner = view.findViewById(R.id.month_spinner);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        // Create an ArrayAdapter for the Spinner
        String[] monthsArray = getResources().getStringArray(R.array.months_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, monthsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        monthSpinner.setAdapter(adapter);

// Set a listener to retrieve the selected month value
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = monthsArray[position];
                int selectedMonthValue = position + 1; // Add 1 to get the numerical value
                containerLayout.removeAllViews();

                incomeAmount = 0;
                expenseAmount = 0;

                TextView incomeTextView = new TextView(requireContext());
                incomeTextView.setText("Top Income");
                incomeTextView.setTypeface(semiBoldFont);
                incomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                incomeTextView.setPadding(0, 10, 0, 10);
                containerLayout.addView(incomeTextView);

                SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", -1);
                List<CategoryTotal> categoryTotals = DB.getIncomeCategoryTotals("" + selectedMonthValue, userId);
                for (CategoryTotal category : categoryTotals) {
                    View categoryItem = inflater.inflate(R.layout.category_item, containerLayout, false);
                    incomeAmount+= category.getTotalAmount();
                    TextView nameTextView = categoryItem.findViewById(R.id.category_item_name);
                    TextView amountTextView = categoryItem.findViewById(R.id.category_item_amount);
                    nameTextView.setText(category.getCategoryName());
                    amountTextView.setText("" + decimalFormat.format(category.getTotalAmount()));
                    containerLayout.addView(categoryItem);
                }


                TextView expenseTextView = new TextView(requireContext());
                expenseTextView.setText("Top Expense");
                expenseTextView.setTypeface(semiBoldFont);
                expenseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                expenseTextView.setPadding(0, 10, 0, 10);
                containerLayout.addView(expenseTextView);
                categoryTotals = DB.getExpenseCategoryTotals("" + selectedMonthValue, userId);

                for (CategoryTotal category : categoryTotals) {
                    View categoryItem = inflater.inflate(R.layout.category_item, containerLayout, false);
                    expenseAmount+= category.getTotalAmount();
                    TextView nameTextView = categoryItem.findViewById(R.id.category_item_name);
                    TextView amountTextView = categoryItem.findViewById(R.id.category_item_amount);
                    nameTextView.setText(category.getCategoryName());
                    amountTextView.setText("" + decimalFormat.format(category.getTotalAmount()));
                    containerLayout.addView(categoryItem);
                }
                double maxValue = Math.max(expenseAmount, incomeAmount);
                double expenseHeightFraction = expenseAmount / maxValue;
                double incomeHeightFraction = incomeAmount / maxValue;

                int maxHeightPixels = 500; // Adjust this value as needed
                int maxWidthPixels = 250; // Adjust this value as needed
                int expenseHeight = (int) (expenseHeightFraction * maxHeightPixels);
                int incomeHeight = (int) (incomeHeightFraction * maxHeightPixels);

                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        maxWidthPixels, incomeHeight);
                layoutParams1.gravity = Gravity.CENTER;

                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        maxWidthPixels, expenseHeight);
                layoutParams2.gravity = Gravity.CENTER;

                expenseColumn.setLayoutParams(layoutParams2);
                incomeColumn.setLayoutParams(layoutParams1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//
            }
        });


        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        double maxValue = Math.max(expenseAmount, incomeAmount);
        double expenseHeightFraction = expenseAmount / maxValue;
        double incomeHeightFraction = incomeAmount / maxValue;

// Set the heights of the columns
        int maxHeightPixels = 500; // Adjust this value as needed
        int maxWidthPixels = 250; // Adjust this value as needed
        int expenseHeight = (int) (expenseHeightFraction * maxHeightPixels);
        int incomeHeight = (int) (incomeHeightFraction * maxHeightPixels);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                maxWidthPixels, incomeHeight);
        layoutParams1.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                maxWidthPixels, expenseHeight);
        layoutParams2.gravity = Gravity.CENTER;

        expenseColumn.setLayoutParams(layoutParams2);
        incomeColumn.setLayoutParams(layoutParams1);

        return view;
    }
}