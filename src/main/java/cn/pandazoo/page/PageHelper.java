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


    public <T, V> List<T> convertToTree(Supplier<List<T>> supplier, Function<T, V> valueSupplier, Function<T, V> parentValueSupplier,
                                Predicate<T> rootCondition, Function<T, List<T>> getOperation,
                                BiConsumer<T, List<T>> setOperation) {
        List<T> ts = supplier.get();
        List<T> parentList = ts.stream()
                .filter(rootCondition)
                .collect(Collectors.toList());
        Map<V, List<T>> childrenMap = ts.stream()
                .filter(rootCondition.negate())
                .collect(Collectors.groupingBy(parentValueSupplier));
        childrenMap.entrySet().forEach(
                entry -> addToTree(parentList, valueSupplier, entry, getOperation, setOperation)
        );

        return parentList;
    }

    private <T, V> void addToTree(List<T> list, Function<T, V> valueSupplier, Map.Entry<V, List<T>> entry,
                                  Function<T, List<T>> getOperation, BiConsumer<T, List<T>> setOperation) {
        if (list == null || list.isEmpty() || entry == null) {
            return;
        }

        for (T t : list) {
            if (Objects.equals(valueSupplier.apply(t), entry.getKey())) {
                setOperation.accept(t, entry.getValue());
                break;
            } else {
                addToTree(getOperation.apply(t), valueSupplier, entry, getOperation, setOperation);
            }
        }
    }
}
