package com.example.moneytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AlertDialog dialog;

    private DBHelper DB;

    private LinearLayout containerLayout;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        DB = new DBHelper(requireContext());
        containerLayout = view.findViewById(R.id.category_scroll_layout);
        SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        List<Category> categoryList = DB.getAllCategories(userId);

        ImageView moveBack = view.findViewById(R.id.category_list_move_back);

        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Regular.ttf");
        Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-Bold.ttf");
        Typeface semiBoldFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins/Poppins-SemiBold.ttf");

        TextView title = view.findViewById(R.id.category_list_title);
        title.setTypeface(boldFont);


        for (Category category : categoryList) {
            View categoryItem = inflater.inflate(R.layout.category_detail_item, containerLayout, false);
            TextView nameTextView = categoryItem.findViewById(R.id.category_item_detail_name);
            TextView typeTextView = categoryItem.findViewById(R.id.category_item_detail_type);

            nameTextView.setTypeface(semiBoldFont);
            typeTextView.setTypeface(customFont);

            nameTextView.setText(category.getName());
            int categoryType = category.getType();
            if(categoryType == 0) typeTextView.setText("Expense");
            else typeTextView.setText("Income");

            TextView editBtn = categoryItem.findViewById(R.id.category_item_detail_edit_btn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.update_category, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setView(dialogView);

                    TextView title = dialogView.findViewById(R.id.update_category_title);
                    TextView cateType = dialogView.findViewById(R.id.update_category_type_label);
                    TextView cateName = dialogView.findViewById(R.id.update_category_name_label);

                    title.setTypeface(boldFont);
                    cateName.setTypeface(customFont);
                    cateType.setTypeface(customFont);

                    EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
                    Button updateCategoryBtn = dialogView.findViewById(R.id.updateCategoryButton);
                    RadioGroup categoryTypeRadioGroup = dialogView.findViewById(R.id.categoryTypeRadioGroup);
                    RadioButton incomeRadio = dialogView.findViewById(R.id.incomeRadioButton);
                    RadioButton expenseRadio = dialogView.findViewById(R.id.expenseRadioButton);
                    if(category.getType() == 0) expenseRadio.setChecked(true);
                    else incomeRadio.setChecked(true);
                    categoryEditText.setText(category.getName());

                    categoryEditText.setTypeface(semiBoldFont);
                    updateCategoryBtn.setTypeface(semiBoldFont);
                    incomeRadio.setTypeface(customFont);
                    expenseRadio.setTypeface(customFont);

                    updateCategoryBtn.setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(updateCategoryBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                boolean check = DB.updateCategory(category.getId(), categoryType, categoryName);
                                if(check){
                                    Toast.makeText(updateCategoryBtn.getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    reloadFragment();
                                }
                                else{
                                    Toast.makeText(updateCategoryBtn.getContext(), "Fail to update", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }
            });
            containerLayout.addView(categoryItem);
        }

        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
    private void reloadFragment() {
        CategoryFragment categoryFragment = new CategoryFragment();

        // Start a fragment transaction
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace the current fragment with the ChangePasswordFragment
        transaction.detach(CategoryFragment.this);
        transaction.replace(R.id.setting_fragment, categoryFragment);

        // Add the transaction to the back stack (optional)
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}