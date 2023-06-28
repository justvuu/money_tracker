package com.example.moneytracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTransactionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView addCategoryBtn;

    private Spinner categorySpinner;

    private int userId;

    private ArrayAdapter<Category> adapter;

    private Button addTransBtn;
    private AlertDialog dialog;
    private String categoryName;
    private int categoryType;
    private int categoryId;
    private DBHelper DB;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTransactionFragment newInstance(String param1, String param2) {
        AddTransactionFragment fragment = new AddTransactionFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Inflate the layout for this fragment
        SharedPreferences preferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
        userId = preferences.getInt("userId", 0);
        addCategoryBtn = view.findViewById(R.id.add_category_btn);
        addTransBtn = view.findViewById(R.id.add_trans_btn);

        Typeface poppinsFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Medium.ttf");

        TextView title = view.findViewById(R.id.add_new_transaction_title);
        TextView amountLabel = view.findViewById(R.id.new_transaction_amount_label);
        TextView categoryLabel = view.findViewById(R.id.new_transaction_category_label);
        TextView dateLabel = view.findViewById(R.id.new_transaction_date_label);
        TextView descriptionLabel = view.findViewById(R.id.new_transaction_description_label);

        title.setTypeface(boldFont);
        amountLabel.setTypeface(semiBoldFont);
        categoryLabel.setTypeface(semiBoldFont);
        dateLabel.setTypeface(semiBoldFont);
        descriptionLabel.setTypeface(semiBoldFont);

        addCategoryBtn.setTypeface(customFont);
        addTransBtn.setTypeface(boldFont);

        EditText dateEditText = view.findViewById(R.id.dateEditText);
        EditText transAmountEditText = view.findViewById(R.id.transaction_amount);
        EditText transDesEditText = view.findViewById(R.id.transaction_description);

        dateEditText.setTypeface(semiBoldFont);
        transAmountEditText.setTypeface(semiBoldFont);
        transDesEditText.setTypeface(semiBoldFont);

        DB = new DBHelper(requireContext());
        List<Category> categories = new ArrayList<>();
        try {
            categories = DB.getAllCategories(userId);
        }

        catch (Exception e){
            categories.add(new Category(1, userId, "Shopping", 0));
            categories.add(new Category(2, userId, "Salary", 1));
        }

        categorySpinner = view.findViewById(R.id.categorySpinner);


        adapter = new ArrayAdapter<Category>(requireContext(), android.R.layout.simple_spinner_item, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Customizing the view for each category item
                TextView textView = (TextView) super.getView(position, convertView, parent);
                Category category = getItem(position);
                if (category != null) {
                    // Display the category name in the Spinner
                    textView.setText(category.getName());
                    textView.setTypeface(semiBoldFont);
                }
                return textView;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Customizing the view for each category item in the dropdown menu
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                Category category = getItem(position);
                if (category != null) {
                    // Display the category name in the dropdown menu
                    textView.setText(category.getName());
                    textView.setTypeface(semiBoldFont);
                }
                return textView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Initialize the Spinner with the custom ArrayAdapter
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                if (selectedCategory != null) {
                    categoryId = selectedCategory.getId();
                    categoryName = selectedCategory.getName();
                    categoryType = selectedCategory.getType();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no category is selected
            }
        });
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_category, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView);
                TextView title = dialogView.findViewById(R.id.add_category_label);
                TextView typeLabel = dialogView.findViewById(R.id.add_category_type_label);
                TextView categoryNameLabel = dialogView.findViewById(R.id.add_category_category_name_label);
                title.setTypeface(boldFont);
                typeLabel.setTypeface(customFont);
                categoryNameLabel.setTypeface(customFont);
                EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
                Button addNewCategoryBtn = dialogView.findViewById(R.id.addCategoryButton);
                RadioGroup categoryTypeRadioGroup = dialogView.findViewById(R.id.categoryTypeRadioGroup);
                RadioButton incomeRadio = dialogView.findViewById(R.id.incomeRadioButton);
                RadioButton expenseRadio = dialogView.findViewById(R.id.expenseRadioButton);
                incomeRadio.setTypeface(customFont);
                expenseRadio.setTypeface(customFont);
                categoryEditText.setTypeface(semiBoldFont);

                addNewCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String categoryName = categoryEditText.getText().toString();
                        int selectedId = categoryTypeRadioGroup.getCheckedRadioButtonId();
                        int categoryType = -1;
                        if (selectedId == R.id.incomeRadioButton) {
                            categoryType = 1;
                        } else if (selectedId == R.id.expenseRadioButton) {
                            categoryType = 0;
                        }
                        if(categoryType == -1 || TextUtils.isEmpty(categoryName)){
                            Toast.makeText(addNewCategoryBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            long id = DB.AddCategory(userId, categoryName, categoryType);
                            if(id != -1){
                                Toast.makeText(addNewCategoryBtn.getContext(), "Add new category successful", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                reloadFragment();
                            }
                            else{
                                Toast.makeText(addNewCategoryBtn.getContext(), "Fail to add new category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create and show the DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Handle the selected date
                                // Update the EditText field with the selected date
                                String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                                dateEditText.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        addTransBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transAmount = transAmountEditText.getText().toString();
                String transDate = dateEditText.getText().toString();
                String transDescription = transDesEditText.getText().toString();
                if(TextUtils.isEmpty(transAmount) || TextUtils.isEmpty(transDate)){
                    Toast.makeText(addTransBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                }
                else{
                    double amountVal = new Double(transAmount);
                    long id = DB.AddTransaction(amountVal, categoryId, transDate, userId, transDescription);
                    if(id != -1){
                        Toast.makeText(addTransBtn.getContext(), "Add new transaction successful", Toast.LENGTH_SHORT).show();
                        User user = DB.getUserById(userId);
                        if(categoryType == 0){
                            DB.updateUserExpense(userId, user.getExpense() + amountVal);
                        }
                        else{
                            DB.updateUserIncome(userId, user.getIncome() + amountVal);
                        }
                        transAmountEditText.setText("");
                        dateEditText.setText("");
                        transDesEditText.setText("");
                    }
                    else{
                        Toast.makeText(addTransBtn.getContext(), "Fail to add new transaction", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
    private void reloadFragment() {
        DB = new DBHelper(requireContext());
        List<Category> categories = new ArrayList<>();
        try {
            categories = DB.getAllCategories(userId);
        } catch (Exception e) {
            // Handle the exception, or show a toast message
            e.printStackTrace();
        }

        // Check if adapter is null (first time setup) or update the existing adapter
        if (adapter == null) {
            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(categories);
            adapter.notifyDataSetChanged();
        }
    }
}