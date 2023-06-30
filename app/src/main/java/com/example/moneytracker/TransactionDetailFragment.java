package com.example.moneytracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Transaction transaction;

    private String categoryName;
    private int categoryType;
    private int categoryId;


    private AlertDialog dialog;

    private DBHelper DB;

    public TransactionDetailFragment(Transaction transaction) {
        // Required empty public constructor
        this.transaction = transaction;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionDetailFragment newInstance(String param1, String param2, Transaction transaction) {
        TransactionDetailFragment fragment = new TransactionDetailFragment(transaction);
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
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);
        DB = new DBHelper(requireContext());
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");
        TextView title = view.findViewById(R.id.transaction_detail_label);
        title.setTypeface(boldFont);
        Category category = DB.getCategoryById(transaction.getCategoryId(), userId);


        ImageView moveBack = view.findViewById(R.id.trans_detail_move_back);

        TextView name = view.findViewById(R.id.categoryNameTextView);
        name.setTypeface(semiBoldFont);
        TextView type = view.findViewById(R.id.typeTextView);
        type.setTypeface(customFont);
        TextView amount = view.findViewById(R.id.amountTextView);
        amount.setTypeface(customFont);
        TextView date = view.findViewById(R.id.dateTextView);
        date.setTypeface(customFont);
        TextView description = view.findViewById(R.id.descriptionTextView);
        description.setTypeface(customFont);
        Button editBtn = view.findViewById(R.id.edit_btn);
        Button deleteBtn = view.findViewById(R.id.delete_btn);
        name.setText(category.getName());
        amount.setText(""+ decimalFormat.format(transaction.getAmount()));
        date.setText(transaction.getDate());
        String desVal = transaction.getDescription();
        try{
            if(desVal == null || desVal.equals("")) description.setText("Description: None");
            else description.setText("Description: " + desVal);
        }
        catch (Exception ex){
            description.setText("Description: None");
        }


        if(category.getType() == 0){
            type.setText("Expense");
        }
        else type.setText("Income");



        editBtn.setTypeface(semiBoldFont);
        deleteBtn.setTypeface(semiBoldFont);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.update_transaction, null);
                Button updateTransBtn = dialogView.findViewById(R.id.update_trans_btn);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView);

                Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
                Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
                Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

                TextView title = dialogView.findViewById(R.id.update_title);
                TextView amountLabel = dialogView.findViewById(R.id.update_amount_label);
                TextView categoryLabel = dialogView.findViewById(R.id.update_category_label);
                TextView dateLabel = dialogView.findViewById(R.id.update_date_label);
                TextView descriptionLabel = dialogView.findViewById(R.id.update_description_label);

                title.setTypeface(boldFont);
                amountLabel.setTypeface(customFont);
                categoryLabel.setTypeface(customFont);
                dateLabel.setTypeface(customFont);

                descriptionLabel.setTypeface(customFont);

                updateTransBtn.setTypeface(semiBoldFont);

                EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
                EditText transAmountEditText = dialogView.findViewById(R.id.transaction_amount);
                EditText transDesEditText = dialogView.findViewById(R.id.transaction_description);

                dateEditText.setTypeface(semiBoldFont);
                transAmountEditText.setTypeface(semiBoldFont);
                transDesEditText.setTypeface(semiBoldFont);

                List<Category> categories = new ArrayList<>();
                try {
                    categories = DB.getAllCategories(userId);
                }

                catch (Exception e){
                    categories.add(new Category(1, userId, "Shopping", 0));
                    categories.add(new Category(2, userId, "Salary", 1));
                }
                Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);

                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(requireContext(), android.R.layout.simple_spinner_item, categories) {
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
                transAmountEditText.setText("" + decimalFormat.format(transaction.getAmount()));
                transDesEditText.setText(transaction.getDescription());
                dateEditText.setText(transaction.getDate());

                int selectedIndex = -1;
                for (int i = 0; i < categories.size(); i++) {
                    Category category_temp = categories.get(i);
                    if (category_temp.getId() == category.getId()) {
                        selectedIndex = i;
                        break;
                    }
                }

// Set the value for the Spinner
                if (selectedIndex != -1) {
                    categorySpinner.setSelection(selectedIndex);
                }

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

                updateTransBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String transAmount = transAmountEditText.getText().toString().replace(",", "");;
                        String transDate = dateEditText.getText().toString();
                        String transDescription = transDesEditText.getText().toString();
                        if(TextUtils.isEmpty(transAmount) || TextUtils.isEmpty(transDate)){
                            Toast.makeText(updateTransBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SharedPreferences preferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
                            int userId = preferences.getInt("userId", 0);
                            double amountVal = new Double(transAmount);
                            boolean check = DB.updateTransaction(transaction.getId(), amountVal, categoryId , transDate, userId, transDescription);
                            if(check){
                                Toast.makeText(updateTransBtn.getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                                User user = DB.getUserById(userId);
                                name.setText(categoryName);
                                amount.setText("" + decimalFormat.format(amountVal));
                                date.setText(transDate);
                                description.setText(transDescription);
                                if(category.getType() == categoryType){
                                    if(categoryType == 0){
                                       DB.updateUserExpense(userId,  user.getExpense() - transaction.getAmount() + amountVal);
                                    }
                                    else{
                                        DB.updateUserIncome(userId, user.getIncome() - transaction.getAmount() + amountVal);
                                    }
                                }
                                else{
                                    if(categoryType == 0){
                                        type.setText("Expense");
                                        DB.updateUserIncome(userId, user.getIncome() - transaction.getAmount());
                                        DB.updateUserExpense(userId, user.getExpense() + amountVal);
                                    }
                                    else{
                                        type.setText("Income");
                                        DB.updateUserIncome(userId, user.getIncome() + amountVal);
                                        DB.updateUserExpense(userId, user.getExpense() - transaction.getAmount());
                                    }
                                }
                                transaction = DB.getTransactionById(transaction.getId());
                                dialog.dismiss();
                            }
                            else{
                                Toast.makeText(updateTransBtn.getContext(), "Fail to update", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this transaction?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Delete" button
                        boolean check = DB.deleteTransaction(transaction.getId());
                        if(check){
                            SharedPreferences preferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
                            int userId = preferences.getInt("userId", 0);
                            User user = DB.getUserById(userId);
                            if(category.getType() == 0){
                                DB.updateUserExpense(userId, user.getExpense() - transaction.getAmount());
                            }
                            else{
                                DB.updateUserIncome(userId, user.getIncome() - transaction.getAmount());
                            }
                            Toast.makeText(deleteBtn.getContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                        else{
                            Toast.makeText(deleteBtn.getContext(), "Fail to delete", Toast.LENGTH_SHORT).show();
                        }
                        // You can also perform any additional actions here
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Cancel" button
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
    private void reloadFragment(Transaction transaction) {
        TransactionDetailFragment transactionDetailFragment = new TransactionDetailFragment(transaction);

        // Start a fragment transaction
        FragmentTransaction transaction1 = getFragmentManager().beginTransaction();

        // Replace the current fragment with the ChangePasswordFragment
        transaction1.detach(TransactionDetailFragment.this);
        transaction1.replace(R.id.updatr_trans_layout, transactionDetailFragment);

        // Add the transaction to the back stack (optional)
        transaction1.addToBackStack(null);

        // Commit the transaction
        transaction1.commit();
    }
}