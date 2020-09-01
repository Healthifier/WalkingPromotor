package io.github.healthifier.walking_promoter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.SquareStepRecord;

import java.util.List;

public class SquareStepListFragment extends Fragment {
    private Database _db;

    public SquareStepListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_list, container, false);
        _db = new Database(getActivity());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        final List<Integer> ids = _db.getSquareStepIds();
        // アイテムを追加します
        for (int id : ids) {
            SquareStepRecord record = _db.getSquareStepRecord(id);
            String suffix = "（試験に未挑戦です）";
            if (record != null && record.correctPercentage != 0) {
                suffix = record.correctPercentage == 100 ? "（試験に合格しました）" : "（まだ合格していません）";
            }
            adapter.add("ステップ " + (id + 1) + " " + suffix);
        }
        ListView listView = v.findViewById(R.id.stepListView);
        // アダプターを設定します
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("stepId", ids.get(position));

                Fragment fragment = new SquareStepMainFragment();
                fragment.setArguments(bundle);

                /**
                 * 都竹用に変更
                 */
                /*
                getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .addToBackStack(null)
                    .commit();*/

                getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });

        return v;
    }
}
