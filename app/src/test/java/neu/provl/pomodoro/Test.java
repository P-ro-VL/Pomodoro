package neu.provl.pomodoro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        int[] data = {3,2,3,7,8,9,10,3,8,3};
        List<Integer> nums = new ArrayList<>();
        for(int i = 0; i < data.length; i++) {
            nums.add(data[i]);
        }

        System.out.println(nums.stream().filter(n -> n % 2 == 0)
                .collect(Collectors.summarizingInt(n -> n * n)).getSum());
    }
}
