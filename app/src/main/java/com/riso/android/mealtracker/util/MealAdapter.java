package com.riso.android.mealtracker.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.riso.android.mealtracker.R;
import com.riso.android.mealtracker.data.DatabaseQuery;
import com.riso.android.mealtracker.data.MealItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richard.janitor on 23-Sep-18.
 */

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder>{
    private static final String TAG = MealAdapter.class.getSimpleName();
    final private ListItemClickListener mOnClickListener;

    private MealItem[] mealItems;
    private int mealPressed;


    public MealAdapter(ListItemClickListener mOnClickListener, MealItem[] mealItems){
        this.mOnClickListener = mOnClickListener;
        this.mealItems = mealItems;
//        this.mealPressed = mealPressed;
    }

    public interface ListItemClickListener {
        void onListItemClick(int listItem);

    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.meal_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MealViewHolder viewHolder = new MealViewHolder(view);Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + mealItems.length);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mealItems.length;
    }

    public void setItemPosition(int position){
        this.mealPressed=position;
        notifyDataSetChanged();
    }

    public class MealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.titleItem)
        TextView titleItem;
        @BindView(R.id.descriptionItem)
        TextView descriptionItem;
        @BindView(R.id.locationItem)
        TextView locationItem;
        @BindView(R.id.dateItem)
        TextView dateItem;
        @BindView(R.id.tiemItem)
        TextView timeItem;
        @BindView(R.id.mailItem)
        ImageButton mailItem;
        @BindView(R.id.trahsItem)
        ImageButton trahsItem;
        DatabaseQuery dbQuery;
        String id;

        public MealViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            titleItem = itemView.findViewById(R.id.titleItem);
            descriptionItem = itemView.findViewById(R.id.descriptionItem);
            locationItem = itemView.findViewById(R.id.locationItem);
            dateItem = itemView.findViewById(R.id.dateItem);
            timeItem = itemView.findViewById(R.id.tiemItem);
            mailItem = itemView.findViewById(R.id.mailItem);
            trahsItem = itemView.findViewById(R.id.trahsItem);
            dbQuery = new DatabaseQuery(itemView.getContext());
            itemView.setOnClickListener(this);
        }

        void bind(final int listIndex){
            if (mealItems != null) {
                titleItem.setText(mealItems[listIndex].typeItem);
                titleItem.setTextColor(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)));
                descriptionItem.setText(mealItems[listIndex].descItem);
                descriptionItem.setTextColor(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)));
                locationItem.setText(mealItems[listIndex].locationItem);
                locationItem.setTextColor(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)));
                dateItem.setText(mealItems[listIndex].dateItem);
                dateItem.setTextColor(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)));
                timeItem.setText(mealItems[listIndex].timeItem);
                timeItem.setTextColor(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)));
                String calendar = mealItems[listIndex].gCalendarItem;
                id = mealItems[listIndex].id;
                if (calendar.equals("true")) {
                    mailItem.setColorFilter(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                mailItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(itemView.getContext(), "Testujem mail", Toast.LENGTH_SHORT).show();
                    }
                });
                trahsItem.setColorFilter(ContextCompat.getColor(itemView.getContext(), chooseColor(mealItems[listIndex].colorItem)), android.graphics.PorterDuff.Mode.SRC_IN);
                trahsItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbQuery.removeMeal(id);
                        newArray(listIndex);
                        notifyItemRemoved(listIndex);
//                    notifyDataSetChanged();
                    }
                });
            }

        }

        public int chooseColor(String color){
            switch (color){
                case "green": return R.color.green;
                case "red": return R.color.red;
                case "blue": return R.color.blue;
                case "Purple": return R.color.purple;
                case "Deep Purple": return R.color.deepPurple;
                case "Orange": return R.color.deepOrange;
                case "Brown": return R.color.brown;
                case "Cyan": return R.color.cyan;
                case "Yellow": return R.color.yellow;
                case "Pink": return R.color.pink;
                default: return R.color.grey;

            }
        }

        public void newArray(int position){
            MealItem[] newMeals;// = new MealItem[mealItems.length];
            newMeals=mealItems;
            mealItems = new MealItem[newMeals.length-1];
            if (newMeals.length!=1) {
                int j = 0;
                for (int i = 0; i < newMeals.length; i++) {
                    if (i != position) {
                        mealItems[j] = newMeals[i];
                        j++;
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
