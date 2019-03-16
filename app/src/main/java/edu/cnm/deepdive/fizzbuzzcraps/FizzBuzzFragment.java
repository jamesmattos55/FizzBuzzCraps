package edu.cnm.deepdive.fizzbuzzcraps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.Random;


public class FizzBuzzFragment extends Fragment {


  private static final int UPPER_BOUND = 99;
  private static final int TIMEOUT_INTERVAL = 5000;

  private TextView numberView;
  private ToggleButton fizzToggle;
  private ToggleButton buzzToggle;
  private TextView activeCorrectView;
  private TextView passiveCorrectView;
  private TextView incorrectView;
  private Random rng;
  private int value;
  private int activeCorrect;
  private int passiveCorrect;
  private int incorrect;
  private boolean running;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_fizz_buzz, container, false);
    numberView = view.findViewById(R.id.number_view);
    fizzToggle = view.findViewById(R.id.fizz_toggle);
    buzzToggle = view.findViewById(R.id.buzz_toggle);
    activeCorrectView = view.findViewById(R.id.active_correct_view);
    passiveCorrectView = view.findViewById(R.id.passive_correct_view);
    incorrectView = view.findViewById(R.id.incorrect_view);
    rng = new Random();
    //startTimer();
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    startTimer();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      stopTimer();
    } else {
    startTimer();
    }
  }

  private void startTimer() {
//    if (isVisible()) {
      running = true;
      Timer timer = new Timer();
      timer.start();
//    }
  }

  private void stopTimer() {
    running = false;
  }

  private void updateTally() {
    boolean isFizz = (value % 3 == 0);
    boolean isBuzz = (value % 5 == 0);
    boolean fizzCorrect = (isFizz == fizzToggle.isChecked());
    boolean buzzCorrect = (isBuzz == buzzToggle.isChecked());
    if (!fizzCorrect || !buzzCorrect) {
      incorrect++;
    } else if (isFizz || isBuzz) {
      activeCorrect++;
    } else {
      passiveCorrect++;
    }
  }

  private void updateTallyDisplay() {
    String activeCorrectFormat = getString(R.string.active_correct, activeCorrect);
    activeCorrectView.setText(activeCorrectFormat);
    String passiveCorrectFormat = getString(R.string.passive_correct, passiveCorrect);
    passiveCorrectView.setText(passiveCorrectFormat);
    String incorrectFormat = getString(R.string.incorrect, incorrect);
    incorrectView.setText(incorrectFormat);
  }

  private void updateView() {
    value = 1 + rng.nextInt(UPPER_BOUND);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        updateTallyDisplay();
        numberView.setText(Integer.toString(value));
        fizzToggle.setChecked(false);
        buzzToggle.setChecked(false);
      }
    });
  }

  class Timer extends Thread {

    @Override
    public void run() {
      try {
        updateView();
        Thread.sleep(TIMEOUT_INTERVAL);
      } catch (InterruptedException e) {
        // DO NOTHING!
      } finally {
        updateTally();
        if(running) {
          startTimer();
        }
      }
    }


  }

}
