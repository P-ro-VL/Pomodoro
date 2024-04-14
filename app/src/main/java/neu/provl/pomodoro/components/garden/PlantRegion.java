package neu.provl.pomodoro.components.garden;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;

public class PlantRegion extends FrameLayout {

    private int density;

    @Getter
    private Plant plant;

    @Getter
    @Setter
    private Consumer<Plant> onPlantSelect;

    public PlantRegion(@NonNull Context context, @Nullable Plant plant) {
        super(context);

        density = (int) getResources().getDisplayMetrics().density;

        LayoutParams layoutParams = new LayoutParams(
                130 * density,
                130 * density
        );
        layoutParams.gravity = Gravity.CENTER;
        setLayoutParams(layoutParams);

        setPlant(plant);

        setOnClickListener((e) -> showSelectPlantDialog());
    }

    public void setPlant(@Nullable Plant plant) {
        this.plant = plant;

        if(plant == null) {
            _setupEmptyPlant();
        } else {
            _setupCurrentPlant();
        }
    }

    private void _setupEmptyPlant() {
        removeAllViews();

        GradientDrawable border = new GradientDrawable();
        border.setCornerRadius(16 * density);
        border.setStroke(2 * density, ContextCompat.getColor(getContext(), R.color.secondary),
                4 * density, 4 * density);

        setBackground(border);

        ImageView addIcon = new ImageView(getContext());
        LayoutParams iconLayoutParams = new LayoutParams(
                64 * density,
                64 * density
        );
        iconLayoutParams.gravity = Gravity.CENTER;
        addIcon.setLayoutParams(iconLayoutParams);
        addIcon.setImageResource(R.drawable.add);

        addView(addIcon);
    }

    private void _setupCurrentPlant() {
        removeAllViews();

        setBackground(null);

        ImageView plant = new ImageView(getContext());
        LayoutParams plantLayoutParams = new LayoutParams(
                130 * density,
                130 * density
        );
        plantLayoutParams.gravity = Gravity.CENTER;
        plant.setLayoutParams(plantLayoutParams);

        addView(plant);

        new NetworkImageInjector(plant).execute(getPlant().getImageUrl());
    }

    private int currentPlantDialogIndex = 0;

    public void showSelectPlantDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.create();

        LinearLayout layout = (LinearLayout) MainActivity.getInstance().getLayoutInflater()
                .inflate(R.layout.plant_select_layout, null);

        ImageView previousPlantBtn = layout.findViewById(R.id.plant_previous_button);
        ImageView nextPlantBtn = layout.findViewById(R.id.plant_next_button);

        previousPlantBtn.setOnClickListener((e) -> {
            currentPlantDialogIndex = Math.max(0, currentPlantDialogIndex - 1);
            _updateSelectPlantDialog(layout, dialog, previousPlantBtn, nextPlantBtn);
        });

        nextPlantBtn.setOnClickListener((e) -> {
            currentPlantDialogIndex = Math.min(AuthenticationDriver.currentUser.getPlants().size() - 1,
                    currentPlantDialogIndex + 1);
            _updateSelectPlantDialog(layout, dialog, previousPlantBtn, nextPlantBtn);
        });

        FrameButton selectBtn = layout.findViewById(R.id.plant_select_button);
        selectBtn.setOnClickListener((e) -> {
            Plant selectedPlant = AuthenticationDriver.currentUser.getPlants().get(currentPlantDialogIndex);
            setPlant(selectedPlant);
            getOnPlantSelect().accept(selectedPlant);
            dialog.cancel();
        });

        _updateSelectPlantDialog(layout, dialog, previousPlantBtn, nextPlantBtn);

        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void _updateSelectPlantDialog(LinearLayout layout, AlertDialog dialog,
                                          ImageView previousPlantBtn, ImageView nextPlantBtn) {
        Plant plant = AuthenticationDriver.currentUser.getPlants().get(currentPlantDialogIndex);

        ImageView plantImage = layout.findViewById(R.id.plant_image);
        new NetworkImageInjector(plantImage).execute(plant.getImageUrl());

        TextView plantName = layout.findViewById(R.id.plant_name);
        plantName.setText(plant.getName());

        TextView coinPerPhase = layout.findViewById(R.id.plant_coin_per_phase);
        coinPerPhase.setText(plant.getCoinPerPhase() + "");

        TextView expPerPhase = layout.findViewById(R.id.plant_xp_per_phase);
        expPerPhase.setText(plant.getXpPerPhase() + "");

        TextView plantDescription = layout.findViewById(R.id.plant_description);
        plantDescription.setText(plant.getDescription());

        if(currentPlantDialogIndex == 0) {
            previousPlantBtn.setVisibility(INVISIBLE);
        } else {
            previousPlantBtn.setVisibility(VISIBLE);
        }

        if(currentPlantDialogIndex == AuthenticationDriver.currentUser.getPlants().size() - 1) {
            nextPlantBtn.setVisibility(INVISIBLE);
        } else {
            nextPlantBtn.setVisibility(VISIBLE);
        }
    }
}
