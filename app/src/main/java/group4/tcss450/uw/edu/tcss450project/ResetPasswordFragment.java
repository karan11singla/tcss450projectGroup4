package group4.tcss450.uw.edu.tcss450project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResetPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ResetPasswordFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        Button b = v.findViewById(R.id.submitPasswordButton);
        b.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.submitPasswordButton:
                    resetPassword();
                    break;
            }
        }
    }
    private void resetPassword() {
        EditText pw1 = getActivity().findViewById(R.id.newPassword);
        EditText pw2 = getActivity().findViewById(R.id.reenterNewPassword);
        boolean valid = true;

        if (pw1.getText().toString().isEmpty() || pw2.getText().toString().isEmpty()) {
            valid = false;
            pw1.setError("Empty Field!");
            pw2.setError("Empty Field!");
        } else if (pw1.getText().toString().length() < 5 || pw2.getText().toString().length() < 5) {
            valid = false;
            pw1.setError("Password must be at least 5 characters");
            pw2.setError("Password must be at least 5 characters");
        }

        if (valid) {
            mListener.onSubmitPassword(pw1.getEditableText());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSubmitPassword(Editable password);
    }
}
