package fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.progamer.R;

import activities.LoginActivity;
import activities.PuzzleActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelDescriptionFragment extends Fragment {

    private Button levelDescriptionContinueButton;

    public LevelDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_level_description, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        levelDescriptionContinueButton = (Button)view.findViewById(R.id.levelDescriptionContinueButton);
        levelDescriptionContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PuzzleActivity.class);
                startActivity(intent);
            }
        });
    }
}
