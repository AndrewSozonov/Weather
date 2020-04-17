package ru.geekbrains.task5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.geekbrains.task5.CitiesFragment;
import ru.geekbrains.task5.CityCard;
import ru.geekbrains.task5.R;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.RecyclerViewHolder> {

    private List<CityCard> listItems;
    private Context context;
    private OnItemClickListener itemClickListener;
    private Fragment fragment;
    private int adapterPosition;

    public CityRecyclerAdapter(List<CityCard> listItems, Context context, CitiesFragment fragment) {
        this.listItems = listItems;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CityRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.citycard_recycler_item, parent, false);
        return new CityRecyclerAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        CityCard cityCard = listItems.get(position);
        holder.setData(cityCard);
        holder.cardView.setOnLongClickListener(view -> {
            adapterPosition = position;
            return false;
        });
        if (fragment != null){
            fragment.registerForContextMenu(holder.cardView);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    public void removeAt(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listItems.size());
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCity;
        private TextView textViewTemperature;
        private ImageView imageViewIcon;
        View cardView;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            textViewCity = itemView.findViewById(R.id.cityCardTextView);
            textViewTemperature = itemView.findViewById(R.id.temperatureCardTextView);
            imageViewIcon = itemView.findViewById(R.id.iconCardImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }

                }
            });
        }

        void setData(CityCard citycard) {
            textViewCity.setText(citycard.getCity());
            textViewTemperature.setText(String.format("%.1f С°", citycard.getTemperature()));
            imageViewIcon.setImageResource(setIcon(citycard));
        }

        private int setIcon(CityCard card) {
            int result = 0;
            switch (card.getIcon()) {
                case ("Thunderstorm"):
                    result = R.drawable.icon200;
                    break;
                case ("Drizzle"):
                    result = R.drawable.icon300;
                    break;
                case ("Rain"):
                    result = R.drawable.icon500_504;
                    break;
                case ("Snow"):
                    result = R.drawable.icon600;
                    break;
                case ("Clear"):
                    result = R.drawable.icon800;
                    break;
                case ("Clouds"):
                    result = R.drawable.icon801;
                    break;
            }
            return result;
        }
    }
}


