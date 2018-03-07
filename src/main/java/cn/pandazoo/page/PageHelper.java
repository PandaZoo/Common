package cn.pandazoo.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * created by yongkang.zhang
 * added at 2018/3/7
 */
public class PageHelper {


    public static <T, V> List<T> convertToTree(Supplier<List<T>> listSupplier,
                                               Function<T, V> valueSupplier,
                                               Function<T, V> parentValueSupplier,
                                               Predicate<T> rootCondition,
                                               Function<T, List<T>> getOperation,
                                               BiConsumer<T, List<T>> setOperation) {
        List<T> ts = listSupplier.get();
        if (ts == null) {
            return null;
        }
        List<T> parentList = ts.stream()
                .filter(rootCondition)
                .collect(Collectors.toList());
        Map<V, List<T>> childrenMap = ts.stream()
                .filter(t -> !rootCondition.test(t))
                .collect(Collectors.groupingBy(parentValueSupplier));

        List<V> doneList = new ArrayList<>();
        int loopCnt = 1;
        // 接下来应该对parent和children进行递归组合。 最坏的情况是childrenMap是子父顺序，每次是添加一个parent
        while (doneList.size() < childrenMap.size() && loopCnt <= childrenMap.size()) {
            childrenMap.entrySet()
                    .stream()
                    .filter(entry -> ! doneList.contains(entry.getKey()))
                    .forEach(entry -> addToTree(parentList, entry, valueSupplier, getOperation, setOperation, doneList));
            loopCnt += 1;
        }


        return parentList;
    }

    private static <T, V> void addToTree(List<T> tList, Map.Entry<V, List<T>> entry, Function<T, V> valueSupplier,
                                         Function<T, List<T>> getOperation, BiConsumer<T, List<T>> setOperation, List<V> doneList) {
        if (tList == null || entry == null || entry.getKey() == null) {
            return;
        }

       for (T t : tList) {
           if (Objects.equals(valueSupplier.apply(t), entry.getKey())) {
               setOperation.accept(t, entry.getValue());
               doneList.add(entry.getKey());
               break;
           } else {
               addToTree(getOperation.apply(t), entry, valueSupplier, getOperation, setOperation, doneList);
           }
       }
    }
}
