package edu.cnm.deepdive.fizzbuzzcraps.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import edu.cnm.deepdive.fizzbuzzcraps.CrapsSimulatorFragment.State;
import edu.cnm.deepdive.fizzbuzzcraps.R;
import edu.cnm.deepdive.fizzbuzzcraps.databinding.ImageRollItemBinding;

public class ImageRollAdapter extends ArrayAdapter<int[]> {

  private int[] faces;
  private State state;

  public ImageRollAdapter(Context context) {
    super(context, R.layout.image_roll_item);
    Resources res = context.getResources();
    String pkg = context.getPackageName();
    faces = new int[6];
    for (int i = 0; i < 6; i++) {
      faces[i] = res.getIdentifier("face_" + (i + 1), "drawable", pkg);
    }
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(getContext());

    ImageRollItemBinding binding = ImageRollItemBinding.inflate(inflater, parent, false);

    binding.setRolls(getItem(position));
    binding.setFaces(faces);
    binding.setState(state);
    return binding.getRoot();
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }
}
