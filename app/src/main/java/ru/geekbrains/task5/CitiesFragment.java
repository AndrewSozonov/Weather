package ru.geekbrains.task5;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.geekbrains.task5.adapter.CityRecyclerAdapter;
import ru.geekbrains.task5.dataBase.History;
import ru.geekbrains.task5.dataBase.HistoryDao;
import ru.geekbrains.task5.model.List;
import ru.geekbrains.task5.model.Weather;
import ru.geekbrains.task5.model.WeatherRequest;

public class CitiesFragment extends Fragment {

    private static String currentCity;
    private WeatherRequestPresenter weatherRequestPresenter = new WeatherRequestPresenter();
    private MyBottomSheetDialogFragment dialogFragment;
    private java.util.List<CityCard> listItems = new ArrayList<>();
    private CityRecyclerAdapter cityRecyclerAdapter;
    private RecyclerView cityCardRecyclerView;
    private static final String TAG = "CitiesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        weatherRequestPresenter.initRetrofit();
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initCityCardRecyclerView();
        weatherRequestPresenter.attachView(this);

        LocationHandler locationHandler = new LocationHandler(weatherRequestPresenter);
        locationHandler.attachView(this);


        Button buttonHistory = getView().findViewById(R.id.button_History);
        Button buttonSearch = getView().findViewById(R.id.button_Search);
        Button buttonNearbyWeather = getView().findViewById(R.id.button_nearbyWeather);
        showBottomSheetDialogFragment();
        buttonHistory.setOnClickListener(v -> showHistoryScreen());
        buttonSearch.setOnClickListener(v -> dialogFragment.show(getActivity().getSupportFragmentManager(),
                "dialog_fragment"));
        buttonNearbyWeather.setOnClickListener(v -> locationHandler.requestPermissions());
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString("current_city", currentCity);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_city_fragment, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.remove_city_fragment:
                int position = cityRecyclerAdapter.getAdapterPosition();
                cityRecyclerAdapter.removeAt(position);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void initCityCardRecyclerView(){
        cityCardRecyclerView = getView().findViewById(R.id.cityCardRecyclerView);
        cityCardRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cityCardRecyclerView.setLayoutManager(layoutManager);
        cityRecyclerAdapter = new CityRecyclerAdapter(listItems, getContext(), this);
        cityCardRecyclerView.setAdapter(cityRecyclerAdapter);

        cityRecyclerAdapter.SetOnItemClickListener((view, position) -> {
            CityCard cityCard = listItems.get(position);
            String city = cityCard.getCity();
            weatherRequestPresenter.requestRetrofitTemperatureHourly(city, Constants.WEATHER_API_KEY);
        });
    }

    public void showBottomSheetDialogFragment() {

        dialogFragment = MyBottomSheetDialogFragment.newInstance();
        dialogFragment.setOnDialogListener(dialogListener);
        dialogFragment.show(getActivity().getSupportFragmentManager(),
                "dialog_fragment");
    }

    private OnDialogListener dialogListener = new OnDialogListener() {
        @Override
        public void onDialogOk() {
            currentCity = dialogFragment.getCurrentCity();
            weatherRequestPresenter.requestRetrofitCity(currentCity, Constants.WEATHER_API_KEY);
        }
    };

    public void addCityCard(WeatherRequest weatherRequest) {
        List[] list = weatherRequest.getList();
        float temperature = list[0].getMain().getTemp();
        Weather[] weather = list[0].getWeather();
        String description = weather[0].getMain();
        CityCard card = new CityCard(currentCity, temperature, description);
        listItems.add(card);
        cityRecyclerAdapter.notifyDataSetChanged();
    }

    public void showTemperatureScreen(String city, WeatherRequest weatherRequest) {

        HistoryDao historyDao = App
                .getInstance()
                .getHistoryDao();
        HistorySource historySource = new HistorySource(historyDao);


        List[] list = weatherRequest.getList();
        String[] dateForecast = new String[12];
        String[] temperatureForecast = new String[12];

        for (int i = 0; i<12; i++ ) {
            String oldDate = list[i].getDate();
           dateForecast[i] = oldDate.substring(11,16);
           temperatureForecast[i] = String.format("%.1f С°",list[i].getMain().getTemp());
        }

        Weather[] weather = list[0].getWeather();
        float currentTemperature = list[0].getMain().getTemp();
        int currentHumidity = list[0].getMain().getHumidity();
        float currentWind = list[0].getWind().getSpeed();
        int currentPressure = list[0].getMain().getPressure();
        String currentDescription = weather[0].getMain();
        int currentId = weather[0].getId();

        History history = new History();
        history.city = city;
        history.temperature = currentTemperature;

        Date dateNow = new Date();
        SimpleDateFormat formatForDayNow = new SimpleDateFormat("dd.MM E");
        history.date = formatForDayNow.format(dateNow);

        historySource.addHistory(history);

        Intent intent = new Intent();
        intent.setClass(getActivity(), TemperatureActivity.class);
        //intent.putExtra(Constants.CITY_KEY, currentCity);
        intent.putExtra(Constants.CITY_KEY, city);
        intent.putExtra(Constants.TEMP_KEY, currentTemperature);
        intent.putExtra(Constants.HUMIDITY_KEY, currentHumidity);
        intent.putExtra(Constants.WIND_KEY, currentWind);
        intent.putExtra(Constants.PRESSURE_KEY, currentPressure);
        intent.putExtra(Constants.DESCRIPTION_KEY, currentDescription);
        intent.putExtra(Constants.ID_KEY, currentId);
        intent.putExtra(Constants.DATE_FORECAST_KEY,dateForecast);
        intent.putExtra(Constants.TEMPERATURE_FORECAST_KEY, temperatureForecast);
        startActivity(intent);
    }

    private void showHistoryScreen() {

        Intent intent = new Intent();
        intent.setClass(getActivity(), HistoryActivity.class);
        startActivity(intent);
    }

    public void showAlertDialogError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.city_error);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}








