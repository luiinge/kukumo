package iti.kukumo.plugin.api;

import iti.kukumo.plugin.api.contributions.DataType;
import java.util.*;
import java.util.stream.*;
import org.jexten.ExtensionManager;

public class DataTypes {

    public static DataTypes of (DataType<?>... dataTypes) {
        return new DataTypes(Arrays.asList(dataTypes));
    }


    private final Map<String,DataType<?>> byName;
    private final List<String> allNames;


    public DataTypes(List<DataType<?>> dataTypes) {
        this.byName = dataTypes.stream().collect(Collectors.toMap(DataType::name, e -> e));
        this.allNames = dataTypes.stream().map(DataType::name).sorted().toList();
    }


    @SuppressWarnings("unchecked")
    DataTypes(ExtensionManager extensionManager) {
        var dataTypes = extensionManager.getExtensions(DataType.class).toList();
        this.byName = dataTypes.stream().collect(Collectors.toMap(DataType::name, e -> e));
        this.allNames = dataTypes.stream().map(DataType::name).sorted().toList();
    }


    public Optional<DataType<?>> getByName(String name) {
        return Optional.ofNullable(byName.get(name));
    }


    public List<String> allNames() {
        return allNames;
    }


    public Stream<DataType<?>> stream() {
        return byName.values().stream();
    }

}
