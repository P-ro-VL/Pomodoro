package neu.provl.pomodoro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.CoinChip;
import neu.provl.pomodoro.components.ShopItemCard;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.controller.DatabaseDriver;

public class ShopFragment extends Fragment {

    private LinearLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(R.layout.shop_layout, null);

        LinearLayout itemScrollView = root.findViewById(R.id.shop_scroll_view);
        initShopItems().forEach(itemScrollView::addView);

        return root;
    }

    void update() {
        LinearLayout itemScrollView = root.findViewById(R.id.shop_scroll_view);
        itemScrollView.removeAllViews();
        initShopItems().forEach(itemScrollView::addView);
    }

    List<View> initShopItems() {
        List<View> items = new ArrayList<>();
        List<String> ownedPlantIds = AuthenticationDriver.currentUser.getPlants().stream().map(Plant::getId).collect(Collectors.toList());
        List<Plant> unownedPlants = DatabaseDriver.getInstance().getAvailablePlants().stream().filter(
                plant -> !ownedPlantIds.contains(plant.getId())
        ).collect(Collectors.toList());

        if(!unownedPlants.isEmpty()) {
            unownedPlants.forEach((plant) -> {
                ShopItemCard itemCard = new ShopItemCard(getContext(), plant);
                itemCard.setOnBuyRequest((p) -> {
                    int coin = AuthenticationDriver.currentUser.getCoin();
                    if(coin >= p.getPrice()) {
                        AuthenticationDriver.currentUser.setCoin(coin - p.getPrice());
                        AuthenticationDriver.currentUser.getPlants().add(p);

                        CoinChip coinChip = root.findViewById(R.id.coin_chip);
                        coinChip.update();

                        update();
                    } else {
                        Snackbar.make(root, R.string.not_enough_money, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.destructive))
                                .show();
                    }
                });
                items.add(itemCard);
            });
        } else {
            TextView text = new TextView(getContext());
            text.setText(getResources().getString(R.string.owned_all));
            text.setTextSize(20);
            text.setTypeface(getResources().getFont(R.font.medium));

            items.add(text);
        }
        return items;
    }
}
