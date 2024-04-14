package neu.provl.pomodoro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.ShopItemCard;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;

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

    List<View> initShopItems() {
        List<View> items = new ArrayList<>();
        AuthenticationDriver.currentUser.getPlants().forEach((plant) -> {
            items.add(new ShopItemCard(getContext(), plant));
        });
        return items;
    }
}
