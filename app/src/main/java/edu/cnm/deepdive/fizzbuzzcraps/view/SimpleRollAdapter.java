package edu.cnm.deepdive.fizzbuzzcraps.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.cnm.deepdive.fizzbuzzcraps.R;
import java.util.Arrays;

public class SimpleRollAdapter extends ArrayAdapter<int[]> {

  public SimpleRollAdapter(@NonNull Context context) {
    super(context, R.layout.text_roll_item);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view;
    if (convertView == null) {
      Context context = getContext();
      LayoutInflater inflater = LayoutInflater.from(context);
      view = inflater.inflate(R.layout.text_roll_item, parent, false);
    } else {
      view = convertView;
    }
    TextView die0 = view.findViewById(R.id.die_0);
    TextView die1 = view.findViewById(R.id.die_1);
    int[] diceRoll = getItem(position);
    die0.setText(Integer.toString(diceRoll[0]));
    die1.setText(Integer.toString(diceRoll[1]));
    return view;
  }
}
