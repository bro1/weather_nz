package nz.theysay.weathernz;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import nz.theysay.weathernz.databinding.FragmentSecondBinding;
import android.widget.TextView;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (MyHandler.forecast7Days != null) {
            MyHandler mh = new MyHandler(Looper.getMainLooper(), getActivity());
            mh.displayForecast7Days(MyHandler.forecast7Days);
        }

//        MyHandler.Forecast t = MyHandler.forecast7Days;
//
//        TextView txt7Days = (TextView) getActivity().findViewById(R.id.textview_7days2);
//        if (txt7Days != null) {
//            txt7Days.setText(t.get7Days());
//        }

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}