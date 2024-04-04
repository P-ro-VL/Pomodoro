package neu.provl.pomodoro.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class Plant {
    @Setter(value = AccessLevel.NONE)
    private String id;
    private String name;
    private PlantType type;
    private String imageUrl;
    private int price;
    private int xpPerPhase;
    private int coinPerPhase;
}
