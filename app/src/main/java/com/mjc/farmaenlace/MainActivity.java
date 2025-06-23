package com.mjc.farmaenlace;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMedicationName, editTextQuantity;
    private Spinner spinnerMedicationType;
    private RadioGroup radioGroupDistributor;
    private CheckBox checkBoxPrincipal, checkBoxSecundaria;
    private Button buttonClear, buttonConfirm;

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]+$");

    private String validatedMedicationName;
    private String validatedMedicationType;
    private int validatedQuantity;
    private String validatedDistributor;
    private boolean validatedIsPrincipal;
    private boolean validatedIsSecundaria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure this is your new XML layout

        editTextMedicationName = findViewById(R.id.editTextMedicationName);
        spinnerMedicationType = findViewById(R.id.spinnerMedicationType);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        radioGroupDistributor = findViewById(R.id.radioGroupDistributor);
        checkBoxPrincipal = findViewById(R.id.checkBoxPrincipal);
        checkBoxSecundaria = findViewById(R.id.checkBoxSecundaria);
        buttonClear = findViewById(R.id.buttonClear);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.medication_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicationType.setAdapter(adapter);

        initializeAnimations();
        setupButtonListeners();
    }

    private void initializeAnimations() {
        animateViewsOnLoad(); // Assumes your XML has the IDs (headerCard, medicationCard etc.)
        setupInteractionAnimations();
    }

    private void animateViewsOnLoad() {
        View headerCard = findViewById(R.id.headerCard);
        View medicationCard = findViewById(R.id.medicationCard);
        View distributorCard = findViewById(R.id.distributorCard);
        View branchCard = findViewById(R.id.branchCard);
        View buttonLayout = findViewById(R.id.buttonLayout);

        if (headerCard != null) {
            headerCard.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
        if (medicationCard != null) {
            medicationCard.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(600)
                    .setStartDelay(200)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
        if (distributorCard != null) {
            distributorCard.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(600)
                    .setStartDelay(400)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
        if (branchCard != null) {
            branchCard.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(600)
                    .setStartDelay(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
        if (buttonLayout != null) {
            buttonLayout.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(600)
                    .setStartDelay(800)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    private void setupInteractionAnimations() {
        addButtonPressAnimation(buttonClear);
        addButtonPressAnimation(buttonConfirm);

        for (int i = 0; i < radioGroupDistributor.getChildCount(); i++) {
            View child = radioGroupDistributor.getChildAt(i);
            if (child instanceof RadioButton) {
                addSelectionAnimation((RadioButton) child);
            }
        }
        addSelectionAnimation(checkBoxPrincipal);
        addSelectionAnimation(checkBoxSecundaria);
    }

    private void addButtonPressAnimation(View button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    private void addSelectionAnimation(CompoundButton compoundButton) {
        compoundButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonView.animate()
                        .scaleX(1.05f).scaleY(1.05f).setDuration(150)
                        .withEndAction(() -> buttonView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start())
                        .start();
            }
        });
    }

    private void setupButtonListeners() {
        buttonClear.setOnClickListener(v -> {
            clearFormAnimated();
            showFeedbackAnimation(v, "Formulario limpiado");
        });

        buttonConfirm.setOnClickListener(v -> {
            if (validateForm()) {
                showFeedbackAnimation(v, "Datos válidos, procesando...");
                submitForm();
            } else {
                // Optionally, show a general "Please correct errors" Snackbar/Toast if any validation failed
                Snackbar.make(findViewById(android.R.id.content), "Por favor, corrija los errores.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showFeedbackAnimation(View view, String message) {
        view.animate()
                .scaleX(1.1f).scaleY(1.1f).setDuration(200)
                .withEndAction(() -> view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start())
                .start();
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void clearFormAnimated() {
        AnimatorSet clearAnimation = new AnimatorSet();
        clearAnimation.playTogether(
                ObjectAnimator.ofFloat(editTextMedicationName, "alpha", 1f, 0.3f, 1f),
                ObjectAnimator.ofFloat(editTextQuantity, "alpha", 1f, 0.3f, 1f)
        );
        clearAnimation.setDuration(400);
        clearAnimation.start();

        editTextMedicationName.setText("");
        editTextQuantity.setText("");
        spinnerMedicationType.setSelection(0);
        radioGroupDistributor.clearCheck();
        checkBoxPrincipal.setChecked(false);
        checkBoxSecundaria.setChecked(false);
        editTextMedicationName.setError(null);
        editTextQuantity.setError(null);
        editTextMedicationName.requestFocus();
    }

    private boolean validateForm() {
        // Rule 1: Medication Name (Alphanumeric)
        String medicationNameInput = editTextMedicationName.getText().toString().trim();
        if (TextUtils.isEmpty(medicationNameInput) || !ALPHANUMERIC_PATTERN.matcher(medicationNameInput).matches()) {
            showValidationError(editTextMedicationName, "Nombre de medicamento inválido (solo alfanumérico).");
            // Toast.makeText(this, "Nombre de medicamento inválido.", Toast.LENGTH_LONG).show(); // Toast is part of showValidationError now
            return false;
        } else {
            editTextMedicationName.setError(null); // Clear error if previously set
        }

        // Rule 2: Medication Type (Selected)
        if (spinnerMedicationType.getSelectedItemPosition() == 0) { // Assuming 0 is "Seleccione Tipo"
            animateViewError(spinnerMedicationType);
            Toast.makeText(this, "Seleccione un tipo de medicamento.", Toast.LENGTH_LONG).show();
            spinnerMedicationType.requestFocus(); // Might open the dropdown
            return false;
        }

        // Rule 3: Quantity (Positive Integer)
        String quantityStr = editTextQuantity.getText().toString().trim();
        int quantityInput = 0;
        if (TextUtils.isEmpty(quantityStr)) {
            showValidationError(editTextQuantity, "Cantidad es requerida.");
            return false;
        }
        try {
            quantityInput = Integer.parseInt(quantityStr);
            if (quantityInput <= 0) {
                showValidationError(editTextQuantity, "La cantidad debe ser un número entero positivo.");
                return false;
            } else {
                editTextQuantity.setError(null); // Clear error if previously set
            }
        } catch (NumberFormatException e) {
            showValidationError(editTextQuantity, "Cantidad inválida. Debe ser un número.");
            return false;
        }

        // Rule 4: Distributor (Selected)
        int selectedDistributorId = radioGroupDistributor.getCheckedRadioButtonId();
        if (selectedDistributorId == -1) {
            animateViewError(radioGroupDistributor); // Animate the whole group
            Toast.makeText(this, "Seleccione un distribuidor.", Toast.LENGTH_LONG).show();
            radioGroupDistributor.requestFocus();
            return false;
        }
        RadioButton selectedDistributorRadioButton = findViewById(selectedDistributorId);
        String distributorInput = (selectedDistributorRadioButton != null) ? selectedDistributorRadioButton.getText().toString() : "";


        // Rule 5: Branch (At least one selected)
        boolean isPrincipalInput = checkBoxPrincipal.isChecked();
        boolean isSecundariaInput = checkBoxSecundaria.isChecked();
        if (!isPrincipalInput && !isSecundariaInput) {
            animateViewError(checkBoxPrincipal); // Animate one of them or their parent container
            animateViewError(checkBoxSecundaria);
            Toast.makeText(this, "Seleccione al menos una sucursal.", Toast.LENGTH_LONG).show();
            checkBoxPrincipal.requestFocus(); // Focus on the first checkbox
            return false;
        }

        // All data is valid, store it in class members for submitForm
        validatedMedicationName = medicationNameInput;
        validatedMedicationType = spinnerMedicationType.getSelectedItem().toString();
        validatedQuantity = quantityInput;
        validatedDistributor = distributorInput;
        validatedIsPrincipal = isPrincipalInput;
        validatedIsSecundaria = isSecundariaInput;

        return true;
    }

    private void showValidationError(EditText view, String message) {
        view.setError(message);
        animateViewError(view); // Use the generic animation
        Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // Added Toast here for consistency
        view.requestFocus();
    }

    private void animateViewError(View view) {
        view.animate()
                .translationX(-10f).setDuration(50)
                .withEndAction(() -> view.animate().translationX(10f).setDuration(50)
                        .withEndAction(() -> view.animate().translationX(0f).setDuration(50).start())
                        .start())
                .start();
    }

    private void submitForm() {
        showLoadingAnimation();

        Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
        intent.putExtra("medicationName", validatedMedicationName);
        intent.putExtra("medicationType", validatedMedicationType);
        intent.putExtra("quantity", validatedQuantity);
        intent.putExtra("distributor", validatedDistributor);
        intent.putExtra("isPrincipal", validatedIsPrincipal);
        intent.putExtra("isSecundaria", validatedIsSecundaria);
        startActivity(intent);
    }

    private void showLoadingAnimation() {
        Toast.makeText(this, "Procesando pedido...", Toast.LENGTH_SHORT).show();
    }
}