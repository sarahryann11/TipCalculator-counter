package csci490.tipcalculator_counter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText billAmount;
    private EditText numPeople;
    private EditText tipOther;

    private RadioGroup tipsButtons;
    private RadioButton tip15Button;
    private RadioButton tip20Button;
    private RadioButton tipCustomButton;
    private Button calculateButton;

    private TextView totalTipAmount;
    private TextView totalBillAmount;
    private TextView totalTipPerPerson;

    DecimalFormat precision = new DecimalFormat("#.00");
    private int otherTipChecked = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalBillAmount = (TextView) findViewById(R.id.amountTotal);
        totalTipAmount = (TextView) findViewById(R.id.tipTotal);
        totalTipPerPerson = (TextView) findViewById(R.id.totalPerPersonTotal);

        billAmount = (EditText) findViewById(R.id.bill);
        numPeople = (EditText) findViewById(R.id.numPeople);
        tipOther = (EditText) findViewById(R.id.tipOther);

        billAmount.requestFocus();

        calculateButton = (Button) findViewById(R.id.button);
        calculateButton.setEnabled(false);

        tipsButtons = (RadioGroup) findViewById(R.id.tipsButtons);
        tip15Button = (RadioButton) findViewById(R.id.tip15Button);
        tip20Button = (RadioButton) findViewById(R.id.tip20Button);
        tipCustomButton = (RadioButton) findViewById(R.id.tipCustomButton);
        tipOther.setEnabled(false);

        tipsButtons.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup g, int checked) {

                if (checked == R.id.tip15Button || checked == R.id.tip20Button)
                {
                    tipOther.setEnabled(false);

                    calculateButton.setEnabled(billAmount.getText().length() > 0
                            && numPeople.getText().length() > 0);
                }
                if (checked == R.id.tipCustomButton)
                {
                    tipOther.setEnabled(true);
                    tipOther.requestFocus();

                    calculateButton.setEnabled(billAmount.getText().length() > 0
                            && numPeople.getText().length() > 0
                            && tipOther.getText().length() > 0);
                }

                otherTipChecked = checked;
            }
        });

        billAmount.setOnKeyListener(mKeyListener);
        numPeople.setOnKeyListener(mKeyListener);
        tipOther.setOnKeyListener(mKeyListener);
        calculateButton.setOnClickListener(mClickListener);
    }

    private OnKeyListener mKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            switch (v.getId()) {
                case R.id.bill:
                case R.id.numPeople:
                    calculateButton.setEnabled(billAmount.getText().length() > 0 && numPeople.getText().length() > 0);
                    break;
                case R.id.tipOther:
                    calculateButton.setEnabled(billAmount.getText().length() > 0
                            && numPeople.getText().length() > 0
                            && tipOther.getText().length() > 0);
                    break;
            }
            return false;
        }

    };

    private OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == R.id.button)
            {
                calculate();
                Toast toast = Toast.makeText(getApplicationContext(), "Bill Calculated!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    };

    private void calculate()
    {
        boolean isError = false;

        Double billTotal = Double.parseDouble(billAmount.getText().toString());
        Double numOfPeople = Double.parseDouble(numPeople.getText().toString());
        Double percentage = null;

        if (billTotal < 1.0)
        {
            showErrorAlert("Amount not valid. Enter valid amount.", billAmount.getId());
            isError = true;
        }

        if (numOfPeople < 1.0)
        {
            showErrorAlert("Number of people not valid. Enter valid number.", numPeople.getId());
            isError = true;
        }

        if (otherTipChecked == -1)
            otherTipChecked = tipsButtons.getCheckedRadioButtonId();

        if (otherTipChecked == R.id.tip15Button)
            percentage = 15.00;

        if (otherTipChecked == R.id.tip20Button)
            percentage = 20.00;

        if (otherTipChecked == R.id.tipCustomButton)
        {
            percentage = Double.parseDouble(tipOther.getText().toString());
            if (percentage < 1.0)
            {
                showErrorAlert("Percentage not valid. Enter valid tip amount", tipOther.getId());
                isError = true;
            }
        }

        if (!isError)
        {
            Double tip = ((billTotal * percentage) / 100);
            Double bill = (billTotal + tip);
            Double individualBill = bill / numOfPeople;

            String billStr = bill.toString();
            String tipStr = tip.toString();
            String individualStr = individualBill.toString();

            totalBillAmount.setText("$" + billStr);
            totalTipAmount.setText("$" + tipStr);
            totalTipPerPerson.setText("$" + individualStr);

            //totalBillAmount.setText("$" + bill.toString());
            //totalTipAmount.setText("$" + tip.toString());
            //totalTipPerPerson.setText("$" + individualBill.toString());
        }

    }

    /**
     *  Shows the error message in an alert dialog
     *
     *  @param errorMessage
     *  String the error message to show
     *  @param fieldId
     *  the Id of the field which caused the error.
     *  This is required so that the focus can be
     *  set on that field once the dialog is
     *  dismissed.
     */

    private void showErrorAlert(String errorMessage, final int fieldId) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setNeutralButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                findViewById(fieldId).requestFocus();
                            }
                        }).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        //restore state and color of the count
        totalBillAmount.setText(savedInstanceState.getString("billTotal"));
        totalTipAmount.setText(savedInstanceState.getString("tipTotal"));
        totalTipPerPerson.setText(savedInstanceState.getString("totalPerPerson"));


    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        //save state
        super.onSaveInstanceState(outState);

        outState.putString("billTotal", totalBillAmount.getText().toString());
        outState.putString("tipTotal", totalTipAmount.getText().toString());
        outState.putString("totalPerPerson", totalTipPerPerson.getText().toString());
    }

}
