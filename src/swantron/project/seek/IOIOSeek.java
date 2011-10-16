package swantron.project.seek;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IOIOSeek extends AbstractIOIOActivity {
	private TextView textView_;
	private SeekBar seekBar1_;
	private SeekBar seekBar2_;
	private ToggleButton toggleButton_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView_ = (TextView)findViewById(R.id.TextView);
        seekBar1_ = (SeekBar)findViewById(R.id.SeekBar1);
        seekBar2_ = (SeekBar)findViewById(R.id.SeekBar2);
        toggleButton_ = (ToggleButton)findViewById(R.id.ToggleButton);

        enableUi(false);
    }
	
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private AnalogInput input_;
		private PwmOutput pwmOutput1_;
		private PwmOutput pwmOutput2_;
		private DigitalOutput led_;

		
		public void setup() throws ConnectionLostException {
			try {
				input_ = ioio_.openAnalogInput(40);
				pwmOutput1_ = ioio_.openPwmOutput(12, 100);
				pwmOutput2_ = ioio_.openPwmOutput(13, 100);
				led_ = ioio_.openDigitalOutput(25, false);
				enableUi(true);
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
		
		public void loop() throws ConnectionLostException {
			try {
				final float reading = input_.read();
				setText(Float.toString(reading));
				pwmOutput1_.setPulseWidth(500 + seekBar1_.getProgress() * 2);
				pwmOutput2_.setPulseWidth(500 + seekBar2_.getProgress() * 2);
				led_.write(!toggleButton_.isChecked());
				sleep(10);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seekBar1_.setEnabled(enable);
				seekBar2_.setEnabled(enable);
				toggleButton_.setEnabled(enable);
			}
		});
	}
	
	private void setText(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_.setText(str);
			}
		});
	}
}
