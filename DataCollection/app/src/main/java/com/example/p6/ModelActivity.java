package com.example.p6;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.p6.databinding.ActivityModelBinding;

public class ModelActivity extends Activity {

    private TextView mTextView;
    private ActivityModelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;
    }
}