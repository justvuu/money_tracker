package com.example.moneytracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

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
    private Button addCategoryBtn;
    private AlertDialog dialog;

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
        // Inflate the layout for this fragment
        addCategoryBtn = view.findViewById(R.id.add_category_btn);
        DB = new DBHelper(requireContext());
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_category, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView);
                EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
                Button addNewCategoryBtn = dialogView.findViewById(R.id.addCategoryButton);
                RadioGroup categoryTypeRadioGroup = dialogView.findViewById(R.id.categoryTypeRadioGroup);

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
                            long id = DB.AddCategory(categoryName, categoryType);
                            if(id != -1){
                                Toast.makeText(addNewCategoryBtn.getContext(), "Add new category successful", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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

        return view;
    }
}