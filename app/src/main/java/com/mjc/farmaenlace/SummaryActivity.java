package com.mjc.farmaenlace; // Ensure this matches your project's package name

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity {

    private TextView textViewSummaryTitle, textViewMedicationOrder, textViewPharmacyAddress;
    private Button buttonCancelOrder, buttonSaveOrder;

    // Animated views (IDs must match your new activity_summary.xml)
    private View headerCard;
    private View orderDetailsCard;
    private View statusCard;
    private View buttonLayout; // This LinearLayout should contain your buttons
    private View footerInfo;

    // Data from Intent
    private String medicationName, medicationType, distributor;
    private int quantity;
    private boolean isPrincipal, isSecundaria;

    private boolean isSaving = false; // Flag to prevent multiple save attempts

    // IMPORTANT: Replace with your server's IP/domain and path to save_order.php
    private static final String SAVE_ORDER_URL = "http://10.10.16.175:8080/farmaenlace_api/save_order.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is where your new XML layout is used
        setContentView(R.layout.activity_summary);

        initializeViews();
        retrieveAndDisplayIntentData();
        setupAnimations();
        setupListeners();
    }

    private void initializeViews() {
        // Core views for data display (IDs from your new XML)
        textViewSummaryTitle = findViewById(R.id.textViewSummaryTitle);
        textViewMedicationOrder = findViewById(R.id.textViewMedicationOrder);
        textViewPharmacyAddress = findViewById(R.id.textViewPharmacyAddress);

        // Buttons (IDs from your new XML)
        buttonCancelOrder = findViewById(R.id.buttonCancelOrder);
        buttonSaveOrder = findViewById(R.id.buttonSaveOrder);

        // Animated views (IDs from your new XML)
        headerCard = findViewById(R.id.headerCard);
        orderDetailsCard = findViewById(R.id.orderDetailsCard);
        statusCard = findViewById(R.id.statusCard);
        buttonLayout = findViewById(R.id.buttonLayout);
        footerInfo = findViewById(R.id.footerInfo);
    }

    private void retrieveAndDisplayIntentData() {
        Intent intent = getIntent();
        medicationName = intent.getStringExtra("medicationName");
        medicationType = intent.getStringExtra("medicationType");
        quantity = intent.getIntExtra("quantity", 0);
        distributor = intent.getStringExtra("distributor");
        isPrincipal = intent.getBooleanExtra("isPrincipal", false);
        isSecundaria = intent.getBooleanExtra("isSecundaria", false);

        if (textViewSummaryTitle != null) {
            // The summary title text is part of the XML's headerCard now,
            // but we can still update the distributor part if needed, or adjust.
            // For now, let's assume the main title "Resumen del Pedido" is static from XML
            // and we just add context about the distributor below it or as part of it.
            // Example: if you had another TextView for distributor in headerCard:
            // TextView distributorTitle = findViewById(R.id.textViewDistributorNameInHeader);
            // if (distributorTitle != null) distributorTitle.setText("Pedido al distribuidor " + distributor);

            // Or update the existing textViewSummaryTitle if you want to include distributor name there
            textViewSummaryTitle.setText("Resumen del Pedido a " + distributor);
        }
        if (textViewMedicationOrder != null) {
            textViewMedicationOrder.setText(quantity + " unidades del " + medicationType + " " + medicationName);
        }

        String address = ""; // Default empty
        if (isPrincipal && isSecundaria) {
            address = "Farmacias Principal (Calle de la Rosa n. 28) y Secundaria (Calle Alcazabilla n. 3)";
        } else if (isPrincipal) {
            address = "Farmacia Principal (Calle de la Rosa n. 28)";
        } else if (isSecundaria) {
            address = "Farmacia Secundaria (Calle Alcazabilla n. 3)";
        }
        if (textViewPharmacyAddress != null) {
            textViewPharmacyAddress.setText(address);
        }
    }

    private void setupAnimations() {
        setupEntryAnimations();
        setupInteractionAnimations();
    }

    private void setupEntryAnimations() {
        // The XML already defines initial alpha and translation states.
        // This method will animate them to their final visible positions.
        if (headerCard != null) {
            headerCard.postDelayed(() -> animateViewsEntry(), 150);
        } else {
            Log.w("SummaryActivityAnims", "headerCard not found. Entry animations might be incomplete.");
            animateViewsEntry();
        }
    }

    private void animateViewsEntry() {
        if (headerCard != null) {
            headerCard.animate()
                    .alpha(1f).translationY(0f).setDuration(800)
                    .setInterpolator(new BounceInterpolator()).start();
        }
        if (orderDetailsCard != null) {
            orderDetailsCard.animate()
                    .alpha(1f).translationX(0f).setDuration(600).setStartDelay(300)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> {
                        if (orderDetailsCard != null) { // Check again in case activity is finishing
                            orderDetailsCard.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200)
                                    .withEndAction(() -> {
                                        if (orderDetailsCard != null) {
                                            orderDetailsCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                                        }
                                    })
                                    .start();
                        }
                    }).start();
        }
        if (statusCard != null) {
            statusCard.animate()
                    .alpha(1f).translationX(0f).setDuration(600).setStartDelay(500)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
        if (buttonLayout != null) {
            buttonLayout.animate()
                    .alpha(1f).translationY(0f).setDuration(700).setStartDelay(700)
                    .setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
        if (footerInfo != null) {
            footerInfo.animate()
                    .alpha(1f).setDuration(500).setStartDelay(1000).start();
        }
    }

    private void setupInteractionAnimations() {
        setupButtonTouchAnimations(buttonCancelOrder);
        setupButtonTouchAnimations(buttonSaveOrder);
        setupSaveButtonSpecialAnimation();
        setupCardHoverAnimations();
    }

    private void setupButtonTouchAnimations(Button button) {
        if (button == null) return;
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    break;
            }
            return false; // Return false to allow onClick to still be triggered
        });
    }

    private void setupSaveButtonSpecialAnimation() {
        if (buttonSaveOrder != null && buttonSaveOrder.getVisibility() == View.VISIBLE && buttonSaveOrder.isEnabled()) {
            animateSaveButtonPulse();
        }
    }

    private void animateSaveButtonPulse() {
        if (buttonSaveOrder == null || !buttonSaveOrder.isEnabled() || buttonSaveOrder.getVisibility() != View.VISIBLE) return;
        buttonSaveOrder.animate().scaleX(1.05f).scaleY(1.05f).setDuration(1000)
                .withEndAction(() -> {
                    if (buttonSaveOrder != null && buttonSaveOrder.isEnabled() && buttonSaveOrder.getVisibility() == View.VISIBLE) {
                        buttonSaveOrder.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000)
                                .withEndAction(this::animateSaveButtonPulse)
                                .start();
                    }
                }).start();
    }

    private void setupCardHoverAnimations() {
        // These are click animations, not true hover.
        if (orderDetailsCard != null) {
            orderDetailsCard.setOnClickListener(v -> {
                v.animate().scaleX(1.02f).scaleY(1.02f).setDuration(150)
                        .withEndAction(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start())
                        .start();
            });
        }
        if (statusCard != null) {
            statusCard.setOnClickListener(v -> {
                v.animate().rotationY(360f).setDuration(600)
                        .withEndAction(() -> v.setRotationY(0f)).start();
            });
        }
    }

    private void setupListeners() {
        if (buttonCancelOrder != null) {
            buttonCancelOrder.setOnClickListener(v -> {
                if (isSaving) return;
                animateButtonPressFeedback(v);
                showCancelDialog();
            });
        }
        if (buttonSaveOrder != null) {
            buttonSaveOrder.setOnClickListener(v -> {
                if (isSaving) return;
                animateButtonPressFeedback(v);
                initiateSaveOrderProcess();
            });
        }
    }

    private void animateButtonPressFeedback(View button) {
        if (button == null) return;
        button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100)
                .withEndAction(() -> button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start())
                .start();
    }

    private void showCancelDialog() {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.animate().alpha(0.7f).setDuration(300)
                    .withEndAction(() -> rootView.animate().alpha(1.0f).setDuration(300).start())
                    .start();
        }
        Toast.makeText(this, "❌ Pedido cancelado", Toast.LENGTH_SHORT).show();
        finish(); // Go back to MainActivity
    }

    private void initiateSaveOrderProcess() {
        isSaving = true;
        if (buttonSaveOrder != null) {
            buttonSaveOrder.setEnabled(false);
            buttonSaveOrder.setText("💾 GUARDANDO...");
            buttonSaveOrder.clearAnimation();
        }
        if (buttonCancelOrder != null) buttonCancelOrder.setEnabled(false);

        saveOrderToDatabase();
    }

    private void saveOrderToDatabase() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SAVE_ORDER_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");
                        String message = jsonResponse.getString("message");
                        if (!error) {
                            // Toast.makeText(SummaryActivity.this, "Éxito: " + message, Toast.LENGTH_LONG).show();
                            animateSuccessSequenceAndFinish();
                        } else {
                            handleSaveOrderFailure("Error al guardar: " + message);
                        }
                    } catch (JSONException e) {
                        Log.e("SummaryActivityJSON", "Error parsing JSON: " + response, e);
                        handleSaveOrderFailure("Error procesando respuesta del servidor.");
                    }
                },
                error -> {
                    Log.e("SummaryActivityVolley", "Volley error: ", error);
                    String errorMessage = "Error de red. Intente de nuevo.";
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    handleSaveOrderFailure(errorMessage);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("medication_name", medicationName);
                params.put("medication_type", medicationType);
                params.put("quantity", String.valueOf(quantity));
                params.put("distributor", distributor);
                params.put("branch_principal", String.valueOf(isPrincipal));
                params.put("branch_secondary", String.valueOf(isSecundaria));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void handleSaveOrderFailure(String errorMessage) {
        isSaving = false;
        if (buttonSaveOrder != null) {
            buttonSaveOrder.setEnabled(true);
            buttonSaveOrder.setText("💾 GRABAR");
            if (buttonSaveOrder.getVisibility() == View.VISIBLE) animateSaveButtonPulse();
        }
        if (buttonCancelOrder != null) buttonCancelOrder.setEnabled(true);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }


    private void animateSuccessSequenceAndFinish() {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.animate().scaleX(1.02f).scaleY(1.02f).setDuration(200)
                    .withEndAction(() -> rootView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start())
                    .start();
        }

        if (buttonSaveOrder != null) {
            buttonSaveOrder.postDelayed(() -> {
                buttonSaveOrder.setText("✅ GRABADO");

                AnimatorSet fadeOutSet = new AnimatorSet();
                if (orderDetailsCard != null) fadeOutSet.playTogether(ObjectAnimator.ofFloat(orderDetailsCard, "alpha", 1f, 0.3f));
                if (statusCard != null) fadeOutSet.playTogether(ObjectAnimator.ofFloat(statusCard, "alpha", 1f, 0.3f));
                if (buttonCancelOrder != null) fadeOutSet.playTogether(ObjectAnimator.ofFloat(buttonCancelOrder, "alpha", 1f, 0.3f));
                if (footerInfo != null) fadeOutSet.playTogether(ObjectAnimator.ofFloat(footerInfo, "alpha", 1f, 0.3f));

                fadeOutSet.setDuration(500);
                fadeOutSet.start();

                Toast.makeText(this, "✅ Pedido grabado exitosamente!", Toast.LENGTH_LONG).show();

                buttonSaveOrder.postDelayed(() -> {
                    if (rootView != null) {
                        rootView.animate().alpha(0f).setDuration(500)
                                .withEndAction(() -> {
                                    Intent mainActivityIntent = new Intent(SummaryActivity.this, MainActivity.class);
                                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainActivityIntent);
                                    finish();
                                }).start();
                    } else {
                        finish();
                    }
                }, 1500);
            }, 300);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only start pulse if not currently saving and button is enabled/visible
        if (!isSaving && buttonSaveOrder != null && buttonSaveOrder.isEnabled() && buttonSaveOrder.getVisibility() == View.VISIBLE) {
            animateSaveButtonPulse();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (buttonSaveOrder != null) {
            buttonSaveOrder.clearAnimation();
        }
        // It's good practice to clear animations on all views that might be continuously animating
        // if (headerCard != null) headerCard.clearAnimation(); // etc. for others if they had ongoing loops
    }
}