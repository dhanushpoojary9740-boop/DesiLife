private void showDepositDialog() {

    EditText amountInput = new EditText(activity);
    amountInput.setHint("Enter deposit amount");
    amountInput.setInputType(2);

    LinearLayout layout = new LinearLayout(activity);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(25, 15, 25, 5);

    TextView label = text("Select account", 15, false);
    label.setTextColor(Color.DKGRAY);
    layout.addView(label);

    Spinner accountSpinner = new android.widget.Spinner(activity);

    String[] accounts = {
        "Savings Account",
        "Current Account"
    };

    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        activity,
        android.R.layout.simple_spinner_item,
        accounts
    );

    adapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item
    );

    accountSpinner.setAdapter(adapter);

    layout.addView(accountSpinner);
    layout.addView(amountInput);

    new AlertDialog.Builder(activity)
        .setTitle("Deposit Money")
        .setMessage("Available cash: " + money(cash))
        .setView(layout)
        .setNegativeButton("CANCEL", null)
        .setPositiveButton("DEPOSIT", (dialog, which) -> {

            long amount = readAmount(amountInput);
            int account = accountSpinner.getSelectedItemPosition();

            if (amount <= 0) {
                toast("Enter a valid amount");
                return;
            }

            if (amount > cash) {
                toast("Not enough available cash");
                return;
            }

            if (account == 0) {
                savingsBalance += amount;
            } else {
                currentBalance += amount;
            }

            updateCash(cash - amount);

            addHistory(
                "Deposited " + money(amount) + " into " +
                (account == 0
                    ? "Savings Account"
                    : "Current Account")
            );

            saveData();
            toast("Deposit successful");
        })
        .show();
}

private void showDepositAmountDialog(int account) {
    // Deposit is now handled in showDepositDialog().
}
