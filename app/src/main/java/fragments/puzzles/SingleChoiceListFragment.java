package fragments.puzzles;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.progamer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleChoiceListFragment extends Fragment {

    private ListView singleSelectionListView;
    private ArrayAdapter<String> arrayAdapter;

    public SingleChoiceListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_choice_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
        loadData();
    }

    private void loadData() {
        String[] dataList = new String[] {"One", "Two", "Three"};
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, dataList);
        singleSelectionListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        singleSelectionListView.setAdapter(arrayAdapter);
    }

    private void assignViews(View view) {
        singleSelectionListView = (ListView)view.findViewById(R.id.singleSelectionListView);
    }

}
