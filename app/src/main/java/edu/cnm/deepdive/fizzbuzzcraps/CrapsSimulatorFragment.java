package edu.cnm.deepdive.fizzbuzzcraps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.commons.rng.simple.JDKRandomBridge;
import org.apache.commons.rng.simple.RandomSource;


public class CrapsSimulatorFragment extends Fragment {

  private static final int SEED_SIZE = 312;
  private MenuItem playOne;
  private MenuItem playFast;
  private MenuItem pause;
  private MenuItem reset;
  private ListView rolls;
  private TextView tally;
  private ArrayAdapter<int[]> adapter;
  private boolean running;
  private Random rng;
  private Game game;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    rolls = findViewById(R.id.rolls);
    tally = findViewById(R.id.tally);
    adapter = new edu.cnm.deepdive.fizzbuzzcraps.view.ImageRollAdapter(this);
    rolls.setAdapter(adapter);
    rng = new JDKRandomBridge(RandomSource.MT_64, RandomSource.createLongArray(SEED_SIZE));
    reset();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.options, menu);
    playOne = menu.findItem(R.id.play_one);
    playFast = menu.findItem(R.id.play_fast);
    pause = menu.findItem(R.id.pause);
    reset = menu.findItem(R.id.reset);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    playOne.setEnabled(!running);
    playOne.setVisible(!running);
    playFast.setEnabled(!running);
    playFast.setVisible(!running);
    pause.setEnabled(running);
    pause.setVisible(running);
    reset.setEnabled(!running);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.play_one:
        playOne();
        break;
      case R.id.play_fast:
        playFast();
        break;
      case R.id.pause:
        pause();
        break;
      case R.id.reset:
        reset();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  private void playOne() {
    game.reset();
    game.play();
    updateRollsDisplay(game.getRolls(), game.getState());
    updateTallyDisplay(game.getWins(), game.getLosses());
  }

  private void playFast() {
    running = true;
    invalidateOptionsMenu();
    new Runner().start();
  }

  private void pause() {
    running = false;

  }

  private void reset() {
    game = new Game(rng);
    updateTallyDisplay(0, 0);
  }

  private void updateRollsDisplay(List<int[]> rolls, State state) {
    adapter.clear();
    ((edu.cnm.deepdive.fizzbuzzcraps.view.ImageRollAdapter) adapter).setState(state);
    adapter.addAll(rolls);
  }

  private void updateTallyDisplay(long wins, long losses) {
    long plays = wins + losses;
    double percentage = (plays > 0) ? 100.0 * wins / plays : 0;
    String tallyString = getString(R.string.tally, wins, plays, percentage);
    tally.setText(tallyString);
  }

  private class Runner extends Thread {

    private long wins;
    private long losses;
    private int updateCycles;
    private List<int[]> rolls;
    private edu.cnm.deepdive.fizzbuzzcraps.CrapsSimulatorFragment.State state;

    @Override
    public void run() {

      while (running) {
        for (int i = 0; i < 5000; i++) {
          game.reset();
          game.play();
        }
        updateCycles++;
        wins = game.getWins();
        losses = game.getLosses();
        if (updateCycles % 20 == 0) {
          rolls = game.getRolls();
          state = game.getState();
        } else {
          rolls = null;
          state = null;
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            updateTallyDisplay(wins, losses);
            if(state != null) {
              updateRollsDisplay(rolls, state);
            }
          }
        });
      }
      invalidateOptionsMenu();
    }
  }

  /**
   * <code>State</code> implements a basic state machine for the main play of a
   * game of craps, starting with the come-out roll, and ending with a win or loss
   * of the main bet.
   *
   * @author James Mattos
   * @version 1.0
   */

  public enum State {
    COME_OUT {
      @Override
      public State change(int roll, int pointValue) {
        switch (roll) {
          case 2:
          case 3:
          case 12:
            return LOSS;
          case 7:
          case 11:
            return WIN;
          default:
            return POINT;
        }
      }
    },
    POINT {
      @Override
      public State change(int roll, int pointValue) {
        if (roll == 7) {
          return LOSS;
        }
        if (roll == pointValue) {
          return WIN;
        }
        return this;
      }
    },
    WIN,
    LOSS;

    /**
     * Applies the specified roll sum to this state , returing the same state
     * resulting from the transition represented by the roll. For the terminal
     * states ({@link #WIN} and {@link #LOSS}), no change of state will result from
     *
     * @param roll
     * @param pointValue
     * @return
     */

    public State change(int roll, int pointValue) {
      return this;
    }

  }

  public static class Game {
    private int pointValue;
    private State state;
    private Random rng;
    private List<int[]> rolls;
    private long wins;
    private long losses;

    public Game(Random rng) {
      this.rng = rng;
      this.rolls = new LinkedList();
    }

    public void reset() {
      this.state = State.COME_OUT;
      this.pointValue = 0;
      this.rolls.clear();
    }

    private void roll() {
      int die0 = this.rng.nextInt(6) + 1;
      int die1 = this.rng.nextInt(6) + 1;
      int sum = die0 + die1;
      State newState = this.state.change(sum, this.pointValue);
      if (this.state == State.COME_OUT && newState == State.POINT) {
        this.pointValue = sum;
      }

      this.state = newState;
      int[] diceRoll = new int[]{die0, die1};
      this.rolls.add(diceRoll);
    }

    public State play() {
      while(this.state != State.WIN && this.state != State.LOSS) {
        this.roll();
        if (this.state == State.WIN) {
          ++this.wins;
        } else if (this.state == State.LOSS) {
          ++this.losses;
        }
      }

      return this.state;
    }

    public int getPointValue() {
      return this.pointValue;
    }

    public State getState() {
      return this.state;
    }

    public List<int[]> getRolls() {
      List<int[]> copy = new LinkedList();
      Iterator var2 = this.rolls.iterator();

      while(var2.hasNext()) {
        int[] roll = (int[])var2.next();
        copy.add(Arrays.copyOf(roll, roll.length));
      }

      return copy;
    }

    public long getWins() {
      return this.wins;
    }

    public long getLosses() {
      return this.losses;
    }
  }
}