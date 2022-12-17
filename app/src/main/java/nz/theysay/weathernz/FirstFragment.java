package nz.theysay.weathernz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import nz.theysay.weathernz.databinding.FragmentFirstBinding;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (MyHandler.forecast2Days != null) {
            MyHandler mh = new MyHandler(Looper.getMainLooper(), getActivity());
            mh.displayForecast2Days(MyHandler.forecast2Days);
        } else {
            loadWeather(getContext(), getActivity());
        }

        binding.button7days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public static void loadWeather(final Context context, final Activity activity) {
        Executor exe = Executors.newCachedThreadPool();
        exe.execute(new Runnable() {
            @Override
            public void run() {

                MyHandler mh = new MyHandler(Looper.getMainLooper(), activity);

                HttpRest http = new HttpRest(context, mh);
                http.get48HoursForecast();
                http.get7Days();
            }
        });

    }

}