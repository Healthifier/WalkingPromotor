package io.github.healthifier.walking_promoter.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.github.healthifier.walking_promoter.Constant;
import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.fragments.DailyGraphFragment;
import io.github.healthifier.walking_promoter.fragments.GoalFragment;
import io.github.healthifier.walking_promoter.fragments.SquareStepListFragment;
import io.github.healthifier.walking_promoter.fragments.SyncFragment;
import io.github.healthifier.walking_promoter.fragments.TokaidoMapFragment;
import io.github.healthifier.walking_promoter.fragments.TotalGraphFragment;
import io.github.healthifier.walking_promoter.fragments.WeeklyGraphFragment;
import io.github.healthifier.walking_promoter.models.Database;

import java.util.ArrayList;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices.
 * On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends Activity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        ListView listView = findViewById(R.id.item_list);
        assert listView != null;
        //setupSideMenu(listView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (mTwoPane) {
            int defaultPosition = 1;
            listView.setItemChecked(defaultPosition, true);
            MenuItem item = ((ArrayAdapter<MenuItem>) listView.getAdapter()).getItem(defaultPosition);
            showFragment(item.activityClass);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Database db = new Database(this);
        db.updateHosts(Constant.serverHosts);
    }

    /*
    private void setupSideMenu(@NonNull final ListView listView) {
        final ArrayList<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem("東海道五十三次", TokaidoMapFragment.class));
        list.add(new MenuItem("歩数 (日毎)", DailyGraphFragment.class));
        list.add(new MenuItem("歩数 (週毎)", WeeklyGraphFragment.class));
        list.add(new MenuItem("みんなの歩数", TotalGraphFragment.class));
        list.add(new MenuItem("目標設定", GoalFragment.class));
        list.add(new MenuItem("ステップ練習", SquareStepListFragment.class));
        list.add(new MenuItem("データ更新", SyncFragment.class));

        ArrayAdapter<MenuItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem item = list.get(position);
                if (item != null) {
                    showFragment(item.activityClass);
                }
            }
        });
    }*/

    private void showFragment(Class<? extends Fragment> clazz) {
        if (mTwoPane) {
            FragmentManager fm = getFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            try {
                Fragment fragment = clazz.newInstance();
                getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("小さいディスプレイは未対応です");
        }
    }


    private class MenuItem {
        public final String label;
        public final Class<? extends Fragment> activityClass;

        public MenuItem(String label, Class<? extends Fragment> fragmentClass) {
            this.label = label;
            this.activityClass = fragmentClass;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
