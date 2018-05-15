package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class EntranceResult extends Fragment {

    AppCompatEditText rollNo;

    private InputMethodManager imm;


    private String mUrl;
    private String mTitle;

    private void workForViewingResult() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rollNo = (AppCompatEditText) view.findViewById(R.id.resultRollNo);

        rollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (SPHandler.getInstance().isResultPublished()) {
            handlePublished();
        } else {
            FirebaseDatabase.getInstance().getReference().child("result_publish").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(boolean.class)) {
                        handlePublished();
                        SPHandler.getInstance().setResultPublished();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void handlePublished() {
        rollNo.requestFocus();
        workForViewingResult();
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/result";
        mTitle = "BSc CSIT Entrance Exam Result";
    }

    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appIndexing();
        return inflater.inflate(R.layout.fragment_result, container, false);
    }
}
