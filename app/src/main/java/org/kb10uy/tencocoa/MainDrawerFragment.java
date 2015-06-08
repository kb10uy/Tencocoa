package org.kb10uy.tencocoa;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


public class MainDrawerFragment extends Fragment {

    private OnDrawerFragmentInteractionListener mListener;
    private ListView mDrawerList;
    private LinearLayout mDrawerLayout;


    public MainDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_drawer, container, false);

        mDrawerLayout = (LinearLayout) view.findViewById(R.id.main_drawer_layout);

        setupListView(view);
        setupListener(view);
        return view;
    }

    private void setupListener(View view) {
        ((ImageView) view.findViewById(R.id.MainDrawerImageViewUserProfileImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDrawerFragmentMiscInteraction("AccountSelect");
            }
        });
    }


    private void setupListView(View view) {
        mDrawerList = (ListView) view.findViewById(R.id.MainDrawerListViewContents);
        String cntlist[] = getResources().getStringArray(R.array.label_drawer_main_list);

        mDrawerList.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                cntlist));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected(position);
            }
        });
    }

    private void onItemSelected(int position) {
        mListener.onDrawerFragmentMainMenuInteraction(position);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDrawerFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDrawerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnDrawerFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDrawerFragmentMainMenuInteraction(int action);

        public void onDrawerFragmentMiscInteraction(String action);
    }

}
